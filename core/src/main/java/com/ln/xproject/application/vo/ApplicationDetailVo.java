package com.ln.xproject.application.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import lombok.Data;

/** *
 * @author yushijun
 * @date 2018/1/12
 * 进件详情接口
  */
@Data
public class ApplicationDetailVo implements Serializable {
    /** 审核系统进件id */
    private Long applicationId;
    /** 性别 */
    private String gender;
    /** 毕业年份 */
    private String graduatedYear;
    /** 最高学历 */
    private String graduation;
    /** 身份证号 */
    private String idCard;
    /** 婚姻状况 */
    private String marriageStatus;
    /** 手机号 */
    private String mobile;
    /** 是否有房产 */
    private String hasHouse;
    /** 姓名 */
    private String realName;
    /** 证件类型 */
    private String certificatesType;
    /** 学历证书编号 */
    private String degreeNo;
    /** 住房性质/房产类别 */
    private String houseType;
    /** 住房性质其他描述 */
    private String houseTypeDesc;
    /** 月房租 */
    private String monthlyRentalFee;
    /** 房产月供 */
    private String houseMonthlyPayment;
    /** 拥有车辆情况 */
    private String carInfo;
    /** 车辆全款价格 */
    private String carFullAmount;
    /** 车辆按揭月供 */
    private String carMonthlyPayment;
    /** 车龄 */
    private String carPeriod;
    /** 月工资 */
    private String companyMonthlyWage;
    /** 其他收入 */
    private String otherIncome;
    /** 每月发薪日 */
    private String workSalaryDay;
    /** 联系人信息 */
    private List<Map> contactInfo;
    /** 现居住省 */
    private String houseProvince;
    /** 现居住市 */
    private String houseCity;
    /** 现居住区县 */
    private String houseDistrict;
    /** 现居住详细地址 */
    private String houseAddress;
    /** 进件号 */
    private String channelLoanId;
    /** 进件时间 */
    private String createTime;
    /** 最新更新时间 */
    private String udpateTime;
    /** 借款用途 */
    private String borrowType;
    /** 借款用途描述 */
    private String description;
    /** 门店-备注 */
    private String shop;
    /** 销售机构id */
    private String groupId;
    /** 大区id */
    private String districtGroupId;
    /** 申请城市 */
    private String areaGroupId;
    /** 借款人经营状况及财务状况 */
    private String manageFinanceInfo;
    /** 借款人款款能力变化情况 */
    private String repayAbilityInfo;
    /** 借款人涉诉及受行政处罚情况 */
    private String complaintPunishInfo;
    /** 借款金额 */
    private String amount;
    /** 项目名称 */
    private String borrowProjectName;
    /** 还款方式 默认等额本息--*/
    private String repayType;
//    /** 起息日 --*/
//    private String interestStartDate;;
    /** 年化利率 */
    private String interest;
    /** 借款周期 */
    private String repayPeriod ;
    /** 还款来源 */
    private String repaySource;
    /** 借款人所属行业 */
    private String companyIndustry;
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
