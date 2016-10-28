package com.kesar.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.kesar.demo.domain.Tag;

import org.kesar.lazy.lazydb.LazyDB;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加tag的activity
 * Created by kesar on 16-10-28.
 */
public class AddTagActivity extends AppCompatActivity {
    public static final int REQUEST_CODE=1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivFinish)
    ImageView ivFinish;
    @BindView(R.id.etContent)
    EditText etContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);
        ButterKnife.bind(this);

        // toolbar
        setSupportActionBar(toolbar);
    }

    @OnClick({R.id.ivBack, R.id.ivFinish})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.ivFinish:
                String text = etContent.getText().toString();
                if (!TextUtils.isEmpty(text)) {
                    Tag tag = new Tag();
                    tag.setText(text);
                    tag.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
                    try {
                        LazyDB lazyDB = LazyDB.create(getApplicationContext());
                        lazyDB.insert(tag);
                        setResult(RESULT_OK);
                        finish();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        Snackbar.make(view, "添加备忘录失败", Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
