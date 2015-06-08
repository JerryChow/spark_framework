/*
 * Copyright 2015-2020 Fengduo.com All right reserved. This software is the confidential and proprietary information of
 * Fengduo.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Fengduo.com.
 */
package com.fengduo.spark.web.controller.account;

import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fengduo.spark.model.entity.User;
import com.fengduo.spark.service.impl.account.ShiroDbRealm.ShiroUser;
import com.fengduo.spark.service.interfaces.AccountService;

/**
 * 用户修改自己资料的Controller.
 * 
 * @author zxc May 28, 2015 11:54:13 PM
 */
@Controller
@RequestMapping(value = "/profile")
public class ProfileController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(method = RequestMethod.GET)
    public String updateForm(Model model) {
        Long id = getCurrentUserId();
        model.addAttribute("user", accountService.getUser(id));
        return "account/profile";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String update(@Valid
    @ModelAttribute("user")
    User user) {
        accountService.updateUser(user);
        updateCurrentUserName(user.getName());
        return "redirect:/";
    }

    /**
     * 所有RequestMapping方法调用前的Model准备方法, 实现Struts2 Preparable二次部分绑定的效果,先根据form的id从数据库查出User对象,再把Form提交的内容绑定到该对象上。
     * 因为仅update()方法的form中有id属性，因此仅在update时实际执行.
     */
    @ModelAttribute
    public void getUser(@RequestParam(value = "id", defaultValue = "-1")
    Long id, Model model) {
        if (id != -1) {
            model.addAttribute("user", accountService.getUser(id));
        }
    }

    /**
     * 取出Shiro中的当前用户Id.
     */
    private Long getCurrentUserId() {
        ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
        return user.id;
    }

    /**
     * 更新Shiro中当前用户的用户名.
     */
    private void updateCurrentUserName(String userName) {
        ShiroUser user = (ShiroUser) SecurityUtils.getSubject().getPrincipal();
        user.name = userName;
    }
}