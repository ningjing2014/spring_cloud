package com.ln.xproject.email.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.base.constants.Constants;
import com.ln.xproject.base.constants.ProcessStatus;
import com.ln.xproject.email.service.MailSender;
import com.ln.xproject.email.vo.AttachmentVo;
import com.ln.xproject.util.ArrayUtils;
import com.ln.xproject.util.CollectionUtils;
import com.ln.xproject.util.FileUtils;
import com.ln.xproject.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class MailSenderImpl implements MailSender {

    @Autowired
    private JavaMailSender mailSender;

    /** 发件人 */
    @Value("${mail.from}")
    private String mailFrom;
    /** 邮件白名单是否启用 */
    @Value("${mail.white.list.enable}")
    private boolean mailWhiteListEnable;
    /** 邮件白名单 */
    @Value("${mail.white.list}")
    private String mailWhiteList;
    /** 邮件白名单列表 */
    private List<String> whiteList;

    private static ExecutorService threadPool = Executors.newFixedThreadPool(5);

    @PostConstruct
    public void init() {
        try {
            whiteList = StringUtils.splitToList(mailWhiteList, Constants.COMMON_SEPARATOR);
        } catch (Exception e) {
            log.error("MailSender初始化出错", e);
        }
    }

    public void sendEmailAsync(String[] to, String[] cc, String[] bcc, String subject, String text,
            AttachmentVo... attachments) {
        threadPool.execute(() -> send(to, cc, bcc, subject, text, attachments));
    }

    @Override
    public ProcessStatus send(String[] to, String[] cc, String[] bcc, String subject, String text,
            AttachmentVo... attachments) {
        List<File> files = null;
        try {

            to = filter(to);
            cc = filter(cc);
            bcc = filter(bcc);

            if (ArrayUtils.isEmpty(to)) {
                log.info("收件人列表为空，停止发送");
                return ProcessStatus.PROCESS_FAIL;
            }

            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(to);
            if (ArrayUtils.isNotEmpty(cc))
                helper.setCc(cc);
            if (ArrayUtils.isNotEmpty(bcc))
                helper.setBcc(bcc);
            helper.setSubject(subject);
            helper.setText(text, true);

            if (ArrayUtils.isNotEmpty(attachments)) {
                files = new ArrayList<File>();

                for (AttachmentVo attachment : attachments) {
                    File file = new File(StringUtils.join(
                            new String[] { FileUtils.getTempDirectoryPath(), attachment.getFileName() },
                            File.separator));
                    FileUtils.writeByteArrayToFile(file, attachment.getFileBytes());
                    files.add(file);
                    helper.addAttachment(attachment.getFileName(), file);
                }
            }

            mailSender.send(message);
        } catch (Exception e) {
            log.error("发送邮件出错", e);
            return ProcessStatus.PROCESS_FAIL;
        } finally {
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
        }
        return ProcessStatus.PROCESS_SUCCESS;
    }

    /**
     * 过滤
     * 
     * @param addresses
     * @return
     */
    private String[] filter(String[] addresses) {
        if (addresses == null) {
            return null;
        }
        if (!mailWhiteListEnable) {
            return addresses;
        }
        if (CollectionUtils.isEmpty(whiteList)) {
            return null;
        }

        List<String> list = new ArrayList<String>();

        for (String address : addresses) {
            if (whiteList.contains(address)) {
                list.add(address);
                continue;
            }
            log.info("邮件白名单中不包含 - [{}]", address);
        }

        if (list.isEmpty()) {
            return new String[] {};
        }

        return list.toArray(new String[] {});
    }

}
