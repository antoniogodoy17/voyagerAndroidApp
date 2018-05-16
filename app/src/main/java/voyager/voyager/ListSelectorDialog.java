package voyager.voyager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ListSelectorDialog extends DialogFragment {
    ArrayList<String> lists = new ArrayList<>();
    CharSequence listNames[];

    public interface NoticeDialogListener {
        public void onListSelected(DialogFragment dialog, String list, int position);
    }

    NoticeDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .child("lists").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(!ds.getKey().equals("favorites")){
                        lists.add(ds.getKey());
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

        mListener = (NoticeDialogListener) getActivity();

        lists.add("Lista de prueba");
        lists.add(getResources().getString(R.string.Add_list));
        listNames = lists.toArray(new CharSequence[lists.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Select_list)
                .setItems(listNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == listNames.length-1){
                            // Create a new List here
                            // Add activity to that list
                        }
                        else{
                            // Add activity to the selected list
                            mListener.onListSelected(ListSelectorDialog.this, listNames[which].toString(), which);
                        }
                    }
                });
        return builder.create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }
}
