package com.ittianyu.relight.widget.stateless;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.view.View;

import com.ittianyu.relight.widget.ContainerWidget;
import com.ittianyu.relight.widget.Widget;

public abstract class StatelessWidget<V extends View, T extends Widget<V>>
        extends Widget<V> implements ContainerWidget<V, T> {

    protected T widget;

    public StatelessWidget(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }

    protected abstract T build(Context context, Lifecycle lifecycle);

    @Override
    public V render() {
        if (widget != null) {
            return widget.render();
        }

        widget = build(context, lifecycle);
        if (widget == null)
            throw new IllegalStateException("can't build widget");
        V view = widget.render();
        if (view == null)
            throw new IllegalStateException("can't render view");
        initWidget(widget);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        } else {
            onStart();
        }
        return view;
    }

    @Override
    public T getInnerWidget() {
        return widget;
    }

    @Override
    public void update() {
        widget.update();
        super.update();
    }

    @Override
    public void initWidget(T widget) {
    }
}
