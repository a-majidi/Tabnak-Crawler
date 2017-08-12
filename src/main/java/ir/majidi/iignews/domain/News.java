package ir.majidi.iignews.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A News.
 */
@Entity
@Table(name = "news")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class News implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "news_id")
    private Integer newsID;

    @Column(name = "news_title",length = 4010)
    private String newsTitle;

    @Column(name = "news_description",length = 4010)
    private String newsDescription;

    @Column(name = "news_body",length = 4010)
    private String newsBody;

    @Column(name = "news_keywords",length = 4010)
    private String newsKeywords;

    @Column(name = "news_category",length = 4010)
    private String newsCategory;

    @Column(name = "news_url",length = 4010)
    private String newsUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNewsID() {
        return newsID;
    }

    public News newsID(Integer newsID) {
        this.newsID = newsID;
        return this;
    }

    public void setNewsID(Integer newsID) {
        this.newsID = newsID;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public News newsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
        return this;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsDescription() {
        return newsDescription;
    }

    public News newsDescription(String newsDescription) {
        this.newsDescription = newsDescription;
        return this;
    }

    public void setNewsDescription(String newsDescription) {
        this.newsDescription = newsDescription;
    }

    public String getNewsBody() {
        return newsBody;
    }

    public News newsBody(String newsBody) {
        this.newsBody = newsBody;
        return this;
    }

    public void setNewsBody(String newsBody) {
        this.newsBody = newsBody;
    }

    public String getNewsKeywords() {
        return newsKeywords;
    }

    public News newsKeywords(String newsKeywords) {
        this.newsKeywords = newsKeywords;
        return this;
    }

    public void setNewsKeywords(String newsKeywords) {
        this.newsKeywords = newsKeywords;
    }

    public String getNewsCategory() {
        return newsCategory;
    }

    public News newsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
        return this;
    }

    public void setNewsCategory(String newsCategory) {
        this.newsCategory = newsCategory;
    }

    public String getNewsUrl() {
        return newsUrl;
    }

    public News newsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
        return this;
    }

    public void setNewsUrl(String newsUrl) {
        this.newsUrl = newsUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        News news = (News) o;
        if (news.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), news.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "News{" +
            "id=" + getId() +
            ", newsID='" + getNewsID() + "'" +
            ", newsTitle='" + getNewsTitle() + "'" +
            ", newsDescription='" + getNewsDescription() + "'" +
            ", newsBody='" + getNewsBody() + "'" +
            ", newsKeywords='" + getNewsKeywords() + "'" +
            ", newsCategory='" + getNewsCategory() + "'" +
            ", newsUrl='" + getNewsUrl() + "'" +
            "}";
    }
}
