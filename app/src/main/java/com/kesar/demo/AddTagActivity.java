package com.kesar.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 添加tag的activity
 * Created by kesar on 16-10-28.
 */
public class AddTagActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivFinish)
    ImageView ivFinish;

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

                break;
        }
    }
}
