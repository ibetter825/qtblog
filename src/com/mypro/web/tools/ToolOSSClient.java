package com.mypro.web.tools;

import java.io.File;
import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.PutObjectResult;

/**
 * OSS
 * @author ibett
 *
 */
public class ToolOSSClient {

	// accessKey请登录https://ak-console.aliyun.com/#/查看
	private static String endpoint = "http://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAIa7i0kMvOnLt2";
    private static String accessKeySecret = "3t1bbc18yG0vsSysgMTJtpVnHAdyou";
    private static String bucketName = "qtblog";
    
    /**
     * 上传文件到阿里云oss
     * @param key 文件的路径 + 名称
     * @param file 上传的文件
     * @return
     */
    public static boolean upload(String key, File file){
    	OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
        	PutObjectResult result = ossClient.putObject(bucketName, key, file);
            System.out.println("upload file to oss result etag:"+result.getETag());
            return true;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
        return false;
    }
    /**
     * 删除oss上面的文件
     * @param key
     */
    public static boolean delete(String key){
    	OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        try {
        	ossClient.deleteObject(bucketName, key);
            System.out.println("delete file from oss.");
            return true;
        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            ossClient.shutdown();
        }
        return false;
    }
}
