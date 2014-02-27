package org.dashee.remote;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.dashee.remote.exception.OutOfRange;

/**
 * This is currently mapping out the HUD display as of Shahmir's hud.fw.png, it
 * should draw a central steer arc which will move when the car is steering. 
 * To the left and right are gauges reflecting power and battery.
 *
 * @author David Buttar
 * @author Shahmir Javaid
 */
public class DrawHud 
    extends View
{
    Context context;

    /**
     * Various paths to be defined onSizeChanged
     */
    Path steerPath = new Path();
    Path[] throttlePaths = new Path[12];
    Path[] batteryPaths = new Path[12];
    Path[] throttlePathsInner = new Path[12];
    Path[] batteryPathsInner = new Path[12];

    /**
     * Arc paths
     */
    Path powerArcPath = new Path();
    Path batteryArcPath = new Path();
    Path reverseArcPath = new Path();
    
    /**
     * Steer arc is  at the center of the design
     * other aspects are offset from it.
     */
    float steerArcRadius;
    float innerGaugeRadius;
    float innerGaugeRadius2;
    float outerGaugeRadius;
    
    /**
     * The center points of the canvas
     */
    float centerX = 0.0f;
    float centerY = 0.0f;

    // Top and bottom of the gauge paths
    double gaugeTopY;
    double gaugeBottomY;
    double powerGaugeTopY;
    double powerGaugeBottomY;
    double reverseGaugeTopY;
    double reverseGaugeBottomY;

    double gaugeBarHieght;
    double gaugeBarGap;
    float lockLineWidth;
    
    /**
     * Values to be updated to reflect app state
     */
    float tilt = 50.0f;
    int throttle = 128;
    
    /**
     * Paint for general items
     */
    Paint steerLine;
    Paint horizonLine;
    Paint steerLineLock;

    /**
     * Arc paints
     */
    Paint powerArc;
    Paint batteryArc;
    Paint reverseArc;

    /**
     * Paint for Bars
     */
    Paint activePowerBar;
    Paint activePowerBarInset;
    Paint activeReverseBar;
    Paint activeReverseBarInset;
    Paint activeBatteryBar;
    Paint activeBatteryBarInset;
    Paint inactiveBar;
    Paint inactiveBarInset;
    Paint inactiveReverseBar;
    Paint inactiveReverseBarInset;

    /**
     * Store the view variable. Used for other classes to call
     */
    View view;

    /**
     * Set our paint colours, for use with the Draw functions
     * 
     * @param mContext - The context required by super
     */
    public DrawHud(Context context, View view)
    {
        super(context);

        this.context = context;
        this.view = view;
        
        initSteerLinePaint();
        initPowerBarPaint();
        initReverseBarPaint();
        initBatteryBarPaint();
        initInactiveBarPaint();
        initArcsPaint();
    }

    /**
     * Initialize the steer line paint.
     */
    private void initSteerLinePaint()
    {
        int horizonLineColor = 0x333333;
        int lineColor = 0x666666;
        int lockColor = 0xD93600;

        steerLine = new Paint();
        steerLine.setAntiAlias(true);
        steerLine.setColor(lineColor);
        steerLine.setAlpha(255);
        steerLine.setStrokeWidth(2.0f);
        steerLine.setStyle(Paint.Style.STROKE);

        steerLineLock = new Paint();
        steerLineLock.setAntiAlias(true);
        steerLineLock.setColor(lockColor);
        steerLineLock.setAlpha(255);
        steerLineLock.setStrokeWidth(5.0f);
        steerLineLock.setStyle(Paint.Style.STROKE);
        
        horizonLine = new Paint();
        horizonLine.setAntiAlias(true);
        horizonLine.setColor(horizonLineColor);
        horizonLine.setAlpha(255);
        horizonLine.setStrokeWidth(2.0f);
        horizonLine.setStyle(Paint.Style.STROKE);
    }

    /**
     * Initialize the View
     */
    private void initPowerBarPaint()
    {
        activePowerBar = new Paint();
        activePowerBar.setAntiAlias(true);
        activePowerBar.setColor(0xD96D00);
        activePowerBar.setAlpha(255);
        activePowerBar.setStyle(Paint.Style.FILL);
        
        activePowerBarInset = new Paint();
        activePowerBarInset.setAntiAlias(true);
        activePowerBarInset.setColor(0xDF8429);
        activePowerBarInset.setAlpha(255);
        activePowerBarInset.setStyle(Paint.Style.FILL);
    }

    /**
     * Initialize the reverse Bar
     */
    private void initReverseBarPaint()
    {
        activeReverseBar = new Paint();
        activeReverseBar.setAntiAlias(true);
        activeReverseBar.setColor(0x8C0000);
        activeReverseBar.setAlpha(255);
        activeReverseBar.setStyle(Paint.Style.FILL);
        
        activeReverseBarInset = new Paint();
        activeReverseBarInset.setAntiAlias(true);
        activeReverseBarInset.setColor(0x9E2929);
        activeReverseBarInset.setAlpha(255);
        activeReverseBarInset.setStyle(Paint.Style.FILL);

        inactiveReverseBar = new Paint();
        inactiveReverseBar.setAntiAlias(true);
        inactiveReverseBar.setColor(0x11111A);
        inactiveReverseBar.setAlpha(255);
        inactiveReverseBar.setStyle(Paint.Style.FILL);

        inactiveReverseBarInset = new Paint();
        inactiveReverseBarInset.setAntiAlias(true);
        inactiveReverseBarInset.setColor(0x37373F);
        inactiveReverseBarInset.setAlpha(255);
        inactiveReverseBarInset.setStyle(Paint.Style.FILL);
    }

    /**
     * Initialize the battery bar.
     */
    private void initBatteryBarPaint()
    {
        activeBatteryBar = new Paint();
        activeBatteryBar.setAntiAlias(true);
        activeBatteryBar.setColor(0xEEEEEE);
        activeBatteryBar.setAlpha(255);
        activeBatteryBar.setStyle(Paint.Style.FILL);
        
        activeBatteryBarInset = new Paint();
        activeBatteryBarInset.setAntiAlias(true);
        activeBatteryBarInset.setColor(0xC8C8C8);
        activeBatteryBarInset.setAlpha(255);
        activeBatteryBarInset.setStyle(Paint.Style.FILL);
    }

    /**
     * Initialize the inactive bar values
     */
    private void initInactiveBarPaint()
    {
        inactiveBar = new Paint();
        inactiveBar.setAntiAlias(true);
        inactiveBar.setColor(0x20202F);
        inactiveBar.setAlpha(255);
        inactiveBar.setStyle(Paint.Style.FILL);

        inactiveBarInset = new Paint();
        inactiveBarInset.setAntiAlias(true);
        inactiveBarInset.setColor(0x444450);
        inactiveBarInset.setAlpha(255);
        inactiveBarInset.setStyle(Paint.Style.FILL);
    }

    /**
     * Initialize all the arcs.
     */
    private void initArcsPaint()
    {
        powerArc = new Paint();
        powerArc.setAntiAlias(true);
        powerArc.setColor(0xD96D00);
        powerArc.setAlpha(255);
        powerArc.setStrokeWidth(3.0f);
        powerArc.setStyle(Paint.Style.STROKE);

        reverseArc = new Paint();
        reverseArc.setAntiAlias(true);
        reverseArc.setColor(0x6D0000);
        reverseArc.setAlpha(255);
        reverseArc.setStrokeWidth(3.0f);
        reverseArc.setStyle(Paint.Style.STROKE);
        
        batteryArc = new Paint();
        batteryArc.setAntiAlias(true);
        batteryArc.setColor(0xFFFFFF);
        batteryArc.setAlpha(255);
        batteryArc.setStrokeWidth(3.0f);
        batteryArc.setStyle(Paint.Style.STROKE);
    }
    
    /**
     * Recalculate on size change.
     *
     * Implementing this function gives access to the canvas width and height 
     * meaning those calculation can be done as required (usually just once), 
     * instead of doing them constantly in the draw.
     *
     * @param canvasWidth The width of the canvas
     * @param canvasHeight The height of the canvas
     * @param oldw Old width value
     * @param oldh Old height value
     */
    protected void onSizeChanged (
            int canvasWidth, 
            int canvasHeight, 
            int oldw, 
            int oldh
        )
    {
    	// Noticed it sometimes runs with 0 values, not sure why.
    	if(canvasWidth == 0 || canvasHeight == 0) return;
    	
    	this.centerX = canvasWidth/2;
    	this.centerY = canvasHeight/2;
    	
    	this.steerArcRadius = (canvasHeight - canvasHeight*0.08f) / 2.0f;

    	// Now three other arc Points on the diagram are percentage increases on
        // the steerArc
    	this.innerGaugeRadius = this.steerArcRadius*1.16f; // 16% bigger
    	this.innerGaugeRadius2 = this.steerArcRadius*1.31f; // 31% bigger
    	this.outerGaugeRadius = this.steerArcRadius*1.39f; // 39% bigger
    	
    	this.lockLineWidth = Math.round(canvasWidth*0.08);
    	this.gaugeBottomY = canvasHeight-Math.round(canvasHeight*0.1);
    	this.gaugeBarHieght = Math.round(canvasHeight*0.053);
    	this.gaugeBarGap = Math.round(canvasHeight*0.015);
    	this.gaugeTopY 
            = gaugeBottomY - (12*this.gaugeBarHieght) -(11*this.gaugeBarGap);

        this.powerGaugeTopY = this.gaugeTopY;
        this.powerGaugeBottomY
            = gaugeBottomY - (3*this.gaugeBarHieght) -(3*this.gaugeBarGap);

        this.reverseGaugeTopY
            = gaugeBottomY - (3*this.gaugeBarHieght) -(2*this.gaugeBarGap);
        this.reverseGaugeBottomY = this.gaugeBottomY;
    	
    	// Start Building Our HUD paths.
    	this.buildSteerPath();
    	this.addGaugePath(
                this.throttlePaths, 
                true, 
                this.innerGaugeRadius, 
                this.outerGaugeRadius
            );
    	this.addGaugePath(
                this.throttlePathsInner, 
                true, 
                this.innerGaugeRadius2, 
                this.outerGaugeRadius
            );
    	this.addGaugePath(
                this.batteryPaths, 
                false, 
                this.innerGaugeRadius, 
                this.outerGaugeRadius
            );
    	this.addGaugePath(
                this.batteryPathsInner, 
                false, 
                this.innerGaugeRadius2, 
                this.outerGaugeRadius
            );

        this.setBatteryArc();
        this.setPowerArc();
        this.setReverseArc();

        // Position some xml elements
        LinearLayout layoutConnection
            = (LinearLayout)view.findViewById(R.id.connection);
        RelativeLayout.LayoutParams params 
            = (RelativeLayout.LayoutParams)layoutConnection.getLayoutParams();
        float textWidth = convertDpToPixel(73.0f, this.context);
        float textHeight = convertDpToPixel(11.0f, this.context);

        params.setMargins(
                Math.round(this.getMiddleInnerRightPos() - textWidth), 
                Math.round(this.centerY-textHeight), 
                10, 
                0
            );
        layoutConnection.setLayoutParams(params);
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device 
     * density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we 
     *  need to convert into pixels
     * @param context the context required to get the resource
     *
     * @return A float value to represent px equivalent to dp depending on 
     *  device density
     */
    public static float convertDpToPixel(float dp, Context context)
    {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return (dp * (metrics.densityDpi / 160f));
    }

    /**
     * Put together the arc that is to rotate on steer.
     */
    private void buildSteerPath()
    {
    	float steerArcSweepAngle = 280.0f;
        float steerArcStartAngle = -180.0f;
        
        final RectF steerRect = new RectF();
        float p1x = this.centerX - this.steerArcRadius;
        float p1y = this.centerY - this.steerArcRadius;
        float p2x = this.centerX + this.steerArcRadius;
        float p2y = this.centerY + this.steerArcRadius;
        steerRect.set(p1x, p1y, p2x, p2y);

        this.steerPath.arcTo(
                steerRect, 
                steerArcStartAngle, 
                -steerArcSweepAngle, 
                true
            );
    }
    
    /**
     * Draw the battery arc.
     */
    private void setBatteryArc()
    {
    	float radius = this.steerArcRadius*1.39f;

    	final RectF outerOval = new RectF();
        float p1x = this.centerX - radius;
        float p1y = this.centerY - radius;
        float p2x = this.centerX + radius;
        float p2y = this.centerY + radius;
        outerOval.set(p1x, p1y, p2x, p2y);

        float[] outerArcParams = this.getArcParams(
                this.gaugeBottomY, 
                this.gaugeTopY, 
                radius, 
                false,
                false
            );
        this.batteryArcPath
            .arcTo(outerOval, outerArcParams[0], -outerArcParams[1], true);
    }

    /**
     * Draw the throttle arc.
     */
    private void setPowerArc()
    {
    	float radius = this.steerArcRadius*1.39f;

    	final RectF outerOval = new RectF();
        float p1x = this.centerX - radius;
        float p1y = this.centerY - radius;
        float p2x = this.centerX + radius;
        float p2y = this.centerY + radius;
        outerOval.set(p1x, p1y, p2x, p2y);

        float[] outerArcParams = this.getArcParams(
                this.powerGaugeBottomY, 
                this.powerGaugeTopY, 
                radius, 
                false,
                true
            );
        this.powerArcPath
            .arcTo(outerOval, outerArcParams[0], -outerArcParams[1], true);
    }

    /**
     * Set the reverse arc.
     */
    private void setReverseArc()
    {
    	float radius = this.steerArcRadius*1.39f;

    	final RectF outerOval = new RectF();
        float p1x = this.centerX - radius;
        float p1y = this.centerY - radius;
        float p2x = this.centerX + radius;
        float p2y = this.centerY + radius;
        outerOval.set(p1x, p1y, p2x, p2y);

        float[] outerArcParams = this.getArcParams(
                this.reverseGaugeBottomY, 
                this.reverseGaugeTopY, 
                radius, 
                false,
                true
            );
        this.reverseArcPath
            .arcTo(outerOval, outerArcParams[0], -outerArcParams[1], true);
    }
    
    /**
     * Put together a collection of arc-ed bar paths, with an innerArc and 
     * outerArc.
     *
     * @param paths
     * @param leftHandSide
     * @param innerRadius
     * @param outerRadius
     */
    private void addGaugePath(
            Path[] paths, 
            boolean leftHandSide, 
            float innerRadius, 
            float outerRadius
        )
    {
    	final RectF innerOval = new RectF();
        final RectF outerOval = new RectF();
    	
    	//Log.d("dashee", "percHieght: "+h*0.1);
    	double curY = this.gaugeBottomY;
    	//Log.d("dashee", "rectSize: "+0.053);//38.0
    	double rectHeight = this.gaugeBarHieght;
    	//Log.d("dashee", "gap: "+ Math.round(0.015));//11
    	double gap = this.gaugeBarGap;
    	
    	float p1x = this.centerX - innerRadius;
        float p1y = this.centerY - innerRadius;
        float p2x = this.centerX + innerRadius;
        float p2y = this.centerY + innerRadius;
        
        float outerP1x = this.centerX - outerRadius;
        float outerP1y = this.centerY - outerRadius;
        float outerP2x = this.centerX + outerRadius;
        float outerP2y = this.centerY + outerRadius;
        
        innerOval.set(p1x, p1y, p2x, p2y);
        outerOval.set(outerP1x, outerP1y, outerP2x, outerP2y);
    	
    	for (int count = 0; count < 12; count++)
        {
            float[] innerArcParams = this.getArcParams(
                    curY, 
                    curY-rectHeight, 
                    innerRadius, 
                    false, 
                    leftHandSide
                );
            float[] outerArcParams = this.getArcParams(
                    curY, 
                    curY-rectHeight, 
                    outerRadius, 
                    true, 
                    leftHandSide
                );

            paths[count] = new Path();
            paths[count].arcTo(
                    innerOval, 
                    innerArcParams[0], 
                    -innerArcParams[1], 
                    true
                );
            paths[count].lineTo(outerArcParams[2], (float) (curY-rectHeight));
            paths[count].arcTo(
                    outerOval, 
                    outerArcParams[0], 
                    -outerArcParams[1], 
                    true
                );
            paths[count].lineTo(innerArcParams[2], (float) (curY));

            curY = curY-rectHeight-gap;
        }
    }
    
    /**
     * Based on desired starY and endY and radius get the appropriate 
     * 'path.arcTo' parameters.
     *
     * Uses getCircleX to find the x coordinates that intersect the centered 
     * circle, and then the getAngle function calculated the start angle form 
     * the center. 
     *
     * @param startY
     * @param endY
     * @param radius
     * @param reverse
     * @param leftHandSide
     */
    private float[] getArcParams(
            double startY, 
            double endY, 
            float radius, 
            boolean reverse, 
            boolean leftHandSide
        )
    {
    	double startXCoor[] = getCircleX(startY, radius);
    	double endXCoor[] = getCircleX(endY, radius);
    	
    	int xcoordinateSide = (leftHandSide) ? 0 : 1;
        
    	float startAngle = (float)this.getAngle(
                (float)startXCoor[xcoordinateSide], 
                (float)startY
            );
        float endAngle = (float)this.getAngle(
                (float)endXCoor[xcoordinateSide], 
                (float)(endY)
            );

        float sweepAngle = startAngle - endAngle;
        // If start angle is in 4th quarter and endAngle is in 3rd quarter
        // we will end up drawing almost a full circle want to avoid this
        // and sweep closest distance.
        if(startAngle < -270 && endAngle > -90)
            sweepAngle = (sweepAngle + 360);

        float[] returnArray = { 
            startAngle, 
            sweepAngle, 
            (float)startXCoor[xcoordinateSide]
        };
        if(reverse)
        {
            returnArray[0] = endAngle;
            returnArray[1] = -sweepAngle;
            returnArray[2] = (float) endXCoor[xcoordinateSide];
        }
        return returnArray;
    }
    
    /**
     * Calculated the x coordinate of a circle with radius r at the center of 
     * the display returns both coordinates as line will intersect 2 times.
     *
     * @param y
     * @param r
     *
     * @return The coordinates of the line
     */
    private double[] getCircleX(double y, double r)
    {
        double x 
            = Math.sqrt(Math.abs(Math.pow(r, 2) - Math.pow(y-this.centerY, 2)));
        double[] returnArray 
            = { Math.abs(x-this.centerX), Math.abs(-x-this.centerX) };
        return returnArray;
    }
   
    /**
     * Calculate the angle from the center of the screen to x, y. Starting at 
     * 3 O'Clock. Always return a negative angle, e.g -90 at 12, -180 at 9, 
     * -270 at 6.
     *
     * @param x
     * @param y
     *
     * @return The angle
     */
    private double getAngle(float x, float y)
    {
    	double angle1 = Math.atan2(
                this.centerY - this.centerY, 
                this.centerX - (this.centerX + 10)
            );

    	double angle2 = Math.atan2(this.centerY - y, this.centerX - x);

    	return -Math.toDegrees(angle1-angle2);
    }

    /**
     * ?.
     *
     * @return ?
     */ 
    public float getMiddleInnerLeftPos()
    {
        return this.centerX-this.steerArcRadius;
    }

    /**
     * ?.
     *
     * @return ?
     */ 
    public float getMiddleInnerRightPos()
    {
        return this.centerX+this.steerArcRadius;
    }
    
    /**
     * Draw our hud paths and apply appropriate paints
     * 
     * @param canvas - The canvas object to draw on.
     */
    protected void onDraw(Canvas canvas)
    {
        drawSteerLine(canvas);   
        drawPowerBars(canvas);
        drawBatteryBars(canvas);

        drawThrottleArc(canvas);
        canvas.drawPath(this.batteryArcPath, batteryArc);

        invalidate();
    }

    /**
     * Draw the steer line.
     *
     * @param canvas where to draw
     */
    private void drawSteerLine(Canvas canvas)
    {
        boolean leftLock = Math.floor(this.tilt) == 0;
        boolean rightLock = Math.ceil(this.tilt) == 100;

        //Mid line right
        if(rightLock)
            canvas.drawLine(
                    this.centerX + this.steerArcRadius - this.lockLineWidth,
                    this.centerY, 
                    this.centerX + this.steerArcRadius, 
                    this.centerY, 
                    steerLineLock
                );
        else
            canvas.drawLine(
                    this.centerX + this.steerArcRadius - this.lockLineWidth, 
                    this.centerY,
                    this.centerX + this.steerArcRadius, 
                    this.centerY, 
                    horizonLine
                );

        
        //Mid line left
    	if(leftLock)
            canvas.drawLine(
                    this.centerX - this.steerArcRadius + this.lockLineWidth, 
                    this.centerY,
                    this.centerX - this.steerArcRadius, 
                    this.centerY, 
                    steerLineLock
                );
    	else
            canvas.drawLine(
                    this.centerX - this.steerArcRadius + this.lockLineWidth, 
                    this.centerY,
    		    this.centerX - this.steerArcRadius, 
                    this.centerY, 
                    horizonLine
                );
    	
        canvas.save();
        canvas.rotate(this.tilt, this.centerX, this.centerY);

        if(rightLock || leftLock)
            canvas.drawPath(this.steerPath, steerLineLock);
        else
            canvas.drawPath(this.steerPath, steerLine);

        canvas.restore();
    }

    /**
     * Draw Power bars.
     *
     * If the throttle is less than 128 we draw the reverse bars. Otherwise
     * we print the power bars.
     *
     * Note for reverse 0-100% is represented by 2-0 as it is inverted when 
     * reversing. 
     *
     * @param canvas where to draw
     */
    public void drawPowerBars(Canvas canvas)
    {
        // We are in reverse mode
        if (throttle < 128)
        {

            // Calculate the percentage if the 100% is 0-127, and then
            // represent the percentage by the number of bars
            float percentage = 1.0f - (this.throttle / 127.0f);
            int cutOff = Math.round(percentage * 3);

            // Print the reverse bars
            for (int count = 0; count < 12; count++)
            {
                // Draw the power inactive bars
                if (count >= 3)
                {
                    canvas.drawPath(
                            this.throttlePaths[count], 
                            inactiveBar
                        );
                    canvas.drawPath(
                            this.throttlePathsInner[count], 
                            inactiveBarInset
                        );
                    continue;
                }
                // Draw the inactive reverse bars
                //
                // Note that `2-count >= cutOff` is the inverse, as the power
                // applied in the reverse goes from 2-0 instead of 0-2 so we 
                // need to invert by using `2-count >= cutOff`
                else if (2-count >= cutOff)
                {
                    canvas.drawPath(
                            this.throttlePaths[count], 
                            inactiveReverseBar
                        );
                    canvas.drawPath(
                            this.throttlePathsInner[count], 
                            inactiveReverseBarInset
                        );
                }
                else
                {
                    canvas.drawPath(
                            this.throttlePaths[count], 
                            activeReverseBar
                        );
                    canvas.drawPath(
                            this.throttlePathsInner[count], 
                            activeReverseBarInset
                        );
                }
            }
        }

        // Power mode
        else
        {
            // Generate the percentage from throttle, and represents
            // it in the number of bars which should be active. As throttle
            // is represented by the top 9 bars and there are total 12, we need
            // to calculate the percentage then add 3.
            float percentage = ((this.throttle-128) / 128.0f);
            int cutOff = Math.round(percentage * 9) + 3;

            for (int count = 0; count < 12; count++)
            {
                if (count < 3)
                {
                    canvas.drawPath(
                            this.throttlePaths[count], 
                            inactiveReverseBar
                        );
                    canvas.drawPath(
                            this.throttlePathsInner[count], 
                            inactiveReverseBarInset
                        );
                }
                else if (count >= cutOff)
                {
                    canvas.drawPath(this.throttlePaths[count], inactiveBar);
                    canvas.drawPath(
                            this.throttlePathsInner[count], 
                            inactiveBarInset
                        );
                }
                else
                {
                    canvas.drawPath(this.throttlePaths[count], activePowerBar);
                    canvas.drawPath(
                            this.throttlePathsInner[count], 
                            activePowerBarInset
                        );
                }
            }
        }
    }

    /**
     * Draw battery bars.
     *
     * @param canvas where to draw
     */
    public void drawBatteryBars(Canvas canvas)
    {
    	for (int count = 0; count < 12; count++)
        {
            canvas.drawPath(this.batteryPaths[count], activeBatteryBar);
            canvas.drawPath(
                    this.batteryPathsInner[count], 
                    activeBatteryBarInset
                );
        }
    }

    /**
     * Draw the power and reverse arcs.
     *
     * @param Canvas where to draw
     */
    public void drawThrottleArc(Canvas canvas)
    {
        canvas.drawPath(this.powerArcPath, powerArc);
        canvas.drawPath(this.reverseArcPath, reverseArc);
    }

    /**
     * Set the values of tilt in degrees
     *
     * @param radians The value in degrees
     */
    public void setTilt(float radians)
    {
        this.tilt = radians;
    }

    /**
     * Set the value of power as they are set on the model.
     *
     * @param throttle The value from 0-255
     */
    public void setThrottle(int throttle)
    {
        this.throttle = throttle;
    }

    public double getPowerGaugeBottomY()
    {
        return this.powerGaugeBottomY;
    }
    
    public double getPowerGaugeTopY()
    {
        return this.powerGaugeTopY;
    }

    public double getReverseGaugeBottomY()
    {
        return this.reverseGaugeBottomY;
    }
    
    public double getReverseGaugeTopY()
    {
        return this.reverseGaugeTopY;
    }
}
