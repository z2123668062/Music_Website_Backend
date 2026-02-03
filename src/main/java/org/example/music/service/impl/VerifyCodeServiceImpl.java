package org.example.music.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.music.dto.SendCodeDTO;
import org.example.music.service.VerifyCodeService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 验证码服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VerifyCodeServiceImpl implements VerifyCodeService {

    // 注入Spring Mail 邮件发送器（已配置QQ邮箱SMTP）
    private final JavaMailSender javaMailSender;
    // 注入Redis操作模板（已配置Redis）
    private final StringRedisTemplate stringRedisTemplate;

    // 常量定义（便于维护）
    /** 验证码Redis存储前缀，格式：verify:code:${verifyType}:${verifyAccount} */
    private static final String REDIS_CODE_KEY_PREFIX = "verify:code:";
    /** 验证码有效期：5分钟（300秒） */
    private static final long CODE_EXPIRE_SECONDS = 300;
    /** 验证码长度：6位数字 */
    private static final int CODE_LENGTH = 6;
    /** 发送邮箱的发件人（需与你配置文件中的QQ邮箱一致） */
    private static final String SENDER_EMAIL = "2123668062@qq.com";

    @Override
    public Boolean verifyCode(SendCodeDTO sendCodeDTO) {
        try {
            // 1. 提取DTO中的参数
            String verifyType = sendCodeDTO.getVerifyType();
            String verifyAccount = sendCodeDTO.getVerifyAccount();

            // 2. 生成6位随机数字验证码
            String verifyCode = generateRandomVerifyCode();

            // 3. 按验证类型分支处理
            if ("email".equals(verifyType)) {
                // 3.1 邮箱类型：真实发送验证码邮件
                sendEmailVerifyCode(verifyAccount, verifyCode);
            } else if ("phone".equals(verifyType)) {
                // 3.2 手机类型：模拟发送（仅打印日志，后续替换为真实短信API即可）
                simulateSendPhoneVerifyCode(verifyAccount, verifyCode);
            } else {
                // 理论上DTO有@Pattern校验，不会走到这里，做兜底容错
                log.error("不支持的验证类型：{}", verifyType);
                return false;
            }

            // 4. 存入Redis（设置过期时间，key保证唯一性）
            String redisKey = REDIS_CODE_KEY_PREFIX + verifyType + ":" + verifyAccount;
            stringRedisTemplate.opsForValue().set(redisKey, verifyCode, CODE_EXPIRE_SECONDS, TimeUnit.SECONDS);

            log.info("验证码处理成功，类型：{}，账号：{}，验证码：{}", verifyType, verifyAccount, verifyCode);
            return true;
        } catch (Exception e) {
            log.error("验证码处理失败，异常信息：", e);
            return false;
        }
    }

    /**
     * 生成6位随机数字验证码
     */
    private String generateRandomVerifyCode() {
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            // 生成0-9的随机数字
            codeBuilder.append(random.nextInt(10));
        }
        return codeBuilder.toString();
    }

    /**
     * 发送邮箱验证码（真实QQ邮箱发送）
     * @param toEmail 收件人邮箱
     * @param verifyCode 验证码
     * @throws MessagingException 邮件发送异常
     */
    private void sendEmailVerifyCode(String toEmail, String verifyCode) throws MessagingException {
        // 1. 创建MimeMessage（支持HTML格式邮件）
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        // 2. 设置邮件参数
        helper.setFrom(SENDER_EMAIL); // 发件人（与配置文件一致）
        helper.setTo(toEmail); // 收件人
        helper.setSubject("【你的系统名称】验证码通知"); // 邮件主题

        // 3. 构建邮件内容（HTML格式，更美观）
        String emailContent = String.format(
                "<div style='font-size: 14px; line-height: 1.8;'>" +
                        "您好！<br/>" +
                        "你正在进行验证操作，本次验证码为：<br/>" +
                        "<span style='font-size: 18px; font-weight: bold; color: #0066cc;'>%s</span><br/>" +
                        "该验证码有效期为5分钟，请在有效期内完成验证，请勿泄露给他人！<br/>" +
                        "如果非你本人操作，请忽略此邮件。</div>",
                verifyCode
        );
        helper.setText(emailContent, true); // 第二个参数为true，表示启用HTML格式

        // 4. 发送邮件
        javaMailSender.send(mimeMessage);
        log.info("邮箱验证码发送成功，收件人：{}", toEmail);
    }

    /**
     * 模拟发送手机验证码（仅打印日志，后续替换为真实短信API）
     * @param phone 收件人手机号
     * @param verifyCode 验证码
     */
    private void simulateSendPhoneVerifyCode(String phone, String verifyCode) {
        // 模拟短信发送逻辑（真实场景需替换为阿里云短信/腾讯云短信等API调用）
        log.info("模拟发送手机验证码成功，手机号：{}，验证码：{}", phone, verifyCode);
    }
}
