package com.gam.hikingclub.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "MODIFIED")
public class Modified {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;

    @Column(name = "modified_title")
    private String modifiedTitle;

    @Column(name = "modified_id")
    private String modifiedId;

    private String url;

    private String date;

    @JsonFormat(pattern="yyyy.MM.dd HH:mm:ss")
    @Column(name = "created_date", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();
}
