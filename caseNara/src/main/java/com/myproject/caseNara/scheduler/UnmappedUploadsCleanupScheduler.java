package com.myproject.caseNara.scheduler;

import com.myproject.caseNara.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UnmappedUploadsCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(UnmappedUploadsCleanupScheduler.class);

    private final ProductService productService;

    public UnmappedUploadsCleanupScheduler(ProductService productService) {
        this.productService = productService;
    }

    // 매일 오전 3시(Asia/Seoul 기준) 실행
    @Scheduled(cron = "0 0 3 * * *", zone = "Asia/Seoul")
    public void runDailyCleanup() {
        try {
            int deleted = productService.cleanupUnmappedLocalUploads();
            log.info("고아 업로드 정리 완료. 삭제된 파일 수: {}", deleted);
        } catch (Exception e) {
            log.error("고아 업로드 정리 실패: {}", e.getMessage(), e);
        }
    }
}