package com.banzhi.indexrecyclerviewsample;

import com.banzhi.indexrecyclerview.interfaces.ISupperInterface;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/8/2.
 * @desciption :
 * @version :
 * </pre>
 */

public class IndexBean implements ISupperInterface{
    String text;
    String tag;

    public IndexBean(String tag,String text) {
        this.text = text;
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public String getIndexText() {
        return tag;
    }
}
