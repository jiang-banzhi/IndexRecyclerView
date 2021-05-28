package com.banzhi.indexrecyclerview.utils

import com.banzhi.indexrecyclerview.bean.BaseIndexBean
import com.banzhi.indexrecyclerview.interfaces.IDataHelper
import java.util.*

/**
 * <pre>
 * @author : No.1
 * @time : 2018/8/2.
 * @desciption :
 * @version :
</pre> *
 */
class IndexDataHelper : IDataHelper {
    override fun cover(datas: MutableList<out BaseIndexBean>) {
        if (datas == null || datas.isEmpty()) {
            return
        }
        for (data in datas) {
            val pinyinUpper = getUpperPinYin(data.getOrderName())
            data.pinyin = pinyinUpper
            data.firstLetter = pinyinUpper.substring(0, 1)
        }
    }

    override fun sortDatas(datas: MutableList<out BaseIndexBean>) {
        if (datas == null || datas.isEmpty()) {
            return
        }
        cover(datas)
        datas.sortWith(Comparator { o1, o2 ->
            if ("#" == o1.pinyin) {
                1
            } else if ("#" == o2.pinyin) {
                -1
            } else {
                o1.pinyin.compareTo(o2.pinyin)
            }
        })
    }

    override fun sortDatasAndGetIndex(datas: MutableList<out BaseIndexBean>, indexDatas: MutableList<String>) {
        if (datas == null || datas.isEmpty()) {
            return
        }
        sortDatas(datas)
        getIndex(datas, indexDatas)
    }

    override fun getIndex(datas: MutableList<out BaseIndexBean>, indexDatas: MutableList<String>) {
        for (data in datas) {
            //获取拼音首字母
            val pinyin = data.getIndexTag()
            if (!indexDatas.contains(pinyin)) {
                //如果是A-Z字母开头
                if (pinyin.matches(Regex("[A-Z]"))) {
                    indexDatas.add(pinyin)
                } else { //特殊字母这里统一用#处理
                    indexDatas.add("#")
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
    private fun getUpperPinYin(text: String): String {
        return PinyinUtils.ccs2Pinyin(text).toUpperCase()
    }
}