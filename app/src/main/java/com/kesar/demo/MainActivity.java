package com.kesar.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kesar.demo.domain.Tag;
import com.kesar.lazy.recyclerview.CommonViewHolder;
import com.kesar.lazy.recyclerview.ListAdapter;

import org.kesar.lazy.lazydb.LazyDB;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    private TagAdapter mAdapter;
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
                case AddTagActivity.REQUEST_CODE: {
                    Tag tag = (Tag) data.getSerializableExtra(Tag.class.getName());
                    mAdapter.addItemAndRefresh(0,tag);
                    break;
                }
                case EditTagActivity.REQUEST_CODE: {
                    int position = data.getIntExtra(EditTagActivity.Extra_Position, 0);
                    Tag tag = (Tag) data.getSerializableExtra(Tag.class.getName());
                    mAdapter.setItemAndRefresh(position, tag);
                    break;
                }
            }
        }
    }

    private void initView() {
        // mToolbar
        setSupportActionBar(mToolbar);
        // TagAdapter
        mAdapter = new TagAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mAdapter.setOnItemClickListener(new ListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {
                Intent intent = new Intent(getApplicationContext(), EditTagActivity.class);
                intent.putExtra(Tag.class.getName(), mAdapter.getItem(position));
                intent.putExtra(EditTagActivity.Extra_Position, position);
                startActivityForResult(intent, EditTagActivity.REQUEST_CODE);
            }
        });
        mAdapter.setOnItemLongClickListener(new ListAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position, View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("确定要删除吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    mLazyDB.delete(mAdapter.getItem(position));
                                    mAdapter.removeItemAndRefresh(position);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        });
    }

    private void loadData() {
        try {
            List<Tag> tagList = mLazyDB.query(Tag.class).selectAll().orderBy("time DESC").execute();
            mAdapter.setAllItemsAndRefresh(tagList);
        } catch (InstantiationException | ParseException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.fab)
    public void onClick(View view) {
        startActivityForResult(new Intent(getApplicationContext(), AddTagActivity.class), AddTagActivity.REQUEST_CODE);
    }

    static class TagAdapter extends ListAdapter<Tag, CommonViewHolder> {

        @Override
        public CommonViewHolder onCreateViewHolder(ViewGroup parent, Context context, int viewType) {
            return new CommonViewHolder(View.inflate(parent.getContext(), R.layout.list_item_tag, null));
        }

        @Override
        public void onBindViewHolder(CommonViewHolder holder, Tag data, final int position) {
            TextView tvTime = holder.getView(R.id.tvTime);
            TextView tvText = holder.getView(R.id.tvText);
            tvText.setText(data.getText());
            tvTime.setText(data.getTime());
        }

    }
}