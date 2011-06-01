package com.ryanm.minedroid.ui;

import static android.opengl.GLES10.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES10.glClear;
import android.hardware.SensorManager;
import android.util.Log;

import com.ryanm.droid.rugl.Game;
import com.ryanm.droid.rugl.gl.GLUtil;
import com.ryanm.droid.rugl.gl.StackedRenderer;
import com.ryanm.droid.rugl.input.AbstractTouchStick.ClickListener;
import com.ryanm.droid.rugl.input.TapPad;
import com.ryanm.droid.rugl.input.Touch;
import com.ryanm.droid.rugl.input.Touch.Pointer;
import com.ryanm.droid.rugl.input.Touch.TouchListener;
import com.ryanm.droid.rugl.input.TouchStickArea;
import com.ryanm.droid.rugl.res.FontLoader;
import com.ryanm.droid.rugl.res.ResourceLoader;
import com.ryanm.droid.rugl.text.Font;
import com.ryanm.droid.rugl.text.TextShape;
import com.ryanm.droid.rugl.util.Colour;
import com.ryanm.droid.rugl.util.FPSCamera;
import com.ryanm.minedroid.Player;
import com.ryanm.minedroid.World;
import com.ryanm.preflect.annote.Category;
import com.ryanm.preflect.annote.Summary;
import com.ryanm.preflect.annote.Variable;

/**
 * Holds the touchsticks
 * 
 * @author ryanm
 */
@Variable( "Interface" )
@Summary( "GUI options" )
public class GUI
{
	private static final float radius = 50;

	private static final float size = 150;

	/***/
	@Variable( "Left stick" )
	@Summary( "Controls motion" )
	@Category( "Controls" )
	public final TouchStickArea left = new TouchStickArea( 0, 0, size, size,
			radius );

	/***/
	@Variable( "Right stick" )
	@Summary( "Controls view direction" )
	@Category( "Controls" )
	public final TouchStickArea right = new TouchStickArea( 800 - size, 0, size,
			size, radius );

	/***/
	@Variable( "Right tap pad" )
	@Summary( "Jump and crouch" )
	@Category( "Controls" )
	public final TapPad rightTap = new TapPad( 800 - size, right.pad.y.getMax(),
			size, size / 2 );

	/***/
	@Variable
	public final Hotbar hotbar;

	/***/
	@Variable
	public final Hand hand;

	/***/
	@Variable
	public final Interaction interaction;

	private final StackedRenderer r = new StackedRenderer();

	private TextShape notification;

	private float notifyTime = 0;

	private Font font;

	/***/
	@Variable
	public final SensorSteering sensorSteering;

	private final TouchListener[] widgets;

	private final TouchListener touchListener = new TouchListener(){
		@Override
		public void pointerRemoved( final Pointer p )
		{
			for( int i = 0; i < widgets.length; i++ )
				widgets[ i ].pointerRemoved( p );
		}

		@Override
		public boolean pointerAdded( final Pointer p )
		{
			boolean eaten = false;
			for( int i = 0; i < widgets.length && !eaten; i++ )
				eaten |= widgets[ i ].pointerAdded( p );

			return false;
		}

		@Override
		public void reset()
		{
			for( int i = 0; i < widgets.length; i++ )
				widgets[ i ].reset();
		}
	};

	/**
	 * @param player
	 * @param world
	 * @param camera
	 * @param sm
	 */
	public GUI( final Player player, final World world, final FPSCamera camera,
			final SensorManager sm )
	{
		hand = new Hand( player );
		interaction = new Interaction( player, world, camera, hand );
		hotbar = new Hotbar( player, interaction );
		sensorSteering = new SensorSteering( sm );

		rightTap.listener = player.jumpCrouchListener;

		widgets =
				new TouchListener[] { left, right, rightTap, hotbar, interaction };

		Touch.addListener( touchListener );

		final ClickListener strikey = new ClickListener(){
			@Override
			public void onClick()
			{
				interaction.action( player.inHand, 400, 240 );
			}

			@Override
			public void onClickHold( final boolean active )
			{
				interaction.touchSticksHeld = active;
			}
		};

		right.listener = strikey;
		left.listener = strikey;

		ResourceLoader.load( new FontLoader( com.ryanm.droid.rugl.R.raw.font,
				false ){
			@Override
			public void fontLoaded()
			{
				font = resource;
			}
		} );
	}

	/**
	 * @param delta
	 *           time delta
	 * @param cam
	 *           to apply steering to
	 */
	public void advance( final float delta, final FPSCamera cam )
	{
		left.advance();

		if( !sensorSteering.isEnabled() )
			right.advance();

		rightTap.advance();

		hotbar.advance( delta );
		hand.advance( delta );

		interaction.advance( delta );

		notifyTime -= delta;
		if( notifyTime < 0 )
			notification = null;

		// steering
		if( sensorSteering.isEnabled() )
			sensorSteering.advance( cam );
		else
			cam.advance( delta, right.x, right.y );
	}

	/**
	 * Note that the projection matrix will be changed and the depth buffer
	 * cleared in here
	 */
	public void draw()
	{
		GLUtil.scaledOrtho( 800, 480, Game.screenWidth, Game.screenHeight, -1, 1 );
		glClear( GL_DEPTH_BUFFER_BIT );

		hand.draw( r );

		r.render();

		left.draw( r );

		if( !sensorSteering.isEnabled() )
			right.draw( r );

		rightTap.draw( r );

		hotbar.draw( r );

		if( notification != null )
			notification.render( r );

		r.render();
	}

	/**
	 * @param string
	 */
	public void notify( final String string )
	{
		Log.i( Game.RUGL_TAG, "Notification: " + string );
		if( font != null )
		{
			notification = font.buildTextShape( string, Colour.black );
			notification.translate(
					( 800 - notification.getBounds().x.getSpan() ) / 2, 100, 0 );
			notifyTime = 1.5f;
		}
	}
}
