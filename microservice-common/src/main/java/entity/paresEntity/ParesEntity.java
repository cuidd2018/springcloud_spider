package entity.paresEntity;

/**
 * @author 一杯咖啡
 * @desc 解析器配置信息
 * @createTime 2018-11-30-12:40
 */
public class ParesEntity {
    private String siteName;
    private String title_rule;
    private String content_rule;
    private String time_rule;
    private String media_rule;
    private String anthor_rule;

    public ParesEntity() {
    }

    public ParesEntity(String siteName, String title_rule, String content_rule, String time_rule, String media_rule, String anthor_rule) {
        this.siteName = siteName;
        this.title_rule = title_rule;
        this.content_rule = content_rule;
        this.time_rule = time_rule;
        this.media_rule = media_rule;
        this.anthor_rule = anthor_rule;
    }

    @Override
    public String toString() {
        return "ParesEntity{" +
                "siteName='" + siteName + '\'' +
                ", title_rule='" + title_rule + '\'' +
                ", content_rule='" + content_rule + '\'' +
                ", time_rule='" + time_rule + '\'' +
                ", media_rule='" + media_rule + '\'' +
                ", anthor_rule='" + anthor_rule + '\'' +
                '}';
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTitle_rule() {
        return title_rule;
    }

    public void setTitle_rule(String title_rule) {
        this.title_rule = title_rule;
    }

    public String getContent_rule() {
        return content_rule;
    }

    public void setContent_rule(String content_rule) {
        this.content_rule = content_rule;
    }

    public String getTime_rule() {
        return time_rule;
    }

    public void setTime_rule(String time_rule) {
        this.time_rule = time_rule;
    }

    public String getMedia_rule() {
        return media_rule;
    }

    public void setMedia_rule(String media_rule) {
        this.media_rule = media_rule;
    }

    public String getAnthor_rule() {
        return anthor_rule;
    }

    public void setAnthor_rule(String anthor_rule) {
        this.anthor_rule = anthor_rule;
    }
}
