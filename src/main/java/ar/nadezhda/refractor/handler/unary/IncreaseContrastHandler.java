package ar.nadezhda.refractor.handler.unary;

import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncreaseContrastHandler implements Handler {
    @Override
    public Map<String, Image> handle(List<ImageState> states, final ActionEvent action) {
        Map<String, Image> result = new HashMap<>();
        if (states.size()!=1){
        	ImageTool.popup(AlertType.WARNING, "Warning!",
        		"You must select only 1 image to apply the 'contrast' action.");
            return result;
        }
        double scalar;
        final Node node = (Node) action.getSource();
        TextField textField = (TextField) node.getScene().lookup("#contrastValue");
        try {
            scalar = Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
        	ImageTool.popup(AlertType.ERROR, "Error!", "The parameter isn't a number.");
            return result;
        }
        ImageState imageState = states.get(0);
        final Image image = imageState.increaseContrast(scalar);
        final String key = ImageTool.buildKey("increaseContrast", image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }
}
