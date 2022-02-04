package com.example.emos.api.db.dao;

import com.example.emos.api.db.pojo.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbMeetingDao {
    public boolean searchMeetingMembersInSameDept(String uuid);

    public HashMap searchMeetingById(HashMap param);

    //查询会议记录
    public ArrayList<HashMap> searchOfflineMeetingByPage(HashMap param);

    //查询会议总数
    public long searchOfflineMeetingCount(HashMap param);

    //更新会议记录instance_id字段值
    public int updateMeetingInstanceId(HashMap param);

    //新增会议记录
    public int insert(TbMeeting meeting);

    //查询周日历和会议详情
    public ArrayList<HashMap> searchOfflineMeetingInWeek(HashMap param);

    //查询会议信息
    public HashMap searchMeetingInfo(long id);

    //查询正在进行的会议信息
    public HashMap searchCurrentMeetingInfo(long id);

    //珊瑚会议信息
    public int deleteMeetingApplication(HashMap param);
}