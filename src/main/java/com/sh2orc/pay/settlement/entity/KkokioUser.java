package com.sh2orc.pay.settlement.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "kkokio_user",
    uniqueConstraints = {@UniqueConstraint(name = "uq_email", columnNames = {"email"})}
)
public class KkokioUser extends BaseAudit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "balance", nullable = false, columnDefinition = "bigint default 0")
    private Long balance;

    //잔고 증가
    public KkokioUser increaseBalance(Long amount){
        this.balance += amount;
        return this;
    }

    //잔고 줄임
    public KkokioUser reduceBalance(Long amount){
        this.balance -= amount;
        return this;
    }

    @Builder
    public KkokioUser(Long id, String name, String email, String password, String mobileNo, Long balance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.mobileNo = mobileNo;
        this.balance = balance;
    }
}
