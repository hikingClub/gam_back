package com.gam.hikingclub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "DOCUMENT")
public class Document {
    @Id
    private String docId;
    private String title;
    private String date;
    private Integer empathy;
    private Integer views;
}
