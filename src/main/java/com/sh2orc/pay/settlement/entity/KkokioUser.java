package com.sh2orc.pay.settlement.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "kkokio_user",
    uniqueConstraints = {@UniqueConstraint(name = "uq_email", columnNames = {"email"})}
)
public class KkokioUser extends BaseAudit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "mobile_no")
    private String mobileNo;

}
