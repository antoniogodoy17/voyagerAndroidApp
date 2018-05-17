package voyager.voyager.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import voyager.voyager.R;

public class ListCreationDialog extends DialogFragment {
    EditText txtListName;
    Button btnAccept, btnCancel;
    ImageButton btnDelete;
    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        void onListCreated(String list);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.list_creation_layout, null);
        builder.setTitle(R.string.Select_list)
                .setView(view);

        mListener = (NoticeDialogListener) getActivity();
        txtListName = view.findViewById(R.id.txtListNameCreated);
        btnAccept = view.findViewById(R.id.btnAcceptListCreation);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listName = txtListName.getText().toString();
                if(!listName.isEmpty() && !listName.equals(getString(R.string.Add_list)) && !listName.equals("favorites") && !listName.matches("\\d+")){
                    mListener.onListCreated(listName);
                    ListCreationDialog.this.getDialog().cancel();
                }
                else{
                    Toast.makeText(getContext(), getResources().getString(R.string.Pleas_fill_listname), Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel = view.findViewById(R.id.btnCancelListCreation);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListCreationDialog.this.getDialog().cancel();
            }
        });
        btnDelete = view.findViewById(R.id.btnDeleteListName);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtListName.setText("");
            }
        });

        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }
}