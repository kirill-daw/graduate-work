package com.skypro.avito.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skypro.avito.dto.Ad;
import com.skypro.avito.dto.Ads;
import com.skypro.avito.dto.CreateOrUpdateAd;
import com.skypro.avito.dto.ExtendedAd;
import com.skypro.avito.service.AdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/ads")
public class AdController {

    private static final Logger log = LoggerFactory.getLogger(AdController.class);

    private final AdService adService;
    private final ObjectMapper objectMapper;

    public AdController(AdService adService, ObjectMapper objectMapper) {
        this.adService = adService;
        this.objectMapper = objectMapper;
    }

    @Operation(summary = "Получение всех объявлений", operationId = "getAllAds")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class)))
    @GetMapping
    public ResponseEntity<Ads> getAllAds() {
        log.info("GET /ads called");
        Ads ads = adService.getAllAds();
        log.info("GET /ads returning {} ads", ads.getCount());
        return ResponseEntity.ok(ads);
    }

    @Operation(summary = "Добавление объявления", operationId = "addAd")
    @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ad.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Ad> addAd(
            @RequestParam("properties")
            @Parameter(description = "JSON с полями title, price, description",
                    schema = @Schema(implementation = CreateOrUpdateAd.class))
            String properties,
            @RequestParam("image")
            @Parameter(description = "Изображение объявления",
                    schema = @Schema(type = "string", format = "binary"))
            MultipartFile image,
            Authentication authentication) throws JsonProcessingException {
        CreateOrUpdateAd createOrUpdateAd = objectMapper.readValue(properties, CreateOrUpdateAd.class);
        Ad ad = adService.addAd(createOrUpdateAd, image, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ad);
    }

    @Operation(summary = "Получение информации об объявлении", operationId = "getAds")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ExtendedAd.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not found")
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAd> getAd(@PathVariable Integer id) {
        return ResponseEntity.ok(adService.getAd(id));
    }

    @Operation(summary = "Удаление объявления", operationId = "removeAd")
    @ApiResponse(responseCode = "204", description = "No Content")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeAd(@PathVariable Integer id) {
        adService.removeAd(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Обновление информации об объявлении", operationId = "updateAds")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ad.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PatchMapping("/{id}")
    public ResponseEntity<Ad> updateAd(@PathVariable Integer id,
                                       @RequestBody CreateOrUpdateAd createOrUpdateAd) {
        return ResponseEntity.ok(adService.updateAd(id, createOrUpdateAd));
    }

    @Operation(summary = "Получение объявлений авторизованного пользователя", operationId = "getAdsMe")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = Ads.class)))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping("/me")
    public ResponseEntity<Ads> getAdsMe(Authentication authentication) {
        String username = authentication.getName();
        log.info("getAdsMe called for user: {}", username);
        Ads ads = adService.getAdsMe(username);
        log.info("getAdsMe returning {} ads for user: {}", ads.getCount(), username);
        return ResponseEntity.ok(ads);
    }

    @Operation(summary = "Обновление картинки объявления", operationId = "updateImage")
    @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(mediaType = "application/octet-stream"))
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "403", description = "Forbidden")
    @ApiResponse(responseCode = "404", description = "Not found")
    @PatchMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<byte[]> updateImage(@PathVariable Integer id,
                                              @RequestParam("image") MultipartFile image) {
        adService.updateImage(id, image);
        return ResponseEntity.ok(new byte[0]);
    }
}
