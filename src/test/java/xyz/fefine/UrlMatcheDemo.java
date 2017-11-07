package xyz.fefine;

/**
 * Created by feng_sh on 17-11-5.
 */
public class UrlMatcheDemo {
    public static void main(String[] args) {
        String url = "http://bangumi.tv/anime/browser?page=10";
        System.out.println(url.matches("http://bangumi\\.tv/anime/browser\\?page=\\d+"));
        System.out.println("(20)".matches("\\(\\d+\\)"));
    }
}
