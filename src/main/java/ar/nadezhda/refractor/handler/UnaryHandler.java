package ar.nadezhda.refractor.handler;

import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;

public abstract class UnaryHandler implements Handler {
    protected final String action;
    private final Random random;
    private boolean lognorm;
    protected DoubleUnaryOperator operation;
    protected boolean normalize;
    protected double scalar;
    private boolean truncate;

    public UnaryHandler(final String action, boolean normalize, boolean lognorm) {
        this.action = action;
        this.normalize= normalize;
        this.lognorm = lognorm;
        this.random = new Random();
    }

    @Override
    public Map<String, Image> handle(List<ImageState> states, Node node) {
        Map<String, Image> result = new HashMap<>();
        if (states.size()!=1){
            System.out.println("Una imagen solo");
        }
        TextField textField = (TextField) node.getScene().lookup("#scalar");
        CheckBox lognormBox = (CheckBox) node.getScene().lookup("#dynamicRange");
        CheckBox linearBox = (CheckBox) node.getScene().lookup("#linearCompression");
        CheckBox truncBox = (CheckBox) node.getScene().lookup("#truncate");

        lognorm = lognormBox.isSelected();
        normalize = linearBox.isSelected();
        truncate = truncBox.isSelected();
        try {
            scalar = Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Not a number");
            return result;
        }
        ImageState imageState = states.get(0);
        generateOperation();


        final Image image = imageState.unaryOp(operation, normalize, lognorm,truncate);
        final String key = ImageTool.buildKey(action, image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }

    private void generateOperation() {
        switch (action) {
            case "scalarprod":
                operation = (n) -> n*scalar;
                break;
            case "negative":
                operation = (n) -> 255.0-n;
                break;
            case "threshold":
                operation = (n) -> n>scalar?255:0;
                break;
            case "power":
                operation = (n) -> Math.pow(255,1-scalar)*Math.pow(n,scalar);
                break;
            case "gaussiannoise":
                operation = (n) -> n+random.nextGaussian()*255*scalar;
                break;
            case "exponentialnoise":
                operation = (n) -> n*Math.log(random.nextDouble())*(-1/scalar);
                break;
            case "saltandpepper":
                operation = (n) -> random.nextDouble()<scalar?(random.nextDouble()>0.5?255:0):n;
                break;
            case "rayleighnoise":
                operation = (n) ->n*scalar*Math.sqrt(-2*Math.log((1-random.nextDouble())));
                break;
        }
    }
}
