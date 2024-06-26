package com.taurus.mybatis.generator;

import com.taurus.mybatis.generator.controller.MainUIController;
import com.taurus.mybatis.generator.util.ConfigHelper;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * 这是本软件的主入口,要运行本软件请直接运行本类就可以了,不用传入任何参数
 * 本软件要求jkd版本大于1.8.0.40
 */
@Slf4j
public class MainUI extends Application {

	private static final Logger _LOG = LoggerFactory.getLogger(MainUI.class);

	@Override
	public void start(Stage primaryStage) throws Exception {
		ConfigHelper.createEmptyFiles();
		URL url = Thread.currentThread().getContextClassLoader().getResource("fxml/MainUI.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(url);
		Parent root = fxmlLoader.load();
		primaryStage.setResizable(true);
		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("【塔思】Mybatis逆向工程生成器v1.1.0");
		Image imageIcon = new Image("icons/mybatis-logo.png");
		primaryStage.getIcons().add(imageIcon);
		primaryStage.show();

		MainUIController controller = fxmlLoader.getController();
		controller.setPrimaryStage(primaryStage);
	}

	public static void main(String[] args) {
		log.info("开始执行软件...");
		launch(args);
	}

}
