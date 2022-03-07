package io.twodigits.urlshortener.controller;

import io.twodigits.urlshortener.model.URL;
import io.twodigits.urlshortener.model.URLStatistic;
import io.twodigits.urlshortener.repository.URLStatisticRepository;
import io.twodigits.urlshortener.service.URLShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
public class OpenUrlController {
    private final Logger log = LoggerFactory.getLogger(URLShortenerController.class);

    private final URLShortenerService urlShortenerService;
    private final URLStatisticRepository urlStatisticRepository;

    public OpenUrlController(URLShortenerService urlShortenerService, URLStatisticRepository urlStatisticRepository) {
        this.urlShortenerService = urlShortenerService;
        this.urlStatisticRepository = urlStatisticRepository;
    }

    @GetMapping("/{id}")
    public RedirectView redirectUrl(@PathVariable String id, HttpServletRequest request) {
        log.debug("Received shortened url to redirect: {}", id);
        Optional<URL> url = urlShortenerService.getURL(id);
        RedirectView redirectView = new RedirectView();
        if (url.isEmpty()) {
            return redirectView;
        }
        String redirectUrl = url.get().getURL();
        log.debug("Redirect to: {}", redirectUrl);
        redirectView.setUrl(redirectUrl);
        updateStatistics(url.get(), request);
        return redirectView;
    }

    private void updateStatistics(URL url, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String id = buildId(url.getId(), userAgent);
        Optional<URLStatistic> urlStatistic = urlStatisticRepository.findById(id);
        urlStatistic.ifPresentOrElse((urlStat) -> {
            updateStatistic(url, userAgent, urlStat);
        }, () -> {
            URLStatistic newUrlStat = new URLStatistic();
            newUrlStat.setId(id);
            updateStatistic(url, userAgent, newUrlStat);
        });

    }

    private void updateStatistic(URL url, String userAgent, URLStatistic urlStat) {
        int nbCall = 1;
        if (urlStat.getNbCalls() != null){
            nbCall = urlStat.getNbCalls() + 1;
        }
        urlStat.setNbCalls(nbCall);
        urlStat.setDateTime(LocalDateTime.now());
        urlStat.setUserAgent(userAgent);
        urlStat.setUrl(url);
        urlStatisticRepository.save(urlStat);
    }

    private String buildId(String id, String userAgent) {
        return String.format("id-%s-%s", id, userAgent);
    }
}
