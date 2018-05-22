package voyager.voyager.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import voyager.voyager.R;
import voyager.voyager.models.Review;

public class ReviewAdapter extends ArrayAdapter<Review> {
    private Context context;
    private int resource;
    private ArrayList<Review> reviews;

    private static class ViewHolder {
        RatingBar ratingBar;
        TextView reviewText;
        TextView reviewTimestamp;
    }

    public ReviewAdapter(Context context, int resource, ArrayList<Review> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.reviews = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //get the cards information
        String reviewText = getItem(position).getReview();
        float rating = (float)getItem(position).getRating();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/mm/yyyy");
        String reviewDate = sdf.format(getItem(position).getDate()).toString();

        try{
            ViewHolder holder;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(resource, parent, false);
                holder = new ViewHolder();
                holder.ratingBar = convertView.findViewById(R.id.reviewRatingBar);
                holder.reviewText = convertView.findViewById(R.id.reviewText);
                holder.reviewTimestamp = convertView.findViewById(R.id.reviewTimestamp);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder)convertView.getTag();
            }
            holder.ratingBar.setRating(rating);
            holder.reviewText.setText(reviewText);
            holder.reviewTimestamp.setText(reviewDate);

            return convertView;
        }catch (IllegalArgumentException e){
            return convertView;
        }
    }
}