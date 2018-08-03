package com.banzhi.indexrecyclerviewsample;

import com.banzhi.indexrecyclerview.bean.BaseIndexBean;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/8/2.
 * @desciption :
 * @version :
 * </pre>
 */

public class IndexBean extends BaseIndexBean {
    String text;

    public IndexBean(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public String getOrderName() {
        return text;
    }
}
