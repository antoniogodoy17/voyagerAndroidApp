package voyager.voyager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    // Database Setup
    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseAuth firebaseAuth;
    private StorageReference userProfileImageRef;
    //
    // UI Setup
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    ProgressDialog progressDialog;
    View header;
    EditText txtNameProfile, txtLastNameProfile, txtEmailProfile, txtPhoneProfile, txtPasswordProfile, txtLocationProfile;
    TextView txtBirthDateProfile;
    Button btnSaveChanges, btnCancel;
    ImageButton btnEditProfile;
    CircleImageView imgProfilePicture;
    Spinner sprCountryProfile, sprStateProfile, sprCityProfile;
    DatePickerDialog datePicker;
    //
    // Variables Setup
    String fbUserId;
    User user;
    String name, lastname, email, phone, birth_date, location, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Database Initialization
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        fbUserId = firebaseAuth.getCurrentUser().getUid();
        userRef = database.getReference("User").child(fbUserId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                displayProgressDialog(R.string.Please_Wait,R.string.Please_Wait);
                user = dataSnapshot.getValue(User.class);
                fillFields();
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
        userProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");
        // End Database Initialization

        // UI Initialization
        progressDialog = new ProgressDialog(this);
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
                Intent next = new Intent(getApplicationContext(),ProfileActivity.class);
                startActivity(next);
            }
        });
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        txtNameProfile = findViewById(R.id.txtNameProfile);
        txtLastNameProfile = findViewById(R.id.txtLastNameProfile);
        txtEmailProfile = findViewById(R.id.txtEmailProfile);
        txtPhoneProfile = findViewById(R.id.txtPhoneProfile);
        txtPasswordProfile = findViewById(R.id.txtPasswordProfile);
        txtLocationProfile = findViewById(R.id.txtLocationProfile);
        sprCountryProfile = findViewById(R.id.sprCountryProfile);
        sprStateProfile = findViewById(R.id.sprStateProfile);
        sprCityProfile = findViewById(R.id.sprCityProfile);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode();
            }
        });
        imgProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(ProfileActivity.this);
            }
        });
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMode();
            }
        });
        txtBirthDateProfile = findViewById(R.id.txtBirthDateProfile);
        txtBirthDateProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar c = Calendar.getInstance();
                int selYear = c.get(Calendar.YEAR); // current year
                int selMonth = c.get(Calendar.MONTH); // current month
                int selDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePicker = new DatePickerDialog(ProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtBirthDateProfile.setText(dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year);
                    }
                }, selYear, selMonth, selDay);
                c.add(Calendar.YEAR,-10);
                datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
                c.add(Calendar.YEAR, -100);
                datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
                datePicker.show();
            }
        });
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
                Toast.makeText(ProfileActivity.this,"Changes have been made." , Toast.LENGTH_LONG).show();
                viewMode();
            }
        });
        // End UI Initialization
    }
    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                StorageReference filePath = userProfileImageRef.child(fbUserId + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(ProfileActivity.this, "Uploaded to database", Toast.LENGTH_SHORT).show();

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            userRef.child("profile_picture").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        Intent selfIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                                        startActivity(selfIntent);

                                        Toast.makeText(ProfileActivity.this, "Profile Image stored to Firebase Database Successfully...", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                    else
                                    {
                                        String message = task.getException().getMessage();
                                        Toast.makeText(ProfileActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
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
        Intent next = null;
        switch (menu.getItemId()){
            case R.id.homeMenu:
                next = new Intent(this,homeActivity.class);
                break;
            case R.id.categoriesMenu:
                next = new Intent(this,CategoriesActivity.class);
                break;
            case R.id.favoritesMenu:
                next = new Intent(this,FavoritesActivity.class);
                break;
            case R.id.listsMenu:
                next = new Intent(this,ListsActivity.class);
                break;
            case R.id.switchLocationMenu:
                next = new Intent(this,SwitchLocationActivity.class);
                break;
            case R.id.logoutMenu:
                firebaseAuth.signOut();
                next = new Intent(this,LogInActivity.class);
                break;
        }
        startActivity(next);
    }
    protected void fillFields(){
        txtNameProfile.setText(user.getName());
        txtLastNameProfile.setText(user.getLastname());
        txtEmailProfile.setText(user.getEmail());
        txtPhoneProfile.setText(user.getPhone());
//        txtPasswordProfile
        txtBirthDateProfile.setText(user.getBirth_date());

        viewMode();
    }

    protected void saveChanges(){
        user.setName(txtNameProfile.getText().toString().trim());
        user.setLastname(txtLastNameProfile.getText().toString().trim());
        user.setEmail(txtEmailProfile.getText().toString().trim());
        user.setPhone(txtPhoneProfile.getText().toString().trim());
        //password pendiente
        user.setBirth_date(txtBirthDateProfile.getText().toString().trim());

        //Agregar aqui los cambios a la base de datos

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(user.getName()+" "+user.getLastname()).build();
        firebaseAuth.getCurrentUser().updateProfile(profileUpdates);
        updateDrawerUserName();
    }

    public void updateDrawerUserName(){
        NavigationView navigationView = findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.drawerUsername);
        username.setText(firebaseAuth.getCurrentUser().getDisplayName());
    }

    protected void editMode(){
        txtNameProfile.setVisibility(View.VISIBLE);
        txtNameProfile.setEnabled(true);

        txtLastNameProfile.setVisibility(View.VISIBLE);
        txtLastNameProfile.setEnabled(true);

        txtEmailProfile.setVisibility(View.VISIBLE);
        txtEmailProfile.setEnabled(true);

        txtPhoneProfile.setVisibility(View.VISIBLE);
        txtPhoneProfile.setEnabled(true);

        txtPasswordProfile.setVisibility(View.VISIBLE);
        txtPasswordProfile.setEnabled(true);

        txtBirthDateProfile.setVisibility(View.VISIBLE);
        txtBirthDateProfile.setEnabled(true);

        txtLocationProfile.setVisibility(View.GONE);

        sprCountryProfile.setVisibility(View.VISIBLE);
        sprStateProfile.setVisibility(View.VISIBLE);
        sprCityProfile.setVisibility(View.VISIBLE);

        imgProfilePicture.setEnabled(true);
        btnEditProfile.setVisibility(View.INVISIBLE);
        btnSaveChanges.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
    }

    protected void viewMode(){
        if (txtNameProfile.getText().toString().isEmpty())
            txtNameProfile.setVisibility(View.GONE);
        else txtNameProfile.setEnabled(false);

        if (txtEmailProfile.getText().toString().isEmpty())
            txtEmailProfile.setVisibility(View.GONE);
        else txtEmailProfile.setEnabled(false);

        if (txtPhoneProfile.getText().toString().isEmpty())
            txtPhoneProfile.setVisibility(View.GONE);
        else txtPhoneProfile.setEnabled(false);

        if (txtPasswordProfile.getText().toString().isEmpty())
            txtPasswordProfile.setVisibility(View.GONE);
        else txtPasswordProfile.setEnabled(false);

        if (txtBirthDateProfile.getText().toString().isEmpty())
            txtBirthDateProfile.setVisibility(View.GONE);
        else txtBirthDateProfile.setEnabled(false);

        if (txtLocationProfile.getText().toString().isEmpty())
            txtLocationProfile.setVisibility(View.GONE);
        else txtLocationProfile.setEnabled(false);

        txtLastNameProfile.setVisibility(View.GONE);
        sprCountryProfile.setVisibility(View.GONE);
        sprStateProfile.setVisibility(View.GONE);
        sprCityProfile.setVisibility(View.GONE);

        imgProfilePicture.setEnabled(false);
        btnEditProfile.setVisibility(View.VISIBLE);
        btnSaveChanges.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }
}
