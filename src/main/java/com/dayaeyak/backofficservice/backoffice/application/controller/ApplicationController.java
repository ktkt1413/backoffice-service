package com.dayaeyak.backofficservice.backoffice.application.controller;


import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationRequestDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationResponseDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationSearchDto;
import com.dayaeyak.backofficservice.backoffice.application.service.ApplicationService;
import com.dayaeyak.backofficservice.backoffice.common.response.ApiResponse;
import com.dayaeyak.backofficservice.backoffice.common.security.Action;
import com.dayaeyak.backofficservice.backoffice.user.annotation.Authorize;
import com.dayaeyak.backofficservice.backoffice.user.annotation.PassportHolder;
import com.dayaeyak.backofficservice.backoffice.user.dto.Passport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("backOffice/application")
@RestController
public class ApplicationController {

    private final ApplicationService service;

    // 단건 조회
    @Authorize( checkOwner = true, action = Action.READ, resourceId = "id")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> getApplication(@PathVariable("id") Long id,
                                                                              @PassportHolder Passport passport) {
        ApplicationResponseDto dto = service.getApplication(id);
        return ApiResponse.success(HttpStatus.OK, "신청서 조회 성공", dto);
    }

    // 조건 기반 검색(목록)
    @Authorize(checkOwner = true, action = Action.READ)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ApplicationResponseDto>>> searchApplication(@RequestBody ApplicationSearchDto searchDto,
                                                                                         @PassportHolder Passport passport,
                                                                                         Pageable pageable) {
        Page<ApplicationResponseDto> pageDto = service.getApplications(searchDto, pageable);
        return ApiResponse.success(HttpStatus.OK, "신청서 조회 성공", pageDto);
    }

    // 신청서 생성
    @Authorize(checkOwner = false, action = Action.CREATE)
    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> createApplication(@Valid @RequestBody ApplicationRequestDto dto,
                                                                                   @PassportHolder Passport passport) {
        ApplicationResponseDto responseDto = service.createApplication(dto, passport.userId());
        return ApiResponse.success(HttpStatus.OK, "신청서 생성 성공", responseDto);
    }

    // 신청서 수정
    @Authorize(checkOwner = true, action = Action.UPDATE, resourceId = "id")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> updateApplication(@PathVariable Long id,
                                                                                   @Valid @RequestBody ApplicationRequestDto dto,
                                                                                   @PassportHolder Passport passport
    ) {
        ApplicationResponseDto responseDto = service.updateApplication(id, dto);
        return ApiResponse.success(HttpStatus.OK, "신청서 수정 성공", responseDto);
    }

    // 신청서 삭제
    @Authorize(checkOwner = true, action = Action.DELETE, resourceId = "id")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> deleteApplication(@PathVariable Long id,
                                                                                   @PassportHolder Passport passport
    ) {
        service.deleteApplication(id);
        return ApiResponse.success(HttpStatus.OK, "신청서 삭제 성공", null);
    }

    // 신청서 승인 요청
    @Authorize(checkOwner = true, action = Action.UPDATE, resourceId = "id")
    @PostMapping("/{id}/register")
    public ResponseEntity<ApiResponse<Void>> requestApproval(@PathVariable Long id,
                                                               @PassportHolder Passport passport
    ) {
        service.requestApproval(id, passport.userId());
        return ApiResponse.success(HttpStatus.OK, "신청 완료", null);
    }

    // 신청서 승인
    @Authorize(action = Action.APPROVE, resourceId = "id")
    @PostMapping("/{id}/approve")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> approve(@PathVariable Long id,
                                                                         @PassportHolder Passport passport
    ) {
        ApplicationResponseDto dto = service.approveApplication(id, passport.userId(), passport.role());
        return ApiResponse.success(HttpStatus.OK, "승인 완료", dto);
    }

    // 신청서 거절
    @Authorize(action = Action.REJECT, resourceId = "id")
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> reject(@PathVariable Long id,
                                                                        @PassportHolder Passport passport
    ) {
        ApplicationResponseDto dto = service.rejectApplication(id, passport.userId());
        return ApiResponse.success(HttpStatus.OK, "거절 완료", dto);
    }
}
