package cnge.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Array;

import javax.imageio.ImageIO;

import cnge.core.Map.MapAccessException;
import cnge.graphics.Camera;
import cnge.graphics.Transform;

public abstract class MapGroup {

	private int sections;
	
	protected int[][][] tiles;
	protected int[] widths;
	protected int[] heights;
	
	private String[] mapImages;
	
	protected Block[] blockSet;
	
	/**
	 * constructs a new mapgroup for the scene
	 * 
	 * @param ip - the paths to the map loader images
	 * @param bs - the blockset for the maps
	 */
	public MapGroup(String[] ip, Block[] bs) {
		sections = ip.length;
		mapImages = ip;
		blockSet = bs;
	}
	
	/**
	 * constructs a new mapgroup for the scene with only 1 section
	 * 
	 * @param ip - the paths to the map loader images
	 * @param bs - the blockset for the maps
	 */
	public MapGroup(String ip, Block[] bs) {
		sections = 1;
		mapImages = new String[] {ip};
		blockSet = bs;
	}

	/**
	 * use this instead of the {@link EntityGroup} one
	 * 
	 * creates all the map sections into indiviudal maps
	 * 
	 * @param x - the x position of the map
	 * @param y - the y position of the map
	 * @param l - the layer the map is on
	 * 
	 * @return returns the maps, NULL if could not create.
	 * NOTE: all maps must be successfully created to be returned
	 */
	public Map[] createMaps(float[] x, float[] y, Object... params) {
		Map[] creates = new Map[sections];//(M[]) Array.newInstance(mClass, sections);
		for(int i = 0; i < sections; ++i) {
			int w = widths[i];
			int h = heights[i];
			int[][] take = tiles[i];
			int[][] give = new int[w][h];
			/*
			 * we make a clone array that we give to the map instance
			 */
			for(int j = 0; j < w; ++j) {
				for(int k = 0; k < h; ++k) {
					give[j][k] = take[j][k];
				}
			}
			creates[i] = mapCreate(i, params);
			creates[i].mapSetup(x[i], y[i], 0, blockSet, give);
		}
		return creates;
	}
	
	/**
	 * use this instead of the {@link EntityGroup} one
	 * 
	 * creates an instance of the single map section belonging to this group
	 * 
	 * @param x - the x position of the map
	 * @param y - the y position of the map
	 * @param l - the layer the map is on
	 * 
	 * @return returns the map, NULL if could not create
	 */
	public Map createMap(float x, float y, Object... params) {
		int w = widths[0];
		int h = heights[0];
		int[][] take = tiles[0];
		int[][] give = new int[w][h];
		/*
		 * we make a clone array that we give to the map instance
		 */
		for(int j = 0; j < w; ++j) {
			for(int k = 0; k < h; ++k) {
				give[j][k] = take[j][k];
			}
		}
		Map create = mapCreate(0, params);
		create.mapSetup(x, y, 0, blockSet, give);
		return create;
	}
	
	public Block[] getBlockSet() {
		return blockSet;
	}
	
	abstract public Map mapCreate(int i, Object... params);
	
	/**
	 * overrides render from entity group 
	 * 
	 * @param m - the map to render
	 * @param layer - the current render layer
	 */
	public void render(Map m, float layer) {
		/*
		 * this transform is the size of a block
		 */
		Transform t = m.getTransform();
		float wide = t.getWidth() / m.getWidth();
		float tall = t.getHeight() / m.getHeight();
		
		Transform blockTransform = new Transform(wide, tall);
		
		int u = m.getUp();
		int r = m.getRight();
		int d = m.getDown();
		int l = m.getLeft();
		
		for(int x = l; x < r; ++x) {
			for(int y = u; y < d; ++y) {
				try {
					int tile = m.access(x, y);
					if(tile != -1) {
						blockTransform.setTranslation(x * wide + t.abcissa, y * tall + t.ordinate);
						if(blockSet[tile].layer == layer) {
							m.mapRender(tile, x, y, blockTransform);
						}
					}
				} catch (MapAccessException ex) { }
			}
		}
		
		m.render();
	}

	/**
	 * when the map needs to actually be used, call this one. Loads in the map,
	 * 
	 * how much time will it take? Who knows
	 */
	public void load() {
		tiles = new int[sections][][];
		widths = new int[sections];
		heights = new int[sections];
		
		Thread[] tList = new Thread[sections];
		for(int i = 0; i < sections; ++i) {
			(tList[i] = new Thread(new LoaderThread(i))).run();
		}
		
		for(int i = 0; i < sections; ++i) {
			try {
				tList[i].join();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		
		mapSpawn();
	}
	
	abstract public void mapSpawn();
	
	private class LoaderThread implements Runnable {
		int number;
		
		public LoaderThread(int n) {
			number = n;
		}
		
		public void run() {
			
			BufferedImage b = null;
			try {
				b = ImageIO.read(new File(mapImages[number]));
			}catch(Exception ex) {
				ex.printStackTrace();
				System.exit(-1);
			}
			
			int width = b.getWidth();
			int height = b.getHeight(); 
			
			int[] data = new int[width * height];
			b.getRGB(0, 0, width, height, data, 0, width);
			
			tiles[number] = new int[width][height];

			//first fill out with -1 (Spooky)
			for(int j = 0; j < width; ++j) {
				for(int k = 0; k < height; ++k) {
					tiles[number][j][k] = -1;
				}
			}
			
			int bs = blockSet.length;
			Thread[] tList = new Thread[bs];
			for(int i = 0; i < bs; ++i) {
				(tList[i] = new Thread(new PlacerThread(i, width, height, data, tiles[number]))).run();
			}
			
			for(int i = 0; i < bs; ++i) {
				try {
					tList[i].join();
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
			}
			
			widths[number] = width;
			heights[number] = height;
		}
	}
	
	private class PlacerThread implements Runnable {
		int block;
		
		int width;
		int height;
		int[] data;
		int[][] array;
		
		public PlacerThread(int b, int w, int h, int[] d, int[][] a) {
			block = b;
			
			width = w;
			height = h;
			data = d;
			array = a;
		}
		
		public void run() {
			int color = blockSet[block].colorCode;
			for(int j = 0; j < height; ++j) {
				for(int i = 0; i < width; ++i) {
					if(data[j * width + i] == color) {
						array[i][j] = block;
					}
				}
			}
		}
	}
	
}