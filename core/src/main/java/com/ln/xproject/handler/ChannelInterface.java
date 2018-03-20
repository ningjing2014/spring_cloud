package com.ln.xproject.handler;

import java.util.Map;

import com.ln.xproject.job.constants.JobMachineStatus;

public interface ChannelInterface {

    public String getChannel();

    public JobMachineStatus getJobMachineStatus();

    public void run(BaseHandler baseHandler, Map<String, Object> paramMap);
}
