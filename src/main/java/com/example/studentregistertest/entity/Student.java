package com.example.studentregistertest.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use auto-increment for primary key
    private long id;

    @Column(nullable = false, length = 20)
    private String name;
    @Column(nullable = false, length = 20)
    private String age;
    @Column(nullable = false, length = 20)
    private String course;
    @Column(nullable = false, length = 20)
    private String gender;
    @Column(nullable = false, length = 20)
    private String dob;
    @Column(nullable = false, length = 50)
    private String address;


}
