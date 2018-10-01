package ar.nadezhda.refractor.core;

import java.awt.Point;
import java.util.Arrays;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ImageState {

    protected final Rectangle area;
    protected final ImageView view;
    protected final Image image;
    protected final Pane root;
    protected final Point anchor;
    protected final String key;
    protected double windowSum;

    public ImageState(final String key, final ImageView view, final Image image) {
        this.key = key;
        this.anchor = new Point(0, 0);
        this.area = new Rectangle();
        this.area.setMouseTransparent(true);
        this.area.setFill(Color.rgb(255, 255, 255, 0.1));
        this.area.setStroke(Color.BLACK);
        this.area.getStrokeDashArray().add(5.0);
        this.area.setManaged(false);
        this.root = (Pane) view.getParent();
        this.root.getChildren().add(area);
        this.view = view;
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public ImageView getView() {
        return view;
    }

    public Image getImage() {
        return image;
    }

    public Pane getRoot() {
        return root;
    }

    public ImageState setStartArea(final double x, final double y) {
        anchor.setLocation(
                Math.max(0, Math.min(x, image.getWidth())),
                Math.max(0, Math.min(y, image.getHeight())));
        area.setX(anchor.x);
        area.setY(anchor.y);
        return resetArea();
    }

    public ImageState updateArea(final double x, final double y) {
        final double sx = Math.max(0, Math.min(x, image.getWidth()));
        final double sy = Math.max(0, Math.min(y, image.getHeight()));
        area.setX(Math.min(sx, anchor.x));
        area.setWidth(Math.abs(anchor.x - sx));
        area.setY(Math.min(sy, anchor.y));
        area.setHeight(Math.abs(anchor.y - sy));
        return this;
    }

    public ImageState resetArea() {
        area.setWidth(0);
        area.setHeight(0);
        return this;
    }

    public int getXArea() {
        return (int) area.getWidth();
    }

    public int getYArea() {
        return (int) area.getHeight();
    }

    public int getPixelCount() {
        return getXArea() * getYArea();
    }

    public Image getSelectedImage() {
        final byte[][][] raw = new byte[image.getChannels()][getXArea()][getYArea()];
        final int x = (int) area.getX();
        final int y = (int) area.getY();
        final int wEnd = (int) (x + area.getWidth());
        final int hEnd = (int) (y + area.getHeight());
        for (int c = 0; c < image.getChannels(); ++c)
            for (int h = y; h < hEnd; ++h)
                for (int w = x; w < wEnd; ++w) {
                    raw[c][w - x][h - y] = image.rawData[c][w][h];
                }
        return new Image(image.getSource(), raw);
    }

    public double[] getRGBAverageOnArea() {
        final double[] avg = {0, 0, 0};
        final double area = getPixelCount();
        final int x = (int) this.area.getX();
        final int y = (int) this.area.getY();
        final int wEnd = (int) (x + this.area.getWidth());
        final int hEnd = (int) (y + this.area.getHeight());
        if (image.getChannels() == 3) {
            for (int h = y; h < hEnd; ++h) {
                for (int w = x; w < wEnd; ++w) {
                    avg[0] += image.red()[w][h];
                    avg[1] += image.green()[w][h];
                    avg[2] += image.blue()[w][h];
                }
            }
        } else {
            for (int h = y; h < hEnd; ++h) {
                for (int w = x; w < wEnd; ++w) {
                    avg[0] += image.gray()[w][h];
                }
            }
            avg[1] = avg[0];
            avg[2] = avg[0];
        }
        if (0 < area) {
            avg[0] /= area;
            avg[1] /= area;
            avg[2] /= area;
        } else {
            avg[0] = avg[1] = avg[2] = 0;
        }
        return avg;
    }

    public Image operate(final ImageState other, final DoubleBinaryOperator op) {
        if (this.image.getHeight() != other.image.getHeight()
                || this.image.getWidth() != other.image.getWidth()
                || this.image.getChannels() != other.image.getChannels()) {
            throw new IllegalArgumentException("Mismo tamaño por favor.");
        }
        double[] minData = new double[this.image.getChannels()];
        double[] maxData = new double[this.image.getChannels()];
        for (int i = 0; i < this.image.getChannels(); i++) {
            minData[i] = op.applyAsDouble(this.image.data[i][0][0], other.image.data[i][0][0]);
            maxData[i] = minData[i];
        }
        //double minData = this.image.data[0][0][0] + other.image.data[0][0][0];
        //double maxData = minData;
        Image res = new Image(
                this.image.source + "_" + other.image.source,
                this.image.getChannels(),
                this.image.getWidth(), this.image.getHeight());
        for (int c = 0; c < this.image.getChannels(); c++) {
            for (int w = 0; w < this.image.getWidth(); w++) {
                for (int h = 0; h < this.image.getHeight(); h++) {
                    res.data[c][w][h] = op.applyAsDouble(
                            this.image.data[c][w][h],
                            other.image.data[c][w][h]);
                    if (res.data[c][w][h] > maxData[c])
                        maxData[c] = res.data[c][w][h];
                    else if (res.data[c][w][h] < minData[c])
                        minData[c] = res.data[c][w][h];
                }
            }
        }
        return res;
    }

    public Image unaryOp(DoubleUnaryOperator op) {
        /*double[] maxData2 = new double[image.getChannels()];
        double[] minData2 = new double[image.getChannels()];
        for (int i=0;i<image.getChannels();i++) {
            maxData2[i]=255;
            minData2[i]=0;
        }*/
        Image res = new Image(this.image.source, this.image.getChannels(), this.image.getWidth(), this.image.getHeight());
        for (int c = 0; c < image.getChannels(); c++) {
            for (int w = 0; w < image.getWidth(); w++) {
                for (int h = 0; h < image.getHeight(); h++) {
                    res.data[c][w][h] = op.applyAsDouble(image.data[c][w][h]);
                    //if (res.data[c][w][h]>maxData
                    /*if (res.data[c][w][h]>maxData2[c])
                        maxData2[c]=res.data[c][w][h];
                    else if (res.data[c][w][h]<minData2[c])
                        minData2[c]=res.data[c][w][h];*/
                }
            }
        }
        return res;
    }

    public Image increaseContrast(double scalar) {
        double[] mean = getMean();
        double[] stdDev = getStdDev();
        Image res = new Image(this.image.source, this.image.getChannels(), this.image.getWidth(), this.image.getHeight());
        for (int c=0;c<image.getChannels();c++){
            for (int w=0;w<image.getWidth();w++){
                for (int h=0;h<image.getHeight();h++){
                    res.data[c][w][h] = 255*(image.data[c][w][h]-(mean[c]-stdDev[c]/scalar))/(2*stdDev[c]/scalar);
                    //System.out.println(res.data[c][w][h]);
                    if (res.data[c][w][h]<0){
                        res.data[c][w][h]=0;
                    }

                    else if (res.data[c][w][h]>255){
                        res.data[c][w][h]=255;
                    }
                    res.rawData[c][w][h] = (byte) res.data[c][w][h];
                }
            }
        }

        return res;

    }

    public Image filter(int size, DoubleBinaryOperator matrixFiller, boolean normalize){
        double[][] window = new double[size][size];
        double[] maxData2 = new double[image.getChannels()];
        double[] minData2 = new double[image.getChannels()];
        for (int i=0;i<image.getChannels();i++) {
            maxData2[i]=255;
            minData2[i]=0;
        }
        double sum = 0.0;
        for (int i=0;i<size;i++) {
            for (int j=0;j<size;j++) {
                double val = matrixFiller.applyAsDouble(i-size/2,j-size/2);
                window[i][j]= val;
                //System.out.print(window[i][j]+ " ");
                sum+=val;
                
            }
            //System.out.println("");


        }
        if (normalize) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    window[i][j] /= sum;

                }


            }
        }
        //System.out.println("------------------------");
        Image res = new Image(this.image.source, this.image.getChannels(), this.image.getWidth(), this.image.getHeight());
        for (int c=0;c<image.getChannels();c++){
            for (int w=0;w<image.getWidth();w++){
                for (int h=0;h<image.getHeight();h++){
                    res.data[c][w][h] = 0;
                        for (int i=w-size/2,i2=0;i<=w+size/2;i++,i2++) {
                            for (int j=h-size/2,j2=0;j<=h+size/2;j++,j2++) {
                                try {
                                    res.data[c][w][h] += window[i2][j2] * image.data[c][i][j];
                                } catch(IndexOutOfBoundsException e){
                                    res.data[c][w][h]+=0;
                                }
                            }
                        }
                    if (res.data[c][w][h]>maxData2[c]){
                        maxData2[c]=res.data[c][w][h];
                    }
                    else if (res.data[c][w][h]<minData2[c]){
                        minData2[c]=res.data[c][w][h];
                    }
                }
            }
        }
        return res;
    }

    public double[] getMean(){
        return image.getMean();
    }

    public double[] getStdDev() {
        return image.getStdDev();
    }

    public Image medianFilter(int dimension, boolean ponderate) {
        double[] window;
        if (!ponderate)
            window = new double[dimension*dimension];
        else
            window = new double[16];

        Image res = new Image(this.image.source, this.image.getChannels(), this.image.getWidth(), this.image.getHeight());
        for (int c=0;c<image.getChannels();c++){
            for (int w=dimension/2;w<image.getWidth()-dimension/2;w++){
                for (int h=dimension/2;h<image.getHeight()-dimension/2;h++){
                    int i2=0;
                    for (int i=w-dimension/2;i<=w+dimension/2;i++) {
                        for (int j=h-dimension/2;j<=h+dimension/2;j++) {
                            window[i2++]= image.data[c][i][j];
                            if (ponderate && (i==w || j==h))
                                window[i2++]= image.data[c][i][j];
                            if (ponderate && i==w && j==h) {
                                window[i2++]= image.data[c][i][j];
                                window[i2++]= image.data[c][i][j];
                            }
                        }
                    }
                    Arrays.sort(window);
                    res.data[c][w][h]=window.length%2==1?window[window.length/2]:window[window.length/2]/2+window[window.length/2+1]/2;

                    res.rawData[c][w][h] = (byte) res.data[c][w][h];

                }
            }
        }
        return res;
    }

    public Image bilinearFilter(int dimension, double sigmar) {
        var imageMatrix = new double[image.getChannels()][image.getWidth()][image.getHeight()];
        for (int c=0;c<imageMatrix.length;c++) {
            for (int w=0;w<imageMatrix[0].length;w++){
                for (int h=0;h<imageMatrix[0][0].length;h++){
                    var window = generateBilinearWindow(dimension,sigmar,c,w,h);
                    for (int i=0;i<window.length;i++){
                        for (int j=0;j<window[0].length;j++){
                            imageMatrix[c][w][h]+=window[i][j];
                        }
                    }
                    imageMatrix[c][w][h]/=windowSum;
                    //System.out.println("Original: "+image.data[c][w][h]+" || Cambio: "+imageMatrix[c][w][h]);

                }
            }
        }
        return new Image(image.source,imageMatrix);
    }

    private double[][] generateBilinearWindow(int dimension, double sigmar,int c, int w, int h) {
        var window = new double[dimension][dimension];
        windowSum=0;
        for (int i=0;i<dimension;i++){
            for (int j=0;j<dimension;j++){
                var base = dimension/2;
                final var ew = w - base + i;
                final var eh = h - base + j;
                if (-1 < ew && ew < image.getWidth() && -1 < eh && eh < image.getHeight()){
                    window[i][j]=Math.exp(
                            - (((w-ew)*(w-ew)) + ((h-eh)*(h-eh)))/
                            (2.0*(dimension/3)*(dimension/3)) -
                            Math.pow(Math.abs(image.data[c][w][h]-image.data[c][ew][eh]),2)/
                                    (2*sigmar*sigmar)
                    );
                    windowSum+=window[i][j];
                    window[i][j]*=image.data[c][ew][eh];
                }
            }
        }
        return window;
    }

    public Image anisotropicFilter(int steps, boolean isotropic) {
        var ret = new double[image.getChannels()][image.getWidth()][image.getHeight()];
        var temp = new double[image.getChannels()][image.getWidth()][image.getHeight()];
        matrixCopy(image.data,ret);
        for (var i=0;i<steps;i++){
            matrixCopy(ret,temp);
            for (var c=0;c<ret.length;c++){
                for (var w=0;w<ret[0].length;w++){
                    for (var h=0;h<ret[0][0].length;h++){
                        var dn=h<ret[0][0].length-1?temp[c][w][h+1]-temp[c][w][h]:0;
                        var ds=h>0?temp[c][w][h-1]-temp[c][w][h]:0;
                        var de=w<ret[0].length-1?temp[c][w+1][h]-temp[c][w][h]:0;
                        var dw=w>0?temp[c][w-1][h]-temp[c][w][h]:0;
                        if (isotropic){
                            ret[c][w][h]=temp[c][w][h]+0.25*(dn+ds+de+dw);
                        } else {
                            ret[c][w][h] = temp[c][w][h] + 0.25 * (dn * Math.exp((-dn) * dn / ((i+1)*(i+1))) +
                                    ds * Math.exp((-ds) * ds / ((i+1)*(i+1))) +
                                    de * Math.exp((-de) * de / ((i+1)*(i+1))) +
                                    dw * Math.exp((-dw) * dw / ((i+1)*(i+1))));
                            //System.out.println(ret[c][w][h]);
                        }
                    }
                }
            }
        }
        return new Image(image.source,ret);

    }

    private void matrixCopy(double[][][] m1, double[][][] m2) {
        for (var i =0;i<m1.length;i++){
            for (var j=0;j<m1[0].length;j++){
                for (var k=0;k<m1[0][0].length;k++){
                    m2[i][j][k]=m1[i][j][k];
                }
            }
        }
    }

    public int getInitialT() {
        var max = image.data[0][0][0];
        var min = max;
        for (var c=0;c<image.getChannels();c++){
            for (var w=0;w<image.getWidth();w++){
                for (var h=0;h<image.getHeight();h++){
                    if (image.data[c][w][h]>max)
                        max=image.data[c][w][h];
                    else if (image.data[c][w][h]<min)
                        min=image.data[c][w][h];
                }
            }
        }
        return (int)(max-min)/2;
    }

    public double getMaxAvg(int t) {
        var sum=0;
        var count=0;
        for (var c=0;c<image.getChannels();c++){
            for (var w=0;w<image.getWidth();w++){
                for (var h=0;h<image.getHeight();h++){
                    if (image.data[c][w][h]>t){
                        count++;
                        sum+=image.data[c][w][h];
                    }
                }
            }
        }
        return sum/count;
    }

    public double getMinAvg(int t) {
        var sum=0;
        var count=0;
        for (var c=0;c<image.getChannels();c++){
            for (var w=0;w<image.getWidth();w++){
                for (var h=0;h<image.getHeight();h++){
                    if (image.data[c][w][h]<=t){
                        count++;
                        sum+=image.data[c][w][h];
                    }
                }
            }
        }
        return sum/count;
    }
}
