package org.dashee.remote.preference.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Our seek bar preference type
 */
public class SeekBarPreference 
    extends DialogPreference 
    implements SeekBar.OnSeekBarChangeListener, OnClickListener
{
    private SeekBar seekBar;
    private TextView tvMessage;
    private TextView tvValue;
    private Context context;

    private String message;
    private String mSuffix;

    private int defaultValue = 0;
    private int max = 0;
    private int value = 0;
    
    /**
     * Create the preference
     */
    public SeekBarPreference(Context context, AttributeSet attrs) {

        super(context,attrs); 
        this.context = context;

        String androidns = "http://schemas.android.com/apk/res/android";

        // Get string value for dialogMessage :
        int messageId 
            = attrs.getAttributeResourceValue(androidns, "dialogMessage", 0);
        if(messageId == 0) 
            this.message 
                = attrs.getAttributeValue(androidns, "dialogMessage");
        else 
            this.message = this.context.getString(messageId);

        // Get string value for suffix (text attribute in xml file) :
        int mSuffixId = attrs.getAttributeResourceValue(androidns, "text", 0);
        if(mSuffixId == 0) 
            mSuffix = attrs.getAttributeValue(androidns, "text");
        else mSuffix 
            = this.context.getString(mSuffixId);

        // Get default and max seekbar values :
        defaultValue = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        max = attrs.getAttributeIntValue(androidns, "max", 100);
    }

    /**
     * Create the view dynamically.
     *
     * First create the encapsulating LinearLayout which will be the returned 
     * view from the function.
     *
     * Secondly, add the SpashText. A helpful summary of what this seek bar is 
     * about.
     *
     * TODO
     * Thirdly add another linearlayout which will encapsulate the seekbar and 
     * the textview representing the value of the seekbar.
     *
     * Finally add the seekbar and the text representing the seekbar value.
     *
     * @return The new view created dynamicly
     */
    @Override 
    protected View onCreateDialogView() 
    {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT
            );

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6,6,6,6);

        this.tvMessage = new TextView(context);
        this.tvMessage.setPadding(30, 10, 30, 10);
        if (this.message != null)
            this.tvMessage.setText(this.message);
        layout.addView(this.tvMessage);

        this.tvValue = new TextView(context);
        this.tvValue.setGravity(Gravity.CENTER_HORIZONTAL);
        this.tvValue.setTextSize(32);
        layout.addView(this.tvValue, params);

        this.seekBar = new SeekBar(context);
        this.seekBar.setOnSeekBarChangeListener(this);
        layout.addView(this.seekBar, params);

        if (shouldPersist())
            this.value = getPersistedInt(this.defaultValue);

        this.seekBar.setMax(max);
        this.seekBar.setProgress(value);

        return layout;
    }

    @Override 
    protected void onBindDialogView(View v) 
    {
        super.onBindDialogView(v);
        seekBar.setMax(max);
        seekBar.setProgress(value);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)
    {
        super.onSetInitialValue(restore, defaultValue);
        if (restore) 
            value = shouldPersist() ? getPersistedInt(this.defaultValue) : 0;
        else 
            value = (Integer)defaultValue;
    }
    
    /**
     * Update our textview which represents the value.
     *
     * @param seek The seekbar changed
     * @param value The value that the seekbar represents
     * @param fromTouch The boolean representing weather or not the value was 
     *  changed using touch
     */
    @Override
    public void onProgressChanged(SeekBar seek, int val, boolean fromTouch)
    {
        String t = String.valueOf(val);
        this.tvValue.setText(mSuffix == null ? t : t.concat(" " + mSuffix));
    }

    /**
     * Not used.
     */
    @Override
    public void onStartTrackingTouch(SeekBar seek) {}

    /**
     * Not used.
     */ 
    @Override
    public void onStopTrackingTouch(SeekBar seek) {}

    /**
     * Set the max value of the seek
     *
     * @param max The max value to set it to
     */
    public void setMax(int max) 
    { 
        this.max = max; 
    }

    /**
     * Get the max value set.
     *
     * @return The maximum value of current max
     */
    public int getMax() 
    { 
        return max; 
    }

    public void setProgress(int progress) 
    { 
        this.value = progress;
        if (seekBar != null)
            seekBar.setProgress(progress); 
    }
    public int getProgress() 
    { 
        return value; 
    }

    @Override
    public void showDialog(Bundle state) 
    {
        super.showDialog(state);

        Button positiveButton 
            = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) 
    {
        if (shouldPersist()) 
        {
            this.value = seekBar.getProgress();
            persistInt(seekBar.getProgress());
            callChangeListener(Integer.valueOf(seekBar.getProgress()));
        }

        ((AlertDialog) getDialog()).dismiss();
    }
}
