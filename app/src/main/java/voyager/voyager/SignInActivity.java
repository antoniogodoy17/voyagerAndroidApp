package voyager.voyager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SignInActivity extends AppCompatActivity {


    Button btnLogIn;
    Button btnSignIn;
    EditText txtname,txtemail,txtpassword;
    TextView txtbirth_date;
    Spinner spnnationality,spnstate,spncity;
    DatePickerDialog datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        btnLogIn = findViewById(R.id.btnLogIn_SignIn);
        btnSignIn = findViewById(R.id.btnSignIn_Signin);
        txtname = findViewById(R.id.txtNameSignIn);
        txtemail = findViewById(R.id.txtEmailSignIn);
        txtpassword = findViewById(R.id.txtPasswordSignIn);
        txtbirth_date = findViewById(R.id.txtBirthDateSignIn);
        spnnationality = findViewById(R.id.sprNationalitySignIn);
        spnstate = findViewById(R.id.sprEstateSignIn);
        spncity = findViewById(R.id.sprCitySignIn);
        btnSignIn = findViewById(R.id.btnSignIn_Signin);

        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(getApplicationContext(),LogInActivity.class);
                startActivity(login);
                finish();
            }
        });

        txtbirth_date.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar c = Calendar.getInstance();
                int selYear = c.get(Calendar.YEAR); // current year
                int selMonth = c.get(Calendar.MONTH); // current month
                int selDay = c.get(Calendar.DAY_OF_MONTH); // current day

                // date picker dialog
                datePicker = new DatePickerDialog(SignInActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtbirth_date.setText(dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year);
                    }
                }, selYear, selMonth, selDay);
                c.add(Calendar.YEAR,-10);
                datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
                c.add(Calendar.YEAR, -100);
                datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
                datePicker.show();
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_user();
            }
        });
    }

    protected void register_user(){
        //Create Object user and register it to firebase, and create a user for authentication.

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent home = new Intent(getApplicationContext(),homeActivity.class);
                startActivity(home);
                finish();
            }
        });

    }
}
