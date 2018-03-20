package com.ln.xproject.util;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.ln.xproject.base.constant.ResponseStatus;
import com.ln.xproject.base.vo.JsonResultVo;

/**
 * HttpServletResponse工具类
 */
public class ResponseUtils {

    private static Logger logger = LoggerFactory.getLogger(ResponseUtils.class);
    public static final String CORRECT_STATUS = "000000";

    /**
     * 响应Json
     * 
     * @param response
     * @param obj
     */
    public static void printJson(HttpServletResponse response, Object obj) {
        String json = JsonUtils.toJson(obj);

        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            PrintWriter out = response.getWriter();
            out.println(json);
            out.flush();
        } catch (Exception e) {
            logger.error("响应Json失败。", e);
        }
    }

    public static JsonResultVo genrateJsonResultVo(JSONObject resultJsonObj) {

        try {
            String status = resultJsonObj.getString("status");
            String message = resultJsonObj.getString("message");
            if (CORRECT_STATUS.equals(status)) {

                JsonResultVo result = new JsonResultVo();
                result.setMessage(message);
                result.setStatus("0");
                if (!Strings.isNullOrEmpty(resultJsonObj.getString("data"))) {
                    Map data = resultJsonObj.getObject("data", Map.class);
                    result.setData(data);
                }
                return result;
            } else {
                logger.error("调用异常 status{} message{}", status, message);
                return JsonResultVo.error(status, message);
            }
        } catch (Exception e) {
            return JsonResultVo.error();
        }

    }

    public static String getResponseStr(ResponseStatus status, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status.getStatus());
        map.put("message", status.getMessage());
        map.put("data", data == null ? new EmptyObject() : data);
        return JsonUtils.toJson(map);
    }

}

class EmptyObject {
}
