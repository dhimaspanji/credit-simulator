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
 git clone https://github.com/username/credit-simulator.git
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
 bin\run.bat file_input.txt
```
Atau tanpa file input:
```sh
 bin\run.bat
```

### **Di Linux/Mac**
Pastikan file `run.sh` memiliki izin eksekusi:
```sh
 chmod +x bin/run.sh
```
Lalu jalankan:
```sh
 ./bin/run.sh file_input.txt
```
Atau tanpa file input:
```sh
 ./bin/run.sh
```

---
## 📂 Struktur Direktori
```
credit-simulator/
│── bin/
│   ├── run.sh    # Script untuk Linux/Mac
│   ├── run.bat   # Script untuk Windows
│── target/
│   ├── credit-simulator-1.0-SNAPSHOT.jar
│── src/
│── pom.xml
│── README.md
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
 docker run --rm credit-simulator
```
Dengan file input:
```sh
 docker run --rm -v $(pwd)/file_input.txt:/app/file_input.txt credit-simulator file_input.txt
```

---
## 🛠 Pengujian dengan Unit Test
Untuk menjalankan unit test menggunakan Maven:
```sh
 mvn test
```

---
## 📄 Lisensi
Aplikasi ini dibuat untuk tujuan simulasi kredit. Lisensi dapat ditentukan sesuai kebutuhan.
