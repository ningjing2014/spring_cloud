package com.ln.xproject.kaptcha.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.kaptcha.KaptchaConstants;
import com.ln.xproject.kaptcha.KaptchaProducer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/validatePic")
public class KaptchaController {

    @Autowired
    private KaptchaProducer kaptchaProducer;
    
    @RequestMapping(value = "/get")
    public JsonResultVo get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session = request.getSession();
        String capText = kaptchaProducer.createText();
        log.info("sessionId:" + session.getId() + ", 短验:" + capText);
        session.setAttribute(KaptchaConstants.KAPTCHA_SESSION_KEY, capText);
        BufferedImage bi = kaptchaProducer.createImage(capText);
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ImageIO.write(bi, "jpg", byteOut);
        JsonResultVo json = JsonResultVo.success();
        String imgData = Base64.encodeBase64String(byteOut.toByteArray());
        IOUtils.closeQuietly(byteOut);
        return json.addData("imgData", imgData);
    }
}
