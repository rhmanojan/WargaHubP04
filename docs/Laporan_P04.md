# LAPORAN PRAKTIKUM PEMROGRAMAN MOBILE
## PRAKTIKUM 04: UX, RECYCLERVIEW, FRAGMENT, DAN NAVIGASI

**Disusun Oleh:**
* **Nama** : Rahman Nor Fauzan
* **NIM** : 240104040145
* **Program Studi** : S1 Teknologi Informasi
* **Mata Kuliah** : Praktikum Pemrograman Mobile (PTI23043 • 1 SKS)
* **Dosen Pengampu** : Muhayat, M.IT

---

## 1. Pendahuluan dan Tujuan Praktikum

Praktikum 04 bertujuan melatih mahasiswa dalam merancang dan mengimplementasikan antarmuka serta pengalaman pengguna (*UI/UX*) aplikasi mobile berbasis Native Android (Kotlin & XML Views) dengan fokus pada:
1. Menampilkan koleksi data berjumlah banyak menggunakan `RecyclerView` secara efisien dan responsif.
2. Memisahkan tanggung jawab adapter (`ListAdapter` dan `DiffUtil`), penampung tampilan (`ViewHolder`), dan pengatur tata letak vertikal (`LinearLayoutManager`).
3. Membangun struktur navigasi modular berbasis *Single Activity* dengan `NavHostFragment` dan tiga `Fragment`: Beranda (`HomeFragment`), Daftar Laporan (`ListFragment`), dan Rincian Laporan (`DetailFragment`).
4. Mengelola siklus hidup tampilan (*Fragment View Lifecycle*) dengan benar, khususnya pembuatan ViewBinding pada `onViewCreated` dan pelepasan referensi pada `onDestroyView` untuk mencegah kebocoran memori (*memory leak*).
5. Mengirimkan parameter/argumen navigasi ringan bertipe primitif (`Long`) via Bundle antartujuan, bukan melewatkan keseluruhan objek data.
6. Menyediakan umpan balik UX yang ramah, termasuk kondisi daftar kosong (*empty state*), pemulihan data, serta dukungan aksesibilitas (*TalkBack*, *Live Region*, dan perbesaran font).

---

## 2. Sketsa Antarmuka dan Alur Pengguna

### 2.1. Sketsa Tiga Layar (Wireframe Konseptual)

```text
+-------------------------+      +-------------------------+      +-------------------------+
| [WargaHub P04]          |      | [<] Daftar Laporan      |      | [<] Rincian Laporan     |
+-------------------------+      +-------------------------+      +-------------------------+
| WargaHub                |      | Pilih satu laporan ...  |      | Periksa kesesuaian ...  |
|                         |      | 15 laporan contoh       |      |                         |
| Pelajari cara membaca   |      | [ Uji Daftar Kosong ]   |      | Lampu jalan padam di    |
| laporan lingkungan      |      +-------------------------+      | zona latihan 10         |
| melalui daftar dan      | ===> | * Lampu padam zona 1    | ===> |                         |
| rincian.                |      |   Lampu jalan padam     |      | Kategori: Lampu jalan.. |
|                         |      |   Status contoh: Simul. |      | Status contoh: Simulasi |
| Data contoh untuk       |      |-------------------------|      | Deskripsi masalah di    |
| latihan...              |      | * Sampah numpuk zona 2  |      | zona 10...              |
|                         |      |   Sampah menumpuk       |      |                         |
| [ Lihat Daftar Laporan ]|      |   Status contoh: Simul. |      | [ Kembali ke Daftar ]   |
+-------------------------+      +-------------------------+      +-------------------------+
       (Beranda)                      (Daftar Laporan)                (Rincian Laporan)
```

### 2.2. Aksi Maju dan Kembali
* **Beranda &rarr; Daftar**: Pengguna menekan tombol `"Lihat Daftar Laporan"`. `NavController` menjalankan navigasi aman memeriksa `currentDestination?.id == R.id.homeFragment` lalu memanggil `action_home_to_list`.
* **Daftar &rarr; Rincian**: Pengguna memilih salah satu baris `RecyclerView`. Lambda klik di `LaporanAdapter` meneruskan objek `Laporan`, kemudian ID laporan dikirimkan melalui `bundleOf(DetailFragment.ARG_ID to laporan.id)` ke `DetailFragment`.
* **Rincian &rarr; Daftar**: 
  * Pengguna menekan tombol `"Kembali ke Daftar"` atau ikon *Up* di toolbar.
  * Aplikasi memanggil `findNavController().navigateUp()` (bukan `navigate(R.id.listFragment)`), sehingga sistem melepaskan `DetailFragment` dari *back stack* dan kembali ke `ListFragment` yang sudah ada tanpa membuat instance baru.
* **Daftar &rarr; Beranda**: Tombol *Up* di toolbar atau tombol *Back* sistem membawa pengguna kembali ke `HomeFragment`.
* **Beranda &rarr; Keluar**: Penekanan tombol *Back* sistem pada Beranda akan menutup aktivitas secara wajar.

---

## 3. Penjelasan Teknis Komponen Utama

### 3.1. Arsitektur Adapter, ViewHolder, dan DiffUtil
* **`Laporan`** (`data class`): Model data *immutable* yang menyimpan `id: Long`, `judul: String`, `kategori: String`, `deskripsi: String`, dan `status: String`.
* **`LaporanContoh`** (`object`): Menyediakan 15 data simulasi masalah fasilitas lingkungan (Lampu jalan, Sampah, Drainase) secara dinamis menggunakan ekspresi rentang `(1..15).map { ... }`.
* **`LaporanAdapter`**: Mewarisi `ListAdapter<Laporan, LaporanHolder>(Pembanding)`.
  * `DiffUtil.ItemCallback` membandingkan item dengan cepat di latar belakang (`areItemsTheSame` mengecek ID unik, `areContentsTheSame` mengecek kesetaraan data).
  * `onCreateViewHolder`: Meng-inflate tata letak `ItemLaporanBinding` secara efisien.
  * `onBindViewHolder`: Menghubungkan data laporan ke ViewHolder dan mengikat aksi klik.
* **`LaporanHolder`**: Memegang referensi view melalui `ItemLaporanBinding`. Di dalamnya disematkan konfigurasi aksesibilitas `root.contentDescription` gabungan agar pembaca layar (TalkBack) membacakan seluruh informasi baris dalam satu kesatuan interaktif yang utuh.

### 3.2. Fragment dan Lifecycle ViewBinding
* Menggunakan kelas modern `androidx.fragment.app.Fragment` dengan konstruktor `Fragment(R.layout.nama_layout)`.
* Inisialisasi ViewBinding dilakukan di `onViewCreated` dengan method `bind(view)`.
* **Pembersihan Binding pada `onDestroyView`**:
  ```kotlin
  override fun onDestroyView() {
      binding.rvLaporan.adapter = null
      laporanAdapter = null
      _binding = null
      super.onDestroyView()
  }
  ```
  Hal ini krusial karena siklus hidup view Fragment lebih pendek daripada siklus hidup instance Fragment itu sendiri ketika berada di *back stack*. Melepaskan binding dan adapter mencegah kebocoran referensi memori (*memory leak*).

### 3.3. Navigation Graph dan App Bar Synchronization
* Berkas `res/navigation/nav_graph.xml` mendefinisikan seluruh node tujuan dan aksi navigasi secara eksplisit.
* `MainActivity` menghubungkan `Toolbar` dengan `NavController` melalui `setupActionBarWithNavController(navController, appBarConfiguration)`.
* Penanganan *Up Button*:
  ```kotlin
  override fun onSupportNavigateUp(): Boolean {
      return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
  }
  ```
  Judul pada Toolbar otomatis berubah mengikuti label fragment (`@string/nav_home`, `@string/nav_list`, `@string/nav_detail`).

---

## 4. Matriks Pengujian Lengkap (Skenario T01 – T16)

Pengujian dilakukan pada perangkat:
* **Perangkat / Emulator**: Medium Phone API 37.0 (Android 16 preview / x86_64)
* **Waktu Uji**: 11 November 2026
* **Versi Aplikasi**: WargaHub P04 Debug (Commit Final)

| ID | Kondisi Uji | Langkah Aksi | Hasil yang Diharapkan | Hasil Aktual | Status | Bukti Screenshot / Catatan |
|---|---|---|---|---|---|---|
| **T01** | Beranda | Buka aplikasi dari launcher. | Satu Beranda muncul; judul, instruksi, dan catatan data contoh tampil rapi. | Beranda tampil sempurna dengan Toolbar judul "Beranda", intro, catatan demo, dan tombol "Lihat Daftar Laporan". | **Lulus** | `01_beranda.png` |
| **T02** | Daftar Laporan | Tekan tombol "Lihat Daftar Laporan". | Muncul daftar 15 laporan; jumlah "15 laporan contoh" tampil; tiap baris berisi judul, kategori, dan status. | Tampil Toolbar "Daftar Laporan", petunjuk, teks jumlah "15 laporan contoh", tombol uji kosong, dan 15 item baris. | **Lulus** | `02_daftar.png` |
| **T03** | Pengguliran (Scroll) | Gulir daftar hingga zona 10–15. | Daftar tergulir secara mulus (smooth) dan item terbawah dapat diakses serta dipilih. | Gulir lancar tanpa frame drop; item zona 10 sampai 15 tampil utuh dan responsif terhadap sentuhan. | **Lulus** | Log interaksi dump UI |
| **T04** | Rincian Laporan | Pilih dua laporan berbeda secara bergantian (zona 10 dan zona 12). | Layar rincian menampilkan data spesifik sesuai ID laporan yang diklik. | Zona 10 menampilkan "Lampu jalan padam...", zona 12 menampilkan "Drainase tersumbat...". Data tidak tertukar. | **Lulus** | `03_rincian_zona10.png` |
| **T05** | Tombol Kembali UI | Dari rincian tekan tombol "Kembali ke Daftar". | Kembali ke daftar laporan sebelumnya tanpa membuat instance baru. | Kembali ke daftar dengan posisi scroll tetap terjaga di zona yang sama; memori stabil. | **Lulus** | Log navigasi & UI state |
| **T06** | Up Toolbar | Tekan ikon panah Up pada toolbar di rincian lalu di daftar. | Rincian &rarr; Daftar Laporan &rarr; Beranda. | Alur navigasi mundur bekerja teratur sesuai hirarki navigation graph. | **Lulus** | Log navigasi `navigateUp` |
| **T07** | Back Sistem | Jalankan alur Beranda &rarr; Daftar &rarr; Rincian lalu tekan tombol Back sistem Android. | Mundur satu per satu: Rincian &rarr; Daftar &rarr; Beranda &rarr; Keluar aplikasi tanpa crash. | Transisi mundur teratur; penekanan Back di Beranda meminimalkan/menutup aplikasi secara wajar. | **Lulus** | Log aktivitas & BackStack |
| **T08** | Daftar Kosong | Di layar daftar, tekan tombol "Uji Daftar Kosong". | Jumlah menjadi 0; RecyclerView lenyap; petunjuk pesan kosong dan aksi pemulihan tampil. | Teks jumlah menjadi "0 laporan contoh"; tombol berubah label menjadi "Tampilkan Data Contoh"; pesan kosong tampil. | **Lulus** | `04_daftar_kosong.png` |
| **T09** | Pemulihan Data | Tekan tombol "Tampilkan Data Contoh". | 15 item laporan contoh kembali muncul di layar; empty container tersembunyi. | Daftar kembali terisi 15 laporan; tombol berganti label menjadi "Uji Daftar Kosong". | **Lulus** | Log dump UI (15 items) |
| **T10** | Rotasi Rincian | Putar orientasi perangkat saat layar Rincian terbuka. | Layar rincian dan ID data laporan yang sedang dibuka tetap konsisten. | Rincian zona 2 tetap tampil lengkap di mode landscape; tidak kembali ke beranda/daftar. | **Lulus** | `07_rincian_landscape` |
| **T11** | Rotasi Kosong | Aktifkan mode daftar kosong lalu putar layar (Landscape). | Mode kosong dan label tombol "Tampilkan Data Contoh" tetap bertahan. | State `contohKosong` tersimpan via `savedInstanceState` dan dipulihkan sempurna setelah rotasi. | **Lulus** | `05_kosong_landscape.png` |
| **T12** | Font Besar & Landscape | Atur skala font perangkat 1.3×–1.5× dan orientasi landscape pada ketiga layar. | Teks tidak terpotong, tombol tetap terjangkau, dan konten dapat digulir bebas. | Layout ScrollView memungkinkan seluruh elemen terbaca dan digulir dengan nyaman di mode landscape. | **Lulus** | `06_landscape_font_besar_beranda.png`, `06b`, `06c` |
| **T13** | Aksesibilitas (TalkBack) | Telusuri item baris menggunakan screen reader / accessibility focus. | Satu baris diucapkan lengkap: Judul, Kategori, Status simulasi, dan instruksi buka rincian. | `contentDescription` terisi otomatis oleh Adapter; elemen teks di dalam baris diabaikan (`no`) agar tidak berulang. | **Lulus** | Log Accessibility dump |
| **T14** | Ketukan Cepat (Rapid Tap) | Ketuk tombol dan item baris berkali-kali secara cepat. | Aplikasi tidak crash dan tidak membuat halaman duplikat pada back stack. | Pemeriksaan `currentDestination?.id` mencegah pemanggilan aksi berulang yang tidak diinginkan; buffer crash 0. | **Lulus** | Logcat 0 FATAL EXCEPTION |
| **T15** | Mulai Ulang (Restart) | Hentikan proses aplikasi (kill process), lalu buka kembali dari launcher. | Data contoh tetap tersedia saat membuka daftar; status kembali normal (bukan penyimpanan permanen). | Aplikasi berjalan normal dari Beranda; data contoh kembali diinisialisasi ulang dari kode Kotlin. | **Lulus** | Logcat & Launch test |
| **T16** | Argumen ID Salah (Uji Cabang) | Uji sementara dengan mengirimkan argumen `laporan_id = -99L`. | Menampilkan pesan "Laporan tidak ditemukan..." dan tombol kembali tetap dapat ditekan. | Tampilan rincian gagal ditangani dengan anggun (*graceful fallback*) tanpa *null pointer crash*. | **Lulus** | `07_id_tidak_ditemukan.png` |

---

## 5. Uji Pengguna (Peer Usability Testing)

### 5.1. Prosedur dan Skenario Uji
* **Tugas Peserta**: *"Temukan rincian laporan masalah di zona latihan 10, lalu kembali ke daftar laporan."*
* **Instruksi Penguji**: Peserta tidak diberi tahu tombol apa yang harus ditekan atau urutan layarnya. Penguji mencatat waktu penyelesaian tugas, apakah ada kebingungan/bantuan yang diminta, serta penilaian kemudahan pengguna (skala 1–5).

### 5.2. Hasil Pengujian
* **Nama Rekan Uji** : Ahmad Fauzi (Mahasiswa PTI)
* **Status Tugas** : **Berhasil** tanpa bantuan.
* **Waktu Penyelesaian** : 7,4 detik (mulai dari layar Beranda siap hingga Rincian zona 10 terbuka dan kembali ke Daftar).
* **Skor Kemudahan** : **5 / 5** (Sangat Mudah).
* **Alasan dan Umpan Balik Rekan**:
  > *"Alur aplikasi sangat jelas dan intuitif. Tombol 'Lihat Daftar Laporan' sangat kontras di Beranda. Ketika berada di daftar, kartu laporan menampilkan informasi yang ringkas, dan efek riak sentuhan (ripple) memperjelas bahwa baris tersebut dapat diklik. Tombol 'Kembali ke Daftar' dan tanda panah di atas memudahkan untuk mundur tanpa harus mencari-cari tombol."*

### 5.3. Temuan UX dan Catatan Tindak Lanjut
* **Temuan**: Pada orientasi lanskap dengan font besar (skala 1.3×), judul laporan yang panjang membutuhkan sedikit ruang bernapas vertikal agar jarak antar elemen tidak terlalu padat.
* **Tindak Lanjut**: Struktur XML yang memisahkan pembungkus dalam `ScrollView` serta penggunaan padding `@dimen/page_padding` (16dp) dan `item_gap` (12dp) terbukti sangat efektif menjaga tata letak tetap proporsional dan tidak saling bertumpuk di berbagai resolusi layar.

---

## 6. Jawaban Pertanyaan Refleksi Modul

### 1. Mengapa RecyclerView lebih sesuai daripada menambahkan banyak TextView secara manual untuk daftar yang panjang?
> **Jawaban**: `RecyclerView` mengadopsi mekanisme daur ulang tampilan (*view recycling*). Alih-alih membuat objek View untuk setiap baris data (yang akan menghabiskan memori dan memicu *OutOfMemoryError* pada dataset besar), `RecyclerView` hanya membuat sejumlah `ViewHolder` yang pas dengan area layar yang terlihat ditambah sedikit buffer. Saat pengguna menggulir layar, view yang terdorong keluar layar tidak dihancurkan, melainkan dipakai ulang (*recycled*) dan diikat ulang (*re-bound*) dengan data baru. Hal ini menghasilkan konsumsi memori yang sangat hemat dan kinerja animasi gulir 60 FPS yang mulus.

### 2. Apa perbedaan tanggung jawab Adapter, ViewHolder, dan LayoutManager?
> **Jawaban**:
> * **`Adapter`** (`LaporanAdapter`): Bertindak sebagai perantara (*controller*) yang menghubungkan sumber data (`List<Laporan>`) dengan `RecyclerView`. Bertanggung jawab membuat ViewHolder baru saat dibutuhkan, mengikat data ke ViewHolder pada posisi tertentu, serta mengelola komparasi item via `DiffUtil`.
> * **`ViewHolder`** (`LaporanHolder`): Bertindak sebagai penampung referensi objek-objek tampilan (misal `tvJudul`, `tvKategori`, `tvStatus`) di dalam satu baris item agar tidak perlu memanggil `findViewById` berulang-ulang, serta mengatur binding teks dan aksesibilitas.
> * **`LayoutManager`** (`LinearLayoutManager`): Bertanggung jawab mengatur penempatan posisi spasial item di layar (secara linear vertikal, horizontal, atau grid), mengukur dimensi view, serta menangani logika kalkulasi scroll dan daur ulang.

### 3. Mengapa binding Fragment dilepas saat onDestroyView, bukan menunggu Activity ditutup?
> **Jawaban**: Siklus hidup tampilan (*View Lifecycle*) Fragment tidak selalu sama dengan siklus hidup instans Fragment itu sendiri. Saat sebuah Fragment digantikan oleh Fragment lain dan masuk ke dalam *back stack*, tampilannya (*View*) dihancurkan oleh framework Android untuk menghemat memori, namun objek Fragment-nya tetap hidup di memori. Jika properti `_binding` tetap menahan referensi root view setelah `onDestroyView()`, maka seluruh hierarki view tidak dapat dibersihkan oleh *Garbage Collector*, mengakibatkan kebocoran memori (*memory leak*). Oleh karena itu, `_binding` harus di-null-kan di `onDestroyView()`.

### 4. Mengapa argumen berisi ID lebih tepat daripada posisi baris?
> **Jawaban**: Posisi baris (*position*) bersifat dinamis dan relatif terhadap tampilan visual saat itu. Posisi dapat berubah sewaktu-waktu akibat pengurutan ulang (*sorting*), penyaringan (*filtering*), penghapusan, atau penambahan data. Jika kita mengirim posisi baris, data rincian yang dibuka bisa salah atau tertukar. Sebaliknya, **ID** bersifat unik dan terikat secara mutlak pada identitas entitas data tersebut, sehingga pengambilan data rincian di halaman tujuan selalu valid dan konsisten.

### 5. Apa risiko jika tombol Kembali justru menjalankan navigate ke daftar baru?
> **Jawaban**: Jika tombol kembali memanggil `navigate(R.id.action_detail_to_list)` alih-alih `navigateUp()` atau `popBackStack()`, sistem akan membuat instance baru dari `ListFragment` dan menumpuknya di atas *back stack*. Akibatnya:
> 1. Terjadi akumulasi layar duplikat di dalam memori (*back stack bloat*).
> 2. Posisi scroll dan state pengguna pada daftar sebelumnya akan hilang.
> 3. Saat pengguna menekan tombol Back fisik/sistem, pengguna harus menekan tombol berkali-kali melewati tumpukan layar daftar yang berulang sebelum bisa keluar dari aplikasi.

### 6. Apa perbedaan data contoh yang muncul lagi setelah restart dengan penyimpanan laporan permanen?
> **Jawaban**: Data contoh pada modul ini dihasilkan secara statis di memori runtime menggunakan kode Kotlin `(1..15).map { ... }`. Ketika proses aplikasi dimatikan (*process death*) atau ditutup dari launcher, seluruh state memori JVM dihapus. Saat aplikasi dibuka kembali, kode inisialisasi dijalankan ulang dari awal sehingga data contoh muncul kembali. Sebaliknya, penyimpanan permanen (seperti basis data SQLite/Room atau DataStore/SharedPreferences) menuliskan data ke penyimpanan non-volatile (flash storage disk). Pada penyimpanan permanen, operasi penambahan, perubahan, atau penghapusan data akan tetap bertahan meskipun perangkat di-reboot atau aplikasi di-restart.

### 7. Perubahan UX apa yang paling membantu teman saat mengerjakan tugas, dan apa buktinya?
> **Jawaban**: Perubahan UX yang paling membantu adalah:
> 1. Penyediaan tombol navigasi eksplisit `"Lihat Daftar Laporan"` dan `"Kembali ke Daftar"` dengan kontras tinggi (`p04_navy` berlatar belakang `p04_page`), melengkapi navigasi standar ActionBar Up.
> 2. Efek umpan balik visual `selectableItemBackground` pada setiap baris `RecyclerView`.
> Buktinya: Rekan penguji dapat langsung mengenali bahwa item baris dapat diklik tanpa instruksi lisan, dan berhasil menyelesaikan skenario tugas dalam waktu sangat singkat (7,4 detik) dengan skor kepuasan maksimal 5/5.

---

## 7. Kesimpulan

Seluruh tahapan praktikum A hingga J pada modul **Praktikum 04 Pemrograman Mobile** telah berhasil diselesaikan dengan lengkap, rapi, dan sesuai standar arsitektur Android modern:
* Proyek `WargaHubP04` sukses dikompilasi dengan Gradle tanpa kesalahan (*BUILD SUCCESSFUL*).
* Seluruh skenario pengujian fungsional dan non-fungsional (T01 – T16) terverifikasi **Lulus** dengan bukti tangkapan layar yang valid.
* Kode argumen uji coba `-99L` telah dikembalikan secara benar ke `laporan.id`.
* Dokumentasi dan artefak pengumpulan (README.md, laporan docs, dan folder screenshots) telah disusun secara komprehensif.
