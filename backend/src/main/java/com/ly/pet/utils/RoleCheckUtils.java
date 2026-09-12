package com.ly.pet.utils;

import cn.hutool.core.util.StrUtil;
import com.ly.pet.common.RoleEnum;
import com.ly.pet.entity.User;
import com.ly.pet.exception.ServiceException;

import java.util.Arrays;
import java.util.List;

/**
 * 统一角色权限校验工具
 */
public class RoleCheckUtils {

    private RoleCheckUtils() {
    }

    public static User assertLogin() {
        User currentUser = TokenUtils.getCurrentUser();
        if (currentUser == null) {
            throw new ServiceException("401", "登录状态已失效，请重新登录");
        }
        return currentUser;
    }

    /**
     * 校验当前用户是否命中任一角色（支持历史角色名兼容）
     */
    public static void assertAnyRole(User user, String... roles) {
        if (user == null || StrUtil.isBlank(user.getRole())) {
            throw new ServiceException("403", "无权限访问");
        }
        List<String> roleList = Arrays.asList(roles);
        if (!roleList.contains(user.getRole())) {
            throw new ServiceException("403", "无权限访问");
        }
    }

    public static void assertManageOrderRole(User user) {
        assertAnyRole(
                user,
                RoleEnum.ROLE_ADMIN.name(),
                RoleEnum.ROLE_SYS_ADMIN.name(),
                RoleEnum.ROLE_ORG_ADMIN.name()
        );
    }
}
