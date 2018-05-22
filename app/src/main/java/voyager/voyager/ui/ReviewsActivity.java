package voyager.voyager.ui;
import voyager.voyager.R;
import voyager.voyager.adapters.ReviewAdapter;
import voyager.voyager.models.Activity;
import voyager.voyager.models.Review;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ReviewsActivity extends AppCompatActivity {
    private DatabaseReference activityRef;

    ListView reviewsListView;
    ArrayList<Review> reviews;
    ReviewAdapter reviewAdapter;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        activity = (Activity) getIntent().getSerializableExtra("activity");
        reviews = new ArrayList<>();
        reviewsListView = findViewById(R.id.revActivityListView);

        activityRef = FirebaseDatabase.getInstance().getReference("Activities").child(activity.get_id());
        activityRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("reviews")){
                    activity = dataSnapshot.getValue(Activity.class);
                    loadReviews();
                }
                else{

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    protected void loadReviews() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-mm-yyyy");
        for(HashMap<String,String> hm:activity.getReviews()){
            try{
                Date activityDate = sdf.parse(hm.get("date"));
                reviews.add(new Review(hm.get("review"),Double.parseDouble(hm.get("rating")),activityDate));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        reviewAdapter = new ReviewAdapter(ReviewsActivity.this,R.layout.review_layout,reviews);
        reviewsListView.setAdapter(reviewAdapter);
    }
}
