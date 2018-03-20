package com.ln.xproject.base.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ln.xproject.base.code.Code;

/**
 * Json结果
 * 
 * @author ning
 */
public class JsonResultVo implements Serializable {

    private static final long serialVersionUID = -3682737772692845415L;

    /** 成功状态码 */
    public static final String SUCCESS = "0";
    /** 失败状态码 */
    public static final String ERROR = "-1";

    /** 状态码 */
    protected String status = SUCCESS;
    /** 消息 */
    protected String message;
    /** 数据集 */
    protected Object data = null;

    /**
     * 通过vo 构建
     *
     * @param codeVo
     * @return
     */
    public static JsonResultVo error(Code codeVo) {
        if (null == codeVo) {
            return JsonResultVo.error(ERROR, "此返回信息未配置编码,请配置");
        }
        return JsonResultVo.error(codeVo.getCode(), codeVo.getMessage());
    }

    public static JsonResultVo error(Code codeVo, String... paras) {
        if (null == codeVo) {
            return JsonResultVo.error(ERROR, "此返回信息未配置编码,请配置");
        }
        return JsonResultVo.error(codeVo.getCode(), String.format(codeVo.getMessage(), paras));
    }

    /**
     * 构建一个成功结果的对象
     * 
     * @param message
     * @return
     */
    public static JsonResultVo success(String message) {
        JsonResultVo vo = new JsonResultVo();
        vo.setStatus(SUCCESS);
        vo.setMessage(message);
        return vo;
    }

    /**
     * 构建一个成功结果的对象
     * 
     * @return
     */
    public static JsonResultVo success() {
        return success("");
    }

    /**
     * 默认的错误异常
     * 
     * @return
     */
    public static JsonResultVo error() {
        JsonResultVo vo = new JsonResultVo();
        vo.setStatus(ERROR);
        vo.setMessage("服务器异常");
        return vo;
    }

    /**
     * 构建一个失败结果的对象
     * 
     * @param status
     * @param message
     * @return
     */
    public static JsonResultVo error(String status, String message) {
        JsonResultVo vo = new JsonResultVo();
        vo.setStatus(status);
        vo.setMessage(message);
        return vo;
    }

    public static JsonResultVo addRows(List<?> list) {
        return JsonResultVo.success().addData("rows", list);
    }

    /**
     * 增加一个值
     * 
     * @param key
     * @param value
     * @return
     */
    @SuppressWarnings("unchecked")
    public JsonResultVo addData(String key, Object value) {
        if (data == null) {
            data = new HashMap<>();
        }
        ((Map<String, Object>) data).put(key, value);
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public JsonResultVo setData(Object data) {
        this.data = data;
        return this;
    }

}