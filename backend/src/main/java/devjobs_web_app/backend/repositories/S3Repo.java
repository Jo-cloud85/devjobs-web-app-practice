package devjobs_web_app.backend.repositories;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import devjobs_web_app.backend.utils.Utils;

@Repository
public class S3Repo {

    @Autowired
    private AmazonS3 s3;

    public String saveFileToS3(MultipartFile file) {

        // User metadata
        Map<String, String> userdata = new HashMap<>();
        userdata.put("upload-timestamp", (new Date()).toString());
        userdata.put("name", "joyoung");
        userdata.put("filename", file.getOriginalFilename());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        metadata.setUserMetadata(userdata);

        String key = UUID.randomUUID().toString().substring(0, 8);

        PutObjectRequest putReq;

        try {
            // The 1st parameter is the bucket name
            putReq = new PutObjectRequest(Utils.S3_BUCKET_NAME, key, file.getInputStream(), metadata);
            // Make the object publicly available (not necessary)
            putReq = putReq.withCannedAcl(CannedAccessControlList.PublicRead);
            PutObjectResult result = s3.putObject(putReq);
            
            System.out.println("PutObjectResult: " + result.toString());
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return s3.getUrl(Utils.S3_BUCKET_NAME, key).toString();
    }

    public byte[] getFileFromS3(String fileId) {
        GetObjectRequest getRequest = new GetObjectRequest(Utils.S3_BUCKET_NAME, fileId);
        S3Object result = s3.getObject(getRequest);
        ObjectMetadata metadata = result.getObjectMetadata();
        Map<String, String> userData = metadata.getUserMetadata();

        System.out.println(userData);

        byte[] buffer = null;
    
        try (S3ObjectInputStream is = result.getObjectContent()) {
            buffer = is.readAllBytes();
            // Read content if needed here, e.g., buffer = is.readAllBytes();
            // However, do not close the InputStream or return the S3Object in this block
            // Optionally process the data here if needed
        } catch (AmazonS3Exception ex) {
            ex.printStackTrace(); 
        } catch (Exception ex) {
            throw new RuntimeException("Error retrieving file from S3", ex); 
        }
        
        // Return the S3Object outside of the try-with-resources block to keep it open
        return buffer;
    }
}
