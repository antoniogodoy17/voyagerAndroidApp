package voyager.voyager;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {
    private OnFragmentInteractionListener mListener;
    private ListView listView;
    private ArrayList<Card> cardsList;
    private ArrayList<Activity> activities;


    private homeVM vm;

    public Home() {
        // Required empty public constructor
    }

    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = homeActivity.getViewModel();
        activities = vm.getActivitiesMap();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        //Drawer Menu > Setting up username
        setDrawerUserName();

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        //Other elements
        listView = view.findViewById(R.id.listView);

        cardsList = new ArrayList<Card>();

        //Implement a loop here to dinamically create Cards with the ordered Activities

        System.out.println("----------------->"+activities.size());
        for(Activity activity:activities){
            System.out.println("------>" + activity.title);
        }
        //End loop
        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 1"));
        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 2"));
        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 3"));
        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 4"));
        cardsList.add(new Card("drawable://"+R.drawable.logo512,"Actividad 5"));

        CardListAdapter cardAdapter = new CardListAdapter(view.getContext(), R.layout.card_layout, cardsList);
        listView.setAdapter(cardAdapter);

        return view;
    }

    public void setDrawerUserName(){
        NavigationView navigationView = getActivity().findViewById(R.id.navigationView);
        View headerView = navigationView.getHeaderView(0);
        TextView username = headerView.findViewById(R.id.drawerUsername);
        if(vm.getFbUser().getDisplayName().isEmpty())
            username.setText("Username");
        else
            username.setText(vm.getFbUser().getDisplayName());
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
