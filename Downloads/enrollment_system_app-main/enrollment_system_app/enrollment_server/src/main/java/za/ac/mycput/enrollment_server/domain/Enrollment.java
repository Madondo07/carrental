package za.ac.mycput.enrollment_server.domain;

/**
 *
 * @author Dumisane MADONDO (230949703)
 */
public class Enrollment {
    private int student_id;
    private String course_code;

    public Enrollment() {
    }

    public Enrollment(int student_id, String course_code) {
        this.student_id = student_id;
        this.course_code = course_code;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    public String getCourse_code() {
        return course_code;
    }

    public void setCourse_code(String course_code) {
        this.course_code = course_code;
    }
    
    
}
