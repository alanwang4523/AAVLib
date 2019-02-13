package com.alanwang.aavlib;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.alanwang.aav.alvideoeditor.preview.AAVVideoPreviewActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView btn_goto_video_editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_goto_video_editor = findViewById(R.id.btn_goto_video_editor);
        btn_goto_video_editor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_goto_video_editor:
                startActivity(new Intent(MainActivity.this, AAVVideoPreviewActivity.class));
                break;
            default:
        }
    }
}
