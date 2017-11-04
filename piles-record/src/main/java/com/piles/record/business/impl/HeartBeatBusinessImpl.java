package com.piles.record.business.impl;


import com.google.common.primitives.Bytes;
import com.piles.common.business.BaseBusiness;
import com.piles.common.entity.type.ECommandCode;
import com.piles.common.util.BytesUtil;
import com.piles.record.entity.HeartBeatRequest;
import com.piles.record.service.IHeartBeatService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 心跳接口实现
 */
public class HeartBeatBusinessImpl extends BaseBusiness {

    //设置返回报文头命令
    ECommandCode responseCode = ECommandCode.HEART_BEAT_CODE;

    @Resource
    private IHeartBeatService heartBeatService;

    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");


    @Override
    protected byte[] processBody(byte[] bodyBytes) {
        //依照报文体规则解析报文
        HeartBeatRequest heartBeatRequest = HeartBeatRequest.packEntity(bodyBytes);
        //调用底层接口
        Date date= heartBeatService.heartBeat(heartBeatRequest);
        byte[] pileNo = BytesUtil.copyBytes(bodyBytes, 0, 8);
        byte[] result = BytesUtil.str2Bcd(sdf.format(date));
        byte[] responseBody = Bytes.concat(pileNo,result);
        //组装返回报文体
        return responseBody;
    }
}
