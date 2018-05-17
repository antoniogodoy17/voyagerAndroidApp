package voyager.voyager.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import voyager.voyager.R;

public class DeleteItemDialog extends DialogFragment{
    Button btnAccept, btnCancel;
    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        void onDeleteItem(String listName);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        final String item = bundle.getString("item");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_layout, null);
        builder.setTitle(R.string.Delete)
                .setView(view);

        mListener = (NoticeDialogListener) getActivity();

        btnAccept = view.findViewById(R.id.btnDeleteItem);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeleteItem(item);
                DeleteItemDialog.this.getDialog().cancel();
            }
        });
        btnCancel = view.findViewById(R.id.btnCancelDeleteItem);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteItemDialog.this.getDialog().cancel();
            }
        });

        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }
}
