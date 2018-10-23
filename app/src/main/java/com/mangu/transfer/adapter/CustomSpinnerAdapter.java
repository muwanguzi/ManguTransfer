package com.mangu.transfer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mangu.transfer.R;

import java.util.List;

/**
 * Created by ok on 7/28/2017.
 */

public class CustomSpinnerAdapter extends BaseAdapter {


    Context context;
    int charity_project[];
    List<String> lables;
    LayoutInflater inflter;

    public CustomSpinnerAdapter(Context applicationContext, int[] charity_project, List<String> lables) {
        this.context = applicationContext;
        this.charity_project = charity_project;
        this.lables = lables;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return charity_project.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.spinner_item, null);
        ImageView icon = view.findViewById(R.id.imageView);
        TextView names = view.findViewById(R.id.textView);
        icon.setImageResource(charity_project[i]);
        names.setText((CharSequence) lables);
        return view;
    }
}

