package voyager.voyager.ui;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import voyager.voyager.adapters.ListsAdapter;
import voyager.voyager.R;
import voyager.voyager.models.User;

public class ListsActivity extends AppCompatActivity {
    // Database Declarations
    private DatabaseReference listsRef, userRef;
    private ValueEventListener listsListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authListener;
    //
    //UI Declarations
    private View header;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private CircleImageView drawerProfilePicture;
    private TextView drawerUsername;
    private ListView listView;
    private ProgressDialog progressDialog;
    //
    // Variables Declarations
    private User user;
    private ArrayList<String> lists;
    private ListsAdapter listsAdapter;
    //


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lists);

        // Variables Initialization
        lists = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        // Variables Initialization
        // Database Initialization
        firebaseAuth = FirebaseAuth.getInstance();
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
        userRef.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
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
        listsRef = FirebaseDatabase.getInstance().getReference("User")
                .child(firebaseAuth.getCurrentUser().getUid())
                .child("lists");
        listsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadLists(dataSnapshot);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        listsRef.addValueEventListener(listsListener);
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
                Intent next = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(next);
            }
        });
        // End Database Initialization

        // UI Initialization
        listView = findViewById(R.id.listView);
        // UI Initialization
    }

    @Override
    protected void onDestroy() {
        firebaseAuth.removeAuthStateListener(authListener);
        listsRef.removeEventListener(listsListener);
        super.onDestroy();
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

    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void loadLists(DataSnapshot data){
        displayProgressDialog(R.string.Loading_lists,R.string.Please_Wait);
        lists.clear();
        for(DataSnapshot ds : data.getChildren()){
            if(!ds.getKey().equals("favorites")){
                lists.add(ds.getKey());
            }
        }
        displayLists();
    }

    public void displayLists(){
        listsAdapter = new ListsAdapter(this,R.layout.list_layout,lists);
        listView.setAdapter(listsAdapter);
        ListsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                listsAdapter.notifyDataSetChanged();
            }
        });
        progressDialog.dismiss();
    }

    public void goToLogin(){
        Intent login = new Intent(this,LogInActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
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
            case R.id.logoutMenu:
                intentClass = LogInActivity.class;
                firebaseAuth.signOut();
                break;
        }
        if(intentClass != this.getClass() && intentClass != null){
            Intent nextView = new Intent(this,intentClass);
            if(intentClass == HomeActivity.class){
                nextView.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            }
            startActivity(nextView);
            finish();
        }
    }

    public void setupDrawerUsername(){
        drawerUsername.setText(user.getName() + " " + user.getLastname());
    }

    public void setupDrawerProfilePicture(String url){
        Picasso.get().load(url).into(drawerProfilePicture);
    }
}
