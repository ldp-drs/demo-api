package com.example.emos.api.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.dao.TbMeetingDao;
import com.example.emos.api.db.pojo.TbMeeting;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.service.MeetingService;
import com.example.emos.api.task.MeetingWorkflowTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    private TbMeetingDao meetingDao;

    @Autowired
    private MeetingWorkflowTask meetingWorkflowTask;

    //查询会议记录
    @Override
    public PageUtils searchOfflineMeetingByPage(HashMap param) {
        ArrayList<HashMap> list = meetingDao.searchOfflineMeetingByPage(param);//获取会议信息
        long count = meetingDao.searchOfflineMeetingCount(param);//获取会议总数
        int start = MapUtil.getInt(param, "start");//获取起始页
        int length = MapUtil.getInt(param, "length");//获取长度
        //循环会议信息
        for (HashMap map : list) {
            String meeting = (String) map.get("meeting");//获取会议信息
            if (meeting != null && meeting.length() > 0) {
                map.replace("meeting", JSONUtil.parseArray(meeting));
            }
        }
        return new PageUtils(list, count, start, length);
    }

    //新增会议记录
    @Override
    public int insert(TbMeeting meeting) {
        int rows = meetingDao.insert(meeting);
        if (rows != 1) {
            throw new EmosException("会议添加失败");
        }
        //执行工作流实例
        meetingWorkflowTask.startMeetingWorkflow(meeting.getUuid(), meeting.getCreatorId(), meeting.getTitle(), meeting.getDate(), meeting.getStart() + ":00", "线下会议");
        return rows;
    }

    //查询周日历和会议详情
    @Override
    public ArrayList<HashMap> searchOfflineMeetingInWeek(HashMap param) {
        return meetingDao.searchOfflineMeetingInWeek(param);
    }

    //根据会议状态查询会议信息
    @Override
    public HashMap searchMeetingInfo(short status, long id) {
        //判断正在进行中的会议
        HashMap map;
        if (status == 4 || status == 5) {
            map = meetingDao.searchCurrentMeetingInfo(id);
        } else {
            map = meetingDao.searchMeetingInfo(id);
        }
        return map;
    }

    //珊瑚会议信息
    @Override
    public int deleteMeetingApplication(HashMap param) {
        Long id = MapUtil.getLong(param, "id");//获取会议id
        String uuid = MapUtil.getStr(param, "uuid");//获取uuid
        String instanceId = MapUtil.getStr(param, "instanceId");//获取工作流id
        //查询会议详情，一会要判断是否距离会议开始不足20分钟
        HashMap meeting = meetingDao.searchMeetingById(param);//获取会议信息
        String date = MapUtil.getStr(meeting, "date");//获取会议日期
        String start = MapUtil.getStr(meeting, "start");//获取会议起始时间
        int status = MapUtil.getInt(meeting, "status");//获取会议状态
        boolean isCreator = Boolean.parseBoolean(MapUtil.getStr(meeting, "isCreator"));//获取创建会议的申请人
        DateTime dateTime = DateUtil.parse(date + " " + start);//对比时间
        DateTime now = DateUtil.date();//获取当前时间
        //TODO

        // if (now.isAfterOrEquals(dateTime.offset(DateField.MINUTE, -20))) {
        //     throw new EmosException("距离会议开始不足20分钟，不能删除会议");
        // }
        //只能申请人删除该会议
        if (!isCreator) {
            throw new EmosException("只能申请人删除该会议");
        }
        //待审批和未开始的会议可以删除
        if (status == 1 || status == 3) {
            int rows = meetingDao.deleteMeetingApplication(param);//执行语句
            if (rows == 1) {
                //获取成功信息
                String reason = MapUtil.getStr(param, "reason");
                //调用工作流方法
                meetingWorkflowTask.deleteMeetingApplication(uuid, instanceId, reason);
            }
            return rows;
        } else {
            throw new EmosException("只能删除待审批和未开始的会议");
        }
    }
}
