package com.hzm.chatdemo;

import com.hzm.chatdemo.netty.WSServer;
import javafx.application.Application;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
//监听springboot，通过ApplicationListener监听ContextRefreshedEvent类型的事件
public class NettyBooter implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        //判断事件上下文对象的parent为空，则启动netty
        if (contextRefreshedEvent.getApplicationContext().getParent() == null){
            try {
                WSServer.getInstance().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
