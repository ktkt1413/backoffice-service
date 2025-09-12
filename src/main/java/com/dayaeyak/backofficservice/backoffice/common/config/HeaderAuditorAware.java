package com.dayaeyak.backofficservice.backoffice.common.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

public class HeaderAuditorAware implements AuditorAware<String> {

    private static final String HEADER_USER_ID = "X-User-Id";

    @Override
    public Optional<String> getCurrentAuditor() {
        RequestAttributes attrs = RequestContextHolder.getRequestAttributes();
        if (!(attrs instanceof ServletRequestAttributes sra)) {
            return Optional.empty(); // 요청 스코프 바깥(배치/비동기 등)일 때
        }
        HttpServletRequest request = sra.getRequest();
        String userId = request.getHeader(HEADER_USER_ID);
        return Optional.ofNullable(userId);
    }
}
