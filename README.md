# Probabilistic Information Retrieval Model

Proyek ini mengimplementasikan berbagai model pencarian informasi probabilistik menggunakan dataset Cranfield.

## Model yang Tersedia

1.  **BIM (Binary Independent Model)**: Model dasar yang menggunakan bobot probabilitas berdasarkan kemunculan term dalam dokumen relevan dan non-relevan.
2.  **Two-Poisson Model**: Model yang mempertimbangkan distribusi frekuensi term menggunakan dua distribusi Poisson. Menggunakan rumus bobot RSJ (Robertson-Sparck Jones).
3.  **BM25 (Best Matching 25)**: Model probabilistik modern yang menyempurnakan Two-Poisson dengan mempertimbangkan saturasi frekuensi term ($k_1$) dan normalisasi panjang dokumen ($b$).

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

- **Pilih Model**: Saat program dimulai, pilih model (1, 2, atau 3).
- **Konfigurasi BM25**: Jika memilih BM25, Anda akan diminta memasukkan nilai $k_1$ (default 1.2) dan $b$ (default 0.75). Tekan **Enter** untuk menggunakan nilai default.
- **Masukkan Query**: Ketik query Anda (misalnya: "what similarity laws..."). Program akan otomatis mencocokkan query Anda dengan query terdekat dalam dataset Cranfield.
- **Ganti Model**: Ketik `switch` saat diminta query untuk mengganti model yang digunakan.
- **Keluar**: Ketik `exit` untuk menghentikan program.
