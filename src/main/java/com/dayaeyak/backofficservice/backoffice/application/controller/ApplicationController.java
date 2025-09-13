package com.dayaeyak.backofficservice.backoffice.application.controller;


import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationRequestDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationResponseDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationSearchDto;
import com.dayaeyak.backofficservice.backoffice.application.service.ApplicationService;
import com.dayaeyak.backofficservice.backoffice.common.response.ApiResponse;
import com.dayaeyak.backofficservice.backoffice.common.security.AccessContext;
import com.dayaeyak.backofficservice.backoffice.common.security.UserRole;
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

    // 유저 권한 확인
    @ModelAttribute
    public AccessContext setAccessContext(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-Role") String role) {
        return AccessContext.of(userId, UserRole.of(role));
    }

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> getApplication(@PathVariable("id") Long id,
                                                                              @ModelAttribute AccessContext ctx) {
        ApplicationResponseDto dto = service.getApplication(id, ctx);
        return ApiResponse.success(HttpStatus.OK, "신청서 조회 성공", dto);
    }


    // 조건 기반 검색(목록)
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ApplicationResponseDto>>> searchApplication(@RequestBody ApplicationSearchDto searchDto,
                                                                                       @ModelAttribute AccessContext ctx,
                                                                                       Pageable pageable) {

        Page<ApplicationResponseDto> pageDto = service.getApplications(searchDto, ctx, pageable);
        return ApiResponse.success(HttpStatus.OK, "신청서 조회 성공", pageDto);
    }

    // 신청서 생성
    @PostMapping
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> createApplication(@Valid @RequestBody ApplicationRequestDto dto,
                                                                                 @ModelAttribute AccessContext ctx) {
        ApplicationResponseDto responseDto = service.createApplication(dto, ctx);
        return ApiResponse.success(HttpStatus.OK, "신청서 생성 성공", responseDto);
    }

    // 신청서 수정
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> updateApplication(@PathVariable Long id,
                                                                                 @Valid @RequestBody ApplicationRequestDto dto,
                                                                                 @ModelAttribute AccessContext ctx) {
        ApplicationResponseDto responseDto = service.updateApplication(id, dto, ctx);
        return ApiResponse.success(HttpStatus.OK, "신청서 수정 성공", responseDto);
    }

    // 신청서 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationResponseDto>> deleteApplication(@PathVariable Long id,
                                                                                 @ModelAttribute AccessContext ctx) {
        service.deleteApplication(id, ctx);
        return ApiResponse.success(HttpStatus.OK, "신청서 삭제 성공", null);
    }
}
