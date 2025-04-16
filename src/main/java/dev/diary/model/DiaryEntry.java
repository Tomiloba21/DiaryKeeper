package dev.sec3.se3.model;

public class DiaryEntry  extends BaseEntity {
    private String title;
    private String content;
    private Long userId;
    private EntryMood mood;
    private boolean isEncrypted;

    public DiaryEntry(String title, String content, Long userId, EntryMood mood) {
        super();
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.mood = mood;
        this.isEncrypted = false;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public EntryMood getMood() {
        return mood;
    }

    public void setMood(EntryMood mood) {
        this.mood = mood;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }
}
