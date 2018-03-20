package com.ln.xproject.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ln.xproject.api.service.ApplicationApiService;
import com.ln.xproject.api.vo.ApplicationImportRequestVo;
import com.ln.xproject.api.vo.ApplicationImportResponseVo;
import com.ln.xproject.api.vo.ApplicationStatusRequestVo;
import com.ln.xproject.api.vo.ApplicationStatusResponseVo;
import com.ln.xproject.base.controller.BaseController;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.job.service.ApplicationJobService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/appl")
public class ApplicationApiController extends BaseController {

    @Autowired
    private ApplicationApiService applicationApiService;
    @Autowired
    private ApplicationJobService applicationJobService;

    @RequestMapping(value = "/import", method = RequestMethod.POST)
    @ResponseBody
    public JsonResultVo importAppl(@RequestBody ApplicationImportRequestVo importRequest) {
        try {
            ApplicationImportResponseVo applInfo = applicationApiService.importAppl(importRequest);
            return JsonResultVo.success().setData(applInfo);
        } catch (ServiceException e) {
            log.error("进件失败:{}", e.getMessage(), e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("进件失败", e);
            return JsonResultVo.error();
        }
    }

    @RequestMapping(value = "/status", method = RequestMethod.POST)
    @ResponseBody
    public JsonResultVo getApplStatus(@RequestBody ApplicationStatusRequestVo statusRequest) {
        try {
            ApplicationStatusResponseVo statusResult = applicationApiService.getApplStatus(statusRequest);
            return JsonResultVo.success().setData(statusResult);
        } catch (ServiceException e) {
            log.error("查询进件状态失败:{}", e.getMessage(), e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询进件状态失败", e);
            return JsonResultVo.error();
        }
    }

    @RequestMapping(value = "/credit", method = RequestMethod.POST)
    @ResponseBody
    public JsonResultVo creditReport(@RequestParam(required = true, value = "applId") Long applId) {
        try {
            applicationJobService.initAppl(applId);
            return JsonResultVo.success();
        } catch (ServiceException e) {
            log.error("查询进件状态失败:{}", e.getMessage(), e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询进件状态失败", e);
            return JsonResultVo.error();
        }
    }

}
