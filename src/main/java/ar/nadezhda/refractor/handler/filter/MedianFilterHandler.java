package ar.nadezhda.refractor.handler.filter;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedianFilterHandler implements Handler {
    @Override
    public Map<String, Image> handle(List<ImageState> states, final ActionEvent action) {
        Map<String, Image> result = new HashMap<>();
        if (states.size()!=1){
        	ImageTool.popup(AlertType.WARNING, "Warning!",
            		"You must select only 1 image to apply the 'median filter' action.");
        	return result;
        }
        int dimension;
        final Node node = (Node) action.getSource();
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
        ImageState imageState = states.get(0);
        final Image image = imageState.medianFilter(dimension, false);
        final String key = ImageTool.buildKey("medianFilter", image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }

    @Override
	public Compressor getCompressor() {
		return Main.context.getBean(NullCompressor.class);
	}
}
