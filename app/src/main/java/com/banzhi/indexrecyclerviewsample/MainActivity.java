package com.banzhi.indexrecyclerviewsample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.banzhi.indexrecyclerview.decoration.LevitationDecoration;
import com.banzhi.indexrecyclerview.utils.IndexDataHelper;
import com.banzhi.indexrecyclerview.widget.IndexBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    IndexBar indexBar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerview);
        indexBar = findViewById(R.id.indexBar);
        indexBar.setUseDatasIndex();
        textView = findViewById(R.id.text);
        initLinear();
//        initGrid();
        indexBar.setTextView(textView);
        indexBar.bindRecyclerView(recyclerView);


    }

    private void initLinear() {
        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);
        LevitationDecoration decor = new LevitationDecoration(this);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.addItemDecoration(decor);
        TestAdapter adapter = new TestAdapter(indexBeanList);
        recyclerView.setAdapter(adapter);
        indexBeanList = initindexBeanList();
        indexBar.setSourceDatas(indexBeanList);
        decor.setDatas(indexBeanList);
        adapter.refresh(indexBeanList);


    }

    private void initGrid() {
        final GridTestAdapter adapter = new GridTestAdapter(indexBeanList);
        GridLayoutManager layout = new GridLayoutManager(this, 4);
        layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                IndexBean indexBean = adapter.getDatas().get(position);
                return indexBean.isIndex ? 4 : 1;
            }
        });
        recyclerView.setLayoutManager(layout);
        recyclerView.setAdapter(adapter);
        indexBeanList = initindexBeanList();
        new IndexDataHelper().sortDatas(indexBeanList);
        indexBeanList = compute();
        indexBar.setOrderly(true);
        indexBar.setSourceDatas(indexBeanList);
        adapter.refresh(indexBeanList);
    }

    private List<IndexBean> compute() {
        List<String> tagList = new ArrayList<>();
        List<IndexBean> list = new ArrayList<>();
        for (IndexBean indexBean : indexBeanList) {
            if (!tagList.contains(indexBean.getIndexTag())) {
                tagList.add(indexBean.getIndexTag());
                indexBean.setIndex(true);
                list.add(indexBean);
            }
            list.add(new IndexBean(indexBean.getText()));
        }
        return list;
    }


    List<IndexBean> indexBeanList = new ArrayList<>();

    public class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {
        List<IndexBean> datas;

        public TestAdapter(List<IndexBean> indexBeanList) {
            this.datas = indexBeanList;
        }

        public List<IndexBean> getDatas() {
            return datas;
        }

        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater from = LayoutInflater.from(parent.getContext());
            View view = from.inflate(R.layout.item_sample, parent, false);
            return new TestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TestViewHolder holder, int position) {
            holder.setText(datas.get(position).getText());
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        public void refresh(List<IndexBean> indexBeanList) {
            this.datas = indexBeanList;
            notifyDataSetChanged();
        }
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public TestViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_sample_text);
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }


    private List<IndexBean> initindexBeanList() {
        List<IndexBean> indexBeanList = new ArrayList<>();
        indexBeanList.add(new IndexBean("包水电费"));
        indexBeanList.add(new IndexBean("啊"));
        indexBeanList.add(new IndexBean("艾尔"));
        indexBeanList.add(new IndexBean("安徽的"));
        indexBeanList.add(new IndexBean("啊"));
        indexBeanList.add(new IndexBean("被"));
        indexBeanList.add(new IndexBean("被3"));
        indexBeanList.add(new IndexBean("的3"));
        indexBeanList.add(new IndexBean("高规格2"));
        indexBeanList.add(new IndexBean("的4"));
        indexBeanList.add(new IndexBean("的5"));
        indexBeanList.add(new IndexBean("主菜单"));
        indexBeanList.add(new IndexBean("高规格1"));
        indexBeanList.add(new IndexBean("高规格3"));
        indexBeanList.add(new IndexBean("哈哈哈"));
        indexBeanList.add(new IndexBean("斤斤计较"));
        indexBeanList.add(new IndexBean("坎坎坷坷"));
        indexBeanList.add(new IndexBean("被2"));
        indexBeanList.add(new IndexBean("坎坎坷坷"));
        indexBeanList.add(new IndexBean("啦啦啦啦"));
        indexBeanList.add(new IndexBean("啦啦啦啦2"));
        indexBeanList.add(new IndexBean("教育厅"));
        indexBeanList.add(new IndexBean("啦啦啦啦3"));
        indexBeanList.add(new IndexBean("男男女女"));
        indexBeanList.add(new IndexBean("男男女女2"));
        indexBeanList.add(new IndexBean("男男女女3"));
        indexBeanList.add(new IndexBean("浅色"));
        indexBeanList.add(new IndexBean("欧尼4"));
        indexBeanList.add(new IndexBean("欧尼6"));
        indexBeanList.add(new IndexBean("poi及"));
        indexBeanList.add(new IndexBean("说得通"));
        indexBeanList.add(new IndexBean("他人"));
        indexBeanList.add(new IndexBean("我土豆粉"));
        indexBeanList.add(new IndexBean("欧尼3"));
        indexBeanList.add(new IndexBean("一样一样"));
        return indexBeanList;
    }

    public class GridTestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        List<IndexBean> datas;
        private static final int TYPE = 32;

        public GridTestAdapter(List<IndexBean> indexBeanList) {
            this.datas = indexBeanList;
        }

        public List<IndexBean> getDatas() {
            return datas;
        }

        @Override
        public int getItemViewType(int position) {
            if (datas.get(position).isIndex) {
                return TYPE;
            }
            return super.getItemViewType(position);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater from = LayoutInflater.from(parent.getContext());
            View view;
            if (viewType == TYPE) {
                view = from.inflate(R.layout.item_sample, parent, false);
                return new TestViewHolder(view);
            } else {
                view = from.inflate(R.layout.item_grid_sample, parent, false);
                return new GridTestViewHolder(view);

            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == TYPE) {
                ((TestViewHolder) holder).setText(datas.get(position).getFirstLetter());
            } else {
                ((GridTestViewHolder) holder).setText(datas.get(position).getText());
            }
        }

        @Override
        public int getItemCount() {
            return datas.size();
        }

        public void refresh(List<IndexBean> indexBeanList) {
            this.datas = indexBeanList;
            notifyDataSetChanged();
        }
    }

    public class GridTestViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public GridTestViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_grid_sample_text);
        }

        public void setText(String text) {
            textView.setText(text);
        }
    }
}
