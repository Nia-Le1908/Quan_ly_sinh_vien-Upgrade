package model;

public class DiemChiTiet {
    private String maSV;
    private String hoTen;
    private String maMon;
    private String tenMon;
    private int soTinChi;
    private int hocKy;
    private float diemQT;
    private float diemThi;
    private float diemTB;

    public DiemChiTiet(String maSV, String hoTen, String maMon, String tenMon, int soTinChi, int hocKy, float diemQT, float diemThi, float diemTB) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.soTinChi = soTinChi;
        this.hocKy = hocKy;
        this.diemQT = diemQT;
        this.diemThi = diemThi;
        this.diemTB = diemTB;
    }

    public DiemChiTiet(String string, String string2, float diemQT2, float diemThi2, float diemTB2, int hocKy2,
            String sql, String sql2, int diemTB3) {
        //TODO Auto-generated constructor stub
    }

    // --- Getters ---
    public String getMaSV() { return maSV; }
    public String getHoTen() { return hoTen; }
    public String getMaMon() { return maMon; }
    public String getTenMon() { return tenMon; }
    public int getSoTinChi() { return soTinChi; }
    public int getHocKy() { return hocKy; }
    public float getDiemQT() { return diemQT; }
    public float getDiemThi() { return diemThi; }
    public float getDiemTB() { return diemTB; }

    /**
     * Chuyển đổi điểm từ thang 10 sang thang 4.
     * @return Điểm theo hệ 4.
     */
    public double getDiemTBHe4() {
        if (diemTB >= 8.5) return 4.0;
        if (diemTB >= 7.0) return 3.0;
        if (diemTB >= 5.5) return 2.0;
        if (diemTB >= 4.0) return 1.0;
        return 0.0;
    }
}