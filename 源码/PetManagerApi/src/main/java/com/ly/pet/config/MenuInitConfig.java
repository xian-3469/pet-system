package com.ly.pet.config;

import com.ly.pet.entity.Menu;
import com.ly.pet.entity.RoleMenu;
import com.ly.pet.mapper.MenuMapper;
import com.ly.pet.mapper.RoleMapper;
import com.ly.pet.mapper.RoleMenuMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 系统初始化配置
 * 检查并初始化菜单
 */
@Component
public class MenuInitConfig implements CommandLineRunner {

    @Resource
    private MenuMapper menuMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    @Transactional
    public void run(String... args) {
        // ===== 以下菜单初始化已禁用，如需启用请取消注释 =====
        // 获取管理员角色ID
        // Integer adminRoleId = roleMapper.selectByFlag("ROLE_ADMIN");
        // if (adminRoleId == null) {
        //     System.out.println("===== 未找到管理员角色，无法初始化菜单 =====");
        //     return;
        // }

        // 初始化流浪动物救助菜单
        // initSalvationMenu(adminRoleId);

        // 初始化宠物走失管理菜单
        // initLostMenu(adminRoleId);
    }

    /**
     * 初始化流浪动物救助菜单（作为宠物管理的子菜单）
     */
    private void initSalvationMenu(Integer adminRoleId) {
        List<Menu> allMenus = menuMapper.selectList(null);

        Menu salvationMenu = allMenus.stream()
            .filter(m -> m.getName() != null && m.getName().equals("流浪动物救助"))
            .findFirst().orElse(null);

        if (salvationMenu == null) {
            Menu menu = new Menu();
            menu.setName("流浪动物救助");
            menu.setPath("/salvationAdmin");
            menu.setIcon("el-icon-first-aid-kit");
            menu.setPagePath("SalvationAdmin");
            menu.setSortNum("25");
            menu.setDescription("流浪动物救助管理");
            menu.setPid(25);  // 设置父菜单为宠物管理
            menuMapper.insert(menu);

            RoleMenu roleMenu = new RoleMenu(adminRoleId, menu.getId());
            roleMenuMapper.insert(roleMenu);
            System.out.println("===== 流浪动物救助菜单初始化完成 =====");
        } else {
            // 如果菜单存在但不是子菜单，更新其父菜单
            if (salvationMenu.getPid() == null || salvationMenu.getPid() != 25) {
                salvationMenu.setPid(25);
                menuMapper.updateById(salvationMenu);
                System.out.println("===== 流浪动物救助菜单已更新为宠物管理子菜单 =====");
            } else {
                System.out.println("===== 流浪动物救助菜单已存在且是宠物管理子菜单 =====");
            }
        }
    }

    /**
     * 初始化宠物走失管理菜单（作为宠物管理的子菜单）
     */
    private void initLostMenu(Integer adminRoleId) {
        List<Menu> allMenus = menuMapper.selectList(null);

        Menu lostMenu = allMenus.stream()
            .filter(m -> m.getName() != null && m.getName().equals("宠物走失管理"))
            .findFirst().orElse(null);

        if (lostMenu == null) {
            Menu menu = new Menu();
            menu.setName("宠物走失管理");
            menu.setPath("/lostAdmin");
            menu.setIcon("el-icon-location");
            menu.setPagePath("LostAdmin");
            menu.setSortNum("26");
            menu.setDescription("宠物走失信息管理");
            menu.setPid(25);  // 设置父菜单为宠物管理
            menuMapper.insert(menu);

            RoleMenu roleMenu = new RoleMenu(adminRoleId, menu.getId());
            roleMenuMapper.insert(roleMenu);
            System.out.println("===== 宠物走失管理菜单初始化完成 =====");
        } else {
            // 如果菜单存在但不是子菜单，更新其父菜单
            if (lostMenu.getPid() == null || lostMenu.getPid() != 25) {
                lostMenu.setPid(25);
                menuMapper.updateById(lostMenu);
                System.out.println("===== 宠物走失管理菜单已更新为宠物管理子菜单 =====");
            } else {
                System.out.println("===== 宠物走失管理菜单已存在且是宠物管理子菜单 =====");
            }
        }
    }
}
