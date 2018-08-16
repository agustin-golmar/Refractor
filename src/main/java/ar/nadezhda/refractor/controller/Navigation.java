
	package ar.nadezhda.refractor.controller;

	import javafx.embed.swing.SwingFXUtils;
    import javafx.event.ActionEvent;
	import javafx.fxml.FXML;
    import javafx.scene.Node;
    import javafx.scene.Scene;
    import javafx.scene.image.Image;
    import javafx.scene.image.ImageView;
    import javafx.scene.image.PixelFormat;
    import javafx.scene.image.WritableImage;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.stage.Stage;

    import javax.imageio.ImageIO;
    import java.io.*;
    import java.util.Arrays;
    import java.util.Scanner;

    public class Navigation {

        private static final int BARCO_RES =290*207;
        private double startX, startY;

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
                        image.getPixelWriter().setColor(i,j,Color.RED);

                    }
                    else {
                        image.getPixelWriter().setColor(i,j,Color.BLUE);
                    }
                }
            }
            ImageView imageView = new ImageView();
            imageView.setImage(image);
            imageView.setOnMouseDragged(e -> {
                        if (e.isShiftDown()) {
                            System.out.println(e.getX() + " " + e.getY());
                            image.getPixelWriter().setColor((int) e.getX(), (int) e.getY(), Color.RED);
                        }
                    });

            imageView.setOnMousePressed(e -> {
                System.out.println("Rect Start: "+e.getX() + " " + e.getY());
                startX=e.getX();
                startY=e.getY();
                //System.out.println("B: "+image.getPixelReader().getColor((int)e.getX(),(int)e.getY()).getBlue());
            });

            getAvgColors(image, imageView);

            // Display image on screen
            StackPane root = new StackPane();
            root.getChildren().add(imageView);
            Scene scene = new Scene(root, 300, 300);
            //Scene scene= ((Node)event.getSource()).getScene();
            //scene.setRoot(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();

        }

        @FXML
        protected void loadBarco(final ActionEvent event) {
            try(FileInputStream fs = new FileInputStream("src/main/resources/BARCO.RAW")) {
                byte[] imageBytes = new byte[BARCO_RES];
                fs.read(imageBytes);
                WritableImage image = new WritableImage(290,207);
                int k=0;
                //System.out.println(imageBytes.length);
                //System.out.println(Arrays.toString(imageBytes));
                for (int h=0;h<207;h++){
                    for (int w=0;w<290;w++){

                        image.getPixelWriter().setColor(w,h,Color.grayRgb(Byte.toUnsignedInt(imageBytes[k])));
                        k++;
                    }
                }
                //image.getPixelWriter().setPixels(0,0,290,207,PixelFormat.getByteBgraPreInstance(),imageBytes,0,290);
                ImageView imageView = new ImageView();
                imageView.setImage(image);

                imageView.setOnMouseDragged(e -> {
                    if (e.isShiftDown()) {
                        System.out.println(e.getX() + " " + e.getY());
                        image.getPixelWriter().setColor((int) e.getX(), (int) e.getY(), Color.RED);
                    }
                });

                imageView.setOnMousePressed(e -> {
                    System.out.println("Rect Start: "+e.getX() + " " + e.getY());
                    startX=e.getX();
                    startY=e.getY();
                    //System.out.println("B: "+image.getPixelReader().getColor((int)e.getX(),(int)e.getY()).getBlue());
                });

                getAvgColors(image, imageView);
                StackPane root = new StackPane();
                root.getChildren().add(imageView);
                Scene scene = new Scene(root, 290, 207);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void getAvgColors(WritableImage image, ImageView imageView) {
            imageView.setOnMouseReleased(e -> {
                System.out.println("Rect Finish: "+e.getX() + " " + e.getY());
                double area = Math.abs(e.getX()-startX)*Math.abs(e.getY()-startY);
                System.out.println("Area: "+area);
                double totalR=0;
                double totalG=0;
                double totalB=0;
                for (int x = (int)Math.min(e.getX(),startX);x<Math.max(e.getX(),startX);x++) {
                    for (int y = (int)Math.min(e.getY(),startY);y<Math.max(e.getY(),startY);y++) {
                        totalR+=image.getPixelReader().getColor(x,y).getRed();
                        totalG+=image.getPixelReader().getColor(x,y).getGreen();
                        totalB+=image.getPixelReader().getColor(x,y).getBlue();
                    }
                }
                System.out.println("Avg red: "+totalR/area);
                System.out.println("Avg green: "+totalG/area);
                System.out.println("Avg blue: "+totalB/area);
            });
        }

        @FXML
        protected void loadPGM (final ActionEvent event) {
            try (RandomAccessFile rf = new RandomAccessFile("src/main/resources/TEST.PGM","r")){


                if (!rf.readLine().equals("P5")) {
                    throw new IllegalArgumentException();
                }

                String[] widthHeight = rf.readLine().split(" ");
                int width = Integer.parseInt(widthHeight[0]);
                int height = Integer.parseInt(widthHeight[1]);


                System.out.println(width + "x" + height);
                String maxVal = rf.readLine();
                byte[] imageBytes = new byte[width*height];
                System.out.println("Lei: "+rf.read(imageBytes));
                WritableImage image = new WritableImage(width,height);

                int k=0;
                //System.out.println(imageBytes.length);
                //System.out.println(Arrays.toString(imageBytes));
                for (int h=0;h<height;h++){
                    for (int w=0;w<width;w++){

                        image.getPixelWriter().setColor(w,h,Color.grayRgb(Byte.toUnsignedInt(imageBytes[k])));
                        k++;
                    }
                }

                ImageView imageView = new ImageView();
                imageView.setImage(image);

                imageView.setOnMousePressed(e -> {
                    System.out.println("Rect Start: "+e.getX() + " " + e.getY());
                    startX=e.getX();
                    startY=e.getY();
                    //System.out.println("B: "+image.getPixelReader().getColor((int)e.getX(),(int)e.getY()).getBlue());
                });

                getAvgColors(image, imageView);
                StackPane root = new StackPane();
                root.getChildren().add(imageView);
                Scene scene = new Scene(root, width, height);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
	}
