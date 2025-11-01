package com.myproject.caseNara.controller;

import com.myproject.caseNara.mapper.MenuMapper;
import com.myproject.caseNara.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Autowired
    private MenuMapper menuMapper;

    // 모든 메뉴 조회
    @GetMapping
    public ResponseEntity<List<Menu>> getAllMenus() {
        try {
            List<Menu> menus = menuMapper.getAllMenus();
            return ResponseEntity.ok(menus);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 특정 메뉴 조회
    @GetMapping("/{menuCode}")
    public ResponseEntity<?> getMenuByCode(@PathVariable Integer menuCode) {
        try {
            Menu menu = menuMapper.getMenuByCode(menuCode);
            if (menu != null) {
                return ResponseEntity.ok(menu);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage() != null ? e.getMessage() : "Unknown error"));
        }
    }
}