# Thiết kế UI (desktop-first) cho CRUD

## Global Styles (Design Tokens)
- Layout: `.container` 1200px; nền trang trung tính (giảm nền đỏ mạnh ở khu vực CRUD), nội dung đặt trong card trắng.
- Màu: Primary `#ffd400`; Text `#111/#333`; Border `#e5e7eb`; Danger `#d0021b`; Success `#16a34a`; Info `#2563eb`.
- Typography: base 14px; H1 24px; H2 20px; line-height 1.4–1.6.
- Spacing: 4/8/12/16/24; radius 8px; shadow nhẹ cho card & dropdown.
- Link: link thường; link trong bảng giảm “margin-right” tự phát, dùng nhóm hành động.

## UI Architecture (tính đồng nhất)
- Dùng class thay cho inline style: `btn`, `btn-primary`, `btn-secondary`, `btn-danger`, `btn-link`.
- Chuẩn hoá table: `table`, `table--zebra`, `table__actions` (gap 8), `th` căn trái; số/giá căn phải.
- Chuẩn hoá form: `form-card`, `form-grid` (2 cột desktop), `field`, `field__label`, `field__control`, `field__error`.
- Chuẩn hoá thông báo: fragment `alert` (success/error/warn/info) đặt ngay dưới tiêu đề trang.

## Page Specs

### 1) Danh sách sản phẩm (/products)
- Meta: title “Danh sách sản phẩm”; description “Quản lý sản phẩm”.
- Structure: Page header (H1 + actions) → Card chứa bảng.
- Components:
  - Action bar: nút primary “Thêm sản phẩm”.
  - Table columns: ID, Ảnh (72px bo góc), Tên (link), Giá (căn phải), Danh mục, Hành động.
  - Hành động: “Sửa” (secondary) + “Xoá” (danger). Xoá cần confirm dialog.
  - Empty state: icon + “Chưa có sản phẩm” + CTA “Thêm sản phẩm”.

### 2) Form sản phẩm (thêm/sửa) (/products/new, /products/edit/{id})
- Meta: title động “Thêm sản phẩm”/“Sửa sản phẩm”.
- Layout: Card form 2 cột desktop (Tên/Danh mục; Giá/Ảnh), Mô tả full-width.
- States:
  - Field error hiển thị dưới input; border đổi màu danger.
  - Footer actions: “Lưu” (primary) + “Huỷ” (link/secondary) cố định cuối form.

### 3) Danh mục cha - danh sách (/categories/parents)
- Structure: H1 + nút “Thêm danh mục cha” → Card table.
- Table: ID, Tên, Hành động (Sửa/Xoá). Xoá có confirm.

### 4) Danh mục cha - form (/categories/parents/add, /categories/parents/edit/{id})
- Form card 1 cột; field “Tên danh mục cha”; error rõ ràng.

### 5) Danh mục con - danh sách (/categories/children)
- Table: ID, Tên, Danh mục cha, Hình (40px), Hành động.
- Ảnh: placeholder khi thiếu; align giữa cột ảnh.

### 6) Danh mục con - form (/categories/children/add, /categories/children/edit/{id})
- Form 2 cột: Tên / Danh mục cha (select); Ảnh URL full-width.
- Select: option mặc định “Chọn danh mục cha”; hiển thị lỗi nếu bắt buộc.

## Thông báo & phản hồi (ưu tiên)
- Sau create/update/delete: hiển thị alert ở đầu trang danh sách.
- Lỗi validate: hiển thị tổng quan 1 dòng + lỗi theo field.
- Delete confirm: copy rõ ràng (VD: “Bạn có chắc muốn xoá ‘{name}’?”) + nút “Xoá” danger.
