package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Mapper
public interface TbUserDao {
    public Set<String> searchUserPermissions(int userId);

    public HashMap searchById(int userId);

    public String searchByIdPassword(int userId);

    public Integer searchIdByOpenId(String openId);

    public HashMap searchUserSummary(int userId);

    public HashMap searchUserInfo(int userId);

    public Integer searchDeptManagerId(int id);

    public Integer searchGmId();

    public ArrayList<HashMap> searchAllUser();


    /***************************/
    //登录
    public Integer login(HashMap param);

    //修改密码
    public int updatePassword(HashMap param);

    //查询用户分页数据
    public ArrayList<HashMap> searchUserByPage(HashMap param);

    //查询用户总数
    public long searchUserCount(HashMap param);

    //新增用户
    public int insert(TbUser user);

    //修改用户信息
    public int update(HashMap param);

    //删除用户信息
    public int deleteUserByIds(Integer[] ids);

    //查询用户角色
    public ArrayList<String> searchUserRoles(int userId);
}