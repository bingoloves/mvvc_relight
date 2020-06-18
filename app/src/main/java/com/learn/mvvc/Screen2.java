package com.learn.mvvc;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;

import com.ittianyu.relight.widget.native_.ButtonWidget;
import com.ittianyu.relight.widget.native_.LinearWidget;
import com.ittianyu.relight.widget.native_.TextWidget;
import com.ittianyu.relight.widget.native_.ToolbarWidget;
import com.ittianyu.relight.widget.stateful.navigator.Navigator;

public class Screen2 extends LinearWidget implements OnClickListener {

    private static final int ID_BW_PUSH_FIRST = 1;
    private static final int ID_BW_PUSH_SECOND = 2;
    private static final int ID_BW_POP = 3;

    public Screen2(Context context, Lifecycle lifecycle, String text) {
        super(context, lifecycle);
        this.addChildren(
            new ToolbarWidget(context, lifecycle).backgroundResource(R.color.colorPrimary).title("首页").titleTextColor(Color.WHITE).marginBottom(16.0f),
//            new HomeListWidget(context,lifecycle)
            new ButtonWidget(context, lifecycle).id(ID_BW_PUSH_FIRST).text("push first").onClickListener(this),
            new ButtonWidget(context, lifecycle).id(ID_BW_PUSH_SECOND).text("push second").onClickListener(this),
            new ButtonWidget(context, lifecycle).id(ID_BW_POP).text("pop").onClickListener(this),
            new TextWidget(context, lifecycle).text(text).marginTop(50.0f)
        );
    }

    @Override
    protected void initProps() {
        super.initProps();
        matchParent()
            .gravity(Gravity.CENTER_HORIZONTAL)
            .backgroundColor(Color.WHITE)
            .orientation(vertical);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case ID_BW_PUSH_FIRST:
                Navigator.push(Router.Route.name, Router.Route.index);
                break;
            case ID_BW_PUSH_SECOND:
                Navigator.push(Router.Route.name, Router.Route.home);
                break;
            case ID_BW_POP:
                Navigator.pop(Router.Route.name);
                break;
        }
    }
}
