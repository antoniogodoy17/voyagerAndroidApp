package voyager.voyager.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

import voyager.voyager.R;
import voyager.voyager.models.Activity;
import voyager.voyager.models.Card;
import voyager.voyager.ui.ActivityActivity;

public class CardListAdapter extends ArrayAdapter<Card> {
    private Context context;
    private int resource;
    private ArrayList<Card> cards;

    private static class ViewHolder {
        TextView title;
        ImageView image;
        ImageButton favIcon;
    }

    public CardListAdapter(Context context, int resource, ArrayList<Card> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.cards = objects;
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
            //ViewHolder object
            ViewHolder holder;

            if(convertView == null){
                LayoutInflater inflater = LayoutInflater.from(context);
                convertView = inflater.inflate(resource, parent, false);
                holder = new ViewHolder();
                holder.title = convertView.findViewById(R.id.CardTitle);
                holder.image = convertView.findViewById(R.id.CardImage);
                holder.favIcon = convertView.findViewById(R.id.FavButton);
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.title.setText(title);

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
            holder.favIcon.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    addFavorite(card.getActivity());
                }
            });

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

    private void addFavorite(Activity activity){
        Toast.makeText(context, activity.get_id(), Toast.LENGTH_SHORT).show();
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
