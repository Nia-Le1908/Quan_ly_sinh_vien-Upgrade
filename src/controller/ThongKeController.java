package controller;

import dao.SinhVienDAO;
import dao.ThongKeDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import model.DiemChiTiet;
import model.SinhVien;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThongKeController {

    @FXML private ComboBox<SinhVien> cbSinhVien;
    @FXML private VBox contentVBox;

    private List<DiemChiTiet> dsDiem;

    @FXML
    public void initialize() {
        // Nạp danh sách sinh viên vào ComboBox
        cbSinhVien.setItems(SinhVienDAO.getAllSinhVien());
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Xem báo cáo".
     */
    @FXML
    private void xemThongKe() {
        SinhVien selectedSV = cbSinhVien.getSelectionModel().getSelectedItem();
        if (selectedSV == null) {
            contentVBox.getChildren().clear();
            contentVBox.getChildren().add(new Label("Vui lòng chọn một sinh viên."));
            return;
        }

        // Lấy toàn bộ điểm của sinh viên được chọn
        dsDiem = ThongKeDAO.getDiemChiTiet(selectedSV.getMaSV());
        taoGiaoDienBaoCao();
    }

    /**
     * Tự động tạo giao diện báo cáo dựa trên danh sách điểm đã lấy được.
     */
    private void taoGiaoDienBaoCao() {
        contentVBox.getChildren().clear();

        if (dsDiem.isEmpty()) {
            contentVBox.getChildren().add(new Label("Sinh viên này chưa có điểm."));
            return;
        }

        // Nhóm điểm theo từng học kỳ
        Map<Integer, List<DiemChiTiet>> diemTheoKy = dsDiem.stream()
                .collect(Collectors.groupingBy(DiemChiTiet::getHocKy));
        
        // Sửa lỗi: Chuyển sang dùng vòng lặp for-each truyền thống để tránh lỗi lambda
        // Lấy danh sách các kỳ và sắp xếp
        List<Integer> sortedHocKy = new ArrayList<>(diemTheoKy.keySet());
        Collections.sort(sortedHocKy);

        // Các biến để tính điểm tích lũy qua các kỳ
        double tongDiemTichLuy = 0;
        int tongTinChiTichLuy = 0;

        for (Integer hocKy : sortedHocKy) {
            List<DiemChiTiet> diemKyNay = diemTheoKy.get(hocKy);

            // --- Tính toán cho kỳ hiện tại ---
            double tongDiemKyNay = diemKyNay.stream()
                    .filter(d -> d.getDiemTB() >= 4.0) // Chỉ tính môn qua
                    .mapToDouble(d -> d.getDiemTBHe4() * d.getSoTinChi())
                    .sum();
            int tongTinChiKyNay = diemKyNay.stream()
                    .filter(d -> d.getDiemTB() >= 4.0)
                    .mapToInt(DiemChiTiet::getSoTinChi)
                    .sum();
            double gpaKyNay = (tongTinChiKyNay == 0) ? 0 : tongDiemKyNay / tongTinChiKyNay;

            // --- Cập nhật điểm tích lũy ---
            tongDiemTichLuy += tongDiemKyNay;
            tongTinChiTichLuy += tongTinChiKyNay;
            double cpaTichLuy = (tongTinChiTichLuy == 0) ? 0 : tongDiemTichLuy / tongTinChiTichLuy;

            // --- Tạo giao diện cho kỳ ---
            Label lblHocKy = new Label("HỌC KỲ " + hocKy);
            lblHocKy.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #34495e;");

            TableView<DiemChiTiet> tableKy = taoBangDiem();
            tableKy.setItems(FXCollections.observableArrayList(diemKyNay));
            
            Label lblGpa = new Label(String.format("=> Điểm trung bình học kỳ (Hệ 4): %.2f", gpaKyNay));
            Label lblCpa = new Label(String.format("=> Điểm tích lũy tính đến hết kỳ (Hệ 4): %.2f", cpaTichLuy));
            lblGpa.setStyle("-fx-font-weight: bold;");
            lblCpa.setStyle("-fx-font-weight: bold;");

            contentVBox.getChildren().addAll(lblHocKy, tableKy, lblGpa, lblCpa);
        }
    }

    /**
     * Helper method để tạo một TableView với các cột được định nghĩa sẵn.
     * @return Một TableView đã được cấu hình.
     */
    @SuppressWarnings({ "deprecation", "unchecked" })
    private TableView<DiemChiTiet> taoBangDiem() {
        TableView<DiemChiTiet> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<DiemChiTiet, String> colMaMon = new TableColumn<>("Mã Môn");
        colMaMon.setCellValueFactory(new PropertyValueFactory<>("maMon"));

        TableColumn<DiemChiTiet, String> colTenMon = new TableColumn<>("Tên Môn Học");
        colTenMon.setCellValueFactory(new PropertyValueFactory<>("tenMon"));
        colTenMon.setPrefWidth(250);

        TableColumn<DiemChiTiet, Integer> colTinChi = new TableColumn<>("Số TC");
        colTinChi.setCellValueFactory(new PropertyValueFactory<>("soTinChi"));

        TableColumn<DiemChiTiet, Float> colDiemQT = new TableColumn<>("Điểm QT");
        colDiemQT.setCellValueFactory(new PropertyValueFactory<>("diemQT"));

        TableColumn<DiemChiTiet, Float> colDiemThi = new TableColumn<>("Điểm Thi");
        colDiemThi.setCellValueFactory(new PropertyValueFactory<>("diemThi"));

        TableColumn<DiemChiTiet, Float> colDiemTB10 = new TableColumn<>("Điểm TB (10)");
        colDiemTB10.setCellValueFactory(new PropertyValueFactory<>("diemTB"));

        TableColumn<DiemChiTiet, Double> colDiemTB4 = new TableColumn<>("Điểm TB (4)");
        colDiemTB4.setCellValueFactory(new PropertyValueFactory<>("diemTBHe4"));

        tableView.getColumns().addAll(colMaMon, colTenMon, colTinChi, colDiemQT, colDiemThi, colDiemTB10, colDiemTB4);
        return tableView;
    }
}