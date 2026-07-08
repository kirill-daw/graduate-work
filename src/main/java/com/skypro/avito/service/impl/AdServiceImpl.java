package com.skypro.avito.service.impl;

import com.skypro.avito.dto.Ad;
import com.skypro.avito.dto.Ads;
import com.skypro.avito.dto.CreateOrUpdateAd;
import com.skypro.avito.dto.ExtendedAd;
import com.skypro.avito.entity.AdEntity;
import com.skypro.avito.entity.UserEntity;
import com.skypro.avito.mapper.AdMapper;
import com.skypro.avito.repository.AdRepository;
import com.skypro.avito.repository.UserRepository;
import com.skypro.avito.service.AdService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdServiceImpl implements AdService {

    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final AdMapper adMapper;

    public AdServiceImpl(AdRepository adRepository,
                         UserRepository userRepository,
                         AdMapper adMapper) {
        this.adRepository = adRepository;
        this.userRepository = userRepository;
        this.adMapper = adMapper;
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
                .orElseThrow(() -> new RuntimeException("User not found"));
        AdEntity adEntity = adMapper.toEntity(createOrUpdateAd);
        adEntity.setAuthor(user);
        adEntity.setImage(image.getOriginalFilename());
        adEntity.setCreatedAt(System.currentTimeMillis());
        AdEntity saved = adRepository.save(adEntity);
        return adMapper.toAd(saved);
    }

    @Override
    public ExtendedAd getAd(Integer id) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));
        return adMapper.toExtendedAd(adEntity);
    }

    @Override
    public Ad updateAd(Integer id, CreateOrUpdateAd createOrUpdateAd) {
        AdEntity adEntity = adRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));
        adEntity.setTitle(createOrUpdateAd.getTitle());
        adEntity.setPrice(createOrUpdateAd.getPrice());
        adEntity.setDescription(createOrUpdateAd.getDescription());
        AdEntity saved = adRepository.save(adEntity);
        return adMapper.toAd(saved);
    }

    @Override
    public void removeAd(Integer id) {
        adRepository.deleteById(id);
    }

    @Override
    public Ads getAdsMe(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<AdEntity> adEntities = adRepository.findByAuthorId(user.getId());
        List<Ad> ads = adEntities.stream()
                .map(adMapper::toAd)
                .collect(Collectors.toList());
        return new Ads(ads.size(), ads);
    }
}
