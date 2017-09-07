package com.tencent.qcloud.network.response.serializer.body;

import com.tencent.qcloud.network.QCloudResult;
import com.tencent.qcloud.network.annotation.SequenceFieldKeySorter;
import com.tencent.qcloud.network.exception.QCloudException;
import com.tencent.qcloud.network.logger.QCloudLogger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Response;

/**
 *
 * Cos xml api下，成功和失败返回的xml文件的根目录节点不一致，导致解析困难，
 *
 * 这里先将response返回的xml文件添加一个相同的根节点
 *
 *
 * Copyright 2010-2017 Tencent Cloud. All Rights Reserved.
 */

public class ResponseXmlS3BodySerializer implements ResponseBodySerializer {



    private Logger logger = LoggerFactory.getLogger(ResponseXmlBodySerializer.class);

    private Class cls;

    private final String S3_RESPONSE_ROOT_NODE = "s3_response_root_node";

    public ResponseXmlS3BodySerializer(Class cls) {

        this.cls = cls;
    }

    @Override
    public QCloudResult serialize(Response response) throws QCloudException {

        if (response == null) {

            return null;
        }

        if (response.body() != null) {

            try {
                String xmlString = response.body().string();

                // 去掉<?xml version="1.0" encoding="UTF-8"?>
                int index = xmlString.lastIndexOf("?>");
                if (index >= 0) {
                    if (xmlString.length() > 2) {
                        xmlString = xmlString.substring(index+2);
                    } else {
                        return null;
                    }
                }

                // 增加统一的根节点
                xmlString = String.format(Locale.ENGLISH, "<%s>%s%s%s</%s>",S3_RESPONSE_ROOT_NODE,
                        System.getProperty("line.separator"), xmlString, System.getProperty("line.separator"),
                        S3_RESPONSE_ROOT_NODE);

                XStream xStream = new XStream(new PureJavaReflectionProvider(new
                        FieldDictionary(new SequenceFieldKeySorter())));

                QCloudLogger.debug(logger, xmlString);

                xStream.processAnnotations(cls);
                xStream.alias(S3_RESPONSE_ROOT_NODE, cls);

                //QCloudLogger.debug(logger, xStream.fromXML(xmlString).toString());
                return (QCloudResult) xStream.fromXML(xmlString);

            } catch (IOException e) {

                // TODO: 17/9/5 这里竟然没有抛异常？
                e.printStackTrace();
            }
        }

        return ResponseSerializerHelper.noBodyResult(cls, response);
    }


}
