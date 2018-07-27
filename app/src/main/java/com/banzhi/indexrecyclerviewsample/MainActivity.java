package com.banzhi.indexrecyclerviewsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.banzhi.indexrecyclerview.IndexBar;

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        indexBar = findViewById(R.id.indexBar);
        textView = findViewById(R.id.text);
        indexBar.setTextView(textView);
        initDatas();
        recyclerView.setAdapter(new TestAdapter());
    }



    List<String> datas = new ArrayList<>();

    public class TestAdapter extends RecyclerView.Adapter<TestViewHolder> {

        @Override
        public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater from = LayoutInflater.from(parent.getContext());
            View view = from.inflate(R.layout.item_sample,parent,false);
            return new TestViewHolder(view);
        }

        @Override
        public void onBindViewHolder(TestViewHolder holder, int position) {
            holder.setText(datas.get(position));
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
            datas.add("啊");
            datas.add("艾尔");
            datas.add("安徽的");
            datas.add("包水电费");
            datas.add("被");
            datas.add("我土豆粉");
            datas.add("教育厅");
            datas.add("啊");
            datas.add("说得通");
            datas.add("poi及");
            datas.add("浅色");
            datas.add("他人");
            datas.add("欧尼");
            datas.add("主菜单");
            datas.add("的");
            datas.add("高规格");
            datas.add("哈哈哈");
            datas.add("斤斤计较");
            datas.add("坎坎坷坷");
            datas.add("啦啦啦啦");
            datas.add("男男女女");
            datas.add("一样一样");
    }
}
