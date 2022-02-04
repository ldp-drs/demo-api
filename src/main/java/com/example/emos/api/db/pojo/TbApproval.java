package com.example.emos.api.db.pojo;

import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TbApproval implements Serializable {

    private String creatorName;

    private String title;

    private String type;

    private boolean filing;

    private String processId;

    private String taskId;

    private Date createDate;

    private String status;
}
