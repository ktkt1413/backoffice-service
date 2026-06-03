package com.dayaeyak.backofficservice.backoffice.application.service;

import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationRequestDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationResponseDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationSearchDto;
import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.application.repository.ApplicationRepository;
import com.dayaeyak.backofficservice.backoffice.client.mapper.ApplicationToExhibitionMapper;
import com.dayaeyak.backofficservice.backoffice.client.mapper.ApplicationToPerformanceMapper;
import com.dayaeyak.backofficservice.backoffice.client.mapper.ApplicationToRestaurantMapper;
import com.dayaeyak.backofficservice.backoffice.client.service.ExhibitionServiceClient;
import com.dayaeyak.backofficservice.backoffice.client.service.PerformanceServiceClient;
import com.dayaeyak.backofficservice.backoffice.client.service.RestaurantServiceClient;
import com.dayaeyak.backofficservice.backoffice.common.enums.ApplicationStatus;
import com.dayaeyak.backofficservice.backoffice.common.exception.BusinessException;
import com.dayaeyak.backofficservice.backoffice.common.exception.ErrorCode;
import com.dayaeyak.backofficservice.backoffice.kafka.ServiceTypeMapper;
import com.dayaeyak.backofficservice.backoffice.kafka.producer.ProducerService;
import com.dayaeyak.backofficservice.backoffice.kafka.producer.dtos.BackofficeRegisterDto;
import com.dayaeyak.backofficservice.backoffice.kafka.producer.dtos.ServiceRegisterRequestDto;
import com.dayaeyak.backofficservice.backoffice.kafka.producer.enums.ServiceType;
import com.dayaeyak.backofficservice.backoffice.kafka.producer.enums.Status;
import com.dayaeyak.backofficservice.backoffice.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;


@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository repository;
    private final ExhibitionServiceClient exhibitionService;
    private final RestaurantServiceClient restaurantService;
    private final PerformanceServiceClient performanceService;
    private final ProducerService producerService;  // 카프카 프로듀서


    // 단건 조회
    @Transactional
    public ApplicationResponseDto getApplication(Long id) {

        Application application = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        return ApplicationResponseDto.from(application);
    }

    // 목록 조회 사업장명, 사업주명, 사업 종류, 기간 검색
    @Transactional
    public Page<ApplicationResponseDto> getApplications(ApplicationSearchDto searchDto, Pageable pageable) {

        return repository.searchApplication(searchDto, pageable);
    }

    // 신청서 생성
    @Transactional
    public ApplicationResponseDto createApplication(ApplicationRequestDto dto, Long userId) {

        Application application = Application.createApplication(dto, userId);
        repository.save(application);
        return ApplicationResponseDto.from(application);
    }

    // 신청서 수정
    @Transactional
    public ApplicationResponseDto updateApplication(Long id, ApplicationRequestDto dto) {

        Application application = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        application.updateApplication(dto);
        repository.save(application);
        return ApplicationResponseDto.from(application);
    }

    // 신청서 삭제
    @Transactional
    public void deleteApplication(Long id) {

        Application application = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        repository.delete(application);
    }

    //신청서 승인 요청
    @Transactional
    public void requestApproval(Long id, Long userId) {

        Application application = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        application.setStatus(ApplicationStatus.APPROVAL_REQUESTED);
        repository.save(application);

        // 관리자에게 승인 요청 알람 전송
        BackofficeRegisterDto dto = new BackofficeRegisterDto(
                userId,
                ServiceType.BACKOFFICE,
                null,
                ServiceTypeMapper.fromBusinessType(application.getBusinessType()),
                application.getBusinessName(),
                application.getOwner()
        );

        String topic = "register";

        sendBackOfficeRegisterAfterCommit(topic, dto);

    }

    // 신청서 승인
    @Transactional(noRollbackFor = BusinessException.class)
    public ApplicationResponseDto approveApplication(Long id, Long userId, UserRole role) {

        Application application = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        application.startApproval();
        repository.saveAndFlush(application);

        try {
            // 외부 서비스 호출 (RestTemplate 사용)
            registerExternalService(application, userId, role);
        } catch (BusinessException e) {
            application.failApproval();
            repository.save(application);
            throw e;
        }

        application.approve();
        repository.save(application);

        // 신청자에게 승인 알람 전송
        ServiceRegisterRequestDto dto = new ServiceRegisterRequestDto(
                userId,
                ServiceType.BACKOFFICE,
                null,
                application.getOwner(),
                Status.APPROVED
        );

        String topic = ServiceTypeMapper.fromBusinessType(application.getBusinessType()).name().toLowerCase();
        sendRegisterResultAfterCommit(topic, dto);

        return ApplicationResponseDto.from(application);
    }

    //신청서 거절
    @Transactional
    public ApplicationResponseDto rejectApplication(Long id, Long userId) {

        Application application = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));

        application.reject();
        repository.save(application);

        // 신청자에게 거절 알람 전송
        ServiceRegisterRequestDto dto = new ServiceRegisterRequestDto(
                userId,
                ServiceType.BACKOFFICE,
                null,
                application.getOwner(),
                Status.DECLINED
        );

        String topic = ServiceTypeMapper.fromBusinessType(application.getBusinessType()).name().toLowerCase();
        sendRegisterResultAfterCommit(topic, dto);

        return ApplicationResponseDto.from(application);
    }

    //외부 서비스 호출
    private void registerExternalService(Application application, Long userId, UserRole role) {
        try {
            switch (application.getBusinessType()) {
                case RESTAURANT:
                    restaurantService.registerRestaurant(
                            ApplicationToRestaurantMapper.toRestaurantRequestDto(application)
                    );
                    break;

                case PERFORMANCE:
                    performanceService.registerPerformance(
                            ApplicationToPerformanceMapper.toPerformanceRequestDto(application)
                    );
                    break;

                case EXHIBITION:
                    exhibitionService.registerExhibition(
                            ApplicationToExhibitionMapper.toExhibitionRequestDto(application)
                    );
                    break;

                default:
                    throw new IllegalArgumentException("알 수 없는 서비스 타입입니다.");
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EXTERNAL_SERVICE_FAILURE, e.getMessage());
        }
    }

    private void sendRegisterResultAfterCommit(String topic, ServiceRegisterRequestDto dto) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    producerService.sendRegisterResult(topic, null, dto);
                }
            });
        } else {
            producerService.sendRegisterResult(topic, null, dto);
        }
    }

    private void sendBackOfficeRegisterAfterCommit(String topic, BackofficeRegisterDto dto) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    producerService.sendBackOfficeRegister(topic, null, dto);
                }
            });
        } else {
            producerService.sendBackOfficeRegister(topic, null, dto);
        }
    }

}
