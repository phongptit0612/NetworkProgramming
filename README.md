# Đề tài 4 — Java TCP Vigenère Chat

Codebase ban đầu cho đồ án Lập trình mạng. **Đã chạy được xử lý văn bản qua TCP bằng console.**
Chưa phải ứng dụng hoàn chỉnh: giao diện Swing, chat hai chiều, truyền file và lưu MySQL còn ở giai đoạn tiếp theo.

## Yêu cầu

- JDK 17 trở lên, Maven 3.8 trở lên.
- Chưa cần MySQL để chạy bản hiện tại.
- Không cần tải thư viện thủ công; Maven tải plugin và JUnit khi build lần đầu.

## Cấu trúc

| Thư mục | Vai trò |
|---|---|
| [common](common/) | Vigenère, thống kê ký tự, DTO và giao thức TCP |
| [server](server/) | TCP Server đa kết nối, service xử lý văn bản |
| [client](client/) | TCP Client tái sử dụng được và giao diện console |
| [database](database/) | Script schema MySQL dự kiến |
| [docs](docs/) | SRS ban đầu, giao thức, dàn ý báo cáo và slide |

## Build và kiểm thử

Mở PowerShell tại thư mục chứa README này:

```powershell
mvn clean verify
```

Trên máy hiện tại, nếu `mvn` chưa có trong PATH:

```powershell
& 'C:/Program Files/Maven/bin/mvn.cmd' clean verify
```

Build tạo hai JAR đã bao gồm common, có thể mang sang máy có JDK/JRE 17+:
`server/target/server.jar` và `client/target/client.jar`.

## Chạy thử

Terminal 1:

```powershell
java -jar server/target/server.jar
```

Terminal 2:

```powershell
java -jar client/target/client.jar
```

Nhập văn bản `BANANA`, khóa `KEY`. Kết quả:

```text
Ban ma: LELKRY
Ky tu nhieu nhat: [A] | So lan: 3
```

Client tiếp tục nhận văn bản mới; nhập `/quit` để thoát. Dùng Ctrl+C để dừng Server.
Thông báo console dùng tiếng Việt không dấu để dễ chạy trên các terminal Windows.

Đổi IP/cổng:

```powershell
java -jar server/target/server.jar 9999 0.0.0.0
java -jar client/target/client.jar 192.168.1.10 9999
```

Thay `192.168.1.10` bằng IP LAN thực tế của Server. Hai máy phải liên lạc được qua mạng
và máy Server phải cho phép Java/cổng TCP đã chọn qua firewall. `127.0.0.1` chỉ trỏ về máy đang chạy Client.
Chỉ khởi động một Server trên cùng IP/cổng. Lỗi kết nối hoặc timeout: kiểm tra Server rồi chạy lại Client.

## Quy ước bản đầu tiên

- Khóa chỉ chứa A–Z/a–z. Vigenère mã hóa chữ ASCII, giữ hoa/thường;
  ký tự có dấu, dấu câu và emoji giữ nguyên, không tiêu thụ vị trí khóa.
- Thống kê bản rõ phân biệt hoa/thường, bỏ qua khoảng trắng, tính cả dấu câu/emoji.
  Đồng hạng trả tất cả, dữ liệu rỗng trả danh sách rỗng và 0.
- Khóa được gửi cùng bản mã để Server giải mã. Đây là mô phỏng thuật toán cổ điển phục vụ môn học.
- Văn bản tối đa 1 MiB UTF-8; khóa tối đa 1.024 byte. Server idle timeout 5 phút.

Chi tiết: [SRS](docs/SRS.md), [giao thức](docs/PROTOCOL.md).

## Các mốc tiếp theo

- [x] Maven multi-module và hai JAR chạy độc lập.
- [x] Client mã hóa → TCP → Server giải mã/thống kê → phản hồi.
- [x] Kiểm thử thuật toán, giao thức và TCP thực trên loopback.
- [ ] Giao diện Swing với tác vụ mạng chạy nền.
- [ ] JDBC/MySQL: cấu hình, ghi và xem lịch sử.
- [ ] Chat hai chiều, xác định rõ người gửi/người nhận.
- [ ] Truyền file theo khối, kiểm tra nhận đủ và xử lý ngắt giữa chừng.
- [ ] Kiểm thử LAN, chụp minh chứng, hoàn thiện báo cáo và PPTX.

## Đóng gói bài nộp

Theo hướng dẫn của giảng viên: một thành viên đại diện nộp
`STT nhóm-Tên đề tài.zip` hoặc `.rar`, gồm báo cáo, source code, database và file thuyết trình `.pptx`.
STT nhóm phải dùng số nhóm thực tế, không tự thay bằng số đề tài.
Trước khi nộp, xuất báo cáo/slide hoàn chỉnh, thêm dữ liệu mẫu và hướng dẫn dựng CSDL;
loại thư mục build, cấu hình riêng và mật khẩu khỏi source đóng gói.
