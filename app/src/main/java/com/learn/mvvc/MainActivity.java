package com.learn.mvvc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.ittianyu.relight.view.ActivityDelegationManager;
import com.ittianyu.relight.widget.stateful.navigator.Navigator;
import com.ittianyu.relight.widget.stateful.navigator.WidgetNavigator;
import com.ittianyu.relight.widget.stateful.navigator.route.WidgetRoute;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        View root = WidgetUtils.render(this, HomeListWidget.class);
//        setContentView(root);
        Navigator navigator = new WidgetNavigator(this, getLifecycle(), Router.Route.name,
                new WidgetRoute<>(Router.Route.index, Screen.class, "first screen"),
                new WidgetRoute<>(Router.Route.home, Screen2.class, "second screen")
        );
        setContentView(navigator.render());
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityDelegationManager.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (ActivityDelegationManager.onKeyDown(this, keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
