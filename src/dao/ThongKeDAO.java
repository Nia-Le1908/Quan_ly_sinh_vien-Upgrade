package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.DiemChiTiet;
import util.DBConnection;

public class ThongKeDAO {

    /**
     * Lấy toàn bộ bảng điểm chi tiết của một sinh viên.
     * @param maSV Mã sinh viên cần xem điểm.
     * @return Danh sách điểm chi tiết.
     */
    public static List<DiemChiTiet> getDiemChiTiet(String maSV) {
        List<DiemChiTiet> list = new ArrayList<>();
        String sql = "SELECT sv.maSV, sv.hoTen, mh.maMon, mh.tenMon, mh.soTinChi, mh.hocKy, bd.diemQT, bd.diemThi, bd.diemTB " +
                     "FROM sinhvien sv " +
                     "JOIN bangdiem bd ON sv.maSV = bd.maSV " +
                     "JOIN monhoc mh ON bd.maMon = mh.maMon " +
                     "WHERE sv.maSV = ? " +
                     "ORDER BY mh.hocKy";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSV);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new DiemChiTiet(
                        rs.getString("maSV"),
                        rs.getString("hoTen"),
                        rs.getString("maMon"), // Sửa lỗi: từ "maMH" thành "maMon"
                        rs.getString("tenMon"),
                        rs.getInt("soTinChi"),
                        rs.getInt("hocKy"),
                        rs.getFloat("diemQT"),
                        rs.getFloat("diemThi"),
                        rs.getFloat("diemTB")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}