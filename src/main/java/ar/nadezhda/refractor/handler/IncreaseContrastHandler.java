package ar.nadezhda.refractor.handler;

import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.scene.Node;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncreaseContrastHandler implements Handler {
    @Override
    public Map<String, Image> handle(List<ImageState> states, Node node) {
        Map<String, Image> result = new HashMap<>();
        if (states.size()!=1){
            System.out.println("Una imagen solo");
        }
        double scalar;
        TextField textField = (TextField) node.getScene().lookup("#scalar");
        try {
            scalar = Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Not a number");
            return result;
        }
        ImageState imageState = states.get(0);
        final Image image = imageState.increaseContrast(scalar);
        final String key = ImageTool.buildKey("increasecontrast", image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }
}
