# SRS ban đầu — Đề tài 4

Nguồn yêu cầu: tài liệu đề tài môn Lập trình mạng, đề tài 4, trang 2–3.
Các quy ước bổ sung bên dưới là lựa chọn ban đầu của nhóm, có thể chỉnh theo giảng viên.

## Mục tiêu và phạm vi

Ứng dụng Java Client–Server qua TCP: chat, truyền file ảnh/audio/video,
mã hóa văn bản bằng Vigenère ở Client, giải mã và tìm ký tự xuất hiện nhiều nhất ở Server,
trả kết quả về Client, lưu dữ liệu trong CSDL.

## Yêu cầu chức năng và tiến độ

| ID | Yêu cầu | Trạng thái |
|---|---|---|
| FR01 | Client kết nối IP/port của Server | Có qua tham số console |
| FR02 | Nhập văn bản và khóa, mã hóa trước khi gửi | Có |
| FR03 | Truyền bản mã và khóa qua TCP | Có: cùng một request |
| FR04 | Server giải mã, đếm ký tự, trả kết quả | Có |
| FR05 | Hiển thị kết quả và thông báo lỗi | Có qua console |
| FR06 | Lưu lịch sử xử lý vào CSDL | Chưa; đã có schema dự kiến |
| FR07 | Chat tương tác, gửi tin từ Server về Client | Chưa |
| FR08 | Gửi/nhận file ảnh, audio, video | Chưa |
| FR09 | Giao diện Swing | Chưa; lựa chọn triển khai của nhóm |

## Quy ước văn bản và khóa

- Khóa gồm một hoặc nhiều ký tự ASCII A–Z/a–z, không chứa dấu cách; giới hạn 1.024 byte.
- Vigenère áp dụng riêng cho A–Z/a–z, giữ hoa/thường. Ký tự khác giữ nguyên,
  không làm tăng vị trí khóa. Vì vậy chữ có dấu và emoji không được mã hóa trong bản này.
- Không tự bỏ dấu hoặc đổi hoa/thường văn bản.
- Đếm Unicode code point, phân biệt hoa/thường, bỏ qua whitespace và space character,
  tính cả dấu câu và emoji. Dấu tổ hợp được đếm riêng nếu đầu vào chưa chuẩn hóa Unicode.
- Nếu đồng hạng, trả tất cả ký tự theo thứ tự xuất hiện đầu tiên.
- Văn bản rỗng hoặc toàn khoảng trắng trả danh sách rỗng và số lần 0.
- Văn bản mã hóa tối đa 1 MiB UTF-8 mỗi request.
- Ví dụ: BANANA / KEY → LELKRY → Server trả A / 3.

## Vận hành hiện tại

- Mặc định Server nghe 0.0.0.0:9999, Client nối 127.0.0.1:9999.
- Server xử lý tối đa 16 kết nối đang phục vụ, hàng đợi 32 kết nối.
- Client timeout kết nối 5 giây, chờ phản hồi 15 giây; lỗi mạng thì kết thúc để chạy lại.
- Server đóng kết nối không gửi dữ liệu trong 5 phút; Client cần chạy lại sau thời gian này.
- Request lỗi khóa trả lỗi và vẫn dùng được kết nối. Khung truyền sai bị đóng kết nối.
- Khóa gửi cùng bản mã trên TCP thuần cho mục tiêu thực hành; chưa có TLS hay xác thực.

## Tiêu chí nghiệm thu phần codebase

- Build bằng JDK 17 trở lên và Maven.
- Chạy hai JAR ở hai terminal; thực hiện ví dụ BANANA/KEY đúng kết quả.
- Kiểm thử vector Vigenère chuẩn, Unicode, đồng hạng, khóa sai, khung truyền sai,
  request liên tiếp và hai Client cùng kết nối.
- Các mục FR06–FR09 cần hoàn thiện ở giai đoạn sau để đáp ứng toàn bộ đề tài.
