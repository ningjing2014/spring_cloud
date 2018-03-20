package com.ln.xproject.system.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.system.constants.ChannelType;
import com.ln.xproject.system.model.SysUserChannel;
import com.ln.xproject.system.repository.SysUserChannelRepository;
import com.ln.xproject.system.service.SysUserChannelService;
import com.ln.xproject.system.service.SysUserService;
import com.ln.xproject.system.vo.ChannelVo;
import com.ln.xproject.util.CollectionUtils;

@Service
@Transactional
public class SysUserChannelServiceImpl extends BaseServiceImpl<SysUserChannel, SysUserChannelRepository>
        implements SysUserChannelService {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    @Override
    protected void setRepository(SysUserChannelRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<ChannelVo> getChannels() {
        List<ChannelVo> list = new ArrayList<>();
        for (ChannelType businessType : ChannelType.values()) {
            list.add(new ChannelVo(businessType.toString(), businessType.name()));
        }
        return list;
    }

    @Override
    public Set<ChannelVo> getChannelByUserId(Long userId) {
        Set<SysUserChannel> userChannels = this.repository.findByUserId(userId);
        Set<ChannelVo> rst = new HashSet<>();
        userChannels.forEach(rcd -> {
            rst.add(new ChannelVo(rcd.getChannel().toString(), rcd.getChannel().name()));
        });
        return rst;
    }

    @Override
    public void setUserChannel(Long userId, Set<String> channels) {

        this.repository.deleteByUserId(userId);
        if (CollectionUtils.isNotEmpty(channels)) {
            this.sysUserService.load(userId);
            channels.forEach(rcd -> {
                ChannelType channelType = checkChannelType(rcd);
                SysUserChannel sl = new SysUserChannel();
                sl.setUserId(userId);
                sl.setChannel(channelType);
                this.save(sl);
            });
        }
    }

    @Override
    public ChannelType checkChannelType(String channelType) {
        try {
            return ChannelType.valueOf(channelType);
        } catch (Exception e) {
            throw ExceptionUtils.commonError("业务线不存在");
        }
    }

}
