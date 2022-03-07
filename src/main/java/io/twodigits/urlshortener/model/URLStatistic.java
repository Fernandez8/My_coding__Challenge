package io.twodigits.urlshortener.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Entity
public class URLStatistic {
    /**
     * The unique ID of an URLStatistic
     */
    @Id
    private String id;
    /**
     * number of calls
     */
    private Integer nbCalls;

    /**
     * last date time call
     */
    private LocalDateTime dateTime;

    /**
     * last userAgent
     */
    private String userAgent;

    /**
     * last userAgent
     */
    private String referrer;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="url_id", nullable=false)
    private URL url;

    public Integer getNbCalls() {
        return nbCalls;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNbCalls(Integer nbCalls) {
        this.nbCalls = nbCalls;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }
}
