package com.alguojian.crash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

public class CrashListActivity extends AppCompatActivity {

    public static void start(Context context) {
        Intent starter = new Intent(context, CrashListActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_list);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final CrashAdapter crashAdapter = new CrashAdapter();

        recyclerView.setAdapter(crashAdapter);

        crashAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        crashAdapter.isFirstOnly(false);

        List<CrashBean> all = LitePal.findAll(CrashBean.class);

        crashAdapter.setNewData(all);

        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.refresh);

        swipeRefreshLayout.setColorSchemeColors(Color.GREEN);

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
                CrashDetailsActivity.start(CrashListActivity.this, crashAdapter.getData().get(position));
            }
        });

        crashAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {


                new AlertDialog.Builder(CrashListActivity.this)
                        .setTitle("提示")
                        .setMessage("确定要删除吗？")
                        .setNegativeButtonIcon(ContextCompat.getDrawable(CrashListActivity.this,R.drawable.common_complete_icon))
                        .setNegativeButton(null, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                CrashBean crashBean = crashAdapter.getData().get(position);
                                int delete = LitePal.delete(CrashBean.class, crashBean._id);

                                if (1 == delete){
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
