package com.bellmate.handler;

import android.content.Context;

import com.bellmate.constant.StatusCodeConstant;
import com.bellmate.result.Result;
import com.bellmate.utils.HttpClient;
import com.bellmate.utils.JsonUtil;

/**
 * ailianlian
 * com.ailianlian.ablecisi.handler
 * StatusCodeHandler <br>
 *
 * @author Ablecisi
 * @version 1.0
 * 2025/6/16
 * 星期一
 * 21:50
 */
public class StatusCodeHandler extends AbstractHandler {
    @Override
    public void handle(Context context, String response, HttpClient.HttpCallback callback) {
        Result<?> result = JsonUtil.fromJson(response, Result.class);
        if (result != null && result.getCode() != StatusCodeConstant.SUCCESS.getCode()) {
            for(StatusCodeConstant code : StatusCodeConstant.values()){
                if(result.getCode() == code.getCode()){
                    if(callback != null){
                        callback.onFailure("出错码："+code.getCode()+"错误消息："+code.getMsg()+"\n回传结果消息："+result.getMsg());
                        break;
                    }
                }
            }
        } else {
            super.handle(context, response, callback);
        }
    }
}
