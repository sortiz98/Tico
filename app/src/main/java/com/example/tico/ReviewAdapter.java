package com.example.tico;

        import androidx.annotation.NonNull;
        import androidx.annotation.RequiresApi;
        import androidx.recyclerview.widget.RecyclerView;

        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Color;
        import android.graphics.PorterDuff;
        import android.graphics.PorterDuffColorFilter;
        import android.graphics.drawable.Drawable;
        import android.os.Build;
        import android.util.Pair;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.SeekBar;
        import android.widget.TextView;

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

        import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    List<String> reviews;
    private Context context;
    private Translator translator;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reviewTv;
        public ViewHolder(View itemView) {
            super(itemView);
            this.reviewTv = itemView.findViewById(R.id.reviewText);
        }
    }

    public ReviewAdapter(List<String> reviews, Context context, Translator translator) {
        this.reviews = reviews;
        this.context = context;
        this.translator = translator;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View restaurantView = inflater.inflate(R.layout.reviews_layout, parent, false);
        return new ViewHolder(restaurantView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder holder, int position) {
        TextView reviewTv = holder.reviewTv;
        final String review = reviews.get(position);
        reviewTv.setText(review);
        translate(reviewTv, review);
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
        return reviews.size();
    }
}