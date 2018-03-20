package com.ln.xproject.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class FileUtils extends org.apache.commons.io.FileUtils {

    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * <p>
     * 文件转换为二进制数组
     * </p>
     *
     * @param filePath
     *            文件路径
     * @return
     * @throws Exception
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = null;
            ByteArrayOutputStream out = null;
            try {
                in = new FileInputStream(file);
                out = new ByteArrayOutputStream(2048);
                byte[] cache = new byte[CACHE_SIZE];
                int nRead = 0;
                while ((nRead = in.read(cache)) != -1) {
                    out.write(cache, 0, nRead);
                    out.flush();
                }
                data = out.toByteArray();
            } finally {
                if (null != out) {
                    out.close();
                }
                if (null != in) {
                    in.close();
                }
            }
        }
        return data;
    }

    /**
     * 判断文件名是否和需要判断一致
     * @param fileName 原文件名(文件+类型)
     * @param targetTypes 判断的文件类型
     * @return
     */
    public static boolean isFileType(String fileName,String... targetTypes){

        if(Strings.isNullOrEmpty(fileName) || targetTypes==null || targetTypes.length==0){
            return false;
        }
        //获取源文件后缀名
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
        //循环比较
        List<String> types = Lists.newArrayList(targetTypes);
        for (String type : types) {
            if(fileType.equals(type)){
                return true;
            }
        }
        return false;
    }

}
