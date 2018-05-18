package voyager.voyager.ui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;
import voyager.voyager.models.Category;
import voyager.voyager.R;
import voyager.voyager.adapters.SliderAdapter;
import voyager.voyager.models.User;

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
    TextView drawerUsername;
    CircleImageView drawerProfilePicture;
    private ViewPager slidePager;
    private LinearLayout dotsLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] dots;
    //
    //
    ArrayList<Category> categories;
    private User user;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        categories = new ArrayList<>();
        categories.add(new Category("Entretenimiento","Descripcion 1","https://firebasestorage.googleapis.com/v0/b/proyecto-turista-af346.appspot.com/o/Profile%20pictures%2F1HRhMs9DyFgBOe98jPLLP5ouG162.jpg?alt=media&token=c2968c64-c5d5-4680-9f45-08767a45379c"));
        categories.add(new Category("Deportivo","Descripcion 2","https://firebasestorage.googleapis.com/v0/b/proyecto-turista-af346.appspot.com/o/Profile%20pictures%2FhIlAKCaUzCQ4i6KxgN2vcW2YQlo1.jpg?alt=media&token=9f16f20f-752a-49a8-a1cd-32e0e595d0a0"));
        categories.add(new Category("Social","Descripcion 3","https://firebasestorage.googleapis.com/v0/b/proyecto-turista-af346.appspot.com/o/Profile%20pictures%2FhIlAKCaUzCQ4i6KxgN2vcW2YQlo1.jpg?alt=media&token=9f16f20f-752a-49a8-a1cd-32e0e595d0a0"));
        slidePager = findViewById(R.id.slidePager);
        dotsLayout = findViewById(R.id.dotsLayout);
        sliderAdapter = new SliderAdapter(CategoriesActivity.this,categories);
        slidePager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        slidePager.addOnPageChangeListener(viewListener);

        NavigationView navigationView = findViewById(R.id.navigationView);
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
        // End Database Initialization


    }
    public void setupDrawerUsername(){
        drawerUsername.setText(user.getName() + " " + user.getLastname());
    }
    public void setupDrawerProfilePicture(String url){
        Picasso.get().load(url).into(drawerProfilePicture);
    }
    public void addDotsIndicator(int position){
        dots = new TextView[categories.size()];
        dotsLayout.removeAllViews();

        for(int i=0; i < dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            dotsLayout.addView(dots[i]);
        }
        if(dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.colorSecondaryDark));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
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
