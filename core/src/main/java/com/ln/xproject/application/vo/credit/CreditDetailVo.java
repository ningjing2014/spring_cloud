package com.ln.xproject.application.vo.credit;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class CreditDetailVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;
    /* 进件号 */
    private String applicationNo;
    /* 用户基本信息 */
    private CustomerInfoVo customerInfo;
    /* 担保信息 */
    private List<GuaranteeInfoVo> guaranteeInfo;
    /* 负面信息 */
    private List<NegativeInfoVo> negativeInfo;
    /* 信用总体信息 */
    private CreditInfoVo creditInfo;
    /* 历史和逾期信息 */
    private HisOverdueInfoVo hisOverdueInfo;
    /* 负债信息 */
    private LiabilityInfoVo liabilityInfo;
    /* 关切程度信息 */
    private AttentioninfoVo attentioninfo;
}
