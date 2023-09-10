package com.zerobase.fastlms.banner.service;

import com.zerobase.fastlms.banner.entity.Banner;
import com.zerobase.fastlms.banner.model.BannerInput;
import com.zerobase.fastlms.banner.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService{
    private final BannerRepository bannerRepository;

    @Override
    public List<Banner> list() {
        List<Banner> list = bannerRepository.findAllByOrderBySortValueDesc();
        return list;
    }

    @Override
    public boolean add(BannerInput bannerInput) {
        Banner banner = Banner.builder()
                .bannerName(bannerInput.getBannerName())
                .linkedUrl(bannerInput.getLinkedUrl())
                .sortValue(bannerInput.getSortValue())
                .newWindowYn(bannerInput.isNewWindowYn())
                .usingYn(bannerInput.isUsingYn())
                .alterText(bannerInput.getAlterText())
                .fileName(bannerInput.getFileName())
                .urlFileName(bannerInput.getUrlFileName())
                .build();
        bannerRepository.save(banner);
        return true;
    }

    @Override
    public Banner findById(long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 배너입니다."));
        return banner;
    }

    @Override
    public boolean update(BannerInput parameter) {
        Optional<Banner> find = bannerRepository.findById(parameter.getId());
        if(!find.isPresent()){
            return false;
        }
        Banner banner = find.get();
        banner.setBannerName(parameter.getBannerName());
        banner.setLinkedUrl(parameter.getLinkedUrl());
        banner.setSortValue(parameter.getSortValue());
        banner.setNewWindowYn(parameter.isNewWindowYn());
        banner.setUsingYn(parameter.isUsingYn());
        banner.setAlterText(parameter.getAlterText());
        banner.setFileName(parameter.getFileName());
        banner.setUrlFileName(parameter.getUrlFileName());

        bannerRepository.save(banner);
        return true;
    }

    @Override
    public boolean delete(long id) {
        bannerRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Banner> frontList() {

        return bannerRepository.findAllByUsingYnIsTrue();
    }
}
