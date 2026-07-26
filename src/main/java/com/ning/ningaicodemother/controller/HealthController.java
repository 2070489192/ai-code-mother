package com.ning.ningaicodemother.controller;


import com.ning.ningaicodemother.common.BaseResponse;
import com.ning.ningaicodemother.common.ResultUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {
    @GetMapping
    public BaseResponse<String> health(){
        return ResultUtil.success("成功");
    }
}
