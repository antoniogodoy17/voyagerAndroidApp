package voyager.voyager.ui.dialogs;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import voyager.voyager.R;
import voyager.voyager.adapters.ReviewAdapter;
import voyager.voyager.models.Review;

public class WatchReviewsDialog extends DialogFragment {
    ImageButton btnCloseReviews;
    Button btnShowAllReviews;
    ListView reviewsListView;
    ArrayList<Review> reviews;
    ReviewAdapter reviewAdapter;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.watch_reviews_layout, null);
        builder.setView(view);

        // ArrayList<Review>
        Date date = Calendar.getInstance().getTime();

        reviews = new ArrayList<Review>();
        reviews.add(new Review("Me gusto mucho la comida, esta bien buena, buen ambiente familiar, todo chido. P.D. Amo a Godoy.",5.0, date));
        reviews.add(new Review("Pesima comida, buen ambiente familiar, podria ir de nuevo. P.D. Amo a Godoy.",3.5, date));
        reviews.add(new Review("Nunca volvere a ir, de lo peor a lo que puedes asistir. No recomendado para nadie. P.D. Amo a Godoy.",0.5, date));

        // ReviewAdapter
        reviewAdapter = new ReviewAdapter(getContext(),R.layout.review_layout,reviews);

        reviewsListView = view.findViewById(R.id.reviewsListView);
        reviewsListView.setAdapter(reviewAdapter);
        btnShowAllReviews = view.findViewById(R.id.btnShowAllReviews);
        btnShowAllReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start activity to watch all reviews
                WatchReviewsDialog.this.dismiss();
            }
        });
        btnCloseReviews = view.findViewById(R.id.btnCloseReviews);
        btnCloseReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WatchReviewsDialog.this.dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }
}
