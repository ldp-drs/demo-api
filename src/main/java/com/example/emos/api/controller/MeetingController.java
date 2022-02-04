package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbMeeting;
import com.example.emos.api.service.MeetingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/meeting")
@Tag(name = "MeetingController", description = "会议接口")
@Slf4j
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @PostMapping("/searchOfflineMeetingByPage")
    @Operation(summary = "查询线下会议的分页数据")
    @SaCheckLogin
    public R searchOfflineMeetingByPage(@Valid @RequestBody SearchOfflineMeetingByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;
        HashMap param = new HashMap() {{
            put("date", form.getDate());//日期
            put("mold", form.getMold());//模式
            put("userId", StpUtil.getLoginId());//用户id
            put("start", start);
            put("length", length);
        }};
        PageUtils pageUtils = meetingService.searchOfflineMeetingByPage(param);//执行语句获取数据
        return R.ok().put("page", pageUtils);
    }

    @PostMapping("/insert")
    @Operation(summary = "添加会议")
    @SaCheckLogin
    public R insert(@Valid @RequestBody InsertMeetingForm form) {
        DateTime start = DateUtil.parse(form.getDate() + " " + form.getStart());//开始时间
        DateTime end = DateUtil.parse(form.getDate() + " " + form.getEnd());//结束时间
        //时间判断
        if (start.isAfterOrEquals(end)) {
            return R.error("结束时间必须大于开始时间");
        } else if (new DateTime().isAfterOrEquals(start)) {
            return R.error("会议开始时间不能早于当前时间");
        }
        //转换数据类型
        TbMeeting meeting = JSONUtil.parse(form).toBean(TbMeeting.class);
        //更新数据
        meeting.setUuid(UUID.randomUUID().toString(true));//生成随机的uuid字符串
        meeting.setCreatorId(StpUtil.getLoginIdAsInt());//获取用户的id
        meeting.setStatus((short) 1);//申请状态
        //执行语句
        int rows = meetingService.insert(meeting);
        return R.ok().put("rows", rows);
    }

    //接收会议审批结果的方法
    @PostMapping("/recieveNotify")
    @Operation(summary = "接收工作流通知")
    public R recieveNotify(@Valid @RequestBody RecieveNotifyForm form) {
        if (form.getResult().equals("同意")) {
            //想发送邮件还是短信的可以自己写代码在这里
            log.debug(form.getUuid() + "的会议审批通过");
        } else {
            //想发送邮件还是短信的可以自己写代码在这里
            log.debug(form.getUuid() + "的会议审批不通过");
        }
        return R.ok();
    }

    @PostMapping("/searchOfflineMeetingInWeek")
    @Operation(summary = "查询某个会议室的一周会议")
    @SaCheckLogin
    public R searchOfflineMeetingInWeek(@Valid @RequestBody SearchOfflineMeetingInWeekForm form) {
        String date = form.getDate();//获取输入的日期时间
        DateTime startDate, endDate;//定义开始和结束日期时间
        //判断输入的日期是否为空
        if (date != null && date.length() > 0) {
            //从date开始，生成七天日期
            startDate = DateUtil.parseDate(date);
            endDate = startDate.offsetNew(DateField.DAY_OF_WEEK, 6);
        } else {
            //查询当前日期，生成本周的日期
            startDate = DateUtil.beginOfWeek(new Date());
            endDate = DateUtil.endOfWeek(new Date());
        }
        //生成数组数据
        HashMap param = new HashMap() {{
            put("place", form.getName());
            put("startDate", startDate.toDateStr());
            put("endDate", endDate.toDateStr());
            put("mold", form.getMold());
            put("userId", StpUtil.getLoginIdAsLong());
        }};
        //获取数据
        ArrayList list = meetingService.searchOfflineMeetingInWeek(param);

        //生成周日历水平表头的文字标题（DAY_OF_WEEK：以天为刻数）
        DateRange range = DateUtil.range(startDate, endDate, DateField.DAY_OF_WEEK);
        ArrayList days = new ArrayList();
        //循环每一天的内容并写入days中
        range.forEach(one -> {
            JSONObject json = new JSONObject();
            json.set("date", one.toString("MM/dd"));
            json.set("day", one.dayOfWeekEnum().toChinese("周"));
            days.add(json);
        });
        return R.ok().put("list", list).put("days", days);
    }

    @PostMapping("/searchMeetingInfo")
    @Operation(summary = "查询会议信息")
    @SaCheckLogin
    public R searchMeetingInfo(@Valid @RequestBody SearchMeetingInfoForm form) {
        HashMap map = meetingService.searchMeetingInfo(form.getStatus(), form.getId());
        return R.ok(map);
    }

    @PostMapping("/deleteMeetingApplication")
    @Operation(summary = "删除会议申请")
    @SaCheckLogin
    public R deleteMeetingApplication(@Valid @RequestBody DeleteMeetingApplicationForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);//转换类型
        param.put("creatorId", StpUtil.getLoginIdAsLong());//更新创建者id
        param.put("userId", StpUtil.getLoginIdAsLong());//更新用户id
        int rows = meetingService.deleteMeetingApplication(param);//执行代码
        return R.ok().put("rows", rows);
    }
}
