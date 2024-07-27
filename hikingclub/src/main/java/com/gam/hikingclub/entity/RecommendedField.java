package com.gam.hikingclub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "RECOMMENDEDFIELD")
public class RecommendedField {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer recIndex;
    private String category1;
    private String category2;
}
