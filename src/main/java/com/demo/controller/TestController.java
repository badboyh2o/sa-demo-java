package com.demo.controller;

import com.sensorsdata.analytics.javasdk.ISensorsAnalytics;
import com.sensorsdata.analytics.javasdk.bean.EventRecord;
import com.sensorsdata.analytics.javasdk.bean.ItemRecord;
import com.sensorsdata.analytics.javasdk.bean.UserRecord;
import com.sensorsdata.analytics.javasdk.exceptions.InvalidArgumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author xuhongqiang
 * @date 2023年08月01日 15:28
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private ISensorsAnalytics sa;

    /**
     * 用户匿名访问的场景
     *
     * 客户端生成 cookieId 并提交服务端 （如何传递）
     * @return
     * @throws InvalidArgumentException
     */
    @GetMapping("/anonymous")
    String anonymous() throws InvalidArgumentException {

        // 1. 用户匿名访问网站，cookieId 默认神策生成分配
        String cookieId = "ABCDEF123456789";

        // 1.1 访问首页
        // 前面有$开头的property字段，是SA提供给用户的预置字段
        // 对于预置字段，已经确定好了字段类型和字段的显示名
        EventRecord firstRecord = EventRecord.builder().setDistinctId(cookieId).isLoginId(Boolean.FALSE)
                .setEventName("track")
                // '$time' 属性是系统预置属性，表示事件发生的时间，如果不填入该属性，则默认使用系统当前时间
                .addProperty("$time", Calendar.getInstance().getTime())
                .addProperty("Channel", "baidu")
                .addProperty("$project", "abc")
                .addProperty("$token", "123")
                .build();
        sa.track(firstRecord);
        // 1.2 搜索商品
        EventRecord searchRecord = EventRecord.builder().setDistinctId(cookieId).isLoginId(Boolean.FALSE)
                .setEventName("SearchProduct")
                .addProperty("KeyWord", "XX手机")
                .build();
        sa.track(searchRecord);
        // 1.3 浏览商品
        EventRecord lookRecord = EventRecord.builder().setDistinctId(cookieId).isLoginId(Boolean.FALSE)
                .setEventName("ViewProduct")
                .addProperty("ProductName", "XX手机")
                .addProperty("ProductType", "智能手机")
                .addProperty("ShopName", "XX官方旗舰店")
                .build();
        sa.track(lookRecord);

        return "ok";
    }


    /**
     * 用户注册的场景
     *  cookieId与登录ID关联
     * @return
     * @throws InvalidArgumentException
     */
    @GetMapping("/signup")
    String signup() throws InvalidArgumentException {

        // 1. 用户匿名访问网站，cookieId 默认神策生成分配
        String cookieId = "ABCDEF123456789";

        // 2. 用户注册登录之后，系统分配的注册ID
        String registerId = "user-123456";
        //使用trackSignUp关联用户匿名ID和登录ID
        sa.trackSignUp(registerId, cookieId);

        // 2.2 用户注册时，填充了一些个人信息，可以用Profile接口记录下来
        List<String> interests = new ArrayList<String>();
        interests.add("movie");
        interests.add("swim");
        UserRecord userRecord = UserRecord.builder().setDistinctId(registerId).isLoginId(Boolean.TRUE)
                .addProperty("$city", "武汉")
                .addProperty("$province", "湖北")
                .addProperty("$name", "昵称123")
                .addProperty("$signup_time", Calendar.getInstance().getTime())
                .addProperty("Gender", "male")
                .addProperty("age", 20)
                .addProperty("interest", interests)
                .build();
        sa.profileSet(userRecord);

        //2.3 设置首次访问时间
        UserRecord firstVisitRecord = UserRecord.builder().setDistinctId(registerId).isLoginId(Boolean.TRUE)
                .addProperty("$first_visit_time", Calendar.getInstance().getTime())
                .build();
        sa.profileSetOnce(firstVisitRecord);

        return "ok";
    }


    /**
     * 用户登录后的业务操作的场景
     *
     * @return
     * @throws InvalidArgumentException
     */
    @GetMapping("/biz")
    String biz() throws InvalidArgumentException {

        // 2. 用户注册登录之后，系统分配的注册ID
        String registerId = "user-123456";

        // 3. 用户注册后，进行后续行为
        // 3.1 提交订单和提交订单详情
        // 订单的信息
        EventRecord orderRecord = EventRecord.builder().setDistinctId(registerId).isLoginId(Boolean.TRUE)
                .setEventName("SubmitOrder")
                .addProperty("OrderId", "SN_123_AB_TEST")
                .build();
        sa.track(orderRecord);

        // 3.2 支付订单和支付订单详情
        // 整个订单的支付情况
        EventRecord payRecord = EventRecord.builder().setDistinctId(registerId).isLoginId(Boolean.TRUE)
                .setEventName("PayOrder")
                .addProperty("PaymentMethod", "AliPay")
                .addProperty("AllowanceAmount", 30.0)
                .addProperty("PaymentAmount", 1204.0)
                .build();
        sa.track(payRecord);

        //物品纬度表上报
        String itemId = "product001", itemType = "mobile";
        ItemRecord addRecord = ItemRecord.builder().setItemId(itemId).setItemType(itemType)
                .addProperty("color", "white")
                .build();
        sa.itemSet(addRecord);

        //删除物品纬度信息
        ItemRecord deleteRecord = ItemRecord.builder().setItemId(itemId).setItemType(itemType)
                .build();
        sa.itemDelete(deleteRecord);

        return "ok";
    }


}
