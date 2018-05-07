package voyager.voyager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import javax.security.auth.login.LoginException;

public class homeActivity extends AppCompatActivity {
    // Database Setup
    private FirebaseDatabase database;
    private DatabaseReference usersDatabase, activityDatabase;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;
    //

    // UI Setup
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    View header;
    TextView drawerUsername;
    LinearLayout progressBarLayout;
    ProgressBar progressBar;
    private ListView listView;
    private CardListAdapter cardAdapter;

    // Variables Setup
    private ArrayList<Card> cardsList;
    private ArrayList<Activity> activities;
    private static homeVM vm;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        vm = ViewModelProviders.of(this).get(homeVM.class);

        // UI Initialization
        NavigationView navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);
        listView = findViewById(R.id.listView);
        cardsList = new ArrayList<Card>();
        progressBarLayout = findViewById(R.id.progressBarLayout);
        progressBarLayout.setVisibility(View.VISIBLE);
        header = navigationView.getHeaderView(0);
        drawerUsername = header.findViewById(R.id.drawerUsername);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(homeActivity.this, ProfileActivity.class);
                startActivity(next);
                setTitle("Profile");
            }
        });
        // End UI Initialization

        // Database Initialization
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();
        usersDatabase = database.getReference("User");
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
        activityDatabase = database.getReference("Activities");
        activityDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                saveActivities(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // End Database Initialization
        setDrawerUserName();
    }
    public void saveActivities(DataSnapshot data){
        activities = new ArrayList<>();
        for(DataSnapshot ds : data.getChildren()){
            activities.add(ds.getValue(Activity.class));
            System.out.println(activities.size());
        }
        displayActivities();
    }
    public void displayActivities(){
        for(Activity activity:activities){
            cardsList.add(new Card(activity));
        }
        cardAdapter = new CardListAdapter(this, R.layout.card_layout, cardsList);
        listView.setAdapter(cardAdapter);
        progressBarLayout.setVisibility(View.GONE);
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
                next = new Intent(this, LogInActivity.class);
                vm.getFirebaseAuth().signOut();
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
}