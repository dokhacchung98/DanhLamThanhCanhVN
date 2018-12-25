package khacchung.com.danhlamthangcanh1.model;

public class ConvertNameVideo {
    public static String convertNameVideo(String url) {
        String name;
        name = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        return name;
    }
}
