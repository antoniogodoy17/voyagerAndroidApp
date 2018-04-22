package voyager.voyager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SignInActivity extends AppCompatActivity {

    Button btnLogIn;
    Button btnSignIn;
    EditText txtname,txtemail,txtpassword,txtbirth_date;
    Spinner spnnationality,spnstate,spncity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        btnLogIn = findViewById(R.id.btnLogIn_SignIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(),LogInActivity.class);
                startActivity(login);
                finish();
            }
        });
        txtname = findViewById(R.id.txtNameSignIn);
        txtemail = findViewById(R.id.txtEmailSignIn);
        txtpassword = findViewById(R.id.txtPasswordSignIn);
        txtbirth_date = findViewById(R.id.txtBirthDateSignIn);
        spnnationality = findViewById(R.id.sprNationalitySignIn);
        spnstate = findViewById(R.id.sprEstateSignIn);
        spncity = findViewById(R.id.sprCitySignIn);
        btnSignIn = findViewById(R.id.btnSignIn_Signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_user();
            }
        });
    }

    protected void register_user(){
        //Create Object user and register it to firebase, and create a user for authentication.
    }
}
