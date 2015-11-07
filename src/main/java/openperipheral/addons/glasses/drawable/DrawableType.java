package openperipheral.addons.glasses.drawable;

public enum DrawableType {
	GRADIENT {
		@Override
		public Drawable create() {
			return new GradientBox();
		}
	},
	BOX {
		@Override
		public Drawable create() {
			return new SolidBox();
		}
	},
	TEXT {
		@Override
		public Drawable create() {
			return new Text();
		}
	},
	LIQUID {
		@Override
		public Drawable create() {
			return new LiquidIcon();
		}
	},
	ITEM {
		@Override
		public Drawable create() {
			return new ItemIcon();
		}
	},
	POINT {
		@Override
		public Drawable create() {
			return new Point();
		}
	},
	LINE {
		@Override
		public Drawable create() {
			return new SolidLine();
		}
	},
	GRADIENT_LINE {
		@Override
		public Drawable create() {
			return new GradientLine();
		}
	},
	LINE_STRIP {
		@Override
		public Drawable create() {
			return new SolidLineStrip();
		}
	},
	GRADIENT_LINE_STRIP {
		@Override
		public Drawable create() {
			return new GradientLineStrip();
		}
	},
	TRIANGLE {
		@Override
		public Drawable create() {
			return new SolidTriangle();
		}
	},
	GRADIENT_TRIANGLE {
		@Override
		public Drawable create() {
			return new GradientTriangle();
		}
	},
	QUAD {
		@Override
		public Drawable create() {
			return new SolidQuad();
		}
	},
	GRADIENT_QUAD {
		@Override
		public Drawable create() {
			return new GradientQuad();
		}
	},
	POLYGON {
		@Override
		public Drawable create() {
			return new SolidPolygon();
		}
	},
	GRADIENT_POLYGON {
		@Override
		public Drawable create() {
			return new GradientPolygon();
		}
	};

	public abstract Drawable create();

	public static final DrawableType[] TYPES = values();
}