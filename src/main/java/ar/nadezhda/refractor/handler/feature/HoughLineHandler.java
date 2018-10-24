package ar.nadezhda.refractor.handler.feature;

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

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HoughLineHandler implements Handler {


    int width;
    int height;
    int maxValue;

    @Override
    public Map<String, Image> handle(List<ImageState> states, ActionEvent action) {
        var result = new HashMap<String, Image>();
        if (states.size() != 1) {
            ImageTool.popup(Alert.AlertType.WARNING, "Warning!", new StringBuilder()
                    .append("You must select only 1 image to apply the '")
                    .append("Hough Line Transform")
                    .append("' action.")
                    .toString());
            return result;
        }
        ImageTool.popup(Alert.AlertType.INFORMATION,"Info","Make sure you've selected a thresholded border image." +
                "Otherwise this won't work.");
        final var imageState = states.get(0);
        var image = imageState.getImage();
        width = image.data[0].length;
        height = image.data[0][0].length;
        var houghMatrix = accumulate(image.data);
        var lines = getMax(houghMatrix);
        var resMatrix = drawLines(lines);

        var imageRes = new Image(image.getSource(),resMatrix);
        final String key = ImageTool.buildKey("Hough", imageRes,
                states.get(0).getKey());
        result.put(key, imageRes);
        return result;


    }

    private double[][][] drawLines(List<Point2D> lines) {
        var res = new double[3][width][height];
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                final int fuckJavaI = i;
                final int fuckJavaJ = j;
                if (lines.stream().anyMatch(p ->
                        Math.abs(fuckJavaI*Math.cos(p.getX()) + fuckJavaJ*Math.sin(p.getX()) - p.getY()) <0.5)){
                    res[0][i][j]=255;
                }
            }
        }
        return res;
    }

    private List<Point2D> getMax(int[][] houghMatrix) {
        var ret = new ArrayList<Point2D>();
        for (int i=0;i<houghMatrix.length;i++){
            for (int j=0;j<houghMatrix[0].length;j++){
                if (houghMatrix[i][j]>maxValue*0.8) {
                    ret.add(getRoAndTheta(i,j));
                }
            }
        }
        return ret;
    }

    private Point2D getRoAndTheta(int i, int j) {
        return new Point2D.Double(Math.toRadians(i*180.0/499-90),
                j*(2*Math.sqrt(2)*Math.max(width,height))/499 - Math.sqrt(2)*Math.max(width,height));
    }

    private int[][] accumulate(double[][][] data) {
        var result = new int[500][500];
        for (int w=0;w<data[0].length;w++){
            for (int h=0;h<data[0][0].length;h++){
                for (int m=0;m<result[0].length;m++) {
                    if (data[0][w][h]==255){
                        int n=getRo(w,h,m);
                        result[m][n]++;
                        if (result[m][n]>maxValue) {
                            maxValue = result[m][n];
                        }
                    }
                }
            }
        }
        return result;
    }

    private int getRo(int w, int h, int m) {
        double angle = Math.toRadians(m*180.0/499.0-90);
        double ro = w*Math.cos(angle)+h*Math.sin(angle);
        return (int)((ro + Math.sqrt(2)*Math.max(width,height)) * (499) / (2*Math.sqrt(2)*Math.max(width,height)));
    }

    @Override
    public Compressor getCompressor() {
        return Main.context.getBean(LinearCompressor.class);
    }
}
