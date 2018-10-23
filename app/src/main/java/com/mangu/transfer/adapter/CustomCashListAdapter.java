package com.mangu.transfer.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.mangu.transfer.R;
import com.mangu.transfer.db.AppControllerList;
import com.mangu.transfer.model.Custom_list_model;

import java.util.List;

/**
 * Created by FRANCIS on 04/03/2017.
 */

public class CustomCashListAdapter extends BaseAdapter

{
    ImageLoader imageLoader = AppControllerList.getInstance().getImageLoader();
    private Activity activity;
    private LayoutInflater inflater;
    private List<Custom_list_model> items;

    public CustomCashListAdapter(Activity activity, List<Custom_list_model> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.order_list_item, null);

        if (imageLoader == null)
            imageLoader = AppControllerList.getInstance().getImageLoader();

        ImageView thumbNail = convertView.findViewById(R.id.image_cartlist);
        TextView title = convertView.findViewById(R.id.txtMenuName);
        TextView description = convertView.findViewById(R.id.txtQuantity1);
        TextView additional_info = convertView.findViewById(R.id.txtPrice);
        TextView other_details = convertView.findViewById(R.id.txtQuantity);
        TextView details = convertView.findViewById(R.id.details);
        TextView id = convertView.findViewById(R.id.txtProductId);
        TextView date = convertView.findViewById(R.id.date);


        // getting list data for the row
        Custom_list_model m = items.get(position);

        // thumbnail image
        // thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);

        // title
        title.setText(m.getTitle());
        //description
        description.setText(m.getDescription());
        //additional info
        additional_info.setText(m.getAdditional_info());
        //genre
        other_details.setText(m.getOther_details());
//        details.setText(m.getImplementer());
        // id.setText(m.getM_id());
        // date.setText(m.getDate());

        return convertView;
    }

}