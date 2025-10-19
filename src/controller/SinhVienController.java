package controller;

import dao.SinhVienDAO;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import model.SinhVien;

import java.time.LocalDate;

public class SinhVienController {

    @FXML private TextField tfMaSV, tfHoTen, tfQueQuan, tfKhoa, tfLop;
    @FXML private DatePicker dpNgaySinh;
    @FXML private ComboBox<String> cbGioiTinh;
    @FXML private TableView<SinhVien> tableSV;
    @FXML private TableColumn<SinhVien, String> colMaSV, colHoTen, colGioiTinh, colQueQuan, colKhoa, colLop;
    @FXML private TableColumn<SinhVien, LocalDate> colNgaySinh;

    private ObservableList<SinhVien> dsSV;

    @FXML
    public void initialize() {
        System.out.println("✅ SinhVienController initialized");
        cbGioiTinh.getItems().addAll("Nam", "Nữ");
        colMaSV.setCellValueFactory(data -> data.getValue().maSVProperty());
        colHoTen.setCellValueFactory(data -> data.getValue().hoTenProperty());
        colNgaySinh.setCellValueFactory(data -> data.getValue().ngaySinhProperty());
        colGioiTinh.setCellValueFactory(data -> data.getValue().gioiTinhProperty());
        colQueQuan.setCellValueFactory(data -> data.getValue().queQuanProperty());
        colKhoa.setCellValueFactory(data -> data.getValue().khoaProperty());
        colLop.setCellValueFactory(data -> data.getValue().lopProperty());

        loadData();
    }

    private void loadData() {
        dsSV = SinhVienDAO.getAllSinhVien();
        tableSV.setItems(dsSV);
    }

    @FXML
    private void themSinhVien() {
        SinhVien sv = new SinhVien(
                tfMaSV.getText(),
                tfHoTen.getText(),
                dpNgaySinh.getValue(),
                cbGioiTinh.getValue(),
                tfQueQuan.getText(),
                tfKhoa.getText(),
                tfLop.getText()
        );
        if (SinhVienDAO.insert(sv)) {
            loadData();
            clearForm();
        }
    }

    @FXML
    private void suaSinhVien() {
        SinhVien sv = tableSV.getSelectionModel().getSelectedItem();
        if (sv != null) {
            sv.setHoTen(tfHoTen.getText());
            sv.setNgaySinh(dpNgaySinh.getValue());
            sv.setGioiTinh(cbGioiTinh.getValue());
            sv.setQueQuan(tfQueQuan.getText());
            sv.setKhoa(tfKhoa.getText());
            sv.setLop(tfLop.getText());

            if (SinhVienDAO.update(sv)) {
                loadData();
            }
        }
    }

    @FXML
    private void xoaSinhVien() {
        SinhVien sv = tableSV.getSelectionModel().getSelectedItem();
        if (sv != null && SinhVienDAO.delete(sv.getMaSV())) {
            loadData();
        }
    }

    private void clearForm() {
        tfMaSV.clear();
        tfHoTen.clear();
        dpNgaySinh.setValue(null);
        cbGioiTinh.setValue(null);
        tfQueQuan.clear();
        tfKhoa.clear();
        tfLop.clear();
    }
}
