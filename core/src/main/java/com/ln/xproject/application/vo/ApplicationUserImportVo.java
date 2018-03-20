package com.ln.xproject.application.vo;

import java.io.Serializable;
import java.util.List;

import com.ln.xproject.base.exception.Assert;

import lombok.Data;

@Data
public class ApplicationUserImportVo implements Serializable {

    private static final long serialVersionUID = -6589650899610998320L;

    private String utmSource;

    private String userRefId;

    private String userEmail;

    private String mobile;

    private String nickName;

    private String realName;

    private String idCard;

    private String birth;

    private String gender;

    private String graduation;

    /** 入学年份 */
    private String graduatedYear;

    private String houseProvince;

    private String houseCity;

    private String houseDistrict;

    private String houseAddress;

    private String entranceYear;

    private String university;

    private String marriageStatus;

    private String hasChild;

    private String hasHouse;

    private String hasHouseLoan;

    private String hasCar;

    private String hasCarLoan;

    private String homeTownCity;

    private String homeTownProvince;

    private String accountLocationCity;

    private String accountLocationProvince;

    private String residence;

    private String residenceTel;

    private String companyName;

    private String jobStatus;

    private String companyProvince;

    private String companyCity;

    private String companyCategory;

    private String companyIndustry;

    private String companySize;

    private String companyPost;

    private String monthlyIncome;

    private String currentUnitLife;

    private String companyTel;

    private String officeEmail;

    private String companyAddress;

    private String immediateName;

    private String immediateRelationShip;

    private String immediateTel;

    private String otherRelationName;

    private String otherRelationShip;

    private String otherRelationTel;

    private String hasOthDebt;

    private String repaySource;

    private String manageFinanceInfo;

    private String repayAbilityInfo;

    private String complaintPunishInfo;

    private String certificatesType;

    private String degreeNo;

    private String houseType;

    private String houseTypeDesc;

    private String monthlyRentalFee;

    private String houseMonthlyPayment;

    private String carInfo;

    private String carFullAmount;

    private String carMonthlyPayment;

    private String carPeriod;

    private String companyMonthlyWage;

    private String otherIncome;

    private String workSalaryDay;

    private List<ContactVo> contactInfo;

    private String password;

    /** 用户产品集合 */
    private String products;

    /** 是否首次申请 */
    private Boolean isFirstApplyLoan;

    /** 注册时间 */
    private Long registerTime;

    /** 银行卡号 */
    private String bankCardNo;

    /** 银行代码 */
    private String bankCode;

    /** 银行名称 */
    private String bankName;

    /** 支行名称 */
    private String bankAddress;

    /** 开户省份 */
    private String province;

    /** 开户城市 */
    private String city;

    /** 意向 */
    private String intention;

    /** 或者人人贷的渠道 */
    private String source;

    public void checkImport() {
        Assert.notBlank(userRefId, "userRefId");
        Assert.notBlank(userEmail, "userEmail");
        Assert.notBlank(mobile, "mobile");
        Assert.notBlank(nickName, "nickName");
        Assert.notBlank(realName, "realName");
        Assert.notBlank(idCard, "idCard");
        Assert.notBlank(birth, "birth");
        Assert.notBlank(gender, "gender");
        Assert.notBlank(graduation, "graduation");
        Assert.notBlank(houseAddress, "houseAddress");
        Assert.notBlank(marriageStatus, "marriageStatus");
        Assert.notBlank(hasChild, "hasChild");
        Assert.notBlank(hasHouse, "hasHouse");
        Assert.notBlank(hasHouseLoan, "hasHouseLoan");
        Assert.notBlank(houseMonthlyPayment, "houseMonthlyPayment");
        Assert.notBlank(hasCar, "hasCar");
        Assert.notBlank(hasCarLoan, "hasCarLoan");
        Assert.notBlank(homeTownProvince, "homeTownProvince");
        Assert.notBlank(homeTownCity, "homeTownCity");
        Assert.notBlank(accountLocationCity, "accountLocationCity");
        Assert.notBlank(accountLocationProvince, "accountLocationProvince");
        Assert.notBlank(residence, "residence");
        Assert.notBlank(companyName, "companyName");
        Assert.notBlank(jobStatus, "jobStatus");
        Assert.notBlank(companyProvince, "companyProvince");
        Assert.notBlank(companyCity, "companyCity");
        Assert.notBlank(companyCategory, "companyCategory");
        Assert.notBlank(companyIndustry, "companyIndustry");
        Assert.notBlank(companySize, "companySize");
        Assert.notBlank(companyPost, "companyPost");
        Assert.notBlank(monthlyIncome, "monthlyIncome");
        Assert.notBlank(currentUnitLife, "currentUnitLife");
        Assert.notBlank(companyTel, "companyTel");
        Assert.notBlank(officeEmail, "officeEmail");
        Assert.notBlank(companyAddress, "companyAddress");
        Assert.notBlank(immediateName, "immediateName");
        Assert.notBlank(immediateRelationShip, "immediateRelationShip");
        Assert.notBlank(immediateTel, "immediateTel");
        Assert.notBlank(otherRelationName, "otherRelationName");
        Assert.notBlank(otherRelationShip, "otherRelationShip");
        Assert.notBlank(otherRelationTel, "otherRelationTel");
        Assert.notNull(contactInfo, "contactInfo");
        Assert.notBlank(certificatesType, "certificatesType");
        Assert.notBlank(carInfo, "carInfo");
        Assert.notBlank(companyMonthlyWage, "companyMonthlyWage");
        Assert.notBlank(otherIncome, "otherIncome");
        Assert.notBlank(workSalaryDay, "workSalaryDay");
        Assert.notBlank(password, "password");
    }
}
