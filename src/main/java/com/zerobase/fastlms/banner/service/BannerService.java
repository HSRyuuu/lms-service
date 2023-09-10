package com.zerobase.fastlms.banner.service;

import com.zerobase.fastlms.banner.entity.Banner;
import com.zerobase.fastlms.banner.model.BannerInput;

import java.util.List;

public interface BannerService {

    List<Banner> list();
    boolean add(BannerInput parameter);

    Banner findById(long id);

    List<Banner> frontList();

    boolean update(BannerInput parameter);

    boolean delete(long id);

}
