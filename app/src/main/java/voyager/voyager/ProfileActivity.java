package voyager.voyager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.lang.reflect.Array;

public class ProfileActivity extends AppCompatActivity {
    String[] data =
            {"Name", "Email", null, "password", "12/11/96", "Tijuana"};
//    String[] data =
//            {"Name", "Email", "6643133393", "password", "12/11/96", "Tijuana"};

    EditText txtNameProfile, txtEmailProfile, txtPhoneProfile, txtPasswordProfile, txtBirthDateProfile, txtLocationProfile;
    Button btnSaveChanges, btnCancel;
    ImageButton btnProfilePic, btnEditProfile;
    Spinner sprCountryProfile, sprStateProfile, sprCityProfile;

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
        txtLocationProfile = findViewById(R.id.txtLocationProfile);

        sprCountryProfile = findViewById(R.id.sprCountryProfile);
        sprStateProfile = findViewById(R.id.sprStateProfile);
        sprCityProfile = findViewById(R.id.sprCityProfile);

        fillData();

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
                fillData();
            }
        });
    }

    protected void fillData(){
        int i = 0;
        EditText[] campos =
                {
                        txtNameProfile, txtEmailProfile, txtPhoneProfile, txtPasswordProfile, txtBirthDateProfile, txtLocationProfile
                };

        for(String value: data){
            if (value == null){
                campos[i].setVisibility(View.GONE);
            }
            campos[i].setText(value);
            i++;
        }
    }

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
        txtNameProfile.setEnabled(false);

        txtEmailProfile.setEnabled(false);

        txtPhoneProfile.setEnabled(false);

        txtPasswordProfile.setEnabled(false);

        txtBirthDateProfile.setEnabled(false);

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
