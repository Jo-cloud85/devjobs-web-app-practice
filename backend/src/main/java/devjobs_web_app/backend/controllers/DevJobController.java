package devjobs_web_app.backend.controllers;

import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import devjobs_web_app.backend.models.Job;
import devjobs_web_app.backend.services.DevJobService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonWriter;

@Controller
@RequestMapping(path="/api", produces = MediaType.APPLICATION_JSON_VALUE)
// @CrossOrigin("*")
public class DevJobController {

    private Logger logger = Logger.getLogger(DevJobController.class.getName());

    @Autowired
    private DevJobService devJobSvc;
    
    @GetMapping("/jobs")
    @ResponseBody
    public ResponseEntity<String> searchJobs(
        @RequestParam(required = false) String q, 
        @RequestParam(required = false) String loc, 
        @RequestParam(defaultValue = "false") boolean fullTime,
        @RequestParam(defaultValue = "false") boolean sortByRecent) {
        
        // System.out.println(sortByRecent);

        logger.log(
            Level.INFO, 
            "SEARCH: q=%s, loc=%s, fullTime=%s, sortByRecent=%s".formatted(q, loc, fullTime, sortByRecent)
        );

        List<Job> jobList = devJobSvc.searchJobs(q, loc, fullTime);
        //System.out.println(jobList);
        if (jobList.size() == 0) {
            ResponseEntity.status(404).body("No jobs found");
        }
        // Sort the list by the most recent "postedAt" date if required
        if (sortByRecent == true) {
            jobList.sort(Comparator.comparing(Job::getPostedAtRealTime).reversed());
        }
        // for (Job job: jobList) {
        //     System.out.println(job.getPostedAt());
        //     System.out.println(job.getPostedAtRealTime());
        // }
        //System.out.println(jobList);
        return ResponseEntity.status(200).body(convertListToJsonStr(jobList));
    }

    @GetMapping("/job/{id}")
    @ResponseBody
    public ResponseEntity<String> searchJobById (
        @PathVariable("id") String id) {

        Job job = devJobSvc.getJobById(Integer.parseInt(id));
        return ResponseEntity.status(200).body(convertJobToStr(job));
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////
    private String convertListToJsonStr(List<Job> jobList) {
        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
        for (Job job : jobList) {
            JsonObject jsonObj = jobToJsonObject(job);
            jsonArrayBuilder.add(jsonObj);
        }
        // Convert the JSON array to a string
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeArray(jsonArrayBuilder.build());
        }
        return stringWriter.toString();
    }


    private String convertJobToStr(Job job) { 
        JsonObject jsonObj = jobToJsonObject(job);
        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeObject(jsonObj);
        }
        return stringWriter.toString();
    }


    private JsonObject jobToJsonObject(Job job) {
        JsonObjectBuilder jsonObj = Json.createObjectBuilder()
                .add("id", job.getId())
                .add("company", job.getCompany())
                .add("logo", job.getLogo())
                .add("logoBackground", job.getLogoBackground())
                .add("position", job.getPosition())
                .add("postedAt", job.getPostedAt().toString())
                .add("contract", job.getContract())
                .add("location", job.getLocation())
                .add("website", job.getWebsite())
                .add("apply", job.getApply())
                .add("description", job.getDescription())
                .add("postedAtRealTime", job.getPostedAtRealTime().toString());
        // Building the requirements object
        JsonObjectBuilder requirementsObj = Json.createObjectBuilder()
            .add("content", job.getRequirements().getContent());
        JsonArrayBuilder requirementsItemsArray = Json.createArrayBuilder();

        for (String item : job.getRequirements().getItems()) {
            requirementsItemsArray.add(item);
        }
        requirementsObj.add("items", requirementsItemsArray);

        // Building the role object
        JsonObjectBuilder roleObj = Json.createObjectBuilder()
            .add("content", job.getRole().getContent());
        JsonArrayBuilder roleItemsArray = Json.createArrayBuilder();

        for (String item : job.getRole().getItems()) {
            roleItemsArray.add(item);
        }

        roleObj.add("items", roleItemsArray);

        jsonObj.add("requirements", requirementsObj);
        jsonObj.add("role", roleObj);

        return jsonObj.build();
    }
}
