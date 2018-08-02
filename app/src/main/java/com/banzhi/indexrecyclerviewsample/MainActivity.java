package com.banzhi.indexrecyclerviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.banzhi.indexrecyclerview.IndexBar;
import com.banzhi.indexrecyclerview.LevitationDecoration;

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
        LinearLayoutManager layout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layout);
        indexBar = findViewById(R.id.indexBar);
        textView = findViewById(R.id.text);
        indexBar.setTextView(textView);
        initDatas();
        LevitationDecoration decor = new LevitationDecoration(this);
        decor.setDatas(datas);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.addItemDecoration(decor);
        indexBar.setSourceDatas(datas);
        indexBar.bindRecyclerView(recyclerView);
        recyclerView.setAdapter(new TestAdapter());

    }


    List<IndexBean> datas = new ArrayList<>();

    public class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {

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


    private void initDatas() {
        datas.add(new IndexBean("B", "包水电费"));
        datas.add(new IndexBean("A", "啊"));
        datas.add(new IndexBean("A", "艾尔"));
        datas.add(new IndexBean("A", "安徽的"));
        datas.add(new IndexBean("A", "啊"));
        datas.add(new IndexBean("B", "被"));
        datas.add(new IndexBean("B", "被3"));
        datas.add(new IndexBean("D", "的3"));
        datas.add(new IndexBean("G", "高规格2"));
        datas.add(new IndexBean("D", "的4"));
        datas.add(new IndexBean("D", "的5"));
        datas.add(new IndexBean("Z", "主菜单"));
        datas.add(new IndexBean("G", "高规格1"));
        datas.add(new IndexBean("G", "高规格3"));
        datas.add(new IndexBean("H", "哈哈哈"));
        datas.add(new IndexBean("J", "斤斤计较"));
        datas.add(new IndexBean("K", "坎坎坷坷"));
        datas.add(new IndexBean("B", "被2"));
        datas.add(new IndexBean("K", "坎坎坷坷"));
        datas.add(new IndexBean("L", "啦啦啦啦"));
        datas.add(new IndexBean("L", "啦啦啦啦2"));
        datas.add(new IndexBean("J", "教育厅"));
        datas.add(new IndexBean("L", "啦啦啦啦3"));
        datas.add(new IndexBean("N", "男男女女"));
        datas.add(new IndexBean("N", "男男女女2"));
        datas.add(new IndexBean("N", "男男女女3"));
        datas.add(new IndexBean("Q", "浅色"));
        datas.add(new IndexBean("O", "欧尼4"));
        datas.add(new IndexBean("O", "欧尼6"));
        datas.add(new IndexBean("P", "poi及"));
        datas.add(new IndexBean("S", "说得通"));
        datas.add(new IndexBean("T", "他人"));
        datas.add(new IndexBean("W", "我土豆粉"));
        datas.add(new IndexBean("O", "欧尼3"));
        datas.add(new IndexBean("Y", "一样一样"));
    }
}
