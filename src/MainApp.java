import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
        Scene scene = new Scene(loader.load());
        
        // Không cần thêm CSS ở đây nữa vì đã được khai báo chính xác trong MainView.fxml.
        
        primaryStage.setTitle("Hệ Thống Quản Lý Điểm");
        primaryStage.setScene(scene);

        // Thiết lập kích thước tối thiểu cho cửa sổ
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(650);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}