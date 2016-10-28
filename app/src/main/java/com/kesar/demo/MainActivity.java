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
        mLazyDB=LazyDB.create(getApplicationContext());
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
    }

    private void loadData() {
        try {
            List<Tag> tagList = mLazyDB.query(Tag.class).selectAll().execute();
            adapter.addAll(tagList);
            adapter.notifyDataSetChanged();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        startActivityForResult(new Intent(getApplicationContext(), AddTagActivity.class), AddTagActivity.REQUEST_CODE);
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

        public void clearAll() {
            this.data.clear();
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