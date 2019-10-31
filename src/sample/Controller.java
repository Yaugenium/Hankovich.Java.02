package sample;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class Controller {
    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Slider slider;

    @FXML
    private Canvas canvas;

    private GraphicsContext graphicsContext;

    @FXML
    private void initGraphicsContext(ActionEvent actionEvent) {
        if (graphicsContext == null) {
            canvas.setCursor(Cursor.CROSSHAIR);
            graphicsContext = canvas.getGraphicsContext2D();

            BoxBlur blur = new BoxBlur();
            blur.setWidth(1);
            blur.setHeight(1);
            blur.setIterations(1);

            graphicsContext.setLineCap(StrokeLineCap.ROUND);
            graphicsContext.setLineJoin(StrokeLineJoin.ROUND);
            graphicsContext.setEffect(blur);
        }

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    @FXML
    private void save(ActionEvent actionEvent) {
        if (graphicsContext == null) {
            return;
        }

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(new Stage());

        if (file == null) {
            return;
        }

        try {
            WritableImage writableImage = new WritableImage((int) canvas.getWidth(), (int) canvas.getHeight());
            canvas.snapshot(null, writableImage);
            RenderedImage renderedImage = SwingFXUtils.fromFXImage(writableImage, null);
            ImageIO.write(renderedImage, "png", file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @FXML
    private void load(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(new Stage());

        if (file != null) {
            Image image = new Image(file.toURI().toString());

            initGraphicsContext(new ActionEvent());

            graphicsContext.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
        }
    }

    @FXML
    private void press(MouseEvent mouseEvent) {
        if (graphicsContext == null) {
            return;
        }

        graphicsContext.setStroke(colorPicker.getValue());
        graphicsContext.setLineWidth(slider.getValue());

        graphicsContext.beginPath();
        graphicsContext.moveTo(mouseEvent.getX(), mouseEvent.getY());
        graphicsContext.stroke();
    }

    @FXML
    private void drag(MouseEvent mouseEvent) {
        if (graphicsContext == null) {
            return;
        }

        graphicsContext.lineTo(mouseEvent.getX(), mouseEvent.getY());
        graphicsContext.stroke();
    }
}
