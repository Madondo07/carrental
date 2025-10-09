package za.ac.mycput.enrollment_server.domain;

/**
 *
 * @author Dumisane MADONDO (230949703)
 */
public class Course {
    private String course_code;
    private String description;

    public Course() {
    }

    public Course(String course_code, String description) {
        this.course_code = course_code;
        this.description = description;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    
}
