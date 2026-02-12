package com.bellmate.handler;

import android.content.Context;
import android.util.Log;

import com.bellmate.constant.StatusCodeConstant;
import com.bellmate.result.Result;
import com.bellmate.utils.HttpClient;
import com.bellmate.utils.JsonUtil;
import com.bellmate.utils.LoginInfoUtil;

/**
 * ailianlian
 * com.ailianlian.ablecisi.handler
 * TokenExpiredHandler <br>
 * 处理Token过期的逻辑
 * 用于在收到Token过期的响应时，清除本地登录信息并跳转到登录页面
 * @author Ablecisi
 * @version 1.0
 * 2025/6/16
 * 星期一
 * 21:14
 */
public class TokenExpiredHandler extends AbstractHandler {
    private static final String TAG = "TokenExpiredHandler";
    @Override
    public void handle(Context context, String response, HttpClient.HttpCallback callback) {
        Result<?> result = JsonUtil.fromJson(response, Result.class);
        Log.i(TAG,"TokenExpiredHandler: 处理Token过期相关逻辑。");
        if (result != null && result.getCode() == StatusCodeConstant.TOKEN_EXPIRED.getCode()) {
            // 可选：不再回调业务层
            if (callback != null) {
                callback.onFailure(StatusCodeConstant.TOKEN_EXPIRED.getMsg()+"，请重新登录");
            }
        } else {
            super.handle(context, response, callback);
        }
    }
}
