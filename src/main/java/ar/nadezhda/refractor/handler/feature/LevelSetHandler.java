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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.shape.Rectangle;

	/**
	* <p>Implementación del algoritmo <i>Level Sets</i>, para tracking de
	* objetos en tiempo real.</p>
	*
	* @see <b>"Real-time Tracking Using Level Sets"</b>
	*	(Yonggang Shi, W. Clem Karl, <i>2005</i>).
	* @see <b>"Seguimiento en Video Utilizando Conjuntos de Nivel"</b>
	*	(Damián Rozitchner, <i>2006</i>).
	* @see <b>"A Topology Preserving Level Set Method For Geometric Deformable Models"</b>
	*	(Xiao Han, Chenyang Xu, Jerry L. Prince, <i>2002</i>).
	*/

public class LevelSetHandler implements Handler {

	// Conjuntos de curvas:
	protected List<Set<Point>> lin = new ArrayList<>();
	protected List<Set<Point>> lout = new ArrayList<>();

	// Conjunto de puntos agregados/removidos:
	protected final List<Set<Point>> mfr = new ArrayList<>();
	protected final List<Set<Point>> mfa = new ArrayList<>();

	// Mapa de velocidades de evolución:
	protected final Map<Point, Double> slin = new HashMap<>();
	protected final Map<Point, Double> slout = new HashMap<>();

	// Parámetros característicos de cada región:
	protected final double [] θback = {0.0, 0.0, 0.0};
	protected final double [] θobj = {0.0, 0.0, 0.0};

	// Filtro de suavizado de curvas:
	protected final double [][] G = Matrix.gaussian(5, 1.0);

	// Mapa de niveles y regiones:
	protected int [][] φ;
	protected int [][] ψ;

	/**
	* <p>Si es dinámico, los parámetros característicos se actualizan en cada
	* frame.</p>
	*/
	protected boolean isDynamic = false;

	/**
	* <p>Si es filtrado, se utiliza la convolución puntual gaussiana para
	* suavizar las curvas, lo que se corresponde con el segundo ciclo del algoritmo.</p>
	*/
	protected boolean isFiltered = false;

	/**
	* <p>En general, el proceso de tracking es el siguiente:
	* <ol>
	*	<li>Computar la función inicial <b>φ</b> y las curvas iniciales.</li>
	*	<li>Computar las características <b>θ</b> de cada target y del fondo.</li>
	*	<li>Aplicar el ciclo inicial del algoritmo (este despliega el resto).</li>
	*	<li>Computar el tiempo de procesamiento de ese frame.</li>
	*	<li>Colorear las curvas finales sobre la imagen.</li>
	*	<li>Agregar la imagen final al resultado.</li>
	*	<li>Seleccionar la siguiente imagen.</li>
	*	<li>Volver al paso 3 hasta que no queden imágenes.</li>
	* </ol>
	* </p>
	*/
	@Override
	public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
		final var result = new HashMap<String, Image>();
		if (states.isEmpty()) return result;
		states.sort(Comparator.comparing(ImageState::getKey));
		isDynamic = ((CheckBox) Main.namespace.get("isDynamic")).isSelected();
		isFiltered = ((CheckBox) Main.namespace.get("isFiltered")).isSelected();
		lin.clear();
		lout.clear();
		mfr.clear();
		mfa.clear();
		final int [] index = {0};
		states.stream().forEachOrdered(state -> {
			final var timer = Timer.start();
			final var image = state.getRGBImage();
			if (index[0] == 0) {
				loadFrame0(state, image);
			}
			mfr.forEach(Set::clear);
			mfa.forEach(Set::clear);
			cycleOne(image.data);
			final var map = new Image(image.getSource(), Matrix.colorFeatures(getContours(), image.data));
			final var key = ImageTool.buildKey("level-set", map, state.getKey());
			result.put(key, map);
			reportFPT(index[0]++, timer);
		});
		return result;
	}

	/**
	* <p>Comresor nulo por defecto, debido a que el algoritmo solo colorea los
	* contornos de los targets trackeados, pero no aplica ninguna
	* transformación sobre la imagen original.</p>
	*/
	@Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}

	/**
	* <p>Inicializa el algoritmo y sus estructuras, solo en el primer
	* frame.</p>
	*
	* @param state
	*	El estado de la imagen.
	* @param image
	*	El espacio de la imagen.
	*/
	protected void loadFrame0(final ImageState state, final Image image) {
		final Rectangle [] targets = new Rectangle [] {state.getArea()};		// TODO: crear la función 'state.getAreas()'
		for (int k = 0; k < targets.length; ++k) {
			lin.add(new HashSet<>());
			lout.add(new HashSet<>());
			mfr.add(new HashSet<>());
			mfa.add(new HashSet<>());
		}
		loadφ(image.data, targets);
		loadθ(image.data);
	}

	/**
	* <p>Imprime en consola el tiempo de procesamiento del último frame
	* (Frame Processing Time).</p>
	*
	* @param frame
	*	Número de frame.
	* @param timer
	*	Timer utilizado para marcar el inicio del frame.
	*/
	protected void reportFPT(final int frame, final Timer timer) {
		System.out.println("\n\t(Frame " + frame + ") Level-set cycled in: "
			+ timer.getTimeInSeconds() + " sec.");
	}

	/**
	* <p>Ciclo de expansión-contracción de curvas.</p>
	*
	* @param space
	*	La imagen a procesar, en RGB.
	*/
	protected void cycleOne(final double [][][] space) {
		final var Na = Math.max(space[0].length, space[0][0].length);
		boolean cycleOneFinish = false;
		final int [] m = {0};
		while (!cycleOneFinish) {
			if (isDynamic) loadθ(space);
			for (int i = 0; i < Na; ++i) {
				for (int k = 0; k < lin.size(); ++k) {
					m[0] = k;
					slin.clear();
					slout.clear();
					lout.get(k).stream()
						.forEach(p -> slout.put(p, getSpeed(space, p, m[0])));
					lin.get(k).stream()
						.forEach(p -> slin.put(p, getSpeed(space, p, m[0])));
					lout.get(k).stream()
						.filter(p -> 0.0 < slout.get(p))
						.forEach(p -> switchIn(p, m[0]));
					update(lout.get(k), k);
					lin.get(k).stream()
						.forEach(p -> drainLin(p, m[0]));
					update(lin.get(k), k);
					lin.get(k).stream()
						.filter(slin::containsKey)
						.filter(p -> slin.get(p) < 0.0)
						.forEach(p -> switchOut(p, m[0]));
					update(lin.get(k), k);
					lout.get(k).stream()
						.forEach(p -> drainLout(p, m[0]));
					update(lout.get(k), k);
				}
				if (isDynamic) loadθ(space);
				if (stoppingCriterion(space)) {
					System.out.println("Cycle I, reach stopping criterion.");
					cycleOneFinish = true;
					break;
				}
			}
			if (isFiltered) {
				cycleTwo(space);
			}
		}
		System.out.println("Cycle I, ended.");
		likelihoodTest(space);
	}

	/**
	* <p>Ciclo de suavizado gaussiano de las curvas. Las convoluciones son
	* puntuales sobre los píxeles de la curva, y no sobre toda la imagen, lo
	* cual acelera considerablemente el procesamiento.</p>
	*
	* @param space
	*	La imagen a procesar, en RGB.
	*/
	protected void cycleTwo(final double [][][] space) {
		final int [] m = {0};
		for (int i = 0; i < G.length; ++i) {
			for (int k = 0; k < lin.size(); ++k) {
				m[0] = k;
				lout.get(k).stream()
					.filter(p -> Matrix.convolution(φ, G, p) < 0)
					.forEach(p -> switchIn(p, m[0]));
				update(lout.get(k), k);
				lin.get(k).stream()
					.forEach(p -> drainLin(p, m[0]));
				update(lin.get(k), k);
				lin.get(k).stream()
					.filter(p -> 0 < Matrix.convolution(φ, G, p))
					.forEach(p -> switchOut(p, m[0]));
				update(lin.get(k), k);
				lout.get(k).stream()
					.forEach(p -> drainLout(p, m[0]));
				update(lout.get(k), k);
			}
		}
		System.out.println("Cycle II, ended.");
	}

	/**
	* <p>Computa el conjunto <i>S(M)</i> de regiones en <i>N(4)</i> alrededor
	* del punto <i>x</i>.</p>
	*
	* @param x
	*	El píxel central.
	*
	* @return
	*	Un conjunto con los índices de las regiones halladas, sin contar el
	*	fondo.
	*/
	protected Set<Integer> S(final Point x) {
		final Set<Integer> regions = new HashSet<>();
		if (x.x + 1 < ψ.length)
			regions.add(ψ[x.x + 1][x.y]);
		if (1 <= x.x)
			regions.add(ψ[x.x - 1][x.y]);
		if (x.y + 1 < ψ[0].length)
			regions.add(ψ[x.x][x.y + 1]);
		if (1 <= x.y)
			regions.add(ψ[x.x][x.y - 1]);
		regions.remove(-1);
		return regions;
	}

	/**
	* <p>Devuelve la región que maximiza la probabilidad de pertenencia de un
	* punto <i>x</i> a una región <i>m</i>.</p>
	*
	* @param space
	*	La imagen a procesar, en RGB.
	* @param regions
	*	Un conjunto de índices con regiones de interés.
	* @param x
	*	El punto a testear.
	*
	* @return
	*	El índice del conjunto de regiones que maximiza la probabilidad.
	*/
	protected int maximal(
			final double [][][] space,
			final Set<Integer> regions, final Point x) {
		if (regions.isEmpty()) return -1;
		final double [] rgb = {
				space[0][x.x][x.y],
				space[1][x.x][x.y],
				space[2][x.x][x.y]
		};
		int bestRegion = -1;
		double bestDistance = Double.POSITIVE_INFINITY;
		for (final int m : regions) {
			final double d = distance(θobj, rgb);								// TODO: Generalizar para 'm' regiones.
			if (d < bestDistance) {
				bestDistance = d;
				bestRegion = m;
			}
		}
		return bestRegion;
	}

	/**
	* <p>Post-procesamiento final. Efectúa un escaneo final para reajustar los
	* píxeles sobre los contornos exteriores de las regiones halladas, solo si
	* estos píxeles no representan puntos simples.</p>
	*/
	protected void likelihoodTest(final double [][][] space) {
		for (int k = 0; k < lout.size(); ++k) {
			lout.get(k).stream()
				.filter(p -> 1 < relaxedTopologicalNumber(p))
				.forEachOrdered(p -> ψ[p.x][p.y] = maximal(space, S(p), p));
		}
		System.out.println("Likelihood Test, ended.");
	}

	/**
	* <p>Computa la función <b>φ</b> para una imagen y su target, efectivamente
	* construyendo la lista de píxeles de las listas <b>Lin</b> y <b>Lout</b>
	* iniciales. También construye el mapa de regiones <b>ψ</b>, donde cada
	* elemento representa la región a la cual pertenece dicho punto, o el valor
	* <i>-1</i>, si no pertenece a ninguna región.</p>
	*
	* <p>El rectángulo de selección representa un área que va desde
	* <i>(X, Y)</i> a <i>(X + W, Y + H)</i>, inclusive, y tiene la forma:
	* <pre>
	*	(X, Y)---------------------o
	*	|                          |
	*	|      o h                 |
	*	|      w                   |
	*	o-------------(X + W, Y + H)
	* </pre></p>
	*
	* @param space
	*	La imagen a procesar, en RGB.
	* @param targets
	*	Los rectángulos iniciales de selección de targets.
	*/
	protected void loadφ(final double [][][] space, final Rectangle [] targets) {
		final var X = new int [targets.length];
		final var Y = new int [targets.length];
		final var W = new int [targets.length];
		final var H = new int [targets.length];
		for (int k = 0; k < targets.length; ++k) {
			X[k] = (int) targets[k].getX();
			Y[k] = (int) targets[k].getY();
			W[k] = (int) targets[k].getWidth();
			H[k] = (int) targets[k].getHeight();
		}
		ψ = Matrix.flatIntAndEmptySpaceFrom(space);
		φ = Matrix.flatFilterToInt(space, (s, c, w, h) -> {
			for (int k = 0; k < targets.length; ++k) {
				if (X[k] < w && w < X[k] + W[k] && Y[k] < h && h < Y[k] + H[k]) {
					// Interior
					ψ[w][h] = k;
					return -3;
				}
			}
			for (int k = 0; k < targets.length; ++k) {
				if (X[k] <= w && w <= X[k] + W[k] && Y[k] <= h && h <= Y[k] + H[k]) {
					// Sobre 'lin'
					lin.get(k).add(new Point(w, h));
					ψ[w][h] = k;
					return -1;
				}
			}
			ψ[w][h] = -1;
			for (int k = 0; k < targets.length; ++k) {
				if (X[k] - 1 <= w && w <= X[k] + W[k] + 1 && Y[k] - 1 <= h && h <= Y[k] + H[k] + 1) {
					// Sobre 'lout'
					lout.get(k).add(new Point(w, h));
					return 1;
				}
			}
			// Exterior (background)
			return 3;
		});
	}

	/**
	* <p>Computa las características de cada objeto trackeado y del fondo, por
	* única vez, a menos que se utilice la propiedad <b>isDynamic</b>, que
	* permite actualizar estos parámetros en cada frame, lo cual degrada
	* considerablemente la performance.</p>
	*
	* @param space
	*	La imagen a procesar, en RGB.
	*/
	protected void loadθ(final double [][][] space) {						// TODO: Extender a 'm' regiones.
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

	/**
	* <p>Computa la velocidad de evolución (denotada como <i>F</i> en el
	* paper), para cierto punto de la imagen.</p>
	*
	* @param space
	*	La imagen a procesar.
	* @param x
	*	El píxel para el cual computar la velocidad.
	* @param m
	*	El número de región.
	*
	* @return
	*	La velocidad sobre ese píxel.
	*/
	protected double getSpeed(final double [][][] space, final Point x, final int m) {		// TODO: Generalizar para 'm'.
		final double [] rgb = {
				space[0][x.x][x.y],
				space[1][x.x][x.y],
				space[2][x.x][x.y]
		};
		// TODO: Ver Rozitchner, pág. 27, para fórmulas más simples.
		return Math.log(distance(θback, rgb)/distance(θobj, rgb));
	}

	/**
	* <p>Criterio de detención del algoritmo.</p>
	*
	* @param space
	*	La imagen sobre la cual verificar el criterio.
	* @return
	*	Devuelve verdadero si se alcanzó una convergencia en las curvas de
	*	tracking, o falso de otro modo.
	*/
	protected boolean stoppingCriterion(final double [][][] space) {
		boolean condition = true;
		final int [] m = {0};
		for (int k = 0; k < lin.size(); ++k) {
			m[0] = k;
			condition = condition
				&& lout.get(k).stream().allMatch(p -> getSpeed(space, p, m[0]) <= 0.0)
				&& lin.get(k).stream().allMatch(p -> 0.0 <= getSpeed(space, p, m[0]));
		}
		return condition;
	}

	/**
	* <p>Permite remover y/o agregar los elementos marcados durante el
	* procesamiento del algoritmo, específicamente luego de ejecutar
	* <b>switchIn/Out</b> o <b>drainLin/Lout</b>. Se debe llamar este método
	* para reflejar los cambios, si o si.</p>
	*
	* <p>Los elementos de <b>mfr</b> (<i>Mark for Removal</i>), se extraen del
	* conjunto especificado, mientras que los de <b>mfa</b> (<i>Mark for
	* Add</i>), se agregan al mismo.</p>
	*
	* @param points
	*	El conjunto <b>lin</b> o <b>lout</b> a actualizar.
	* @param m
	*	El número de región.
	*/
	protected void update(final Set<Point> points, final int m) {
		points.removeAll(mfr.get(m));
		points.addAll(mfa.get(m));
		mfr.get(m).clear();
		mfa.get(m).clear();
	}

	/**
	* <p>Computa la distancia euclídea entre dos vectores característicos.</p>
	*
	* @param u
	*	El primer vector RGB.
	* @param v
	*	El segundo vector RGB.
	* @return
	*	La distancia, como un número real mayor o igual a cero.
	*/
	protected double distance(final double [] u, final double [] v) {
		final double Δr = u[0] - v[0];
		final double Δg = u[1] - v[1];
		final double Δb = u[2] - v[2];
		return Math.sqrt(Δr*Δr + Δg*Δg + Δb*Δb);
	}

	/**
	* <p>Número de regiones diferentes presentes en <i>N(8)</i>, excluyendo el
	* fondo y el píxel central <i>x</i>.</p>
	*
	* @param x
	*	El píxel central.
	* @return
	*	El número de regiones, debe ser cero o un número positivo.
	*/
	protected int α(final Point x) {
		final Set<Integer> regions = new HashSet<>();
		if (x.x + 1 < ψ.length)
			regions.add(ψ[x.x + 1][x.y]);
		if (1 <= x.x)
			regions.add(ψ[x.x - 1][x.y]);
		if (x.y + 1 < ψ[0].length)
			regions.add(ψ[x.x][x.y + 1]);
		if (1 <= x.y)
			regions.add(ψ[x.x][x.y - 1]);
		if (x.x + 1 < ψ.length && x.y + 1 < ψ[0].length)
			regions.add(ψ[x.x + 1][x.y + 1]);
		if (x.x + 1 < ψ.length && 1 <= x.y)
			regions.add(ψ[x.x + 1][x.y - 1]);
		if (1 <= x.x && x.y + 1 < ψ[0].length)
			regions.add(ψ[x.x - 1][x.y + 1]);
		if (1 <= x.x && 1 <= x.y)
			regions.add(ψ[x.x - 1][x.y - 1]);
		if (regions.contains(-1)) {
			return regions.size() - 1;
		}
		else return regions.size();
	}

	/**
	* <p>Devuelve el número topológico de <i>x</i>, equivalente a la cantidad
	* de componentes conexas en <i>N(4)</i>, en la vecindad <i>N(8)</i> del
	* punto. Una componente conexa contiene al menos 2 píxeles y además está
	* compuesta por píxeles que pertenecen a un mismo objeto, pero no al
	* fondo.</p>
	*
	* @param x
	*	El punto central.
	* @return
	*	Un número positivo o igual a cero.
	*/
	protected int objectTopologicalNumber(final Point x) {
		// TODO: Ver Rozitchner, pág. 41.
		return 1;
	}

	/**
	* <p>Devuelve el número topológico de <i>x</i>, equivalente a la cantidad
	* de componentes conexas en <i>N(8)</i>, en la vecindad <i>N(8)</i> del
	* punto. Una componente conexa contiene al menos 2 píxeles y además está
	* compuesta por píxeles que pertenecen únicamente al fondo.</p>
	*
	* @param x
	*	El punto central.
	* @return
	*	Un número positivo o igual a cero.
	*/
	protected int backgroundTopologicalNumber(final Point x) {
		// TODO: Ver Rozitchner, pág. 41.
		return 1;
	}

	/**
	* <p>Computa el número topológico relajado, el cual permite indicar la
	* posibilidad de expansión de una curva.</p>
	*
	* @param x
	*	El punto a analizar.
	* @return
	*	Un entero mayor o igual a cero, donde <i>1</i> indica la presencia de
	*	un punto simple.
	*/
	protected int relaxedTopologicalNumber(final Point x) {
		return Math.min(α(x), Math.max(objectTopologicalNumber(x), backgroundTopologicalNumber(x)));
	}

	/**
	* <p>Proceso de expansión de curvas.</p>
	*
	* @param x
	*	El punto de la imagen a procesar.
	* @param m
	*	El número de región.
	*/
	protected void switchIn(final Point x, final int m) {
		if (relaxedTopologicalNumber(x) != 1) return;
		mfr.get(m).add(x);
		lin.get(m).add(x);
		φ[x.x][x.y] = -1;
		ψ[x.x][x.y] = m;
		final var up = new Point(x.x, x.y - 1);
		final var down = new Point(x.x, x.y + 1);
		final var left = new Point(x.x - 1, x.y);
		final var right = new Point(x.x + 1, x.y);
		if (0 <= up.y && φ[up.x][up.y] == 3) {
			mfa.get(m).add(up);
			φ[up.x][up.y] = 1;
		}
		if (down.y < φ[0].length && φ[down.x][down.y] == 3) {
			mfa.get(m).add(down);
			φ[down.x][down.y] = 1;
		}
		if (0 <= left.x && φ[left.x][left.y] == 3) {
			mfa.get(m).add(left);
			φ[left.x][left.y] = 1;
		}
		if (right.x < φ.length && φ[right.x][right.y] == 3) {
			mfa.get(m).add(right);
			φ[right.x][right.y] = 1;
		}
	}

	/**
	* <p>Proceso de contracción de curvas.</p>
	*
	* @param x
	*	El punto de la imagen a procesar.
	* @param m
	*	El número de región.
	*/
	protected void switchOut(final Point x, final int m) {
		mfr.get(m).add(x);
		lout.get(m).add(x);
		φ[x.x][x.y] = 1;
		ψ[x.x][x.y] = -1;
		final var up = new Point(x.x, x.y - 1);
		final var down = new Point(x.x, x.y + 1);
		final var left = new Point(x.x - 1, x.y);
		final var right = new Point(x.x + 1, x.y);
		if (0 <= up.y && φ[up.x][up.y] == -3) {
			mfa.get(m).add(up);
			φ[up.x][up.y] = -1;
		}
		if (down.y < φ[0].length && φ[down.x][down.y] == -3) {
			mfa.get(m).add(down);
			φ[down.x][down.y] = -1;
		}
		if (0 <= left.x && φ[left.x][left.y] == -3) {
			mfa.get(m).add(left);
			φ[left.x][left.y] = -1;
		}
		if (right.x < φ.length && φ[right.x][right.y] == -3) {
			mfa.get(m).add(right);
			φ[right.x][right.y] = -1;
		}
	}

	/**
	* <p>Depuración de la lista <i>Lin</i>. Este proceso implica verificar
	* aquellos píxeles que se encuentran en la curva interior, pero que se
	* encuentran rodeados por píxeles del mismo tipo (<i>φ < 0</i>). En este
	* caso, no es necesario retener dicho píxel en el conjunto.</p>
	*
	* @param x
	*	El punto de la imagen a procesar.
	* @param m
	*	El número de región.
	*/
	protected void drainLin(final Point x, final int m) {
		final var up = new Point(x.x, x.y - 1);
		final var down = new Point(x.x, x.y + 1);
		final var left = new Point(x.x - 1, x.y);
		final var right = new Point(x.x + 1, x.y);
		final var leftup = new Point(x.x - 1, x.y - 1);
		final var rightup = new Point(x.x + 1, x.y - 1);
		final var leftdown = new Point(x.x - 1, x.y + 1);
		final var rightdown = new Point(x.x + 1, x.y + 1);
		final boolean condition =
				(up.y < 0 || φ[up.x][up.y] < 0) &&
				(φ[0].length <= down.y || φ[down.x][down.y] < 0) &&
				(left.x < 0 || φ[left.x][left.y] < 0) &&
				(φ.length <= right.x || φ[right.x][right.y] < 0) &&
				(leftup.x < 0 || leftup.y < 0 || φ[leftup.x][leftup.y] < 0) &&
				(φ.length <= rightup.x || rightup.y < 0 || φ[rightup.x][rightup.y] < 0) &&
				(leftdown.x < 0 || φ[0].length <= leftdown.y || φ[leftdown.x][leftdown.y] < 0) &&
				(φ.length <= rightdown.x || φ[0].length <= rightdown.y || φ[rightdown.x][rightdown.y] < 0);
		if (condition) {
			mfr.get(m).add(x);
			φ[x.x][x.y] = -3;
			ψ[x.x][x.y] = m;							// TODO: Verificar (no está en los papers, pero tiene sentido).
		}
	}

	/**
	* <p>Depuración de la lista <i>Lout</i>. Este proceso implica verificar
	* aquellos píxeles que se encuentran en la curva exterior, pero que se
	* encuentran rodeados por píxeles del mismo tipo (<i>0 < φ</i>). En este
	* caso, no es necesario retener dicho píxel en el conjunto.</p>
	*
	* @param x
	*	El punto de la imagen a procesar.
	* @param m
	*	El número de región.
	*/
	protected void drainLout(final Point x, final int m) {
		final var up = new Point(x.x, x.y - 1);
		final var down = new Point(x.x, x.y + 1);
		final var left = new Point(x.x - 1, x.y);
		final var right = new Point(x.x + 1, x.y);
		final var leftup = new Point(x.x - 1, x.y - 1);
		final var rightup = new Point(x.x + 1, x.y - 1);
		final var leftdown = new Point(x.x - 1, x.y + 1);
		final var rightdown = new Point(x.x + 1, x.y + 1);
		final boolean condition =
				(up.y < 0 || φ[up.x][up.y] > 0) &&
				(φ[0].length <= down.y || φ[down.x][down.y] > 0) &&
				(left.x < 0 || φ[left.x][left.y] > 0) &&
				(φ.length <= right.x || φ[right.x][right.y] > 0) &&
				(leftup.x < 0 || leftup.y < 0 || φ[leftup.x][leftup.y] > 0) &&
				(φ.length <= rightup.x || rightup.y < 0 || φ[rightup.x][rightup.y] > 0) &&
				(leftdown.x < 0 || φ[0].length <= leftdown.y || φ[leftdown.x][leftdown.y] > 0) &&
				(φ.length <= rightdown.x || φ[0].length <= rightdown.y || φ[rightdown.x][rightdown.y] > 0);
		if (condition) {
			mfr.get(m).add(x);
			φ[x.x][x.y] = 3;
			ψ[x.x][x.y] = -1;								// TODO: Verificar (está en Rozitchner, pero no en Shi/Karl).
		}
	}

	/**
	* <p>Reemplaza los píxeles pertenecientes a las curvas con valores
	* preseteados, lo cual permite identificarlos para luego colorearlos e
	* identificarlos fácilmente utilizando {@link Matrix#colorFeatures}.</p>
	*
	* @return
	*	La imagen con las curvas marcadas bajo alguna constante.
	*/
	protected double [][][] getContours() {													// TODO: Agregar más colores.
		final var contours = new double [1][φ.length][φ[0].length];
		for (int k = 0; k < lin.size(); ++k) {
			lin.get(k).stream()
				.forEach(p -> contours[0][p.x][p.y] = Matrix.CONTOUR);
			lout.get(k).stream()
				.forEach(p -> contours[0][p.x][p.y] = Matrix.OUTTER_CONTOUR);
		}
		return contours;
	}
}
