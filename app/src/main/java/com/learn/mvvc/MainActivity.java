package com.learn.mvvc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.ittianyu.relight.utils.WidgetUtils;
import com.learn.mvvc.pages.home.HomeWidget;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = WidgetUtils.render(this, HomeWidget.class);
        setContentView(root);
    }
}
