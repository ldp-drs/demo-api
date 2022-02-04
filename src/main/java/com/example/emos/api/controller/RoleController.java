package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.db.sql.SqlExecutor;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbRole;
import com.example.emos.api.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("/role")
@Tag(name = "RoleController", description = "角色Web接口")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/searchAllRole")
    @Operation(summary = "查询所有角色")
    public R searchAllRole() {
        ArrayList<HashMap> list = roleService.searchAllRole();
        return R.ok().put("list", list);
    }

    @PostMapping("/searchById")
    @Operation(summary = "根据ID查询角色")
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    public R searchById(@Valid @RequestBody SearchRoleByIdForm form) {
        HashMap map = roleService.searchById(form.getId());
        return R.ok(map);
    }

    //查询角色分页信息
    @PostMapping("/searchRoleByPage")
    @Operation(summary = "查询角色分页信息")
    @SaCheckPermission(value = {"ROOT", "ROLE:SELECT"}, mode = SaMode.OR)
    public R searchRoleByPage(@Valid @RequestBody SearchRoleByPageForm form) {
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;//计算起始页
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);//将HashMap转换成json字符串对象
        param.put("start", start);//写入起始页
        PageUtils pageUtils = roleService.searchRoleByPage(param);//获取角色信息
        return R.ok().put("page", pageUtils);
    }

    //新增角色信息
    @PostMapping("/insert")
    @Operation(summary = "添加角色")
    @SaCheckPermission(value = {"ROOT", "ROLE:INSERT"}, mode = SaMode.OR)
    public R insert(@Valid @RequestBody InsertRoleForm form) {
        TbRole role = new TbRole();//初始化类
        role.setRoleName(form.getRoleName());//写入数据
        role.setPermissions(JSONUtil.parseArray(form.getPermissions()).toString());//转换对象后写入数据
        role.setDesc(form.getDesc());//写入数据
        int rows = roleService.insert(role);//执行插入方法
        return R.ok().put("rows", rows);
    }

    //修改角色信息
    @PostMapping("/update")
    @Operation(summary = "修改角色")
    @SaCheckPermission(value = {"ROOT", "ROLE:UPDATE"}, mode = SaMode.OR)
    public R update(@Valid @RequestBody UpdateRoleForm form) {
        TbRole role = new TbRole();
        role.setId(form.getId());
        role.setRoleName(form.getRoleName());
        role.setPermissions(JSONUtil.parseArray(form.getPermissions()).toString());
        role.setDesc(form.getDesc());
        int rows = roleService.update(role);
        //如果用户修改成功，并且用户修改了该角色的关联权限，则把该角色关联的用户踢下线
        if (rows == 1 && form.getChanged()) {
            ArrayList<Integer> list = roleService.searchUserIdByRoleId(form.getId());//获取该角色下关联的用户
            list.forEach(userId -> {
                StpUtil.logoutByLoginId(userId);
            });
        }
        return R.ok().put("rows", rows);
    }

    //删除角色信息
    @PostMapping("/deleteRoleByIds")
    @Operation(summary = "删除角色记录")
    @SaCheckPermission(value = {"ROOT", "ROLE:DELETE"}, mode = SaMode.OR)
    public R deleteRoleByIds(@Valid @RequestBody DeleteRoleByIdsForm form) {
        int rows = roleService.deleteRoleByIds(form.getIds());
        return R.ok().put("rows", rows);
    }
}
