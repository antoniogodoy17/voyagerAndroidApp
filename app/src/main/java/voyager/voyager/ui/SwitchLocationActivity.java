package voyager.voyager.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.utils.L;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import voyager.voyager.R;
import voyager.voyager.adapters.CardListAdapter;
import voyager.voyager.adapters.CityAdapter;
import voyager.voyager.models.Card;
import voyager.voyager.models.City;
import voyager.voyager.models.User;

public class SwitchLocationActivity extends AppCompatActivity {// Database Initialization
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseUser fbUser;
    private FirebaseAuth firebaseAuth;
    //
    // UI Declarations
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView listView;
    private User user;
    TextView drawerUsername;
    CircleImageView drawerProfilePicture;
    CityAdapter cityAdapter;
    View header;
    ArrayList<City> cities;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switch_location);

        NavigationView navigationView = findViewById(R.id.navigationView);
        cities = new ArrayList<>();
        cities.add(new City("Ensenada", "http://www.frontera.info/Edicionenlinea/Fotos/Noticias/1394823-N.JPG"));
        cities.add(new City("Mexicali", "https://elsouvenir.com/wp-content/uploads/2017/02/Mexicali-Baja-California.jpg"));
        cities.add(new City("Rosarito", "https://i.pinimg.com/originals/03/3a/63/033a63fab4de5c3b0a5bda64df817c50.jpg"));
        cities.add(new City("Tecate", "https://cadenanoticias.com/assets/article/00013484/20171113_WYEKT9ivpx.jpg"));
        cities.add(new City("Tijuana", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcROMeQe25AefeeUoUFusutJMzRTThScc-5a3Yp-CzxFaAVgvHfC4w"));
        cityAdapter = new CityAdapter(getApplicationContext(), R.layout.city_layout,cities);
//        for(City c : cities){
//            cityAdapter.add(c);
//        }
        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);

        header = navigationView.getHeaderView(0);
        drawerUsername = header.findViewById(R.id.drawerUsername);
        drawerProfilePicture = header.findViewById(R.id.drawerProfilePicture);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
            }
        });
        listView = findViewById(R.id.switchLocationListView);

        listView.setAdapter(cityAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(SwitchLocationActivity.this, HomeActivity.class);
                i.putExtra("City", cities.get(position).getName());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SwitchLocationActivity.this.startActivity(i);
            }
        });
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
//                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        //End Database Initialization
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    public void setupDrawerUsername(){
        drawerUsername.setText(user.getName() + " " + user.getLastname());
    }
    public void setupDrawerProfilePicture(String url){
        Picasso.get().load(url).into(drawerProfilePicture);
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
    public void goToProfile(){
        Intent profile = new Intent(this,ProfileActivity.class);
        startActivity(profile);
        finish();
    }
    public void goToLogin(){
        Intent login = new Intent(this,LogInActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }
}
