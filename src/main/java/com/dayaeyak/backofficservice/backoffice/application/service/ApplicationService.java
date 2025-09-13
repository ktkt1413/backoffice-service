package com.dayaeyak.backofficservice.backoffice.application.service;

import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationRequestDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationResponseDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationSearchDto;
import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import com.dayaeyak.backofficservice.backoffice.application.repository.ApplicationRepository;
import com.dayaeyak.backofficservice.backoffice.common.exception.BusinessException;
import com.dayaeyak.backofficservice.backoffice.common.exception.ErrorCode;
import com.dayaeyak.backofficservice.backoffice.common.security.AccessContext;
import com.dayaeyak.backofficservice.backoffice.common.security.AccessGuard;
import com.dayaeyak.backofficservice.backoffice.common.security.Action;
import com.dayaeyak.backofficservice.backoffice.common.security.ResourceScope;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository repository;
    private final ApplicationRepository applicationRepository;

    // 단건 조회
    @Transactional
    public ApplicationResponseDto getApplication(Long id, AccessContext ctx) {
        AccessGuard.requiredPermission(Action.READ, ctx, ResourceScope.of(ctx.getUserId()));
        Application application = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        return ApplicationResponseDto.from(application);
    }

    // 목록 조회 사업장명, 사업주명, 사업 종류, 기간 검색
    @Transactional
    public Page<ApplicationResponseDto> getApplications(ApplicationSearchDto searchDto, AccessContext ctx, Pageable pageable) {
        AccessGuard.requiredPermission(Action.READ, ctx, ResourceScope.of(ctx.getUserId()));
        return repository.searchApplication(searchDto, pageable);
    }

    // 신청서 생성
    @Transactional
    public ApplicationResponseDto createApplication(ApplicationRequestDto dto, AccessContext ctx) {
        AccessGuard.requiredPermission(Action.CREATE, ctx, ResourceScope.of(ctx.getUserId()));
        Application application = Application.createApplication(dto, ctx.getUserId());
        applicationRepository.save(application);
        return ApplicationResponseDto.from(application);
    }

    // 신청서 수정
    @Transactional
    public ApplicationResponseDto updateApplication(Long id, ApplicationRequestDto dto, AccessContext ctx) {
        AccessGuard.requiredPermission(Action.UPDATE, ctx, ResourceScope.of(ctx.getUserId()));
        Application application = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        application.updateApplication(dto);
        repository.save(application);
        return ApplicationResponseDto.from(application);
    }

    // 신청서 삭제
    @Transactional
    public void deleteApplication(Long id, AccessContext ctx) {
        AccessGuard.requiredPermission(Action.DELETE, ctx, ResourceScope.of(ctx.getUserId()));
        Application application = repository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.APPLICATION_NOT_FOUND));
        repository.delete(application);
    }
}
