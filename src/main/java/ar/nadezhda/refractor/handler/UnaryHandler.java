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
import java.util.function.DoubleUnaryOperator;

public class UnaryHandler implements Handler {
    protected final String action;
    protected DoubleUnaryOperator operation;
    protected boolean dynamicRange;
    protected double scalar;

    public UnaryHandler(final String action, boolean dynamicRange) {
        this.action = action;
        this.dynamicRange = dynamicRange;
    }

    @Override
    public Map<String, Image> handle(List<ImageState> states, Node node) {
        Map<String, Image> result = new HashMap<>();
        if (states.size()!=1){
            System.out.println("Una imagen solo");
        }
        TextField textField = (TextField) node.getScene().lookup("#scalar");
        try {
            scalar = Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            System.out.println("Not a number");
            return result;
        }
        generateOperation();


        final Image image = states.get(0).unaryOp(operation, dynamicRange);
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
        }
    }
}
