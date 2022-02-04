package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;

import java.util.ArrayList;
import java.util.HashMap;

public interface ApprovalService {
    PageUtils searchTaskByPage(HashMap param, int userId);

    //获取审批信息
    HashMap searchApprovalContent(HashMap param);

    //获取审批流程图
    ArrayList<HashMap> searchApprovalBpmn(String instanceId);
}
