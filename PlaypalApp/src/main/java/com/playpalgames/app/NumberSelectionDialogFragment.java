package com.playpalgames.app;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by javi on 03/07/2014.
 */
public class NumberSelectionDialogFragment extends DialogFragment {


    public interface NumberDialogListener{
        public void onNumberPicked(int id);
    }

    NumberDialogListener activityListener;



    public NumberSelectionDialogFragment(){
        super();
       }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            activityListener = (NumberDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NumberDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Bundle bundle=getArguments();
        String[] names= bundle.getStringArray("names");
        builder.setTitle(R.string.pick_number)
                .setItems(names, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        activityListener.onNumberPicked(which);

                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

}
