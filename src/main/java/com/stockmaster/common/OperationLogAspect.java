package com.stockmaster.common;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * AOP 操作日志：记录所有 Controller 方法的请求和耗时
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Around("execution(* com.stockmaster.wms.controller..*.*(..))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String module = joinPoint.getTarget().getClass().getSimpleName();
        String operation = method.getName();
        String url = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        Object result;
        int code = 200;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            code = 500;
            throw e;
        } finally {
            long cost = System.currentTimeMillis() - start;
            try {
                jdbcTemplate.update(
                    "INSERT INTO wms_interface_log(module,operation,request_url,request_params,response_code,cost_ms,create_time) VALUES(?,?,?,?,?,?,?)",
                    module, operation, url, JSON.toJSONString(args), code, cost, LocalDateTime.now());
            } catch (Exception ignored) {
                // 日志记录失败不影响业务
            }
        }
        return result;
    }
}
