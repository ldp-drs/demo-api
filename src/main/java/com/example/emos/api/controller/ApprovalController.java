package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.SearchApprovalContentForm;
import com.example.emos.api.controller.form.SearchTaskByPageForm;
import com.example.emos.api.exception.EmosException;
import com.example.emos.api.service.ApprovalService;
import com.example.emos.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/approval")
@Tag(name = "ApprovalController", description = "任务审批Web接口")
@Slf4j
public class ApprovalController {

    @Value("${workflow.url}")
    private String workflow;

    @Value("${emos.code}")
    private String code;

    @Value("${emos.tcode}")
    private String tcode;

    @Autowired
    private ApprovalService approvalService;

    @Autowired
    private UserService userService;

    @PostMapping("/searchTaskByPage")
    @Operation(summary = "查询分页任务列表")
    @SaCheckPermission(value = {"ROOT", "WORKFLOW:APPROVAL", "FILE:ARCHIVE"}, mode = SaMode.OR)
    public R searchTaskByPage(@Valid @RequestBody SearchTaskByPageForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        int userId = StpUtil.getLoginIdAsInt();//获取用户id
        param.put("userId", userId);
        param.put("role", userService.searchUserRoles(userId));//获取用户角色
        PageUtils pageUtils = approvalService.searchTaskByPage(param, userId);//同步线程
        return R.ok().put("page", pageUtils);
    }

    //获取审批信息
    @PostMapping("/searchApprovalContent")
    @Operation(summary = "查询任务详情")
    @SaCheckPermission(value = {"ROOT", "WORKFLOW:APPROVAL", "FILE:ARCHIVE"}, mode = SaMode.OR)
    public R searchApprovalContent(@Valid @RequestBody SearchApprovalContentForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        int userId = StpUtil.getLoginIdAsInt();
        param.put("userId", userId);
        param.put("role", userService.searchUserRoles(userId));
        HashMap content = approvalService.searchApprovalContent(param);
        return R.ok().put("content", content);
    }

    //获取bmp审批流程图片
    @GetMapping("/searchApprovalBpmn")
    @Operation(summary = "获取BPMN图形")
    @SaCheckPermission(value = {"ROOT", "WORKFLOW:APPROVAL", "FILE:ARCHIVE"}, mode = SaMode.OR)
    public void searchApprovalBpmn(String instanceId, HttpServletResponse response) {
        if (StrUtil.isBlankIfStr(instanceId)) {
            throw new EmosException("instanceId不能为空");
        }
        // ArrayList<HashMap> bpmnUrl = approvalService.searchApprovalBpmn(instanceId);
        HashMap param = new HashMap() {
            {
                put("code", code);
                put("tcode", tcode);
                put("instanceId", instanceId);
            }
        };
        String url = workflow + "/workflow/searchApprovalBpmn";
        HttpResponse resp = HttpRequest.post(url).header("Content-Type", "application/json").body(JSONUtil.toJsonStr(param)).execute();
        if (resp.getStatus() == 200) {
            try (InputStream in = resp.bodyStream();
                 BufferedInputStream bin = new BufferedInputStream(in);
                 OutputStream out = response.getOutputStream();
                 BufferedOutputStream bout = new BufferedOutputStream(out)) {
                IOUtils.copy(bin, bout);
            } catch (Exception e) {
                log.error("执行异常", e);
            }
        } else {
            throw new EmosException("获取工作流BPMN失败");
        }
    }
}
