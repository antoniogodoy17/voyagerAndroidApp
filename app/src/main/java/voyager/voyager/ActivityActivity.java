package voyager.voyager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityActivity extends AppCompatActivity {
    private DatabaseReference databaseActivity;
    TextView activityTitle, activityPrice, activityDescription, activityDate, activityLocation, activityCategoria;
    RatingBar activityRating;
    Activity activity;
    String activityId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);

        databaseActivity = FirebaseDatabase.getInstance().getReference("Acivities");
        Bundle extras = getIntent().getExtras();

        if(extras != null){
            activityId = extras.getString("_id");
        }

//        databaseActivity. ;

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
    }
}
