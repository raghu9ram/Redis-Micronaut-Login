package com.example.cached

import jakarta.inject.Singleton
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool

@Singleton
class RedisConfig {
    private final JedisPool jedisPool

    RedisConfig() {
        // Configure your Redis connection parameters
        String redisHost = "localhost"  // Change this if your Redis is hosted elsewhere
        int redisPort = 6379  // Default Redis port

        // Create a JedisPool for connection management
        jedisPool = new JedisPool(redisHost, redisPort)
    }

    Jedis getJedis() {
        return jedisPool.resource
    }
}
