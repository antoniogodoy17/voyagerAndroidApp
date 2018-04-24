package voyager.voyager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class homeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    Button button2;
    View header;
    static DatabaseReference database;
    static FirebaseUser fbUser;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        database = FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();

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
                goProfile();
            }
        });

//        button2 = findViewById(R.id.button2);
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent profile = new Intent(getApplicationContext(),ProfileActivity.class);
//                startActivity(profile);
//                //          overridePendingTransition(R.anim.alpha_transition,R.anim.alpha_transition);
//                finish();
//            }
//        });
    }

//    public static FirebaseUser getfbUser(){
//        return fbUser;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void selectDrawerMenu(MenuItem menu){
        Fragment fragment = null;
        Class fragmentClass;

        switch (menu.getItemId()){
            case R.id.categoriesMenu:
                fragmentClass = Categories.class;
                break;
            default:
                fragmentClass = Categories.class;
                break;
        }
        try{
            fragment = (Fragment)fragmentClass.newInstance();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentHandlerLayout,fragment).commit();
        menu.setChecked(true);
        setTitle(menu.getTitle());
        drawerLayout.closeDrawers();
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

    public void goProfile(){


        try{
            Class fragmentClass = Profile.class;
            Fragment fragment = (Fragment)fragmentClass.newInstance();
            FragmentManager fragmentManager = getFragmentManager();
            Log.d("FRAGMENT ", fragment.toString());
            fragmentManager.beginTransaction().replace(R.id.fragmentHandlerLayout,fragment).commit();
//                setTitle(header.toString());
            drawerLayout.closeDrawers();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "dsdgdfsi", Toast.LENGTH_SHORT).show();
        }

    }
}
