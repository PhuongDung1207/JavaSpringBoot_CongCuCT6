# PhuongDungShopWeb

Ung dung Spring Boot ban hang, da duoc cau hinh de:

- chay local bang Docker Compose
- tu dong test tren GitHub Actions
- build va push Docker image len Docker Hub khi push len nhanh `main`

## Cong nghe

- Java 21
- Spring Boot
- Maven
- MySQL 8.4
- Docker
- GitHub Actions

## Chay du an bang Docker

### 1. Tao file `.env`

Sao chep tu file mau:

```bash
cp .env.example .env
```

Neu ban dang dung Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Sau do sua cac gia tri trong `.env`, dac biet la:

- `MYSQL_ROOT_PASSWORD`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `APP_MAIL_FROM`
- `DOCKER_IMAGE_NAME`

### 2. Build va chay container

```bash
docker compose up -d --build
```

### 3. Truy cap ung dung

```text
http://localhost:8081
```

## Docker image

Build image thu cong:

```bash
docker build -t your-dockerhub-username/phuongdungshopweb:latest .
```

Push image len Docker Hub:

```bash
docker push your-dockerhub-username/phuongdungshopweb:latest
```

Trong `docker-compose.yml`, ten image dang duoc lay tu bien:

```env
DOCKER_IMAGE_NAME=your-dockerhub-username/phuongdungshopweb
```

Neu khong dat bien nay, compose se dung mac dinh:

```text
phuongdungphan/phuongdungshopweb:latest
```

## GitHub Actions cho du an nay

Workflow nam tai:

```text
.github/workflows/demo.yml
```

Pipeline hien tai gom 3 phan:

1. `Maven Test`: chay test voi MySQL service tren GitHub runner
2. `Docker Build Check`: build image de dam bao `Dockerfile` hop le
3. `Docker Publish`: push image len Docker Hub khi push vao `main`

### Dieu kien workflow duoc kich hoat

- `pull_request` vao nhanh `main`
- `push` vao nhanh `main`

### Can cau hinh gi tren GitHub

Vao `Settings -> Secrets and variables -> Actions`, sau do them:

Secrets:

- `DOCKERHUB_USERNAME`: ten dang nhap Docker Hub
- `DOCKERHUB_TOKEN`: access token cua Docker Hub

Variables:

- `DOCKER_IMAGE_NAME`: vi du `your-dockerhub-username/phuongdungshopweb`

### Cach tao Docker Hub access token

1. Dang nhap Docker Hub
2. Vao `Account Settings -> Personal access tokens`
3. Tao token moi
4. Luu token vao GitHub secret `DOCKERHUB_TOKEN`

### Ket qua sau khi cau hinh xong

- Tao pull request: GitHub se tu dong chay test va build Docker
- Push len `main`: GitHub se test, build image, roi push len Docker Hub

## Mot so lenh huu ich

Xem log:

```bash
docker compose logs -f
```

Dung container:

```bash
docker compose down
```

Xoa ca container va volume:

```bash
docker compose down -v
```

Kiem tra volume:

```bash
docker volume ls
docker volume inspect phanthiphuongdung_2280600393_mysql_data
```

## Luu y bao mat

- Khong commit file `.env` len GitHub
- Khong dua password database hoac mail token vao source code
- Nen dung Docker Hub access token thay vi password
