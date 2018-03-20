package com.ln.xproject.base.controller;

import java.util.Date;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.ln.xproject.base.constant.PageConstants;
import com.ln.xproject.configuration.web.propertyeditors.DateEditor;

/**
 * Created by Administrator on 2015/5/29.
 */

@Controller
public class BaseController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, new DateEditor());
    }

    // 开始页标
    protected int getPageNum(Integer pageNum) {
        if (null == pageNum || pageNum <= 0) {
            pageNum = PageConstants.PAGE_START;
        }
        return pageNum;
    }

    // 每页条数
    protected int getPageSize(Integer pageSize) {
        if (null == pageSize || pageSize <= 0) {
            pageSize = PageConstants.PAGE_SIZE_DEFAULT;
        } else if (pageSize > PageConstants.PAGE_SIZE) {
            pageSize = PageConstants.PAGE_SIZE;
        }
        return pageSize;
    }

}
