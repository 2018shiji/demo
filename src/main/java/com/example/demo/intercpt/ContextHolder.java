package com.example.demo.intercpt;

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 添加和获取本地线程缓存信息
 */
@Component
public class ContextHolder {
    private ThreadLocal<Context> contextThreadLocal = new ThreadLocal<>();

    public boolean available(){
        return contextThreadLocal.get() != null;
    }

    public Context getContext(){
        Context ctx = contextThreadLocal.get();
        if(ctx == null){
            ctx = new Context();
            contextThreadLocal.set(ctx);
        }

        return ctx;
    }

    public void clearContext(){
        contextThreadLocal.remove();
    }

    public <T> T getAttributeByType(String name, Class<T> t){
        try{
            return (T) Optional.ofNullable(getContext().getAttributes()).map((m) -> {
                return m.get(name);
            }).orElse(null);
        } catch (Exception e){
            return null;
        }
    }
}
