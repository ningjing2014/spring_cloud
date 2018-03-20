package com.ln.xproject.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付系统回调
 *
 * @author ln
 */
@Slf4j
@Controller
@RequestMapping("/verify/recall/pay")
public class PayRecallController {

    @Autowired
    private ApplicationService applicationService;

    /**
     * 代付回调
     *
     * @param data
     * @param response
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String pay(String data, HttpServletResponse response) {
        try {
            JSONObject json = JSONObject.parseObject(data);
            applicationService.saveLoanRecall(json);
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            log.error("支付系统代付回调失败, data:{}", data, e);
        }
        return StringUtils.EMPTY;
    }

}
