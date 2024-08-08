package com.gam.hikingclub.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "NOTIFICATION")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer memberSeq;
    private String message;
    private boolean checked;

    @JsonFormat(pattern="yyyy.MM.dd HH:mm:ss") // JSON 직렬화 시 형식 지정
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedDate;
}
