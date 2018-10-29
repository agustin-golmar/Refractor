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
        var image = imageState.getImage();

        //Hardcoded Gauss Filter
        var image1 = imageState.filter(3,(i,j)->(1.0/(2*Math.PI*Math.pow(1.0,2)))*
                Math.exp(-(Math.pow(i,2)+Math.pow(j,2))/Math.pow(1.0,2)),false);
        var image2 = ImageState.filter(image,5,(i,j)->(1.0/(2*Math.PI*Math.pow(2.0,2)))*
                Math.exp(-(Math.pow(i,2)+Math.pow(j,2))/Math.pow(2.0,2)),false);

        final var dx1 = operator.convolutionOverX(image1.data);
        final var dy1 = operator.convolutionOverY(image1.data);
        final var dx2 = operator.convolutionOverX(image2.data);
        final var dy2 = operator.convolutionOverY(image2.data);
        final var grad1 = Matrix.absoluteGradient(dx1, dy1);
        var ret1 = Matrix.nonMaxSupression(grad1,dx1,dy1);
        Matrix.hystheresis(ret1,Math.min(t1,t2),Math.max(t1,t2));

        final var grad2 = Matrix.absoluteGradient(dx2, dy2);
        var ret2 = Matrix.nonMaxSupression(grad2,dx2,dy2);
        Matrix.hystheresis(ret2,Math.min(t1,t2),Math.max(t1,t2));

        final var borders = new Image(image.getSource(), ret2);


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
