# ArenaForge: Game Tournament Manager (Java Swing)

Aplikasi desktop untuk mengelola turnamen e-sports: game dan role, player, team dan roster, turnamen,
jadwal fase grup, bagan gugur, input skor, klasemen, dan statistik player.
Seluruh antarmuka memakai **Java Swing/AWT**. Tidak ada HTML, CSS, JavaScript, maupun JavaFX.
Tidak ada dependency tambahan, jadi cukup JDK 17 atau lebih baru.

## Menjalankan

- **IntelliJ / VS Code**: buka folder `src`, jalankan `arenaforge.Main`.
- **Maven**: `mvn compile exec:java` (atau `mvn package` lalu `java -jar target/arenaforge-0.2.0.jar`).
- **Terminal tanpa Maven**: `javac -d out $(find src -name "*.java")` lalu `java -cp out arenaforge.Main`.

Akun demo: panitia `admin` / `admin`, player `player` / `player`. Penonton masuk tanpa akun.

## Alur demo (panitia)

1. Dashboard, klik **Muat Data Contoh** (8 team Mobile Legends, 48 player, turnamen MLBB Cup 2026).
2. Menu **Turnamen**, pilih MLBB Cup 2026, klik **Susun Jadwal** (grup diundi acak).
3. Menu **Input Skor**: catat skor fase grup (Bo2: 2-0, 1-1, 0-2). Setelah semua laga grup selesai, bagan gugur terisi otomatis.
4. Lanjutkan perempat final, semifinal, dan final (Bo3: 2-0 atau 2-1).
5. Lihat hasilnya di **Jadwal & Bracket**, **Klasemen**, dan **Statistik Player**.

## Struktur paket

| Paket | Isi |
|---|---|
| `arenaforge` | `Main`, `LoginDialog`, `MainFrame`, `Tema` (palet hitam-merah), `DataSementara` (data di memori) |
| `arenaforge.model` | Logika domain: `Game`, `Player`, `Team`, `Match` (abstract), `GroupMatch`, `KnockoutMatch`, `Tournament`, `Leaderboard`, `AturanTurnamenException` |
| `arenaforge.halaman` | Halaman menu panitia/penonton (Dashboard, Game, Player, Team, Turnamen, Jadwal, Skor, Klasemen, Statistik) beserta dialog formulirnya |
| `arenaforge.ui` | Komponen Swing buatan sendiri: `Tombol`, `Kartu`, `Lencana`, `JalurTurnamen`, `BaganGugur`, `TabelData`, `ComboGelap`, `Pesan`, dst. |
| `arenaforge.user` | Portal Player (Dashboard, Jadwal, Ikut lomba, Buat lomba) dengan data contoh `DataPortal` |

## Konsep OOP yang dipakai

- **Class dan object**: `Game`, `Player`, `Team`, `Tournament`, `Leaderboard`, dibuat dengan `new` (mis. `new Player("P01", "Viter", game, "Jungler")`).
- **Encapsulation**: semua atribut `private`, diakses lewat getter. `Player.setRole()` memvalidasi role terhadap game-nya; `Team` menjaga kapasitas roster dan kunci roster.
- **Inheritance**: `GroupMatch` dan `KnockoutMatch` mewarisi `Match`.
- **Abstract class**: `Match` tidak bisa dibuat langsung; `tentukanPemenang()`, `namaFase()`, dan `validasiSkor()` abstract.
- **Polymorphism**: `tentukanPemenang()` bekerja berbeda. Fase grup boleh seri (3/1/0 poin), babak gugur tidak boleh seri dan memajukan pemenang ke babak berikutnya.
- **Template method**: `Match.catatHasil()` (final) memanggil `validasiSkor()`, memperbarui statistik, lalu `setelahHasilDicatat()` yang diisi subclass.
- **Exception**: `AturanTurnamenException` (checked) untuk pelanggaran aturan; layar menangkapnya dan menampilkan pesan.
- **Event handling**: `ActionListener`, `ListSelectionListener`, `ItemListener`, `WindowListener`, `FocusListener`, key binding.
- **Custom painting**: `Tombol`, `Kartu`, `JalurTurnamen`, `BaganGugur` menggambar sendiri lewat `paintComponent` (Graphics2D).

## Catatan

- Data tersimpan di memori (`DataSementara`) dan hilang saat aplikasi ditutup. Database MySQL lewat JDBC opsional dan belum dipasang.
- Portal Player masih memakai data contoh sendiri (`DataPortal`), belum terhubung ke data panitia.
