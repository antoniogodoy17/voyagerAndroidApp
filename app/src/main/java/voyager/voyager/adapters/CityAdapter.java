package voyager.voyager.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import voyager.voyager.models.City;
import voyager.voyager.models.User;
import voyager.voyager.ui.ActivityActivity;
import voyager.voyager.ui.ListActivity;
import voyager.voyager.ui.dialogs.DeleteItemDialog;

public class CityAdapter extends ArrayAdapter<City> {
    private Context context;
    private int resource;
    private ArrayList<City> cities;


    private static class ViewHolder {
        TextView name;
        ImageView image;
    }

    public CityAdapter(Context context, int resource, ArrayList<City> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.cities = objects;

        // Get Firebase Information
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //sets up the image loader library
        setupImageLoader();


        //get the cards information
        final City city = cities.get(position);
        String title = getItem(position).getName();
        String imgUrl = getItem(position).getImagePath();
        if(imgUrl == null){
            imgUrl = "drawable://"+ R.drawable.logo512;
        }

        try{
            ViewHolder holder;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(resource, parent, false);
                holder = new ViewHolder();
                holder.name = convertView.findViewById(R.id.CardTitle);
                holder.image = convertView.findViewById(R.id.CardImage);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.name.setText(title);
            //create the imageloader object
            ImageLoader imageLoader = ImageLoader.getInstance();

            int defaultImage = context.getResources().getIdentifier("@drawable/image_failed",null, context.getPackageName());

            //create display options
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .resetViewBeforeLoading(true)
                    .showImageForEmptyUri(defaultImage)
                    .showImageOnFail(defaultImage)
                    .showImageOnLoading(defaultImage).build();

            //download and display image from url
            imageLoader.displayImage(imgUrl, holder.image, options);
            return convertView;
        }catch (IllegalArgumentException e){
            return convertView;
        }

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
