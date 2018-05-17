package voyager.voyager.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;

import voyager.voyager.R;

public class ListSelectorDialog extends DialogFragment {
    private ArrayList<String> lists;
    NoticeDialogListener mListener;

    public interface NoticeDialogListener {
        void onListSelected(String list);
        void onCreateListSelected(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        lists = new ArrayList<>();
        Bundle bundle = getArguments();
        lists = bundle.getStringArrayList("lists");

        mListener = (NoticeDialogListener) getActivity();
        if(!lists.contains(getResources().getString(R.string.Add_list))){
            lists.add(getResources().getString(R.string.Add_list));
        }

        final CharSequence listNames[] = lists.toArray(new CharSequence[lists.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.Select_list)
                .setItems(listNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(listNames[which].toString().equals(getString(R.string.Add_list))){
                            ListCreationDialog listCreationDialog = new ListCreationDialog();
                            mListener.onCreateListSelected(listCreationDialog);
                        }
                        else{
                            mListener.onListSelected(listNames[which].toString());
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
