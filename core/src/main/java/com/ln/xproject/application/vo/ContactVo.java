package com.ln.xproject.application.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ContactVo implements Serializable {

    private static final long serialVersionUID = -6682321128545925925L;

    private String name;

    private String mobile;

    private String relationship;

    private String familiar;
}
