package com.kesar.demo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kesar.demo.adapter.CommonViewHolder;
import com.kesar.demo.domain.Tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private TagAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        // toolbar
        setSupportActionBar(toolbar);
        // TagAdapter
        List<Tag> tagList=new ArrayList<>();
        for(int i=0;i<100;i++){
            Tag tag=new Tag();
            tag.setText(" 萨拉；反击的；拉萨发觉了；三大件发牢骚激发了；阿三及；了房间示范基地打；烧烤架非；拉丝机非；阿三激发了的会计师法律及哦舞台剧上帝给了据哇哦特钢哈市了；记得给非感到十分各地市该发生发射的公司更是反倒是根深蒂固发射的根深蒂固撒");
            tag.setTime(new Date().toLocaleString());
            tagList.add(tag);
        }
        adapter = new TagAdapter(tagList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    class TagAdapter extends RecyclerView.Adapter<CommonViewHolder> {

        private List<Tag> data = new ArrayList<>();

        public TagAdapter() {
        }

        public TagAdapter(List<Tag> data) {
            this.data.addAll(data);
        }

        public void addAll(List<Tag> data) {
            this.data.addAll(data);
        }

        @Override
        public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommonViewHolder(View.inflate(parent.getContext(), R.layout.list_item_tag, null));
        }

        @Override
        public void onBindViewHolder(CommonViewHolder holder, int position) {
            Tag tag = data.get(position);
            TextView tvTime = holder.getView(R.id.tvTime);
            TextView tvText = holder.getView(R.id.tvText);
            tvText.setText(tag.getText());
            tvTime.setText(tag.getTime());
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}