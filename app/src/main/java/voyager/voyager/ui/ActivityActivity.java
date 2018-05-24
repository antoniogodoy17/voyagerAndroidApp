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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import voyager.voyager.adapters.ReviewAdapter;
import voyager.voyager.models.Activity;
import voyager.voyager.R;
import voyager.voyager.models.Review;
import voyager.voyager.models.User;
import voyager.voyager.ui.dialogs.ListCreationDialog;
import voyager.voyager.ui.dialogs.ListSelectorDialog;
import voyager.voyager.ui.dialogs.ReviewDialog;
import voyager.voyager.ui.dialogs.WatchReviewsDialog;

public class ActivityActivity extends AppCompatActivity implements ListSelectorDialog.NoticeDialogListener, ListCreationDialog.NoticeDialogListener, ReviewDialog.NoticeDialogListener{
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
    Button closeButton, btnActivityShowReviews;
    ProgressDialog progressDialog;
    // Variables Setup
    private User user;
    Activity activity;
    private ArrayList<HashMap<String,String>> favoriteList;
    private HashMap<String,ArrayList<HashMap<String,String>>> lists;
    private ArrayList<String> listNames;
    ArrayList<HashMap<String,String>> reviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity);
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .build();
        Slidr.attach(this, config);

        activity = (Activity) getIntent().getSerializableExtra("activity");
        favoriteList = new ArrayList<>();
        listNames = new ArrayList<>();
        lists = new HashMap<>();
        reviews = new ArrayList<>();

        activityScore = findViewById(R.id.activityScore);
        activityRating = findViewById(R.id.activityRating);
        activityRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(fromUser){
                    Bundle bundle = new Bundle();
                    bundle.putString("title",activity.getTitle());
                    bundle.putDouble("rating", activityRating.getRating());

                    ReviewDialog dialog = new ReviewDialog();
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(),activity.getTitle());
                }
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

                if(activity.getReviews() != null){
                    reviews = activity.getReviews();
                    ActivityActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activityScore.setText(Double.toString(Math.round(activity.getScore()*100.0)/100.0));
                        }
                    });
                }
                else{
                    activityScore.setText("0");
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
        btnActivityShowReviews = findViewById(R.id.btnActivityShowReviews);
        btnActivityShowReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(activity.getReviews()!=null){
                    if(activity.getReviews().size()>0){
                        WatchReviewsDialog dialog = new WatchReviewsDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("id",activity.get_id());
                        dialog.setArguments(bundle);
                        dialog.show(getSupportFragmentManager(),"Watch reviews");
                    }
                }
                else{
                    Toast.makeText(ActivityActivity.this, R.string.No_Reviews, Toast.LENGTH_SHORT).show();
                }
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

    @Override
    public void onReviewCreated(String review) {
        createReview(review,activityRating.getRating());
    }

    @Override
    public void onReviewCanceled() {
        activityRating.setRating(0);
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
        activityRating.setRating(0);

//        if (activity.getImages() != null) activityHeader.setImageURI();
        activityPrice.setText(makeCost(activity.getCost()));
        if (activity.getDescription() != "") activityDescription.setText(activity.getDescription());
        else activityCategory.setVisibility(View.GONE);

        if(!activity.getType().equals("Restaurante")){
            if (!activity.getDate().isEmpty()) activityDate.setText(activity.getDate());
        }
        else{
            activityDate.setText(activity.getSchedule());
        }
        activityLocation.setText(activity.getLocation().get("address"));
//        activityLocation.setVisibility(View.GONE);
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
                bookmarkedList.remove(listItem);
                listRef.child(listName).setValue(bookmarkedList);
                Toast.makeText(this, getResources().getString(R.string.Activity_removed_from_list), Toast.LENGTH_SHORT).show();
            }
            else{
                bookmarkedList.add(listItem);
                listRef.child(listName).setValue(bookmarkedList);
                Toast.makeText(this, getResources().getString(R.string.Activity_added_to_list), Toast.LENGTH_SHORT).show();
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

    public void createReview(String review, double rating){
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date today = Calendar.getInstance().getTime();
        String strDate = sdf.format(today);
        HashMap<String,String> newReview = new HashMap<>();
        newReview.put("id",FirebaseAuth.getInstance().getCurrentUser().getUid());
        newReview.put("rating",String.valueOf(rating));
        newReview.put("review",review);
        newReview.put("date", strDate);

        if(reviews.isEmpty()){
            reviews = new ArrayList<>();
            reviews.add(newReview);
        }
        else{
            reviews.add(newReview);
        }
        activitiesReference.child("reviews").setValue(reviews);
    }

}