package com.example.cached.domain

import grails.gorm.annotation.Entity

@Entity
class UserrDomain {
    String email
    String password
    String firstName
    String lastName
    Long mobileNumber

    static constraints = {
        email(email: true, unique: true)
        password(blank: false, size: 8..100)
        firstName(blank: false)
        lastName(blank: false)
        mobileNumber(nullable: true) // Adjust based on your requirements
    }
}
