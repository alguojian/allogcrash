package com.alguojian.logcrash

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import java.text.SimpleDateFormat
import java.util.*


class CrashAdapter : BaseQuickAdapter<CrashBean, BaseViewHolder>(R.layout.item_crash) {
    override fun convert(helper: BaseViewHolder, item: CrashBean) {
        if (item.getCrash().length >= 62) {
            helper.setText(R.id.title, item.getCrash().substring(0, 61))
        } else {
            helper.setText(R.id.title, item.getCrash())
        }
        helper.setText(R.id.time, SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(item.time)))
    }
}