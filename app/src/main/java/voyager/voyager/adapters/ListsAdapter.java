package voyager.voyager.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import voyager.voyager.R;
import voyager.voyager.ui.ListActivity;

public class ListsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    private Context context;
    private int resource;
    private ArrayList<String> lists;

    public ListsAdapter(Context context, int resource, ArrayList<String> lists){
        this.context = context;
        this.resource = resource;
        this.lists = lists;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
                Toast.makeText(context, list, Toast.LENGTH_SHORT).show();
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
