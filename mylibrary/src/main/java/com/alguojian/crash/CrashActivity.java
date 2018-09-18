package com.alguojian.crash;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;

public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 设置contentFeature,可使用切换动画
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition explode = TransitionInflater.from(this).inflateTransition(android.R.transition.no_transition);
            getWindow().setEnterTransition(explode);
        }
        setContentView(R.layout.activity_crash);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashListActivity.start(CrashActivity.this);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CrashHandler.getInstance().postCrashToDingding();
            }
        });
    }

    public static void start(Activity context) {
        Intent starter = new Intent(context, CrashActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(starter, ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
        } else {
            context.startActivity(starter);
        }
    }
}
