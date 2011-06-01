package com.ryanm.minedroid;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Entry point for application - searches for world files and also the user to
 * select one to launch
 * 
 * @author ryanm
 */
public class WorldChooserActivity extends ListActivity
{
	private static FileFilter dirFilter = new FileFilter(){
		@Override
		public boolean accept( final File pathname )
		{
			return pathname.isDirectory() && pathname.listFiles() != null;
		}
	};

	private File[] worlds = new File[0];

	@Override
	protected void onCreate( final Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		final SharedPreferences prefs = getPreferences( MODE_PRIVATE );

		final String worldString = prefs.getString( "worlds", "" );
		if( worldString.length() != 0 )
		{
			final String[] saved = worldString.split( "\n" );
			worlds = new File[saved.length];
			for( int i = 0; i < saved.length; i++ )
			{
				worlds[ i ] = new File( saved[ i ] );
			}
		}

		updateWorldList();
	}

	@Override
	protected void onListItemClick( final ListView l, final View v,
			final int position, final long id )
	{
		if( position == 0 )
		{
			// rescan
			new WorldFinder().execute();
		}
		else
		{
			final Intent i = new Intent( this, MineDroidActivity.class );
			i.putExtra( "world", worlds[ position - 1 ].getAbsolutePath() );

			startActivity( i );
		}
	}

	private void updateWorldList()
	{
		final List<String> worldList = new LinkedList<String>();
		final StringBuilder buff = new StringBuilder();

		worldList.add( "Scan for worlds" );
		for( final File f : worlds )
		{
			worldList.add( f.getName() );
			buff.append( f.getAbsolutePath() ).append( "\n" );
		}

		if( buff.length() > 0 )
		{
			buff.deleteCharAt( buff.length() - 1 );
		}

		final SharedPreferences.Editor pref =
				getPreferences( MODE_PRIVATE ).edit();
		pref.putString( "worlds", buff.toString() );
		pref.commit();

		setListAdapter( new ArrayAdapter<String>( this,
				android.R.layout.simple_list_item_1, worldList ) );
	}

	private class WorldFinder extends AsyncTask<Void, String, File[]>
	{
		private ProgressDialog dialog;

		private final List<File> found = new ArrayList<File>();

		@Override
		protected void onPreExecute()
		{
			dialog =
					ProgressDialog.show( WorldChooserActivity.this,
							"Searching for worlds", "" );
			dialog.setIndeterminate( true );
			dialog.setCancelable( true );
			dialog.show();
		}

		@Override
		protected File[] doInBackground( final Void... params )
		{
			final Stack<File> dirs = new Stack<File>();
			dirs.add( Environment.getExternalStorageDirectory() );

			while( !dirs.isEmpty() && !isCancelled() )
			{
				final File dir = dirs.pop();

				publishProgress( dir.getAbsolutePath() );

				if( isWorld( dir ) )
				{
					found.add( dir );
					publishProgress( "Found " + dir.getName() );
				}
				else
				{
					final File[] subDirs = dir.listFiles( dirFilter );
					Arrays.sort( subDirs );

					for( final File subDir : subDirs )
					{
						dirs.add( subDir );
					}
				}
			}

			return found.toArray( new File[found.size()] );
		}

		@Override
		protected void onProgressUpdate( final String... values )
		{
			if( values[ 0 ].startsWith( "Found " ) )
			{
				Toast.makeText( WorldChooserActivity.this, values[ 0 ],
						Toast.LENGTH_SHORT ).show();
				worlds = found.toArray( new File[found.size()] );
				updateWorldList();
			}
			else
			{
				dialog.setMessage( values[ 0 ] );
			}
		}

		@Override
		protected void onPostExecute( final File[] result )
		{
			dialog.hide();
		}
	}

	private static boolean isWorld( final File dir )
	{
		for( final File f : dir.listFiles() )
		{
			if( f.getName().equals( "level.dat" ) )
			{
				return true;
			}
		}
		return false;
	}
}
