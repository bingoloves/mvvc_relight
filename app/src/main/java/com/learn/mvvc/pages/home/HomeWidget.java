package com.learn.mvvc.pages.home;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.RelativeLayout;
import com.ittianyu.relight.utils.StateUtils;
import com.ittianyu.relight.widget.native_.RelativeWidget;
import com.ittianyu.relight.widget.native_.TextWidget;
import com.ittianyu.relight.widget.stateful.StatefulWidget;
import com.ittianyu.relight.widget.stateful.state.State;
import com.learn.mvvc.R;

/**
 * Created by Administrator on 2020/6/17 0017.
 */

public class HomeWidget extends StatefulWidget<RelativeLayout, RelativeWidget> {
    //private UserBean user = UserDataSource.getInstance().getUser();
    private TextWidget twId;
    private TextWidget twName;

    public HomeWidget(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }

    @Override
    protected State<RelativeWidget> createState(Context context, Lifecycle lifecycle) {
        twId = new TextWidget(context, lifecycle) {
            @Override
            protected void initProps() {
                super.initProps();
                id(R.id.tw_id);
                textSize(dp(16));
                textColor(Color.BLACK);
            }
        };
        twName = new TextWidget(context, lifecycle);
        twName.id(R.id.tw_name);
//        RelativeWidget root = new RelativeWidget(context, lifecycle,
//                new WidgetAndProps(twId, new Prop(RelativeLayout.CENTER_IN_PARENT, Prop.TRUE)),
//                new WidgetAndProps(twName, new Prop(RelativeLayout.BELOW, R.id.tw_id),
//                        new Prop(RelativeLayout.CENTER_HORIZONTAL, Prop.TRUE))
//        );
        RelativeWidget root = new RelativeWidget(context, lifecycle,
                twId.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeWidget.Prop.TRUE),
                twName.addRule(RelativeLayout.BELOW, R.id.tw_id)
                        .addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeWidget.Prop.TRUE)
        );
        return StateUtils.create(root);
    }

    @Override
    public void initWidget(RelativeWidget widget) {
        widget
                .matchParent()
                .onClickListener(callPhone);
        update();
    }

    /**
     * 拨打电话
     */
    private View.OnClickListener tel = v -> {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:12306"));
        context.startActivity(intent);
    };
    /**
     * 拨打电话
     */
    private View.OnClickListener callPhone = v -> {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri parse = Uri.parse("tel:12306");
        intent.setData(parse);
        context.startActivity(intent);
    };
    @Override
    public void update() {
        super.update();
        twId.text("123");
        twName.text("456");
    }
}
