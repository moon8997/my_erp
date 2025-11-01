package com.myproject.caseNara.controller;

import com.myproject.caseNara.model.Account;
import com.myproject.caseNara.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountMapper accountMapper;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Account account) {
        try {
            // 아이디 중복 체크
            int dup = accountMapper.countById(account.getId());
            if (dup > 0) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "이미 사용 중인 아이디입니다."));
            }
            // 비밀번호 암호화 적용 (jBCrypt 사용)
            String hashed = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
            account.setPassword(hashed);
            int result = accountMapper.insertAccount(account);
            
            if (result > 0) {
                return ResponseEntity.ok(Map.of("success", true));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "회원가입에 실패했습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account loginRequest,
                                   HttpServletRequest request) {
        try {
            Account found = accountMapper.findById(loginRequest.getId());
            if (found == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "아이디 또는 비밀번호가 일치하지 않습니다."));
            }
            
            boolean matches;
            String stored = found.getPassword();
            // BCrypt 해시로 저장된 경우 체크, 아니면 평문 비교(구버전 호환)
            if (stored != null && stored.startsWith("$2")) {
                matches = BCrypt.checkpw(loginRequest.getPassword(), stored);
            } else {
                matches = loginRequest.getPassword().equals(stored);
            }
            if (matches) {
                // 로그인 성공: 세션에 사용자 정보 저장
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", found.getId());
                
                return ResponseEntity.ok(Map.of("success", true, "userId", found.getId()));
            } else {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "아이디 또는 비밀번호가 일치하지 않습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

}
