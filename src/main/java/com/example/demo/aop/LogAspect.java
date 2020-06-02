package com.example.demo.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAspect {
    Logger logger = Logger.getLogger(LogAspect.class);

    @Pointcut("execution(public * com.example.demo.controller.*.*(..))")
    public void logControllerAspect(){}

    @Pointcut("execution(public * com.example.demo.service.*.*.*(..))")
    public void logServiceAspect(){}

    @Pointcut("logControllerAspect() || logServiceAspect()")
    public void logAspect(){}

    public Object deAround(ProceedingJoinPoint joinPoint) throws Throwable {
        long begin = System.currentTimeMillis();
        logger.info("start exec: " + joinPoint.getSignature().toString());
        Object object = joinPoint.proceed();
        long end = System.currentTimeMillis();
        logger.info("finished exec: " +  joinPoint.getSignature().toString()
        + "     exec cost: " + (end - begin));
        return object;
    }

}
