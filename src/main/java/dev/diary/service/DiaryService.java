package dev.diary.service;

import dev.diary.dao.DiaryEntryDAO;
import dev.diary.model.DiaryEntry;
import dev.diary.model.EntryMood;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DiaryService {
    private final DiaryEntryDAO diaryEntryDAO;

    public DiaryService() {
        this.diaryEntryDAO = new DiaryEntryDAO();
    }

    public DiaryEntry saveEntry(DiaryEntry entry) throws Exception {
        // Set timestamps if not already set
        LocalDateTime now = LocalDateTime.now();
        if (entry.getCreatedAt() == null) {
            entry.setCreatedAt(now);
        }
        entry.setUpdatedAt(now);

        return diaryEntryDAO.save(entry);
    }

    public void updateEntry(DiaryEntry entry) throws Exception {
        entry.setUpdatedAt(LocalDateTime.now());
        diaryEntryDAO.update(entry);
    }

    public void deleteEntry(Long entryId) throws Exception {
        diaryEntryDAO.delete(entryId);
    }

    public List<DiaryEntry> getUserEntries(Long userId) throws SQLException {
        return diaryEntryDAO.findByUserId(userId);
    }

    public List<DiaryEntry> searchEntries(Long userId, String searchTerm) throws SQLException {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getUserEntries(userId);
        }
        return diaryEntryDAO.searchByContent(userId, searchTerm.trim());
    }

    public List<DiaryEntry> getEntriesByMood(Long userId, EntryMood mood) throws SQLException {
        return diaryEntryDAO.findByUserIdAndMood(userId, mood);
    }

    public Optional<DiaryEntry> getEntryById(Long entryId) throws Exception {
        return diaryEntryDAO.findById(entryId);
    }

    public List<DiaryEntry> getRecentEntries(Long userId, int limit) throws SQLException {
        // You'll need to add this method to the DAO
        return diaryEntryDAO.findByUserId(userId).stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Helper method to validate entry
    private void validateEntry(DiaryEntry entry) throws IllegalArgumentException {
        if (entry == null) {
            throw new IllegalArgumentException("Entry cannot be null");
        }
        if (entry.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (entry.getContent() == null || entry.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Entry content cannot be empty");
        }
        if (entry.getMood() == null) {
            throw new IllegalArgumentException("Mood cannot be null");
        }
    }
}