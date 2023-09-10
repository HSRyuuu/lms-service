package com.zerobase.fastlms.banner.model;

import lombok.Data;
import lombok.ToString;

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
}
