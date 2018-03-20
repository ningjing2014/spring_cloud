package com.ln.xproject.api.vo;

import java.io.Serializable;

import com.ln.xproject.util.JsonUtils;
import lombok.Data;

/**
 * 推标响应Vo
 * 
 * @author taixin
 */

@Data
public class ApplicationResultResponseVo implements Serializable {

    private Head head;
    private String lendId;
    private String verifyCode;
    private String verifyMsg;
    private String pushCode;
    private String pushMsg;
    private String weCode;
    private String weMsg;

    @Data
    public static class Head {
        String reqBizCode;
        String reqSeqNo;
    }

    public static void main(String[] args) {
        ApplicationResultResponseVo vo = new ApplicationResultResponseVo();
        ApplicationResultResponseVo.Head head = new ApplicationResultResponseVo.Head();
        head.setReqBizCode("reqBisCode");
        head.setReqSeqNo("seqNo");
        vo.setHead(head);
        vo.setLendId("lendID");
        vo.setHead(head);
        System.out.println(JsonUtils.toJson(vo));
    }

}
