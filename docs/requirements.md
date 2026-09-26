# Backend Quản lý thu chi cá nhân

## 1. Đầu vào

Xây dựng hệ thống Backend Quản lý thu chi cá nhân.

---

## 2. Mô tả

Xây dựng Hệ thống sổ thu chi (BE) với các yêu cầu nghiệp vụ sau:

### 2.1. Xác thực, phân quyền

- Đăng ký tài khoản bằng định danh người dùng và mật khẩu.
- Đăng nhập/Đăng xuất.
- Xem và cập nhật hồ sơ cá nhân.
- Đổi mật khẩu/Quên mật khẩu (gửi qua SMS hoặc Email).

### 2.2. Danh mục và ví

- Quản lý danh mục (Danh mục phẳng hoặc danh mục cây, tùy chọn).
- Quản lý ví của người dùng:
    - Tạo mới.
    - Chỉnh sửa.
    - Xem danh sách.
    - Xem chi tiết.

### 2.3. Quản lý giao dịch

- Tạo mới hoặc ghi giao dịch.
- Xem danh sách giao dịch và chi tiết giao dịch.
- Tạo file CSV báo cáo.
- Upload hóa đơn (nếu có).

### 2.4. Ngân sách và mục tiêu

- Quản lý ngân sách.
- Quản lý mục tiêu.

### 2.5. Báo cáo đối soát

- Xây dựng chức năng báo cáo đối soát.

---

## 3. Yêu cầu phi chức năng

- Format response thống nhất cho toàn bộ API.
- Xử lý exception tập trung, thông báo lỗi rõ ràng.
- Tài liệu API tự sinh qua các thư viện API Docs.
- Có migration.
- Đóng gói hệ thống.

---

## 4. Kết quả mong đợi

- [ ] Tài liệu thiết kế hệ thống (viết bằng Markdown hoặc Word đều được).
- [ ] Repository Git, branch `main` luôn build được, lịch sử commit theo convention.
- [ ] 1 file `docker-compose` chạy được toàn bộ các thành phần của hệ thống.
- [ ] Migration chạy thành 1 schema đầy đủ.
- [ ] Unit test, coverage 80%.
- [ ] 1 file `README` mô tả:
    - Môi trường.
    - Cài đặt.
    - Cách chạy.
    - Biến cấu hình.
    - Tài khoản test.
- [ ] OpenAPI docs.

---

## 5. Tóm tắt phạm vi hệ thống

Hệ thống Backend quản lý thu chi cá nhân bao gồm các nhóm chức năng chính:

1. **Xác thực và phân quyền**
2. **Quản lý danh mục và ví**
3. **Quản lý giao dịch**
4. **Quản lý ngân sách và mục tiêu**
5. **Báo cáo đối soát**

Hệ thống cần đảm bảo API có response thống nhất, xử lý lỗi tập trung, có migration, tài liệu API tự sinh, unit test đạt
coverage 80% và có khả năng đóng gói/chạy toàn bộ hệ thống bằng Docker Compose.