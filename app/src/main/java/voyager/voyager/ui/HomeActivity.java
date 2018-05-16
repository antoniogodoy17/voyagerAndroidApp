package voyager.voyager.ui;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

import voyager.voyager.R;
import voyager.voyager.adapters.CardListAdapter;
import voyager.voyager.models.Activity;
import voyager.voyager.models.Card;
import voyager.voyager.models.User;

public class HomeActivity extends AppCompatActivity {
    // Database Declarations
    private DatabaseReference userRef, activityDatabase;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    private ValueEventListener activityValueListener;
    //
    // UI Declarations
    private SearchView searchView;
    private MenuItem searchItem;
    private View header;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CircleImageView drawerProfilePicture;
    private TextView drawerUsername;
    private ListView listView;
    private CardListAdapter cardAdapter;
    private ProgressDialog progressDialog;
    //
    // Variables Declarations
    private User user;
    private ArrayList<Card> cardsList;
    private ArrayList<Activity> activities, activitiesBackup;
    boolean searched = false;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // UI Initialization
        activities = new ArrayList<>();
        cardsList = new ArrayList<Card>();
        progressDialog = new ProgressDialog(this);
        NavigationView navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);
        listView = findViewById(R.id.listView);
        header = navigationView.getHeaderView(0);
        drawerUsername = header.findViewById(R.id.drawerUsername);
        drawerProfilePicture = header.findViewById(R.id.drawerProfilePicture);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(next);
            }
        });
        displayProgressDialog(R.string.Loading_events,R.string.Please_Wait);
        // End UI Initialization

        // Database Initialization
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() == null){
                    goToLogin();
                }
            }
        };
        firebaseAuth .addAuthStateListener(authListener);
        userRef = FirebaseDatabase.getInstance().getReference("User");
        userRef.child(fbUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(dataSnapshot.hasChild("profile_picture")){
                    setupDrawerProfilePicture(user.getProfile_picture());
                }
                setupDrawerUsername();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        activityDatabase = FirebaseDatabase.getInstance().getReference("Activities");
        activityValueListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadActivities(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        activityDatabase.addValueEventListener(activityValueListener);
        // End Database Initialization
    }

    @Override
    protected void onDestroy() {
        firebaseAuth.removeAuthStateListener(authListener);
        activityDatabase.removeEventListener(activityValueListener);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if(searched){
            activities = activitiesBackup;
            searched = false;
            displayActivities();
        }
        else {
            super.onBackPressed();
        }
    }

    public void hideKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void goToLogin(){
        Intent login = new Intent(this,LogInActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }

    public void loadActivities(DataSnapshot data){
        displayProgressDialog(R.string.Loading_events,R.string.Please_Wait);
        activities.clear();
        for(DataSnapshot ds : data.getChildren()){
            activities.add(ds.getValue(Activity.class));
        }
        activitiesBackup = activities;
        sortDate();
        displayActivities();
    }

    private void sortDate(){
        Collections.sort(activities, new Comparator<Activity>() {
            @Override
            public int compare(Activity o1, Activity o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
    }

    public void displayActivities(){
        cardsList.clear();
        for(Activity activity:activities){
            cardsList.add(new Card(activity));
        }
        cardAdapter = new CardListAdapter(this, R.layout.card_layout, cardsList);
        listView.setAdapter(cardAdapter);
        HomeActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cardAdapter.notifyDataSetChanged();
            }
        });
        progressDialog.dismiss();
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

    public void setupDrawerUsername(){
        drawerUsername.setText(user.getName() + " " + user.getLastname());
    }

    public void setupDrawerProfilePicture(String url){
        Picasso.get().load(url).into(drawerProfilePicture);
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
                displayProgressDialog(R.string.Please_Wait,R.string.Please_Wait);
                if(!query.isEmpty()){
                    searched = true;
                    activities = searchActivity(query);
                    displayActivities();
                    hideKeyboard();
                }
                searchView.setQuery("", false);
                searchView.setIconified(true);
                return true;
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
        Class intentClass = null;
        switch (menu.getItemId()){
            case R.id.homeMenu:
                intentClass = HomeActivity.class;
                break;
            case R.id.categoriesMenu:
                intentClass = CategoriesActivity.class;
                break;
            case R.id.favoritesMenu:
                intentClass = ListActivity.class;
                break;
            case R.id.listsMenu:
                intentClass = ListsActivity.class;
                break;
            case R.id.switchLocationMenu:
                intentClass = SwitchLocationActivity.class;
                break;
            case R.id.nearMeMenu:
                intentClass = MapsActivity.class;
                break;
            case R.id.logoutMenu:
                intentClass = LogInActivity.class;
                firebaseAuth.signOut();
                break;
        }
        if(intentClass != this.getClass() && intentClass != null){
            Intent nextView = new Intent(this,intentClass);
            if(intentClass == ListActivity.class){
                nextView.putExtra("list","favorites");
            }
            startActivity(nextView);
            drawerLayout.closeDrawers();
        }
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