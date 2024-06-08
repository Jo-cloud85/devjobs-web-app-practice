package devjobs_web_app.backend.repositories;

import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import devjobs_web_app.backend.models.Application;

@Repository
public class AppliedJobSqlRepo {
    
    @Autowired
    JdbcTemplate jdbcTemplate;

    public static final String SQL_ADD_APPLICATION = """
            insert into jobs (application_id, name, email, mobileNumber, position, startDate, feedback) VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

    public static final String SQL_GET_ALL_APPLICATION = """
            select * from jobs
        """;

    public static final String SQL_GET_APPLICATION_BY_ID = """
            select * from jobs where application_id = ?
        """;


    public Boolean saveApplication(Application appl) {
        int isSaved = 0;
        isSaved = jdbcTemplate.update(
            SQL_ADD_APPLICATION, 
            appl.getApplication_id(),
            appl.getName(),
            appl.getEmail(),
            appl.getMobileNumber(),
            appl.getPosition(),
            appl.getStartDate(),
            appl.getFeedback()
        );
        return isSaved > 0 ? true : false;
    }   

    // public Optional<List<Application>> getAllApplications() {
        
    // }


    public Optional<Application> getApplicationById(String appl_id) {
        return jdbcTemplate.query(SQL_GET_APPLICATION_BY_ID, (ResultSet rs) -> {
            if(!rs.next()) return Optional.empty();

            Application appl = new Application();
            appl.setApplication_id(appl_id);
            appl.setName(rs.getString("name"));
            appl.setEmail(rs.getString("email"));
            appl.setMobileNumber(rs.getString("mobileNumber"));
            LocalDate date = (LocalDate) rs.getObject("startDate");
            appl.setStartDate(date);
            appl.setPosition(rs.getString("position"));
            appl.setFeedback(rs.getString("feedback"));

            return Optional.of(appl);
        }, appl_id);
    }
}
