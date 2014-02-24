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
    private TextView mSplashText;
    private TextView mValueText;
    private Context context;

    private String mDialogMessage;
    private String mSuffix;
    private int mDefault = 0;
    private int mMax = 0;
    private int mValue = 0;
    
    /**
     * Create the preference
     */
    public SeekBarPreference(Context context, AttributeSet attrs) {

        super(context,attrs); 
        this.context = context;

        String androidns = "http://schemas.android.com/apk/res/android";

        // Get string value for dialogMessage :
        int mDialogMessageId 
            = attrs.getAttributeResourceValue(androidns, "dialogMessage", 0);
        if(mDialogMessageId == 0) 
            mDialogMessage 
                = attrs.getAttributeValue(androidns, "dialogMessage");
        else 
            mDialogMessage = this.context.getString(mDialogMessageId);

        // Get string value for suffix (text attribute in xml file) :
        int mSuffixId = attrs.getAttributeResourceValue(androidns, "text", 0);
        if(mSuffixId == 0) 
            mSuffix = attrs.getAttributeValue(androidns, "text");
        else mSuffix 
            = this.context.getString(mSuffixId);

        // Get default and max seekbar values :
        mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        mMax = attrs.getAttributeIntValue(androidns, "max", 100);
    }

    /**
     * Create the view dynamically.
     */
    @Override 
    protected View onCreateDialogView() 
    {

        LinearLayout.LayoutParams params;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6,6,6,6);

        mSplashText = new TextView(context);
        mSplashText.setPadding(30, 10, 30, 10);
        if (mDialogMessage != null)
            mSplashText.setText(mDialogMessage);
        layout.addView(mSplashText);

        mValueText = new TextView(context);
        mValueText.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueText.setTextSize(32);
        params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.addView(mValueText, params);

        seekBar = new SeekBar(context);
        seekBar.setOnSeekBarChangeListener(this);
        layout.addView(
                seekBar, 
                new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, 
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            );

        if (shouldPersist())
            mValue = getPersistedInt(mDefault);

        seekBar.setMax(mMax);
        seekBar.setProgress(mValue);

        return layout;
    }

    @Override 
    protected void onBindDialogView(View v) 
    {
        super.onBindDialogView(v);
        seekBar.setMax(mMax);
        seekBar.setProgress(mValue);
    }

    @Override
    protected void onSetInitialValue(boolean restore, Object defaultValue)  
    {
        super.onSetInitialValue(restore, defaultValue);
        if (restore) 
            mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
        else 
            mValue = (Integer)defaultValue;
    }
    
    /**
     *
     */
    @Override
    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch)
    {
        String t = String.valueOf(value);
        mValueText.setText(mSuffix == null ? t : t.concat(" " + mSuffix));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seek) {}
    @Override
    public void onStopTrackingTouch(SeekBar seek) {}

    public void setMax(int max) 
    { 
        mMax = max; 
    }
    public int getMax() 
    { 
        return mMax; 
    }

    public void setProgress(int progress) 
    { 
        mValue = progress;
        if (seekBar != null)
            seekBar.setProgress(progress); 
    }
    public int getProgress() 
    { 
        return mValue; 
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
            mValue = seekBar.getProgress();
            persistInt(seekBar.getProgress());
            callChangeListener(Integer.valueOf(seekBar.getProgress()));
        }

        ((AlertDialog) getDialog()).dismiss();
    }
}
