package voyager.voyager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CategoriesActivity extends AppCompatActivity {
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
    View header;
    private ViewPager slidePager;
    private LinearLayout dotsLayout;
    private SliderAdapter sliderAdapter;
    //
    //
    ArrayList<Category> categories;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        categories = new ArrayList<>();
        categories.add(new Category("Titulo 1","Descripcion 1","https://firebasestorage.googleapis.com/v0/b/proyecto-turista-af346.appspot.com/o/Profile%20pictures%2FbR3zmD1qVyX1ZTlEkF0KlDC2tvr2.jpg?alt=media&token=5b2a905f-a4be-44a3-bb0b-652df6a25656"));
        categories.add(new Category("Titulo 2","Descripcion 2","https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwjp9oianvfaAhVD7mMKHYtVAuMQjRx6BAgBEAU&url=http%3A%2F%2Fwww.iconarchive.com%2Fshow%2Fenkel-icons-by-froyoshark%2FBitcoin-icon.html&psig=AOvVaw3jqrO8VIwOeh-j1SUw5dR2&ust=1525907362905153"));
        categories.add(new Category("Titulo 3","Descripcion 3","https://www.google.com/url?sa=i&rct=j&q=&esrc=s&source=images&cd=&cad=rja&uact=8&ved=2ahUKEwiW3uCgnvfaAhVM7GMKHeSUABwQjRx6BAgBEAU&url=https%3A%2F%2Fwww.iconfinder.com%2Ficons%2F291715%2Fbrowser_chrome_google_internet_logo_network_web_icon&psig=AOvVaw3jqrO8VIwOeh-j1SUw5dR2&ust=1525907362905153"));
        slidePager = findViewById(R.id.slidePager);
        dotsLayout = findViewById(R.id.dotsLayout);
        sliderAdapter = new SliderAdapter(CategoriesActivity.this,categories);
        slidePager.setAdapter(sliderAdapter);

        NavigationView navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);

        header = navigationView.getHeaderView(0);
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfile();
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
        // End Database Initialization


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
            if(intentClass == homeActivity.class){
                nextView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            startActivity(nextView);
            finish();
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
