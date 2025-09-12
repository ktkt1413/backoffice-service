package com.dayaeyak.backofficservice.backoffice.application.repository;


import com.dayaeyak.backofficservice.backoffice.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long>,ApplicationRepositoryCustom {
    Optional<Application> findByIdAndDeletedAtIsNull(Long id);

}
