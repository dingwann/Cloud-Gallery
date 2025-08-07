package org.example.cloudgallery.service.impl;

import jakarta.annotation.Resource;
import org.example.cloudgallery.service.IEmailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class EmailServiceImplTest {

    @Resource
    private IEmailService emailService;

    @Resource
    private ResourceLoader resourceLoader;

    @Test
    void send() {
    }

    @Test
    void sendText() {
    }

    @Test
    void sendHtml() throws IOException {
        org.springframework.core.io.Resource resource = resourceLoader.getResource("classpath:html/register.st");
        // 使用 try-with-resources 确保流被正确关闭
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            String content = FileCopyUtils.copyToString(reader);

            emailService.sendHtml("dingwann@qq.com", "895141668@qq.com", "云图库注册验证码", content);
        }
    }
}