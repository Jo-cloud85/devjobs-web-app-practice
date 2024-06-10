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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import devjobs_web_app.backend.models.Application;
import devjobs_web_app.backend.services.AppliedJobService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonWriter;

@RestController
@RequestMapping("/api")
public class JobApplController {
    
    @Autowired
    private AppliedJobService appliedJobSvc;

    @PostMapping(path="/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submitApplication(
        @RequestPart("id") String companyId,
        @RequestPart("company") String companyName,
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
            application_id, Integer.parseInt(companyId), companyName, name, email, mobileNumber, position, startDate, feedback
        );

        // Only when we successfully save to MySQL then we save to S3
        Boolean isApplicationSaved = appliedJobSvc.saveApplicationToSql(application);

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
                .add("application_id", application_id)
                .add("id", companyId)
                .add("company", companyName)
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


    @GetMapping(path="/applications")
    public ResponseEntity<String> getAllApplications() {
        List<Application> applList = appliedJobSvc.getAllApplicationsFrSql();

        JsonArrayBuilder jsonArrBuilder = Json.createArrayBuilder();

        for (Application appl : applList) {
            JsonObject jsonObj = Json.createObjectBuilder()
                .add("applicationId", appl.getApplication_id())
                .add("id", appl.getId())
                .add("company", appl.getCompany())
                .add("name", appl.getName())
                .add("email", appl.getEmail())
                .add("mobileNumber", appl.getMobileNumber())
                .add("position", appl.getPosition())
                .add("startDate", appl.getStartDate().toString())
                .add("feedback", appl.getFeedback())
                .build();
            jsonArrBuilder.add(jsonObj);
        }
        
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeArray(jsonArrBuilder.build());
        }

        if (applList.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("No jobs applications found");
        }

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(stringWriter.toString());
    }


    // Not used
    // @GetMapping(path="/application/{file_id}")
    // public ResponseEntity<String> getApplicationById(@PathVariable("file_id") String fileId) {
    //     Application appl = appliedJobSvc.getApplicationByIdFrSql(fileId);

    //     JsonObject jsonObj = Json.createObjectBuilder()
    //         .add("name", appl.getName())
    //         .add("email", appl.getEmail())
    //         .add("mobileNumber", appl.getMobileNumber())
    //         .add("position", appl.getPosition())
    //         .add("startDate", appl.getStartDate().toString())
    //         .add("feedback", appl.getFeedback())
    //         .build();
            
    //     StringWriter stringWriter = new StringWriter();
    //     try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
    //         jsonWriter.writeObject(jsonObj);
    //     }

    //     return ResponseEntity
    //         .status(HttpStatus.OK)
    //         .body(stringWriter.toString()); 
    // }
}