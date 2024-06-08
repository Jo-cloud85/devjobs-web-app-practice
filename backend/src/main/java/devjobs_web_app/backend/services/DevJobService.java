package devjobs_web_app.backend.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devjobs_web_app.backend.models.Job;
import devjobs_web_app.backend.repositories.DevJobMongoRepo;

// Link to MongoDB
// Getting jobs data from MongoDB

@Service
public class DevJobService {
    
    @Autowired
    private DevJobMongoRepo devJobRepo;

    public List<Job> searchJobs(String searchText, String location, boolean fullTime) {
        return devJobRepo.searchJobs(searchText, location, fullTime);
    }

    public Job getJobById(int id) {
        return devJobRepo.getJobById(id);
    }
}
