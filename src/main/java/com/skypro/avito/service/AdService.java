package com.skypro.avito.service;

import com.skypro.avito.dto.Ad;
import com.skypro.avito.dto.Ads;
import com.skypro.avito.dto.CreateOrUpdateAd;
import com.skypro.avito.dto.ExtendedAd;

public interface AdService {

    Ads getAllAds();

    Ad addAd(CreateOrUpdateAd createOrUpdateAd, String imagePath, String username);

    ExtendedAd getAd(Integer id);

    Ad updateAd(Integer id, CreateOrUpdateAd createOrUpdateAd);

    void removeAd(Integer id);

    Ads getAdsMe(String username);
}
