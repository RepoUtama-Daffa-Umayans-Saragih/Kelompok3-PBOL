package arenaforge.user;

/** Satu anggota roster tim. Pemain inti tampil di laga, cadangan menunggu di bangku. */
public record Anggota(String nickname, String role, boolean inti) {
}
