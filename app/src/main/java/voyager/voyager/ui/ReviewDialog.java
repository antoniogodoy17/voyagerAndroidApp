package voyager.voyager.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import voyager.voyager.R;

public class ReviewDialog extends DialogFragment {
    TextView txtRatingSelected, txtReviewCount;
    EditText txtReview;
    Button btnAccept, btnCancel;
    NoticeDialogListener mListener;
    String title;
    double rating;

    public interface NoticeDialogListener {
        void onReviewCreated(String review);
        void onReviewCanceled();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        title = bundle.getString("title");
        rating = bundle.getDouble("rating");

        mListener = (ReviewDialog.NoticeDialogListener) getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.review_dialog_layout, null);
        builder.setTitle(title)
                .setView(view);

        txtRatingSelected = view.findViewById(R.id.txtRatingSelected);
        txtRatingSelected.setText(String.valueOf(rating));
        txtReviewCount = view.findViewById(R.id.txtReviewCount);
        txtReviewCount.setText("0/100");
        txtReview = view.findViewById(R.id.txtReview);
        txtReview.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                txtReviewCount.setText(txtReview.getText().length() + "/100");
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        btnAccept = view.findViewById(R.id.btnAcceptReview);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = txtReview.getText().toString();
                if(!review.isEmpty()){
                    mListener.onReviewCreated(review);
                    ReviewDialog.this.getDialog().cancel();
                }
                else{
                    Toast.makeText(getContext(), R.string.Please_Fill_Review, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel = view.findViewById(R.id.btnCancelReview);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onReviewCanceled();
                ReviewDialog.this.getDialog().dismiss();
            }
        });
        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }
}