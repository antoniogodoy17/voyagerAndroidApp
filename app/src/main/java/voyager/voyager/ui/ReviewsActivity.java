package voyager.voyager.ui;
import voyager.voyager.R;
import voyager.voyager.adapters.ReviewAdapter;
import voyager.voyager.models.Review;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

public class ReviewsActivity extends AppCompatActivity {
    ListView reviewsListView;
    ArrayList<Review> reviews;
    ReviewAdapter reviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        reviews = new ArrayList<Review>();
        reviewsListView = findViewById(R.id.revActivityListView);
    }
}
