package com.myproject.caseNara;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {
    @GetMapping(value = {"/{path:[^\\.]*}", "/"})
    public String forward() {
        return "forward:/index.html";
    }

    // 인증된 사용자가 /login 접근 시 메인으로 리다이렉트
    @GetMapping("/login")
    public String loginGate(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        boolean authenticated = session != null && session.getAttribute("userId") != null;
        if (authenticated) {
            return "redirect:/";
        }
        return "forward:/index.html";
    }
}
