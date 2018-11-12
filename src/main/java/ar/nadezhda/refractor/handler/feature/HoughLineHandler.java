package ar.nadezhda.refractor.handler.feature;

import ar.nadezhda.refractor.Main;
import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.handler.compression.LinearCompressor;
import ar.nadezhda.refractor.interfaces.Compressor;
import ar.nadezhda.refractor.interfaces.Handler;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.geometry.Point3D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class HoughLineHandler implements Handler {

	int width;
    int height;
    int maxValue;
    boolean circle;
    int accumAngles;
    int accumRos;

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
        //ImageTool.popup(Alert.AlertType.INFORMATION,"Info","Make sure you've selected a thresholded border image." +
          //      "Otherwise this won't work.");
        final var imageState = states.get(0);
        circle = ((CheckBox) Main.namespace.get("houghCircle")).isSelected();
        accumAngles = Integer.parseInt(((TextField) Main.namespace.get("accumAngles")).getText());
        accumRos = Integer.parseInt(((TextField) Main.namespace.get("accumRos")).getText());
        var image = imageState.getImage();
        width = image.data[0].length;
        height = image.data[0][0].length;
        Image imageRes = null;
        if (!circle) {
            var houghMatrix = accumulate(image.data);
            var lines = getMax(houghMatrix);
            var resMatrix = drawLines(lines);

            imageRes = new Image(image.getSource(), resMatrix);
        }
        else {
            var houghMatrix = circleAccumulate(image.data);
            var circles = getMaxCircles(houghMatrix);
            var resMatrix = drawCircles(circles);
            imageRes = new Image(image.getSource(), resMatrix);
            

        }
        final String key = ImageTool.buildKey("Hough", imageRes,
                states.get(0).getKey());
        result.put(key, imageRes);
        return result;


    }

    private double[][][] drawCircles(List<Point3D> circles) {
        final Stage stage = new Stage();
        final Pane root = new Pane();
        //final Scene scene = new Scene(root, width, height);
        //root.setAlignment(Pos.TOP_LEFT);
        for (Point3D c:circles){
            System.out.println(c.getX() + "||" + c.getY() + "||" + c.getZ());
            Circle cir = new Circle();
            cir.setCenterX(c.getX());
            cir.setCenterY(c.getY());
            cir.setRadius(c.getZ());
            cir.setFill(Color.TRANSPARENT);
            cir.setStroke(Color.RED);
            root.getChildren().add(cir);
        }
        final Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.show();
        var res = new double[3][width][height];
        for (int i=0;i<width;i++) {
            for (int j = 0; j < height; j++) {
                final int fuckJavaI = i;
                final int fuckJavaJ = j;
                if (circles.stream().anyMatch(p->Math.abs(
                        (fuckJavaI-p.getX())*(fuckJavaI-p.getX()) + (fuckJavaJ-p.getY())*(fuckJavaJ-p.getY()) - p.getZ()*p.getZ()
                )<=2)){
                    res[0][i][j]=255;
                }
            }
        }
        return res;
    }

    private List<Point3D> getMaxCircles(int[][][] houghMatrix) {
        var ret = new ArrayList<Point3D>();
        for (int i=0;i<houghMatrix.length;i++){
            for (int j=0;j<houghMatrix[0].length;j++){
                for (int k=0;k<houghMatrix[0][0].length;k++){
                    if (houghMatrix[i][j][k]>maxValue*0.6){
                        ret.add(new Point3D(i,j,k));
                    }
                }
            }
        }
        return ret;
    }

    private int[][][] circleAccumulate(double[][][] data) {
        var result = new int[data[0].length][data[0][0].length][(int)Math.sqrt(height*height+width*width)];
        for (int w=0;w<data[0].length;w++) {
            for (int h=0;h<data[0][0].length;h++){
                if (data[0][w][h]==255){
                    for (int x=0;x<result.length;x++){
                        for (int y=0;y<result[0].length;y++){
                            int r = getRadius(w,h,x,y);
                            if (r<result[0][0].length && r>5) {
                                result[x][y][r]++;
                                if (result[x][y][r]>maxValue)
                                    maxValue=result[x][y][r];
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private int getRadius(int w, int h, int x, int y) {
        return (int) Math.sqrt((w-x)*(w-x) + (h-y)*(h-y));
    }

    private double[][][] drawLines(List<Point2D> lines) {
/*        final Stage stage = new Stage();
        final Pane root = new Pane();
        //final Scene scene = new Scene(root, width, height);
        //root.setAlignment(Pos.TOP_LEFT);
        for (Point2D l:lines){
            //System.out.println(c.getX() + "||" + c.getY() + "||" + c.getZ());
            Line lin = new Line();
            root.getChildren().add(lin);
        }
        final Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.show();*/
        var res = new double[3][width][height];
        for (int i=0;i<width;i++){
            for (int j=0;j<height;j++){
                final int fuckJavaI = i;
                final int fuckJavaJ = j;
                if (lines.stream().anyMatch(p ->
                        Math.abs(fuckJavaI*Math.cos(p.getX()) + fuckJavaJ*Math.sin(p.getX()) - p.getY()) <=0.4)){
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
                if (houghMatrix[i][j]>maxValue*0.35) {
                    ret.add(getRoAndTheta(i,j));
                }
            }
        }
        return ret;
    }

    private Point2D getRoAndTheta(int i, int j) {
        return new Point2D.Double(Math.toRadians(i*180.0/(accumAngles-1)-90),
                j*(2*Math.hypot(width,height))/(accumRos-1) - Math.hypot(width,height));
    }

    private int[][] accumulate(double[][][] data) {
        var result = new int[accumAngles][accumRos];
        for (int w=0;w<data[0].length;w++){
            for (int h=0;h<data[0][0].length;h++){
                for (int m=0;m<result.length;m++) {
                    if (data[0][w][h]==255){
                        int n=getRo(w,h,m);
                        //System.out.println(n);
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
        double angle = Math.toRadians(m*180.0/(accumAngles-1)-90);
        double ro = w*Math.cos(angle)+h*Math.sin(angle);
        return (int)((ro + Math.hypot(width,height)) * (accumRos-1) / (2*Math.hypot(width,height)));
    }

    @Override
    public Compressor getCompressor() {
        return Main.context.getBean(LinearCompressor.class);
    }
}
