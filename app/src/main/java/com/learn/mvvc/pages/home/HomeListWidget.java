package com.learn.mvvc.pages.home;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import com.ittianyu.relight.widget.native_.LinearWidget;
import com.ittianyu.relight.widget.native_.TextWidget;
import com.ittianyu.relight.widget.stateless.StatelessWidget;
import com.learn.mvvc.base.BaseLceeWidget;
import com.learn.mvvc.base.BaseRvAdapter;
import java.util.Arrays;
import java.util.List;

public class HomeListWidget extends BaseLceeWidget<String> {

    public HomeListWidget(Context context, Lifecycle lifecycle) {
        super(context, lifecycle);
    }

    @Override
    public BaseRvAdapter getTargetAdapter() {
        HomeListAdapter homeListAdapter = new HomeListAdapter(lifecycle);
        homeListAdapter.setOnItemClickListener((v,p)->reload());
        return homeListAdapter;
    }
    private int i = 0;
    @Override
    public List<String> getTargetData() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        i++;
        String[] list = {"数据1","数据2","数据3"};
        if (i%2 == 0){
           list = null;
        }
        return list == null? null:Arrays.asList(list);
    }

    /**
     * 列表适配器
     */
    public class HomeListAdapter extends BaseRvAdapter<String,HomeListItemWidget>{

        public HomeListAdapter(Lifecycle lifecycle) {
            super(lifecycle);
        }

        @Override
        public HomeListItemWidget getItemWidget(Context context, Lifecycle lifecycle) {
            return new HomeListItemWidget(context,lifecycle);
        }

        @Override
        public void onBindViewHolder(HomeListItemWidget widget, String item) {
            widget.setData(item);
        }
    }

    /**
     * 列表子项Item
     */
    public class HomeListItemWidget extends StatelessWidget<LinearLayout, LinearWidget>{

        private TextWidget twName;
        private String name;

        public HomeListItemWidget(Context context, Lifecycle lifecycle) {
            super(context, lifecycle);
        }

        @Override
        protected LinearWidget build(Context context, Lifecycle lifecycle) {
            twName = new TextWidget(context, lifecycle);
            return new LinearWidget(context, lifecycle,twName);
        }

        @Override
        public void initWidget(LinearWidget widget) {
            widget.orientation(LinearWidget.horizontal)
                    .padding(16.0f);
        }

        @Override
        public void update() {
            super.update();
            if (TextUtils.isEmpty(name)) return;
            twName.text(name);
        }

        public void setData(String name) {
            this.name = name;
            update();
        }
    }
}
