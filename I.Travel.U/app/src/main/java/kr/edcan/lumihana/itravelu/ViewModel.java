package kr.edcan.lumihana.itravelu;

/**
 * Created by kimok_000 on 2016-10-30.
 */
public class ViewModel {
    private String title;
    private String content;
    private String imageUrl;

    public ViewModel(String title, String content, String imageUrl) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
