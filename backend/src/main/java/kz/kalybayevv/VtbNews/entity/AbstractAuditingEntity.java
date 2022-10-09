package kz.kalybayevv.VtbNews.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @CreatedBy
    @Field("created_by")
    @JsonIgnore
    private String createdBy;

    @CreatedDate
    @Field("created_date")
    @JsonIgnore
    private LocalDate createdDate = LocalDate.now(ZoneId.systemDefault());

    @LastModifiedBy
    @Field("last_modified_by")
    @JsonIgnore
    private String lastModifiedBy;

    @LastModifiedDate
    @Field("last_modified_date")
    @JsonIgnore
    private LocalDate lastModifiedDate = LocalDate.now(ZoneId.systemDefault());
}
