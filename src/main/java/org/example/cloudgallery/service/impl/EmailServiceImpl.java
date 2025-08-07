package org.example.cloudgallery.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import jakarta.annotation.Resource;
import jakarta.mail.internet.InternetAddress;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.example.cloudgallery.exception.BusinessException;
import org.example.cloudgallery.exception.ErrorCode;
import org.example.cloudgallery.service.IEmailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class EmailServiceImpl implements IEmailService {

    @Resource
    private JavaMailSender javaMailSender;//注入邮件工具类

    @Override
    public void send(String name, String form, String to, String subject, String content, Boolean isHtml, String cc, String bcc, List<File> files) {

        if (StringUtils.isAnyBlank(form, to, subject, content)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "发送人,接收人,主题,内容均不可为空");
        }
        try {
            //true表示支持复杂类型
            MimeMessageHelper messageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(), true);
            //邮件发信人
            messageHelper.setFrom(new InternetAddress(name + "<" + form + ">"));
            //邮件收信人
            messageHelper.setTo(to.split(","));
            //邮件主题
            messageHelper.setSubject(subject);
            //邮件内容
            messageHelper.setText(content, isHtml);
            //抄送
            if (!StringUtils.isEmpty(cc)) {
                messageHelper.setCc(cc.split(","));
            }
            //密送
            if (!StringUtils.isEmpty(bcc)) {
                messageHelper.setCc(bcc.split(","));
            }
            //添加邮件附件
            if (CollectionUtil.isNotEmpty(files)) {
                for (File file : files) {
                    messageHelper.addAttachment(file.getName(), file);
                }
            }
            // 邮件发送时间
            messageHelper.setSentDate(new Date());
            //正式发送邮件
            javaMailSender.send(messageHelper.getMimeMessage());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "邮件发送失败");
        }
    }


    @Override
    public void sendText(String form, String to, String subject, String content) {
        this.send("Cloud Gallery", form, to, subject, content, false, null, null, null);
    }

    @Override
    public void sendHtml(String form, String to, String subject, String content) {
        this.send("Cloud Gallery", form, to, subject, content, true, null, null, null);
    }

}

