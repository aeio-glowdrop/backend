package com.unithon.aeio.domain.member.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_agreement")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserAgreement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_agreement_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false, unique = true)
    private Member member;

    @Column(nullable = false, length = 20)
    private String termsVersion;
    @Column(nullable = false, length = 20)
    private String privacyVersion;

    @Column(nullable = false)
    private Boolean termsAgree;
    @Column(nullable = false)
    private Boolean privacyAgree;
    @Column(nullable = false)
    private Boolean personalInfoAgree;
    @Column(nullable = false)
    private Boolean ageOver14At;
    @Column
    private Boolean marketingAgree;
    @Column(nullable = false)
    private LocalDateTime agreedAt;
}