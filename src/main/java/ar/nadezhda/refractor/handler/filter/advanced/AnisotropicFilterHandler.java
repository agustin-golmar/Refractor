package ar.nadezhda.refractor.handler.filter.advanced;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.LinearCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnisotropicFilterHandler implements Handler {

    boolean isotropic = false;
    @Override
    public Map<String, Image> handle(List<ImageState> states, ActionEvent action) {

        var result = new HashMap<String, Image>();
        if (states.size()!=1){
            ImageTool.popup(Alert.AlertType.WARNING, "Warning!", new StringBuilder()
                    .append("You must select only 1 image to apply the '")
                    .append("anisotropic filter")
                    .append("' action.")
                    .toString());
            return result;
        }
        System.out.println("Iso: "+isotropic);
        final Node node = (Node) action.getSource();
        int steps;
        double sigma;
        TextField textField = (TextField) node.getScene().lookup("#steps");
        TextField textField2 = (TextField) node.getScene().lookup("#sigmadet");
        try {
            steps = Integer.parseInt(textField.getText());
            sigma = Double.parseDouble(textField2.getText());
        } catch (NumberFormatException e) {
            ImageTool.popup(Alert.AlertType.ERROR, "Error!", "The steps aren't an integer number.");
            return result;
        }
        ImageState imageState = states.get(0);
        final Image image = imageState.anisotropicFilter(steps,sigma,isotropic);
        final String key = ImageTool.buildKey("anisotropic", image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }

    @Override
    public Compressor getCompressor() {
        return Main.context.getBean(LinearCompressor.class);
    }
}
