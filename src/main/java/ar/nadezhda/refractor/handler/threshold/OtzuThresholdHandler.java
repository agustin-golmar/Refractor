package ar.nadezhda.refractor.handler.threshold;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.NullCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtzuThresholdHandler implements Handler {

    private int maxColor;
    @Override
    public Map<String, Image> handle(List<ImageState> states, ActionEvent action) {
        var result = new HashMap<String, Image>();
        if (states.size() != 1) {
            ImageTool.popup(Alert.AlertType.WARNING, "Warning!", new StringBuilder()
                    .append("You must select only 1 image to apply the '")
                    .append("Otzu threshold")
                    .append("' action.")
                    .toString());
            return result;
        }
        ImageState imageState = states.get(0);
        var cumHist = imageState.getImage().getCummulativeHistogram()[0];
        var cumMean = imageState.getImage().getCummulativeMean()[0];
        int maxCount=1;
        double maxVar=0;
        maxColor=0;
        double globalMean = cumMean[255];
        for (int i=0;i<cumHist.length;i++){
            double variance = Math.pow(globalMean*cumHist[i]-cumMean[i],2)/(cumHist[i]*(1-cumHist[i]));
            if (variance>maxVar){
                maxCount=1;
                maxColor=i;
                maxVar=variance;
            }
            else if (variance==maxVar){
                maxCount++;
                maxColor+=i;
            }
        }
        maxColor/=maxCount;
        final Image image = imageState.unaryOp((n) -> n > maxColor ? 255 : 0);
        final String key = ImageTool.buildKey("otzu_threshold", image,
                states.get(0).getKey());
        result.put(key, image);
        return result;




    }

    @Override
    public Compressor getCompressor() {
        return Main.context.getBean(NullCompressor.class);
    }
}
