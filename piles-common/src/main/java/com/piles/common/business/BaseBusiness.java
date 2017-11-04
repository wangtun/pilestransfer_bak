package com.piles.common.business;

import com.google.common.primitives.Bytes;
import com.piles.common.entity.SocketBaseDTO;
import com.piles.common.entity.type.ECommandCode;
import com.piles.common.util.BytesUtil;
import com.piles.common.util.CRC16Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseBusiness implements IBusiness{
    //返回编码
    ECommandCode responseCode;

//    public BaseBusiness(ECommandCode responseCode) {
//        this.responseCode = responseCode;
//    }

    //接收请求处理
    public boolean preProcess(byte[] msg){
        return false;
    }

    @Override
    public SocketBaseDTO process(byte[] msg) {
        log.info("接收到请求,请求体为:"+msg);
        //消息头6个，校验2个
        if(msg.length<=8){
            //TODO 返回失败
            log.error("消息格式有误，总长度小于等于8");
            return null;
        }
        byte[] lenBytes = BytesUtil.copyBytes(msg, 4, 2);
        //消息体长度
        int len = BytesUtil.bytesToInt(lenBytes, 0);
        if(msg.length!=(len+8)){
            //TODO 返回失败
            log.error("消息体长度与消息头中标记的长度不一致");
            return null;

        }
        //消息流水号
        byte[] orderBytes = BytesUtil.copyBytes(msg, 2, 2);
        int order = BytesUtil.bytesToInt(orderBytes, 0);
        log.info("消息流水号为:{}",order);

        byte[] bodyBytes = BytesUtil.copyBytes(msg, 8, len);
        byte[] responseBody = processBody(bodyBytes);
        byte[] responseMsg = postProcess(responseBody, orderBytes);
        //TODO 修改返回值
        return null;
    }

    protected abstract byte[] processBody(byte[] bodyBytes);

    //返回结果处理
    public byte[] postProcess(byte[] body,byte[] orderBytes){
        //concat head
        byte[] first = new byte[]{0x68};
        byte[] command = BytesUtil.intToBytes(this.responseCode.getCode());
        byte[] len = BytesUtil.intToBytes(body.length);

        byte[] head = Bytes.concat(first, command, orderBytes, len);
        //concat crc校验
        int crcInt = CRC16Util.getCRC(Bytes.concat(command,orderBytes,len));
        byte[] crc=BytesUtil.intToBytes(crcInt);
        byte[] responseMsg = Bytes.concat(head, body, crc);
        return responseMsg;
    }
}
