package devjobs_web_app.backend.repositories;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.AggregationSpELExpression;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyRepo {

    @Autowired 
	private MongoTemplate mongoTemplate;

    // db.companies.aggregate([
    //     {
    //         $match: { company: "Blogr" }
    //     },
    //     {
    //         $lookup: {
    //             from: "data",
    //             localField: "company",
    //             foreignField: "company",
    //             as: "jobPostings"
    //         }
    //     },
    //     {
    //         $unwind: "$jobPostings"
    //     },
    //     {
    //         $group: {
    //             _id: "$_id",
    //             company: { $first: "$company" },
    //             address: { $first: "$address" },
    //             city: { $first: "$city" },
    //             coordinates: { $first: "$address.coord" },
    //             reviews: { $first: "$reviews" },
    //             jobPostings: {
    //                 $push: {
    //                     position: "$jobPostings.position",
    //                     postedAt: "$jobPostings.postedAt",
    //                     contract: "$jobPostings.contract",
    //                 }
    //             }
    //         }
    //     },
    //     {
    //         $project: {
    //             company: 1,
    //             address: {
    //                 $concat: [
    //                     "$address.building",
    //                     ", ",
    //                     "$address.street",
    //                     ", ",
    //                     "$address.zipcode",
    //                     ", ",
    //                     "$city"
    //                 ]
    //             },
    //             coordinates: 1,
    //             reviews: 1,
    //             jobPostings: 1
    //         }
    //     },
    //     {
    //         $limit: 1
    //     }
    // ]);
    

    public Document getCompanyDetails(String companyName) {
        // Match documents in the 'companies' collection by the company name
        MatchOperation matchOperation = Aggregation.match(Criteria.where("company").is(companyName));

        // Perform a lookup to join documents from the 'data' collection based on the 'company' field
        LookupOperation lookupOperation = Aggregation.lookup("data", "company", "company", "jobPostings");

        // Unwind the 'jobPostings' array to separate the joined documents
        UnwindOperation unwindOperation = Aggregation.unwind("jobPostings");

        // Group the documents by the company fields and collect the relevant details
        GroupOperation groupOperation = Aggregation.group("_id")
            .first("company").as("company")
            .first("address").as("address")
            .first("city").as("city")
            .first("address.coord").as("coordinates")
            .first("reviews").as("reviews")
            .push(new Document("position", "$jobPostings.position")
                .append("postedAt", "$jobPostings.postedAt")
                .append("contract", "$jobPostings.contract"))
            .as("jobPostings");

        // Project the final output
        ProjectionOperation projectionOperation = Aggregation.project()
            .and("company").as("company")
            .and("address.building").as("building")
            .and("address.street").as("street")
            .and("address.zipcode").as("zipcode")
            .and("city").as("city")
            .and("coordinates").as("coordinates")
            .and("reviews").as("reviews")
            .and("jobPostings").as("jobPostings")
            .and(
                AggregationSpELExpression.expressionOf(
                    "concat(address.building, ', ', address.street, ', ', address.zipcode, ', ', city)"
                )
            ).as("address");

        // Limit the result to one document
        LimitOperation limitOperation = Aggregation.limit(1);

        // Create the aggregation pipeline
        Aggregation aggregation = Aggregation.newAggregation(
            matchOperation, lookupOperation, unwindOperation, groupOperation, projectionOperation, limitOperation);

        // Execute the aggregation
        AggregationResults<Document> aggregationResults = mongoTemplate.aggregate(aggregation, "companies", Document.class);

        // Return the single result
        return aggregationResults.getUniqueMappedResult();
    }
}
