package voyager.voyager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityActivity extends AppCompatActivity {
    TextView activityTitle, activityPrice, activityDescription, activityDate, activityLocation, activityCategoria;
    RatingBar activityRating;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);

        activity = (Activity) getIntent().getSerializableExtra("activity");

        activityTitle = findViewById(R.id.activityTitle);
        activityPrice = findViewById(R.id.activityPrice);
        activityRating = findViewById(R.id.activityRating);
        activityDescription = findViewById(R.id.activityDescription);
        activityDate = findViewById(R.id.activityDate);
        activityLocation = findViewById(R.id.activityLocation);
        activityCategoria = findViewById(R.id.activityCategoria);

    }

    void fillData(){
        activityTitle.setText(activity.getTitle());
        activityPrice.setText(activity.getCost());
//        activityRating.setRating(activity.getReviews());
        activityDescription.setText(activity.getDescription());
        activityDate.setText(activity.getDate());
        activityLocation.setText(activity.getLocation().get("address"));
        activityCategoria.setText(activity.getCategory());
    }
}
