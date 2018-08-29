package ar.nadezhda.refractor.handler;

import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedianFilterHandler implements Handler {
    @Override
    public Map<String, Image> handle(List<ImageState> states, Node node) {
        Map<String, Image> result = new HashMap<>();
        if (states.size()!=1){
        	ImageTool.popup(AlertType.WARNING, "Warning!",
            		"You must select only 1 image to apply the 'median filter' action.");
        	return result;
        }
        int dimension;
        TextField textField = (TextField) node.getScene().lookup("#scalar");
        CheckBox checkBox = (CheckBox) node.getScene().lookup("#ponderateMedian");
        boolean ponderate = checkBox.isSelected();
        try {
            dimension = Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
        	ImageTool.popup(AlertType.ERROR, "Error!", "The dimension isn't an integer number.");
            return result;
        }
        if (dimension%2==0) {
        	ImageTool.popup(AlertType.ERROR, "Error!", "The dimension isn't an odd number.");
            return result;
        }
        if (ponderate && dimension!=3){
        	ImageTool.popup(AlertType.ERROR, "Error!",
        		"The weighted median filter only supports a dimension of 3.");
            return result;
        }
        ImageState imageState = states.get(0);
        final Image image = imageState.medianFilter(dimension, ponderate);
        final String key = ImageTool.buildKey("medianFilter", image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }
}
