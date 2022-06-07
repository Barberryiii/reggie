package com.xzkj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xzkj.reggie.common.R;
import com.xzkj.reggie.entity.User;
import com.xzkj.reggie.service.UserService;
import com.xzkj.reggie.utils.SMSUtils;
import com.xzkj.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${SMS.signName}")
    private String signName;

    @Value("${SMS.templateCode}")
    private String templateCode;

    @Value("${SMS.accessKeyId}")
    private static String accessKeyId;

    @Value("${SMS.accessKeySecret}")
    private static String accessKeySecret;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        // 获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            // 生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();

            log.info("code={}", code);

            // 调用阿里云提供的短信服务API完成发送短信
            SMSUtils.sendMessage(accessKeyId, accessKeySecret, signName, templateCode, phone, code);

            // 需要将生成的验证码保存到redis
            redisTemplate.opsForValue().set(phone, code, 5L, TimeUnit.MINUTES);

            return R.success(code);
        }

        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(HttpSession session, @RequestBody Map map){
        // 获取手机号
        String phone = (String) map.get("phone");
        // 获取验证码
        String code = (String) map.get("code");
        // 从redis中获取保存的验证码
        String realCode = (String) redisTemplate.opsForValue().get(phone);
        // 进行验证码的比对（页面提交的验证码和redis中保存的验证码比对）
        if(realCode != null && realCode.equals(code)){
            // 如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
            qw.eq(User::getPhone, phone);

            User user = userService.getOne(qw);

            // 判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
            if(user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }

            session.setAttribute("user", user.getId());

            return R.success(user);
        }

        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
