package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import static com.sky.constant.AutoFillConstant.*;

/**
 * 自定义切面，实现公共字段自动填充处理逻辑
 */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointcut() {}
    //传入切点表达式，前面相当于将切点表达式封装成一个变量，变量名就是函数名，函数就是切点表达式
    @Before("autoFillPointcut()")
    public void autoFill(JoinPoint joinPoint) throws Throwable {
        log.info("开始进行公共字段填充...");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        //获取注解中的值，看是插入还是更新
        OperationType operationType = autoFill.value();
        Object[] args = joinPoint.getArgs();
        if (args==null && args.length==0) {
            return;
        }
        //获取传入的对象
        Object entity = args[0];
        //准备数据
        LocalDateTime now = LocalDateTime.now();
        Long uid = BaseContext.getCurrentId();

        if(operationType==OperationType.INSERT) {
            //获取实体中方法并反射调用
            Method setUpdateTime = entity.getClass().getMethod(SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getMethod(SET_UPDATE_USER, Long.class);
            Method setCreateTime = entity.getClass().getMethod(SET_CREATE_TIME, LocalDateTime.class);
            Method setCreateUser = entity.getClass().getMethod(SET_CREATE_USER, Long.class);

            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, uid);
            setCreateTime.invoke(entity, now);
            setCreateUser.invoke(entity, uid);
        }else if(operationType==OperationType.UPDATE) {
            Method setUpdateTime = entity.getClass().getMethod(SET_UPDATE_TIME, LocalDateTime.class);
            Method setUpdateUser = entity.getClass().getMethod(SET_UPDATE_USER, Long.class);
            setUpdateTime.invoke(entity, now);
            setUpdateUser.invoke(entity, uid);
        }
    }
}
