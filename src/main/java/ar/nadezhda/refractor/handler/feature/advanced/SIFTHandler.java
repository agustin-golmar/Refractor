package ar.nadezhda.refractor.handler.feature.advanced;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Slider;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

	/**
	* <p>Esta implementación de SIFT requiere que OpenCV se encuentre instalado
	* en el sistema. La versión correspondiente debe ser <b>2.4.13.6</b>. Se
	* debe especificar, además, la propiedad <b>java.library.path</b> con la
	* ruta hacia el directorio donde se encuentra la librería correspondiente.
	* </p>
	*/

public class SIFTHandler implements Handler {

	private static final Logger logger = LoggerFactory
			.getLogger(SIFTHandler.class);

	public SIFTHandler() {
		try {
			// Load OpenCV Native Library:
			System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		}
		catch (final UnsatisfiedLinkError exception) {
			logger.warn("No se pudo cargar la librería necesaria para ejecutar el detector SIFT.");
			logger.warn("Refractor seguirá funcionando, pero el método SIFT no podrá utilizarse.");
			logger.warn("Se requiere una librería para la versión OpenCV {}.", Core.VERSION);
			logger.warn("Se especificó la ruta '{}'", System.getProperty("java.library.path"));
		}
	}

	@Override
	public Map<String, Image> handle(final List<ImageState> states, final ActionEvent action) {
		final var result = new HashMap<String, Image>();
		if (states.size() != 2) {
			ImageTool.popup(AlertType.WARNING, "Warning!",
				"You must select 2 images to apply the 'S.I.F.T. Detector'.");
		}
		else {
			// Configuración:
			final FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
			final DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
			final DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
			final float knnRatio = (float) ((Slider) Main.namespace.get("knnRatio")).getValue();
			final double matchRatio = ((Slider) Main.namespace.get("matchRatio")).getValue();
			logger.info("Using knn-ratio = {}", knnRatio);
			logger.info("Using match-ratio = {}%", matchRatio);

			// Imagen 1:
			final var state1 = states.get(0);
			final var image1 = state1.getImage();
			final Mat matrix1 = new Mat(image1.getHeight(), image1.getWidth(), CvType.CV_8U);
			matrix1.put(0, 0, image1.getPlainGrayscale());

			// Imagen 2:
			final var state2 = states.get(1);
			final var image2 = state2.getImage();
			final Mat matrix2 = new Mat(image2.getHeight(), image2.getWidth(), CvType.CV_8U);
			matrix2.put(0, 0, image2.getPlainGrayscale());

			// SIFT over Image-1:
			final MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
			final MatOfKeyPoint descriptors1 = new MatOfKeyPoint();
			detector.detect(matrix1, keypoints1);
			extractor.compute(matrix1, keypoints1, descriptors1);

			// SIFT over Image-2:
			final MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
			final MatOfKeyPoint descriptors2 = new MatOfKeyPoint();
			detector.detect(matrix2, keypoints2);
			extractor.compute(matrix2, keypoints2, descriptors2);

			// Output-1 Features:
			final Mat output1 = new Mat(matrix1.rows(), matrix1.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
			Features2d.drawKeypoints(matrix1, keypoints1, output1, new Scalar(0, 0, 255), 0);
			final var rawOutput1 = new byte [(int) (output1.total() * output1.channels())];
			output1.get(0, 0, rawOutput1);
			final var outputImage1 = new Image(image1.getSource(), rawOutput1, 3, output1.cols(), output1.rows());
			final var output1key = ImageTool.buildKey("sift", outputImage1, state1.getKey());
			result.put(output1key, outputImage1);

			// Output-1 Features:
			final Mat output2 = new Mat(matrix2.rows(), matrix2.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
			Features2d.drawKeypoints(matrix2, keypoints2, output2, new Scalar(0, 255, 255), 0);
			final var rawOutput2 = new byte [(int) (output2.total() * output2.channels())];
			output2.get(0, 0, rawOutput2);
			final var outputImage2 = new Image(image2.getSource(), rawOutput2, 3, output2.cols(), output2.rows());
			final var output2key = ImageTool.buildKey("sift", outputImage2, state2.getKey());
			result.put(output2key, outputImage2);

			// Encuentra los 2 mejores vecinos y filtra los parecidos:
			final List<MatOfDMatch> matches = new ArrayList<>();
			matcher.knnMatch(descriptors1, descriptors2, matches, 2);
			final List<DMatch> bestMatches = matches.stream()
					.map(MatOfDMatch::toArray)
					.filter(m -> m[0].distance <= knnRatio * m[1].distance)
					.map(m -> m[0])
					.collect(Collectors.toList());

			// Construir rectas entre keypoints:
			final MatOfDMatch bestDMatches = new MatOfDMatch();
			bestDMatches.fromList(bestMatches);
			final var width = matrix1.cols() + matrix2.cols();
			final var height = Math.max(matrix1.rows(), matrix2.rows());
			final Mat matrixMatch = new Mat(height, width, Highgui.CV_LOAD_IMAGE_COLOR);
	        final var colorMatch = new Scalar(0, 255, 0);
			Features2d.drawMatches(matrix1, keypoints1, matrix2, keypoints2, bestDMatches,
					matrixMatch, colorMatch, new Scalar(0, 255, 255), new MatOfByte(), 2);

			// Generar imagen final con características asociadas:
			final var rawOutput3 = new byte [(int) (matrixMatch.total() * matrixMatch.channels())];
			matrixMatch.get(0, 0, rawOutput3);
			final var outputImage3 = new Image(image1.getSource() + "|" + image2.getSource(),
					rawOutput3, 3, matrixMatch.cols(), matrixMatch.rows());
			final var output3key = ImageTool.buildKey("sift-associations", outputImage3, state1.getKey(), state2.getKey());
			result.put(output3key, outputImage3);

			// Informa y construye el resultado:
			if (matchRatio <= (100.0 * bestMatches.size()/(double) matches.size())) {
				ImageTool.popup(AlertType.INFORMATION, "S.I.F.T.",
					"El objeto fue encontrado!!! Se hallaron " + bestMatches.size() +
					"/" + matches.size() + " asociaciones.");
			}
			else {
				ImageTool.popup(AlertType.INFORMATION, "S.I.F.T.",
					"El objeto no fue encontrado. Se hallaron " + bestMatches.size() +
					"/" + matches.size() + " asociaciones.");
			}
		}
		return result;
	}

	@Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
