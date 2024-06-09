package devjobs_web_app.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import devjobs_web_app.backend.models.Application;
import devjobs_web_app.backend.repositories.AppliedJobSqlRepo;
import devjobs_web_app.backend.repositories.S3Repo;

@Service
public class AppliedJobService {

    @Autowired
    private S3Repo s3Repo;

    @Autowired
    private AppliedJobSqlRepo applJobSqlRepo;

    public String saveFileToS3(MultipartFile file) {
        return s3Repo.saveFileToS3(file);
    }

    public byte[] getFileFrS3(String fileId) {
        return s3Repo.getFileFromS3(fileId);
    }

    public Boolean saveApplicationToSql(Application appl) {
        return applJobSqlRepo.saveApplication(appl);
    }

    public List<Application> getAllApplicationsFrSql() {
        return applJobSqlRepo.getAllApplications().get();
    }

    public Application getApplicationByIdFrSql(String appId) {
        return applJobSqlRepo.getApplicationById(appId).get();
    }
}
