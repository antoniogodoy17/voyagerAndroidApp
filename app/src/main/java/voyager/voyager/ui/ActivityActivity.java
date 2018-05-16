package voyager.voyager.ui;

import android.app.ProgressDialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import voyager.voyager.models.Activity;
import voyager.voyager.R;
import voyager.voyager.models.User;

public class ActivityActivity extends AppCompatActivity implements ListSelectorDialog.NoticeDialogListener, ListCreationDialog.NoticeDialogListener {
    // Database Setup
    private DatabaseReference userRef, listRef, activitiesReference;
    private ValueEventListener activityListener;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;
    //
    // UI Setup
    TextView activityPrice, activityDescription, activityDate, activityLocation, activityCategory, activityScore;
    ImageView activityHeader;
    RatingBar activityRating;
    ImageButton favButton;
    ImageButton bookmarkButton;
    Button closeButton;
    ProgressDialog progressDialog;
    //
    // Variables Setup
    private User user;
    Activity activity;
    private ArrayList<HashMap<String,String>> favoriteList;
    private HashMap<String,ArrayList<HashMap<String,String>>> lists;
    private ArrayList<String> listNames;
    ArrayList<HashMap<String,String>> ratings;
    HashMap<String,String> userRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .build();
        Slidr.attach(this, config);

        activity = (Activity) getIntent().getSerializableExtra("activity");
//        activity.updateScore();
        favoriteList = new ArrayList<>();
        listNames = new ArrayList<>();
        lists = new HashMap<>();
        ratings = new ArrayList<>();

        activityScore = findViewById(R.id.activityScore);
        activityRating = findViewById(R.id.activityRating);
        activityRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                updateUserRating(firebaseAuth.getCurrentUser().getUid(), activity.get_id(),activityRating.getRating());
            }
        });
        progressDialog = new ProgressDialog(this);
        displayProgressDialog(R.string.Please_Wait,R.string.Please_Wait);

        activitiesReference = FirebaseDatabase.getInstance().getReference("Activities")
                .child(activity.get_id());

        activityListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                activity = dataSnapshot.getValue(Activity.class);

                if(activity.getRatings() != null){
                    ratings = activity.getRatings();
                    ActivityActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activityScore.setText(Double.toString(Math.round(activity.getScore()*100.0)/100.0));
                        }
                    });
                }
                activitiesReference.child("score").setValue(activity.getScore());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        };

        activitiesReference.addValueEventListener(activityListener);

        activityHeader = findViewById(R.id.activityHeader);
        activityPrice = findViewById(R.id.activityPrice);
        activityDescription = findViewById(R.id.activityDescription);
        activityDate = findViewById(R.id.activityDate);
        activityLocation = findViewById(R.id.activityLocation);
        activityCategory = findViewById(R.id.activityCategory);
        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        favButton = findViewById(R.id.favButton);
        favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFavorite(activity.get_id())){
                    favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    removeFavorite(activity.get_id());
                }
                else{
                    favButton.setImageResource(R.drawable.ic_favorited_24dp);
                    displayProgressDialog(R.string.Please_Wait,R.string.Please_Wait);
                    addFavorite(activity.get_id());
                }
            }
        });
        bookmarkButton = findViewById(R.id.bookmarkButton);
        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("lists",listNames);

                ListSelectorDialog dialog = new ListSelectorDialog();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(),"Select a list");
            }
        });
        // Database Initialization
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("User").child(fbUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(dataSnapshot.hasChild("lists")){
                    listRef = userRef.child("lists");
                    listRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            lists = user.getLists();
                            listNames.clear();
                            if(dataSnapshot.hasChild("favorites")){
                                setFavoriteList();
                                fillData();
                            }
                            for(DataSnapshot ds:dataSnapshot.getChildren()){
                                if(!ds.getKey().equals("favorites")){
                                    listNames.add(ds.getKey());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        // End Database Initialization
        fillData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activitiesReference.removeEventListener(activityListener);
    }

    @Override
    public void onListSelected(String list) {
        addToList(list,activity.get_id());
    }

    @Override
    public void onCreateListSelected(DialogFragment dialog) {
        dialog.show(getSupportFragmentManager(),"Create a list");
    }

    @Override
    public void onListCreated(String list) {
        addToList(list,activity.get_id());
    }

    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    void fillData(){
        setTitle(activity.getTitle());

        if(isFavorite(activity.get_id())){
            favButton.setImageResource(R.drawable.ic_favorited_24dp);
        }
        else {
            favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        if (activity.getImage_principal() != null){
            Picasso.get().load(activity.getImage_principal()).into(activityHeader);
        }
        else {
            Picasso.get().load(R.drawable.logo512).into(activityHeader);
        }

        if(ratings != null){
            if(hashMapContains(ratings,FirebaseAuth.getInstance().getCurrentUser().getUid())){
                for (HashMap hm:ratings){
                    if(hm.get("id").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                        activityRating.setRating(Float.valueOf(hm.get("rating").toString()));
                    }
                }
            }
        }
        else {
            activityRating.setRating(0);
        }

//        if (activity.getImages() != null) activityHeader.setImageURI();
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
        progressDialog.dismiss();
    }

    String makeCost(int i){
        String cost = "";
        for (int j =0; j<i; j++){ cost += "$"; }
        return cost;
    }

    public void setFavoriteList(){
        favoriteList = lists.get("favorites");
        progressDialog.dismiss();
    }

    public void addFavorite(String id){
        HashMap<String,String> newFavorite = new HashMap<>();
        newFavorite.put("id", id);
        favoriteList.add(newFavorite);
        try {
            userRef.child("lists").child("favorites").setValue(favoriteList);
            progressDialog.dismiss();
        }
        catch (Exception e){
            e.printStackTrace();
            progressDialog.dismiss();
        }
    }

    public boolean isFavorite(String actId){
        for(HashMap<String, String> hm:favoriteList){
            if(hm.get("id").equals(actId)){
                return true;
            }
        }
        return false;
    }

    public boolean hashMapContains(ArrayList<HashMap<String,String>> list, String id){
        for(HashMap hm : list){
            if(hm.containsValue(id)){
                return true;
            }
        }
        return false;
    }

    public void removeFavorite(String idRemove){
        for(int i =0;i<favoriteList.size();i++){
            if(favoriteList.get(i).get("id").equals(idRemove)){
                favoriteList.remove(i);
            }
        }
        userRef.child("lists").child("favorites").setValue(favoriteList);
    }

    public void addToList (String listName, String activityId){
        ArrayList<HashMap<String, String>> bookmarkedList;
        HashMap<String , String> listItem = new HashMap<>();
        listItem.put("id", activityId);

        if(lists.isEmpty()){
            bookmarkedList = new ArrayList<>();
            bookmarkedList.add(listItem);
            HashMap<String,ArrayList<HashMap<String,String>>> list = new HashMap<>();
            list.put(listName,bookmarkedList);

            userRef.child("lists").setValue(list);
            Toast.makeText(this, getResources().getString(R.string.Activity_added_to_list), Toast.LENGTH_SHORT).show();
            finish();
        }
        else if(lists.containsKey(listName)){
            bookmarkedList = lists.get(listName);
            if(hashMapContains(bookmarkedList,activityId)){
                Toast.makeText(this, getResources().getString(R.string.Activity_already_on_list), Toast.LENGTH_SHORT).show();
            }
            else{
                bookmarkedList.add(listItem);
                listRef.child(listName).setValue(bookmarkedList);
                Toast.makeText(this, getResources().getString(R.string.Activity_added_to_list), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        else{
            bookmarkedList = new ArrayList<>();
            bookmarkedList.add(listItem);
            listRef.child(listName).setValue(bookmarkedList);
            Toast.makeText(this, getResources().getString(R.string.Activity_added_to_list), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void updateUserRating(String userId, String activityId, final double rating){
        HashMap<String,String> newRating = new HashMap<>();
        newRating.put("id",userId);
        newRating.put("rating",String.valueOf(rating));

        if(ratings.isEmpty()){
            ratings = new ArrayList<>();
            ratings.add(newRating);
        }
        else{
            if(hashMapContains(ratings,userId)){
                ArrayList<HashMap<String,String>> tempRatings = new ArrayList<>();
                for(HashMap hm:ratings){
                    if(!hm.get("id").equals(userId)){
                        tempRatings.add(hm);
                    }
                }
                tempRatings.add(newRating);
                ratings = tempRatings;
            }
            else{
                ratings.add(newRating);
            }
        }

        activitiesReference.child("ratings").setValue(ratings);

//        activitiesReference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                ratings = new ArrayList<>();
//                for(DataSnapshot ds:dataSnapshot.getChildren()){
//                    HashMap<String,String> userRating = new HashMap<>();
//                    userRating.put("id",ds.child("id").getValue().toString());
//                    userRating.put("rating",String.valueOf(ds.child("rating").getValue()));
//                    ratings.add(userRating);
//                }
//                System.out.println("***************************************************");
//                for (HashMap hm:ratings){
//                    System.out.println(" ID: " + hm.get("id") + "; RATING: " + hm.get("rating"));
//                }
//                System.out.println("***************************************************");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) { }
//        });

//        activitiesReference.setValue(newRating);
    }
}