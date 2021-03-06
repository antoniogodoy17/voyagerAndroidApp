package voyager.voyager.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import voyager.voyager.R;

public class FilterDialog extends DialogFragment {
    RadioButton rbtEntertainment,rbtSocial,rbtSports,rbtCultural,rbtAttraction,rbtRestaurant,rbtEvent;
    SeekBar costBar,ratingBar;
    Button btnApply, btnCancel;
    ImageButton btnClose;
    RadioGroup categoryGroup,typeGroup;
    TextView txtDate;
    DatePickerDialog datePicker;
    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        void onFiltersApplied(HashMap<String,String> filters);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mListener = (FilterDialog.NoticeDialogListener) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.filters_layout, null);
        builder.setView(view);

        categoryGroup = view.findViewById(R.id.categoryGroup);
        rbtEntertainment = view.findViewById(R.id.checkBoxEntertainment);
        rbtSocial = view.findViewById(R.id.checkBoxSocial);
        rbtSports = view.findViewById(R.id.checkBoxSports);
        rbtCultural = view.findViewById(R.id.checkBoxCultural);
        typeGroup = view.findViewById(R.id.typeGroup);
        rbtAttraction = view.findViewById(R.id.checkBoxAttraction);
        rbtRestaurant = view.findViewById(R.id.checkBoxRest);
        rbtEvent = view.findViewById(R.id.checkBoxEvent);
        costBar = view.findViewById(R.id.seekBarCost);
        txtDate = view.findViewById(R.id.txtDate);
        txtDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR); // current year
                int month = c.get(Calendar.MONTH); // current month
                int day = c.get(Calendar.DAY_OF_MONTH); // current day
                //Date picker dialog
                datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtDate.setText(dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year);
                    }
                }, year, month, day);
                //User must have at least 10 years to sign up
//                c.add(Calendar.YEAR,-10);
//                datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
                //User must not have more than 100 years to sign up
                c.add(Calendar.YEAR, -100);
                datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
                datePicker.show();
            }
        });
        ratingBar = view.findViewById(R.id.seekBarRating);
        btnApply = view.findViewById(R.id.btnApplyFilters);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> filters = new HashMap<>();

                // Category Filter
                if(rbtEntertainment.isChecked()){
                    filters.put("Categories","Entretenimiento");
                }
                else if(rbtSocial.isChecked()){
                    filters.put("Categories","Social");
                }
                else if(rbtSports.isChecked()){
                    filters.put("Categories","Deportivo");
                }
                else if(rbtCultural.isChecked()){
                    filters.put("Categories","Cultural");
                }
                else{
                    filters.put("Categories","");
                }

                // Cost Filter
                filters.put("Cost",String.valueOf(costBar.getProgress()+1));

                // Date Filter
                filters.put("Date",txtDate.getText().toString().replace('/','-'));

                // Rating Filter
                filters.put("Rating",String.valueOf(ratingBar.getProgress()+1));

                // Type Filter
                if(rbtAttraction.isChecked()){
                    filters.put("Type","Atraccion");
                }
                else if(rbtRestaurant.isChecked()){
                    filters.put("Type","Restaurante");
                }
                else if(rbtEvent.isChecked()){
                    filters.put("Type","Evento");
                }
                else{
                    filters.put("Type","");
                }

                mListener.onFiltersApplied(filters);
                FilterDialog.this.dismiss();
            }
        });
        btnCancel = view.findViewById(R.id.btnClearFilters);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        btnClose = view.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FilterDialog.this.dismiss();
            }
        });

        return builder.create();
    }

    private void clear(){
        categoryGroup.clearCheck();
        typeGroup.clearCheck();
        txtDate.setText("");
        costBar.setProgress(4);
        ratingBar.setProgress(0);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }
}