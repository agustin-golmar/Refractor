package ar.nadezhda.refractor.handler.unary;

import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

public abstract class UnaryHandler implements Handler {
    protected final String action;
    private final Random random;
    protected DoubleUnaryOperator operation;
    protected double scalar;
    protected double mean;

    public UnaryHandler(final String action) {
        this.action = action;
        this.random = new Random();
    }

    @Override
    public Map<String, Image> handle(List<ImageState> states, final ActionEvent action) {
        Map<String, Image> result = new HashMap<>();
        if (states.size()!=1){
        	ImageTool.popup(AlertType.WARNING, "Warning!", new StringBuilder()
					.append("You must select only 1 image to apply the '")
					.append(this.action)
					.append("' action.")
					.toString());
        	return result;
        }
        final Node node = (Node) action.getSource();
        // Validate required parameters:
        try {
	        if (this.action.equals("scalarProd")) {
	        	scalar = Double.parseDouble(((TextField) node.getScene().lookup("#scalarValue")).getText());
	        }
	        else if (this.action.equals("power")) {
	        	scalar = ((Slider) node.getScene().lookup("#gammaValue")).getValue();
	        }
	        else if (this.action.equals("threshold")) {
	        	scalar = ((Slider) node.getScene().lookup("#uValue")).getValue();
	        }
	        else if (this.action.equals("exponentialNoise")) {
	        	scalar = ((Slider) node.getScene().lookup("#lambdaValue")).getValue();
	        }
	        else if (this.action.equals("rayleighNoise")) {
	        	scalar = ((Slider) node.getScene().lookup("#rayleighValue")).getValue();
	        }
	        else if (this.action.equals("gaussianNoise")) {
	        	scalar = ((Slider) node.getScene().lookup("#deviationValue")).getValue();
	        	mean = ((Slider) node.getScene().lookup("#meanValue")).getValue();
	        }
	        else if (this.action.equals("saltAndPepper")) {
	        	scalar = 0.01 * ((Slider) node.getScene().lookup("#contaminationValue")).getValue();
	        }
	        else if (this.action.equals("negative")) {
	        	// Negative doesn't need parameters...
	        }
	        else {
	        	ImageTool.popup(AlertType.ERROR, "Error!", "Unknown action: " + this.action + ".");
	        	return result;
	        }
        }
	    catch (final NumberFormatException exception) {
            ImageTool.popup(AlertType.ERROR, "Error!", "The parameter isn't a number.");
            return result;
        }
        ImageState imageState = states.get(0);
        generateOperation();
        final Image image = imageState.unaryOp(operation);
        final String key = ImageTool.buildKey(this.action, image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }

    private void generateOperation() {
        switch (action) {
            case "scalarProd":
                operation = (n) -> n*scalar;
                break;
            case "negative":
                operation = (n) -> 255.0-n;
                break;
            case "threshold":
                operation = (n) -> n>scalar?255:0;
                break;
            case "power":
                operation = (n) -> Math.pow(255.0,1.0-scalar)*Math.pow(n,scalar);
                break;
            case "gaussianNoise":
                operation = (n) -> n+255.0*(random.nextGaussian()*scalar+mean);
                break;
            case "exponentialNoise":
                operation = (n) -> n*Math.log(random.nextDouble())*(-1/scalar);
                break;
            case "saltAndPepper":
                operation = (n) -> random.nextDouble()<scalar?(random.nextDouble()>0.5?255:0):n;
                break;
            case "rayleighNoise":
                operation = (n) ->n*scalar*Math.sqrt(-2*Math.log((1-random.nextDouble())));
                break;
        }
    }
}
