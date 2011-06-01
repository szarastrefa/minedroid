package com.ryanm.minedroid.ui;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.ryanm.droid.rugl.util.FPSCamera;
import com.ryanm.droid.rugl.util.Trig;
import com.ryanm.droid.rugl.util.geom.Vector3f;
import com.ryanm.droid.rugl.util.math.LowPassFilter;
import com.ryanm.preflect.annote.Summary;
import com.ryanm.preflect.annote.Variable;

/**
 * Accelerometer/magnetometer based steering
 * 
 * @author ryanm
 */
@Variable( "Sensor steering" )
@Summary( "Pretend your phone is a window into minecraft" )
public class SensorSteering
{
	private boolean enabled = false;

	private float[] compassReadings;

	private float[] gravityReadings;

	private float[] rotMatrix = new float[16];

	private float[] orientation = new float[3];

	private float azimuth;

	private float roll;

	private SensorManager sm;

	private final LowPassFilter[] vectorFilters = new LowPassFilter[3];

	/***/
	@Variable( "Filter alpha" )
	@Summary( "Controls the smoothing/latency of the filter, in range 0-1" )
	public float filterAlpha = 0.1f;

	private SensorEventListener compass = new SensorEventListener(){

		@Override
		public void onSensorChanged( SensorEvent event )
		{
			compassReadings = event.values;
		}

		@Override
		public void onAccuracyChanged( Sensor sensor, int accuracy )
		{
			// not sure that I care
		}
	};

	private SensorEventListener gravity = new SensorEventListener(){

		@Override
		public void onSensorChanged( SensorEvent event )
		{
			gravityReadings = event.values;
		}

		@Override
		public void onAccuracyChanged( Sensor sensor, int accuracy )
		{
			// not sure that I care
		}
	};

	/**
	 * @param sm
	 */
	public SensorSteering( SensorManager sm )
	{
		this.sm = sm;

		for( int i = 0; i < vectorFilters.length; i++ )
			vectorFilters[ i ] = new LowPassFilter( 30 );
	}

	/**
	 * @param b
	 */
	@Variable( "Enabled" )
	public void setEnabled( boolean b )
	{
		enabled = b;

		if( enabled )
		{
			Sensor c = sm.getDefaultSensor( Sensor.TYPE_MAGNETIC_FIELD );
			enabled &=
					sm.registerListener( compass, c, SensorManager.SENSOR_DELAY_GAME );

			Sensor a = sm.getDefaultSensor( Sensor.TYPE_ACCELEROMETER );
			enabled &=
					sm.registerListener( gravity, a, SensorManager.SENSOR_DELAY_GAME );
		}
		else
		{
			sm.unregisterListener( compass );
			sm.unregisterListener( gravity );
		}
	}

	/**
	 * @return <code>true</code> if we are currently using sensor-based steering
	 */
	@Variable( "Enabled" )
	public boolean isEnabled()
	{
		return enabled;
	}

	/**
	 * @param cam
	 */
	public void advance( FPSCamera cam )
	{
		if( gravityReadings != null && compassReadings != null )
		{
			SensorManager.getRotationMatrix( rotMatrix, null, gravityReadings,
					compassReadings );
			SensorManager.getOrientation( rotMatrix, orientation );

			azimuth = -orientation[ 0 ];
			roll = -( orientation[ 2 ] - Trig.HALF_PI );

			cam.setHeading( azimuth );
			cam.setElevation( roll );
			cam.updateVectors();

			vectorFilters[ 0 ].addInput( cam.forward.x );
			vectorFilters[ 1 ].addInput( cam.forward.y );
			vectorFilters[ 2 ].addInput( cam.forward.z );

			cam.forward.set( vectorFilters[ 0 ].getOutput( filterAlpha ),
					vectorFilters[ 1 ].getOutput( filterAlpha ),
					vectorFilters[ 2 ].getOutput( filterAlpha ) );

			// right vector...
			cam.right.set( cam.forward.z, 0, -cam.forward.x );
			cam.right.normalise();

			// up is target x right
			Vector3f.cross( cam.forward, cam.right, cam.up );
		}
	}

	/**
	 * @param samples
	 */
	@Variable( "Filter length" )
	@Summary( "The number of samples held by the filters" )
	public void setFilterLength( int samples )
	{
		for( int i = 0; i < vectorFilters.length; i++ )
			vectorFilters[ i ] = new LowPassFilter( samples );
	}

	/**
	 * @return filter length
	 */
	@Variable( "Filter length" )
	public int getFilterLength()
	{
		return vectorFilters[ 0 ].getLength();
	}
}
