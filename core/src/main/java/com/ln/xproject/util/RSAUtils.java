package com.ln.xproject.util;

import java.io.ByteArrayOutputStream;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author sw
 */
public class RSAUtils {

    private final static Log LOG = LogFactory.getLog(RSAUtils.class);
    //private static String DEFAULT_RSA_PUBLICKEY = property.getProperty("rsa.public.key");
    private static String DEFAULT_RSA_PUBLICKEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCfUED/ofIQUnZ/fjYAY7N0tBtcCB97WpEx7JT8WDrOIG/IXwxbgAq83GHH5RTl9sBrsRUgGF9w8b4FBQ63wJNNma80LO2xL6Rj3eUJMLq0sMx80f+kmCtlqxxr9iDhzAEcjz4W6ioaiJSfT1YCdZ+J0pA+XrSsvx5r1mVBuI45cwIDAQAB";
    private static String DEFAULT_RSA_PRIVATEKEY = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAJ9QQP+h8hBSdn9+NgBjs3S0G1wIH3takTHslPxYOs4gb8hfDFuACrzcYcflFOX2wGuxFSAYX3DxvgUFDrfAk02ZrzQs7bEvpGPd5QkwurSwzHzR/6SYK2WrHGv2IOHMARyPPhbqKhqIlJ9PVgJ1n4nSkD5etKy/HmvWZUG4jjlzAgMBAAECgYEAiGDFI5BYjYxqED3UsISozNCZUzFI8enXvWsPJu3IeUffKSHkjDeR+ZaWxe3e8/VFR2sx8GWs3hyzU8y+iqJ59WjMHmzqMiHDUEfAOxxhzpmYCe+exjgPAO+ne5mLLSDKmipW9axbXVRSmG32k+bbHKqwX2zXgt3r0UQH4XNmDUECQQDor13FH3VJkWk3cFpI38cZbzmxi0mbsHYqHeuQZ2qKDNKUEnomOI5gRLkyVH8TaZFfUgzLJgbhufuyGdwDErwjAkEAr0bTLeDpHWZf2vTpeZ1+s30KumuDSK2+8wZHz13A1V8dx0qtkeZQhNo7HMzwBmDGVG2TmIIsdgsyQem9ZaX6cQJBALGrxlQQk0RbjhXQ6n+er66SKWGSU+BXu8KpGggnj0heRiTefvrUUJLdpvPxZR7zixdX1YEBRlvx4Wi7Ki4GjjUCQBqjQOTwEX2OWXMFVZZOATt1/XNnc4RQI3z7r14seWn0Eqp/BvHtdr86C1HNM41El9+s54l9/xPRj8nEpCwWlYECQQDNe05pje0RPnbs1LM//qw/qSkArfJQwu+5OxaM0VFNZerS6NQM7ENduduI85Hn9VB5PLrO2Korvd0Z3aa0zBUo";
    //private static String DEFAULT_RSA_PRIVATEKEY = "MIICXAIBAAKBgQDkLnreGBp/8iqykpVzizOq8ARM06NtbZuYMe3Oo6UgF79V/lhQ4eAyqWBCAVf24A3hjU2lVlahsYrMCNO66hYirsHdAssaK1W9VXe3/JewxQ5XY7mveFf96qf1HJqzySKsOXk128cxghXnX0nThK0W7pda8jVt+68PqC7zwdgKSQIDAQABAoGAaobYjb8q//iKvnt4kky2fmM3XrafYYL+VPtaTlJP0L/Fb9wiVwBcrsosSiRecL8BO/+/9CRupWZBqiek0Wpfkke94jWywIU2pbMH1SrQJTdSMM9DIM7PcYLnJccGRWmA9BxQQkilHmH3OumuBXodkwLbr0q6nEAQ1L4yW0Ff3gECQQD6LN3FgrBer/Gc00SOTXUVOlsYFA3Yzg95dTun/8SyOqryWta341QImPr1GNdeu04xW1be8vTYg2aWFHj1SmmRAkEA6X6F/J0jA5OH95VsAxzzO24KI6cadiLhIrWL/LiMwtjlsHDQ7N99KPugqwublzWmD/IiE4LxIf13nM3budF5OQJAN7mitIoFyzGZufr9PE1YlR/ohpaA9xf/LmjEnwlDwQd8aHt/dHp4j2m0DMA5yOoj4q6bzRGFJG0wfqGUx96JYQJAFRLpTupzzY/UNsFStVk4jsWYvq0HU2BEh5hxIQcbFT70RB96i9aD+l3zm1x514TtDwVgk/g+gT3aPNnLmhLQ6QJBAKMH7A6PR3yqi46oI/lzS4e5mX98Jzk5GIv1ZoHZNn17EyaQV7egyJ0yVhRI7GpNUhdNAcXBWinw9hzHmNAXd7k=";

    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * 生成对应公钥 私钥
     *
     * @return
     * @throws Exception
     */
    public static Map<String, Object> genKeyPair() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 生成数字签名
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return Base64.encodeBase64String(signature.sign());
    }

    /**
     * 校验数字签名
     *
     * @param data
     * @param publicKey
     * @param sign
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(Base64.decodeBase64(sign));
    }

    /**
     * 私钥解密
     *
     * @param encryptedData
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥解密
     *
     * @param encryptedData
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 获取私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 获取公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    public static String encryptStringByDefaultPublicKey(String input) {
        String result = input;
        try {
            byte[] data = Base64.encodeBase64(input.getBytes("utf-8"));
            byte[] encryptData = encryptByPublicKey(data, DEFAULT_RSA_PUBLICKEY);
            result = Base64.encodeBase64String(encryptData);
        } catch (Exception e) {
            LOG.error(input);
            LOG.error("加密失败", e);
        }
        return result;
    }

    public static String decryptStringByDefaultPrivateKey(String input) {
        String result = input;
        try {
            byte[] data = Base64.decodeBase64(input);
            byte[] decryptData = decryptByPrivateKey(data, DEFAULT_RSA_PRIVATEKEY);
            result = new String(Base64.decodeBase64(decryptData), "utf-8");
        } catch (Exception e) {
            LOG.error(input);
            LOG.error("解密失败", e);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        String a="{\"channelId\":\"CUS000000089\",\"productCode\":\"P000000792\",\"userId\":\"1000001405\",\"applyNo\":\"20180124160833100000006662405\",\"payType\":\"1\"}";
        String b = encryptStringByDefaultPublicKey(a);
        System.out.println(b);
        System.out.println(decryptStringByDefaultPrivateKey(b));
    }
}
