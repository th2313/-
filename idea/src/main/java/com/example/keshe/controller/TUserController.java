package com.example.keshe.controller;

import com.example.keshe.dto.LoginDTO;
import com.example.keshe.dto.RegisterDTO;
import com.example.keshe.dto.Result;
import com.example.keshe.entity.TUser;
import com.example.keshe.service.ITUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jaun
 * @since 2025-09-21
 */
@RestController
@RequestMapping("/tUser")
public class TUserController {

    @Autowired
    private ITUserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<TUser> login(@RequestBody LoginDTO loginDTO) {
        try {
            System.out.println("=== 开始登录处理 ===");
            System.out.println("1. 接收到的登录数据: " + loginDTO.getUname() + " / " + loginDTO.getUpassword());

            // 打印所有用户，检查数据库连接和数据
            List<TUser> allUsers = userService.list();
            System.out.println("2. 数据库中的所有用户:");
            for (TUser u : allUsers) {
                System.out.println("   - UName: '" + u.getUname() + "'");
                System.out.println("   - UPassword: '" + u.getUpassword() + "'");
                System.out.println("   - UType: '" + u.getUtype() + "'");
            }

            // 执行查询
            System.out.println("3. 执行查询: UName = '" + loginDTO.getUname() + "'");
            List<TUser> users = userService.lambdaQuery()
                    .eq(TUser::getUname, loginDTO.getUname())
                    .list();

            System.out.println("4. 查询结果数量: " + users.size());

            if (users.isEmpty()) {
                System.out.println("5. 错误: 用户不存在");
                return Result.error(400, "用户不存在");
            }

            // 密码验证
            TUser matchedUser = null;
            for (TUser user : users) {
                System.out.println("6. 密码比较:");
                System.out.println("   - 输入密码: '" + loginDTO.getUpassword() + "'");
                System.out.println("   - 数据库密码: '" + user.getUpassword() + "'");
                System.out.println("   - 是否匹配: " + user.getUpassword().equals(loginDTO.getUpassword()));

                if (user.getUpassword().equals(loginDTO.getUpassword())) {
                    matchedUser = user;
                    break;
                }
            }

            if (matchedUser == null) {
                System.out.println("7. 错误: 密码不匹配");
                return Result.error(400, "密码错误");
            }

            System.out.println("7. 登录成功: " + matchedUser.getUname());
            matchedUser.setUpassword(null);
            return Result.success(matchedUser);

        } catch (Exception e) {
            System.out.println("8. 异常: " + e.getMessage());
            e.printStackTrace();
            return Result.fail("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<TUser> register(@RequestBody RegisterDTO registerDTO) {
        try {
            System.out.println("=== 开始注册处理 ===");
            System.out.println("1. 接收到的注册数据: " + registerDTO.getUname() + " / " + registerDTO.getUpassword() + " / " + registerDTO.getUtype());

            // 检查用户名是否已存在
            TUser existingUser = userService.lambdaQuery()
                    .eq(TUser::getUname, registerDTO.getUname())
                    .one();

            if (existingUser != null) {
                System.out.println("2. 错误: 用户名已存在");
                return Result.error(400, "用户名已存在");
            }

            // 创建新用户
            TUser newUser = new TUser();
            newUser.setUname(registerDTO.getUname());
            newUser.setUpassword(registerDTO.getUpassword());
            // 设置用户类型，默认为"普通用户"
            newUser.setUtype(registerDTO.getUtype() != null ? registerDTO.getUtype() : "普通用户");

            // 保存用户
            boolean saved = userService.save(newUser);
            if (saved) {
                System.out.println("3. 注册成功: " + newUser.getUname());
                // 清除密码信息后返回
                newUser.setUpassword(null);
                return Result.success(newUser);
            } else {
                System.out.println("3. 错误: 保存用户失败");
                return Result.fail("注册失败");
            }

        } catch (Exception e) {
            System.out.println("4. 异常: " + e.getMessage());
            e.printStackTrace();
            return Result.fail("注册失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有用户（测试用）
     */
    @GetMapping("/list")
    public Result<List<TUser>> listAllUsers() {
        try {
            List<TUser> users = userService.list();
            System.out.println("获取所有用户，数量: " + users.size());
            return Result.success(users);
        } catch (Exception e) {
            return Result.fail("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 测试字段映射（调试用）
     */
    @GetMapping("/test-field-mapping")
    public Result<Object> testFieldMapping() {
        try {
            System.out.println("=== 测试字段映射 ===");

            // 获取所有用户
            List<TUser> users = userService.list();

            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("totalUsers", users.size());

            List<java.util.Map<String, String>> userList = new java.util.ArrayList<>();
            for (TUser user : users) {
                java.util.Map<String, String> userMap = new java.util.HashMap<>();
                userMap.put("uname", user.getUname());
                userMap.put("upassword", user.getUpassword());
                userMap.put("utype", user.getUtype());
                userList.add(userMap);

                // 打印到控制台
                System.out.println("用户: " + user.getUname() + ", 密码: " + user.getUpassword() + ", 类型: " + user.getUtype());
            }

            result.put("users", userList);
            return Result.success(result);

        } catch (Exception e) {
            return Result.fail("测试失败: " + e.getMessage());
        }
    }

    /**
     * 直接查询用户（调试用）
     */
    @GetMapping("/direct-query/{username}")
    public Result<TUser> directQuery(@PathVariable String username) {
        try {
            System.out.println("直接查询用户: " + username);

            // 使用MyBatis Plus的原始查询
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<TUser> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            queryWrapper.eq("uname", username);  // 直接使用数据库字段名

            TUser user = userService.getOne(queryWrapper);
            System.out.println("直接查询结果: " + user);

            return Result.success(user);
        } catch (Exception e) {
            return Result.fail("查询失败: " + e.getMessage());
        }
    }

    /**
     * 测试接口（调试用）
     */
    @PostMapping("/test-request")
    public Result<Object> testRequest(@RequestBody java.util.Map<String, Object> request) {
        try {
            System.out.println("测试请求接收到的数据: " + request);
            return Result.success(request);
        } catch (Exception e) {
            return Result.fail("测试失败: " + e.getMessage());
        }
    }
}