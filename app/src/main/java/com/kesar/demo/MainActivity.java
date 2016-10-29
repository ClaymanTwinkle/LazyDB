package com.kesar.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kesar.demo.adapter.CommonViewHolder;
import com.kesar.demo.domain.Tag;

import org.kesar.lazy.lazydb.LazyDB;

import java.text.ParseException;
import java.util.ArrayList;
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
    private LazyDB mLazyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        mLazyDB = LazyDB.create(getApplicationContext());
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AddTagActivity.REQUEST_CODE:
                    adapter.clearAll();
                    loadData();
                    break;
                case EditTagActivity.REQUEST_CODE:
                    int position=data.getIntExtra(EditTagActivity.Extra_Position,0);
                    Tag tag= (Tag) data.getSerializableExtra(Tag.class.getName());
                    adapter.setItem(position,tag);
                    adapter.notifyItemChanged(position);
                    break;
            }
        }
    }

    private void initView() {
        // toolbar
        setSupportActionBar(toolbar);
        // TagAdapter
        adapter = new TagAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter.setOnItemClickListener(new TagAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent = new Intent(getApplicationContext(), EditTagActivity.class);
                intent.putExtra(Tag.class.getName(), adapter.getItem(position));
                intent.putExtra(EditTagActivity.Extra_Position,position);
                startActivityForResult(intent, EditTagActivity.REQUEST_CODE);
            }
        });
    }

    private void loadData() {
        try {
            List<Tag> tagList = mLazyDB.query(Tag.class).selectAll().execute();
            adapter.addAll(tagList);
            adapter.notifyDataSetChanged();
        } catch (InstantiationException | ParseException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        startActivityForResult(new Intent(getApplicationContext(), AddTagActivity.class), AddTagActivity.REQUEST_CODE);
    }

    static class TagAdapter extends RecyclerView.Adapter<CommonViewHolder> {

        private OnItemClickListener mOnItemClickListener;
        private List<Tag> data = new ArrayList<>();

        public TagAdapter() {
        }

        public TagAdapter(List<Tag> data) {
            this.data.addAll(data);
        }

        public void addAll(List<Tag> data) {
            this.data.addAll(data);
        }

        public void setItem(int position,Tag data){
            this.data.set(position,data);
        }

        public void clearAll() {
            this.data.clear();
        }

        public Tag getItem(int position) {
            return data.get(position);
        }

        @Override
        public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommonViewHolder(View.inflate(parent.getContext(), R.layout.list_item_tag, null));
        }

        @Override
        public void onBindViewHolder(CommonViewHolder holder, final int position) {
            Tag tag = data.get(position);
            TextView tvTime = holder.getView(R.id.tvTime);
            TextView tvText = holder.getView(R.id.tvText);
            tvText.setText(tag.getText());
            tvTime.setText(tag.getTime());
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(position, v);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        public static interface OnItemClickListener {
            void onItemClick(int position, View view);
        }
    }
}