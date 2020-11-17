package com.example.tico;

import androidx.recyclerview.widget.RecyclerView;
import androidx.core.content.res.ResourcesCompat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurants;
    public static Map<String, Bitmap> photos;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIv;
        TextView nameTv;

        public ViewHolder(View itemView) {
            super(itemView);
            this.imageIv = itemView.findViewById(R.id.phototest);
            this.nameTv = itemView.findViewById(R.id.nametest);
        }
    }

    public RestaurantAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
        photos = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View restaurantView = inflater.inflate(R.layout.card_layout, parent, false);
        ViewHolder viewHolder = new ViewHolder(restaurantView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RestaurantAdapter.ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        TextView textViewName = holder.nameTv;
        textViewName.setText(restaurant.getName());
        textViewName.setText("hello");
//        ImageView imageViewPhoto = holder.imageIv;
//        String photoUrl = restaurant.getPhotoURL();

//        if (photoUrl != null)
//            setPhoto(restaurant.getName(), photoUrl, imageViewPhoto);
//
//        imageViewPhoto.setContentDescription(restaurant.getName());
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

//    public void setPhoto(final String name, String url, final ImageView imageViewPhoto) {
//        RequestQueue queue = Volley.newRequestQueue(this);
//        ImageRequest request = new ImageRequest(url,
//                new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap response) {
//                        imageViewPhoto.setImageBitmap(response);
//                        photos.put(name, response);
//                    }
//                }, 270, 350, ImageView.ScaleType.CENTER_CROP, Bitmap.Config.RGB_565,
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("setPhoto", error.toString());
//                    }
//                });
//
//        queue.add(request);
//    }
}