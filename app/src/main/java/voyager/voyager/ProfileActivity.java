package voyager.voyager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {

    EditText txtNameProfile, txtEmailProfile, txtPhoneProfile, txtPasswordProfile, txtLocationProfile;
    TextView txtBirthDateProfile;
    Button btnSaveChanges, btnCancel;
    ImageButton btnProfilePic, btnEditProfile;
    Spinner sprCountryProfile, sprStateProfile, sprCityProfile;
    DatePickerDialog datePicker;
    String name, lastname, email, phone, birth_date, location, password;
    DatabaseReference database;
    FirebaseUser fbUser;
    FirebaseAuth firebaseAuth;
    User user;

    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth = FirebaseAuth.getInstance();
        fbUser = firebaseAuth.getCurrentUser();


        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnProfilePic = findViewById(R.id.btnProfilePic);
        btnCancel = findViewById(R.id.btnCancel);

        txtNameProfile = findViewById(R.id.txtNameProfile);
        txtEmailProfile = findViewById(R.id.txtEmailProfile);
        txtPhoneProfile = findViewById(R.id.txtPhoneProfile);
        txtPasswordProfile = findViewById(R.id.txtPasswordProfile);
        txtBirthDateProfile = findViewById(R.id.txtBirthDateProfile);
        txtLocationProfile = findViewById(R.id.txtLocationProfile);

        sprCountryProfile = findViewById(R.id.sprCountryProfile);
        sprStateProfile = findViewById(R.id.sprStateProfile);
        sprCityProfile = findViewById(R.id.sprCityProfile);



//        fillData();

        fillFields();


        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMode();
//                fillData();
            }
        });

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

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
                Toast.makeText(ProfileActivity.this,"Changes Made " , Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void fillFields(){

        email = fbUser.getEmail();
        database.orderByChild("email").startAt(email).endAt(email+"\uf8ff").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                user = dataSnapshot.getValue(User.class);

                setValues();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    protected void setValues() {
//          txtNameProfile.setText(user.getName() +" "+ user.getLastname());
          txtBirthDateProfile.setText(user.getBirth_date());
          txtEmailProfile.setText(email);
          viewMode();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                btnProfilePic.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void saveChanges(){


        String userId = user.id;
        Object[] campos =
                {
                        txtNameProfile, txtEmailProfile, txtPhoneProfile, txtPasswordProfile, txtBirthDateProfile, txtLocationProfile
                };

        User userChanged = new User();

//
    // get Data from Fields and Change it on Object User

        database.child(userId).setValue(user);
    }



//    protected void fillData(){
//        int i = 0;
//        EditText[] campos =
//                {
//                        txtNameProfile, txtEmailProfile, txtPhoneProfile, txtPasswordProfile, txtBirthDateProfile, txtLocationProfile
//                };
//
//        for(String value: data){
//            if (value == null){
//                campos[i].setVisibility(View.GONE);
//            }
//            campos[i].setText(value);
//            i++;
//        }
//        txtBirthDate.setText(data[4]);
//    }

    protected void editMode(){
        txtNameProfile.setVisibility(View.VISIBLE);
        txtNameProfile.setEnabled(true);

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

        btnProfilePic.setEnabled(true);
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

        sprCountryProfile.setVisibility(View.GONE);
        sprStateProfile.setVisibility(View.GONE);
        sprCityProfile.setVisibility(View.GONE);

        btnProfilePic.setEnabled(false);
        btnEditProfile.setVisibility(View.VISIBLE);
        btnSaveChanges.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }
}
