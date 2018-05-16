package voyager.voyager.ui;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
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

import voyager.voyager.ListSelectorDialog;
import voyager.voyager.models.Activity;
import voyager.voyager.R;
import voyager.voyager.models.User;

public class ActivityActivity extends AppCompatActivity implements ListSelectorDialog.NoticeDialogListener {
    // Database Setup
    private FirebaseDatabase database;
    private DatabaseReference userRef, activityDatabase,listRef;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;
    //

    // UI Setup
    TextView activityPrice, activityDescription, activityDate, activityLocation, activityCategory;
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
    private HashMap<String,ArrayList<HashMap<String,String>>> list;
    private ArrayList<String> listNames;

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
        progressDialog = new ProgressDialog(this);
        displayProgressDialog(R.string.Please_Wait,R.string.Please_Wait);
        activityHeader = findViewById(R.id.activityHeader);
        activityPrice = findViewById(R.id.activityPrice);
        activityRating = findViewById(R.id.activityRating);
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
                ListSelectorDialog dialog = new ListSelectorDialog();
                dialog.show(getSupportFragmentManager(),"Select a list");
                // If succeed -> Change image resource
            }
        });
        // Database Initialization
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();
        userRef = database.getReference("User").child(fbUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(dataSnapshot.hasChild("lists")){
                    listRef = userRef.child("lists");
                    listRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
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
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        // End Database Initialization

        fillData();
//        addToList("prueba", "-LBJY-3GpfSEin-Omo0I");
    }

    @Override
    public void onListSelected(android.support.v4.app.DialogFragment dialog, String list, int position) {
        Toast.makeText(this, "Lista: " + list + " - Posicion: " + String.valueOf(position), Toast.LENGTH_SHORT).show();
    }

    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }
    void fillData(){
        if(isFavorite(activity.get_id())){
            favButton.setImageResource(R.drawable.ic_favorited_24dp);
        }
        else{
            favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);

        }

        if (activity.getImage_principal() != null){
            Picasso.get().load(activity.getImage_principal()).into(activityHeader);
        }
        else {
            Picasso.get().load(R.drawable.logo512).into(activityHeader);
        }
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
        progressDialog.dismiss();
    }
    String makeCost(int i){
        String cost = "";
        for (int j =0; j<i; j++){ cost += "$"; }
        return cost;
    }
    public void setFavoriteList(){
        list = user.getLists();
        favoriteList = list.get("favorites");
        progressDialog.dismiss();
    }
    public void addFavorite(String id ){
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
    public void removeFavorite(String idRemove){
        for(int i =0;i<favoriteList.size();i++){
            if(favoriteList.get(i).get("id").equals(idRemove)){
                favoriteList.remove(i);
            }
        }
        userRef.child("lists").child("favorites").setValue(favoriteList);
    }
    public void addToList (String listName, String activityid){
        ArrayList<HashMap<String, String>> newList = new ArrayList<>();
        HashMap<String , String> listItem = new HashMap<>();
        listItem.put("id", activityid);
        newList.add(listItem);
        try{
            userRef.child("lists").child(listName).setValue(newList);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}