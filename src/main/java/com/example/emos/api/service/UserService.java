package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public interface UserService {
    public HashMap createQrCode();

    public boolean checkQrCode(String code,String uuid);

    public HashMap wechatLogin(String uuid);

    public Set<String> searchUserPermissions(int userId);

    String searchByIdPassword(int userId);

    public HashMap searchUserSummary(int userId);

    public HashMap searchById(int userId);

    public ArrayList<HashMap> searchAllUser();

    //登录
    Integer login(HashMap param);

    //修改密码
    int updatePassword(HashMap param);

    //查询用户分页数据
    PageUtils searchUserByPage(HashMap param);

    //新增用户
    int insert(TbUser user);

    //修改用户
    int update(HashMap param);

    //删除用户信息
    int deleteUserByIds(Integer[] ids);

    //查询用户角色
    ArrayList<String> searchUserRoles(int userId);
}
