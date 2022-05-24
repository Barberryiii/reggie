package com.xzkj.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xzkj.reggie.common.R;
import com.xzkj.reggie.entity.Employee;
import com.xzkj.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        String username = employee.getUsername();
        String password = employee.getPassword();

        // 1、将页面提交的密码password进行md5加密处理
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        // 2、根据页面提交的username查询数据库
        LambdaQueryWrapper<Employee> qw = new LambdaQueryWrapper<>();
        qw.eq(Employee::getUsername, username);
        Employee one = employeeService.getOne(qw);

        // 3、如果没有查询到返回登录失败结果
        if(one == null)
            return R.error("用户名或密码错误");

        // 4、密码比对，如果不一致则返回登录失败结果
        if(!one.getPassword().equals(password))
            return R.error("用户名或密码错误");

        // 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(one.getStatus() == 0)
            return R.error("账号已禁用");

        // 6、登录成功，将员工id存入Session并返回登陆成功结果
        request.getSession().setAttribute("employee", one.getId());

        return R.success(one);
    }

    /**
     * 员工退出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

}
