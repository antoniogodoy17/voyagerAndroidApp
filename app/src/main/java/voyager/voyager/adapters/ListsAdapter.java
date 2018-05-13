package voyager.voyager.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import voyager.voyager.R;

public class ListsAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;

    Context context;
    ArrayList<String> lists;

    public ListsAdapter(Context context, ArrayList<String> lists){
        this.context = context;
        this.lists = lists;

        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view = inflater.inflate(R.layout.list_layout, null);
        final String list;
        TextView listName = (TextView) view.findViewById(R.id.listName);
        ImageView listLogo = (ImageView) view.findViewById(R.id.listLogo);

        list = lists.get(position);
        listName.setText(list);
        if (list.equals("favorites")){
            listLogo.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else listLogo.setImageResource(R.drawable.ic_format_list_bulleted_black_24dp);
        return null;
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
