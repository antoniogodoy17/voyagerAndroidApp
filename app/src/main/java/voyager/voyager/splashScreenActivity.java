package voyager.voyager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class splashScreenActivity extends AppCompatActivity {
    private LinearLayout l1;
    Animation alphaAnim;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        sp = getSharedPreferences("login",MODE_PRIVATE);

//        if (sp.getBoolean("logged", true)) {
//                Intent home = new Intent(getApplicationContext(), homeActivity.class);
//            startActivity(home);
//            finish();
//        } else {
            //Hiding status bar
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //Hiding action bar
            getSupportActionBar().hide();

            l1 = findViewById(R.id.l1);
            alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_transition);
            l1.setAnimation(alphaAnim);

            Thread timer = new Thread(){
                @Override
                public void run(){
                    try{
                        sleep(2500);
                        Intent login = new Intent(getApplicationContext(),LogInActivity.class);
                        startActivity(login);
                        finish();
                        super.run();
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            };
            timer.start();
        }
//    }
}
