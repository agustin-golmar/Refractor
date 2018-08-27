package ar.nadezhda.refractor.handler;

import ar.nadezhda.refractor.core.Image;
import ar.nadezhda.refractor.core.ImageState;
import ar.nadezhda.refractor.core.ImageTool;
import ar.nadezhda.refractor.interfaces.Handler;
import javafx.scene.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncreaseContrastHandler implements Handler {
    @Override
    public Map<String, Image> handle(List<ImageState> states, Node node) {
        Map<String, Image> result = new HashMap<>();
        if (states.size()!=1){
            System.out.println("Una imagen solo");
        }
        ImageState imageState = states.get(0);
        final Image image = imageState.increaseContrast();
        final String key = ImageTool.buildKey("increasecontrast", image,
                states.get(0).getKey());
        result.put(key, image);
        return result;
    }
}
