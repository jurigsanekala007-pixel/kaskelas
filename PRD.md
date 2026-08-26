# PRD - Aplikasi Kas Kelas Offline

## 1. Ringkasan Produk

Aplikasi kas kelas offline adalah aplikasi Android yang digunakan oleh bendahara kelas untuk mencatat pemasukan, pengeluaran, saldo, serta membuat laporan keuangan kelas secara cepat dan aman. Aplikasi ini berjalan tanpa koneksi internet dan disimpan sepenuhnya di perangkat lokal, sehingga cocok untuk penggunaan sederhana dan terpercaya di lingkungan sekolah.

Tujuan utama aplikasi ini adalah membantu bendahara kelas mengelola uang kelas dengan mudah, akurat, dan tanpa membutuhkan sistem backend atau koneksi internet.

## 2. Masalah yang Diselesaikan

Bendahara kelas biasanya masih menggunakan cara manual seperti buku kas, catatan di kertas, atau spreadsheet. Hal ini sering menimbulkan:

- kesalahan pencatatan
- sulitnya menghitung saldo secara real-time
- laporan yang lambat dibuat
- data mudah hilang atau tidak terdokumentasi dengan baik
- proses pencatatan yang kurang rapi dan sulit dikontrol

Aplikasi ini dibuat untuk mengatasi masalah tersebut dengan cara yang lebih praktis, aman, dan cepat.

## 3. Target Pengguna

### 3.1 Persona utama

- Bendahara kelas
- Umumnya berusia remaja atau dewasa muda
- Memiliki kebutuhan sehari-hari untuk mencatat pemasukan dan pengeluaran kas kelas
- Menginginkan sistem yang sederhana, cepat, dan mudah digunakan

### 3.2 Karakteristik pengguna

- tidak terlalu familiar dengan teknologi kompleks
- membutuhkan tampilan yang sederhana dan mudah dipahami
- mengutamakan akurasi data dan kemudahan dalam laporan
- mayoritas menggunakan perangkat Android

## 4. Tujuan Produk

### 4.1 Tujuan bisnis

- mempermudah bendahara kelas mencatat transaksi kas
- mengurangi kesalahan manual dalam pengelolaan uang kelas
- menyediakan laporan keuangan yang lebih rapi dan cepat
- membuat data kas tetap aman dan dapat dipulihkan

### 4.2 Tujuan pengguna

- melihat saldo secara real-time
- menambahkan transaksi dengan cepat
- mendapatkan laporan bulan ini atau periode tertentu
- menjaga data tetap tersimpan di perangkat lokal

## 5. Nilai Produk

- Offline-first: dapat digunakan tanpa internet
- Sederhana: proses kas masuk dan kas keluar mudah dilakukan
- Aman: data tersimpan di perangkat dan dilindungi PIN/password
- Cepat: laporan dan saldo langsung terlihat
- Andal: data dapat di-backup dan dipulihkan

## 6. Scope Produk

### 6.1 Fitur MVP (Versi Awal)

Fitur yang harus ada untuk versi awal:

1. Login dengan PIN/password lokal
2. Dashboard saldo saat ini
3. Tambah transaksi pemasukan
4. Tambah transaksi pengeluaran
5. Edit transaksi
6. Hapus transaksi
7. Riwayat transaksi
8. Filter berdasarkan tanggal atau kategori
9. Laporan ringkas bulanan
10. Backup data ke file lokal
11. Restore data dari file backup
12. Pengaturan dasar aplikasi

### 6.2 Fitur lanjutan (Opsional)

- kategori transaksi custom
- grafik pemasukan dan pengeluaran
- ekspor PDF/CSV
- riwayat backup otomatis
- pengingat iuran atau tagihan
- fitur multi-user (jika dikembangkan ke tahap berikutnya)

## 7. User Flow

### 7.1 Flow utama

1. User membuka aplikasi
2. User masuk menggunakan PIN/password
3. User melihat dashboard saldo
4. User menambah transaksi
5. Sistem menghitung saldo otomatis
6. User melihat riwayat transaksi
7. User melihat laporan bulanan
8. User melakukan backup data bila diperlukan

### 7.2 Flow transaksi

- pilih jenis transaksi: pemasukan atau pengeluaran
- masukkan nominal
- pilih kategori
- pilih tanggal
- tulis keterangan
- simpan
- saldo otomatis berubah

## 8. Functional Requirements

### 8.1 Autentikasi

- pengguna harus login sebelum masuk ke dashboard
- PIN/password disimpan secara lokal di perangkat
- sistem harus menolak akses jika PIN/password salah

### 8.2 Manajemen transaksi

- pengguna dapat menambahkan pemasukan dan pengeluaran
- setiap transaksi harus memiliki:
  - tanggal
  - nominal
  - jenis transaksi
  - kategori
  - keterangan
- saldo dihitung otomatis berdasarkan transaksi yang sudah tersimpan

### 8.3 Dashboard

- menampilkan saldo saat ini
- menampilkan total pemasukan bulanan
- menampilkan total pengeluaran bulanan
- menampilkan transaksi terakhir

### 8.4 Riwayat transaksi

- menampilkan list transaksi terbaru
- dapat filter berdasarkan tanggal
- dapat mencari transaksi berdasarkan keterangan atau kategori

### 8.5 Laporan

- menampilkan ringkasan kas bulanan
- menghitung total pemasukan, total pengeluaran, dan saldo akhir
- menampilkan daftar transaksi untuk periode tertentu

### 8.6 Backup & restore

- pengguna dapat melakukan backup file data
- file backup dapat disimpan di folder lokal perangkat
- pengguna dapat memulihkan data dari file backup

### 8.7 Pengaturan

- ubah PIN/password
- lihat versi aplikasi
- pengaturan kategori transaksi

## 9. Non-Functional Requirements

### 9.1 Kinerja

- aplikasi harus membuka dashboard dalam waktu singkat
- transaksi harus diproses dengan cepat
- database local harus responsif meski data meningkat

### 9.2 Keamanan

- data pengguna harus tersimpan di perangkat lokal
- PIN/password tidak boleh tampil di layar saat diketik
- backup data harus aman dan tidak mudah rusak

### 9.3 Keandalan

- aplikasi harus tetap berfungsi tanpa koneksi internet
- transaksi yang sudah disimpan tidak boleh hilang akibat error kecil

### 9.4 Kemudahan penggunaan

- antarmuka harus mudah dipahami oleh bendahara kelas
- tombol utama harus terlihat jelas
- alur transaksi harus singkat dan intuitif

## 10. UI/UX Requirements

- desain sederhana, bersih, dan profesional
- warna yang jelas untuk membedakan pemasukan dan pengeluaran
- tombol utama besar dan mudah disentuh
- penggunaan bahasa yang singkat dan jelas
- tidak memakai terlalu banyak menu
- layout harus nyaman untuk penggunaan satu tangan

## 11. Desain Visual yang Direkomendasikan

### 11.1 Palette warna

- Midnight Navy: #0F172A
- Deep Blue: #1E3A5F
- Forest Green: #1F7A5C
- Coral Red: #D95D5D
- Amber Gold: #C9953F
- Bone White: #F6F1E8
- Cloud Gray: #E7EDF3
- Charcoal: #2F3A3F

### 11.2 Gaya UI

- minimalis modern
- cards untuk setiap informasi utama
- rounded corners yang konsisten
- shadow lembut untuk elemen penting
- ikon garis sederhana

## 12. Acceptance Criteria

### 12.1 Login

- pengguna dapat masuk menggunakan PIN/password yang valid
- pengguna tidak dapat masuk dengan PIN salah

### 12.2 Transaksi

- transaksi pemasukan menambah saldo
- transaksi pengeluaran mengurangi saldo
- data transaksi tersimpan secara lokal

### 12.3 Laporan

- laporan menampilkan total pemasukan, pengeluaran, dan saldo akhir
- laporan sesuai dengan periode yang dipilih

### 12.4 Backup dan restore

- file backup berhasil dibuat
- file backup berhasil dipulihkan
- data yang dipulihkan sesuai dengan data lama

## 13. Kriteria Sukses

Aplikasi dianggap berhasil jika:

- bendahara class dapat mencatat transaksi kas dengan cepat
- saldo selalu akurat
- laporan mudah dibuat
- aplikasi dapat digunakan tanpa internet
- data aman dan dapat dipulihkan

## 14. Prioritas Pengerjaan

### Prioritas 1

- login
- dashboard
- transaksi
- saldo otomatis
- riwayat transaksi

### Prioritas 2

- laporan bulanan
- filter transaksi
- backup & restore

### Prioritas 3

- export laporan
- grafik
- kategori custom
- pengingat iuran

## 15. Risiko dan Mitigasi

### Risiko 1: Data hilang

Mitigasi:

- backup data rutin
- sistem restore data yang jelas

### Risiko 2: Kesalahan transaksi

Mitigasi:

- validasi input nominal
- konfirmasi sebelum hapus data
- laporan yang mudah dicek ulang

### Risiko 3: Aplikasi terlalu rumit

Mitigasi:

- fokus pada fitur inti MVP
- hindari fitur yang tidak perlu di awal

## 16. Kesimpulan

Aplikasi kas kelas offline dirancang untuk membantu bendahara kelas mengelola uang secara cepat, aman, dan rapi tanpa memerlukan internet. Produk ini harus fokus pada kebutuhan utama: pencatatan transaksi, saldo real-time, laporan, dan keamanan data lokal.

Dengan pendekatan MVP yang sederhana namun kuat, produk ini siap dikembangkan menjadi aplikasi yang benar-benar bermanfaat dan mudah digunakan oleh user utama.
