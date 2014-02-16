package org.dashee.remote.preference.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.dashee.remote.R;

/**
 * Class that holds The Min and Max Dialog as a picker.
 */
public class MinMax extends DialogFragment 
{
    /**
     * View holder used by all our functions. Set by initView
     */
    private View view;

    /**
     * The value of minimum.
     */
    private int min;

    /**
     * The minimum number picker.
     */
    private NumberPicker nmin;

    /**
     * The value of maximum.
     */
    private int max;

    /**
     * The maximum number picker.
     */
    private NumberPicker nmax;

    /**
     * Construct our stuff.
     */
    public MinMax()
    {
        super();
        this.min = 0;
        this.max = 100;
    }

    /**
     * Create and build our Dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
        // Initialise our inits
        initView();
        initNumberPickers();

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Update min and max values.")
            .setView(this.view)
            .setPositiveButton(
                "Update", 
                new DialogInterface.OnClickListener() 
                {
                    public void onClick(DialogInterface dialog, int id) 
                    {
                        handleUpdate();
                    }
                }
            )
            .setNegativeButton(
                "Cancel", 
                new DialogInterface.OnClickListener() 
                {
                    public void onClick(DialogInterface dialog, int id) 
                    {
                        // User cancelled the dialog
                    }
                }
            );

        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * Actions handled for when the user clicks the update on the alert.
     */
    private void handleUpdate()
    {
        Toast toast = Toast.makeText(
                getActivity(),
                "Min and Max updated!", 
                Toast.LENGTH_SHORT
        );

        if (nmax.getValue() <= nmin.getValue())
            toast.setText(
                "Maximum must be greater than Minimum!"
            );
        else if (nmin.getValue() >= nmax.getValue())
            toast.setText(
                "Minimum must be less than Maximum!"
            );
        else
        {
            min = nmin.getValue();
            max = nmax.getValue();
        }

        toast.show();
    }

    /**
     * Set our View object.
     */ 
    private void initView()
    {
        this.view = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_minmax, null);
    }

    /**
     * Initialize our Number pickers.
     */
    private void initNumberPickers()
    {
        this.nmin = (NumberPicker)this.view.findViewById(R.id.min);
        this.nmin.setMinValue(0);
        this.nmin.setMaxValue(99);
        this.nmin.setValue(this.min);
        
        this.nmax = (NumberPicker)this.view.findViewById(R.id.max);
        this.nmax.setWrapSelectorWheel(true);
        this.nmax.setMinValue(1);
        this.nmax.setMaxValue(100);
        this.nmax.setValue(this.max);
    }

    /** 
     * Handle the event when the NumberPickers are changed.
     *
     * TODO
     */
    private void initNumberPickersHandler()
    {
    }   

    /**
     * Set the min value.
     *
     * @param min The value to set min to
     */
    public void setMin(int min)
    {
        this.min = min;
    }

    /**
     * Set the max value.
     *
     * @param max The value to set max to
     */
    public void setMax(int max)
    {
        this.max = max;
    }
}
