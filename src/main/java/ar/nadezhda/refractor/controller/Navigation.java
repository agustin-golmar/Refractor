
	package ar.nadezhda.refractor.controller;

	import javafx.embed.swing.SwingFXUtils;
    import javafx.event.ActionEvent;
	import javafx.fxml.FXML;
    import javafx.scene.Scene;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.image.WritableImage;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.stage.Stage;

    import javax.imageio.ImageIO;
    import java.io.*;

    public class Navigation {

		@FXML
		protected void clickButton(final ActionEvent event) {
			System.out.println("click");
		}

		@FXML
        protected void whiteSquare(final ActionEvent event) {
            WritableImage image = new WritableImage(300,300);

            for (int i=0;i<300;i++) {
                for (int j=0;j<300;j++) {
                    if (i<100 || i>200 || j<100 || j>200) {
                        image.getPixelWriter().setColor(i,j,Color.BLACK);
                    }
                }
            }
            ImageView imageView = new ImageView();
            imageView.setImage(image);

            // Display image on screen
            StackPane root = new StackPane();
            root.getChildren().add(imageView);
            Scene scene = new Scene(root, 300, 250);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

        }
	}
