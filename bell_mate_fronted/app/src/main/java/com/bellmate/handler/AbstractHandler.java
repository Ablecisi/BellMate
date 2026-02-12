package com.bellmate.handler;

import android.content.Context;
import android.util.Log;

import com.bellmate.utils.HttpClient;

/**
 * ailianlian
 * com.ailianlian.ablecisi.handler
 * AbstractHandler <br>
 * 抽象处理器类，用于定义处理器的基本结构和方法
 *
 * @author Ablecisi
 * @version 1.0
 * 2025/6/16
 * 星期一
 * 21:13
 */
public abstract class AbstractHandler {
    private static final String TAG = "AbstractHandler";
    private AbstractHandler nextHandler;

    public void setNextHandler(AbstractHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public void handle(Context context, String response, HttpClient.HttpCallback callback) {
        Log.i(TAG,"当下服务器相应内容："+response);
        if (nextHandler != null) {
            nextHandler.handle(context, response, callback);
        }
    }
}
