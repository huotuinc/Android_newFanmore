/*
 * Copyright (c) 2014,KJFrameForAndroid Open Source Project,张涛.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cy.com.morefan.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 上拉加载ListView的底部<br>
 * 
 * <b>创建时间</b> 2014-7-5
 * 
 * @author kymjs (https://github.com/kymjs)
 * @version 1.0
 */
public class KJListViewFooter extends LinearLayout
{
    /** 头部刷新状态 */
    public enum LoadMoreState
    {
        STATE_NORMAL, // 原样
        STATE_READY, // 完成
        STATE_LOADING // 正在刷新
    }

    private String refreshing = "上拉加载更多";// 正在刷新

    private String refreshed = "加载完成";// 刷新完成

    private String refresh = "上拉加载";// 开始刷新

    RelativeLayout contentView;

    private TextView hintView;

    public KJListViewFooter(Context context)
    {
        super(context);
        initView(context);
    }

    /**
     * 初始化底部组件
     */
    private void initView(Context context)
    {
        // 初始情况，设置上拉加载view高度为0
        RelativeLayout.LayoutParams fp = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 0);
        contentView = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);        
        LinearLayout l = new LinearLayout(context);
        l.setGravity(Gravity.CENTER);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setLayoutParams(params);
        hintView = new TextView(context);
        hintView.setText("");
        hintView.setGravity(Gravity.CENTER);
        l.addView(hintView);
        contentView.addView(l);
        addView(contentView, fp);
        setGravity(Gravity.BOTTOM);
    }

    /**
     * 设置底部组件的显示
     * 
     * @param state
     *            底部组件当前状态
     */
    public void setState(LoadMoreState state)
    {
        hintView.setVisibility(View.INVISIBLE);
        if (state == LoadMoreState.STATE_READY)
        {
            hintView.setVisibility(View.VISIBLE);
            hintView.setText(refreshing);
        } else if (state == LoadMoreState.STATE_LOADING)
        {
            hintView.setVisibility(View.VISIBLE);
            hintView.setText(refreshing);
        } else
        {
            hintView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置底边距
     * 
     * @param height
     */
    public void setBottomMargin(int height)
    {
        if (height < 0)
        {
            return;
        }
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        params.bottomMargin = height;
        contentView.setLayoutParams(params);
    }

    /**
     * 获取底边距
     */
    public int getBottomMargin()
    {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        return params.bottomMargin;
    }

    /**
     * 普通状态
     */
    public void normal()
    {
        hintView.setVisibility(View.GONE);
    }

    /**
     * 加载状态
     */
    public void loading()
    {
        hintView.setVisibility(View.GONE);
    }

    /**
     * 没有更多时隐藏底部
     */
    public void hide()
    {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        params.height = 0;
        contentView.setLayoutParams(params);
    }

    /**
     * 显示底部
     */
    public void show()
    {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        params.height = LayoutParams.WRAP_CONTENT;
        contentView.setLayoutParams(params);
    }

    /**
     * 设置上拉刷新时的文字
     * 
     * @param refreshing
     *            正在刷新的状态
     */
    public void setRefreshing(String refreshing)
    {
        this.refreshing = refreshing;
    }
}
