package devjobs_web_app.backend.controllers;


import java.io.StringWriter;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import devjobs_web_app.backend.services.CompanyService;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonWriter;

@RestController
@RequestMapping("/api")
public class CompanyController {
    
    @Autowired
    private CompanyService companySvc;

    @GetMapping("/company/{companyName}")
    public ResponseEntity<String> getCompanyByName(@PathVariable String companyName) {
        Document doc = companySvc.getCompanyByName(companyName);

        // Building JSON object from the Document
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
            .add("company", doc.getString("company"))
            .add("address", doc.getString("address"))
            .add("coordinates", Json.createArrayBuilder()
                .add(doc.getList("coordinates", Double.class).get(0))
                .add(doc.getList("coordinates", Double.class).get(1))
                .build());

        // Adding reviews to JSON object
        JsonArrayBuilder reviewsArrayBuilder = Json.createArrayBuilder();
        for (Document review : doc.getList("reviews", Document.class)) {
            JsonObjectBuilder reviewObjectBuilder = Json.createObjectBuilder()
                .add("date", review.getDate("date").toInstant().toString())
                .add("comments", Json.createArrayBuilder(review.getList("comments", String.class)))
                .add("score", review.getDouble("score"));
            reviewsArrayBuilder.add(reviewObjectBuilder);
        }
        jsonObjectBuilder.add("reviews", reviewsArrayBuilder);

        // Adding jobPostings to JSON object
        JsonArrayBuilder jobPostingsArrayBuilder = Json.createArrayBuilder();
        for (Document job : doc.getList("jobPostings", Document.class)) {
            JsonObjectBuilder jobObjectBuilder = Json.createObjectBuilder()
                .add("position", job.getString("position"))
                .add("postedAt", job.getString("postedAt"))
                .add("contract", job.getString("contract"));
            jobPostingsArrayBuilder.add(jobObjectBuilder);
        }
        jsonObjectBuilder.add("jobPostings", jobPostingsArrayBuilder);

        JsonObject jsonObject = jsonObjectBuilder.build();

        StringWriter stringWriter = new StringWriter();
        try (JsonWriter jsonWriter = Json.createWriter(stringWriter)) {
            jsonWriter.writeObject(jsonObject);
        }

        // System.out.println(stringWriter.toString());

        return ResponseEntity.status(HttpStatus.OK).body(stringWriter.toString());
    }
}
