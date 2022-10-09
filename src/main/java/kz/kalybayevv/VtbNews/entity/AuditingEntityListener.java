package kz.kalybayevv.VtbNews.entity;

import kz.kalybayevv.VtbNews.constants.Roles;
import kz.kalybayevv.VtbNews.utils.SecurityUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDate;
import java.time.ZoneId;

@Component
@Configuration
public class AuditingEntityListener {
    @PrePersist
    public void fillCreated(AbstractAuditingEntity abstractAuditingEntity) {
        String username = SecurityUtils.getCurrentUserLogin();
        abstractAuditingEntity.setCreatedBy(username != null ? username : Roles.ANONYMOUS);
        abstractAuditingEntity.setCreatedDate(LocalDate.now(ZoneId.systemDefault()));
        abstractAuditingEntity.setLastModifiedBy(username != null ? username : Roles.ANONYMOUS);
        abstractAuditingEntity.setLastModifiedDate(LocalDate.now(ZoneId.systemDefault()));
    }

    @PreUpdate
    public void fillLastModified(AbstractAuditingEntity abstractAuditingEntity) {
        String username = SecurityUtils.getCurrentUserLogin();
        if (abstractAuditingEntity.getCreatedDate() == null) {
            abstractAuditingEntity.setCreatedDate(LocalDate.now(ZoneId.systemDefault()));
        }
        abstractAuditingEntity.setLastModifiedBy(username != null ? username : Roles.ANONYMOUS);
        abstractAuditingEntity.setLastModifiedDate(LocalDate.now(ZoneId.systemDefault()));
    }
}
