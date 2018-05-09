package voyager.voyager;

import android.app.ProgressDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.support.v7.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.internal.StringResourceValueReader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class homeActivity extends AppCompatActivity {
        // Database Initialization
        private FirebaseDatabase database;
        private DatabaseReference userRef, activityDatabase;
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
        CircleImageView drawerProfilePicture;
        ProgressDialog progressDialog;
        private ListView listView;
        private CardListAdapter cardAdapter;

        // Variables Initialization
        String fbUserId;
        boolean searched = false;
        private ArrayList<Card> cardsList;
        private ArrayList<Activity> activities, activitiesBackup;
        private static homeVM vm;
        private User user;
        private ArrayList<HashMap<String,String>> favoriteList;
        private  HashMap<String,ArrayList<HashMap<String,String>>> list;
        //

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);
            vm = ViewModelProviders.of(this).get(homeVM.class);

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
            drawerProfilePicture = header.findViewById(R.id.drawerProfilePicture);
            header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent next = new Intent(homeActivity.this, ProfileActivity.class);
                    startActivity(next);
                }
            });
            // End UI Initialization

            // Database Initialization
            database = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            fbUser = firebaseAuth.getCurrentUser();
            firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(fbUser == null){
                        goToLogin();
                    }
                }
            });
            userRef = database.getReference("User");
            userRef.child(fbUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    user = dataSnapshot.getValue(User.class);
                    setupDrawerUsername();
                    if(dataSnapshot.hasChild("profile_picture")){
                        setupDrawerProfilePicture(user.getProfile_picture());
                    }
                    progressDialog.dismiss();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            activityDatabase = database.getReference("Activities");
            activityDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    saveActivities(dataSnapshot);
                    progressDialog.dismiss();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            // End Database Initialization

            displayProgressDialog(R.string.Loading_events,R.string.Please_Wait);
        }

    @Override
        public void onBackPressed() {
            if(searched){
                activities = activitiesBackup;
                updateActivities();
                activitiesBackup.clear();
                searched = false;
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
        public void saveActivities(DataSnapshot data){
            activities = new ArrayList<>();
            for(DataSnapshot ds : data.getChildren()){
                activities.add(ds.getValue(Activity.class));
            }
            activitiesBackup = activities;
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
        }
        public void displayActivities(){
            for(Activity activity:activities){
                cardsList.add(new Card(activity));
            }
            cardAdapter = new CardListAdapter(this, R.layout.card_layout, cardsList);
            listView.setAdapter(cardAdapter);
            progressDialog.dismiss();
        }
        public void setupDrawerUsername(){
            drawerUsername.setText(user.name + " " + user.lastname);
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
                        updateActivities();
                        hideKeyboard();
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
            Class intentClass = null;
            switch (menu.getItemId()){
                case R.id.homeMenu:
                    intentClass = homeActivity.class;
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
                case R.id.logoutMenu:
                    intentClass = LogInActivity.class;
                    firebaseAuth.signOut();
                    break;
            }
            if(intentClass != this.getClass() && intentClass != null){
                Intent nextView = new Intent(this,intentClass);
                startActivity(nextView);
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
}