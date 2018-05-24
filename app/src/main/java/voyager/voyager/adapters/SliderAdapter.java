package voyager.voyager.adapters;


import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import voyager.voyager.R;
import voyager.voyager.models.Activity;
import voyager.voyager.models.Category;
import voyager.voyager.ui.HomeActivity;

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Category> categories;

    public SliderAdapter(Context context, ArrayList<Category> categories){
        this.context = context;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slider_layout,container,false);
        TextView categoryTitle = view.findViewById(R.id.categoryTitle);
        ImageView categoryImage = view.findViewById(R.id.categoryImage);
        TextView categoryDescription = view.findViewById(R.id.categoryDescription);

        categoryTitle.setText(categories.get(position).getTitle());
        Picasso.get().load(categories.get(position).getImage()).into(categoryImage);
        categoryDescription.setText(categories.get(position).getDescription());

        container.addView(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, HomeActivity.class);
                i.putExtra("Category", categories.get(position).getTitle());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(i);
            }
        });
        return view;
    }
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout)object);
    }
}
