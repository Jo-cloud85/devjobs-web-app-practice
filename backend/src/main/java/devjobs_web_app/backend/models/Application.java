package devjobs_web_app.backend.models;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    private String application_id;
    private Integer id;
    private String company;
    private String name;
    private String email;
    private String mobileNumber;
    private String position;
    private LocalDate startDate;
    private String feedback;
}
