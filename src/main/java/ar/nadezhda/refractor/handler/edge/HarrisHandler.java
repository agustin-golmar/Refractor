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
import javafx.scene.control.Alert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HarrisHandler implements Handler {

    private static final double K = 0.04;


    @Override
    public Map<String, Image> handle(List<ImageState> states, ActionEvent action) {
        final var result = new HashMap<String, Image>();
        if (states.size() != 1) {
            ImageTool.popup(Alert.AlertType.WARNING, "Warning!",
                    "You must select only 1 image to apply the Harris detection.");
            return result;
        }
        final var imageState = states.get(0);
        final var operator = ConvolutionHandler.getOperator();
        var image = imageState.getImage();
        var dx = operator.convolutionOverX(image.data);
        var dy = operator.convolutionOverY(image.data);

        var dx2 = ImageState.filter(new Image("dx2", Matrix.pow2(dx)),7,(i,j)->(1.0/(2*Math.PI*Math.pow(2.0,2)))*
                Math.exp(-(Math.pow(i,2)+Math.pow(j,2))/(2*Math.pow(2.0,2))),false);
        var dy2 = ImageState.filter(new Image("dy2", Matrix.pow2(dy)),7,(i,j)->(1.0/(2*Math.PI*Math.pow(2.0,2)))*
                Math.exp(-(Math.pow(i,2)+Math.pow(j,2))/(2*Math.pow(2.0,2))),false);

        var dxdy = ImageState.filter(new Image("dxy", Matrix.prod(dx,dy)),7,(i,j)->(1.0/(2*Math.PI*Math.pow(2.0,2)))*
                Math.exp(-(Math.pow(i,2)+Math.pow(j,2))/(2*Math.pow(2.0,2))),false);

        var ret = new Image(image.getSource(),getCim1(dx2.data,dy2.data,dxdy.data));

        final String key = ImageTool.buildKey("Harris",ret,states.get(0).getKey());
        result.put(key,ret);







        return result;
    }

    private double[][][] getCim1(double[][][] dx2, double[][][] dy2, double[][][] dxy) {
        var res = new double[dx2.length][dx2[0].length][dx2[0][0].length];

        for (int i=0;i<res.length;i++) {
            for (int j=0;j<res[0].length;j++) {
                for (int k=0;k<res[0][0].length;k++) {
                    res[i][j][k] = (dx2[i][j][k]*dy2[i][j][k]-dxy[i][j][k]*dxy[i][j][k])-
                            K*(dx2[i][j][k]+dy2[i][j][k])*(dx2[i][j][k]+dy2[i][j][k]);
                }
            }
        }
        return res;
    }

    @Override
    public Compressor getCompressor() {
        return Main.context.getBean(LinearCompressor.class);
    }
}
