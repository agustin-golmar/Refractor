package ar.nadezhda.refractor.handler.feature;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import ar.nadezhda.refractor.support.Matrix;
import ar.nadezhda.refractor.support.Timer;
import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.shape.Rectangle;

public class LevelSetHandler implements Handler {

	protected final Set<Point> lin = new HashSet<>();
	protected final Set<Point> lout = new HashSet<>();
	protected final Set<Point> mfr = new HashSet<>();
	protected final Set<Point> mfa = new HashSet<>();
	protected final Map<Point, Double> slin = new HashMap<>();
	protected final Map<Point, Double> slout = new HashMap<>();
	protected final double [] θback = {0.0, 0.0, 0.0};
	protected final double [] θobj = {0.0, 0.0, 0.0};
	protected final double [][] G = Matrix.gaussian(5, 1.0);

	protected int [][] φ;
	protected boolean isDynamic = false;
	protected boolean isFiltered = false;

	@Override
	public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
		final var result = new HashMap<String, Image>();
		isDynamic = ((CheckBox) Main.namespace.get("isDynamic")).isSelected();
		isFiltered = ((CheckBox) Main.namespace.get("isFiltered")).isSelected();
		states.stream()
			.forEachOrdered(state -> {
				final var image = state.getRGBImage();
				// ------------------------------------------------------------
				final var timer = Timer.start();
				lin.clear();
				lout.clear();
				mfr.clear();
				mfa.clear();
				loadφ(image.data, state.getArea());
				loadθ(image.data);
				cycleOne(image.data);
				System.out.println("\n\tLevelSet cycled in: " + timer.getTimeInSeconds() + " sec.");
				// ------------------------------------------------------------
				final var map = new Image(image.getSource(), Matrix.colorFeatures(getContours(), image.data));
				final var key = ImageTool.buildKey("level-set", map, state.getKey());
				result.put(key, map);
			});
		return result;
	}

	protected double [][][] getContours() {
		final var contours = new double [1][φ.length][φ[0].length];
		lin.stream().forEach(p -> contours[0][p.x][p.y] = Matrix.CONTOUR);
		lout.stream().forEach(p -> contours[0][p.x][p.y] = Matrix.OUTTER_CONTOUR);
		return contours;
	}

	protected boolean stoppingCriterion(final double [][][] space) {
		return lout.stream().allMatch(p -> getSpeed(space, p) <= 0.0)
			&& lin.stream().allMatch(p -> 0.0 <= getSpeed(space, p));
	}

	protected void cycleTwo(final double [][][] space) {
		for (int i = 0; i < G.length; ++i) {
			lout.stream()
				.filter(p -> Matrix.convolution(φ, G, p) < 0)
				.forEach(this::switchIn);
			update(lout);
			lin.stream()
				.forEach(this::drainLin);
			update(lin);
			lin.stream()
				.filter(p -> 0 < Matrix.convolution(φ, G, p))
				.forEach(this::switchOut);
			update(lin);
			lout.stream()
				.forEach(this::drainLout);
			update(lout);
		}
		System.out.println("CycleTwo(lin, lout) = (" + lin.size() + ", " + lout.size() + ")");
	}

	protected void cycleOne(final double [][][] space) {
		final var Na = Math.max(space[0].length, space[0][0].length);
		boolean cycleOneFinish = false;
		while (!cycleOneFinish) {
			slin.clear();
			slout.clear();
			if (isDynamic) loadθ(space);
			for (int i = 0; i < Na; ++i) {
				lout.stream()
					.forEach(p -> slout.put(p, getSpeed(space, p)));
				lin.stream()
					.forEach(p -> slin.put(p, getSpeed(space, p)));
				lout.stream()
					.filter(p -> 0.0 < slout.get(p))
					.forEach(this::switchIn);
				update(lout);
				lin.stream()
					.forEach(this::drainLin);
				update(lin);
				lin.stream()
					.filter(slin::containsKey)
					.filter(p -> slin.get(p) < 0.0)
					.forEach(this::switchOut);
				update(lin);
				lout.stream()
					.forEach(this::drainLout);
				update(lout);
				if (isDynamic) loadθ(space);
				if (stoppingCriterion(space)) {
					System.out.println("CycleOne(lin, lout) = (" + lin.size() + ", " + lout.size() + ")");
					System.out.println("Stopping Criterion over CycleOne.");
					cycleOneFinish = true;
					break;
				}
			}
			if (isFiltered) {
				System.out.println("CycleOne(lin, lout) = (" + lin.size() + ", " + lout.size() + ")");
				cycleTwo(space);
			}
		}
	}

	/**
	* <p>Se debe ejecutar cada vez que se aplica <b>switchIn/Out</b> o
	* <b>drainLin/Lout</b>.</p>
	*
	* @param points
	*	El conjunto <b>lin</b> o <b>lout</b> a actualizar.
	*/
	protected void update(final Set<Point> points) {
		points.removeAll(mfr);
		points.addAll(mfa);
		mfr.clear();
		mfa.clear();
	}

	protected double distance(final double [] u, final double [] v) {
		final double Δr = u[0] - v[0];
		final double Δg = u[1] - v[1];
		final double Δb = u[2] - v[2];
		return Math.sqrt(Δr*Δr + Δg*Δg + Δb*Δb);
	}

	protected double getSpeed(final double [][][] space, final Point x) {
		final double [] rgb = {
				space[0][x.x][x.y],
				space[1][x.x][x.y],
				space[2][x.x][x.y]
		};
		return Math.log(distance(θback, rgb)/distance(θobj, rgb));
	}

	protected void drainLin(final Point x) {
		final var up = new Point(x.x, x.y - 1);
		final var down = new Point(x.x, x.y + 1);
		final var left = new Point(x.x - 1, x.y);
		final var right = new Point(x.x + 1, x.y);
		// Usar 8-neighbours?
		if (0 <= up.y && φ[up.x][up.y] < 0
			&& down.y < φ[0].length && φ[down.x][down.y] < 0
			&& 0 <= left.x && φ[left.x][left.y] < 0
			&& right.x < φ.length && φ[right.x][right.y] < 0) {
			mfr.add(x);
			φ[x.x][x.y] = -3;
		}
	}

	protected void drainLout(final Point x) {
		final var up = new Point(x.x, x.y - 1);
		final var down = new Point(x.x, x.y + 1);
		final var left = new Point(x.x - 1, x.y);
		final var right = new Point(x.x + 1, x.y);
		// Usar 8-neighbours?
		if (0 <= up.y && φ[up.x][up.y] > 0
			&& down.y < φ[0].length && φ[down.x][down.y] > 0
			&& 0 <= left.x && φ[left.x][left.y] > 0
			&& right.x < φ.length && φ[right.x][right.y] > 0) {
			mfr.add(x);
			φ[x.x][x.y] = 3;
		}
	}

	protected void switchIn(final Point x) {
		mfr.add(x);
		lin.add(x);
		φ[x.x][x.y] = -1;
		final var up = new Point(x.x, x.y - 1);
		final var down = new Point(x.x, x.y + 1);
		final var left = new Point(x.x - 1, x.y);
		final var right = new Point(x.x + 1, x.y);
		if (0 <= up.y && φ[up.x][up.y] == 3) {
			mfa.add(up);
			φ[up.x][up.y] = 1;
		}
		if (down.y < φ[0].length && φ[down.x][down.y] == 3) {
			mfa.add(down);
			φ[down.x][down.y] = 1;
		}
		if (0 <= left.x && φ[left.x][left.y] == 3) {
			mfa.add(left);
			φ[left.x][left.y] = 1;
		}
		if (right.x < φ.length && φ[right.x][right.y] == 3) {
			mfa.add(right);
			φ[right.x][right.y] = 1;
		}
	}

	protected void switchOut(final Point x) {
		mfr.add(x);
		lout.add(x);
		φ[x.x][x.y] = 1;
		final var up = new Point(x.x, x.y - 1);
		final var down = new Point(x.x, x.y + 1);
		final var left = new Point(x.x - 1, x.y);
		final var right = new Point(x.x + 1, x.y);
		if (0 <= up.y && φ[up.x][up.y] == -3) {
			mfa.add(up);
			φ[up.x][up.y] = -1;
		}
		if (down.y < φ[0].length && φ[down.x][down.y] == -3) {
			mfa.add(down);
			φ[down.x][down.y] = -1;
		}
		if (0 <= left.x && φ[left.x][left.y] == -3) {
			mfa.add(left);
			φ[left.x][left.y] = -1;
		}
		if (right.x < φ.length && φ[right.x][right.y] == -3) {
			mfa.add(right);
			φ[right.x][right.y] = -1;
		}
	}

	protected void loadθ(final double [][][] space) {
		θback[0] = θback[1] = θback[2] = 0;
		θobj[0] = θobj[1] = θobj[2] = 0;
		int backSize = 0;
		int objSize = 0;
		for (int w = 0; w < space[0].length; ++w)
			for (int h = 0; h < space[0][0].length; ++h) {
				switch (φ[w][h]) {
					case -3:
					case -1: {
						++objSize;
						θobj[0] += space[0][w][h];
						θobj[1] += space[1][w][h];
						θobj[2] += space[2][w][h];
						break;
					}
					case 3:
					case 1: {
						++backSize;
						θback[0] += space[0][w][h];
						θback[1] += space[1][w][h];
						θback[2] += space[2][w][h];
						break;
					}
				}
			}
		if (0 < backSize) {
			θback[0] /= backSize;
			θback[1] /= backSize;
			θback[2] /= backSize;
		}
		else θback[0] = θback[1] = θback[2] = 0;
		if (0 < objSize) {
			θobj[0] /= objSize;
			θobj[1] /= objSize;
			θobj[2] /= objSize;
		}
		else θobj[0] = θobj[1] = θobj[2] = 0;
		System.out.println("\n(θback) = (" + θback[0] + ", " + θback[1] + ", " + θback[2] + ")");
		System.out.println("(θobj) = (" + θobj[0] + ", " + θobj[1] + ", " + θobj[2] + ")");
	}

	protected void loadφ(final double [][][] space, final Rectangle target) {
		/*
		 * (X, Y) --------------------o
		 * |                          | El área va de (X, Y) a (X + W, Y + H), inclusive.
		 * |      o h                 |
		 * |      w                   |
		 * o-------------(X + W, Y + H)
		 */
		final var X = (int) target.getX();
		final var Y = (int) target.getY();
		final var W = (int) target.getWidth();
		final var H = (int) target.getHeight();
		φ = Matrix.flatFilterToInt(space, (s, c, w, h) -> {
			if (X < w && w < X + W && Y < h && h < Y + H) {
				// Interior
				return -3;
			}
			else if (X <= w && w <= X + W && Y <= h && h <= Y + H) {
				// Sobre 'lin'
				lin.add(new Point(w, h));
				return -1;
			}
			else if (X - 1 <= w && w <= X + W + 1 && Y - 1 <= h && h <= Y + H + 1) {
				// Sobre 'lout'
				lout.add(new Point(w, h));
				return 1;
			}
			else {
				// Exterior
				return 3;
			}
		});
	}

	@Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
