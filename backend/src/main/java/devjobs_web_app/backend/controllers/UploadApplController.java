package devjobs_web_app.backend.controllers;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import devjobs_web_app.backend.models.Application;
import devjobs_web_app.backend.services.AppliedJobService;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;

@RestController
@RequestMapping("/api")
public class UploadApplController {
    
    @Autowired
    private AppliedJobService appliedJobSvc;

    @PostMapping(path="/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submitApplication(
        @RequestPart("name") String name,
        @RequestPart("email") String email,
        @RequestPart("mobileNumber") String mobileNumber,
        @RequestPart("position") String position,
        @RequestPart("startDate") String startDateStr,
        @RequestPart("feedback") String feedback,
        @RequestPart("resume") MultipartFile resumeFile,
        @RequestPart("otherDocs") MultipartFile[] otherFiles) throws IOException {

        System.out.println("resumeFile input stream >>> " + resumeFile.getInputStream().toString());
        System.out.println("resumeFile original filename >>> " + resumeFile.getOriginalFilename().toString());
        System.out.println("resumeFile size >>> " + resumeFile.getSize());

        LocalDate startDate = LocalDate.parse(startDateStr);

        String application_id = UUID.randomUUID().toString().substring(0, 8);

        // Saving application details to MySQL
        Application application = new Application(
            application_id, name, email, mobileNumber, position, startDate, feedback
        );

        // Only when we successfully save to MySQL then we save to S3
        Boolean isApplicationSaved = appliedJobSvc.saveDetailsToSql(application);

        if (isApplicationSaved) {
            // Saving resume file to S3
            String urlForResume = appliedJobSvc.saveFileToS3(resumeFile);
    
            // Saving other files to S3
            List<String> urlsForOtherFiles = new ArrayList<>();
            for (MultipartFile file : otherFiles) {
                System.out.println("otherFile input stream >>> " + resumeFile.getInputStream().toString());
                System.out.println("otherFile original filename >>> " + resumeFile.getOriginalFilename().toString());
                System.out.println("otherFile size >>> " + resumeFile.getSize());
                String fileUrl = appliedJobSvc.saveFileToS3(file);
                urlsForOtherFiles.add(fileUrl);
            }
    
            JsonObject jsonObj = Json.createObjectBuilder()
                .add("name", name)
                .add("email", email)
                .add("mobileNumber", mobileNumber)
                .add("position", position)
                .add("startDate", startDate.toString())
                .add("feedback", feedback)
                .add("resume", urlForResume)
                .add("otherDocs", urlsForOtherFiles.toString())
                .build();
            
            StringWriter stringWriter = new StringWriter();
            try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
                jsonWriter.writeObject(jsonObj);
            }
    
            return ResponseEntity.status(HttpStatus.OK).body(stringWriter.toString());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Cannot save your application.");
        }
    }

    // @GetMapping(path="/application/{file_id}")
    // public ResponseEntity<String> retrieveApplication(@PathVariable("file_id") String fileId) {
    //     byte[] buffer = appliedJobSvc.getFileFromS3(fileId);

    //     return ResponseEntity
    //         .status(HttpStatus.OK)
    //         .body(buffer);
    // }
}