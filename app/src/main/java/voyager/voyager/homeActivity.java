package voyager.voyager;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class homeActivity extends AppCompatActivity {
    // Database Initialization
    private FirebaseDatabase database;
    private DatabaseReference usersDatabase, activityDatabase;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    //

    // UI Initialization
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private MenuItem searchItem;
    private SearchView searchView;
    View header;
    TextView drawerUsername;
    ProgressDialog progressDialog;
    private ListView listView;
    private CardListAdapter cardAdapter;

    // Variables Initialization
    String fbUserId;
    private ArrayList<Card> cardsList;
    private ArrayList<Activity> activities;
    private static homeVM vm;
    private User user;
    private ArrayList<HashMap<String,String>> favoriteList;
    //

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        vm = ViewModelProviders.of(this).get(homeVM.class);

        // Database Initialization
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(fbUser == null){
                    goToLogin();
                }
                else{
                    synchronized (authListener) {
                        fbUserId = fbUser.getUid();
                        setDrawerUserName();
                        System.out.println("----------------------------- > " + fbUser.getDisplayName() + " < ---------------------------");
                    }
                }
            }
        };
//        usersDatabase = database.getReference("User").child(fbUserId);
        usersDatabase = database.getReference("User");
        usersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
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
        activityDatabase = database.getReference("Activities");
        activityDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saveActivities(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        // End Database Initialization

        // UI Initialization
        progressDialog = new ProgressDialog(this);
        NavigationView navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);
        favoriteList = new ArrayList<>();
        listView = findViewById(R.id.listView);
        cardsList = new ArrayList<Card>();
        header = navigationView.getHeaderView(0);
        drawerUsername = header.findViewById(R.id.drawerUsername);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(homeActivity.this, ProfileActivity.class);
                startActivity(next);
            }
        });
        // End UI Initialization

        displayProgressDialog(R.string.Loading_events,R.string.Please_Wait);
//        displayActivities();
    }
    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
    }
    public void goToLogin(){
        Intent login = new Intent(this,LogInActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }
    public void saveActivities(DataSnapshot data){
        activities = new ArrayList<>();
        for(DataSnapshot ds : data.getChildren()){
            activities.add(ds.getValue(Activity.class));
        }
        sortDate();
        displayActivities();
    }
    public void updateActivities(){
        cardAdapter.clear();
        for(Activity activity:activities){
            cardAdapter.add(new Card(activity));
        }
        homeActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cardAdapter.notifyDataSetChanged();
            }
        });
        progressDialog.dismiss();
        listView.setVisibility(View.VISIBLE);
    }
    public void displayActivities(){
        for(Activity activity:activities){
            cardsList.add(new Card(activity));
        }
        cardAdapter = new CardListAdapter(this, R.layout.card_layout, cardsList);
        listView.setAdapter(cardAdapter);
        progressDialog.dismiss();
//        listView.setClickable(true);
//        cardAdapter.notifyDataSetChanged();
    }
    public void setDrawerUserName(){
        if(fbUser.getDisplayName().isEmpty())
            drawerUsername.setText("Username");
        else
            drawerUsername.setText(fbUser.getDisplayName());
    }
    public static homeVM getViewModel(){
        return vm;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.searchview, menu);
        searchItem = menu.findItem(R.id.actionSearch);
        searchView = (SearchView)searchItem.getActionView();
        searchView.setQueryHint("Search an event here");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!query.isEmpty()){
                    displayProgressDialog(R.string.Loading_events,R.string.Please_Wait);
                    activities = searchActivity(query);
                    updateActivities();
                }
                searchView.setQuery("", false);
                searchView.setIconified(true);
                return true;
                // Save an alternate list for the query result.
                // Add a variable to track if a search has been made
                // If so, track when the BACK button is pressed and reset the list
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void selectDrawerMenu(MenuItem menu){
        Intent next = null;
        switch (menu.getItemId()){
            case R.id.homeMenu:
                next = new Intent(this,homeActivity.class);
                break;
            case R.id.categoriesMenu:
                next = new Intent(this,CategoriesActivity.class);
                break;
            case R.id.favoritesMenu:
                next = new Intent(this,ListActivity.class);
                break;
            case R.id.listsMenu:
                next = new Intent(this,ListsActivity.class);
                break;
            case R.id.switchLocationMenu:
                next = new Intent(this,SwitchLocationActivity.class);
                break;
            case R.id.logoutMenu:
                firebaseAuth.signOut();
                next = new Intent(this, LogInActivity.class);
                break;
        }
        startActivity(next);
    }
    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerMenu(item);
                return true;
            }
        });
    }
    private void sortDate(){
        Collections.sort(activities, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
    }
    private ArrayList<Activity> searchActivity(String search) {
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
    public void addFavorite(String id ){
        //System.out.println("----------------------------> "+ user.getId());
        HashMap<String,String> newFavorite = new HashMap<>();
        newFavorite.put("id", id);
        favoriteList.add(newFavorite);
        try {
//            favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            usersDatabase.child("-LAnypCKztq8359duHiA").child("list").child("favorite").setValue(favoriteList);
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
        catch (Exception e){
            e.printStackTrace();
        }
    }

//    public void removeFavorite(){
//        System.out.println("----------------------------> "+ user.getId());
//
//        HashMap<String,ArrayList<HashMap<String,String>>> list = user.getLists();
//        System.out.println("-------------->"+list.values());
//        for(int i = 0; i < list.get("favorite").size(); i++){
//            System.out.print("-------@!#@!#-------------> " +list.get("favorite").get(i));
//        }
////        Query removeQuery =
//               // usersDatabase.child("-LAnypCKztq8359duHiA").child("list").child("favorite").child("0").child("id").removeValue();
////                        .addValueEventListener(new ValueEventListener() {
////                    @Override
////                    public void onDataChange(DataSnapshot dataSnapshot) {
////                        for(DataSnapshot d: dataSnapshot.getChildren()){
////                            System.out.println("-----------------------------> "+d.getRef());
////                        }
////                    }
////
////                    @Override
////                    public void onCancelled(DatabaseError databaseError) {
////
////                    }
////                });
////        removeQuery.addListenerForSingleValueEvent(new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                for(DataSnapshot ds:dataSnapshot.getChildren()){
////                    System.out.println("***************** ------------->   Que pedo prroooooo!!!!" + ds.getKey());
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        });
//    }
}