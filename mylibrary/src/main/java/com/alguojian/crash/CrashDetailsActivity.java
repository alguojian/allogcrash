package com.alguojian.crash;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class CrashDetailsActivity extends AppCompatActivity {

    public static void start(Activity context, CrashBean crashBean, CardView cardView) {
        Intent starter = new Intent(context, CrashDetailsActivity.class);
        starter.putExtra("data", crashBean);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(starter, ActivityOptions.makeSceneTransitionAnimation(context, cardView, "jump").toBundle());
        } else {
            context.startActivity(starter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crash_details);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        CrashBean data = (CrashBean) getIntent().getSerializableExtra("data");
        TextView textView = findViewById(R.id.name);
        TextView textView1 = findViewById(R.id.text);
        if (TextUtils.isEmpty(data.userId)) {
            textView.setText(data.phoneName);
        } else {
            textView.setText(data.userId + data.phoneName);
        }
        textView1.setText(data.crash);
    }
}
