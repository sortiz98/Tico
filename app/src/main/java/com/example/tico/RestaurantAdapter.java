package com.example.tico;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.nl.translate.Translator;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private List<Restaurant> restaurants;
    private RequestQueue requestQueue;
    private Context context;
    private Translator translator;
    private static String cuisine;
    private HashMap<SeekBar, Integer> map;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView restaurantNameTv;
        TextView restaurantDistanceTv;
        TextView restaurantScoreTv;
        ImageView restaurantPhotoIv;
        TextView authStampText;
        TextView authBarLabel;
        ImageView frameView;
        ImageView authenticStamp;
        SeekBar mexicanBar;
        SeekBar indianBar;
        SeekBar chineseBar;
        SeekBar japaneseBar;
        SeekBar bar;
        public ViewHolder(View itemView) {
            super(itemView);
            this.restaurantNameTv = itemView.findViewById(R.id.restaurantName);
            this.restaurantDistanceTv = itemView.findViewById(R.id.restaurantDistance);
            this.restaurantScoreTv = itemView.findViewById(R.id.restaurantScore);
            this.restaurantPhotoIv = itemView.findViewById(R.id.restaurantPhoto);
            this.authStampText = itemView.findViewById(R.id.authStampText);
            this.authBarLabel = itemView.findViewById(R.id.authBarLabel);
            this.frameView = itemView.findViewById(R.id.frameView);
            this.authenticStamp = itemView.findViewById(R.id.authenticStamp);

            this.mexicanBar = itemView.findViewById(R.id.seekBarMexican);
            if (cuisine.equals("mexican")) {
                this.bar = this.mexicanBar;
            } else {
                this.mexicanBar.setVisibility(View.INVISIBLE);
            }

            this.chineseBar = itemView.findViewById(R.id.seekBarChinese);
            if (cuisine.equals("chinese")) {
                this.bar = this.chineseBar;
            } else {
                this.chineseBar.setVisibility(View.INVISIBLE);
            }

            this.japaneseBar = itemView.findViewById(R.id.seekBarJapanese);
            if (cuisine.equals("japanese")) {
                this.bar = this.japaneseBar;
            } else {
                this.japaneseBar.setVisibility(View.INVISIBLE);
            }

            this.indianBar = itemView.findViewById(R.id.seekBarIndian);
            if (cuisine.equals("indian")) {
                this.bar = this.indianBar;
            } else {
                this.indianBar.setVisibility(View.INVISIBLE);
            }


            //this.bar.setThumb(flag);
        }
    }

    public RestaurantAdapter(List<Restaurant> restaurants, Context context, Translator translator, String cuisine) {
        this.restaurants = restaurants;
        this.context = context;
        this.translator = translator;
        this.cuisine = cuisine;
        map = new HashMap<>();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View restaurantView = inflater.inflate(R.layout.cards_layout, parent, false);
        return new ViewHolder(restaurantView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(RestaurantAdapter.ViewHolder holder, final int position) {
        TextView nameTv = holder.restaurantNameTv;
        final TextView distanceTv = holder.restaurantDistanceTv;
        final TextView scoreTv = holder.restaurantScoreTv;
        ImageView photoIv = holder.restaurantPhotoIv;
        final Restaurant restaurant = restaurants.get(position);
        TextView authBarLabel = holder.authBarLabel;
        TextView authStampText = holder.authStampText;
        ImageView frameView = holder.frameView;
        ImageView authenticStamp = holder.authenticStamp;
        SeekBar bar = holder.bar;

        translate(authBarLabel, "authenticity");
        translate(authStampText, "authentic");
        nameTv.setText(restaurant.getName());
        String photoURL = restaurant.getPhotoURL();
        if (photoURL.length() != 0) {
            Picasso.get().load(photoURL).resize(200, 0).centerCrop().into(photoIv);
        }
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


        int rating = restaurant.getScore();
        map.put(bar, rating);
        //bar.setThumb(flag);
        //bar.setProgress(0); // call these two methods before setting progress.
        //bar.setMax(100);


        scoreTv.setText(Integer.toString(rating));


        // Change color of seekbar progress according to rating
        int barColor;
        if (rating >= 80) {
            //barColor = Color.parseColor("#72D74F");
            bar.setProgressDrawable(context.getDrawable(R.drawable.green_bar));
            frameView.setImageResource(R.drawable.frame);
            authenticStamp.setImageResource(R.drawable.blank_stamp);
            authStampText.setTextColor(Color.WHITE);
            authenticStamp.setVisibility(View.VISIBLE);
            authStampText.setVisibility(View.VISIBLE);
            frameView.setVisibility(View.VISIBLE);

        } else if (rating >= 50) {
            frameView.setVisibility(View.INVISIBLE);
            authenticStamp.setVisibility(View.INVISIBLE);
            authStampText.setVisibility(View.INVISIBLE);
            bar.setProgressDrawable(context.getDrawable(R.drawable.yellow_bar));
            //barColor = Color.parseColor("#F5E135");
        }  else if (rating > 25)  {
            frameView.setVisibility(View.INVISIBLE);
            authenticStamp.setVisibility(View.INVISIBLE);
            authStampText.setVisibility(View.INVISIBLE);
            bar.setProgressDrawable(context.getDrawable(R.drawable.orange_bar));
            //barColor = Color.parseColor("#F6B831");
        } else {
            frameView.setVisibility(View.INVISIBLE);
            authenticStamp.setVisibility(View.INVISIBLE);
            authStampText.setVisibility(View.INVISIBLE);
            bar.setProgressDrawable(context.getDrawable(R.drawable.red_bar));
            //barColor = Color.parseColor("#FC1204");
        }

        bar.setProgress(rating);
        scoreTv.setText(Integer.toString(rating));

        RequestQueue queue = Volley.newRequestQueue(this.context);
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.GET, distanceURL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject distanceInformation = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);
                    String distance = distanceInformation.getJSONObject("distance").getString("text");
                    String time = distanceInformation.getJSONObject("duration").getString("text");
                    restaurant.distance = Double.valueOf(distance.split("\\s")[0]);
                    restaurant.time = Double.valueOf(time.split("\\s")[0]);

                    // Change color of text according to distance
                    if (restaurant.getDistance() <= 1.5)
                        distanceTv.setTextColor(Color.parseColor("#72D74F"));
                    else if (restaurant.getDistance() <= 2.5)
                        distanceTv.setTextColor(Color.parseColor("#F5E135"));
                    else if (restaurant.getDistance() <= 3.5)
                        distanceTv.setTextColor(Color.parseColor("#F6B831"));
                    else distanceTv.setTextColor(Color.parseColor("#FC1204"));

                    distanceTv.setText(String.valueOf(restaurant.getDistance()));
                    /*if (position == 8) {
                        MainActivity.recyclerView.smoothScrollToPosition(0);
                    }*/
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
        ////MainActivity.recyclerView.smoothScrollToPosition(position);
    }

    public void translate(final TextView view, String str) {
        translator.translate(str).addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                view.setText(s);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public void updateRestaurant(int position) {

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

}