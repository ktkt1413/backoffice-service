package com.dayaeyak.backofficservice.backoffice.application.repository;

import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationResponseDto;
import com.dayaeyak.backofficservice.backoffice.application.dtos.ApplicationSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationRepositoryCustom {
    Page<ApplicationResponseDto> searchApplication(ApplicationSearchDto applicationSearchDto, Pageable pageable);
}
