package com.example.emos.api.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.TbApprovalDao;
import com.example.emos.api.db.pojo.TbApproval;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.service.ApprovalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class ApprovalServiceImpl implements ApprovalService {

    @Value("${workflow.url}")
    private String workflow;

    @Value("${emos.code}")
    private String code;

    @Value("${emos.tcode}")
    private String tcode;

    @Autowired
    private TbApprovalDao approvalDao;

    @Override
    public PageUtils searchTaskByPage(HashMap param, int userId) {
        param.put("code", code);
        param.put("tcode", tcode);
        String url = workflow + "/workflow/searchTaskByPage";
        // HttpResponse resp = HttpRequest.post(url).header("Content-Type", "application/json").body(JSONUtil.toJsonStr(param)).execute();
        // if (resp.getStatus() == 200) {
        //     JSONObject json = JSONUtil.parseObj(resp.body());
        //     JSONObject page = json.getJSONObject("page");
        //     ArrayList list = page.get("list", ArrayList.class);
        //     Long totalCount = page.getLong("totalCount");
        //     Integer pageIndex = page.getInt("pageIndex");
        //     Integer pageSize = page.getInt("pageSize");
        ArrayList<HashMap> Result = approvalDao.searchUserSummary1(param);
        List list = new ArrayList();
        if (Result.size() > 0) {
            for (int i = 0; i < Result.size(); i++) {
                JSONObject jsonObject = new JSONObject(Result.get(i));
                if (jsonObject.get("type").equals(2)) {
                    jsonObject.set("type", "????????????");
                } else {
                    jsonObject.set("type", "????????????");
                }
                if (jsonObject.get("status").equals(1)) {
                    jsonObject.set("status", "?????????");
                } else if (jsonObject.get("status").equals(2)) {
                    jsonObject.set("status", "???????????????");
                } else if (jsonObject.get("status").equals(2)) {
                    jsonObject.set("status", "????????????");
                } else if (jsonObject.get("status").equals(2)) {
                    jsonObject.set("status", "???????????????");
                } else {
                    jsonObject.set("status", "????????????");
                }
                list.add(jsonObject);
            }
        }

        // if (map.get("type").equals(2)) {
        //     map.put("type", "????????????");
        // } else {
        //     map.put("type", "????????????");
        // }
        // if (map.get("status").equals(1)) {
        //     map.put("status", "?????????");
        // } else if (map.get("status").equals(2)) {
        //     map.put("status", "???????????????");
        // } else if (map.get("status").equals(3)) {
        //     map.put("status", "????????????");
        // } else if (map.get("status").equals(4)) {
        //     map.put("status", "???????????????");
        // } else {
        //     map.put("status", "????????????");
        // }
        // JSONObject jsonObject = new JSONObject(map);//???HashMap?????????json
        // List list = new ArrayList();
        // list.add(jsonObject);//???json????????????list???
        Long totalCount = Long.valueOf(list.size());
        Integer pageIndex = (Integer) param.get("page");
        Integer pageSize = (Integer) param.get("length");
        PageUtils pageUtils = new PageUtils(list, totalCount, pageIndex, pageSize);
        return pageUtils;
        // } else {
        //     log.error(resp.body());
        //     throw new EmosException("???????????????????????????");
        // }
    }

    //??????????????????
    @Override
    public HashMap searchApprovalContent(HashMap param) {
        param.put("code", code);
        param.put("tcode", tcode);
        String url = workflow + "/workflow/searchApprovalContent";
        // HttpResponse resp = HttpRequest.post(url).header("Content-Type", "application/json").body(JSONUtil.toJsonStr(param)).execute();
        // if (resp.getStatus() == 200) {
        // JSONObject json = JSONUtil.parseObj(resp.body());
        ArrayList<HashMap> Result = approvalDao.searchApprovalContent(param);
        // List list = new ArrayList();
        // JSONObject json = JSONUtil.parseObj(Result.get(0));
        // String arr = (String) json.get("members");
        // String[] s = arr.split(",");
        // System.out.println(arr);
        // for (String each : s) {
        //     if (each.substring(each.lastIndexOf("[") + 1).equals("5")) {
        //         System.out.println(each.substring(each.lastIndexOf("[") + 1));
        //     } else {
        //         System.out.println(each.substring(1,3));
        //     }
        // }
        // for (int i = 0; i < arr.length(); i++) {
        //     System.out.println();
        // }

        return Result.get(0);
        // } else {
        //     throw new EmosException("???????????????????????????");
        // }
    }

    //?????????????????????
    @Override
    public ArrayList<HashMap> searchApprovalBpmn(String instanceId){
        return approvalDao.searchApprovalBpmn(instanceId);
    }
}
