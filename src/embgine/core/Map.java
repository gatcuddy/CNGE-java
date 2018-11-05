package embgine.core;

import embgine.core.group.EntityGroup;

public class Map extends Entity{
	
	public static final int NO_BLOCK = -1;
	
	private Block[] set;
	private int[][] tiles;
	private int width;
	private int height;
	
	public Map(EntityGroup g, int np, float x, float y, int l, float s, int[][] t, Block[] b) {
		super(g, np, x, y, l);
		tiles = t;
		width = t.length;
		height = t[0].length;
		set = b;
		transform.setSize(width * s, height * s);
	}
	
	public int[][] getTies() {
		return tiles;
	}
	
	public int get(int x, int y) {
		return tiles[x][y];
	}
	
	public Block[] getSet() {
		return set;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	/**
	 * gets the map coordinate correspoding to a world coordinate
	 * 
	 * @param x - x pposition in the world
	 * 
	 * @return the integer x position in map coordinates 
	 */
	public int atX(float x) {
		return (int)((x-transform.abcissa) * width / transform.width);
	}
	
	/**
	 * gets the map coordinate correspoding to a world coordinate
	 * 
	 * @param y - y pposition in the world
	 * 
	 * @return the integer y position in map coordinates 
	 */
	public int atY(float y) {
		return (int)((y-transform.ordinate) * height / transform.height);
	}
	
	/**
	 * accesses the map only within its bounds
	 * 
	 * @param x - map coordinate
	 * @param y - map coordinate
	 * 
	 * @return the block there
	 * @throws MapAccessException
	 * 
	 * @see atX()
	 * @see atY()
	 */
	public int boundedAccess(int x, int y) throws MapAccessException {
		try {
			return tiles[x][y];
		} catch(ArrayIndexOutOfBoundsException ex) {
			throw new MapAccessException();
		}
	}
	
	/**
	 * accesses the map but each edge repeats
	 * 
	 * @param x - map coordinate
	 * @param y - map coordinate
	 * 
	 * @return the block there
	 * 
	 * @see atX()
	 * @see atY()
	 */
	public int edgeAccess(int x, int y) {
		try {
			return tiles[x][y];
		} catch(ArrayIndexOutOfBoundsException ex) {
			if(x < 0) {
				x = 0;
			} else if(x >= width) {
				x = width - 1;
			}
			if(y < 0) {
				y = 0;
			} else if(y >= height) {
				y = height - 1;
			}
			return tiles[x][y];
		}
	}
	
	/**
	 * accesses the map but it extends horizontally in both directions
	 * 
	 * @param x - map coordinate
	 * @param y - map coordinate
	 * 
	 * @return the block there
	 * @throws MapAccessException
	 * 
	 * @see atX()
	 * @see atY()
	 */
	public int horzEdgeAccess(int x, int y) throws MapAccessException {
		try {
			return tiles[x][y];
		} catch(ArrayIndexOutOfBoundsException ex) {
			if(x < 0) {
				x = 0;
			} else if(x >= width) {
				x = width - 1;
			}
			try {
				return tiles[x][y];
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new MapAccessException();
			}
		}
	}
	
	/**
	 * accesses the map but it extends vertically in both directions
	 * 
	 * @param x - map coordinate
	 * @param y - map coordinate
	 * 
	 * @return the block there
	 * @throws MapAccessException
	 * 
	 * @see atX()
	 * @see atY()
	 */
	public int vertEdgeAccess(int x, int y) throws MapAccessException {
		try {
			return tiles[x][y];
		} catch(ArrayIndexOutOfBoundsException ex) {
			if(y < 0) {
				y = 0;
			} else if(y >= height) {
				y = height - 1;
			}
			try {
				return tiles[x][y];
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new MapAccessException();
			}
		}
	}
	
	/**
	 * accesses the map but it's cloned horizontally
	 * 
	 * @param x - map coordinate
	 * @param y - map coordinate
	 * 
	 * @return the block there
	 * @throws MapAccessException
	 * 
	 * @see atX()
	 * @see atY()
	 */
	public int repeatHorzAccess(int x, int y) throws MapAccessException {
		try {
			return tiles[x][y];
		} catch(ArrayIndexOutOfBoundsException ex) {
			x %= width;
			try {
				return tiles[x][y];
			} catch(ArrayIndexOutOfBoundsException e) {
				throw new MapAccessException();
			}
		}
	}
	
	/**
	 * accesses the map but it's cloned everywhere
	 * 
	 * @param x - map coordinate
	 * @param y - map coordinate
	 * 
	 * @return the block there
	 * 
	 * @see atX()
	 * @see atY()
	 */
	public int repeatAllAccess(int x, int y) {
		try {
			return tiles[x][y];
		} catch(ArrayIndexOutOfBoundsException ex) {
			x %= width;
			y %= height;
			return tiles[x][y];
		}
	}
	
	/**
	 * will be thrown when you try and access a map and it's outside the map bounds
	 * 
	 * @author Emmet
	 */
	private class MapAccessException extends Exception {
		private static final long serialVersionUID = 9197260479519042104L;
	}
	
}