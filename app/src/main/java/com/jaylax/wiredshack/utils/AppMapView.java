package com.jaylax.wiredshack.utils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.SupportMapFragment;
import com.jaylax.wiredshack.R;

public class AppMapView extends SupportMapFragment {

    OnTouchListener mListener = null;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View view = super.onCreateView(layoutInflater, viewGroup, bundle);
        FrameLayout frameLayout = new TouchableWrapper(this.getContext());
        if (!frameLayout.equals(null)){
            frameLayout.setBackgroundColor(getResources().getColor(R.color.transparent));
            ((ViewGroup)view).addView(frameLayout,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        }
        return view;
    }

    public void setListener(OnTouchListener listener){
        this.mListener = listener;
    }

    public interface OnTouchListener {
        void onTouch();
    }

    public class TouchableWrapper extends FrameLayout{
        public Context context;
        public TouchableWrapper(@NonNull Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {

            if (ev.getAction()== MotionEvent.ACTION_UP){
                if (mListener != null){
                    mListener.onTouch();
                }
            }else if (ev.getAction()== MotionEvent.ACTION_DOWN){
                if (mListener != null){
                    mListener.onTouch();
                }
            }
            return super.dispatchTouchEvent(ev);
        }
    }
}