package com.dayaeyak.backofficservice.backoffice.client.dtos;

import com.dayaeyak.backofficservice.backoffice.client.enums.performance.Grade;
import com.dayaeyak.backofficservice.backoffice.client.enums.performance.Type;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CreatePerformanceRequestDto{

        @Schema(description = "판매자 ID", example = "3")
        @NotNull(message = "판매자 ID는 필수 입력값입니다.")
        Long sellerId;

        @Schema(description = "공연 이름", example = "오페라의 유령")
        @NotBlank(message = "공연 이름은 필수 입력값입니다.")
        @Size(max = 100, message = "공연 이름은 100자까지 입력 가능합니다.")
        String performanceName;

        @Schema(description = "공연장 ID", example = "4")
        @NotNull(message = "공연장 ID는 필수 입력값입니다.")
        Long hallId;

        @Schema(description = "출연진 목록", example = "아이유,박효신,NCT 127")
        @NotBlank(message = "출연진 목록은 필수 입력값입니다.")
        String castList;

        @Schema(description = "공연 설명", example = "이거완전쩔어용")
        @NotBlank(message = "공연 설명은 필수 입력값입니다.")
        @Size(max = 255, message = "공연 설명은 255자까지 입력 가능합니다.")
        String description;

        @Schema(description = "공연 타입", example = "MUSICAL")
        @NotNull(message = "공연 타입은 필수 입력값입니다.")
        Type type;

        @Schema(description = "관람 가능 연령", example = "ALL")
        @NotNull(message = "관람 가능 연령은 필수 입력값입니다.")
        Grade grade;

        @Schema(description = "공연 시작일", example = "2025-10-01")
        @NotNull(message = "공연 시작일은 필수 입력값입니다.")
        LocalDate startDate;

        @Schema(description = "공연 종료일", example = "2025-10-31")
        @NotNull(message = "공연 종료일은 필수 입력값입니다.")
        LocalDate endDate;

        @Schema(description = "티켓 오픈 일시", example = "2025-09-01T09:00:00")
        @NotNull(message = "티켓 오픈 일시는 필수 입력값입니다.")
        LocalDateTime ticketOpenAt;

        @Schema(description = "티켓 마감 일시", example = "2025-10-30T23:59:59")
        @NotNull(message = "티켓 마감 일시는 필수 입력값입니다.")
        LocalDateTime ticketCloseAt;

        @Schema(description = "공연 활성화 여부", example = "false", defaultValue = "true")
        Boolean isActivated;
}
