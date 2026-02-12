package com.bellmate.utils;

import android.content.Context;
import android.widget.TextView;

import io.noties.markwon.Markwon;
import io.noties.markwon.ext.tables.TablePlugin;
import io.noties.markwon.image.ImagesPlugin;
import io.noties.markwon.image.glide.GlideImagesPlugin;

/**
 * bellmate
 * com.bellmate.utils
 * MarkDownUtil <br>
 * Markdown 工具类
 *
 * @author Ablecisi
 * @version 1.0
 * 2026/2/12
 * 星期四
 * 12:44
 */
public class MarkDownUtil {

    private MarkDownUtil() {
        // 私有构造函数，防止实例化
    }

    // 获取 Markwon实例
    public static void setMarkdown(Context context, TextView textView, String markdownText) {
        if (textView == null || markdownText == null || markdownText.isEmpty()) {
            return;
        }
        Markwon markwon = Markwon.builder(context)
                .usePlugin(TablePlugin.create(context))
                .usePlugin(ImagesPlugin.create())
                .usePlugin(GlideImagesPlugin.create(context))
                .build();
        // 可以在这里配置 Markwon的样式或插件
        markwon.setMarkdown(textView, markdownText);
    }

    /**
     * 将Markdown文本 转换为HTML
     *
     * @param markdownText Markdown 文本
     * @return 转换后的 HTML文本
     */
    public static String markdownToHtml(String markdownText, Context context) {
        Markwon markwon = Markwon.create(context);
        if (markdownText == null || markdownText.isEmpty()) {
            return "";
        }

        // 使用第三方库或自定义方法将Markdown转换为HTML
        // 这里可以使用如CommonMark等库进行转换
        return "<html><body>" + markdownText + "</body></html>"; // 简单示例，实际应使用Markdown解析库
    }

    /**
     * 将Markdown文本转换为 Spannable
     *
     * @param markdownText Markdown 文本
     * @return 转换后的 Spannable对象
     */
    public static CharSequence markdownToSpannable(String markdownText, Context context) {
        Markwon markwon = Markwon.create(context);
        if (markdownText == null || markdownText.isEmpty()) {
            return "";
        }

        // 使用Markwon将Markdown 转换为Spannable
        return markwon.toMarkdown(markdownText);
    }
}
