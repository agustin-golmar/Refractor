package ar.nadezhda.refractor.handler.feature.advanced;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
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
		/*if (states.size() != 2) {
			ImageTool.popup(AlertType.WARNING, "Warning!",
				"You must select 2 images to apply the 'S.I.F.T. Detector'.");
		}
		else {*/
			// Imagen 1:
			final var state1 = states.get(0);
			final var image1 = state1.getImage();
			final Mat matrix1 = new Mat(image1.getHeight(), image1.getWidth(), CvType.CV_8U);
			matrix1.put(0, 0, image1.getPlainGrayscale());

			// SIFT over Image-1:
			final MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
			final MatOfKeyPoint descriptors1 = new MatOfKeyPoint();
			final FeatureDetector detector = FeatureDetector.create(FeatureDetector.SIFT);
			final DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
			detector.detect(matrix1, keypoints1);
			extractor.compute(matrix1, keypoints1, descriptors1);

			//final KeyPoint [] keypoints = objectKeyPoints.toArray();
			System.out.println("Keypoints founded: " + keypoints1.size());
			System.out.println("Channels: " + descriptors1.channels());						// 1
			System.out.println("Dims: " + descriptors1.dims());								// 2
			System.out.println("Rows: " + descriptors1.rows());								// 888
			System.out.println("Cols: " + descriptors1.cols());								// 128
			System.out.println("Descriptor size: " + descriptors1.size());					// 128x888 (cols x rows)
			System.out.println("Row 0 size: " + descriptors1.row(0).size());				// 128 x 1 (cols x rows)
			System.out.println("Descriptors keypoints: " + descriptors1.toArray().length);	// 113664 = 128x888

			// Output-1 Features:
			final Mat output1 = new Mat(matrix1.rows(), matrix1.cols(), Highgui.CV_LOAD_IMAGE_COLOR);
			Features2d.drawKeypoints(matrix1, keypoints1, output1, new Scalar(255, 0, 0), 0);
			final var rawOutput1 = new byte [(int) (output1.total() * output1.channels())];
			output1.get(0, 0, rawOutput1);
			final var outputImage1 = new Image(image1.getSource(), rawOutput1, 3, output1.cols(), output1.rows());
			final var output1key = ImageTool.buildKey("sift", outputImage1, state1.getKey());
			result.put(output1key, outputImage1);
		//}
		return result;
	}

	@Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
