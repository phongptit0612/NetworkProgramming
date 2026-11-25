# Database

[schema.sql](schema.sql) là khung MySQL dự kiến cho lịch sử xử lý văn bản và truyền file.
**Server hiện chưa kết nối hoặc lưu MySQL. Không cần cài MySQL để chạy bản console.**

Khi triển khai JDBC:

1. Chạy script bằng MySQL Workbench hoặc MySQL CLI.
2. Thêm MySQL Connector/J vào module server.
3. Đọc URL, user và password từ biến môi trường; không đưa mật khẩu vào source code.
4. Tạo repository dùng PreparedStatement, gọi từ AnalysisService sau khi giải mã/thống kê.
5. Thêm dữ liệu mẫu và kiểm thử đọc lại lịch sử trước khi đóng gói bài nộp.

Không lưu khóa Vigenère trong lịch sử mặc định. Đây là mã hóa cổ điển phục vụ môn học,
không phải phương án bảo mật dữ liệu thực tế.
