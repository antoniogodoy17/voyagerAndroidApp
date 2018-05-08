package voyager.voyager;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityActivity extends AppCompatActivity {
    // Database Setup
    private FirebaseDatabase database;
    private DatabaseReference userRef, activityDatabase;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;
    //

    // UI Setup
    TextView activityPrice, activityDescription, activityDate, activityLocation, activityCategory;
    ImageView activityHeader;
    RatingBar activityRating;
    ImageButton favButton;
   ProgressDialog progressDialog;
    //
    // Variables Setup
    private User user;
    Activity activity;
    private ArrayList<HashMap<String,String>> favoriteList;
    private HashMap<String,ArrayList<HashMap<String,String>>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);

        activity = (Activity) getIntent().getSerializableExtra("activity");
        progressDialog = new ProgressDialog(this);
        activityHeader = findViewById(R.id.activityHeader);
        activityPrice = findViewById(R.id.activityPrice);
        activityRating = findViewById(R.id.activityRating);
        activityDescription = findViewById(R.id.activityDescription);
        activityDate = findViewById(R.id.activityDate);
        activityLocation = findViewById(R.id.activityLocation);
        activityCategory = findViewById(R.id.activityCategory);
        favButton = findViewById(R.id.favButton);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if favorited
                //favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                //else
                addFavorite(activity.get_id());
                favButton.setImageResource(R.drawable.ic_favorited_24dp);

            }
        });

        // Database Initialization
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();
        userRef = database.getReference("User").child(fbUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayProgressDialog(R.string.Please_Wait,R.string.Please_Wait);
                user = dataSnapshot.getValue(User.class);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        // End Database Initialization

        fillData();
    }
    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }
    void fillData(){
//        if (activity.getImages() != null) activityHeader.setImageURI();
        setTitle(activity.getTitle());
        activityPrice.setText(makeCost(activity.getCost()));
//        activityRating.setRating(activity.getReviews());
        if (activity.getDescription() != "") activityDescription.setText(activity.getDescription());
        else activityCategory.setVisibility(View.GONE);
        if (activity.getDate() != "") activityDate.setText(activity.getDate());
        else if (activity.getSchedule() != "") activityDate.setText(activity.getSchedule());
        else activityDate.setVisibility(View.GONE);
//        activityLocation.setText(activity.getLocation().get("address"));
        activityLocation.setVisibility(View.GONE);
        activityCategory.setText(activity.getCategory());
    }
    String makeCost(int i){
        String cost = "";
        for (int j =0; j<i; j++){ cost += "$"; }
        return cost;
    }
    public void setFavoriteList(){
        list = user.getLists();
        favoriteList = list.get("favorite");
        for(HashMap<String,String> hm:favoriteList){
            Toast.makeText(this, "-------------------------->"+hm.get("id"), Toast.LENGTH_SHORT).show();

        }
    }
    public void addFavorite(String id ){
        //System.out.println("----------------------------> "+ user.getId());
        HashMap<String,String> newFavorite = new HashMap<>();
        newFavorite.put("id", id);
        favoriteList.add(newFavorite);
        try {
            //            favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            userRef.child(fbUser.getUid()).child("list").child("favorite").setValue(favoriteList);
            userRef.child(fbUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    displayProgressDialog(R.string.Please_Wait,R.string.Please_Wait);
                    user = dataSnapshot.getValue(User.class);
                    if(dataSnapshot.hasChild("list")){
                        System.out.println("------------> Entreee al if2 prrooooo");
                        setFavoriteList();
                    }
                    System.out.println("--------------------->" + favoriteList.size());
                    progressDialog.dismiss();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public void removeFavorite(String idRemove){
        for(int i =0;i<favoriteList.size();i++){
            if(favoriteList.get(i).get("id").equals(idRemove)){
                favoriteList.remove(i);
            }
        }

        System.out.println("*********************sizeeeeee---->" + favoriteList.size());
        userRef.child(fbUser.getUid()).child("list").child("favorite").setValue(favoriteList);
        userRef.child(fbUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayProgressDialog(R.string.Please_Wait,R.string.Please_Wait);
                user = dataSnapshot.getValue(User.class);
                if(dataSnapshot.hasChild("list")){
                    //System.out.println("------------> Entreee al if2 prrooooo");
                    setFavoriteList();
                }
                System.out.println("--------------------->" + favoriteList.size());
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }
}
