package org.dashee.remote.fragment.hud;

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
import android.text.Html;

import java.util.Observable;
import java.util.Observer;

import android.widget.TextView;

import org.dashee.remote.DrawHud;
import org.dashee.remote.R;
import org.dashee.remote.RangeMapping;
import org.dashee.remote.model.*;

/**
 * This is our HUD Fragment. which controls, sending and receiving controls
 * from our servers, and updating our HUD
 * 
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class Car 
    extends org.dashee.remote.fragment.Hud
    implements Observer
{
    /**
     * Draw our Hud object.
     */
    private DrawHud hud;

    /**
     * The view of the fragment. Useful to retrieve the layout values within 
     * other methods
     */
    private View view;
    
    /**
     * Assign area for current steer value
     */
    private float steer;

    /**
     * Dont update steer UI if it's same as last time.
     */
    private int prevSteer = -1;
            
    /**
     * Handlers to our text view.
     */    
    private TextView textViewHudIpValue;
    private TextView textViewHudConnectionValue;
    private TextView textViewHudBpsValue;

    /**
     * Throttle TextView handlers.
     */
    private TextView textViewHudThrottleValue;
    private TextView textViewHudThrottleMinValue;
    private TextView textViewHudThrottleMaxValue;

    /**
     * Roll TextView handlers.
     */
    private TextView textViewHudRollValue;
    private TextView textViewHudRollMinValue;
    private TextView textViewHudRollMaxValue;

    /**
     * Drive and Reverse TextView Handlers
     */
    private TextView textViewDrive;
    private TextView textViewReverse;

    /**
     * To set weather or not we are in reverse
     */
    private boolean Reverse = false;

    /**
     * Power mapping values
     */
    private RangeMapping visualPowerMapping;
    
    /**
     * Handle to our Phone schematics. This will return
     * our phones roll, pitch state, by notifying the observer
     */
    private ModelPhonePosition modelPosition;

    /**
     * Constructor. Required by Fragment type Objects,
     * and they have to be public
     */
    public Car()
    {
        this.visualPowerMapping = new RangeMapping(0.0f,255.0f,-50.0f,50.0f);
    }
    
    /**
     * Set our HUD. Initiate the servers IP address so our thread can talk to 
     * it, start our thread and return the view which is required by this 
     * function
     */
    public View onCreateView(
            LayoutInflater inflater, 
            ViewGroup container, 
            Bundle savedInstanceState
        ) 
    {
        view = inflater.inflate(R.layout.fragment_hud, container, false);
        assert view != null;

        this.initHud();
        this.initModels();
        this.initThrottleListener();
        this.initDriveTypeListener();
        this.initOptionsButtonListener();
        this.initTextViews();
        
        this.setHudConnection("unknown");
        //this.setHudBps(0);
    //    this.setThrottle(128);
     //   this.setRoll(0.0f);
        
        // Get the sharedPreferences so the values can be set
        SharedPreferences sharedPreferences 
            = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        this.setHudIp(
                sharedPreferences.getString("pref_server_ip", "192.168.1.115")
            );
        
        return view;
    }

    /**
     * Initialize our HUD which is used to draw the graphics on our linear 
     * layout.
     */
    private void initHud()
    {
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.hud_canvas);
        hud = new DrawHud (this.getActivity(), view);
        layout.addView(hud);
    }

    /**
     * Create new instances of all models used inside the Fragment.
     */
    private void initModels()
    {
        // This will initialise our PhonePosition Observer,
        // So our this.update function can handle updates 
        this.modelPosition = new ModelPhonePosition(this.getActivity());
        this.modelPosition.addObserver(this);
    }

    /**
     * When a user slides on the screen he and she increase or decrease the 
     * throttle value of the vehicle. This function initializes the event 
     * handler
     */
    private void initThrottleListener()
    {
        // Use the height and width of the image and the position of the stick 
        // to map to car power value
        final ImageView iv = (ImageView)view.findViewById(R.id.power_stick);

        iv.setOnTouchListener(new OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) 
            {
                float mapVal = 128.0f;
    
                // Only if the user is still touching the screen
                // will the mapValue change
                if (event.getAction() != MotionEvent.ACTION_UP) 
                {
                    if(Reverse)
                    {
                        mapVal = RangeMapping.mapValue(
                            event.getY(), 
                            120, 
                            iv.getHeight()-72, 
                            0, 
                            128
                        );
                    }
                    else
                    {
                        mapVal = RangeMapping.mapValue(
                            event.getY(), 
                            120, 
                            iv.getHeight()-72, 
                            255, 
                            128
                        );
                    }
                }
                    
                setThrottle((int)mapVal);

                return true;
            }
        });
    }

    /**
     * Every time the Power HUD value is clicked the car can go from drive to 
     * reverse. This handler deals with creating tones and highlighting the 
     * current state
     */
    public void initDriveTypeListener()
    {
        LinearLayout powerToggle = (LinearLayout) view.findViewById(
                R.id.power_direction_toggle
            );
        powerToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                Reverse = !Reverse;
                final ToneGenerator tg 
                    = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
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
    }

    /**
     * Start the preferences activity every time the dashee Icon is clicked.
     */
    private void initOptionsButtonListener()
    {
        Button optsButton = (Button)view.findViewById(R.id.dot_settings);
        optsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) 
            {
                Intent intent = new Intent(
                    getActivity(), 
                    org.dashee.remote.preference.PreferencesActivity.class
                );
                startActivity(intent);
            }
        });
    }

    /**
     * Set the Aliasing and fonts on all listeners.
     *
     * Useful to do this using code as it provides more granularity on the 
     * things which can be changed
     */
    private void initTextViews()
    {
        // The original
        Typeface visitorFont = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/visitor1.ttf"
            );
        // The modified version
        Typeface visitor2Font = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/visitor2.ttf"
            );
        // Other font used in text used to highlight small numeric text
        Typeface novamonoFont = Typeface.createFromAsset(
                getActivity().getAssets(),
                "fonts/novamono2.ttf"
            );

        // Generic values
        textViewHudIpValue 
            = (TextView)view.findViewById(R.id.hud_text_ip_value);
        textViewHudIpValue.getPaint().setAntiAlias(false);
        textViewHudIpValue.setTypeface(visitorFont);

        textViewHudConnectionValue 
            = (TextView)view.findViewById(R.id.hud_text_connection_value);
        textViewHudConnectionValue.getPaint().setAntiAlias(false);
        textViewHudConnectionValue.setTypeface(visitorFont);

        textViewHudBpsValue 
            = (TextView)view.findViewById(R.id.hud_text_bps_value);

        // Throttle
        textViewHudThrottleValue 
            = (TextView)view.findViewById(R.id.hud_text_throttle_value);
        textViewHudThrottleValue.setTypeface(visitor2Font);

        textViewHudThrottleMaxValue 
            = (TextView)view.findViewById(R.id.hud_text_throttle_max_value);
        textViewHudThrottleMaxValue.setTypeface(novamonoFont);

        textViewHudThrottleMinValue 
            = (TextView)view.findViewById(R.id.hud_text_throttle_min_value);
        textViewHudThrottleMinValue.setTypeface(novamonoFont);

        // Roll
        textViewHudRollValue 
            = (TextView)view.findViewById(R.id.hud_text_roll_value);
        textViewHudRollValue.setTypeface(visitor2Font);

        textViewHudRollMinValue 
            = (TextView)view.findViewById(R.id.hud_text_roll_min_value);
        textViewHudRollMinValue.setTypeface(novamonoFont);

        textViewHudRollMaxValue 
            = (TextView)view.findViewById(R.id.hud_text_roll_max_value);
        textViewHudRollMaxValue.setTypeface(novamonoFont);

        // Drive/Reverse
        textViewDrive 
            = (TextView)view.findViewById(R.id.hud_text_drive_label);
        textViewDrive.getPaint().setAntiAlias(false);
        textViewDrive.setTypeface(visitorFont);

        textViewReverse 
            = (TextView)view.findViewById(R.id.hud_text_reverse_label);
        textViewReverse.getPaint().setAntiAlias(false);
        textViewReverse.setTypeface(visitorFont);
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
     * Set our textbox Pitch value
     *
     * @param car - the car model
     */
    public void setMaxMinValues(ModelVehicleCar car)
    {
        if(car.getSettingChange())
        {
            Log.d("Dashee", "Updating minor settings");
            String steerMin 
                = String.format("%03d", Math.round(car.getSteerMin()));
            String steerMax 
                = String.format("%03d", Math.round(car.getSteerMax()));
            String powerMin 
                = String.format("%03d", Math.round(car.getPowerMin()));
            String powerMax 
                = String.format("%03d", Math.round(car.getPowerMax()));

            textViewHudRollMinValue.setText(steerMin+"");
            textViewHudRollMaxValue.setText(steerMax+"");

            textViewHudThrottleMinValue.setText(powerMin+"");
            textViewHudThrottleMaxValue.setText(powerMax+"");
        }
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
     * Set the roll value of our view. This will change the value's of the text
     * box and also update the rotation of the steering value
     *
     * TODO Refactor this, as this does not work
     *
     * @param roll the pitch value
     */
    public void setRoll(float roll)
    {
        //Display invalid values when things are out of range
        if (roll < 0.0f || roll > 100.0f)
            textViewHudRollValue.setText(
                    Html.fromHtml("<font color='#D93600'>---</font>")
                );

        int rollValue = Math.round(roll);

        if (rollValue != prevSteer)
        {
            if (rollValue == 0 || rollValue == 100)
            {
                // Get instance of Vibrator from current Context
                Vibrator v = (Vibrator) getActivity().getSystemService(
                        Context.VIBRATOR_SERVICE
                    );
                // Vibrate for 30 milliseconds
                v.vibrate(30);
            }

            textViewHudRollValue.setText(rollValue-50+"");
        }

        prevSteer = rollValue;
    	hud.setTilt(roll);
    }
    
    /**
     * Set our Throttle value. Update the button values and also update the 
     * throttle applied in the hud view
     *
     * TODO Fix the Hud drawing the correct bar on the left
     *
     * @param throttle The value of throttle applied
     */
    public void setThrottle(int throttle)
    {
        this.vehicle.setThrottle(throttle);

        //Display invalid values when things are out of range
        if (throttle < 0.0 || throttle > 255)
            textViewHudThrottleValue.setText(
                    Html.fromHtml("<font color='#D93600'>---</font>")
                );
        else
        {
            textViewHudThrottleValue.setText(
                    Math.round(
                        visualPowerMapping.remapValue(throttle)
                    ) + ""
                );
            
            // TODO change the name of this thing
            hud.setPowerPerc(throttle/128);
        }
    }

    /**
     *  Update our view and model. Given the phone's roll
     *  we update our server/model using our thread and we also
     *  update the HUD rotational value
     *  
     *  @param o The observer handler
     *  @param arg The arguments to the Observer
     */
    public void update(Observable o, Object arg)
    {
        if (o instanceof ModelPhonePosition)
        {
            this.setRoll(modelPosition.getRoll());
        }
    }

    /**
     * Pause our values
     */
    public void onPause()
    {
        this.modelPosition.onPause();
        super.onPause();
    }   

    /**
     * Resume our values
     */
    public void onResume()
    {
        this.modelPosition.onResume();
        super.onResume();
    }
}
