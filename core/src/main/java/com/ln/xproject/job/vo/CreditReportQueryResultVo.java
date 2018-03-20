package com.ln.xproject.job.vo;

import com.alibaba.fastjson.JSONObject;

import lombok.Data;

@Data
public class CreditReportQueryResultVo {

    private QueryResult result;

    private JSONObject resultJson;

    public static enum QueryResult {
        SUCCESS, RETRY, PASS;
    }
}
