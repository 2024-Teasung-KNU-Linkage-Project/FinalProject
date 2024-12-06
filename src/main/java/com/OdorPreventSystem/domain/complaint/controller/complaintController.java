package com.OdorPreventSystem.domain.complaint.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@CrossOrigin
@Controller
@RequestMapping("/complaint")
public class complaintController {

    // 초기 화면 렌더링
    @GetMapping("/")
    public String complaintPage() throws Exception {
        return "complaint/total";
    }

}
