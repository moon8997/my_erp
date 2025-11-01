package com.myproject.caseNara.mapper;

import com.myproject.caseNara.model.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {
    
    List<Menu> getAllMenus();
    
    Menu getMenuByCode(Integer menuCode);
}