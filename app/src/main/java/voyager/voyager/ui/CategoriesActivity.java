package voyager.voyager.ui;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import java.util.ArrayList;

import voyager.voyager.models.Category;
import voyager.voyager.R;
import voyager.voyager.adapters.SliderAdapter;

public class CategoriesActivity extends AppCompatActivity {
    // Database Initialization
    private FirebaseDatabase database;
    private DatabaseReference categoryRef;
    private FirebaseAuth.AuthStateListener authListener;
    //
    // UI Initialization
    private ViewPager slidePager;
    private LinearLayout dotsLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] dots;
    private Button closeButton;
    //
    //
    ArrayList<Category> categories;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.TOP)
                .build();
        Slidr.attach(this, config);

        closeButton = findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        categories = new ArrayList<>();

        // Database Initialization
        database = FirebaseDatabase.getInstance();
        categoryRef = database.getReference("Category");
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fillCategories(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        // End Database Initialization
    }
    public void addDotsIndicator(int position){
        dots = new TextView[categories.size()];
        dotsLayout.removeAllViews();

        for(int i=0; i < dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorSecondary));
            dotsLayout.addView(dots[i]);
        }
        if(dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.colorSecondaryDark));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener(){
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }
        @Override
        public void onPageScrollStateChanged(int state) {}
    };

    protected void fillCategories(DataSnapshot data){
        for(DataSnapshot ds:data.getChildren()){
            categories.add(ds.getValue(Category.class));
        }
        fillSlidePager();
    }

    void fillSlidePager(){
        slidePager = findViewById(R.id.slidePager);
        dotsLayout = findViewById(R.id.dotsLayout);
        sliderAdapter = new SliderAdapter(CategoriesActivity.this,categories);
        slidePager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        slidePager.addOnPageChangeListener(viewListener);
    }
}
