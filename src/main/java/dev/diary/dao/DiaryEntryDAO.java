
package dev.diary.dao;
import dev.diary.db.DatabaseConnection;
import dev.diary.model.DiaryEntry;
import dev.diary.model.EntryMood;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DiaryEntryDAO implements CrudDAO<DiaryEntry,Long>{
    @Override
    public DiaryEntry save(DiaryEntry entry) throws Exception {
        String sql = """
            INSERT INTO diary_entries (title, content, user_id, mood, is_encrypted, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getContent());
            pstmt.setLong(3, entry.getUserId());
            pstmt.setString(4, entry.getMood().name());
            pstmt.setBoolean(5, entry.isEncrypted());
            pstmt.setTimestamp(6, Timestamp.valueOf(entry.getCreatedAt()));
            pstmt.setTimestamp(7, Timestamp.valueOf(entry.getUpdatedAt()));

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entry.setId(generatedKeys.getLong(1));
                }
            }

            return entry;
        }
    }

    @Override
    public Optional<DiaryEntry> findById(Long id) throws Exception {
        String sql = "SELECT * FROM diary_entries WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapResultSetToEntry(rs));
            }

            return Optional.empty();
        }
    }

    @Override
    public List<DiaryEntry> findAll() throws Exception {
        String sql = "SELECT * FROM diary_entries ORDER BY created_at DESC";
        List<DiaryEntry> entries = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
        }

        return entries;
    }

    @Override
    public void update(DiaryEntry entry) throws Exception {
        String sql = """
            UPDATE diary_entries 
            SET title = ?, content = ?, mood = ?, is_encrypted = ?, updated_at = ?
            WHERE id = ? AND user_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            entry.setUpdatedAt(LocalDateTime.now());

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getContent());
            pstmt.setString(3, entry.getMood().name());
            pstmt.setBoolean(4, entry.isEncrypted());
            pstmt.setTimestamp(5, Timestamp.valueOf(entry.getUpdatedAt()));
            pstmt.setLong(6, entry.getId());
            pstmt.setLong(7, entry.getUserId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void delete(Long id) throws Exception {
        String sql = "DELETE FROM diary_entries WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        }
    }
    public List<DiaryEntry> findByUserId(Long userId) throws SQLException {
        String sql = "SELECT * FROM diary_entries WHERE user_id = ? ORDER BY created_at DESC";
        List<DiaryEntry> entries = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
        }

        return entries;
    }

    public List<DiaryEntry> findByUserIdAndMood(Long userId, EntryMood mood) throws SQLException {
        String sql = "SELECT * FROM diary_entries WHERE user_id = ? AND mood = ? ORDER BY created_at DESC";
        List<DiaryEntry> entries = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            pstmt.setString(2, mood.name());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
        }

        return entries;
    }

    public List<DiaryEntry> searchByContent(Long userId, String searchTerm) throws SQLException {
        String sql = "SELECT * FROM diary_entries WHERE user_id = ? AND content LIKE ? ORDER BY created_at DESC";
        List<DiaryEntry> entries = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setLong(1, userId);
            pstmt.setString(2, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                entries.add(mapResultSetToEntry(rs));
            }
        }

        return entries;
    }

    private DiaryEntry mapResultSetToEntry(ResultSet rs) throws SQLException {
        DiaryEntry entry = new DiaryEntry(
                rs.getString("title"),
                rs.getString("content"),
                rs.getLong("user_id"),
                EntryMood.valueOf(rs.getString("mood"))
        );

        entry.setId(rs.getLong("id"));
        entry.setEncrypted(rs.getBoolean("is_encrypted"));
        entry.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        entry.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

        return entry;
    }
}
