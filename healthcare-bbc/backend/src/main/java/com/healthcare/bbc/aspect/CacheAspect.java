package com.healthcare.bbc.aspect;

import com.healthcare.bbc.annotation.CacheEvict;
import com.healthcare.bbc.annotation.Cacheable;
import com.healthcare.bbc.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class CacheAspect {

    private final RedisUtil redisUtil;

    public CacheAspect(RedisUtil redisUtil) {
        this.redisUtil = redisUtil;
    }

    @Around("@annotation(cacheable)")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String cacheKey = buildCacheKey(method, joinPoint.getArgs(), cacheable.key());

        Object cachedValue = redisUtil.get(cacheKey);
        if (cachedValue != null) {
            log.debug("Cache hit for key: {}", cacheKey);
            return cachedValue;
        }

        log.debug("Cache miss for key: {}", cacheKey);
        Object result = joinPoint.proceed();
        redisUtil.set(cacheKey, result, cacheable.expire(), cacheable.timeUnit());
        return result;
    }

    @Around("@annotation(cacheEvict)")
    public Object aroundCacheEvict(ProceedingJoinPoint joinPoint, CacheEvict cacheEvict) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String cacheKey = buildCacheKey(method, joinPoint.getArgs(), cacheEvict.key());

        Object result = joinPoint.proceed();
        redisUtil.delete(cacheKey);
        log.debug("Cache evicted for key: {}", cacheKey);
        return result;
    }

    private String buildCacheKey(Method method, Object[] args, String customKey) {
        if (!customKey.isEmpty()) {
            return customKey;
        }
        StringBuilder keyBuilder = new StringBuilder(method.getDeclaringClass().getSimpleName());
        keyBuilder.append(":").append(method.getName());
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg != null) {
                    keyBuilder.append(":").append(arg.toString());
                }
            }
        }
        return keyBuilder.toString();
    }
}
