package ar.nadezhda.refractor.handler.threshold;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalThresholdHandler implements Handler {

    private int t;

    @Override
    public Map<String, Image> handle(List<ImageState> states, ActionEvent action) {
        var result = new HashMap<String, Image>();
        if (states.size() != 1) {
            ImageTool.popup(Alert.AlertType.WARNING, "Warning!", new StringBuilder()
                    .append("You must select only 1 image to apply the '")
                    .append("global threshold")
                    .append("' action.")
                    .toString());
            return result;
        }

        final Node node = (Node) action.getSource();

        var delta = ((Slider) node.getScene().lookup("#uValue")).getValue();
        ImageState imageState = states.get(0);
        t = imageState.getInitialT();
        //System.out.println(t);
        int lastT;
        do {
            lastT = t;
            double maxAvg = imageState.getMaxAvg(t);
            double minAvg = imageState.getMinAvg(t);
            t = (int) (maxAvg + minAvg) / 2;
            //System.out.println(t);

        } while (Math.abs(lastT - t) > delta);
        final Image image = imageState.unaryOp((n) -> n > t ? 255 : 0);
        final String key = ImageTool.buildKey("global_threshold", image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }

    @Override
    public Compressor getCompressor() {
        return Main.context.getBean(NullCompressor.class);
    }
}
