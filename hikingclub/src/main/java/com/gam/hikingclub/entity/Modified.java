package com.gam.hikingclub.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "MODIFIED")
public class Modified {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer seq;
    @Column(name = "modified_id")
    private String modifiedTitle;
    private String url;

}
