package com.alguojian.crash;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CrashDetailsActivity extends AppCompatActivity {

    public static void start(Activity context, CrashBean crashBean) {
        Intent starter = new Intent(context, CrashDetailsActivity.class);
        starter.putExtra("data", crashBean);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(starter, ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
        } else {
            context.startActivity(starter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 设置contentFeature,可使用切换动画
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition explode = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom);
            getWindow().setEnterTransition(explode);
        }
        setContentView(R.layout.activity_crash_details);

        CrashBean data = (CrashBean) getIntent().getSerializableExtra("data");
        TextView textView = findViewById(R.id.name);
        TextView textView1 = findViewById(R.id.text);
        textView.setText(data.phoneName);
        textView1.setText(data.crash);

    }
}
