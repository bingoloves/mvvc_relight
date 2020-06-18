package com.learn.mvvc.base;

import android.accounts.NetworkErrorException;
import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ittianyu.relight.widget.Widget;
import com.ittianyu.relight.widget.native_.FrameWidget;
import com.ittianyu.relight.widget.native_.RecyclerWidget;
import com.ittianyu.relight.widget.stateful.lcee.CommonEmptyWidget;
import com.ittianyu.relight.widget.stateful.lcee.CommonLoadingWidget;
import com.ittianyu.relight.widget.stateful.lcee.LceeStatus;
import com.ittianyu.relight.widget.stateful.lcee.LceeWidget;
import com.learn.mvvc.R;

import java.util.Collections;
import java.util.List;

/**
 * 通用列表组件
 * @param <T>
 */
public abstract class BaseLceeWidget<T> extends LceeWidget {
    private List<T> data = Collections.emptyList();
    private View.OnClickListener reload = v -> reload();
    private RecyclerWidget recyclerWidget;
    private RecyclerView.LayoutManager layoutManager;
    private BaseRvAdapter targetAdapter;

    public BaseLceeWidget(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
        layoutManager = new LinearLayoutManager(context);
    }

    @Override
    protected Widget renderLoading() {
        return new CommonLoadingWidget(context, lifecycle);
    }

    @Override
    protected Widget renderContent() {
        recyclerWidget = renderRecycler();
        return new FrameWidget(context, lifecycle,
                recyclerWidget
        ).matchParent();
    }

    @Override
    protected Widget renderEmpty() {
        return new CommonEmptyWidget(context, lifecycle, "暂无数据", reload);
    }

    @Override
    protected Widget renderError() {
        if (lastError != null) lastError.printStackTrace();
        return new CommonEmptyWidget(context, lifecycle, "网络异常", reload);
    }

    @Override
    protected LceeStatus onLoadData() throws NetworkErrorException {
        data = getTargetData();
        if (data == null || data.isEmpty()){
            return LceeStatus.Empty;
        }
        return LceeStatus.Content;
    }

    private RecyclerWidget renderRecycler() {
        targetAdapter = getTargetAdapter();
        return new RecyclerWidget<BaseRvAdapter>(context, lifecycle)
                .adapter(targetAdapter)
                .matchParent()
                .layoutManager(layoutManager)
                .onUpdate(() -> targetAdapter.setData(data));
    }
    public abstract BaseRvAdapter getTargetAdapter();
    public abstract  List<T> getTargetData();

    /**
     * 设置布局管理器
     * @param layoutManager
     */
    protected void setLayoutManager(RecyclerView.LayoutManager layoutManager){
        this.layoutManager = layoutManager;
        //recyclerWidget.layoutManager(layoutManager);
    }
}
