package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbRole;

import java.util.ArrayList;
import java.util.HashMap;

public interface RoleService {
    public ArrayList<HashMap> searchAllRole();
    public HashMap searchById(int id);

    //查询角色分页信息
    PageUtils searchRoleByPage(HashMap param);

    //新增角色信息
    int insert(TbRole role);

    //修改角色信息
    int update(TbRole role);

    //查询角色关联的用户id
    ArrayList<Integer> searchUserIdByRoleId(int roleId);

    //删除角色信息
    int deleteRoleByIds(Integer[] ids);
}
