package devjobs_web_app.backend.services;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import devjobs_web_app.backend.repositories.CompanyRepo;

@Service
public class CompanyService {
    
    @Autowired
    private CompanyRepo companyRepo;

    public Document getCompanyByName(String companyName) {
        return companyRepo.getCompanyDetails(companyName);
    }
}
