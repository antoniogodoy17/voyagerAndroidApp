package voyager.voyager.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import voyager.voyager.R;
import voyager.voyager.ui.ListActivity;
import voyager.voyager.ui.dialogs.DeleteItemDialog;

public class ListsAdapter extends BaseAdapter {
    private Context context;
    private int resource;
    private ArrayList<String> lists;
    private int selectedPos;

    public ListsAdapter(Context context, int resource, ArrayList<String> lists){
        this.context = context;
        this.resource = resource;
        this.lists = lists;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_layout, parent, false);
        final String list;

        final TextView listName = convertView.findViewById(R.id.listName);
        ImageView listLogo = convertView.findViewById(R.id.listLogo);

        list = lists.get(position);
        listName.setText(list);
        if (list.equals("favorites")){
            listLogo.setImageResource(R.drawable.ic_favorite_black_24dp);
        }
        else {
            listLogo.setImageResource(R.drawable.ic_format_list_bulleted_black_24dp);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openList(list);
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v){
                Bundle bundle = new Bundle();
                bundle.putString("item",list);

                FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                DeleteItemDialog dialog = new DeleteItemDialog();
                dialog.setArguments(bundle);
                dialog.show(manager,"Delete Item");
                return true;
            }

        });

        return convertView;
    }

    private void openList(String listName){
        Intent listActivity = new Intent(context, ListActivity.class);
        listActivity.putExtra("list",listName);
        context.startActivity(listActivity);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
