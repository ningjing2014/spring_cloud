package com.ln.xproject.application.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ln.xproject.base.model.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the application_user database table.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "application_user")
public class ApplicationUser extends BaseModel {

    private static final long serialVersionUID = 1L;

    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    @Column(name = "user_ref_id", nullable = false)
    private String userRefId;

    @Column(name = "verify_user_key", nullable = false)
    private String verifyUserKey;

    /** 用户来源 */
    @Column(name = "utm_source")
    private String utmSource;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "mobile", nullable = false)
    private String mobile;

    @Column(name = "nick_name", nullable = false)
    private String nickName;

    @Column(name = "real_name", nullable = false)
    private String realName;

    @Column(name = "id_card", nullable = false)
    private String idCard;

    @Column(name = "birth", nullable = false)
    private String birth;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "graduation", nullable = false)
    private String graduation;

    /** 毕业年份 */
    @Column(name = "graduated_year")
    private String graduatedYear;

    @Column(name = "house_province")
    private String houseProvince;

    @Column(name = "house_city")
    private String houseCity;

    @Column(name = "house_district")
    private String houseDistrict;

    @Column(name = "house_address", nullable = false)
    private String houseAddress;

    @Column(name = "entrance_year")
    private String entranceYear;

    @Column(name = "university")
    private String university;

    @Column(name = "marriage_status", nullable = false)
    private String marriageStatus;

    @Column(name = "has_child", nullable = false)
    private String hasChild;

    @Column(name = "has_house", nullable = false)
    private String hasHouse;

    @Column(name = "has_house_loan", nullable = false)
    private String hasHouseLoan;

    @Column(name = "has_car", nullable = false)
    private String hasCar;

    @Column(name = "has_car_loan", nullable = false)
    private String hasCarLoan;

    @Column(name = "home_town_province", nullable = false)
    private String homeTownProvince;

    @Column(name = "home_town_city", nullable = false)
    private String homeTownCity;

    @Column(name = "account_location_province", nullable = false)
    private String accountLocationProvince;

    @Column(name = "account_location_city", nullable = false)
    private String accountLocationCity;

    @Column(name = "residence", nullable = false)
    private String residence;

    @Column(name = "residence_tel")
    private String residenceTel;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(name = "job_status", nullable = false)
    private String jobStatus;

    @Column(name = "company_province", nullable = false)
    private String companyProvince;

    @Column(name = "company_city", nullable = false)
    private String companyCity;

    @Column(name = "company_category", nullable = false)
    private String companyCategory;

    @Column(name = "company_industry", nullable = false)
    private String companyIndustry;

    @Column(name = "company_size", nullable = false)
    private String companySize;

    @Column(name = "company_post", nullable = false)
    private String companyPost;

    @Column(name = "monthly_income", nullable = false)
    private String monthlyIncome;

    @Column(name = "current_unit_life", nullable = false)
    private String currentUnitLife;

    @Column(name = "company_tel", nullable = false)
    private String companyTel;

    @Column(name = "office_email", nullable = false)
    private String officeEmail;

    @Column(name = "company_address", nullable = false)
    private String companyAddress;

    @Column(name = "immediate_name", nullable = false)
    private String immediateName;

    @Column(name = "immediate_relation_ship", nullable = false)
    private String immediateRelationShip;

    @Column(name = "immediate_tel", nullable = false)
    private String immediateTel;

    @Column(name = "other_relation_name", nullable = false)
    private String otherRelationName;

    @Column(name = "other_relation_ship", nullable = false)
    private String otherRelationShip;

    @Column(name = "other_relation_tel", nullable = false)
    private String otherRelationTel;

    @Column(name = "contact_info", nullable = false)
    private String contactInfo;

    @Column(name = "has_oth_debt")
    private String hasOthDebt;

    @Column(name = "repay_source")
    private String repaySource;

    @Column(name = "manage_finance_info")
    private String manageFinanceInfo;

    @Column(name = "repay_ability_info")
    private String repayAbilityInfo;

    @Column(name = "complaint_punish_info")
    private String complaintPunishInfo;

    @Column(name = "certificates_type", nullable = false)
    private String certificatesType;

    @Column(name = "degree_no")
    private String degreeNo;

    @Column(name = "house_type")
    private String houseType;

    @Column(name = "house_type_desc")
    private String houseTypeDesc;

    @Column(name = "monthly_rental_fee")
    private String monthlyRentalFee;

    @Column(name = "house_monthly_payment", nullable = false)
    private String houseMonthlyPayment;

    @Column(name = "car_info", nullable = false)
    private String carInfo;

    @Column(name = "car_full_amount")
    private String carFullAmount;

    @Column(name = "car_monthly_payment")
    private String carMonthlyPayment;

    @Column(name = "car_period")
    private String carPeriod;

    @Column(name = "company_monthly_wage", nullable = false)
    private String companyMonthlyWage;

    @Column(name = "other_income", nullable = false)
    private String otherIncome;

    @Column(name = "work_salary_day", nullable = false)
    private String workSalaryDay;

    @Column(name = "password", nullable = false)
    private String password;

    /** 用户产品集合 */
    @Column(name = "products")
    private String products;

    /** 是否首次申请 */
    @Column(name = "is_first_apply_loan")
    private String isFirstApplyLoan;

    /** 注册时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "register_time")
    private Date registerTime;

    /** 银行卡号 */
    @Column(name = "bank_card_no")
    private String bankCardNo;

    /** 银行代码 */
    @Column(name = "bank_code")
    private String bankCode;

    /** 银行名称 */
    @Column(name = "bank_name")
    private String bankName;

    /** 支行名称 */
    @Column(name = "bank_address")
    private String bankAddress;

    /** 开户省份 */
    @Column(name = "province")
    private String province;

    /** 开户城市 */
    @Column(name = "city")
    private String city;

    /** 意向 */
    @Column(name = "intention")
    private String intention;

    /** 获知人人贷的渠道 */
    @Column(name = "source")
    private String source;

}