package com.skypro.avito.service.impl;

import com.skypro.avito.dto.Ad;
import com.skypro.avito.dto.Ads;
import com.skypro.avito.dto.CreateOrUpdateAd;
import com.skypro.avito.dto.ExtendedAd;
import com.skypro.avito.entity.AdEntity;
import com.skypro.avito.entity.UserEntity;
import com.skypro.avito.exception.AdNotFoundException;
import com.skypro.avito.exception.UserNotFoundException;
import com.skypro.avito.mapper.AdMapper;
import com.skypro.avito.repository.AdRepository;
import com.skypro.avito.repository.UserRepository;
import com.skypro.avito.service.AdService;
import com.skypro.avito.service.ImageService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;
    private final ImageService imageService;

    public AdServiceImpl(AdRepository adRepository,
                         UserRepository userRepository,
                         AdMapper adMapper,
                         ImageService imageService) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.adMapper = adMapper;
        this.imageService = imageService;
    }

    @Override
    public Ads getAllAds() {
        List<AdEntity> adEntities = adRepository.findAll();
        List<Ad> ads = adEntities.stream()
                .map(adMapper::toAd)
                .collect(Collectors.toList());
        return new Ads(ads.size(), ads);
    }

    @Override
    public Ad addAd(CreateOrUpdateAd createOrUpdateAd, MultipartFile image, String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        AdEntity adEntity = adMapper.toEntity(createOrUpdateAd);
        adEntity.setAuthor(user);
        adEntity.setImage(imageService.saveAdImage(image));
        adEntity.setCreatedAt(System.currentTimeMillis());
        AdEntity saved = adRepository.save(adEntity);
        return adMapper.toAd(saved);
    }

    @Override
    public ExtendedAd getAd(Integer id) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException(id));
        return adMapper.toExtendedAd(adEntity);
    }

    @Override
    @PreAuthorize("@securityService.isAdOwner(#id, authentication.name) || hasRole('ADMIN')")
    public Ad updateAd(Integer id, CreateOrUpdateAd createOrUpdateAd) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException(id));
        adEntity.setTitle(createOrUpdateAd.getTitle());
        adEntity.setPrice(createOrUpdateAd.getPrice());
        adEntity.setDescription(createOrUpdateAd.getDescription());
        AdEntity saved = adRepository.save(adEntity);
        return adMapper.toAd(saved);
    }

    @Override
    @PreAuthorize("@securityService.isAdOwner(#id, authentication.name) || hasRole('ADMIN')")
    public void removeAd(Integer id) {
        adRepository.deleteById(id);
    }

    @Override
    public Ads getAdsMe(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        List<AdEntity> adEntities = adRepository.findAllByAuthorId(user.getId());
        List<Ad> ads = adEntities.stream()
                .map(adMapper::toAd)
                .collect(Collectors.toList());
        return new Ads(ads.size(), ads);
    }

    @Override
    @PreAuthorize("@securityService.isAdOwner(#id, authentication.name) || hasRole('ADMIN')")
    public void updateImage(Integer id, MultipartFile image) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new AdNotFoundException(id));
        adEntity.setImage(imageService.saveAdImage(image));
        adRepository.save(adEntity);
    }
}
