package voyager.voyager;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Card> cardsList;
    private ArrayList<Activity> activities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        listView = findViewById(R.id.listView);

        cardsList = new ArrayList<Card>();

        //Implement a loop here to dinamically create Cards with the ordered Activities

//        for(Activity activity:activities){
//            System.out.println("------>" + activity.title);
//        }
        //End loop
//        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 1"));
//        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 2"));
//        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 3"));
//        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 4"));
//        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 5"));

        CardListAdapter cardAdapter = new CardListAdapter(this, R.layout.card_layout, cardsList);
        listView.setAdapter(cardAdapter);
    }
}