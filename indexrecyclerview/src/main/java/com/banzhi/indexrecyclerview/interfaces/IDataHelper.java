package com.banzhi.indexrecyclerview.interfaces;

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
     * 排序
     *
     * @param datas
     */
    void sortDatas(List<? extends ISupperInterface> datas, List<String> indexDatas);
}
