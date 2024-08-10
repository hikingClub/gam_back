package com.gam.hikingclub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "EMPATHY")
public class Empathy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    private Integer seq;
    private String empathyTitle;
    private String docId;
    private String date;
}
