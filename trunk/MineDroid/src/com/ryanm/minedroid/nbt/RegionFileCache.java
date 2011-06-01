package com.ryanm.minedroid.nbt;

/*
 * * 2011 January 5** The author disclaims copyright to this source code. In
 * place of* a legal notice, here is a blessing:** May you do good and not
 * evil.* May you find forgiveness for yourself and forgive others.* May you
 * share freely, never taking more than you give.
 */

/*
 * 2011 February 16 This source code is based on the work of Scaevolus (see
 * notice above). It has been slightly modified by Mojang AB to limit the
 * maximum cache size (relevant to extremely big worlds on Linux systems with
 * limited number of file handles). The region files are postfixed with ".mcr"
 * (Minecraft region file) instead of ".data" to differentiate from the original
 * McRegion files.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple cache and wrapper for efficiently multiple RegionFiles
 * simultaneously.
 * 
 * @author ryanm
 */
public class RegionFileCache
{

	private static final int MAX_CACHE_SIZE = 256;

	private static final Map<File, Reference<RegionFile>> cache =
			new HashMap<File, Reference<RegionFile>>();

	private RegionFileCache()
	{
	}

	/**
	 * @param basePath
	 *           The world directory
	 * @param chunkX
	 * @param chunkZ
	 * @return The region file that contains the specified chunk
	 */
	public static synchronized RegionFile getRegionFile( File basePath,
			int chunkX, int chunkZ )
	{
		File regionDir = new File( basePath, "region" );
		File file =
				new File( regionDir, "r." + ( chunkX >> 5 ) + "." + ( chunkZ >> 5 )
						+ ".mcr" );

		Reference<RegionFile> ref = cache.get( file );

		if( ref != null && ref.get() != null )
			return ref.get();

		if( !regionDir.exists() )
			regionDir.mkdirs();

		if( cache.size() >= MAX_CACHE_SIZE )
			RegionFileCache.clear();

		RegionFile reg = new RegionFile( file );
		cache.put( file, new SoftReference<RegionFile>( reg ) );
		return reg;
	}

	/**
	 * Clears the cache
	 */
	public static synchronized void clear()
	{
		for( Reference<RegionFile> ref : cache.values() )
			try
			{
				if( ref.get() != null )
					ref.get().close();
			}
			catch( IOException e )
			{
				e.printStackTrace();
			}
		cache.clear();
	}

	/**
	 * @param basePath
	 *           The world directory
	 * @param chunkX
	 * @param chunkZ
	 * @return how much the region file that contains the specified chunk has
	 *         grown since it was last checked
	 */
	public static int getSizeDelta( File basePath, int chunkX, int chunkZ )
	{
		RegionFile r = getRegionFile( basePath, chunkX, chunkZ );
		return r.getSizeDelta();
	}

	/**
	 * @param basePath
	 *           The world directory
	 * @param chunkX
	 * @param chunkZ
	 * @return an uncompressed input stream for the specified chunk data
	 */
	public static DataInputStream getChunkDataInputStream( File basePath,
			int chunkX, int chunkZ )
	{
		RegionFile r = getRegionFile( basePath, chunkX, chunkZ );
		return r.getChunkDataInputStream( chunkX & 31, chunkZ & 31 );
	}

	/**
	 * @param basePath
	 *           The world directory
	 * @param chunkX
	 * @param chunkZ
	 * @return an outputs stream for saving the specified chunk's data
	 */
	public static DataOutputStream getChunkDataOutputStream( File basePath,
			int chunkX, int chunkZ )
	{
		RegionFile r = getRegionFile( basePath, chunkX, chunkZ );
		return r.getChunkDataOutputStream( chunkX & 31, chunkZ & 31 );
	}
}
