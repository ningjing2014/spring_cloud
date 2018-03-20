package com.ln.xproject.system.service;

import java.util.List;
import java.util.Set;

import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.system.constants.ChannelType;
import com.ln.xproject.system.model.SysUserChannel;
import com.ln.xproject.system.vo.ChannelVo;

public interface SysUserChannelService extends BaseService<SysUserChannel> {

    /**
     * 获取业务线列表
     *
     * @return
     */
    List<ChannelVo> getChannels();

    /**
     * 获取业务线列表
     *
     * @return
     */
    Set<ChannelVo> getChannelByUserId(Long userId);

    /**
     * 设置用户业务线
     * 
     * @param userId
     * @param channels
     */
    void setUserChannel(Long userId, Set<String> channels);

    /**
     * 校验业务线合法性
     * 
     * @param channelType
     */
    ChannelType checkChannelType(String channelType);

}
