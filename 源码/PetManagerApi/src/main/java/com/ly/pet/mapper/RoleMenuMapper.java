package com.ly.pet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.pet.entity.RoleMenu;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    @Delete("delete from sys_role_menu where role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") Integer roleId);

    @Select("select menu_id from sys_role_menu where role_id = #{roleId}")
    List<Integer> selectByRoleId(@Param("roleId")Integer roleId);

    @org.apache.ibatis.annotations.Insert("<script>INSERT INTO sys_role_menu (role_id, menu_id) VALUES <foreach collection='list' item='item' separator=','>(#{item.roleId}, #{item.menuId})</foreach></script>")
    void batchInsert(@Param("list") List<RoleMenu> roleMenus);

}
