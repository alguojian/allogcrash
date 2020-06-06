package com.alguojian.logcrash;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

/**
 * ${Descript}
 *
 * @author alguojian
 * @date 2018/8/15
 */
public class OtherNewsBean extends LitePalSupport implements Serializable {

    public long _id;
    public String crash;//crash信息
    public String dingding;//crash信息

}
