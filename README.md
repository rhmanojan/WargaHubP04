# WargaHub P04 - UX, RecyclerView, Fragment, dan Navigasi

Proyek Praktikum 04 Pemrograman Mobile (PTI23043 • 1 SKS)  
Program Studi Teknologi Informasi, UIN Antasari Banjarmasin.

---

## 1. Identitas Mahasiswa
* **Nama** : Rahman Nor Fauzan
* **NIM** : 240104040145
* **Mata Kuliah** : Praktikum Pemrograman Mobile (PTI23043)
* **Semester / Pertemuan** : Semester V / Pertemuan 4 (Praktikum 04)
* **Dosen Pengampu** : Muhayat, M.IT

---

## 2. Perangkat dan Versi Alat Pengembangan
* **IDE** : Android Studio Ladybug / Meerkat
* **Bahasa** : Kotlin
* **Arsitektur UI** : Single Activity + Jetpack Navigation + XML Views + ViewBinding
* **Target & Kompatibilitas** :
  * `compileSdk`: 36 (Android 15 / UpsideDownCake/Baklava preview)
  * `minSdk`: 26 (Android 8.0 Oreo)
  * `targetSdk`: 36
* **Gradle & Plugin** :
  * Gradle Wrapper: 9.6.0
  * Android Gradle Plugin (AGP): 9.4.1
  * Gradle JDK: JetBrains Runtime (JBR) OpenJDK 25
* **Perangkat Uji** :
  * Emulator: Medium Phone API 37.0 (x86_64 Android 16/37)
  * Mode Uji: Portrait, Landscape, Skala Font Standar (1.0×), Skala Font Besar (1.3×), TalkBack/Accessibility Live Region.

---

## 3. Cara Membuka dan Menjalankan Aplikasi
1. Buka **Android Studio**.
2. Pilih menu **Open** lalu arahkan ke folder proyek: `P04_240104040145_RahmanNorFauzan/WargaHubP04`.
3. Tunggu proses **Gradle Sync** selesai. Pastikan dependensi terunduh dengan baik.
4. Pilih target perangkat/emulator pada *Device Selector* (minimal API 26).
5. Klik tombol **Run 'app'** (`Shift + F10`) atau jalankan via terminal di dalam direktori `WargaHubP04`:
   ```powershell
   .\gradlew.bat assembleDebug
   ```
6. File APK debug hasil kompilasi tersedia di:
   `app/build/outputs/apk/debug/app-debug.apk`.

---

## 4. Ringkasan Fitur Aplikasi
1. **Single Activity & Navigation Component** :
   * Menggunakan satu `MainActivity` dengan `FragmentContainerView` (`NavHostFragment`) dan `Toolbar` material yang tersinkronisasi via `NavigationUI.setupActionBarWithNavController()`.
   * Peta alur navigasi dikelola secara deklaratif melalui `res/navigation/nav_graph.xml` (`homeFragment` &rarr; `listFragment` &rarr; `detailFragment`).
   * Tombol *Up* di ActionBar dan tombol *Back* sistem beroperasi secara terpadu tanpa menduplikasi layar di *back stack*.
2. **Beranda (`HomeFragment`)** :
   * Menyambut pengguna dan memberikan panduan membaca laporan lingkungan.
   * Terdapat tombol interaktif untuk membuka daftar laporan (`open_list`).
3. **Daftar Laporan (`ListFragment` & `RecyclerView`)** :
   * Menampilkan kumpulan 15 laporan simulasi lingkungan hidup menggunakan `RecyclerView`, `LinearLayoutManager`, dan `ListAdapter` dengan `DiffUtil.ItemCallback` yang efisien.
   * Setiap baris (`item_laporan.xml`) memiliki umpan balik sentuhan (`selectableItemBackground`), judul tebal, kategori, dan status simulasi.
   * Mendukung aksesibilitas penuh melalui atribut `contentDescription` baris terpadu dan `accessibilityLiveRegion="polite"` pada teks jumlah laporan.
   * **State Preservation**: Menggunakan `stateRestorationPolicy = PREVENT_WHEN_EMPTY` serta `onSaveInstanceState` untuk mempertahankan status saat konfigurasi layar berubah (seperti rotasi).
4. **Mode Empty State (Daftar Kosong) & Pemulihan** :
   * Tombol uji toggle `"Uji Daftar Kosong"` / `"Tampilkan Data Contoh"`.
   * Ketika daftar kosong, `RecyclerView` disembunyikan dan `emptyContainer` (`ScrollView` + `TextView`) ditampilkan dengan instruksi pemulihan yang jelas bagi pengguna.
5. **Rincian Laporan (`DetailFragment`)** :
   * Menerima argumen navigasi ringan bertipe `Long` (`laporan_id`).
   * Menampilkan judul, kategori, status contoh, dan deskripsi lengkap laporan terkait.
   * Terdapat penanganan *fallback UI* yang aman jika data tidak ditemukan (misal ID tidak valid/salah), tanpa menyebabkan crash (*null pointer exception*).
   * Tombol `"Kembali ke Daftar"` menggunakan `findNavController().navigateUp()` yang mengembalikan pengguna ke posisi scroll daftar sebelumnya tanpa membuat instance fragment baru.

---

## 5. Batasan dan Status Data Contoh
* **Data Simulasi Lokal**: Seluruh 15 data laporan yang dimuat oleh `LaporanContoh` merupakan data tiruan fiktif lokal di memori untuk keperluan latihan UX dan navigasi.
* **Tanpa Database / CRUD**: Aplikasi belum menerapkan persistensi SQLite/Room, jaringan REST API, maupun fitur penambahan formulir laporan baru (fitur penyimpanan dan CRUD direncanakan pada modul lanjutan berikutnya).
* **Reset Saat Restart**: Data yang dikosongkan melalui tombol simulasi akan kembali semula saat aplikasi ditutup dan dibuka kembali dari launcher.

---

## 6. Struktur Direktori Pengumpulan
```text
P04_240104040145_RahmanNorFauzan/
├── docs/
│   ├── Laporan_P04.docx
│   └── Laporan_P04.md
├── screenshots/
│   ├── 01_beranda.png
│   ├── 02_daftar.png
│   ├── 03_rincian_zona10.png
│   ├── 04_daftar_kosong.png
│   ├── 05_kosong_landscape.png
│   ├── 06_landscape_font_besar_beranda.png
│   ├── 06b_landscape_font_besar_daftar.png
│   ├── 06c_landscape_font_besar_rincian.png
│   └── 07_id_tidak_ditemukan.png
├── WargaHubP04/
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── AndroidManifest.xml
│   │   │   ├── java/id/antasari/p04/wargahub/
│   │   │   │   ├── MainActivity.kt
│   │   │   │   ├── adapter/LaporanAdapter.kt
│   │   │   │   ├── data/Laporan.kt
│   │   │   │   ├── data/LaporanContoh.kt
│   │   │   │   └── ui/HomeFragment.kt
│   │   │   │   └── ui/ListFragment.kt
│   │   │   │   └── ui/DetailFragment.kt
│   │   │   └── res/
│   │   │       ├── layout/
│   │   │       ├── navigation/nav_graph.xml
│   │   │       ├── values/
│   │   │       └── values-night/
│   │   └── build.gradle.kts
│   ├── gradle/
│   ├── build.gradle.kts
│   ├── gradle.properties
│   ├── gradlew
│   ├── gradlew.bat
│   └── settings.gradle.kts
├── Modul_Praktikum_04_UX_RecyclerView_Fragment_dan_Navigasi.pdf
└── README.md
```
