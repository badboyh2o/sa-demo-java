package com.demo.config;

import com.sensorsdata.analytics.javasdk.ISensorsAnalytics;
import com.sensorsdata.analytics.javasdk.SensorsAnalytics;
import com.sensorsdata.analytics.javasdk.bean.SuperPropertiesRecord;
import com.sensorsdata.analytics.javasdk.consumer.ConcurrentLoggingConsumer;
import com.sensorsdata.analytics.javasdk.exceptions.InvalidArgumentException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author xuhongqiang
 * @date 2023年08月01日 15:26
 */
@Configuration
public class SaConfig {

    @Bean(destroyMethod = "shutdown")
    public ISensorsAnalytics init() throws IOException, InvalidArgumentException {
        //本地日志模式（此模式会在指定路径生成相应的日志文件）  实时写入？批量写入？
        SensorsAnalytics sa = new SensorsAnalytics(new ConcurrentLoggingConsumer("data.log"));


        //设置公共属性,以后上传的每一个事件都附带该属性
        SuperPropertiesRecord propertiesRecord = SuperPropertiesRecord.builder()
                .addProperty("$os", "Windows")
                .addProperty("$os_version", "8.1")
                .addProperty("$ip", "123.123.123.123")
                .build();
        sa.registerSuperProperties(propertiesRecord);

        return sa;



        //debug 模式(此模式只适用于测试集成 SDK 功能，千万不要使用到生产环境！！！)
        //return new SensorsAnalytics(new DebugConsumer("数据接收地址", true));

        //网络批量发送模式（此模式在容器关闭的时候，如果存在数据还没有发送完毕，就会丢失未发送的数据！！！）
        //return new SensorsAnalytics(new BatchConsumer("数据接收地址"));
    }


}
