# 1Weather 🌤️

**1Weather** adalah aplikasi prakiraan cuaca berbasis Android yang dikembangkan menggunakan Kotlin dan Jetpack Compose. Aplikasi ini menampilkan informasi cuaca terkini, prakiraan cuaca per jam, serta prakiraan cuaca selama 7 hari.

## ✨ Fitur

- **Informasi Cuaca Terkini** — Menampilkan suhu, kondisi cuaca, kelembapan, kecepatan angin, tekanan udara, dan informasi lainnya.
- **Prakiraan Per Jam** — Melihat perubahan kondisi dan suhu cuaca berdasarkan waktu.
- **Prakiraan 7 Hari** — Menampilkan perkiraan cuaca selama tujuh hari ke depan.
- **Pencarian Kota** — Mencari informasi cuaca berdasarkan nama kota.
- **Grafik Suhu** — Menampilkan perubahan suhu dalam bentuk grafik.
- **Informasi Matahari** — Menampilkan waktu matahari terbit dan terbenam.
- **Tema Light dan Dark** — Pilihan tampilan terang dan gelap.
- **Dukungan Bahasa** — Tersedia dalam bahasa Indonesia dan Inggris.
- **Animasi Cuaca** — Tampilan ikon cuaca dengan animasi.

## 🛠️ Teknologi yang Digunakan

| Teknologi | Kegunaan |
|---|---|
| Kotlin | Bahasa pemrograman |
| Jetpack Compose | Membangun antarmuka aplikasi |
| Material 3 | Komponen dan desain UI |
| Retrofit | Mengakses REST API |
| Gson | Parsing data JSON |
| ViewModel | Mengelola data dan state UI |
| Kotlin Coroutines | Proses asynchronous |
| Open-Meteo API | Mengambil data cuaca |
| Geocoding API | Pencarian lokasi kota |

## 🌐 API

Aplikasi ini menggunakan beberapa layanan API:

- [Open-Meteo Weather API](https://open-meteo.com/) — Menyediakan data cuaca dan prakiraan cuaca.
- [Open-Meteo Geocoding API](https://open-meteo.com/en/docs/geocoding-api) — Digunakan untuk mencari koordinat berdasarkan nama kota.
- [Nominatim](https://nominatim.org/) — Digunakan untuk kebutuhan informasi lokasi.

## 🚀 Cara Menjalankan Project

1. Clone repository:

   ```bash
   git clone https://github.com/ivannaone/1Weather.git
   ```

2. Buka project menggunakan **Android Studio**.

3. Tunggu proses Gradle Sync sampai selesai.

4. Hubungkan perangkat Android atau jalankan emulator.

5. Klik tombol **Run** untuk menjalankan aplikasi.

## 📱 Platform

- Android
- Dibangun menggunakan Kotlin dan Jetpack Compose.

## 📂 Struktur Project

```text
1Weather/
├── app/
│   └── src/
│       └── main/
│           ├── java/com/ivanna/oneweather/
│           │   ├── data/
│           │   │   └── remote/
│           │   ├── model/
│           │   ├── repository/
│           │   ├── ui/
│           │   │   ├── components/
│           │   │   ├── screens/
│           │   │   └── theme/
│           │   └── viewmodel/
│           └── res/
├── gradle/
├── build.gradle.kts
└── settings.gradle.kts
```

## 👩‍💻 Developer

**Ivanna Maulida Ikmala**

Project: **1Weather — Android Weather Application**

GitHub: [@ivannaone](https://github.com/ivannaone)

---

Dibuat sebagai project pengembangan aplikasi Android dengan Kotlin dan Jetpack Compose.