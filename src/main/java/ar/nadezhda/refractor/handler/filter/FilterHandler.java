package ar.nadezhda.refractor.handler.filter;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.LinearCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;

public abstract class FilterHandler implements Handler {
    protected DoubleBinaryOperator operation;
    protected final String action;
    protected boolean normalize;

    public FilterHandler(String action, boolean normalize) {
        this.action=action;
        this.normalize=normalize;
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
        int dimension;
        TextField textField = (TextField) node.getScene().lookup("#dimensionValue");
        try {
            dimension = Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
        	ImageTool.popup(AlertType.ERROR, "Error!", "The dimension isn't an integer number.");
            return result;
        }
        if (dimension%2==0) {
        	ImageTool.popup(AlertType.ERROR, "Error!", "The dimension must be even.");
            return result;
        }
        generateOperation((dimension - 1.0)/2.0, dimension);
        ImageState imageState = states.get(0);
        final Image image = imageState.filter(dimension, operation, normalize);
        final String key = ImageTool.buildKey(this.action, image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }

    @Override
	public Compressor getCompressor() {
		return Main.context.getBean(LinearCompressor.class);
	}

    private void generateOperation(double stdDev, int dimension) {
        switch (action) {
            case "meanFilter":
                operation = (i,j)->1.0;
                break;
            case "gaussFilter":
                operation = (i,j)->(1.0/(2*Math.PI*Math.pow(stdDev,2)))*
                        Math.exp(-(Math.pow(i,2)+Math.pow(j,2))/Math.pow(stdDev,2));
                break;
            case "highpassFilter":
                operation = (i,j)->(i==0&&j==0?(dimension*dimension-1.0)/(dimension*dimension):-1.0/(dimension*dimension));
                break;
        }
    }
}
