package com.alguojian.crash;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.litepal.LitePal;

import java.util.List;

public class CrashListActivity extends AppCompatActivity {

    public static void start(Activity context) {
        Intent starter = new Intent(context, CrashListActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(starter, ActivityOptions.makeSceneTransitionAnimation(context).toBundle());
        } else {
            context.startActivity(starter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 设置contentFeature,可使用切换动画
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            Transition explode = TransitionInflater.from(this).inflateTransition(android.R.transition.explode);
            getWindow().setEnterTransition(explode);
        }

        setContentView(R.layout.activity_crash_list);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            );
        }

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final CrashAdapter crashAdapter = new CrashAdapter();

        recyclerView.setAdapter(crashAdapter);

        crashAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        crashAdapter.isFirstOnly(false);

        List<CrashBean> all = LitePal.order("time desc").find(CrashBean.class);

        crashAdapter.setNewData(all);

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.refresh);

        swipeRefreshLayout.setColorSchemeColors(Color.RED);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                List<CrashBean> all = LitePal.findAll(CrashBean.class);
                crashAdapter.setNewData(all);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        crashAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CrashDetailsActivity.start(CrashListActivity.this, crashAdapter.getData().get(position),
                        (CardView) crashAdapter.getViewByPosition(recyclerView, position, R.id.cardView));
            }
        });

        crashAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                new AlertDialog.Builder(CrashListActivity.this)
                        .setTitle("提示")
                        .setMessage("确定要删除吗？")
                        .setNegativeButton("删除全部", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LitePal.deleteAll(CrashBean.class);
                                crashAdapter.setNewData(null);
                            }
                        })
                        .setPositiveButton("删除该条", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                CrashBean crashBean = crashAdapter.getData().get(position);
                                int delete = LitePal.delete(CrashBean.class, crashBean._id);

                                if (1 == delete) {
                                    Toast.makeText(CrashListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                }
                                crashAdapter.remove(position);
                            }
                        }).create().show();
                return true;
            }
        });
    }

}
