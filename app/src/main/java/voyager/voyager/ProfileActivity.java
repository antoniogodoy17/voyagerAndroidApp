package voyager.voyager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity {
    String[] data =
            {"Name", "Email", null, "password", "12/11/96", "Tijuana"};
//    String[] data =
//            {"Name", "Email", "6643133393", "password", "12/11/96", "Tijuana"};

    EditText txtNameProfile, txtEmailProfile, txtPhoneProfile, txtPasswordProfile, txtBirthDateProfile, txtLocationProfile;
    TextView txtBirthDate;
    Button btnSaveChanges, btnCancel;
    ImageButton btnProfilePic, btnEditProfile;
    Spinner sprCountryProfile, sprStateProfile, sprCityProfile;
    DatePickerDialog datePicker;
    String name,lastname, email, phone,birth_date,location, password;
    DatabaseReference database;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        btnProfilePic = findViewById(R.id.btnProfilePic);
        btnCancel = findViewById(R.id.btnCancel);

        txtNameProfile = findViewById(R.id.txtNameProfile);
        txtEmailProfile = findViewById(R.id.txtEmailProfile);
        txtPhoneProfile = findViewById(R.id.txtPhoneProfile);
        txtPasswordProfile = findViewById(R.id.txtPasswordProfile);
        txtBirthDateProfile = findViewById(R.id.txtBirthDateProfile);
        txtBirthDate = findViewById(R.id.txtBirthDate);
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

        txtBirthDate.setOnClickListener(new View.OnClickListener(){
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
                        txtBirthDate.setText(dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year);
                    }
                }, selYear, selMonth, selDay);
                c.add(Calendar.YEAR,-10);
                datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
                c.add(Calendar.YEAR, -100);
                datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
                datePicker.show();
            }
        });
    }

    protected void fillFields(){

        database = FirebaseDatabase.getInstance().getReference("User");
        database.orderByChild("email").startAt("diegomendozaco97@gmail.com").endAt("diegomendozaco97@gmail.com"+"\uf8ff").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                name = user.name;
                email = user.email;
                lastname = user.lastname;
                birth_date = user.birth_date;
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
    }

    protected void setValues() {
          txtNameProfile.setText(name +" "+ lastname);
          txtBirthDate.setText(birth_date.toString());
          txtEmailProfile.setText(email);

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

        txtBirthDateProfile.setVisibility(View.GONE);

        txtBirthDate.setVisibility(View.VISIBLE);

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
        txtNameProfile.setEnabled(false);

        txtEmailProfile.setEnabled(false);

        txtPhoneProfile.setEnabled(false);

        txtPasswordProfile.setEnabled(false);

        txtBirthDateProfile.setVisibility(View.VISIBLE);
        txtBirthDateProfile.setEnabled(false);

        txtBirthDate.setVisibility(View.GONE);

        txtLocationProfile.setVisibility(View.VISIBLE);

        sprCountryProfile.setVisibility(View.GONE);
        sprStateProfile.setVisibility(View.GONE);
        sprCityProfile.setVisibility(View.GONE);

        btnProfilePic.setEnabled(false);
        btnEditProfile.setVisibility(View.VISIBLE);
        btnSaveChanges.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }
}
