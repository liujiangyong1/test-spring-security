package com.ljy.testspringsecurity.config;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author 不要有情绪的  ljy
 * @Date 2023/1/13 15:39
 * @Description:
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    //授权认证
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //定制请求的授权规则    其目的是：告诉有哪些权限的人才可以访问页面
        http.authorizeRequests()
                .antMatchers("/").permitAll()  //首页所有人可以访问
                .antMatchers("/level1/**").hasRole("vip1")
                .antMatchers("/level2/**").hasRole("vip2")
                .antMatchers("/level3/**").hasRole("vip3");

        //开启自动配置的登录功能
        //没有权限默认会到登录页面
        //定制登陆页面("/toLogin")
        //登录认证("/login")
        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/toLogin")
                .loginProcessingUrl("/login");


        /*防止网站攻击*/   //也是解决注销报错的方法
        http.csrf().disable();//关闭csrf


        // 开启自动配置的注销的功能
        /*注销成功后跳到首页*/
        http.logout().logoutSuccessUrl("/");


        //记住我功能，自定义接受前端数据
        http.rememberMe().rememberMeParameter("remember");
    }


    //定义认证规则     其目的是：告诉哪些的用户拥有哪些权限，即给用户绑定用户角色
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //在内存中定义,也可以在jdbc中去拿
        //Spring security 5.0中新增了多种加密方式，也改变了密码的格式
        //要想我们的项目还能够正常登录，需要修改以下configure中的代码，我们要将前端传过来的密码进行某种方法的加密
        //spring security 官方推荐的是使用bcrypt加密方式。
        auth.inMemoryAuthentication().passwordEncoder(new BCryptPasswordEncoder())
                .withUser("kuangshen").password(new BCryptPasswordEncoder().encode("123456")).roles("vip2", "vip3")
                .and()
                .withUser("root").password(new BCryptPasswordEncoder().encode("123456")).roles("vip1", "vip2", "vip3")
                .and()
                .withUser("guest").password(new BCryptPasswordEncoder().encode("123456")).roles("vip1", "vip2");
    }
}
