package com.learn.mvvc.base;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import com.ittianyu.relight.widget.stateless.StatelessWidget;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用适配器
 * @param <T>
 */

public abstract class BaseRvAdapter<T,M extends StatelessWidget> extends RecyclerView.Adapter<BaseRvAdapter.ViewHolder> {
    private List<T> data = new ArrayList<>();
    private Lifecycle lifecycle;

    public BaseRvAdapter(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        M itemWidget = getItemWidget(parent.getContext(), lifecycle);
        return new ViewHolder(itemWidget);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        T item = getData().get(position);
        onBindViewHolder((M) holder.widget,item);
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                onItemClickListener.onClick(v, position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<T> data) {
        if (null != data){
            this.data = data;
        } else{
            this.data.clear();
        }
        notifyDataSetChanged();
    }

    public void addData(List<T> data) {
        if (null == data || data.size() == 0) return;
        int index = getItemCount();
        this.data.addAll(data);
        notifyItemRangeInserted(index, data.size());
    }

    public static class ViewHolder<M extends StatelessWidget> extends RecyclerView.ViewHolder {
        M widget;
        public ViewHolder(M widget) {
            super(widget.render());
            this.widget = widget;
        }
    }

    /**
     * 获取当前数据集
     * @return
     */
    public List<T> getData() {
        return data;
    }

    /**
     * 列表子项组件对象的创建
     * @param context
     * @param lifecycle
     * @return
     */
    public abstract M getItemWidget(Context context, Lifecycle lifecycle);


    /**
     *
     * @param widget
     * @param item
     */
    public abstract void onBindViewHolder(M widget, T item);

    /**
     * item点击事件
     */
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }
}
