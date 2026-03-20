# PhuongDungShopWeb

Ung dung Spring Boot ban hang co cau hinh Docker de chay cung MySQL va luu du lieu bang Docker volume.

## Cong nghe

- Java 21
- Spring Boot
- MySQL 8.4
- Docker
- Docker Compose

## Chay du an bang Docker

### 1. Tao file `.env`

Tao file `.env` trong thu muc goc cua du an va them noi dung nhu sau:

```env
MYSQL_DATABASE=shopweb1
MYSQL_ROOT_PASSWORD=123456
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/shopweb1?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=123456

# Neu du an co gui mail OTP thi them cac bien sau
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
APP_MAIL_FROM=your-email@gmail.com
```

### 2. Build va chay container

```bash
docker compose up -d --build
```

### 3. Truy cap ung dung

Ung dung chay tai:

```text
http://localhost:8081
```

## Luu tru du lieu bang volume

Du lieu MySQL duoc luu trong named volume:

```text
mysql_data
```

Volume nay duoc gan vao container database tai:

```text
/var/lib/mysql
```

Vi vay khi xoa container, du lieu van duoc giu lai neu ban chua xoa volume.

### Lenh kiem tra volume

```bash
docker volume ls
docker volume inspect phanthiphuongdung_2280600393_mysql_data
```

## Dung image public tu Docker Hub

Neu ban da push image len Docker Hub public, sua `docker-compose.yml` nhu sau:

```yaml
app:
  image: YOUR_DOCKERHUB_USERNAME/phuongdungshopweb:latest
```

Sau do chay:

```bash
docker compose up -d
```

## Cach push len Docker Hub

### 1. Dang nhap Docker Hub

```bash
docker login
```

### 2. Build image

```bash
docker build -t YOUR_DOCKERHUB_USERNAME/phuongdungshopweb:latest .
```

### 3. Push image

```bash
docker push YOUR_DOCKERHUB_USERNAME/phuongdungshopweb:latest
```

### 4. Dat repository o che do Public

Tren Docker Hub, hay dat repository thanh `Public` de moi nguoi co the tim thay va pull image.

## Mot so lenh huu ich

### Xem log

```bash
docker compose logs -f
```

### Dung container

```bash
docker compose down
```

### Dung container nhung giu du lieu

```bash
docker compose down
```

### Xoa ca container va volume

```bash
docker compose down -v
```

## Luu y bao mat

- Khong commit file `.env` len GitHub.
- Khong dua mat khau database hoac app password email vao source code.
- Neu public image, hay truyen secret qua bien moi truong.
