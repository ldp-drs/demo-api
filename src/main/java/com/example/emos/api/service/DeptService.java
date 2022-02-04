package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbDept;

import java.util.ArrayList;
import java.util.HashMap;

public interface DeptService {
    public ArrayList<HashMap> searchAllDept();
    public HashMap searchById(int id);

    //查询部门分页信息
    PageUtils searchDeptByPage(HashMap param);

    //新增部门信息
    int insert(TbDept dept);

    //修改部门信息
    int update(TbDept dept);

    //删除部门信息
    int deleteDeptByIds(Integer[] ids);
}
