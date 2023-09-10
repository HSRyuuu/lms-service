package com.zerobase.fastlms.banner.model;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class BannerInput {
    long id;
    String bannerName;
    String linkedUrl;
    long sortValue;
    boolean newWindowYn;
    boolean usingYn;
    String alterText;

    String fileName;
    String urlFileName;
    LocalDateTime createDt;
}
