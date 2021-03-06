package com.dc18TokenExchange.STSserver.controller;


import com.dc18TokenExchange.STSserver.model.UserInfo;
import com.dc18TokenExchange.STSserver.model.UserInfoDAO;
import com.dc18TokenExchange.STSserver.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;


@RestController
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;


    @GetMapping("/user")
    public Page<UserInfo> getUsers(Pageable pageable){
        return userInfoService.getUsers(pageable);
    }

    @GetMapping("/user/{userId}")
    public UserInfo getDistinctByUserId(@PathVariable Long userId){
        return userInfoService.getDistinctByUserId(userId);
    }

    @PostMapping("/user")
    public UserInfo createUserInfo(@Valid @RequestBody UserInfoDAO userInfoDAO){ //NOTE: This method generates a UserInfo object, but does NOT accept it as input. Rather, it uses UserInfoDAO as a proxy for generating a new UserInfo object.
        return userInfoService.createUserInfo(userInfoDAO);
    }

    @PutMapping("/user")
    public UserInfo updateUserInfo(@Valid @RequestBody UserInfoDAO userInfoDAO){
        return userInfoService.updateUserInfo(userInfoDAO);
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity deleteUserInfo(@PathVariable Long userId){
        return userInfoService.deleteUserInfo(userId);
    }
}
