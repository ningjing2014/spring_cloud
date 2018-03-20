package com.ln.xproject.system.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.base.controller.BaseController;
import com.ln.xproject.util.UserUtils;
import com.ln.xproject.api.vo.ApplicationListRequestVo;
import com.ln.xproject.application.service.ApplicationAuditLogService;
import com.ln.xproject.application.service.ApplicationAuditService;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.application.vo.ApplicationAuditLogListVo;
import com.ln.xproject.application.vo.ApplicationDetailVo;
import com.ln.xproject.application.vo.ApplicationListVo;
import com.ln.xproject.application.vo.credit.CreditDetailVo;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.base.vo.PageVo;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.util.CollectionUtils;
import com.ln.xproject.util.StringUtils;
import com.ln.xproject.web.service.ApplicationWebService;
import com.ln.xproject.web.service.CreditReportService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/verify/appl")
public class ApplicationWebController extends BaseController {

    @Autowired
    private ApplicationWebService applicationWebService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private CreditReportService creditReportService;

    @Autowired
    private ApplicationAuditLogService applicationAuditLogService;

    @Autowired
    private ApplicationAuditService applicationAuditService;

    @RequestMapping("/list")
    public JsonResultVo listAppl(ApplicationListRequestVo listRequest) {
        try {
            Long userId = UserUtils.getCurrentUserId();

            if (null == userId) {
                throw ServiceException.exception(CodeConstants.C_10111000);
            }
            // 手机号特殊处理
            if (StringUtils.isNotEmpty(listRequest.getMobile())) {
                listRequest.setMobile(listRequest.getMobile() + ".yx");
            }
            Integer pageNum = super.getPageNum(listRequest.getPageNum());
            Integer pageSize = super.getPageSize(listRequest.getPageSize());
            // 分页查询
            PageVo<ApplicationListVo> applList = applicationWebService.listAppl(userId, listRequest, pageNum, pageSize);
            return JsonResultVo.success().setData(applList);
        } catch (ServiceException e) {
            log.error("查询进件列表失败:{}", e.getMessage(), e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询进件列表失败", e);
            return JsonResultVo.error();
        }
    }

    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public JsonResultVo applicationDetail(@RequestParam(required = true, name = "applicationId") Long applicationId) {
        try {
            ApplicationDetailVo applInfo = applicationWebService.getApplicationDetail(applicationId);
            return JsonResultVo.success().setData(applInfo);
        } catch (ServiceException e) {
            log.error("查询进件{}详情失败:{}", applicationId, e.getMessage(), e);
            return JsonResultVo.error(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询进件{}详情失败", applicationId, e);
            return JsonResultVo.error();
        }
    }

    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    public JsonResultVo auditApplication(@RequestParam(required = true, name = "applicationId") Long applicationId,
            @RequestParam(required = true, name = "auditStatus") String auditStatus,
            @RequestParam(required = true, name = "auditorEmail") String auditorEmail,
            @RequestParam(required = true, name = "remark") String remark) {
        try {
            log.info("进件信息人工审核：applicationId={},auditStatus={},auditorEmail={},remark={}", applicationId, auditStatus,
                    auditorEmail, remark);
            //验证当前操作用户和session用户是否一致
            SysUser user = UserUtils.getCurrentUser();
            if (!auditorEmail.equals(user.getEmail())) {
                throw ExceptionUtils.commonError("登录用户和操作用户不一致");
            }
            applicationService.applicationAuditManual(applicationId, auditStatus, auditorEmail, remark);
            return JsonResultVo.success();
        } catch (ServiceException se) {
            return JsonResultVo.error(se.getCode(), se.getMessage());
        } catch (Exception e) {
            return JsonResultVo.error();
        }
    }

    @RequestMapping(value = "/creditDetail", method = RequestMethod.POST)
    public JsonResultVo creditDetail(@RequestParam(required = true, name = "applicationId") Long applicationId) {
        try {
            CreditDetailVo creditDetailVo = creditReportService.creditDetail(applicationId);
            return JsonResultVo.success().setData(creditDetailVo);
        } catch (ServiceException se) {
            return JsonResultVo.error(se.getCode(), se.getMessage());
        } catch (Exception e) {
            return JsonResultVo.error();
        }
    }

    @RequestMapping(value = "/log", method = RequestMethod.POST)
    public JsonResultVo log(@RequestParam(required = true, name = "applicationId") Long applicationId) {
        try {
            List<ApplicationAuditLogListVo> applicationAuditLogs = applicationAuditLogService
                    .getByApplicationNo(applicationId);
            return JsonResultVo.success().setData(applicationAuditLogs);
        } catch (ServiceException se) {
            return JsonResultVo.error(se.getCode(), se.getMessage());
        } catch (Exception e) {
            return JsonResultVo.error();
        }
    }

    @RequestMapping(value = "/distribute", method = RequestMethod.POST)
    public JsonResultVo distribute(@RequestParam(required = true, name = "applicationIds") String applicationIds,
            @RequestParam(required = true, name = "auditorId") Long auditorId) {
        try {
            Assert.notNull(applicationIds, "审核系统进件号");
            Assert.notNull(auditorId, "分配人员");
            String[] applicationId = applicationIds.split(",");
            List<String> failList = new ArrayList<>();
            for (String applicationNo : applicationId) {
                Boolean result = applicationAuditService.isDistribute(applicationNo, auditorId);
                if (!result) {
                    failList.add(applicationNo);
                }
            }
            if (CollectionUtils.isNotEmpty(failList)) {
                throw ExceptionUtils.commonError("进件号 : " + JSONObject.toJSONString(failList) + " 分配失败");
            }
            return JsonResultVo.success();
        } catch (ServiceException se) {
            return JsonResultVo.error(se.getCode(), se.getMessage());
        } catch (Exception e) {
            return JsonResultVo.error();
        }
    }
}
