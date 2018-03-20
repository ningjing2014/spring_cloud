package com.ln.xproject.web.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.ln.xproject.api.vo.ApplicationListRequestVo;
import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationChannel;
import com.ln.xproject.application.constants.VerifyBusinessType;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.model.ApplicationAudit;
import com.ln.xproject.application.model.ApplicationUser;
import com.ln.xproject.application.model.QApplication;
import com.ln.xproject.application.model.QApplicationUser;
import com.ln.xproject.application.service.ApplicationAuditService;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.application.service.ApplicationUserService;
import com.ln.xproject.application.vo.ApplicationDetailVo;
import com.ln.xproject.application.vo.ApplicationListVo;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.config.CostomPageRequest;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.vo.PageVo;
import com.ln.xproject.system.model.SysRole;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.system.service.SysRoleService;
import com.ln.xproject.system.service.SysUserService;
import com.ln.xproject.util.CollectionUtils;
import com.ln.xproject.util.DateUtils;
import com.ln.xproject.util.StringUtils;
import com.ln.xproject.web.service.ApplicationWebService;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ApplicationWebServiceImpl implements ApplicationWebService {

    @PersistenceContext
    protected EntityManager em;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private ApplicationAuditService applicationAuditService;

    @Override
    public PageVo<ApplicationListVo> listAppl(Long userId, ApplicationListRequestVo listRequest, Integer pageNum,
            Integer pageSize) {
        // 校验
        this.checkList(listRequest);

        // 动态查询
        JPAQueryFactory query = new JPAQueryFactory(em);
        QApplication qApplication = QApplication.application;
        QApplicationUser qApplicationUser = QApplicationUser.applicationUser;
        CostomPageRequest pageRequest = new CostomPageRequest(pageNum, pageSize);

        BooleanExpression exp = qApplication.id.eq(qApplicationUser.applicationId)
                .and(qApplication.isInited.eq(Boolean.TRUE));
        Set<SysRole> roleSet = sysRoleService.getUserRolelist(userId);

        roleSet = roleSet.stream().filter(role -> role.getName().equals("审核员")).collect(Collectors.toSet());

        // 审核员只能查看分配到自身的进件 - 查询该审核员的进件信息Id
        if (CollectionUtils.isNotEmpty(roleSet)) {
            List<ApplicationAudit> audits = applicationAuditService.loadByOperatorUserId(userId);
            List<Long> applicationId = audits.stream().map(audit -> audit.getApplicationId())
                    .collect(Collectors.toList());
            // 该审核员无审核信息-->返回结果
            if (CollectionUtils.isEmpty(applicationId)) {
                return new PageVo<ApplicationListVo>(Lists.newArrayList(), 0);
            }
            if (CollectionUtils.isNotEmpty(applicationId)) {
                exp = exp.and(qApplication.id.in(applicationId));
            }
        }
        // 按渠道
        if (StringUtils.isNotBlank(listRequest.getApplicationChannel())) {
            ApplicationChannel channel = ApplicationChannel.valueOf(listRequest.getApplicationChannel());
            exp = exp.and(qApplication.applicationChannel.eq(channel));
        }

        // 按审核状态-支持多状态筛选
        List<ApplicationAuditStatus> auditStatusList = null;
        VerifyBusinessType businessType = VerifyBusinessType.valueOf(listRequest.getBusinessType());

        if (StringUtils.isNotBlank(listRequest.getAuditStatus())) {
            List<String> status = StringUtils.splitToList(listRequest.getAuditStatus(), ",");
            auditStatusList = status.stream().map(s -> ApplicationAuditStatus.valueOf(s)).collect(Collectors.toList());
        } else {
            if (businessType != VerifyBusinessType.LIST) {
                auditStatusList = businessType.ownedAuditStatus();
            }
        }
        if (CollectionUtils.isNotEmpty(auditStatusList)) {
            exp = exp.and(qApplication.auditStatus.in(auditStatusList));
        }

        // 按用户名称
        if (StringUtils.isNotBlank(listRequest.getUserName())) {
            exp = exp.and(qApplicationUser.realName.eq(listRequest.getUserName()));
        }
        // 按用户身份证号
        if (StringUtils.isNotBlank(listRequest.getIdCard())) {
            exp = exp.and(qApplicationUser.idCard.eq(listRequest.getIdCard()));
        }
        // 按用户手机号
        if (StringUtils.isNotBlank(listRequest.getMobile())) {
            exp = exp.and(qApplicationUser.mobile.eq(listRequest.getMobile()));
        }
        // 按渠道方进件号
        if (StringUtils.isNotBlank(listRequest.getChannelLoanId())) {
            exp = exp.and(qApplication.channelLoanId.eq(listRequest.getChannelLoanId()));
        }
        BigDecimal minAmount = listRequest.getMinAmount();
        BigDecimal maxAmount = listRequest.getMaxAmount();

        // 按进件金额
        if (null != maxAmount) {
            exp = exp.and(qApplication.amount.loe(maxAmount));
        }
        if (null != minAmount) {
            exp = exp.and(qApplication.amount.gt(minAmount));
        }
        // 按进件时间
        if (null != listRequest.getStartImportTime() && null != listRequest.getEndImportTime()) {
            Date from = new Date(listRequest.getStartImportTime());
            Date to = new Date(listRequest.getEndImportTime());
            exp = exp.and(qApplication.createTime.between(from, to));
        }
        // 查询
        QueryResults<Tuple> pageList = query.select(qApplication, qApplicationUser).from(qApplication, qApplicationUser)
                .where(exp).offset(pageRequest.getPageNumber() * pageRequest.getPageSize())
                .limit(pageRequest.getPageSize()).fetchResults();

        if (null == pageList || CollectionUtils.isEmpty(pageList.getResults())) {
            return new PageVo<ApplicationListVo>(Collections.emptyList(), 0L);
        }
        Long total = pageList.getTotal();
        List<Tuple> resultList = pageList.getResults();
        // 数据拼装
        List<ApplicationListVo> voList = new ArrayList<ApplicationListVo>();

        resultList.forEach(tuple -> {
            Application appl = tuple.get(qApplication);
            ApplicationUser applUser = tuple.get(qApplicationUser);
            ApplicationListVo vo = new ApplicationListVo();

            vo.setChannelLoanId(appl.getChannelLoanId());
            vo.setAmount(appl.getAmount().toString());
            vo.setApplicationId(appl.getId().toString());
            vo.setAuditStatus(appl.getAuditStatus().name());
            vo.setAuditStatusDesc(appl.getAuditStatus().toString());
            vo.setPushStatus(appl.getPushStatus().name());
            vo.setPushStatusDesc(appl.getPushStatus().toString());
            vo.setImportTime(String.valueOf(appl.getCreateTime().getTime()));
            vo.setIdCard(applUser.getIdCard());
            vo.setMobile(StringUtils.isNotEmpty(applUser.getMobile()) ? applUser.getMobile().replace(".yx", "") : "");
            vo.setUserName(applUser.getRealName());
            ApplicationAudit applicationAudit = applicationAuditService
                    .loadByApplicationIdWithoutException(appl.getId());
            if (applicationAudit != null && applicationAudit.getOperatorUserId() != null) {
                SysUser user = sysUserService.load(applicationAudit.getOperatorUserId());
                vo.setOperator(user.getRealName());
            }
            voList.add(vo);
        });

        return new PageVo<ApplicationListVo>(voList, total);
    }

    /**
     * 校验列表请求参数
     * 
     * @param listRequest
     */
    private void checkList(ApplicationListRequestVo listRequest) {
        Assert.notNull(listRequest, "请求信息");
        VerifyBusinessType type = Assert.enumNotValid(VerifyBusinessType.class, listRequest.getBusinessType(), "业务类型");

        if (StringUtils.isNoneBlank(listRequest.getApplicationChannel())) {
            Assert.enumNotValid(ApplicationChannel.class, listRequest.getApplicationChannel(), "进件渠道");
        }

        if (StringUtils.isNoneBlank(listRequest.getAuditStatus())) {
            List<String> statusList = StringUtils.splitToList(listRequest.getAuditStatus(), ",");
            for (String status : statusList) {
                ApplicationAuditStatus enumStatus = Assert.enumNotValid(ApplicationAuditStatus.class, status, "审核状态");

                if (!type.isOwnedStatus(enumStatus)) {
                    throw ServiceException.exception(CodeConstants.C_10121005);
                }
            }
        }
        BigDecimal minAmount = listRequest.getMinAmount();
        BigDecimal maxAmount = listRequest.getMaxAmount();
        Long startImportTime = listRequest.getStartImportTime();
        Long endImportTime = listRequest.getEndImportTime();

        if (null != minAmount) {
            Assert.geZero(minAmount, "最小申请额度");
        }
        if (null != maxAmount) {
            Assert.gtZero(maxAmount, "最大申请额度");
        }
        if (null != minAmount && null != maxAmount && minAmount.compareTo(maxAmount) >= 0) {
            throw ExceptionUtils.commonError("查询最小申请额度必须小于最大申请额度");
        }
        if (null != startImportTime || null != endImportTime) {
            Assert.notNull(startImportTime, "查询开始进件时间");
            Assert.notNull(endImportTime, "查询结束进件时间");

            if (new Date(startImportTime).after(new Date(endImportTime))) {
                throw ExceptionUtils.commonError("查询开始时间必须在结束时间之前");
            }
        }
    }

    /**
     * 查询进件详情
     * 
     * @param applicationId
     *            审核系统进件id
     * @return
     */
    @Override
    public ApplicationDetailVo getApplicationDetail(Long applicationId) throws ServiceException {
        Assert.notNull(applicationId, "进件id");

        Application application = applicationService.load(applicationId);

        ApplicationUser applicationUser = applicationUserService.loadByApplicationId(applicationId);

        return buildApplicationDetailVo(application, applicationUser);
    }

    private ApplicationDetailVo buildApplicationDetailVo(Application application, ApplicationUser applicationUser) {
        ApplicationDetailVo vo = new ApplicationDetailVo();
        // 进件信息
        vo.setApplicationId(application.getId());
        vo.setChannelLoanId(application.getChannelLoanId());
        vo.setAreaGroupId(application.getAreaGroupId());
        vo.setGroupId(application.getGroupId());
        vo.setShop(application.getShop());
        vo.setUdpateTime(
                application.getUpdateTime() != null ? DateUtils.format(application.getUpdateTime(), DateUtils.DATE_TIME)
                        : "");
        vo.setDescription(application.getDescription());
        vo.setCreateTime(
                application.getCreateTime() != null ? DateUtils.format(application.getCreateTime(), DateUtils.DATE_TIME)
                        : "");
        vo.setDistrictGroupId(application.getDistrictGroupId());
        vo.setBorrowType(application.getBorrowType());
        vo.setAmount(application.getAmount().toString());
        vo.setBorrowProjectName(application.getBorrowType() + applicationUser.getNickName());
        vo.setRepayType("等额本息");
        vo.setInterest(application.getInterest().toString());
        vo.setRepayPeriod(application.getPeriods().toString());
        // 用户进件信息
        vo.setCarFullAmount(applicationUser.getCarFullAmount());
        vo.setCarInfo(applicationUser.getCarInfo());
        vo.setCarMonthlyPayment(applicationUser.getCarMonthlyPayment());
        vo.setCarPeriod(applicationUser.getCarPeriod());
        vo.setCertificatesType(applicationUser.getCertificatesType());
        vo.setCompanyMonthlyWage(applicationUser.getCompanyMonthlyWage());
        try {
            vo.setContactInfo(StringUtils.isNotEmpty(applicationUser.getContactInfo())
                    ? JSONArray.parseArray(applicationUser.getContactInfo(), Map.class)
                    : Lists.newArrayList());
        } catch (Exception e) {
            log.error("联系人信息解析失败applicationId:{}", application.getId());
        }

        vo.setDegreeNo(applicationUser.getDegreeNo());
        vo.setGender(applicationUser.getGender());
        vo.setGraduatedYear(applicationUser.getGraduatedYear());
        vo.setGraduation(applicationUser.getGraduation());
        vo.setHasHouse(applicationUser.getHasHouse());
        vo.setHouseAddress(applicationUser.getHouseAddress());
        vo.setHouseCity(applicationUser.getHouseCity());
        vo.setHouseDistrict(applicationUser.getHouseDistrict());
        vo.setHouseMonthlyPayment(applicationUser.getHouseMonthlyPayment());
        vo.setHouseProvince(applicationUser.getHouseProvince());
        vo.setHouseType(applicationUser.getHouseType());
        vo.setHouseTypeDesc(applicationUser.getHouseTypeDesc());
        vo.setIdCard(applicationUser.getIdCard());
        vo.setMarriageStatus(applicationUser.getMarriageStatus());
        vo.setMobile(
                StringUtils.isNotEmpty(applicationUser.getMobile()) ? applicationUser.getMobile().replace(".yx", "")
                        : "");
        vo.setMonthlyRentalFee(applicationUser.getMonthlyRentalFee());
        vo.setOtherIncome(applicationUser.getOtherIncome());
        vo.setRealName(applicationUser.getRealName());
        vo.setWorkSalaryDay(applicationUser.getWorkSalaryDay());
        vo.setManageFinanceInfo(applicationUser.getManageFinanceInfo());
        vo.setRepayAbilityInfo(applicationUser.getRepayAbilityInfo());
        vo.setComplaintPunishInfo(applicationUser.getComplaintPunishInfo());
        vo.setRepaySource(applicationUser.getRepaySource());
        vo.setCompanyIndustry(applicationUser.getCompanyIndustry());

        return vo;
    }

}
