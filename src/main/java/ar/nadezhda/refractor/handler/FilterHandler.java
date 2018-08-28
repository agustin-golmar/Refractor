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
import java.util.function.DoubleBinaryOperator;

public abstract class FilterHandler implements Handler {
    protected DoubleBinaryOperator operation;
    protected final String action;
    protected boolean normalize;

    public FilterHandler(String action, boolean normalize) {
        this.action=action;
        this.normalize=normalize;
    }

    @Override
    public Map<String, Image> handle(List<ImageState> states, Node node) {
        Map<String, Image> result = new HashMap<>();
        if (states.size()!=1){
            System.out.println("Una imagen solo");
        }
        int dimension;
        double stdDev;
        TextField textField = (TextField) node.getScene().lookup("#scalar");
        TextField textField2 = (TextField) node.getScene().lookup("#scalar2");
        try {
            dimension = Integer.parseInt(textField.getText());
            stdDev = Double.parseDouble(textField2.getText());
        } catch (NumberFormatException e) {
            System.out.println("Not a number");
            return result;
        }
        if (dimension%2==0) {
            System.out.println("Solo impar");
            return result;
        }
        generateOperation(stdDev,dimension);
        ImageState imageState = states.get(0);
        final Image image = imageState.filter(dimension, operation,normalize);
        final String key = ImageTool.buildKey(action, image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }

    private void generateOperation(double stdDev, int dimension) {
        switch (action) {
            case "meanfilter":
                operation = (i,j)->1.0;
                break;
            case "gaussfilter":
                operation = (i,j)->(1.0/(2*Math.PI*Math.pow(stdDev,2)))*
                        Math.exp(-(Math.pow(i,2)+Math.pow(j,2))/Math.pow(stdDev,2));
                break;
            case "highpassfilter":
                operation = (i,j)->(i==0&&j==0?(dimension*dimension-1.0)/(dimension*dimension):-1.0/(dimension*dimension));
                break;
        }
    }
}
