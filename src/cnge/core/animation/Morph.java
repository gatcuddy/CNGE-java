package cnge.core.animation;

import cnge.core.Base;
import cnge.graphics.Transform;

public class Morph {
	
	/*
	 * the morphers
	 */
	
	private static final Morpher doNothing = new Morpher() {
		public void morph(Transform t, Interpolator i, float start, float end, float along) {}
	};
	
	private static final Morpher doTransX = new Morpher() {
		public void morph(Transform t, Interpolator i, float start, float end, float along) {
			t.abcissa = i.interpolate(start, end, along);
		}
	};
	
	private static final Morpher doTransY = new Morpher() {
		public void morph(Transform t, Interpolator i, float start, float end, float along) {
			t.ordinate = i.interpolate(start, end, along);
		}
	};
	
	private static final Morpher doRotate = new Morpher() {
		public void morph(Transform t, Interpolator i, float start, float end, float along) {
			t.rotation = i.interpolate(start, end, along);
		}
	};
	
	private static final Morpher doScaleX = new Morpher() {
		public void morph(Transform t, Interpolator i, float start, float end, float along) {
			t.wScale = i.interpolate(start, end, along);
		}
	};
	
	private static final Morpher doScaleY = new Morpher() {
		public void morph(Transform t, Interpolator i, float start, float end, float along) {
			t.hScale = i.interpolate(start, end, along);
		}
	};
	
	/*
	 * the interpolators
	 */
	
	public static final Interpolator lINEAR = new Interpolator() {
		public float interpolate(float start, float end, float along) {
			return betweenValues(start, end, along);
		}
	};
	
	public static final Interpolator SQUARE = new Interpolator() {
		public float interpolate(float start, float end, float along) {
			return betweenValues(start, end, (float)Math.pow(along, 2));
		}
	};
	
	public static final Interpolator ROOT = new Interpolator() {
		public float interpolate(float start, float end, float along) {
			return betweenValues(start, end, (float)Math.sqrt(along));
		}
	};
	
	public static final Interpolator UNDERCIRCLE = new Interpolator() {
		public float interpolate(float start, float end, float along) {
			return betweenValues(start, end, 1 - (float)Math.sqrt(1 - Math.pow(along, 2)));
		}
	};
	
	public static final Interpolator OVERCIRCLE = new Interpolator() {
		public float interpolate(float start, float end, float along) {
			return betweenValues(start, end, (float)Math.sqrt(1 - Math.pow(along - 1, 2)));
		}
	};
	
	public static final Interpolator COSINE = new Interpolator() {
		public float interpolate(float start, float end, float along) {
			return betweenValues(start, end, (float)( (Math.cos(Math.PI * (along - 1)) + 1) / 2));
		}
	};
	
	private Transform modify;
	
	private double timer;
	private double time;
	
	private float xPos;
	private float yPos;
	private float rotation;
	private float wScale;
	private float hScale;
	
	private float oXPos;
	private float oYPos;
	private float oRotation;
	private float oWScale;
	private float oHScale;
	
	private Morpher translatorX;
	private Morpher translatorY;
	private Morpher rotator;
	private Morpher scalorX;
	private Morpher scalorY;
	
	private Interpolator interpolator;
	
	public static float betweenValues(float start, float end, float interpolated) {
		return ((end - start) * interpolated + start);
	}
	
	public Morph(Transform start, Interpolator i) {
		modify = start;
		interpolator = i;
		
		oXPos = modify.abcissa;
		oYPos = modify.ordinate;
		oRotation = modify.rotation;
		oWScale = modify.wScale;
		oHScale = modify.hScale;
		xPos = oXPos;
		yPos = oYPos;
		rotation = oRotation;
		wScale = oWScale;
		hScale = oHScale;
		
		translatorX = doNothing;
		translatorY = doNothing;
		rotator = doNothing;
		scalorX = doNothing;
		scalorY = doNothing;
	}
	
	public Morph addPositionX(float x) {
		xPos = x;
		translatorX = doTransX;
		return this;
	}
	
	public Morph addPositionY(float y) {
		yPos = y;
		translatorY = doTransY;
		return this;
	}
	
	public Morph addRotation(float r) {
		oRotation = r;
		rotator = doRotate;
		return this;
	}
	
	public Morph addScaleW(float w) {
		wScale = w;
		scalorX = doScaleX;
		return this;
	}
	
	public Morph addScaleH(float h) {
		hScale = h;
		scalorY = doScaleY;
		return this;
	}
	
	public void update(Transform t) {
		timer += Base.time;
		if(timer > time) {
			timer = time;
		}
		float along = (float)(timer / time);
		translatorX.morph(t, interpolator, oXPos, xPos, along);
		translatorY.morph(t, interpolator, oYPos, yPos, along);
		rotator.morph(t, interpolator, oRotation, rotation, along);
		scalorX.morph(t, interpolator, oWScale, wScale, along);
		scalorY.morph(t, interpolator, oHScale, hScale, along);
	}

	
	private interface Morpher {
		public void morph(Transform t, Interpolator i, float start, float end, float along);
	}
	
	private interface Interpolator {
		public float interpolate(float start, float end, float along);
	}
	
}