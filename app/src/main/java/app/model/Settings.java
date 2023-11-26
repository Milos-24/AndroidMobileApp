package app.model;

public class Settings {
    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public boolean isNotifications() {
        return notifications;
    }

    public void setNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    private String languages;
    private String notificationTime;
    private boolean notifications;
    public Settings( String languages, String notificationTime, boolean notifications)
    {

        this.languages = languages;
        this.notificationTime = notificationTime;
        this.notifications = notifications;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "languages='" + languages + '\'' +
                ", notificationTime='" + notificationTime + '\'' +
                ", notifications=" + notifications +
                '}';
    }
}
