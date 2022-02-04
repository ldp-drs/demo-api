package com.example.emos.api.service;

import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.db.pojo.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;

public interface MeetingService {
    //查询会议记录
    PageUtils searchOfflineMeetingByPage(HashMap param);

    //新增会议记录
    int insert(TbMeeting meeting);

    //查询周日历和会议详情
    ArrayList<HashMap> searchOfflineMeetingInWeek(HashMap param);

    ////根据会议状态查询会议信息
    HashMap searchMeetingInfo(short status, long id);

    //珊瑚会议信息
    int deleteMeetingApplication(HashMap param);
}
