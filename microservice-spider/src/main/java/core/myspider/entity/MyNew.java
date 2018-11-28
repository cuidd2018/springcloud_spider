package core.myspider.entity;

/**
 * 自定义新闻类
 */
public class MyNew {

    private Integer new_id;
    private String title;
    private String URL;
    private String content;
    private String media;
    private String anthor;
    private String time;

    public MyNew() {

    }

    @Override
    public String toString() {
        return "MyNew{" +
                "new_id=" + new_id +
                ", title='" + title + '\'' +
                ", URL='" + URL + '\'' +
                ", content='" + content + '\'' +
                ", media='" + media + '\'' +
                ", anthor='" + anthor + '\'' +
                ", time='" + time + '\'' +
                '}';
    }

    public Integer getNew_id() {
        return new_id;
    }

    public void setNew_id(Integer new_id) {
        this.new_id = new_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getAnthor() {
        return anthor;
    }

    public void setAnthor(String anthor) {
        this.anthor = anthor;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
