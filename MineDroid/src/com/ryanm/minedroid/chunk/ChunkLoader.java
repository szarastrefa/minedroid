package com.ryanm.minedroid.chunk;

import java.io.DataInputStream;

import android.util.Log;

import com.ryanm.droid.rugl.Game;
import com.ryanm.droid.rugl.res.ResourceLoader.Loader;
import com.ryanm.minedroid.World;
import com.ryanm.minedroid.nbt.RegionFileCache;

/**
 * This is packaged up like this so it can happen on the resource loading
 * thread, rather than the main render thread
 * 
 * @author ryanm
 */
public abstract class ChunkLoader extends Loader<Chunk>
{
	private final World world;

	private final int x;

	private final int z;

	/**
	 * @param w
	 * @param x
	 * @param z
	 */
	public ChunkLoader( World w, int x, int z )
	{
		world = w;
		this.x = x;
		this.z = z;
	}

	@Override
	public void load()
	{

		try
		{
			DataInputStream is =
					RegionFileCache.getChunkDataInputStream( world.dir, x, z );

			resource = new Chunk( world, is );

			if( resource.chunkX != x || resource.chunkZ != z )
				Log.e( Game.RUGL_TAG, "expected " + toString() + ", got "
						+ resource );
		}
		catch( Exception e )
		{
			Log.e( Game.RUGL_TAG, "Problem loading chunk (" + x + "," + z + ")", e );

			exception = e;
			resource = null;
		}
	}

	@Override
	public String toString()
	{
		return "chunk " + x + ", " + z;
	}
}
