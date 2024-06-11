package devjobs_web_app.backend.repositories;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import devjobs_web_app.backend.models.Job;
import devjobs_web_app.backend.models.Requirements;
import devjobs_web_app.backend.models.Role;

@Repository
public class DevJobMongoRepo {
    
    @Autowired
    private MongoTemplate mongoTemplate;

    /* 
        db.data.find({
            $and: [
                {
                    $or: [
                        { "position": { $regex: "developer", $options: "i" } },
                        { "company": { $regex: "developer", $options: "i" } }
                    ]
                },
                { "location": { $regex: "London", $options: "i" } },
                { "contract": "Full Time" }
            ]
        });
    */
    public List<Job> searchJobs(String searchText, String location, boolean fullTime) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();
        
        if (searchText != null && !searchText.isEmpty()) {
            Criteria searchCriteria = new Criteria().orOperator(
                Criteria.where("position").regex(searchText, "i"),
                Criteria.where("company").regex(searchText, "i")
            );
            criteriaList.add(searchCriteria);
        }
        
        if (location != null && !location.isEmpty()) {
            criteriaList.add(Criteria.where("location").regex(location, "i"));
        }
        
        if (fullTime) {
            criteriaList.add(Criteria.where("contract").is("Full Time"));
        }
        
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }
        
        List<Document> docResults = mongoTemplate.find(query, Document.class, "data");
        return docListToJobList(docResults);
    }

    /*
        db.data.find(
            { "id": 1 }
        );
    */
    public Job getJobById(int id) {
        Query query = new Query(Criteria.where("id").is(id));
        Document doc = mongoTemplate.findOne(query, Document.class, "data");
        return docToJob(doc);
    }


    private List<Job> docListToJobList(List<Document> docList) {
        List<Job> jobList = new LinkedList<>();
        for (Document doc : docList) {
            Job job = docToJob(doc);
            jobList.add(job);
        }
        return jobList;
    }


    private Job docToJob (Document doc) {
        Job job = new Job();
        job.setId(doc.getInteger("id"));
        job.setCompany(doc.getString("company"));
        job.setLogo(doc.getString("logo"));
        job.setLogoBackground(doc.getString("logoBackground"));
        job.setPosition(doc.getString("position"));
        String postedAt = doc.getString("postedAt");
        job.setPostedAt(postedAt);
        job.setContract(doc.getString("contract"));
        job.setLocation(doc.getString("location"));
        job.setWebsite(doc.getString("website"));
        job.setApply(doc.getString("apply"));
        job.setDescription(doc.getString("description"));
        Document requirementsDoc = doc.get("requirements", Document.class);
        if (requirementsDoc != null) {
            String requirementsContent = requirementsDoc.getString("content");
            List<String> requirementsItems = requirementsDoc.getList("items", String.class);

            Requirements req = new Requirements(requirementsContent, requirementsItems);
            job.setRequirements(req);
        }
        Document roleDoc = doc.get("role", Document.class);
        if (roleDoc != null) {
            String roleContent = roleDoc.getString("content");
            List<String> roleItems = roleDoc.getList("items", String.class);
            Role role = new Role(roleContent, roleItems);
            job.setRole(role);
        }

        // This is for sorting by most recent later
        LocalDateTime postedAtTime = convertPostedAtToLocalDateTime(postedAt);
        job.setPostedAtRealTime(postedAtTime);

        return job;
    } 
    

    // Helper method
    private LocalDateTime convertPostedAtToLocalDateTime(String postedAt) {
        String[] timeParts = postedAt.split(" ")[0].split("");
        Integer amount = 0;
        if (timeParts.length > 2) {
            if (Character.isDigit(timeParts[1].charAt(0))){
                amount = Integer.parseInt(timeParts[0] + timeParts[1]);
            } else {
                amount = Integer.parseInt(timeParts[0]);
            }
        } else {
            amount = Integer.parseInt(timeParts[0]);
        }
        String unit = timeParts[timeParts.length-1];
        
        // Determine the appropriate ChronoUnit based on the unit
        ChronoUnit chronoUnit;
        switch (unit) {
            case "h":
                chronoUnit = ChronoUnit.HOURS;
                break;
            case "d":
                chronoUnit = ChronoUnit.DAYS;
                break;
            case "w":
                chronoUnit = ChronoUnit.WEEKS;
                break;
            case "o": // this will represent "mo"
                chronoUnit = ChronoUnit.MONTHS;
                break;
            default:
                throw new IllegalArgumentException("Unsupported unit: " + unit);
        }

        // Subtract the specified amount of time from the current LocalDateTime
        return LocalDateTime.now().minus(amount, chronoUnit);
    }


    /////////////////////////////////////// EXTRA ///////////////////////////////////////////////////
    /*
        db.data.find({
            $or: [
                { "position": { $regex: "developer", $options: "i" } },
                { "company": { $regex: "developer", $options: "i" } }
            ]
        });
     */
    public List<Job> getJobsByPositionOrCompany(String searchText) {
        Query query = new Query();
        Criteria criteria = new Criteria().orOperator(
            Criteria.where("position").regex(searchText, "i"),
            Criteria.where("company").regex(searchText, "i")
        );
        query.addCriteria(criteria);
        // I can't use Job.class coz I need to process the postedAt field
        List<Document> docResult= mongoTemplate.find(query, Document.class);
        return docListToJobList(docResult);
    }

    /*
        db.data.aggregate([
            {
                $match: { "location": { $regex: "Japan", $options: "i" } }
            }
        ]);
    */
    public List<Job> getJobsByLocation(String searchLoc) {
        MatchOperation matchOperation = Aggregation.match(
            Criteria.where("location").regex(searchLoc, "i"));
        Aggregation pipeline = Aggregation.newAggregation(matchOperation);
        AggregationResults<Document> aggregationResults = mongoTemplate.aggregate(pipeline, "data", Document.class);
        List<Document> docResult = aggregationResults.getMappedResults();
        return docListToJobList(docResult);
    }

    /*
        db.data.find(
            { "contract": "Full Time" }
        );
    */
    public List<Job> getFullTimeJobs() {
        Query query = new Query(Criteria.where("contract").is("Full Time"));
        List<Document> docList = mongoTemplate.find(query, Document.class, "data");
        return docListToJobList(docList);
    }

    /////////////////////////////////////// EXTRA ///////////////////////////////////////////////////
}
