package com.gam.hikingclub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "NOTIFICATION")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="member_seq")
    private Integer memberSeq;

    @ManyToOne
    @JoinColumn(name = "modified_seq", referencedColumnName = "seq")
    private Modified modified;

    private boolean checked;
}
