package com.banzhi.indexrecyclerview.utils;

import com.banzhi.indexrecyclerview.bean.BaseIndexBean;
import com.banzhi.indexrecyclerview.interfaces.IDataHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/8/2.
 * @desciption :
 * @version :
 * </pre>
 */

public class IndexDataHelper implements IDataHelper {


    @Override
    public void cover(List<? extends BaseIndexBean> datas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        for (BaseIndexBean data : datas) {
            String pinyinUpper = getUpperPinYin(data.getOrderName());
            data.setPinyin(pinyinUpper);
            data.setFirstLetter(pinyinUpper.substring(0, 1));
        }
    }

    @Override
    public void sortDatas(List<? extends BaseIndexBean> datas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        cover(datas);
        Collections.sort(datas, new Comparator<BaseIndexBean>() {
            @Override
            public int compare(BaseIndexBean o1, BaseIndexBean o2) {
                if ("#".equals(o1.getPinyin())) {
                    return 1;
                } else if ("#".equals(o2.getPinyin())) {
                    return -1;
                } else {
                    return o1.getPinyin().compareTo(o2.getPinyin());
                }
            }
        });
    }

    @Override
    public void sortDatasAndGetIndex(List<? extends BaseIndexBean> datas, List<String> indexDatas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        sortDatas(datas);
        getIndex(datas, indexDatas);
    }

    @Override
    public void getIndex(List<? extends BaseIndexBean> datas, List<String> indexDatas) {
        for (BaseIndexBean data : datas) {
            //获取拼音首字母
            String pinyin = data.getIndexTag();
            if (!indexDatas.contains(pinyin)) {
                //如果是A-Z字母开头
                if (pinyin.matches("[A-Z]")) {
                    indexDatas.add(pinyin);
                } else {//特殊字母这里统一用#处理
                    indexDatas.add("#");
                }
            }
        }
    }


    /**
     * 获取拼音 大写
     *
     * @param text
     * @return
     */
    private String getUpperPinYin(String text) {
        return PinyinUtils.ccs2Pinyin(text).toUpperCase();
    }
}
