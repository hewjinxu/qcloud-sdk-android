package com.tencent.qcloud.core.network.auth;

/**
 * Created by wjielai on 2017/9/21.
 * <p>
 * Copyright (c) 2010-2017 Tencent Cloud. All rights reserved.
 */

public interface QCloudRawCredentials extends QCloudCredentials {

    /**
     * 返回永久 secretKey
     *
     * @return secretKey
     */
    String getSecretKey();
}
