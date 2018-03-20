package com.ln.xproject.api.service.impl;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ln.xproject.api.service.SignService;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.server.ServerConfigHolder;
import com.ln.xproject.base.vo.ApiBaseRequestVo;
import com.ln.xproject.util.MD5Utils;

@Service
public class SignServiceImpl implements SignService {

    private static final Logger logger = LoggerFactory.getLogger(SignServiceImpl.class);
    private static final String DATE_PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss";// 日期格式
    private static final String SIGN_EQUAL = "=";// 符号=
    private static final String SIGN_AND = "&";// 符号&

    @Override
    public <T extends ApiBaseRequestVo> boolean verify(T object) {
        Assert.notNull(object, "签名对象");

        // 传入的签名
        String sign = object.getSign();
        // 数据来源渠道
        String partner = object.getPartner();

        Assert.notBlank(partner, "数据来源渠道");
        Assert.notBlank(sign, "传入的签名");

        try {
            // 计算的签名
            String calSign = this.sign(object, partner);

            if (calSign.equals(sign)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            throw ExceptionUtils.commonError("校验签名异常");
        }
    }

    @Override
    public <T extends ApiBaseRequestVo> String sign(T object, String partner) {
        Assert.notNull(object, "签名对象");
        Assert.notBlank(partner, "数据来源渠道");

        try {
            // 签名盐
            String signSalt = ServerConfigHolder.getServerSignSaltMap().get(partner);
            // 拼装签名串
            String signStr = getToSignString(object) + signSalt;
            logger.info("签名串：" + signStr);

            String sign = MD5Utils.md5Str(signStr).toLowerCase();
            logger.info("签名后字段：" + sign);
            return sign;
        } catch (Exception e) {
            throw ExceptionUtils.commonError("计算签名异常");
        }
    }

    private String getToSignString(Object value) throws Exception {
        String str = genQueryForOneField("", value);
        if (str.substring(str.length() - 1).equals(SIGN_AND)) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }

    private String genQueryForOneField(String keyName, Object value) throws Exception {
        // 不参与签名的字段
        Map<String, String> excludes = new HashMap<String, String>();
        excludes.put("sign", null);// 数字签名字段
        excludes.put("serialVersionUID", null);// 序列化字段

        // 排除的字段不参与
        if (excludes.containsKey(keyName)) {
            return "";
        }
        // 为Null的不参与
        if (null == value) {
            return "";
        }
        // 字段类型名称
        String fieldTypeName = value.getClass().getName();

        // 如果是java内建类型
        if (isBuildInClass(fieldTypeName)) {
            String valueStr = getStringValue(value);
            return getQueryString(keyName, valueStr);
        }

        // 如果是对象引用
        Field[] _fields = this.getAllFields(value.getClass());
        // 排序
        List<Field> fields = sortByAscii(_fields);
        StringBuilder strBuffer = new StringBuilder();

        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object fieldValue = field.get(value);

            if (fieldValue != null) {
                Class<?> fieldType = field.getType();
                // 如果是集合
                if (Collection.class.isAssignableFrom(fieldType)) {
                    strBuffer.append(genCollectionQuery(fieldName, fieldValue));
                } else {
                    strBuffer.append(genQueryForOneField(fieldName, fieldValue));
                }
            }
        }
        return strBuffer.toString();
    }

    private Field[] getAllFields(Class<? extends Object> clazz) {
        List<Field> fieldList = new ArrayList<Field>();
        // 变量
        Field[] fields = clazz.getDeclaredFields();

        if (null != fields && fields.length > 0) {
            fieldList.addAll(Arrays.asList(fields));
        }
        // 递归获取父类变量
        Class<? extends Object> superClass = clazz.getSuperclass();

        if (null != superClass) {
            Field[] inheritedFields = getAllFields(superClass);

            if (null != inheritedFields && inheritedFields.length > 0) {
                fieldList.addAll(Arrays.asList(inheritedFields));
            }
        }
        return fieldList.toArray(new Field[] {});
    }

    private static String getQueryString(String key, String name) {
        return key + SIGN_EQUAL + name + SIGN_AND;
    }

    private static String getStringValue(Object value) {
        // 日期类型
        if (value instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN_DEFAULT);
            return sdf.format(value);
        }
        return value.toString();
    }

    /*
     * 验证字段类型是否为java内建的类型
     */
    private static boolean isBuildInClass(String name) {
        if (name.startsWith("java")) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 字段排序
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Field> sortByAscii(Field[] fields) {
        List<Field> _fields = Arrays.asList(fields);
        Collections.sort(_fields, new Comparator() {

            public int compare(Object f1, Object f2) {
                Field one = (Field) f1;
                Field two = (Field) f2;
                return one.getName().compareTo(two.getName());
            }
        });
        return _fields;
    }

    /*
     * 生成集合数据的QueryString
     */
    private String genCollectionQuery(String name, Object value) throws Exception {
        StringBuilder strBuffer = new StringBuilder();
        Collection<?> collection = (Collection<?>) value;
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            Object eleValue = it.next();
            strBuffer.append(genQueryForOneField(name, eleValue));
        }
        return strBuffer.toString();
    }
}
