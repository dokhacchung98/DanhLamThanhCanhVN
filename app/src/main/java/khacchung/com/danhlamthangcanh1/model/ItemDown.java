package khacchung.com.danhlamthangcanh1.model;

public class ItemDown {
    private String url;
    private String id;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ItemDown(String url, String id) {
        this.url = url;
        this.id = id;
    }

    public ItemDown() {
    }
}
