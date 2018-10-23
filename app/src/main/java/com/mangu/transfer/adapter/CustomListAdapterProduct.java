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
import com.bumptech.glide.Glide;
import com.mangu.transfer.R;
import com.mangu.transfer.db.AppControllerList;
import com.mangu.transfer.model.Product;

import java.util.List;

public class CustomListAdapterProduct extends BaseAdapter {
    ImageLoader imageLoader = AppControllerList.getInstance().getImageLoader();
    private Activity activity;
    private LayoutInflater inflater;
    private List<Product> movieItems;

    public CustomListAdapterProduct(Activity activity, List<Product> movieItems) {
        this.activity = activity;
        this.movieItems = movieItems;
    }

    @Override
    public int getCount() {
        return movieItems.size();
    }

    @Override
    public Object getItem(int location) {
        return movieItems.get(location);
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
            convertView = inflater.inflate(R.layout.item_row_list, null);


        ImageView thumbNail = convertView.findViewById(R.id.thumbnail);
        TextView title = convertView.findViewById(R.id.title);
        TextView rating = convertView.findViewById(R.id.description);
        TextView genre = convertView.findViewById(R.id.additional_text);
        TextView year = convertView.findViewById(R.id.id);

        // getting movie data for the row
        Product m = movieItems.get(position);

        // thumbnail image
        // thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
        Glide.with(this.activity)
                .load("https://mangumangu.com/Shop/i/" + m.getThumbnailUrl())
                .into(thumbNail);

        // title
        title.setText(m.getTitle());
        //rating
        rating.setText(String.valueOf(m.getRating()));
        // genre
       /* String genreStr = "";
        for (String str : m.getGenre()) {
            genreStr += str + ", ";
        }
*/

        // release year
        year.setText(String.valueOf(m.getYear()));

        return convertView;
    }

}