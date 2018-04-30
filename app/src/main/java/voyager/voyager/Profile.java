package voyager.voyager;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import java.io.IOException;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class Profile extends Fragment {

    private OnFragmentInteractionListener mListener;
    //Our code from profile goes here
    EditText txtNameProfile, txtLastNameProfile, txtEmailProfile, txtPhoneProfile, txtPasswordProfile, txtLocationProfile;
    TextView txtBirthDateProfile;
    Button btnSaveChanges, btnCancel;
    ImageButton btnProfilePic, btnEditProfile;
    Spinner sprCountryProfile, sprStateProfile, sprCityProfile;
    DatePickerDialog datePicker;
    String name, lastname, email, phone, birth_date, location, password;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    User user;
    homeVM vm;

    public Profile() {
        // Required empty public constructor
    }
    public static Profile newInstance(String param1, String param2) {
        Profile fragment = new Profile();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vm = homeActivity.getViewModel();
        user = vm.getUser();
    }

    //OUR CODE FROM PROFILE GOES HERE

    protected void fillFields(){
        txtNameProfile.setText(user.getName());
        txtLastNameProfile.setText(user.getLastname());
        txtBirthDateProfile.setText(user.getBirth_date());
        txtEmailProfile.setText(user.getEmail());

        viewMode();

        btnProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null ){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getView().getContext().getContentResolver(), filePath);
                btnProfilePic.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void saveChanges(){
        user.setName(txtNameProfile.getText().toString().trim());
        user.setBirth_date(txtBirthDateProfile.getText().toString().trim());
        vm.getUsersDatabase().child(user.getId()).setValue(user);
        vm.setUser(user);
    }

    protected void editMode(){
        txtNameProfile.setVisibility(View.VISIBLE);
        txtNameProfile.setEnabled(true);

        txtLastNameProfile.setVisibility(View.VISIBLE);
        txtLastNameProfile.setEnabled(true);

        txtEmailProfile.setVisibility(View.VISIBLE);
        txtEmailProfile.setEnabled(true);

        txtPhoneProfile.setVisibility(View.VISIBLE);
        txtPhoneProfile.setEnabled(true);

        txtPasswordProfile.setVisibility(View.VISIBLE);
        txtPasswordProfile.setEnabled(true);

        txtBirthDateProfile.setVisibility(View.VISIBLE);
        txtBirthDateProfile.setEnabled(true);

        txtLocationProfile.setVisibility(View.GONE);

        sprCountryProfile.setVisibility(View.VISIBLE);
        sprStateProfile.setVisibility(View.VISIBLE);
        sprCityProfile.setVisibility(View.VISIBLE);

        btnProfilePic.setEnabled(true);
        btnEditProfile.setVisibility(View.INVISIBLE);
        btnSaveChanges.setVisibility(View.VISIBLE);
        btnCancel.setVisibility(View.VISIBLE);
    }

    protected void viewMode(){
        if (txtNameProfile.getText().toString().isEmpty())
            txtNameProfile.setVisibility(View.GONE);
        else txtNameProfile.setEnabled(false);

        if (txtEmailProfile.getText().toString().isEmpty())
            txtEmailProfile.setVisibility(View.GONE);
        else txtEmailProfile.setEnabled(false);

        if (txtPhoneProfile.getText().toString().isEmpty())
            txtPhoneProfile.setVisibility(View.GONE);
        else txtPhoneProfile.setEnabled(false);

        if (txtPasswordProfile.getText().toString().isEmpty())
            txtPasswordProfile.setVisibility(View.GONE);
        else txtPasswordProfile.setEnabled(false);

        if (txtBirthDateProfile.getText().toString().isEmpty())
            txtBirthDateProfile.setVisibility(View.GONE);
        else txtBirthDateProfile.setEnabled(false);

        if (txtLocationProfile.getText().toString().isEmpty())
            txtLocationProfile.setVisibility(View.GONE);
        else txtLocationProfile.setEnabled(false);

        txtLastNameProfile.setVisibility(View.GONE);
        sprCountryProfile.setVisibility(View.GONE);
        sprStateProfile.setVisibility(View.GONE);
        sprCityProfile.setVisibility(View.GONE);

        btnProfilePic.setEnabled(false);
        btnEditProfile.setVisibility(View.VISIBLE);
        btnSaveChanges.setVisibility(View.GONE);
        btnCancel.setVisibility(View.GONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnProfilePic = view.findViewById(R.id.btnProfilePic);
        btnCancel = view.findViewById(R.id.btnCancel);
        txtNameProfile = view.findViewById(R.id.txtNameProfile);
        txtLastNameProfile = view.findViewById(R.id.txtLastNameProfile);
        txtEmailProfile = view.findViewById(R.id.txtEmailProfile);
        txtPhoneProfile = view.findViewById(R.id.txtPhoneProfile);
        txtPasswordProfile = view.findViewById(R.id.txtPasswordProfile);
        txtBirthDateProfile = view.findViewById(R.id.txtBirthDateProfile);
        txtLocationProfile = view.findViewById(R.id.txtLocationProfile);
        sprCountryProfile = view.findViewById(R.id.sprCountryProfile);
        sprStateProfile = view.findViewById(R.id.sprStateProfile);
        sprCityProfile = view.findViewById(R.id.sprCityProfile);

        fillFields();
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMode();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewMode();
            }
        });

        txtBirthDateProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final Calendar c = Calendar.getInstance();
                int selYear = c.get(Calendar.YEAR); // current year
                int selMonth = c.get(Calendar.MONTH); // current month
                int selDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePicker = new DatePickerDialog(getView().getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        txtBirthDateProfile.setText(dayOfMonth + "/"+ (monthOfYear + 1) + "/" + year);
                    }
                }, selYear, selMonth, selDay);
                c.add(Calendar.YEAR,-10);
                datePicker.getDatePicker().setMaxDate(c.getTimeInMillis());
                c.add(Calendar.YEAR, -100);
                datePicker.getDatePicker().setMinDate(c.getTimeInMillis());
                datePicker.show();
            }
        });

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
                Toast.makeText(getView().getContext(),"Changes have been made." , Toast.LENGTH_LONG).show();
                viewMode();
            }
        });
        return view;
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
        void onFragmentInteraction(Uri uri);
    }
}
