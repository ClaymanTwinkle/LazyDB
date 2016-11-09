package com.kesar.demo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.kesar.demo.adapter.ListAdapter;
import com.kesar.demo.adapter.CommonViewHolder;
import com.kesar.demo.domain.Tag;

import org.kesar.lazy.lazydb.LazyDB;

import java.text.ParseException;
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
                    int position = data.getIntExtra(EditTagActivity.Extra_Position, 0);
                    Tag tag = (Tag) data.getSerializableExtra(Tag.class.getName());
                    adapter.setItem(position, tag);
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
                intent.putExtra(EditTagActivity.Extra_Position, position);
                startActivityForResult(intent, EditTagActivity.REQUEST_CODE);
            }
        });
        adapter.setOnItemLongClickListener(new TagAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(final int position, View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage("确定要删除吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    mLazyDB.delete(adapter.getItem(position));
                                    adapter.remove(position);
                                    adapter.notifyItemRemoved(position);
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

    static class TagAdapter extends ListAdapter<Tag, CommonViewHolder> {

        private OnItemClickListener mOnItemClickListener;
        private OnItemLongClickListener mOnItemLongClickListener;

        @Override
        public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new CommonViewHolder(View.inflate(parent.getContext(), R.layout.list_item_tag, null));
        }

        @Override
        public void onBindViewHolder(CommonViewHolder holder, Tag data, final int position) {
            TextView tvTime = holder.getView(R.id.tvTime);
            TextView tvText = holder.getView(R.id.tvText);
            tvText.setText(data.getText());
            tvTime.setText(data.getTime());
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onItemClick(position, v);
                    }
                });
            }
            if(mOnItemLongClickListener!=null){
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        mOnItemLongClickListener.onItemLongClick(position,v);
                        return true;
                    }
                });
            }
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            mOnItemClickListener = onItemClickListener;
        }

        public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
            this.mOnItemLongClickListener = onItemLongClickListener;
        }

        public static interface OnItemClickListener {
            void onItemClick(int position, View view);
        }

        public static interface OnItemLongClickListener{
            void onItemLongClick(int position,View view);
        }
    }
}