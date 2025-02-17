# Credit Simulator

## 📌 Deskripsi
Credit Simulator adalah aplikasi berbasis Java yang digunakan untuk mensimulasikan cicilan kredit kendaraan berdasarkan jumlah pinjaman, DP, suku bunga, dan tenor. 

---
## ⚙️ Cara Build dengan Maven
### **Prasyarat**
- **Java 17+**
- **Maven 3+**
- **Git** *(opsional, jika ingin clone repo)*

### **1️⃣ Clone Repository (Opsional)**
```sh
 git clone https://github.com/dhimaspanji/credit-simulator.git
 cd credit-simulator
```

### **2️⃣ Build dengan Maven**
```sh
 mvn clean package
```
Setelah selesai, file JAR akan berada di:
```
target/credit-simulator-1.0-SNAPSHOT.jar
```

---
## 🚀 Cara Menjalankan Aplikasi

### **Di Windows (cmd/PowerShell)**
```sh
 bin\credit-simulator.bat file_input.txt
```
Atau tanpa file input:
```sh
 bin\credit-simulator.bat
```

### **Di Linux/Mac**
Pastikan file `credit-simulator.sh` memiliki izin eksekusi:
```sh
 chmod +x bin/credit-simulator.sh
```
Lalu jalankan:
```sh
 ./bin/credit-simulator.sh file_input.txt
```
Atau tanpa file input:
```sh
 ./bin/credit-simulator.sh
```

---
## 🐳 Menjalankan dengan Docker
### **1️⃣ Build Image Docker**
```sh
 docker build -t credit-simulator .
```

### **2️⃣ Menjalankan Container**
Tanpa file input:
```sh
 docker run -it --rm credit_simulator
```
Dengan file input:
```sh
 docker run -it --rm credit_simulator file_input.txt
```

---
## 🛠 Pengujian dengan Unit Test
Untuk menjalankan unit test menggunakan Maven:
```sh
 mvn test
```
