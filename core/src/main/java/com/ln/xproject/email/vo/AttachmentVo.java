package com.ln.xproject.email.vo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 邮件附件
 */
@Data
@AllArgsConstructor
public class AttachmentVo implements Serializable {

    private static final long serialVersionUID = 3693364562845237894L;

    /** 文件名 */
    private String fileName;
    /** 文件 */
    private byte[] fileBytes;

}
