package com.tencent.cos.xml;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.CosXmlResultListener;
import com.tencent.cos.xml.model.bucket.DeleteBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.DeleteBucketLifecycleResult;
import com.tencent.cos.xml.model.bucket.GetBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.GetBucketLifecycleResult;
import com.tencent.cos.xml.model.bucket.PutBucketLifecycleRequest;
import com.tencent.cos.xml.model.bucket.PutBucketLifecycleResult;
import com.tencent.cos.xml.model.tag.AbortIncompleteMultiUpload;
import com.tencent.cos.xml.model.tag.Rule;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bradyxiao on 2017/8/26.
 * author bradyxiao
 */
public class LifecycleRequestTest extends ApplicationTestCase {
    private static final String TAG = "Unit_Test";
    private QBaseServe qBaseServe;
    public volatile int hasCompleted = 0;

    public LifecycleRequestTest() {
        super(Application.class);
    }

    //service
    private void init() {
        if (qBaseServe == null) {
            qBaseServe = QBaseServe.getInstance(getContext());
        }
    }

    public boolean testPutBucketLifecycleRequest() throws Exception{
        PutBucketLifecycleRequest request = new PutBucketLifecycleRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        Rule rule = new Rule();
        rule.id = "lifeID";
        rule.status = "Enabled";
        //配置未完成分块上传的定期删除规则
        rule.abortIncompleteMultiUpload = new AbortIncompleteMultiUpload();
        rule.abortIncompleteMultiUpload.daysAfterInitiation = "1";
        request.setRuleList(rule);
        PutBucketLifecycleResult result =  qBaseServe.cosXmlService.putBucketLifecycle(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG, response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testPutBucketLifecycleRequest2() throws Exception{
        hasCompleted = 0;
        PutBucketLifecycleRequest request = new PutBucketLifecycleRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        Rule rule = new Rule();
        rule.id = "lifeID";
        rule.status = "Enabled";
        //配置未完成分块上传的定期删除规则
        rule.abortIncompleteMultiUpload = new AbortIncompleteMultiUpload();
        rule.abortIncompleteMultiUpload.daysAfterInitiation = "1";
        List<Rule> list = new ArrayList<Rule>();
        list.add(rule);
        request.setRuleList(list);
        qBaseServe.cosXmlService.putBucketLifecycleAsync(request, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders() + "|" + result.printError());
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        if(hasCompleted == 1){
            return true;
        }else {
            return false;
        }
    }

    public boolean testGetBucketLifecycleRequest() throws Exception{
        GetBucketLifecycleRequest request = new GetBucketLifecycleRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        GetBucketLifecycleResult result =  qBaseServe.cosXmlService.getBucketLifecycle(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG, response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testGetBucketLifecycleRequest2() throws Exception{
        hasCompleted = 0;
        GetBucketLifecycleRequest request2 = new GetBucketLifecycleRequest();
        request2.setBucket(qBaseServe.bucket);
        request2.setSign(600,null,null);
        qBaseServe.cosXmlService.getBucketLifecycleAsync(request2, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders() + "|" + result.printError());
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        if(hasCompleted == 1){
            return true;
        }else {
            return false;
        }
    }

    public boolean testDeleteBucketLifecycleRequest() throws Exception{
        DeleteBucketLifecycleRequest request = new DeleteBucketLifecycleRequest();
        request.setBucket(qBaseServe.bucket);
        request.setSign(600,null,null);
        DeleteBucketLifecycleResult result =  qBaseServe.cosXmlService.deleteBucketLifecycle(request);
        String headers = result.printHeaders();
        String body = result.printBody();
        String error = result.printError();
        String response = "Headers =" + headers + "|body =" + body + "|error =" + error;
        Log.d(TAG, response);
        if(qBaseServe.isSuccess(result.getHttpCode())){
            return true;
        }else {
            return false;
        }
    }

    public boolean testDeleteBucketLifecycleRequest2() throws Exception{
        hasCompleted = 0;
        DeleteBucketLifecycleRequest request2 = new DeleteBucketLifecycleRequest();
        request2.setBucket(qBaseServe.bucket);
        request2.setSign(600, null, null);
        qBaseServe.cosXmlService.deleteBucketLifecycleAsync(request2, new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders());
                hasCompleted = 1;
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlResult result) {
                Log.d(TAG, result.printHeaders() + "|" + result.printError());
                hasCompleted = 2;
            }
        });
        while(hasCompleted == 0){
            Thread.sleep(500);
        }
        if(hasCompleted == 1){
            return true;
        }else {
            return false;
        }
    }

    @Test
    public void test1() throws Exception{
        init();
        boolean result1 = testPutBucketLifecycleRequest();
        boolean result2 = testGetBucketLifecycleRequest();
        boolean result3 = testDeleteBucketLifecycleRequest();
        assertEquals(true,result1|result2|result3);
    }

    @Test
    public void test2() throws Exception{
        init();
        boolean result1 = testPutBucketLifecycleRequest2();
        boolean result2 = testGetBucketLifecycleRequest2();
        boolean result3 = testDeleteBucketLifecycleRequest2();
        assertEquals(true,result1|result2|result3);
    }



}
