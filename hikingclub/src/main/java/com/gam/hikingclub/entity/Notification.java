package com.gam.hikingclub.entity;

import jakarta.persistence.*;
import lombok.Data;

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

    @ManyToOne
    @JoinColumn(name = "modified_seq", referencedColumnName = "seq")
    private Modified modified; // 업데이트된 정보에 대한 참조
}
