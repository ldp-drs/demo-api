package com.example.emos.api.task;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.db.dao.TbMeetingDao;
import com.example.emos.api.db.dao.TbUserDao;
import com.example.emos.api.exception.EmosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;

/**
 * 异步线程类
 */
@Component//交给spring管理
@Slf4j
public class MeetingWorkflowTask {

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private TbMeetingDao meetingDao;

    @Value("${emos.recieveNotify}")
    private String recieveNotify;//请求URL路径

    @Value("${emos.code}")
    private String code;//授权码

    @Value("${emos.tcode}")
    private String tcode;//小程序授权码

    @Value("${workflow.url}")
    private String workflow;//工作流请求URL路径

    @Async("AsyncTaskExecutor")//异步线程执行注解
    //（uuid，申请人id，会议标题，会议日期，会议开始时间，会议类型）
    public void startMeetingWorkflow(String uuid, int creatorId, String title, String date, String start, String meetingType) {
        //查询申请人基本信息
        HashMap info = userDao.searchUserInfo(creatorId);

        JSONObject json = new JSONObject();//创建提交的信息
        json.set("url", recieveNotify);//请求URL路径
        json.set("uuid", uuid);//uuid
        json.set("creatorID", creatorId);//申请人id
        json.set("creatorName", info.get("name").toString());//创建人名字
        json.set("code", code);//授权码
        json.set("tcode", tcode);//小程序授权码
        json.set("title", title);//会议标题
        json.set("date", date);//会议日期
        json.set("start", start);//会议开始时间
        json.set("meetingType", meetingType);//会议类型

        String[] roles = info.get("roles").toString().split("，");//获取角色信息并使用,间隔
        //判断用户角色是不是总经理，总经理创建的会议不需要审批，所以不需要查询总经理userId和部门经理的userId了
        if (!ArrayUtil.contains(roles, "总经理")) {//判断数组里是否包含总经理的角色
            //查询部门经理userId
            Integer managerId = userDao.searchDeptManagerId(creatorId);
            json.set("managerId", managerId);

            //查询总经理userId
            Integer gmId = userDao.searchGmId();
            json.set("gmId", gmId);

            //查询参会人是否为同一部门
            boolean bool = meetingDao.searchMeetingMembersInSameDept(uuid);
            json.set("sameDept", bool);
        }
        String url = workflow + "/workflow/startMeetingProcess";//工作流请求
        //发送请求（post请求，请求头，data数据）执行
        // HttpResponse resp = HttpRequest.post(url).header("Content-Type", "application/json").body(json.toString()).execute();
        //判断返回的状态
        // if (resp.getStatus() == 200) {
        //提取数据
        // json = JSONUtil.parseObj(resp.body());
        //获取工作流实例ID
        // String instanceId = json.getStr("instanceId");
        String instanceId = "c76d1083-dff4-11eb-b51f-" + new Date().getTime();
        HashMap param = new HashMap();
        param.put("uuid", uuid);
        param.put("instanceId", instanceId);
        //更新会议记录的instance_id字段
        int row = meetingDao.updateMeetingInstanceId(param);
        if (row != 1) {
            throw new EmosException("保存会议工作流实例ID失败");
        }
        // } else {
        //     log.error(resp.body());
        // }
    }

    @Async("AsyncTaskExecutor")
    public void deleteMeetingApplication(String uuid, String instanceId, String reason) {
        JSONObject json = new JSONObject();
        json.set("uuid", uuid);
        json.set("instanceId", instanceId);//工作流id
        json.set("code", code);
        json.set("tcode", tcode);
        json.set("type", "会议申请");
        json.set("reason", reason);
        String url = workflow + "/workflow/deleteProcessById";
        // HttpResponse resp = HttpRequest.post(url).header("Content-Type", "application/json").body(json.toString()).execute();
        // if (resp.getStatus() == 200) {
        log.debug("工作流删除了会议申请");
        // } else {
        //     log.error(resp.body());
        // }
    }
}
