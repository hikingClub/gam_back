package com.gam.hikingclub.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "VIEWHISTORY")
public class ViewHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idx;

    private Integer seq;
    private String viewTitle;
    private String docId;
    private String date;

    @JsonFormat(pattern="yyyy.MM.dd HH:mm:ss")
    private LocalDateTime viewTime;
}
