package voyager.voyager;

import android.app.Fragment;
import android.app.FragmentManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

public class homeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    View header;
    LinearLayout progressBarLayout;
    ProgressBar progressBar;
    private ListView listView;
    private ArrayList<Card> cardsList;
    private ArrayList<Activity> activities;
    private static homeVM vm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        vm = ViewModelProviders.of(this).get(homeVM.class);

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
                Intent next = new Intent(homeActivity.this, ProfileActivity.class);
                startActivity(next);
                setTitle("Profile");
            }
        });

        //Drawer Menu > Setting up username
        setDrawerUserName();

        //Other elements
        listView = findViewById(R.id.listView);

        cardsList = new ArrayList<Card>();
        activities = vm.getActivitiesMap();



        for(Activity activity:activities){
            cardsList.add(new Card("drawable://"+R.drawable.logo512,activity.title));
        }
        //End loop

        CardListAdapter cardAdapter = new CardListAdapter(this, R.layout.card_layout, cardsList);
        listView.setAdapter(cardAdapter);

//        progressBarLayout.setVisibility(View.GONE);
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
    public void setDrawerUserName(){
        TextView username = header.findViewById(R.id.drawerUsername);
        if(vm.getFbUser().getDisplayName().isEmpty())
            username.setText("Username");
        else
            username.setText(vm.getFbUser().getDisplayName());
    }
}
