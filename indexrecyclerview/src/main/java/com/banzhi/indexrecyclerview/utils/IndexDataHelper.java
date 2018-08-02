package com.banzhi.indexrecyclerview.utils;

import com.banzhi.indexrecyclerview.interfaces.IDataHelper;
import com.banzhi.indexrecyclerview.interfaces.ISupperInterface;

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
    public void sortDatas(List<? extends ISupperInterface> datas, List<String> indexDatas) {
        if (datas == null || datas.isEmpty()) {
            return;
        }
        Collections.sort(datas, new Comparator<ISupperInterface>() {
            @Override
            public int compare(ISupperInterface o1, ISupperInterface o2) {
                if ("#".equals(getUpperPinYin(o1.getIndexText()))) {
                    return 1;
                } else if ("#".equals(getUpperPinYin(o2.getIndexText()))) {
                    return -1;
                } else {
                    return getUpperPinYin(o1.getIndexText()).compareTo(getUpperPinYin(o2.getIndexText()));
                }
            }
        });

        for (ISupperInterface data : datas) {
            //获取拼音首字母
            String pinyin = getUpperPinYin(data.getIndexText()).substring(0, 1);
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
