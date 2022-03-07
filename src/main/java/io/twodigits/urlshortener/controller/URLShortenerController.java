package io.twodigits.urlshortener.controller;

import io.twodigits.urlshortener.model.URL;
import io.twodigits.urlshortener.service.URLShortenerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Optional;

@RestController
@RequestMapping(path = "/url-shorteners")
public class URLShortenerController {
    private final Logger log = LoggerFactory.getLogger(URLShortenerController.class);

    private final URLShortenerService urlShortenerService;

    public URLShortenerController(URLShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    /**
     * {@code POST  /url-shorteners}  : Creates a new url short.
     *
     * @param url the url to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new url or {@code 400 (badRequest)}
     */
    @PostMapping
    public ResponseEntity<URL> createUrl(@RequestBody URL url) throws URISyntaxException {
        log.debug("REST request to save url : {}", url);
        if (url.getId() != null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(urlShortenerService.addURL(url.getUser(), url.getURL()));
    }


    /**
     * {@code GET /url-shorteners} : get all urls with all the details
     *
     * @param user the user,s list of urls.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all urls of user.
     */
    @GetMapping
    public ResponseEntity<Iterable<URL>> getAllUrl(@RequestParam String user) {
        log.debug("REST request to get all user {} urls", user);
        if (!StringUtils.hasText(user)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(urlShortenerService.listURLs(user));
    }

    /**
     * {@code GET /url-shorteners/:id} : get the "id" user.
     *
     * @param id the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "id" url, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<URL> getURL(@PathVariable String id) {
        log.debug("REST request to get Url : {}", id);
        if (!StringUtils.hasText(id)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<URL> urlOptional = urlShortenerService.getURL(id);
        return wrapOrNotFoundResponse(urlOptional);
    }

    /**
     * {@code GET /url-shorteners/:id} : get the "id" user.
     *
     * @param id the login of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the "id" url, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{user}/{id}")
    public ResponseEntity<URL> getUserURL(@PathVariable String user, @PathVariable String id) {
        log.debug("REST request to get Url : {}", id);
        if (!StringUtils.hasText(id)) {
            return ResponseEntity.badRequest().build();
        }
        Optional<URL> urlOptional = urlShortenerService.getURL(user, id);
        return wrapOrNotFoundResponse(urlOptional);
    }

    private ResponseEntity<URL> wrapOrNotFoundResponse(Optional<URL> urlOptional) {
        return urlOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE /url-shorteners} : delete the "url".
     *
     * @param url the url to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)} or {@code 400 (badRequest)}
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@RequestBody URL url) {
        log.debug("REST request to delete Url: {}", url);
        if (url.getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        urlShortenerService.deleteURL(url.getUser(), url.getId());
        return ResponseEntity.noContent().build();
    }
}
