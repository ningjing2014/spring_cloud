package com.ln.xproject.job.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.job.service.TaskTimerParamService;
import com.ln.xproject.job.vo.TaskTimerParamRequestVo;
import com.ln.xproject.job.vo.TaskTimerParamVo;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/taskTimerParam")
@Slf4j
public class TaskTimerParamController {

    @Autowired
    private TaskTimerParamService taskTimerParamService;

    @RequestMapping(value = "/findByTaskTimerId", method = RequestMethod.POST)
    public JsonResultVo findByTaskTimerId(@RequestBody TaskTimerParamRequestVo messageVo) {
        // 返回结果
        List<TaskTimerParamVo> responseVo = new ArrayList<TaskTimerParamVo>();
        try {
            // 查询列表
            responseVo = taskTimerParamService.findTaskTimerParamByTaskTimerId(messageVo);
            log.info("根据taskTimerId查询taskTimerParam列表成功。");
            return JsonResultVo.addRows(responseVo);
        } catch (Exception e) {
            log.error("根据taskTimerId查询taskTimerParam列表异常", e);
            return JsonResultVo.error(JsonResultVo.ERROR, "获取列表失败");
        }
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public JsonResultVo updateTaskTimerParam(@RequestBody TaskTimerParamRequestVo messageVo) {
        try {
            taskTimerParamService.updateTaskTimerParam(messageVo);
            log.info("taskTimerParam修改成功messageVo:{}", messageVo);
            return JsonResultVo.success();
        } catch (Exception e) {
            log.error("taskTimerParam修改失败", e);
            return JsonResultVo.error(JsonResultVo.ERROR, "taskTimerParam修改失败");
        }
    }

}
