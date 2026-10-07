# Muse Blater 🎬✨
### Application Search & Discovery Anime Android

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android" />
  <img src="https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Compose" />
  <img src="https://img.shields.io/badge/API-Tenrai%20v1-FF8C00?style=for-the-badge" alt="API" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-00C853?style=for-the-badge" alt="MVVM" />
</p>

---

## 📌 Deskripsi Singkat Aplikasi

**Muse Blater** adalah aplikasi mobile Android modern yang dirancang untuk memudahkan pengguna dalam mencari dan menjelajahi informasi anime terkini secara *real-time*. Antarmuka pengguna dibangun secara deklaratif menggunakan **Jetpack Compose** dan **Material 3** sesuai spesifikasi desain **Google Stitch UI**, dipadukan dengan **Tenrai REST API** (`api.tenrai.org/v1/`) sebagai penyedia data anime *live*.

---

## 📸 Tampilan Screenshot Aplikasi

<div align="center">

| 🏠 Halaman Utama (Home) | 📖 Detail Anime | 🔍 Pencarian & Filter Genre |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/46249331-738b-4946-951e-ef892ea6de5f" width="260" alt="Halaman Utama Muse Blater" /> | <img src="https://github.com/user-attachments/assets/7d73decf-d48c-4445-a350-6eebcb7a8175" width="260" alt="Pencarian & Genre" /> | <img src="https://github.com/user-attachments/assets/8dd99902-857e-48b6-99b8-2453495d804f" width="260" alt="Detail Anime" /> |

</div>

---

## ✨ Fitur Utama

- 🔍 **Pencarian Anime Live**: Fitur cari judul anime secara *real-time* dari Tenrai API dilengkapi dengan *debounce* (500ms) dan pembersihan kueri cepat.
- 🏷️ **Filter Kategori Genre**: Chip filter genre horizontal yang dimuat secara dinamis dari API.
- 🖼️ **Grid & Kartu Anime**: Grid 2 kolom responsif yang menampilkan poster anime, badge tipe (TV/Movie), rating bintang, dan tag genre utama dengan animasi *shimmer skeleton placeholder*.
- 📖 **Halaman Detail Anime**: Tampilan detail mendalam berdasarkan `mal_id` dengan:
  - Efek pencahayaan *ambient glow* oranye di belakang poster.
  - Badge peringkat (*Ranked #1*).
  - Banner skor komunitas MyAnimeList lengkap dengan metrik popularitas & jumlah member.
  - Grid 2×2 informasi detail (Tipe, Jumlah Episode, Status Tayang, Durasi).
  - Sinopsis ringkasan yang dapat diperluas (*Read More / Show Less*).
  - Fitur Bagikan Anime (*Android Native Share Intent*).
  - Tautan langsung untuk membuka halaman anime di browser (*View on MAL*).
- 🎨 **Sistem Desain Warm Radiant**: Tema oranye khas (`#FF8C00`) dengan tipografi kustom *Plus Jakarta Sans*, sudut lengkung *RoundedCornerShape*, dan batas kartu bermotif lembut.
- 🛡️ **Penanganan Network & Error Mandiri**: Penanganan error ramah pengguna dengan pesan kesalahan Bahasa Indonesia/Inggris, tombol *Retry*, serta penanganan *rate limit* HTTP 429 otomatis.

---

## 🏛️ Penjelasan Struktur MVVM (Model-View-ViewModel)

Aplikasi **Muse Blater** menerapkan pola arsitektur **MVVM (Model-View-ViewModel)** dengan prinsip *Unidirectional Data Flow (UDF)* untuk memisahkan logika bisnis, data, dan antarmuka pengguna:

```
┌─────────────────────────────────────────────────────────────┐
│                          VIEW                               │
│  (HomeScreen, DetailScreen, CommonComponents, Navigation)   │
└──────────────────────────────▲──────────────────────────────┘
                               │ UI State (StateFlow)
                               │ User Events (Callbacks)
┌──────────────────────────────▼──────────────────────────────┐
│                        VIEWMODEL                            │
│              (HomeViewModel, DetailViewModel)               │
└──────────────────────────────▲──────────────────────────────┘
                               │ Coroutines Result
                               │ Suspend Functions
┌──────────────────────────────▼──────────────────────────────┐
│                        REPOSITORY                           │
│                      (AnimeRepository)                      │
└──────────────────────────────▲──────────────────────────────┘
                               │ Retrofit API Calls
┌──────────────────────────────▼──────────────────────────────┐
│                    MODEL / REMOTE DATA                      │
│       (AnimeModels DTO, JikanApiService, RetrofitClient)    │
└─────────────────────────────────────────────────────────────┘
```

### 1. **Model (Data Layer)**
- **`AnimeModels.kt`**: Berisi definisi data DTO (*Data Transfer Object*) dari respon JSON API (seperti `AnimeSearchResponse`, `AnimeItem`, `Genre`) serta model tampilan UI (`AnimeUiModel`, `AnimeDetailUiModel`). File ini juga menyediakan fungsi pemetaan (*mapper extension functions*) untuk mengubah data mentah API menjadi data UI yang siap ditampilkan.
- **`JikanApiService.kt`**: Antarmuka Retrofit yang mendefinisikan endpoint REST API Tenrai (`@GET("anime")`, `@GET("top/anime")`, `@GET("anime/{id}/full")`, `@GET("genres/anime")`).
- **`RetrofitClient.kt`**: Singleton provider untuk mengonfigurasi `OkHttpClient` dengan Interceptor custom (*User-Agent*, *Rate Limit Retry HTTP 429*, dan *HttpLoggingInterceptor*) serta menginisialisasi pustaka `Retrofit`.

### 2. **Repository Layer**
- **`AnimeRepository.kt`**: Bertindak sebagai sumber data tunggal (*Single Source of Truth*). Repository menjembatani pemanggilan API dari `RetrofitClient` dengan ViewModel, menangani pengolahan kueri pendek (< 3 karakter) agar tidak memicu error API 422, dan mengembalikan data dalam bentuk `Result<T>`.

### 3. **ViewModel (State Management)**
- **`HomeViewModel.kt`**: Mengelola state UI halaman utama (`HomeUiState`). Menyediakan logika pencarian berfitur *debounce* (500ms), penyaringan genre, penanganan status loading/error, dan pemetaan exception menjadi pesan error yang ramah pengguna.
- **`DetailViewModel.kt`**: Mengelola state UI halaman detail (`DetailUiState`). Bertanggung jawab mengambil data detail anime berdasarkan `mal_id` dan menyediakan fungsi *retry*.

### 4. **View (UI Layer)**
- **`HomeScreen.kt`**: Komposable UI deklaratif untuk halaman utama yang mengamati `HomeUiState` dari `HomeViewModel` dan menampilkan grid anime 2 kolom, search bar, serta chip genre.
- **`DetailScreen.kt`**: Komposable UI deklaratif untuk halaman detail yang mengamati `DetailUiState` dari `DetailViewModel` dan menampilkan rincian informasi anime, poster dengan efek *ambient glow*, serta tombol aksi bagikan/buka tautan.
- **`CommonComponents.kt`**: Berisi komponen UI reusabel seperti `ErrorState`, `LoadingScreen`, `EmptyState`, dan animasi `shimmerEffect()`.
- **`MuseBlaterNavGraph.kt`**: Mengatur alur navigasi dan parameter antar halaman menggunakan Jetpack Navigation Compose.

---

## 🛠️ Penjelasan Penggunaan Library

Berikut adalah pustaka (*libraries*) utama yang digunakan dalam pengembangan aplikasi beserta peran fungsinya:

| Pustaka / Library | Versi | Peran & Fungsi Utama dalam Aplikasi |
| :--- | :--- | :--- |
| **Jetpack Compose (Material 3)** | `2026.02.01 (BOM)` | Framework UI deklaratif modern Android untuk membangun seluruh tampilan komponen, skema warna, dan tipografi tanpa XML. |
| **Navigation Compose** | `2.9.0` | Mengelola navigasi antar layar (`HomeScreen` $\leftrightarrow$ `DetailScreen`) serta pengiriman argumen `mal_id` secara aman. |
| **Retrofit 2** | `2.11.0` | Client HTTP type-safe untuk berkomunikasi dan mengirimkan request ke Tenrai REST API (`api.tenrai.org/v1/`). |
| **Gson Converter** | `2.11.0` | Converter Retrofit yang mengurai (*parse*) respon JSON dari Tenrai API secara otomatis menjadi Kotlin Data Class. |
| **OkHttp Logging Interceptor** | `4.12.0` | Interceptor HTTP untuk mencetak log request/response di Logcat serta memfasilitasi penambahan header kustom dan retry otomatis HTTP 429. |
| **Coil Compose** | `2.7.0` | Pustaka pemuat gambar (*image loader*) berbasis Coroutines untuk mengunduh, menampilkan, dan meng-cache gambar poster anime secara asinkron. |
| **Kotlin Coroutines & Flow (StateFlow)** | `1.10.2` | Pustaka *asynchronous programming* untuk menjalankan proses jaringan di background thread serta mengalirkan state UI secara reaktif dengan `StateFlow`. |
| **Plus Jakarta Sans Font** | Kustom | Font tipografi resmi aplikasi yang memberikan tampilan visual modern dan bersih sesuai spesifikasi Google Stitch UI. |

---

## 🚀 Cara Menjalankan Aplikasi

### Persyaratan
- Android Studio Ladybug / Meerkat (atau lebih baru)
- JDK 17 / 21
- Android SDK 34+ (Target SDK: 37, Min SDK: 24)

### Menjalankan lewat Terminal / Gradle

```bash
# Clone atau buka direktori proyek
cd MuseBlater

# Jalankan pengujian unit (Unit Tests)
.\gradlew.bat test

# Build APK Debug
.\gradlew.bat assembleDebug
```

Hasil file APK yang sudah di-compile terletak di:
`app/build/outputs/apk/debug/app-debug.apk`
