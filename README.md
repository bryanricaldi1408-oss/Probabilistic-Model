# Probabilistic Information Retrieval Model

Proyek ini mengimplementasikan berbagai model pencarian informasi probabilistik menggunakan dataset Cranfield.  

Anggota Kelompok:
- **6182301013 - Bryan Ricaldi Chandra**
- **6182301080 - Robert Saputra**
- **6182301094 - Bima Rahmadani Arifandi**  

## Model yang Tersedia

1.  **BIM (Binary Independent Model)**: Model dasar yang menggunakan bobot probabilitas berdasarkan kemunculan term dalam dokumen relevan dan non-relevan.
2.  **Two-Poisson Model**: Model yang mempertimbangkan distribusi frekuensi term menggunakan dua distribusi Poisson. Menggunakan rumus bobot RSJ (Robertson-Sparck Jones).
3.  **BM25 (Best Matching 25)**: Model probabilistik modern yang menyempurnakan Two-Poisson dengan mempertimbangkan saturasi frekuensi term ($k_1$) dan normalisasi panjang dokumen ($b$).
4.  **BM11 (Best Matching 10)**: predecessor dari model B25 sebelum implementasi variabel b

## Prasyarat

- Java Development Kit (JDK) 8 atau versi yang lebih baru.
- Dataset Cranfield (pastikan folder `cran/` berada di lokasi yang benar sesuai path di `Main.java`).

## Cara Menjalankan Program

Ikuti langkah-langkah berikut untuk mengompilasi dan menjalankan program melalui terminal:

1.  **Buka terminal** dan masuk ke direktori `src`:
    ```bash
    cd src
    ```

2.  **Kompilasi program**:
    ```bash
    javac Main.java
    ```

3.  **Jalankan program**:
    ```bash
    java Main
    ```

## Panduan Penggunaan

- **Pilih Model**: Saat program dimulai, pilih model (1, 2, 3, atau 4).
- **Konfigurasi BM25**: Jika memilih BM25, Anda akan diminta memasukkan nilai $k_1$ (default 1.2) dan $b$ (default 0.75). Tekan **Enter** untuk menggunakan nilai default.
- **Konfigurasi BM25**: Jika memilih BM10, Anda akan diminta memasukkan nilai $k_1$ (default 1.2). Tekan **Enter** untuk menggunakan nilai default.
- **Masukkan Query**: Ketik query Anda (misalnya: "what similarity laws..."). Program akan otomatis mencocokkan query Anda dengan query terdekat dalam dataset Cranfield. Setelah hasil pencarian ditampilkan, **evaluasi otomatis** akan dijalankan.
- **Mode Evaluasi**: Ketik `eval` untuk masuk ke mode evaluasi:
  - **Opsi 1**: Evaluasi satu query — masukkan query dan lihat detail evaluasinya.
  - **Opsi 2**: Evaluasi seluruh query Cranfield — menghitung MAP dan rata-rata metrik seluruh query.
- **Ganti Model**: Ketik `switch` saat diminta query untuk mengganti model yang digunakan.
- **Keluar**: Ketik `exit` untuk menghentikan program.

## Metrik Evaluasi

Program ini mengimplementasikan metrik evaluasi Information Retrieval berikut:

| Metrik | Deskripsi |
|---|---|
| **Precision** | Proporsi dokumen relevan dari seluruh dokumen yang di-retrieve |
| **Recall** | Proporsi dokumen relevan yang berhasil di-retrieve dari seluruh dokumen relevan |
| **F1-Score** | Harmonic mean dari Precision dan Recall |
| **Precision@K** | Precision pada K dokumen teratas (K = 1, 3, 5, 10, 20) |
| **Average Precision (AP)** | Rata-rata precision pada setiap posisi dokumen relevan ditemukan |
| **11-Point Interpolated Avg Precision** | Interpolated precision pada 11 recall level standar (0.0 – 1.0) |
| **Mean Average Precision (MAP)** | Rata-rata AP seluruh query (pada evaluasi batch) |

