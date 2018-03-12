package com.tencent.cos.xml;

import android.content.Context;

import com.tencent.cos.xml.common.Range;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.AbortMultiUploadRequest;
import com.tencent.cos.xml.model.object.AbortMultiUploadResult;
import com.tencent.cos.xml.model.object.AppendObjectRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadRequest;
import com.tencent.cos.xml.model.object.CompleteMultiUploadResult;
import com.tencent.cos.xml.model.object.DeleteObjectRequest;
import com.tencent.cos.xml.model.object.DeleteObjectResult;
import com.tencent.cos.xml.model.object.GetObjectRequest;
import com.tencent.cos.xml.model.object.GetObjectResult;
import com.tencent.cos.xml.model.object.InitMultipartUploadRequest;
import com.tencent.cos.xml.model.object.InitMultipartUploadResult;
import com.tencent.cos.xml.model.object.ListPartsRequest;
import com.tencent.cos.xml.model.object.ListPartsResult;
import com.tencent.cos.xml.model.object.PutObjectRequest;
import com.tencent.cos.xml.model.object.PutObjectResult;
import com.tencent.cos.xml.model.object.UploadPartRequest;
import com.tencent.cos.xml.model.object.UploadPartResult;
import com.tencent.cos.xml.transfer.ResponseFileBodySerializer;
import com.tencent.cos.xml.transfer.ResponseXmlS3BodySerializer;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.common.QCloudClientException;
import com.tencent.qcloud.core.common.QCloudResultListener;
import com.tencent.qcloud.core.common.QCloudServiceException;
import com.tencent.qcloud.core.http.HttpConstants;
import com.tencent.qcloud.core.http.HttpResult;
import com.tencent.qcloud.core.http.HttpTask;
import com.tencent.qcloud.core.http.QCloudHttpClient;
import com.tencent.qcloud.core.http.QCloudHttpRequest;
import com.tencent.qcloud.core.logger.QCloudLogger;

import java.util.List;

/**
 * Created by bradyxiao on 2017/11/30.
 */

public class CosXmlSimpleService implements SimpleCosXml {
    protected QCloudHttpClient client;
    protected QCloudCredentialProvider credentialProvider;
    protected String scheme;
    protected String region;
    protected String appid;
    protected String tag = "CosXml";

    /**
     * cos android SDK 服务
     * @param context Application 上下文{@link android.app.Application}
     * @param configuration cos android SDK 服务配置{@link CosXmlServiceConfig}
     * @param qCloudCredentialProvider cos android SDK 签名提供者 {@link QCloudCredentialProvider}
     */
    public CosXmlSimpleService(Context context, CosXmlServiceConfig configuration, QCloudCredentialProvider qCloudCredentialProvider){
        QCloudLogger.setUp(context);
        client = QCloudHttpClient.getDefault();
        client.addVerifiedHost("*.myqcloud.com");
        client.setDebuggable(configuration.isDebuggable());
        scheme = configuration.getProtocol();
        region = configuration.getRegion();
        appid = configuration.getAppid();
        credentialProvider = qCloudCredentialProvider;
    }

    /** 构建请求 */
    protected <T1 extends CosXmlRequest, T2 extends CosXmlResult> QCloudHttpRequest buildHttpRequest(T1 cosXmlRequest, T2 cosXmlResult) throws CosXmlClientException {
        cosXmlRequest.checkParameters();
        String host = cosXmlRequest.getHost(appid, region);
        QCloudHttpRequest.Builder<T2> httpRequestBuilder = new QCloudHttpRequest.Builder<T2>()
                .method(cosXmlRequest.getMethod())
                .scheme(scheme)
                .host(host)
                .path(cosXmlRequest.getPath())
                .addHeader(HttpConstants.Header.HOST, host)
                .userAgent(CosXmlServiceConfig.DEFAULT_USER_AGENT)
                .setUseCache(false)
                .tag(tag)
                .signer("CosXmlSigner", cosXmlRequest.getSignSourceProvider());

        httpRequestBuilder.query(cosXmlRequest.getQueryString());
        httpRequestBuilder.addHeaders(cosXmlRequest.getRequestHeaders());

        if(cosXmlRequest.isNeedMD5()){
            httpRequestBuilder.contentMD5();
        }

        if(cosXmlRequest.getRequestBody() != null){
              httpRequestBuilder.body(cosXmlRequest.getRequestBody());
        }

        if(cosXmlRequest instanceof GetObjectRequest){
            String absolutePath = ((GetObjectRequest) cosXmlRequest).getDownloadPath();
            Range range = ((GetObjectRequest) cosXmlRequest).getRange();
            long start = 0;
            if(range != null){
                start = range.getStart();
            }
            httpRequestBuilder.converter(new ResponseFileBodySerializer<T2>((GetObjectResult) cosXmlResult, absolutePath, start));
        }else {
            httpRequestBuilder.converter(new ResponseXmlS3BodySerializer<T2>(cosXmlResult));
        }

        QCloudHttpRequest httpRequest = httpRequestBuilder.build();
        return httpRequest;
    }

    /** 同步执行 */
    protected <T1 extends CosXmlRequest, T2 extends  CosXmlResult> T2 execute(T1 cosXmlRequest, T2 cosXmlResult)
            throws CosXmlClientException, CosXmlServiceException {
        try {
            QCloudHttpRequest<T2> httpRequest = buildHttpRequest(cosXmlRequest, cosXmlResult);
            HttpTask<T2> httpTask = client.resolveRequest(httpRequest, credentialProvider);

            cosXmlRequest.setTask(httpTask);

            if(cosXmlRequest instanceof AppendObjectRequest){
                httpTask.addProgressListener(((AppendObjectRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof PutObjectRequest){
                httpTask.addProgressListener(((PutObjectRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof UploadPartRequest){
                httpTask.addProgressListener(((UploadPartRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof GetObjectRequest){
                httpTask.addProgressListener(((GetObjectRequest) cosXmlRequest).getProgressListener());
            }
            return httpTask.executeNow().content();
        } catch (QCloudServiceException e) {
            throw (CosXmlServiceException) e;
        } catch (QCloudClientException e) {
           throw new CosXmlClientException(e);
        }
    }

    /** 异步执行 */
    protected  <T1 extends CosXmlRequest, T2 extends  CosXmlResult> void schedule(final T1 cosXmlRequest, T2 cosXmlResult,
                                                                              final CosXmlResultListener cosXmlResultListener) {

        QCloudResultListener<HttpResult<T2>> qCloudResultListener = new QCloudResultListener<HttpResult<T2>>() {
            @Override
            public void onSuccess(HttpResult<T2> result) {
                cosXmlResultListener.onSuccess(cosXmlRequest, result.content());
            }

            @Override
            public void onFailure(QCloudClientException clientException, QCloudServiceException serviceException) {
                if(clientException != null){
                    cosXmlResultListener.onFail(cosXmlRequest, new CosXmlClientException(clientException), null);
                }else {
                    cosXmlResultListener.onFail(cosXmlRequest,null, (CosXmlServiceException)serviceException);
                }

            }
        };

        try {
            QCloudHttpRequest<T2> httpRequest = buildHttpRequest(cosXmlRequest, cosXmlResult);
            HttpTask<T2> httpTask = client.resolveRequest(httpRequest, credentialProvider);

            cosXmlRequest.setTask(httpTask);

            if(cosXmlRequest instanceof AppendObjectRequest){
                httpTask.addProgressListener(((AppendObjectRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof PutObjectRequest){
                httpTask.addProgressListener(((PutObjectRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof UploadPartRequest){
                httpTask.addProgressListener(((UploadPartRequest) cosXmlRequest).getProgressListener());
            }else if(cosXmlRequest instanceof GetObjectRequest){
                httpTask.addProgressListener(((GetObjectRequest) cosXmlRequest).getProgressListener());
            }

            httpTask.schedule().addResultListener(qCloudResultListener);
        }catch (QCloudClientException e) {
            cosXmlResultListener.onFail(cosXmlRequest, new CosXmlClientException(e),
                    null);
        }
    }

    /**
     * 获取请求的访问地址
     * @param cosXmlRequest
     * @return String
     */
    public String getAccessUrl(CosXmlRequest cosXmlRequest){
        String host = cosXmlRequest.getHost(appid, region);
        String path = cosXmlRequest.getPath();
        return host + path;
    }

    /**
     * <p>
     * 初始化分块上传的同步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#initMultipartUpload(InitMultipartUploadRequest request)}
     *</p>
     */
    @Override
    public InitMultipartUploadResult initMultipartUpload(InitMultipartUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new InitMultipartUploadResult());
    }

    /**
     * <p>
     * 初始化分块上传的异步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#initMultipartUploadAsync(InitMultipartUploadRequest request, CosXmlResultListener cosXmlResultListener)}
     *</p>
     */
    @Override
    public void initMultipartUploadAsync(InitMultipartUploadRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new InitMultipartUploadResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 查询特定分块上传中的已上传的块的同步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#listParts(ListPartsRequest request)}
     *</p>
     */
    @Override
    public ListPartsResult listParts(ListPartsRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new ListPartsResult());
    }

    /**
     * <p>
     * 查询特定分块上传中的已上传的块的异步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#listPartsAsync(ListPartsRequest request, CosXmlResultListener cosXmlResultListener)}
     *</p>
     */
    @Override
    public void listPartsAsync(ListPartsRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new ListPartsResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 上传一个对象某个分片块的同步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#uploadPart(UploadPartRequest request)}
     *</p>
     */
    @Override
    public UploadPartResult uploadPart(UploadPartRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new UploadPartResult());
    }

    /**
     * <p>
     * 上传一个对象某个分片块的异步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#uploadPartAsync(UploadPartRequest request, CosXmlResultListener cosXmlResultListener)}
     *</p>
     */
    @Override
    public void uploadPartAsync(UploadPartRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new UploadPartResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 舍弃一个分块上传且删除已上传的分片块的同步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#abortMultiUpload(AbortMultiUploadRequest request)}
     *</p>
     */
    @Override
    public AbortMultiUploadResult abortMultiUpload(AbortMultiUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new AbortMultiUploadResult());
    }

    /**
     * <p>
     * 舍弃一个分块上传且删除已上传的分片块的异步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#abortMultiUploadAsync(AbortMultiUploadRequest request, CosXmlResultListener cosXmlResultListener)}
     *</p>
     */
    @Override
    public void abortMultiUploadAsync(AbortMultiUploadRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new AbortMultiUploadResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 完成整个分块上传的同步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#completeMultiUpload(CompleteMultiUploadRequest request)}
     *</p>
     */
    @Override
    public CompleteMultiUploadResult completeMultiUpload(CompleteMultiUploadRequest request) throws CosXmlClientException, CosXmlServiceException {
        CompleteMultiUploadResult completeMultiUploadResult =  new CompleteMultiUploadResult();
        completeMultiUploadResult.accessUrl = getAccessUrl(request);
        return execute(request, completeMultiUploadResult);
    }

    /**
     * <p>
     * 完成整个分块上传的异步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#completeMultiUploadAsync(CompleteMultiUploadRequest request, CosXmlResultListener cosXmlResultListener)}
     *</p>
     */
    @Override
    public void completeMultiUploadAsync(CompleteMultiUploadRequest request, CosXmlResultListener cosXmlResultListener) {
        CompleteMultiUploadResult completeMultiUploadResult =  new CompleteMultiUploadResult();
        completeMultiUploadResult.accessUrl = getAccessUrl(request);
        schedule(request, completeMultiUploadResult, cosXmlResultListener);
    }

    /**
     * <p>
     * 删除 COS 上单个对象的同步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#deleteObject(DeleteObjectRequest request)}
     *</p>
     */
    @Override
    public DeleteObjectResult deleteObject(DeleteObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new DeleteObjectResult());
    }

    /**
     * <p>
     * 删除 COS 上单个对象的异步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#deleteObjectAsync(DeleteObjectRequest request, CosXmlResultListener cosXmlResultListener)}
     *</p>
     */
    @Override
    public void deleteObjectAsync(DeleteObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new DeleteObjectResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 获取 COS 对象的同步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#getObject(GetObjectRequest request)}
     *</p>
     */
    @Override
    public GetObjectResult getObject(GetObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        return execute(request, new GetObjectResult());
    }

    /**
     * <p>
     * 获取 COS 对象的异步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#getObjectAsync(GetObjectRequest request, CosXmlResultListener cosXmlResultListener)}
     *</p>
     */
    @Override
    public void getObjectAsync(GetObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        schedule(request, new GetObjectResult(), cosXmlResultListener);
    }

    /**
     * <p>
     * 简单上传的同步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#putObject(PutObjectRequest request)}
     *</p>
     */
    @Override
    public PutObjectResult putObject(PutObjectRequest request) throws CosXmlClientException, CosXmlServiceException {
        PutObjectResult putObjectResult =  new PutObjectResult();
        putObjectResult.accessUrl = getAccessUrl(request);
        return execute(request, putObjectResult);
    }

    /**
     * <p>
     * 简单上传的异步方法.&nbsp;
     *
     * 详细介绍，请查看:{@link  SimpleCosXml#putObjectAsync(PutObjectRequest request, CosXmlResultListener cosXmlResultListener)}
     *</p>
     */
    @Override
    public void putObjectAsync(PutObjectRequest request, CosXmlResultListener cosXmlResultListener) {
        PutObjectResult putObjectResult =  new PutObjectResult();
        putObjectResult.accessUrl = getAccessUrl(request);
        schedule(request, putObjectResult, cosXmlResultListener);
    }

    /**
     * 取消请求任务.&nbsp;
     * 详细介绍，请查看:{@link  SimpleCosXml#cancel(CosXmlRequest)}
     */
    @Override
    public void cancel(CosXmlRequest cosXmlRequest) {
        if (cosXmlRequest != null && cosXmlRequest.getHttpTask() != null) {
            cosXmlRequest.getHttpTask().cancel();
        }
    }

    /**
     * 取消所有的请求任务.&nbsp;
     * 详细介绍，请查看:{@link  SimpleCosXml#cancelAll()}
     */
    @Override
    public void cancelAll() {
        List<HttpTask> tasks = client.getTasksByTag(tag);
        for(HttpTask task : tasks){
            task.cancel();
        }
    }

    /**
     * 释放所有的请求.&nbsp;
     * 详细介绍，请查看:{@link  SimpleCosXml#release()}
     */
    @Override
    public void release() {
        cancelAll();
    }
}