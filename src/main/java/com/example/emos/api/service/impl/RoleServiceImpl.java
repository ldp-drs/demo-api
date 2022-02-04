package com.example.emos.api.service.impl;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.TbRoleDao;
import com.example.emos.api.db.pojo.TbRole;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private TbRoleDao roleDao;

    @Override
    public ArrayList<HashMap> searchAllRole() {
        ArrayList<HashMap> list = roleDao.searchAllRole();
        return list;
    }

    @Override
    public HashMap searchById(int id) {
        HashMap map = roleDao.searchById(id);
        return map;
    }

    //查询角色分页信息
    @Override
    public PageUtils searchRoleByPage(HashMap param) {
        ArrayList<HashMap> list = roleDao.searchRoleByPage(param);//获取角色信息
        long count = roleDao.searchRoleCount(param);//获取角色总数量
        int start = (Integer) param.get("start");//获取起始页
        int length = (Integer) param.get("length");//获取显示条数
        return new PageUtils(list, count, start, length);//返回数据
    }

    //新增角色信息
    @Override
    public int insert(TbRole role) {
        return roleDao.insert(role);
    }

    //修改角色信息
    @Override
    public int update(TbRole role) {
        return roleDao.update(role);
    }

    //查询角色关联的用户id
    @Override
    public ArrayList<Integer> searchUserIdByRoleId(int roleId) {
        return roleDao.searchUserIdByRoleId(roleId);
    }

    //删除角色信息
    @Override
    public int deleteRoleByIds(Integer[] ids) {
        if (!roleDao.searchCanDelete(ids)) {
            throw new EmosException("无法删除拥有关联用户的角色");
        }
        return roleDao.deleteRoleByIds(ids);
    }
}
