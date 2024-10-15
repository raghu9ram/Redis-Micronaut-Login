package com.example.cached.service

import com.example.cached.RedisConfig
import com.example.cached.domain.UserrDomain
import com.example.cached.handlers.UserNotFound
import com.example.cached.model.UserrModel
import grails.gorm.transactions.Transactional
import jakarta.inject.Inject
import jakarta.inject.Singleton
import redis.clients.jedis.Jedis

@Singleton
class UserService {
    @Inject
    RedisConfig redisConfig

    @Transactional
    def createAUser(UserrModel userModel) {
        UserrDomain userDomain = toUserDomain(userModel)
        userDomain.save(flush: true)

        // Cache the user's password in Redis
        cacheUserCredentials(userDomain.email, userDomain.password)

        return toUserModel(userDomain)
    }

    @Transactional
    def userLogin(String email, String password) {
        // Check Redis for cached password
        String cachedPassword = getCachedPassword(email)
        if (cachedPassword) {
            if (cachedPassword == password) {
                return toUserModel(UserrDomain.findByEmail(email))  // Fetch user details if needed
            } else {
                throw new UserNotFound("Invalid Credentials")
            }
        }

        // Fetch user from PostgreSQL if not found in cache
        UserrDomain userrDomain = UserrDomain.findByEmail(email)
        if (!userrDomain || userrDomain.password != password) {
            throw new UserNotFound("Invalid Credentials")
        }
        cacheUserCredentials(email, userrDomain.password)
        return [message: "Using PostgreSQL credentials for user: $email", user: toUserModel(userrDomain)]
    }

    private String getCachedPassword(String email) {
        Jedis jedis = redisConfig.getJedis()
        try {
            return jedis.get(email)
        } finally {
            jedis.close()
        }
    }

    private void cacheUserCredentials(String email, String password) {
        Jedis jedis = redisConfig.getJedis()
        try {
            jedis.setex(email, 45, password)
        } finally {
            jedis.close()
        }
    }

    static UserrDomain toUserDomain(UserrModel userModel) {
        new UserrDomain(
                email: userModel.email,
                password: userModel.password,
                firstName: userModel.firstName,
                lastName: userModel.lastName,
                mobileNumber: userModel.mobileNumber
        )
    }

    static UserrModel toUserModel(UserrDomain userDomain) {
        new UserrModel(
                email: userDomain.email,
                password: userDomain.password,
                firstName: userDomain.firstName,
                lastName: userDomain.lastName,
                mobileNumber: userDomain.mobileNumber
        )
    }
}
