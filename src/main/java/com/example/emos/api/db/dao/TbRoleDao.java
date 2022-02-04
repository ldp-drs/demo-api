package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;


@Mapper
public interface TbRoleDao {
    public ArrayList<HashMap> searchAllRole();

    public HashMap searchById(int id);

    //查询角色分页信息
    public ArrayList<HashMap> searchRoleByPage(HashMap param);

    //查询角色总数
    public long searchRoleCount(HashMap param);

    //新增角色信息
    public int insert(TbRole role);

    //修改角色信息
    public int update(TbRole role);

    //查询角色关联的用户id
    public ArrayList<Integer> searchUserIdByRoleId(int roleId);

    //删除角色信息
    public int deleteRoleByIds(Integer[] ids);

    //查询删除的角色是否关联了其他用户
    public boolean searchCanDelete(Integer[] ids);
}