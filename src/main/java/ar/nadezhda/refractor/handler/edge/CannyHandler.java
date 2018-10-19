package ar.nadezhda.refractor.handler.edge;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.LinearCompressor;
import ar.nadezhda.refractor.handler.edge.operator.ConvolutionHandler;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import ar.nadezhda.refractor.support.Matrix;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CannyHandler implements Handler {

    @Override
    public Map<String, Image> handle(List<ImageState> states, ActionEvent action) {
        var result = new HashMap<String, Image>();
        if (states.size() != 1) {
            ImageTool.popup(Alert.AlertType.WARNING, "Warning!", new StringBuilder()
                    .append("You must select only 1 image to apply the '")
                    .append("Canny Detector")
                    .append("' action.")
                    .toString());
            return result;
        }

        final Node node = (Node) action.getSource();

        var t1 = ((Slider) node.getScene().lookup("#t1")).getValue();
        var t2 = ((Slider) node.getScene().lookup("#t2")).getValue();
        final var imageState = states.get(0);

        final var operator = ConvolutionHandler.getOperator();
        var image = imageState.filter(3,(i,j)->(1.0/(2*Math.PI*Math.pow(1.0,2)))*
                Math.exp(-(Math.pow(i,2)+Math.pow(j,2))/Math.pow(1.0,2)),false);
        image = ImageState.filter(image,5,(i,j)->(1.0/(2*Math.PI*Math.pow(2.0,2)))*
                Math.exp(-(Math.pow(i,2)+Math.pow(j,2))/Math.pow(2.0,2)),false);

        final var dx = operator.convolutionOverX(image.data);
        final var dy = operator.convolutionOverY(image.data);
        final var grad = Matrix.absoluteGradient(dx, dy);
        var ret = Matrix.nonMaxSupression(grad,dx,dy);
        Matrix.hystheresis(ret,Math.min(t1,t2),Math.max(t1,t2));

        final var borders = new Image(image.getSource(), ret);


        final String key = ImageTool.buildKey("Canny", borders,
                states.get(0).getKey());
        result.put(key, borders);
        return result;

    }

    @Override
    public Compressor getCompressor() {
        return Main.context.getBean(LinearCompressor.class);
    }
}
