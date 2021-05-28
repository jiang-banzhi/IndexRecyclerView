package com.banzhi.indexrecyclerview.interfaces;

import com.banzhi.indexrecyclerview.bean.BaseIndexBean;

import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/8/2.
 * @desciption :
 * @version :
 * </pre>
 */

public interface IDataHelper {
    /**
     * 数据转换 根据getorderName生成pinyin
     *
     * @param datas
     */
    void cover(List<? extends BaseIndexBean> datas);

    /**
     * 排序
     *
     * @param datas
     */
    void sortDatas(List<? extends BaseIndexBean> datas);

    /**
     * 排序并获取索引数据
     *
     * @param datas
     */
    void sortDatasAndGetIndex(List<? extends BaseIndexBean> datas, List<String> indexDatas);

    /**
     * 获取索引
     *
     * @param datas
     * @param indexDatas
     */
    void getIndex(List<? extends BaseIndexBean> datas, List<String> indexDatas);
}