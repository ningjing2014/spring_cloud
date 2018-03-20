package com.ln.xproject.api.service;

import com.ln.xproject.base.vo.ApiBaseRequestVo;

public interface SignService {

    <T extends ApiBaseRequestVo> String sign(T object, String partner);

    <T extends ApiBaseRequestVo> boolean verify(T object);

}
