package com.example.tico;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurants;
    private RequestQueue requestQueue;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantNameTv;
        TextView restaurantDistanceTv;
        ImageView restaurantPhotoIv;
        public ViewHolder(View itemView) {
            super(itemView);
            this.restaurantNameTv = itemView.findViewById(R.id.restaurantName);
            this.restaurantDistanceTv = itemView.findViewById(R.id.restaurantDistance);
            this.restaurantPhotoIv = itemView.findViewById(R.id.restaurantPhoto);
        }
    }

    public RestaurantAdapter(List<Restaurant> restaurants, Context context) {
        this.restaurants = restaurants;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View restaurantView = inflater.inflate(R.layout.card_layout, parent, false);
        return new ViewHolder(restaurantView);
    }

    @Override
    public void onBindViewHolder(RestaurantAdapter.ViewHolder holder, int position) {
        TextView nameTv = holder.restaurantNameTv;
        final TextView distanceTv = holder.restaurantDistanceTv;
        ImageView photoIv = holder.restaurantPhotoIv;
        final Restaurant restaurant = restaurants.get(position);
        nameTv.setText(restaurant.getName());
        String photoURL = restaurant.getPhotoURL();
        Picasso.get().load(photoURL).resize(200, 0).centerCrop().into(photoIv);
        photoIv.setContentDescription(restaurant.getName());
        photoIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startIntent = new Intent(view.getContext(), DetailsActivity.class);
                startIntent.putExtra("restaurant", restaurant);
                view.getContext().startActivity(startIntent);
            }
        });
        String distanceURL = restaurant.getDistanceURL();
        RequestQueue queue = Volley.newRequestQueue(this.context);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, distanceURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject distanceInformation = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                    String distance = distanceInformation.getJSONObject("distance").getString("text");
                    String time = distanceInformation.getJSONObject("duration").getString("text");
                    distanceTv.setText(distance + " " + time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String jsonError = new String(error.networkResponse.data);
            }
        });
        queue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void clearRestaurants() {
        int size = this.restaurants.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                restaurants.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
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