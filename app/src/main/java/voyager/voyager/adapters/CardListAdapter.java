package voyager.voyager.adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;

import voyager.voyager.R;
import voyager.voyager.models.Activity;
import voyager.voyager.models.Card;
import voyager.voyager.models.User;
import voyager.voyager.ui.ActivityActivity;
import voyager.voyager.ui.ListActivity;
import voyager.voyager.ui.ListsActivity;
import voyager.voyager.ui.dialogs.DeleteItemDialog;

public class CardListAdapter extends ArrayAdapter<Card> {
    private Context context;
    private int resource;
    private ArrayList<Card> cards;
    private User user;
    private DatabaseReference userRef, listRef;
    private ArrayList<HashMap<String,String>> favoriteList;
    private ArrayList<String> lists;

    private static class ViewHolder {
        TextView title;
        ImageView image;
        ImageButton favIcon;
        ImageButton bookmarkIcon;
    }

    public CardListAdapter(Context context, int resource, ArrayList<Card> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.cards = objects;

        // Get Firebase Information
        favoriteList = new ArrayList<>();
        lists = new ArrayList<>();
        userRef = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if(dataSnapshot.hasChild("lists")){
                    listRef = userRef.child("lists");
                    listRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds : dataSnapshot.getChildren()){
//                                if(!ds.getKey().equals("favorites")){
                                    lists.add(ds.getKey());
//                                }
                            }
                            if(dataSnapshot.hasChild("favorites")){
                                favoriteList = user.getLists().get("favorites");
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) { }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //sets up the image loader library
        setupImageLoader();
        //get the cards information
        final Card card = cards.get(position);
        String title = getItem(position).getTitle();
        String imgUrl = getItem(position).getImgUrl();
        if(imgUrl == null){
            imgUrl = "drawable://"+ R.drawable.logo512;
        }

        try{
            ViewHolder holder;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(resource, parent, false);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.CardTitle);
                holder.image = convertView.findViewById(R.id.CardImage);
                holder.favIcon = convertView.findViewById(R.id.FavButton);
                if(isFavorite(card.getActivity().get_id())){
                    holder.favIcon.setImageResource(R.drawable.ic_favorited_24dp);
                }
                holder.bookmarkIcon = convertView.findViewById(R.id.bookmarkIcon);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(title);

            //create the imageloader object
            ImageLoader imageLoader = ImageLoader.getInstance();
            int defaultImage = context.getResources().getIdentifier("@drawable/icon_transparent",null, context.getPackageName());
            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url
            imageLoader.displayImage(imgUrl, holder.image, options);
            holder.image.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    openCard(card.getActivity());
                }
            });
            holder.title.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    openCard(card.getActivity());
                }
            });

            if(context.getClass().equals(ListActivity.class)){
                holder.image.setLongClickable(true);
                holder.image.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("item",card.getActivity().get_id());

                        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                        DeleteItemDialog dialog = new DeleteItemDialog();
                        dialog.setArguments(bundle);
                        dialog.show(manager,"Delete Item");
                        return true;
                    }
                });
                holder.title.setLongClickable(true);
                holder.title.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id",card.getActivity().get_id());

                        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
                        DeleteItemDialog dialog = new DeleteItemDialog();
                        dialog.setArguments(bundle);
                        dialog.show(manager,"Delete Item");
                        return true;
                    }
                });
            }
//            holder.favIcon.setOnClickListener(new View.OnClickListener(){
//                @Override
//                public void onClick(View v) {
//                    toggleFavorite(card.getActivity());
//                }
//            });

            return convertView;
        }catch (IllegalArgumentException e){
//            Log.e(TAG, "getView: IllegalArgumentException: " + e.getMessage() );
            return convertView;
        }

    }

    private void openCard(Activity activity){
        Intent cardActivity = new Intent(context,ActivityActivity.class);
        cardActivity.putExtra("activity",activity);
        context.startActivity(cardActivity);
    }

    private void toggleFavorite(Activity activity){
        if(isFavorite(activity.get_id())){
            removeFavorite(activity);
        }
        else{
            addFavorite(activity);
        }
    }

    public boolean isFavorite(String actId){
        for(HashMap<String, String> hm:favoriteList){
            if(hm.get("id").equals(actId)){
                return true;
            }
        }
        return false;
    }

    public void addFavorite(Activity activity){
        HashMap<String,String> newFavorite = new HashMap<>();
        newFavorite.put("id", activity.get_id());
        favoriteList.add(newFavorite);
        userRef.child("lists").child("favorites").setValue(favoriteList);
    }

    public void removeFavorite(Activity activity){
        userRef.child("lists").child("favorites").child(activity.get_id()).removeValue();
    }

    private void setupImageLoader(){
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache()).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP
    }

}
