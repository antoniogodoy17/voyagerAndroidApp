package voyager.voyager.ui;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.Address;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import voyager.voyager.R;
import voyager.voyager.adapters.CardListAdapter;
import voyager.voyager.models.Activity;
import voyager.voyager.models.Card;
import voyager.voyager.models.FilterInterface.CategoryFilter;
import voyager.voyager.models.FilterInterface.CityFilter;
import voyager.voyager.models.FilterInterface.CostFilter;
import voyager.voyager.models.FilterInterface.DateFilter;
import voyager.voyager.models.FilterInterface.Invoker;
import voyager.voyager.models.FilterInterface.ScoreFilter;
import voyager.voyager.models.FilterInterface.TypeFilter;
import voyager.voyager.models.User;
import voyager.voyager.ui.dialogs.FilterDialog;

public class HomeActivity extends AppCompatActivity implements FilterDialog.NoticeDialogListener {
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
    private FloatingActionButton btnFilters;
    private ActionBarDrawerToggle drawerToggle;
    private CircleImageView drawerProfilePicture;
    private TextView drawerUsername;
    private ListView listView;
    private CardListAdapter cardAdapter;
    private ProgressDialog progressDialog;
    private Dialog filtersDialog;
    //
    // Variables Declarations
    private static final int REQUEST_LOCATION = 1;
    private User user;
    private ArrayList<Card> cardsList;
    private ArrayList<Activity> activities, activitiesBackup, filteredActivities;
    boolean searched = false;
    private Invoker myInvoker;
    private String categorySelected, citySelected,currentCity;
    private  Location location;

    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        categorySelected = null;
        citySelected = null;
        currentCity = null;
        //Get Location and current City


        //TO get the location,manifest file is added with 2 permissions
        //Location Manager is used to figure out which location provider needs to be used.
        LocationManager locationManager=(LocationManager)getSystemService(LOCATION_SERVICE);


        //Best location provider is decided by the criteria
        Criteria criteria=new Criteria();
        //location manager will take the best location from the criteria
        locationManager.getBestProvider(criteria, true);

        //nce you know the name of the LocationProvider, you can call getLastKnownPosition() to find out where you were recently.

        if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            location=locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria,true));
        }


        Geocoder gcd=new Geocoder(getBaseContext(), Locale.getDefault());

        Log.d("Tag","1");
        List<Address> addresses;

        try {
            addresses=gcd.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            if(addresses.size()>0)

            {
                //while(locTextView.getText().toString()=="Location") {
                currentCity = addresses.get(0).getLocality().toString();
                System.out.println("****************** --->" + currentCity);
                // }
            }

        } catch (IOException e) {
            e.printStackTrace();

        }




        //End Location and Current City





        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            if(bundle.containsKey("Category")) {
                categorySelected = bundle.getString("Category");
            }
            else if(bundle.containsKey("City")){
                citySelected = bundle.getString("City");
                Toast.makeText(this, citySelected, Toast.LENGTH_SHORT).show();
            }


        }


        myInvoker = new Invoker();
        filteredActivities = new ArrayList<>();
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

//        filtersDialog = new Dialog(HomeActivity.this);
//        filtersDialog.setContentView(R.layout.filters_layout);
        btnFilters = findViewById(R.id.btnFilters);
        // End UI Initialization

        btnFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialog dialog = new FilterDialog();
                dialog.show(getSupportFragmentManager(),"Filters");
//                filtersDialog.show();
            }
        });

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent next = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(next);
            }
        });

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
    public void onFiltersApplied(HashMap<String,String> filters) {
        myInvoker.setFilteredActivities(activities);
        for(Map.Entry m:filters.entrySet()){
            switch(m.getKey().toString()){
                case("Categories"):
                    if(m.getValue() != ""){
                        CategoryFilter myCategoryFilter = new CategoryFilter(m.getValue().toString());
                        myInvoker.setFilter(myCategoryFilter);

                    }
                    break;
                case("Cost"):
                    if(Integer.parseInt(m.getValue().toString()) < 5){
                        CostFilter myCostFilter = new CostFilter(m.getValue().toString());
                        myInvoker.setFilter(myCostFilter);
                    }
                    break;
                case("Date"):
                    if(m.getValue() != ""){
                        DateFilter myDateFilter = new DateFilter("Today",m.getValue().toString());
                        myInvoker.setFilter(myDateFilter);
                    }
                    break;
                case("Rating"):
                    if(Integer.parseInt(m.getValue().toString()) > 1){
                        ScoreFilter myScoreFilter = new ScoreFilter(m.getValue().toString());
                        myInvoker.setFilter(myScoreFilter);
                    }
                    break;
                case("Type"):
                    if(m.getValue() != ""){
                        TypeFilter myTypeFilter = new TypeFilter(m.getValue().toString());
                        myInvoker.setFilter(myTypeFilter);
                    }
                    break;

            }
        }

        filteredActivities = myInvoker.applyFilters();
        displayFilteredActivities();
        filteredActivities = activities;

    }

    @Override
    public void onBackPressed() {
        if(searched){
            activities = activitiesBackup;
            searched = false;
            displayActivities();
        }
        if(categorySelected != null){

            categorySelected = null;

            if(currentCity != null){
                myInvoker.setFilteredActivities(activities);

                CityFilter myCityFilter = new CityFilter(currentCity);
                myInvoker.setFilter(myCityFilter);
                filteredActivities = myInvoker.applyFilters();
                displayFilteredActivities();
            }
            else{
                displayActivities();
            }

        }
        else if(citySelected != null){
            if(!citySelected.equals(currentCity)){
                citySelected = null;
                if(currentCity != null){
                    myInvoker.setFilteredActivities(activities);

                    CityFilter myCityFilter = new CityFilter(currentCity);
                    myInvoker.setFilter(myCityFilter);
                    filteredActivities = myInvoker.applyFilters();
                    displayFilteredActivities();
                }
                else{
                    displayActivities();
                }
            }



        }

        else {
            if(currentCity != null){
                myInvoker.setFilteredActivities(activities);

                CityFilter myCityFilter = new CityFilter(currentCity);
                myInvoker.setFilter(myCityFilter);
                filteredActivities = myInvoker.applyFilters();
                displayFilteredActivities();
            }
            else{
                displayActivities();
            }
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
        filteredActivities = activities;
        sortDate();
        if(categorySelected!= null){
            myInvoker.setFilteredActivities(activities);
            CategoryFilter myCategoryFilter = new CategoryFilter(categorySelected);
            myInvoker.setFilter(myCategoryFilter);
            filteredActivities = myInvoker.applyFilters();
            displayFilteredActivities();
        }else if(citySelected != null){
            myInvoker.setFilteredActivities(activities);
            CityFilter myCityFilter = new CityFilter(citySelected);
            myInvoker.setFilter(myCityFilter);
            filteredActivities = myInvoker.applyFilters();
            displayFilteredActivities();
        }
        else if(currentCity != null){
            myInvoker.setFilteredActivities(activities);
            CityFilter myCityFilter = new CityFilter(currentCity);
            myInvoker.setFilter(myCityFilter);
            activities = myInvoker.applyFilters();
            displayActivities();
        }

        else{
            displayActivities();
        }
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

    public void displayFilteredActivities(){
        cardsList.clear();
        for(Activity activity:filteredActivities){
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