# UTP_PAM

Food Ordering App sederhana berbasis Android untuk memenuhi brief UTP PAM menggunakan Jetpack Compose, Navigation Compose, dan state management in-memory tanpa database.

## Deskripsi

Aplikasi ini menampilkan daftar menu makanan, halaman detail menu, pengaturan jumlah pesanan, ringkasan total pesanan secara realtime, serta fitur tambahan untuk level pedas, pilihan nasi, nomor antrian, estimasi makanan jadi, dan konfirmasi pembayaran di kasir.

## Fitur Utama

- Daftar menu makanan menggunakan `LazyColumn`
- Halaman detail menu dengan route `detail/{itemId}`
- Tambah dan kurangi jumlah pesanan
- Ringkasan total item dan total harga secara realtime
- Empty state `No orders yet`
- Level pedas: Tidak pedas, Sedang, Pedas, Sangat pedas
- Opsi pakai nasi atau tidak
- Nomor antrian dan estimasi makanan jadi
- Konfirmasi pembayaran setelah bayar di kasir

## Teknologi

- Kotlin
- Jetpack Compose
- Navigation Compose
- Coil untuk gambar menu

## State Management

Aplikasi ini menggunakan:

- `remember`
- `rememberSaveable`
- `mutableStateListOf`
- state hoisting

Semua data dikelola secara in-memory tanpa database atau local storage.

## Struktur Halaman

- `menu`
- `detail/{itemId}`

## Cara Menjalankan

1. Buka project di Android Studio.
2. Tunggu Gradle sync selesai.
3. Jalankan emulator atau hubungkan device Android.
4. Klik `Run`.

## Author

- Iqbal
