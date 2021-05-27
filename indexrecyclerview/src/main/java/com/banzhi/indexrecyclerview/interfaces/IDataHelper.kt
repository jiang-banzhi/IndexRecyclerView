package com.banzhi.indexrecyclerview.interfaces

import com.banzhi.indexrecyclerview.bean.BaseIndexBean

/**
 * <pre>
 * @author :
 * @time : 2021/5/26.
 * @desciption :
 * @version :
</pre> *
 */
interface IDataHelper {
    /**
     * 数据转换 根据getorderName生成pinyin
     *
     * @param datas
     */
    fun cover(datas: MutableList<out BaseIndexBean>)

    /**
     * 排序
     *
     * @param datas
     */
    fun sortDatas(datas: MutableList<out BaseIndexBean>)

    /**
     * 排序并获取索引数据
     *
     * @param datas
     */
    fun sortDatasAndGetIndex(datas: MutableList<out BaseIndexBean>, indexDatas: MutableList<String>)

    /**
     * 获取索引
     *
     * @param datas
     * @param indexDatas
     */
    fun getIndex(datas: MutableList<out BaseIndexBean>, indexDatas: MutableList<String>)
}