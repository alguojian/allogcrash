package com.alguojian.crash;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ${Descript}
 *
 * @author alguojian
 * @date 2018/8/18
 */
public class CrashAdapter extends BaseQuickAdapter<CrashBean, BaseViewHolder> {

    public CrashAdapter() {
        super(R.layout.item_crash);
    }

    @Override
    protected void convert(BaseViewHolder helper, CrashBean item) {

        if (item.crash.length() >= 62){
            helper.setText(R.id.title, item.crash.substring(0, 61));
        }else {
            helper.setText(R.id.title, item.crash);
        }
        helper.setText(R.id.time, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(item.time)));
    }
}
