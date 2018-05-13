package voyager.voyager;

import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import voyager.voyager.models.Activity;
import voyager.voyager.models.User;

public class homeVM extends ViewModel{
    private FirebaseDatabase database;
    private DatabaseReference usersDatabase, activityDatabase;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;
    private User user;
    private ArrayList<String> activitiesId = new ArrayList<>();
    private ArrayList<Activity> activities,activitiesWDate;

    private Map<String, Object> activitiesMap = new HashMap<>();
    private int count;
    private boolean finish = false;


    public homeVM(){
//        database = FirebaseDatabase.getInstance();
//        firebaseAuth = FirebaseAuth.getInstance();
//        usersDatabase = database.getReference("User");
//        activityDatabase = database.getReference("Activities");
//        activities = new ArrayList<Activity>();

//        fbUser = firebaseAuth.getCurrentUser();
        count = 0;
//        usersDatabase.orderByChild("email").startAt(fbUser.getEmail()).endAt(fbUser.getEmail() + "\uf8ff").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                user = dataSnapshot.getValue(User.class);
//            }
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) { }
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
//            @Override
//            public void onCancelled(DatabaseError databaseError) { }
//        });
    }
    public void init(){
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();
        usersDatabase = database.getReference("User");
        activityDatabase = database.getReference("Activities");
    }
    public void initUser(){
        usersDatabase.orderByChild("email").startAt(fbUser.getEmail()).endAt(fbUser.getEmail() + "\uf8ff").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                user = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    public void fetchActivities(){
        activities = new ArrayList<Activity>();
        activityDatabase.orderByChild("id").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                activities.add(dataSnapshot.getValue(Activity.class));
                System.out.println(activities.size());
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    public FirebaseDatabase getDatabase() {
        return database;
    }
    public FirebaseUser getFbUser() {
        return fbUser;
    }

    public FirebaseAuth getFirebaseAuth() {
        return firebaseAuth;
    }

    public DatabaseReference getUsersDatabase() {
        return usersDatabase;
    }

    public User getUser() {
        return user;
    }

    public void setFbUser(FirebaseUser fbUser) {
        this.fbUser = fbUser;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setActivities(ArrayList<Activity> acts){
        this.activities = acts;
    }

    public ArrayList<Activity> getActivitiesMap(){
        return activities;
    }

    public boolean getFinish(){return finish;}

    public void fillActivitiesDate(){

    }

    public void sortDate(){
        Collections.sort(activities, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });


    }

    public ArrayList<Activity> searchActivity(String search) {
        ArrayList<Activity> searchActivity = new ArrayList<>();
        for (Activity a: activities) {
            for (int i =0; i < a.getTags().size();i ++){
                if(search.equals( a.getTags().get(i).get("tag"))){
                    searchActivity.add(a);

                }
            }
        }
        return searchActivity;
    }

}
