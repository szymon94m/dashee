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

import org.dashee.remote.exception.OutOfRange;
import org.dashee.remote.DrawHud;
import org.dashee.remote.R;
import org.dashee.remote.RangeMapping;
import org.dashee.remote.model.PhoneSensors;
import org.dashee.remote.fragment.Hud;

/**
 * This is our HUD Fragment. which controls, sending and receiving controls
 * from our servers, and updating our HUD
 * 
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class Car 
    extends Hud
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
    private TextView tvIp;
    private TextView tvStatus;
    private TextView tvBPS;

    /**
     * Throttle TextView handlers.
     */
    private TextView tvThrottle;
    private TextView tvThrottleMin;
    private TextView tvThrottleMax;

    /**
     * Roll TextView handlers.
     */
    private TextView tvRoll;
    private TextView tvRollMin;
    private TextView tvRollMax;

    /**
     * Drive and Reverse TextView Handlers
     */
    private TextView tvDrive;
    private TextView tvReverse;

    /**
     * To set weather or not we are in reverse
     */
    private boolean reverse = false;

    /**
     * Handle to our Phone schematics. This will return
     * our phones roll, pitch state, by notifying the observer
     */
    private PhoneSensors phoneSensors;

    /**
     * Constructor. Required by Fragment type Objects,
     * and they have to be public
     */
    public Car()
    {
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
        this.phoneSensors = new PhoneSensors(this.getActivity());
        this.phoneSensors.addObserver(this);
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
                    if(reverse)
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
                reverse = !reverse;
                final ToneGenerator tg 
                    = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                if(reverse)
                {
                    tg.startTone(ToneGenerator.TONE_PROP_ACK);
                    tvDrive.setTextColor(Color.parseColor("#444444"));
                    tvReverse.setTextColor(Color.parseColor("#D93600"));
                }
                else
                {
                    tg.startTone(ToneGenerator.TONE_PROP_BEEP2);
                    tvReverse.setTextColor(Color.parseColor("#444444"));
                    tvDrive.setTextColor(Color.parseColor("#2FB900"));
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
        tvIp = (TextView)view.findViewById(R.id.ip);
        tvIp.getPaint().setAntiAlias(false);
        tvIp.setTypeface(visitorFont);

        tvStatus = (TextView)view.findViewById(R.id.status);
        tvStatus.getPaint().setAntiAlias(false);
        tvStatus.setTypeface(visitorFont);

        tvBPS = (TextView)view.findViewById(R.id.BPS_value);
        tvBPS.getPaint().setAntiAlias(false);
        tvBPS.setTypeface(visitorFont);

        // Throttle
        tvThrottle = (TextView)view.findViewById(R.id.throttle);
        tvThrottle.setTypeface(visitor2Font);

        tvThrottleMax = (TextView)view.findViewById(R.id.throttle_max);
        tvThrottleMax.setTypeface(novamonoFont);

        tvThrottleMin = (TextView)view.findViewById(R.id.throttle_min);
        tvThrottleMin.setTypeface(novamonoFont);

        // Roll
        tvRoll = (TextView)view.findViewById(R.id.roll);
        tvRoll.setTypeface(visitor2Font);

        tvRollMin = (TextView)view.findViewById(R.id.roll_min);
        tvRollMin.setTypeface(novamonoFont);

        tvRollMax = (TextView)view.findViewById(R.id.roll_max);
        tvRollMax.setTypeface(novamonoFont);

        // Drive/Reverse
        tvDrive = (TextView)view.findViewById(R.id.drive);
        tvDrive.getPaint().setAntiAlias(false);
        tvDrive.setTypeface(visitorFont);

        tvReverse = (TextView)view.findViewById(R.id.reverse);
        tvReverse.getPaint().setAntiAlias(false);
        tvReverse.setTypeface(visitorFont);

        // Get the sharedPreferences so the values can be set
        SharedPreferences sp 
            = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

        // Set the text views
        this.setConnection("unknown");
        this.setIp(
                sp.getString("pref_server_ip", "xxx.xxx.xxx.xxx")
            );
        this.setRollMin(sp.getInt("roll_min", 0));
        this.setRollMax(sp.getInt("roll_max", 0));
    }

    /**
     * Set our textbox ip value
     *
     * @param ip - the ip address
     */
    public void setIp(String ip)
    {
        if (tvIp == null)
           return;

        tvIp.setText(ip);
    }

    /**
     * Set our textbox connection value
     *
     * @param value the value to update
     */
    public void setConnection(String value)
    {
        if (tvStatus == null)
            return;

        tvStatus.setText(value);
    }

    /**
     * Set the Minimum Roll
     *
     * @param value The value to set
     */
    public void setRollMin(int value)
    {
        if (tvRollMin == null)
            return;

        tvRollMin.setText(""+value);
    }

    /**
     * Set the Minimum Roll
     *
     * @param value The value to set
     */
    public void setRollMax(int value)
    {
        if (tvRollMax == null)
            return;

        tvRollMax.setText(""+value);
    }

    /**
     * Set our textbox BytesPerSecond value
     *
     * @param bps - the bps value
     */
    public void setBps(int bps)
    {
        if (tvBPS == null)
            return;

        if (bps < 0)
            tvBPS.setText("Negative?");
        else
            tvBPS.setText(Integer.toString(bps));
    }

    /**
     * Set the roll value of our view. This will change the value's of the text
     * box and also update the rotation of the steering value
     *
     * @param roll the pitch value
     */
    public void setRoll(float roll)
    {
        try
        {
            // This is important, as the sensor calls this before the OS can 
            // call setVehicle method
            if (this.vehicle == null)
                return;

            // Set the vehicle value
            this.vehicle.setRoll(
                    Math.round(
                        RangeMapping.mapValue(roll, -0.5f, 0.5f, 255.0f, 0.0f)
                    )
                );

            // Vibrate and print the text in a color, when min or max is hit
            if (
                    this.vehicle.getRoll() == this.vehicle.getRollMax() ||
                    this.vehicle.getRoll() == this.vehicle.getRollMin()
                )

            {
                // Get instance of Vibrator from current Context
                Vibrator v = (Vibrator) getActivity().getSystemService(
                        Context.VIBRATOR_SERVICE
                    );
                // Vibrate for 30 milliseconds
                v.vibrate(30);
            }

            // Convert the roll value from the vehicle, so min, max is 
            // compensated
            float mapped = RangeMapping.mapValue(
                    this.vehicle.getRoll(), 
                    0,
                    255,
                    0.0f,
                    100.0f
                );


            tvRoll.setText(Math.round(mapped)-50+"");
            hud.setTilt(mapped);
        }
        catch (OutOfRange e)    
        {
            tvRoll.setText(
                        Html.fromHtml("<font color='#D93600'>---</font>")
                    );
        }
    }

    /**
     * Set our Throttle value. Update the button values and also update the 
     * throttle applied in the hud view
     *
     * @param throttle The value of throttle applied
     */
    public void setThrottle(int throttle)
    {
        try
        {
            // Set the throttle value
            this.vehicle.setThrottle(throttle);

            float mapped 
                = RangeMapping.mapValue(
                        this.vehicle.getThrottle(), 
                        0.0f, 
                        255.0f, 
                        -50.0f, 
                        50.0f
                    );

            tvThrottle.setText(Math.round(mapped) + "");

            // If we are in reverse we go from 0-128 other wise we go from 
            // 128-255. The throttle percentage sent to hud is from 0.0 to 1.0.
            //
            // Therefore when we are in reverse we need to calculate the 
            // percentage from the range 0-128 other wise we calculate from 
            // 128-255
            float percentage = 0.0f;
            if (reverse) 
                percentage = (this.vehicle.getThrottle() / -128.0f) + 1.0f;
            else
                percentage = (this.vehicle.getThrottle() -128) / 128.0f;

            // Change our hud bar value
            hud.setThrottle(percentage);
        }

        // If throttle fails, set this to the error string
        catch (OutOfRange e)
        {
            tvThrottle.setText(
                    Html.fromHtml("<font color='#D93600'>---</font>")
                );
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
        if (o instanceof PhoneSensors)
        {
            this.setRoll(phoneSensors.getRoll());
        }
    }

    /**
     * Pause our values
     */
    public void onPause()
    {
        this.phoneSensors.onPause();
        super.onPause();
    }

    /**
     * Resume our values
     */
    public void onResume()
    {
        this.phoneSensors.onResume();
        super.onResume();
    }
}
