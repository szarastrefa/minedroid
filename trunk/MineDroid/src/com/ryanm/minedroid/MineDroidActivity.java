package com.ryanm.minedroid;

import java.io.File;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.ryanm.droid.rugl.Game;
import com.ryanm.droid.rugl.GameActivity;
import com.ryanm.droid.rugl.gl.GLVersion;
import com.ryanm.droid.rugl.res.ResourceLoader;
import com.ryanm.droid.rugl.util.ExceptionHandler;
import com.ryanm.droid.rugl.util.geom.Vector3f;
import com.ryanm.minedroid.nbt.Tag;
import com.ryanm.minedroid.nbt.TagLoader;

/**
 * Entry point for application. Not much happens here, look to {@link BlockView}
 * for actual behaviour
 * 
 * @author ryanm
 */
public class MineDroidActivity extends GameActivity
{
	private ProgressDialog loadDialog;

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		final String worldFileName = getIntent().getExtras().getString( "world" );

		Log.e( Game.RUGL_TAG, "loading " + worldFileName );

		final File dir = new File( worldFileName );

		final ProgressDialog pd =
				ProgressDialog.show( this, "", "Loading level.dat", true, true,
						new DialogInterface.OnCancelListener(){
							@Override
							public void onCancel( final DialogInterface dialog )
							{
								MineDroidActivity.this.finish();
							}
						} );
		loadDialog = pd;

		// It's verboten to do IO on the main event thread, so let's
		// load level.dat using the resourceloader
		final TagLoader tl = new TagLoader( new File( dir, "level.dat" ) ){
			@Override
			public void complete()
			{
				// we're currently in the resourceLoader's processing
				// thread, get back onto the gui thread
				MineDroidActivity.this.runOnUiThread( new Runnable(){
					@Override
					public void run()
					{
						if( resource == null )
						{
							showToast( "Could not load world level.dat\n"
									+ exception.getClass().getSimpleName() + ":"
									+ exception.getMessage(), true );

							ExceptionHandler.handle( exception );

							finish();
						}
						else
						{
							try
							{
								final Tag player = resource.findTagByName( "Player" );
								final Tag pos = player.findTagByName( "Pos" );

								final Tag[] tl =
										( com.ryanm.minedroid.nbt.Tag[] ) pos.getValue();
								final Vector3f p = new Vector3f();
								p.x = ( ( Double ) tl[ 0 ].getValue() ).floatValue();
								p.y = ( ( Double ) tl[ 1 ].getValue() ).floatValue();
								p.z = ( ( Double ) tl[ 2 ].getValue() ).floatValue();

								final World w = new World( dir, p );

								final Game game =
										new Game( MineDroidActivity.this,
												GLVersion.OnePointZero, new BlockView( w ) );

								pd.dismiss();

								start( game, "therealryan+minedroid@gmail.com" );
							}
							catch( final Exception e )
							{
								showToast(
										"Problem parsing level.dat - Maybe a corrupt file?",
										true );
								Log.e( Game.RUGL_TAG, "Level.dat corrupted?", e );

								finish();
							}
						}
					}
				} );
			}
		};

		tl.selfCompleting = true;

		ResourceLoader.load( tl );
	}

	@Override
	protected void onPause()
	{
		super.onPause();

		if( loadDialog != null )
		{
			loadDialog.dismiss();
		}
	}
}
