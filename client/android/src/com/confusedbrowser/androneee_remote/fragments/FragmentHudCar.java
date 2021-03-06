package com.confusedbrowser.androneee_remote.fragments;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.View;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.confusedbrowser.androneee_remote.DrawHud;
import com.confusedbrowser.androneee_remote.R;
import com.confusedbrowser.androneee_remote.RangeMapping;
import com.confusedbrowser.androneee_remote.models.*;
import android.text.Html;

import android.widget.TextView;

/**
 * This is our HUD Fragment. which controls, sending and receiving controls
 * from our servers, and updating our HUD
 * 
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class FragmentHudCar extends FragmentHud
{
    /**
     * This is the variable where our HUD is drawn
     * We use DrawHud
     */
    LinearLayout layout_hud;
    
    /**
     * Draw our Hud object
     */
    DrawHud draw_hud;

    View view;
    
    /**
     * Assign area for current steer value
     */
    float steer;

    /**
     * Dont update steer UI if it's same as last time.
     */
    int prevSteer = -1;
            
    /**
     * Handle to our TextViews
     */    
    private TextView textViewHudIpValue;
    private TextView textViewHudConnectionValue;
    private TextView textViewHudBpsValue; //Bytes Per Second
    private TextView textViewHudPitchValue; //Pitch Value
    private TextView textViewHudRollValue; //Pitch Value
    private TextView textViewHudRollMinValue; //Pitch Min Value
    private TextView textViewHudRollMaxValue; //Pitch Max Value
    private TextView textViewHudPowerMinValue;
    private TextView textViewHudPowerMaxValue;
    private TextView textViewDrive;
    private TextView textViewReverse;

    private ModelVehicleCar car; //Pitch Value

    // Touch control to pass reverse numbers?
    private boolean Reverse = false;

    RangeMapping visualPowerMapping;

    /**
     * Constructor. Required by Fragment type Objects,
     * and they have to be public
     */
    public FragmentHudCar()
    {
        this.visualPowerMapping = new RangeMapping(0.0f,255.0f,-50.0f,50.0f);
    }

    /**
     * Set the ModelVehicle
     *
     * @param modelVehicle - The vehicle object
     */
    public void setVehicle(ModelVehicle modelVehicle)
    {
    	this.car = (ModelVehicleCar) modelVehicle;
    }

    /**
     * Set the vehicle power
     *
     * @param power - The value to set it to
     */
    private void setVehiclePower(int power)
    {
    	this.car.setFromSlider(power);
    }
    
    /**
     * Set our HUD. Initiate the servers IP address so our thread can talk to it, 
     * start our thread and return the view which is required by this function
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
        view = inflater.inflate(R.layout.fragment_hud, container, false);

        assert view != null;
        layout_hud = (LinearLayout)view.findViewById(R.id.hud_canvas);
        draw_hud = new DrawHud (this.getActivity(), view);
        layout_hud.addView(draw_hud);
        
        /*LinearLayout slider_layout = (LinearLayout)view.findViewById(R.id.hud_power_slider);
        slider_layout.setRotation(-90.0f);*/
        
        // Use the height and width of the image and the position of the stick to
        // map to car power value
        final ImageView iv = (ImageView)view.findViewById(R.id.power_stick);
        iv.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				//Log.d("dashee", "Got click");
                if(Reverse){
                    float mapVal = RangeMapping.mapValue(event.getY(), 120, iv.getHeight()-72, 0, 128);
                    setVehiclePower((int) mapVal);
                }else{
                    float mapVal = RangeMapping.mapValue(event.getY(), 120, iv.getHeight()-72, 255, 128);
                    setVehiclePower((int) mapVal);
                }

				//moveGrip((int) Math.round(event.getX()),(int) Math.round(event.getY()));
				if (event.getAction() == MotionEvent.ACTION_UP) {
					//moveGrip(150,150);
					setVehiclePower(128);
				}
				return true;
			}
		});

        LinearLayout powerToggle = (LinearLayout)view.findViewById(R.id.power_direction_toggle);
        powerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reverse = !Reverse;
                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                if(Reverse)
                {
                    tg.startTone(ToneGenerator.TONE_PROP_ACK);
                    textViewDrive.setTextColor(Color.parseColor("#444444"));
                    textViewReverse.setTextColor(Color.parseColor("#D93600"));
                }
                else
                {
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                    textViewReverse.setTextColor(Color.parseColor("#444444"));
                    textViewDrive.setTextColor(Color.parseColor("#2FB900"));
                }
            }
        });

        Button optsButton = (Button)view.findViewById(R.id.dot_settings);
        optsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	
                Intent intent = new Intent(getActivity(), com.confusedbrowser.androneee_remote.preferences.PreferencesActivity.class);
                startActivity(intent);
            }
        });

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        Typeface visitorFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/visitor1.ttf");
        Typeface visitor2Font = Typeface.createFromAsset(getActivity().getAssets(),"fonts/visitor2.ttf");
        Typeface novamonoFont = Typeface.createFromAsset(getActivity().getAssets(),"fonts/novamono2.ttf");

        // Set all of our textViews        
        textViewHudIpValue = (TextView)view.findViewById(R.id.hud_text_ip_value);
        textViewHudIpValue.getPaint().setAntiAlias(false);

        textViewHudConnectionValue = (TextView)view.findViewById(R.id.hud_text_connection_value);
        textViewHudConnectionValue.getPaint().setAntiAlias(false);
        textViewHudBpsValue = (TextView)view.findViewById(R.id.hud_text_bps_value);

        textViewHudPitchValue = (TextView)view.findViewById(R.id.hud_text_pitch_value);
        textViewHudPitchValue.setTypeface(visitor2Font);

        textViewHudRollValue = (TextView)view.findViewById(R.id.hud_text_roll_value);
        textViewHudRollValue.setTypeface(visitor2Font);

        textViewHudRollMinValue = (TextView)view.findViewById(R.id.hud_text_roll_min_value);
        textViewHudRollMinValue.setTypeface(novamonoFont);

        textViewHudRollMaxValue = (TextView)view.findViewById(R.id.hud_text_roll_max_value);

        textViewHudPowerMaxValue = (TextView)view.findViewById(R.id.hud_text_pitch_max_value);
        textViewHudPowerMinValue = (TextView)view.findViewById(R.id.hud_text_pitch_min_value);

        textViewDrive = (TextView)view.findViewById(R.id.hud_text_drive_label);
        textViewDrive.getPaint().setAntiAlias(false);
        textViewReverse = (TextView)view.findViewById(R.id.hud_text_reverse_label);
        textViewReverse.getPaint().setAntiAlias(false);

        this.setElementsFont(R.id.hud_text_roll_max_value, novamonoFont);
        this.setElementsFont(R.id.hud_text_ip_value, visitorFont);
        this.setElementsFont(R.id.hud_text_connection_value, visitorFont);
        this.setElementsFont(R.id.hud_text_drive_label, visitorFont);
        this.setElementsFont(R.id.hud_text_reverse_label, visitorFont);
        this.setElementsFont(R.id.hud_text_pitch_min_value, novamonoFont);
        this.setElementsFont(R.id.hud_text_pitch_max_value, novamonoFont);
        this.setElementsFont(R.id.hud_text_tilt_label, visitorFont);
        
        this.setHudConnection("unknown");
        //this.setHudBps(0);
        this.setHudPitch(128.0f);
        this.setHudRoll(0.0f);
        
        // Get the sharedPreferences so the values can be set
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        this.setHudIp(sharedPreferences.getString("pref_server_ip", "192.168.1.115"));
        
        return view;
    }

    /**
     * Sets the ids listed to the font listed
     */
    private void setElementsFont(int elementID, Typeface font){
        TextView textElement = (TextView)view.findViewById(elementID);
        textElement.setTypeface(font);
    }
    
    /**
     * Set our textbox ip value
     *
     * @param ip - the ip address
     */
    public void setHudIp(String ip)
    {
        textViewHudIpValue.setText(ip);
    }
    
    /**
     * Set our textbox connection value
     *
     * @param ip - the ip address
     */
    public void setHudConnection(String ip)
    {
        textViewHudConnectionValue.setText(ip);
    }
    
    /**
     * Set our textbox BytesPerSecond value
     *
     * @param bps - the bps value
     */
    public void setHudBps(int bps)
    {
        if (bps < 0)
            textViewHudBpsValue.setText("Negative?");

        textViewHudBpsValue.setText(Integer.toString(bps));
    }
    
    /**
     * Set our textbox Pitch value
     *
     * @param pitch - the pitch value
     */
    public void setHudPitch(float pitch)
    {
        //Display invalid values when things are out of range
        if (pitch < 0.0f || pitch > 100.0f)
            textViewHudPitchValue.setText(Html.fromHtml("<font color='#D93600'>---</font>"));

        int pitchValue = Math.round(visualPowerMapping.remapValue(pitch));


        textViewHudPitchValue.setText(""+pitchValue);

        // This slows shit up, No idea why, investigate. because having this would be good
        /*
        if (pitchValue == 100)
            textViewHudPitchValue.setText(Html.fromHtml("<font color='#D93600'>100</font>"));
        else if (pitchValue == 0)
            textViewHudPitchValue.setText(Html.fromHtml("<font color='#D93600'>000</font>"));
        else if (pitchValue < 10)
            textViewHudPitchValue.setText(Html.fromHtml("<font color='#333333'>00</font>"+pitchValue));
        else
            textViewHudPitchValue.setText(Html.fromHtml("<font color='#333333'>0</font>"+pitchValue));
        */
    }



    /**
     * Set our textbox Pitch value
     *
     * @param car - the car model
     */
    public void setMaxMinValues(ModelVehicleCar car)
    {
        if(car.getSettingChange()){
            Log.d("Dashee", "Updating minor settings");
            String steerMin = String.format("%03d", Math.round(car.getSteerMin()));
            String steerMax = String.format("%03d", Math.round(car.getSteerMax()));
            String powerMin = String.format("%03d", Math.round(car.getPowerMin()));
            String powerMax = String.format("%03d", Math.round(car.getPowerMax()));
            textViewHudRollMinValue.setText(steerMin+"");
            textViewHudRollMaxValue.setText(steerMax+"");

            textViewHudPowerMinValue.setText(powerMin+"");
            textViewHudPowerMaxValue.setText(powerMax+"");
        }
    }

    
    /**
     * Set our textbox Roll value
     *
     * @param roll - the pitch value
     */
    public void setHudRoll(float roll)
    {
        //Display invalid values when things are out of range
        if (roll < 0.0f || roll > 100.0f)
            textViewHudRollValue.setText(Html.fromHtml("<font color='#D93600'>---</font>"));

        int rollValue = Math.round(roll);


        if (rollValue != prevSteer){
            if (rollValue == 0 || rollValue == 100){
                // Get instance of Vibrator from current Context
                Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

                // Vibrate for 30 milliseconds
                v.vibrate(30);
            }
            textViewHudRollValue.setText(rollValue-50+"");
        }

        prevSteer = rollValue;

        /*if (rollValue == 100)
            textViewHudRollValue.setText(Html.fromHtml("<font color='#D93600'>100</font>"));
        else if (rollValue == 0)
            textViewHudRollValue.setText(Html.fromHtml("<font color='#D93600'>000</font>"));
        else if (rollValue < 10)
            textViewHudRollValue.setText(Html.fromHtml("<font color='#333333'>00</font>"+rollValue));
        else
            textViewHudRollValue.setText(Html.fromHtml("<font color='#333333'>0</font>"+rollValue));*/
    }
    
    /**
     * Rotate our HUD. Given a value, rotate our hud accordingly
     *
     * @param roll - The rotation value.
     */
    public void rotateHud(float roll)
    {
        //layout_hud.setRotation(-1.0f*(roll - 50.0f));
    	draw_hud.setTilt(roll);
    }
    
    /**
     * Change our Hud, according to the vehicle.
     * The vehicle must be a ModelVehicleCar, other
     * wise an exception will be thrown.
     *
     * @param vehicle - The vehicle
     */
    public void setPosition(ModelVehicle vehicle)
    {
        if (vehicle instanceof ModelVehicleCar)
        {
            ModelVehicleCar car = (ModelVehicleCar)vehicle;
            this.steer = car.getSteer();
            this.setHudRoll(this.steer);
            this.setHudPitch(car.getPower());
            this.rotateHud(this.steer);
            this.setMaxMinValues(car);
            float powerVal = (this.Reverse) ? 128-car.getPower() : car.getPower()-128 ;
            draw_hud.setPowerPerc((powerVal)/128);
        }
    }
}
