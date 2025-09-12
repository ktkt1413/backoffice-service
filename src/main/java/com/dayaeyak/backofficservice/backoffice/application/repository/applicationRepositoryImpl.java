package com.dayaeyak.backofficservice.backoffice.application.repository;

import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationResponseDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationSearchDto;
import com.dayaeyak.backofficservice.backoffice.common.enums.BusinessType;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static com.dayaeyak.backofficservice.backoffice.application.entity.QApplication.application;

@RequiredArgsConstructor
public class applicationRepositoryImpl implements ApplicationRepositoryCustom {
    private JPAQueryFactory queryFactory;

    @Override
    public Page<ApplicationResponseDto> searchApplication(ApplicationSearchDto searchDto, Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        //Id 조회
        if (searchDto.getId() != null) {
            builder.and(application.id.eq(searchDto.getId()));
        }
        if (searchDto.getSellerId() != null) {
            builder.and(application.sellerId.eq(searchDto.getSellerId()));
        }
        if (searchDto.getRegistrationNumber() != null) {
            builder.and(application.registrationNumber.eq(searchDto.getRegistrationNumber()));
        }

        // 다중 조건
        builder.and(businessNameContains(searchDto.getBusinessName()));
        builder.and(ownerContains(searchDto.getOwner()));
        builder.and(orderDateBetween(searchDto.getCreatedFrom(), searchDto.getCreatedTo()));
        builder.and(BusinessTypeEq(searchDto.getBusinessType()));

        // 실제 조화
        List<ApplicationResponseDto> applications = queryFactory
                .select(Projections.constructor(ApplicationResponseDto.class,
                        application.id,
                        application.sellerId,
                        application.registrationNumber,
                        application.businessName,
                        application.owner,
                        application.address,
                        application.contactNumber,
                        application.email,
                        application.createdAt,
                        application.updatedAt
                ))
                .from(application)
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        // 전체 카운트
        Long total = queryFactory
                .select(application.count())
                .from(application)
                .where(builder)
                .fetchOne();

        long totalCount = (total != null) ? total : 0;

        return new PageImpl<>(applications, pageable, totalCount);
    }

    // 사업명 조회
    private BooleanExpression businessNameContains(String name) {
        return name != null ? application.businessName.containsIgnoreCase(name) : null;
    }

    // 사장 이름으로 조회
    private BooleanExpression ownerContains(String name) {
        return name != null ? application.owner.containsIgnoreCase(name) : null;
    }

    // 기간 조회
    private BooleanExpression orderDateBetween(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null) {
            return application.createdAt.between(start, end);
        } else if (start != null) {
            return application.createdAt.goe(start);
        } else if (end != null) {
            return application.createdAt.lt(end);
        } else {
            return null;
        }

    }

    //신청 사업 타입별 조회
    private BooleanExpression BusinessTypeEq(BusinessType type) {
        return type != null ? application.businessType.eq(type) : null;
    }
}
