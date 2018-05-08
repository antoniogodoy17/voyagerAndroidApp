package voyager.voyager;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Card> cardsList;
    private ArrayList<Activity> activities;
    private ArrayList<HashMap<String,String>> favoriteList;
    private ArrayList<Activity> favoriteActivites;
    private ArrayList<Activity> activitiesBackup;

    // Database Setup
    private FirebaseDatabase database;
    private DatabaseReference userRef, activityDatabase;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;
    //
    private User user;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);

        cardsList = new ArrayList<Card>();


        progressDialog = new ProgressDialog(this);

        //Implement a loop here to dinamically create Cards with the ordered Activities

//        for(Activity activity:activities){
//            System.out.println("------>" + activity.title);
//        }
        //End loop
//        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 1"));
//        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 2"));
//        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 3"));
//        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 4"));
//        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 5"));


        // Database Initialization
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();
        activityDatabase = database.getReference("Activities");
        activityDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayProgressDialog(R.string.Loading_events,R.string.Please_Wait);
                saveActivities(dataSnapshot);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        userRef = database.getReference("User").child(fbUser.getUid());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayProgressDialog(R.string.Please_Wait,R.string.Please_Wait);
                user = dataSnapshot.getValue(User.class);
                if(dataSnapshot.hasChild("list")){
                    setFavoriteList();
                }
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // End Database Initialization

        CardListAdapter cardAdapter = new CardListAdapter(this, R.layout.card_layout, cardsList);
        listView.setAdapter(cardAdapter);



    }
    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void saveActivities(DataSnapshot data){
        activities = new ArrayList<>();
        for(DataSnapshot ds : data.getChildren()){
            activities.add(ds.getValue(Activity.class));
        }
        activitiesBackup = activities;

        //displayActivities();
    }

    public void setFavoriteList(){

        favoriteList = user.getLists().get("favorite");
        for(int i=0;i<activities.size();i++){
            for(HashMap<String,String> hm:favoriteList){
                if(hm.get("id").equals(activities.get(i)._id)){
                    favoriteActivites.add(activities.get(i));
                }
            }
        }

    }
}