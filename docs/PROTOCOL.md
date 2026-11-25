# Giao thức TCP v1

Client và Server dùng DataInputStream/DataOutputStream; số nguyên 32 bit big-endian.
Mỗi chuỗi gồm `length:int32` rồi đúng `length` byte UTF-8, không dùng writeUTF.
Mỗi request có đúng một response, một kết nối cho phép nhiều cặp request/response tuần tự.
TCP không bảo toàn ranh giới thông điệp; bên nhận luôn đọc theo độ dài đã khai báo.

## Request ANALYZE

| Trường | Kiểu |
|---|---|
| version | int32 = 1 |
| ciphertext | Chuỗi UTF-8, tối đa 1.048.576 byte |
| key | Chuỗi UTF-8, tối đa 1.024 byte; chỉ A–Z/a–z |

## Response

| Trường | Kiểu |
|---|---|
| version | int32 = 1 |
| success | boolean, 1 byte |
| message | Chuỗi UTF-8 |
| occurrenceCount | int32 |
| characterCount | int32 |
| characters | characterCount chuỗi UTF-8, mỗi chuỗi một code point |

Phản hồi lỗi khóa có success=false, danh sách rỗng, occurrenceCount=0.
Sai version, sai độ dài hoặc khung thiếu byte làm đóng kết nối.

Khi thêm chat và file, cần định nghĩa loại thông điệp, request ID và phiên bản giao thức mới.
Không gửi byte file trực tiếp xen giữa các trường của request v1.
