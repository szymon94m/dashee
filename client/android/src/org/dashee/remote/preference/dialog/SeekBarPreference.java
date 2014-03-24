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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Our seek bar preference type
 */
public class SeekBarPreference 
    extends DialogPreference 
    implements SeekBar.OnSeekBarChangeListener, OnClickListener
{
    /**
     * Store the context in the class as OnCreateView function
     * will use the context.
     */
    private Context context;

    /**
     * The message string read from attribute
     */
    private String attributeMessage;
    private int attributeDefault;
    private int attributeMax;

    /**
     * Main View items used in our dialog
     */ 
    private SeekBar seekBar;
    private TextView tvMessage;
    private TextView tvValue;
    
    /**
     * Create the preference.
     *
     * @param context The context to create view in
     * @param attrs The attributes from the XML file
     */
    public SeekBarPreference(Context context, AttributeSet attrs) 
    {
        super(context,attrs);
        this.context = context;
        this.initValuesFromAttributes(attrs);
    }

    /**
     * Initialize values from attributes.
     *
     * @param attrs The attribute object
     */
    private void initValuesFromAttributes(AttributeSet attrs)
    {
        String androidns = "http://schemas.android.com/apk/res/android";
        
        // Get string value for dialogMessage :
        int messageId 
            = attrs.getAttributeResourceValue(androidns, "dialogMessage", 0);
        if (messageId == 0) 
            this.attributeMessage 
                = attrs.getAttributeValue(androidns, "dialogMessage");
        else 
            this.attributeMessage = this.context.getString(messageId);
        
        this.attributeDefault 
            = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        this.attributeMax = attrs.getAttributeIntValue(androidns, "max", 100);
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
     * Finally add the seekbar and the text representing the seekbar value.
     *
     * @return The new view created dynamically
     */
    @Override 
    protected View onCreateDialogView() 
    {
        this.initTvMessage();
        this.initSeekBar();
        this.initTvValue();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT
            );

        LinearLayout layout = new LinearLayout(this.context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6,6,6,6);

        LinearLayout layoutBar = new LinearLayout(this.context);
        layoutBar.setOrientation(LinearLayout.VERTICAL);
        
        layoutBar.addView(this.seekBar, params);
        layoutBar.addView(this.tvValue, params);
        layout.addView(this.tvMessage);
        layout.addView(layoutBar, params);

        // If we are allowed to persist, set the seek bar to the last known 
        // value, other wise set return the persisted value as the current value
        if (shouldPersist())
            this.setProgress(getPersistedInt(this.seekBar.getProgress()));

        return layout;
    }
    
    /**
     * Initialize the message.
     */
    private void initTvMessage()
    {
        this.tvMessage = new TextView(this.context);
        this.tvMessage.setText(this.attributeMessage);
        this.tvMessage.setPadding(30, 10, 40, 10);
        this.tvMessage.setTextSize(12);
    }

    /**
     * Initialize the seek bar.
     */
    private void initSeekBar()
    {
        String androidns = "http://schemas.android.com/apk/res/android";

        // Get default and max seekbar values :
        this.seekBar = new SeekBar(this.context);
        this.seekBar.setProgress(this.attributeDefault);
        this.seekBar.setMax(this.attributeMax);
        this.seekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * Initialize the text view value.
     */
    private void initTvValue()
    {
        this.tvValue = new TextView(this.context);
        this.tvValue.setTextSize(25);
        this.tvValue.setGravity(Gravity.CENTER);
        this.tvValue.setPadding(0,10,10,10);
        this.setValue(this.seekBar.getProgress());
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
        this.setValue(val);
    }

    /**
     * Not used.
     */
    @Override
    public void onStartTrackingTouch(SeekBar seek) 
    {
    }

    /**
     * Not used.
     */ 
    @Override
    public void onStopTrackingTouch(SeekBar seek) 
    {
    }

    /**
     * Show the dialog and change the event listener to this.
     *
     * @param state
     */
    @Override
    public void showDialog(Bundle state) 
    {
        super.showDialog(state);

        Button positiveButton 
            = ((AlertDialog) this.getDialog())
                .getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(this);
    }

    /**
     * When clicked, make sure the value is persisted.
     */
    @Override
    public void onClick(View v) 
    {
        if (shouldPersist()) 
        {
            persistInt(this.seekBar.getProgress());
            callChangeListener(Integer.valueOf(this.seekBar.getProgress()));
        }

        ((AlertDialog) getDialog()).dismiss();
    }

    /**
     * Set the progress value of the seek bar from the outside world.
     *
     * @param progress the Value to set
     */
    public void setProgress(int progress)
    {
        this.seekBar.setProgress(progress);
    }

    /**
     * Set the value of the text box, which represents the current state of the
     * seekbar.
     *
     * @param value The value to set
     */
    private void setValue(int value)
    {
        String t = String.valueOf(value);
        this.tvValue.setText(t);
    }

    /**
     * Interface to allow the outside world to set the message
     *
     * @param message The string to set the message
     */
    public void setMessage(String message)
    {
        this.tvMessage.setText(message);
    }
}
