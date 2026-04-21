package com.pymerstan.server.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "countries")
@Getter
@Setter
@NoArgsConstructor
public class Country {

    @Id
    private Integer id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "visiable", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean visible;
}