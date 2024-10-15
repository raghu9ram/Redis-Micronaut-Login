package com.example.cached.handlers

class UserNotFound extends RuntimeException {

    UserNotFound(String msg) {
        super(msg)
    }
}
