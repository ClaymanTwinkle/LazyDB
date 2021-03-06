package com.kesar.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.kesar.demo.domain.Tag;

import org.kesar.lazy.lazydb.LazyDB;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加tag的activity
 * Created by kesar on 16-10-28.
 */
public class AddTagActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 1;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.etContent)
    EditText mEtContent;
    @BindView(R.id.tvTime)
    TextView mTvTime;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        ButterKnife.bind(this);

        // mToolbar
        setSupportActionBar(mToolbar);

        mTvTime.setText(simpleDateFormat.format(new Date()));
    }

    @OnClick({R.id.tvCancel, R.id.tvFinish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCancel:
                onBackPressed();
                break;
            case R.id.tvFinish:
                String text = mEtContent.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    Snackbar.make(view, "记录不能为空", Snackbar.LENGTH_SHORT).show();
                } else {
                    Tag tag = new Tag();
                    tag.setId(UUID.randomUUID().toString());
                    tag.setText(text);
                    tag.setTime(simpleDateFormat.format(new Date()));
                    try {
                        LazyDB lazyDB = LazyDBFactory.createDB(getApplicationContext());
                        lazyDB.insert(tag);
                        Intent intent = new Intent();
                        intent.putExtra(Tag.class.getName(), tag);
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(view, "添加备忘录失败", Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
