package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mapper
public interface TbDeptDao {
    public ArrayList<HashMap> searchAllDept();

    public HashMap searchById(int id);

    //查询部门分页信息
    public ArrayList<HashMap> searchDeptByPage(HashMap param);

    //查询部门分页信息总数
    public long searchDeptCount(HashMap param);

    //新增部门信息
    public int insert(TbDept dept);

    //修改部门信息
    public int update(TbDept dept);

    //删除部门信息
    public int deleteDeptByIds(Integer[] ids);

    //查询要删除的部门是否有关联
    public boolean searchCanDelete(Integer[] ids);
}