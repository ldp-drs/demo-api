package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbApproval;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbApprovalDao {

    //获取用户的信息
    public ArrayList<HashMap> searchUserSummary1(HashMap param);

    //获取审批信息
    public ArrayList<HashMap> searchApprovalContent(HashMap param);

    //获取审批流程图
    public ArrayList<HashMap> searchApprovalBpmn(String instanceId);
}
