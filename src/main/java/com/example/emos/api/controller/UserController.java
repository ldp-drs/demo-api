package com.example.emos.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import com.example.emos.api.common.util.PageUtils;
import com.example.emos.api.common.util.R;
import com.example.emos.api.controller.form.*;
import com.example.emos.api.db.pojo.TbUser;
import com.example.emos.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

@RestController
@RequestMapping("/user")
@Tag(name = "UserController", description = "用户Web接口")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 生成登陆二维码的字符串
     */
    @GetMapping("/createQrCode")
    @Operation(summary = "生成二维码Base64格式的字符串")
    public R createQrCode() {
        HashMap map = userService.createQrCode();
        return R.ok(map);
    }

    /**
     * 检测登陆验证码
     *
     * @param form
     * @return
     */
    @PostMapping("/checkQrCode")
    @Operation(summary = "检测登陆验证码")
    public R checkQrCode(@Valid @RequestBody CheckQrCodeForm form) {
        boolean bool = userService.checkQrCode(form.getCode(), form.getUuid());
        return R.ok().put("result", bool);
    }

    @PostMapping("/wechatLogin")
    @Operation(summary = "微信小程序登陆")
    public R wechatLogin(@Valid @RequestBody WechatLoginForm form) {
        HashMap map = userService.wechatLogin(form.getUuid());
        boolean result = (boolean) map.get("result");
        if (result) {
            int userId = (int) map.get("userId");
            StpUtil.setLoginId(userId);
            Set<String> permissions = userService.searchUserPermissions(userId);
            map.remove("userId");
            map.put("permissions", permissions);
        }
        return R.ok(map);
    }

    /**
     * 登陆成功后加载用户的基本信息
     */
    @GetMapping("/loadUserInfo")
    // @PostMapping("/loadUserInfo")
    @Operation(summary = "登陆成功后加载用户的基本信息")
    @SaCheckLogin
    public R loadUserInfo() {
        int userId = StpUtil.getLoginIdAsInt();
        HashMap summary = userService.searchUserSummary(userId);
        return R.ok(summary);
    }

    @PostMapping("/searchById")
    @Operation(summary = "根据ID查找用户")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)
    public R searchById(@Valid @RequestBody SearchUserByIdForm form) {
        HashMap map = userService.searchById(form.getUserId());
        String password = userService.searchByIdPassword(form.getUserId());
        map.put("password", password);
        return R.ok(map);
    }

    @GetMapping("/searchAllUser")
    @Operation(summary = "查询所有用户")
    @SaCheckLogin
    public R searchAllUser() {
        ArrayList<HashMap> list = userService.searchAllUser();
        return R.ok().put("list", list);
    }

    //登录
    @PostMapping("/login")
    @Operation(summary = "登录系统")
    public R login(@Valid @RequestBody LoginForm form) {
        //使用JSONUtil将form内容转换成JsonObject对象，然后再转换成HashMap对象数据
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);
        Integer userId = userService.login(param);//执行sql语句并获取结果
        R r = R.ok().put("result", userId != null ? true : false);//保存需要返回的结果
        if (userId != null) {
            StpUtil.setLoginId(userId);//可以将token字符串给浏览器保存以此来判断是否登录
            Set<String> permissions = userService.searchUserPermissions(userId);//获取该用户的权限集合
            r.put("permissions", permissions);
        }
        return r;
    }

    //忘记密码
    @PostMapping("/updatePassword")
    @SaCheckLogin//判断用户是否登录注解
    @Operation(summary = "修改密码")
    public R updatePassword(@Valid @RequestBody UpdatePasswordForm form) {
        int userId = StpUtil.getLoginIdAsInt();//获取登录的用户id
        HashMap param = new HashMap() {{
            put("userId", userId);
            put("password", form.getPassword());
        }};
        int rows = userService.updatePassword(param);
        return R.ok().put("rows", rows);
    }

    //退出登录
    @GetMapping("/logout")
    @Operation(summary = "退出系统")
    public R logout() {
        StpUtil.logout();
        return R.ok();
    }

    //查询用户分页数据
    @PostMapping("/searchUserByPage")
    @Operation(summary = "查询用户分页记录")
    @SaCheckPermission(value = {"ROOT", "USER:SELECT"}, mode = SaMode.OR)//权限限制
    public R searchUserByPage(@Valid @RequestBody SearchUserByPageForm form) {
        int page = form.getPage();//页数
        int length = form.getLength();//显示的条数
        int start = (page - 1) * length;//转换起始页
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);//将form转换成HashMap对象
        param.put("start", start);//将起始页写入对象中
        PageUtils pageUtils = userService.searchUserByPage(param);//获取数据
        return R.ok().put("page", pageUtils);
    }

    //添加新用户
    @PostMapping("/insert")
    @SaCheckPermission(value = {"ROOT", "USER:INSERT"}, mode = SaMode.OR)
    @Operation(summary = "添加新用户")
    public R insert(@Valid @RequestBody InsertUserForm form) {
        TbUser user = JSONUtil.parse(form).toBean(TbUser.class);//将form转换成映射类对象
        user.setStatus((byte) 1);//设置用户状态
        user.setRole(JSONUtil.parseArray(form.getRole()).toString());//将传入的数据转换成json对象，然后再转成字符串格式
        user.setCreateTime(new Date());//设置时间
        int rows = userService.insert(user);//执行语句
        return R.ok().put("rows", rows);
    }

    //修改用户信息
    @PostMapping("/update")
    @SaCheckPermission(value = {"ROOT", "USER:UPDATE"}, mode = SaMode.OR)
    @Operation(summary = "修改用户")
    public R update(@Valid @RequestBody UpdateUserForm form) {
        HashMap param = JSONUtil.parse(form).toBean(HashMap.class);//将form转换成HashMap对象
        param.replace("role", JSONUtil.parseArray(form.getRole()).toString());//将role数组转成成json对象，然后再转换成json字符串
        int rows = userService.update(param);
        if (rows == 1) {
            StpUtil.logoutByLoginId(form.getUserId());//修改资料后，把该用户踢下线
        }
        return R.ok().put("rows", rows);
    }

    //删除用户信息
    @PostMapping("/deleteUserByIds")
    @SaCheckPermission(value = {"ROOT", "USER:DELETE"}, mode = SaMode.OR)
    @Operation(summary = "删除用户")
    public R deleteUserByIds(@Valid @RequestBody DeleteUserByIdsForm form) {
        String userId = StpUtil.getLoginIdAsString();//获取用户id
        //TODO 无法判断是否是自己的账户
        if (ArrayUtil.contains(form.getIds(),userId)) {//判断用户的id和要删除的id是否相同
            return R.error("您不能删除自己的账户");
        }
        int rows = userService.deleteUserByIds(form.getIds());//执行语句
        if (rows > 0) {
            //把被删除的用户踢下线
            for (Integer id : form.getIds()) {
                StpUtil.logoutByLoginId(id);
            }
        }
        return R.ok().put("rows", rows);
    }
}
