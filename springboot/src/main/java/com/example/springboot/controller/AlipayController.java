package com.example.springboot.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.example.springboot.common.AlipayConfig;
import com.example.springboot.entity.Course;
import com.example.springboot.entity.Order;
import com.example.springboot.service.CourseService;
import com.example.springboot.service.OrderService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/alipay")
public class AlipayController {

    @Resource
    private AlipayConfig alipayConfig;

    @Resource
    private OrderService orderService;

    @Resource
    private CourseService courseService;

    private static final String GATEWAY_URL = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private static final String FORMAT = "json";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";


    /**
     * 发起支付请求（PC网页支付）
     */
    @GetMapping("/pay")
    public void pay(String orderNo, HttpServletResponse httpResponse) throws Exception {
        // 查询订单信息
        Order order = orderService.selectByOrderNo(orderNo);
        if (order == null) {
            return;
        }

        // 获取课程标题
        Course course = courseService.getCourseTitleById(order.getCourseId());
        if (course != null) {
            order.setCourseTitle(course.getTitle());  // 填充 courseTitle
        }

        // 1. 创建支付宝客户端
        AlipayClient alipayClient = new DefaultAlipayClient(GATEWAY_URL, alipayConfig.getAppId(),
                alipayConfig.getAppPrivateKey(), FORMAT, CHARSET, alipayConfig.getAlipayPublicKey(), SIGN_TYPE);

        // 2. 创建支付请求并设置参数
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(alipayConfig.getNotifyUrl());
        JSONObject bizContent = new JSONObject();
        bizContent.set("out_trade_no", order.getOrderNo());
        bizContent.set("total_amount", String.format("%.2f", order.getTotalPrice()));
        bizContent.set("subject", order.getCourseTitle());  // 使用填充后的课程标题
        bizContent.set("product_code", "FAST_INSTANT_TRADE_PAY");  // 固定配置
        request.setBizContent(bizContent.toString());
        request.setReturnUrl("http://localhost:5173/user/payOrder");

        System.out.println("支付宝支付参数: " + bizContent.toString());
        System.out.println("商品标题: " + order.getCourseTitle());

        // 执行请求，拿到响应的结果，返回给浏览器
        String form = "";
        try {
            form = alipayClient.pageExecute(request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        httpResponse.setContentType("text/html;charset=" + CHARSET);
        httpResponse.getWriter().write(form);
        httpResponse.getWriter().flush();
        httpResponse.getWriter().close();
    }


    /**
     * 支付宝服务器异步通知接口（必须为公网地址，且无需登录）
     */
    @PostMapping("/notify")
    public void payNotify(HttpServletRequest request) throws Exception{
        if(request.getParameter("trade_status").equals("TRADE_SUCCESS")){
            System.out.println("========支付宝异步回调========");
            Map<String,String> params = new HashMap<>();
            Map<String,String[]> requestParams = request.getParameterMap();
            for(String name : requestParams.keySet()){
                params.put(name, request.getParameter(name));
            }

            String sign = params.get("sign");
            String content = AlipaySignature.getSignCheckContentV1(params);
            boolean CheckSignature = AlipaySignature.rsa256CheckContent(content, sign, alipayConfig.getAlipayPublicKey(), "UTF-8");
            if(CheckSignature){
                System.out.println("交易名称：" + params.get("subject"));
                System.out.println("交易状态：" + params.get("trade_status"));
                System.out.println("支付宝交易凭证号：" + params.get("trade_no"));
                System.out.println("商户订单号：" + params.get("out_trade_no"));
                System.out.println("交易金额：" + params.get("total_amount"));
                System.out.println("买家在支付宝唯一ID：" + params.get("buyer_id"));
                System.out.println("买家付款时间：" + params.get("gmt_payment"));
                System.out.println("买家付款金额：" + params.get("buyer_pay_amount"));

                String tradeNo = params.get("out_trade_no");
                String gmtPayment = params.get("gmt_payment");
                String alipayTradeNo = params.get("trade_no");

                //更新订单状态
                Order order = orderService.selectByOrderNo(tradeNo);
                order.setStatus("已支付");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date paymentDate = sdf.parse(gmtPayment); // 将字符串转为 Date

                order.setPaymentDate(paymentDate);
                order.setTradeNo(alipayTradeNo);
                orderService.update(order);

            }
        }
    }
}
