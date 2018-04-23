package voyager.voyager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {
    Button btnSignIn, btnLogin;
    EditText txtEmail, txtPassword;
    String email, password;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        //Database reference
        firebaseAuth = FirebaseAuth.getInstance();

        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        getSupportActionBar().hide();

        //Elements initialization
        btnLogin = findViewById(R.id.btnLogIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        txtEmail = findViewById(R.id.txtEmailLogin);
        txtPassword = findViewById(R.id.txtPasswordLogIn);

        //Elements Listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_user_values();
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Animation to wait until authorization is completed
                        if(task.isSuccessful()){
                            Intent home = new Intent(getApplicationContext(),homeActivity.class);
                            startActivity(home);
                            finish();
                        }else{
                            Toast.makeText(LogInActivity.this,"El usuario o la contraseña son incorrectos", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = new Intent(getApplicationContext(),SignInActivity.class);
                startActivity(signin);
    //          overridePendingTransition(R.anim.alpha_transition,R.anim.alpha_transition);
                finish();
            }
        });
    }

    public void set_user_values(){
        email = txtEmail.getText().toString().trim();
        password = txtPassword.getText().toString().trim();
    }
}
