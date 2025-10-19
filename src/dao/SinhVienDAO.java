package dao;

import model.SinhVien;
import util.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class SinhVienDAO {

    /**
     * Lấy toàn bộ danh sách sinh viên, đồng thời tính toán sẵn điểm tích lũy hệ 4.
     * Cách làm này hiệu quả hơn nhiều so với việc gọi vào CSDL cho từng sinh viên.
     */
    public static ObservableList<SinhVien> getAllSinhVien() {
        System.out.println("📡 Đang tải dữ liệu sinh viên từ MySQL...");
        ObservableList<SinhVien> list = FXCollections.observableArrayList();
        
        // Câu lệnh SQL phức tạp hơn để tính điểm tích lũy ngay tại CSDL
        String sql = "SELECT sv.*, COALESCE(gpa.diemTichLuyHe4, 0) AS diemTichLuy " +
                     "FROM sinhvien sv " +
                     "LEFT JOIN (" +
                     "    SELECT bd.maSV, " +
                     "           SUM(" +
                     "               CASE " +
                     "                   WHEN bd.diemTB >= 8.5 THEN 4.0 " +
                     "                   WHEN bd.diemTB >= 8.0 THEN 3.5 " +
                     "                   WHEN bd.diemTB >= 7.0 THEN 3.0 " +
                     "                   WHEN bd.diemTB >= 6.5 THEN 2.5 " +
                     "                   WHEN bd.diemTB >= 5.5 THEN 2.0 " +
                     "                   WHEN bd.diemTB >= 5.0 THEN 1.5 " +
                     "                   WHEN bd.diemTB >= 4.0 THEN 1.0 " +
                     "                   ELSE 0.0 " +
                     "               END * mh.soTinChi" +
                     "           ) / SUM(CASE WHEN bd.diemTB >= 4.0 THEN mh.soTinChi ELSE 0 END) AS diemTichLuyHe4 " +
                     "    FROM bangdiem bd " +
                     "    JOIN monhoc mh ON bd.maMon = mh.maMon " +
                     "    WHERE bd.diemTB >= 4.0 " +
                     "    GROUP BY bd.maSV" +
                     ") AS gpa ON sv.maSV = gpa.maSV";

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                SinhVien sv = new SinhVien(
                        rs.getString("maSV"),
                        rs.getString("hoTen"),
                        rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null,
                        rs.getString("gioiTinh"),
                        rs.getString("queQuan"),
                        rs.getString("khoa"),
                        rs.getString("lop")
                );
                sv.setDiemTichLuy(rs.getFloat("diemTichLuy")); // Gán điểm tích lũy đã tính
                list.add(sv);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ✅ Thêm sinh viên
    public static boolean insert(SinhVien sv) {
        String sql = "INSERT INTO sinhvien (maSV, hoTen, ngaySinh, gioiTinh, queQuan, khoa, lop) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sv.getMaSV());
            ps.setString(2, sv.getHoTen());
            if (sv.getNgaySinh() != null) {
                ps.setDate(3, java.sql.Date.valueOf(sv.getNgaySinh()));
            } else {
                ps.setNull(3, java.sql.Types.DATE);
            }
            ps.setString(4, sv.getGioiTinh());
            ps.setString(5, sv.getQueQuan());
            ps.setString(6, sv.getKhoa());
            ps.setString(7, sv.getLop());

            return ps.executeUpdate() > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("⚠️ Mã sinh viên đã tồn tại!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Cập nhật sinh viên
    public static boolean update(SinhVien sv) {
        String sql = "UPDATE sinhvien SET hoTen=?, ngaySinh=?, gioiTinh=?, queQuan=?, khoa=?, lop=? WHERE maSV=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sv.getHoTen());
             if (sv.getNgaySinh() != null) {
                ps.setDate(2, java.sql.Date.valueOf(sv.getNgaySinh()));
            } else {
                ps.setNull(2, java.sql.Types.DATE);
            }
            ps.setString(3, sv.getGioiTinh());
            ps.setString(4, sv.getQueQuan());
            ps.setString(5, sv.getKhoa());
            ps.setString(6, sv.getLop());
            ps.setString(7, sv.getMaSV());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Xóa sinh viên
    public static boolean delete(String maSV) {
        String sql = "DELETE FROM sinhvien WHERE maSV=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSV);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Lấy danh sách tất cả các lớp có trong CSDL.
     */
    public static ObservableList<String> getAllLop() {
        ObservableList<String> list = FXCollections.observableArrayList();
        String sql = "SELECT DISTINCT lop FROM sinhvien ORDER BY lop";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(rs.getString("lop"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

