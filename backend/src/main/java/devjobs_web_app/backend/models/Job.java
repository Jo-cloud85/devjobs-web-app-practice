package devjobs_web_app.backend.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    int id;
    String company;
    String logo;
    String logoBackground;
    String position;
    String postedAt;
    String contract;
    String location;
    String website;
    String apply;
    String description;
    Requirements requirements;
    Role role;
    LocalDateTime postedAtRealTime;
}
