package arenaforge.user;

/**
 * Lomba yang bisa diikuti lewat kode. Dipakai menu Ikut lomba (mencari kode)
 * dan menu Buat lomba (lomba baru dari player).
 */
public record Lomba(String kode, String nama, String game, int kuota, int terdaftar, String penyelenggara) {
