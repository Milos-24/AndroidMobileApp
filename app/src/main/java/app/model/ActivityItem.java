package app.model;

public class ActivityItem {
    private String title;
    private String description;
    private String date;
    private String time;
    private String lanLeng;
    private String url1;
    private String url2;
    private String url3;
    private String type;

    public ActivityItem(String title, String description, String date, String time, String lanLeng, String url1, String url2, String url3, String type) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.lanLeng = lanLeng;
        this.url1 = url1;
        this.url2 = url2;
        this.url3 = url3;
        this.type = type;
    }

    public ActivityItem() {
        // Default constructor
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLanLeng() {
        return lanLeng;
    }

    public void setLanLeng(String lanLeng) {
        this.lanLeng = lanLeng;
    }

    public String getUrl1() {
        return url1;
    }

    public void setUrl1(String url1) {
        this.url1 = url1;
    }

    public String getUrl2() {
        return url2;
    }

    public void setUrl2(String url2) {
        this.url2 = url2;
    }

    public String getUrl3() {
        return url3;
    }

    public void setUrl3(String url3) {
        this.url3 = url3;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
