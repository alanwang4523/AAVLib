package com.alanwang.aav.alvideoeditor.business.record;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alanwang.aav.alvideoeditor.R;

/**
 * Author: AlanWang4523.
 * Date: 19/4/17 07:53.
 * Mail: alanwang4523@gmail.com
 */
public class CameraRecordFragment extends Fragment implements CameraRecordContract.View {

    public static CameraRecordFragment newInstance() {
        return new CameraRecordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.aav_activity_camera_record, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void showLoadingView() {

    }

    @Override
    public void dismissLoadingView() {

    }
}
