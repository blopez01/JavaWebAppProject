package mvc.model;

import org.json.JSONObject;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Entity
@Table(name="people")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "dob", nullable = false)
    private int age;

    public Person(){
    }

    public Person(long id, String firstName, String lastName, int age) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public Person(long id, String firstName, String lastName, String dateOfBirth) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = CalculateAge(dateOfBirth);
    }
    public Person(String firstName, String lastName, String dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = CalculateAge(dateOfBirth);
    }

    public static int CalculateAge(String dateOfBirth) {
        if (dateOfBirth != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formatter = formatter.withLocale( Locale.US );
            LocalDate dob = LocalDate.parse(dateOfBirth, formatter);
            if (dob.isAfter(LocalDate.now())) {
                return -1;
            }
            return Period.between(dob, LocalDate.now()).getYears();
        }
        else {
            return 0;
        }
    }

    public static boolean checkIfDateIsValid(DateTimeFormatter formatter, String date) {
        try {
            LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    //convert from JSON to Object
    public static Person fromJSONObject(JSONObject json) {
        try {
            Person person = new Person(json.getLong("id"), json.getString("firstName"), json.getString("lastName"),
                    json.getInt("age"));
            return person;
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to parse person from provided json: " + json.toString());
        }
    }

    //convert from JSON to Object
    public static Person fromJSONObjectSpring(JSONObject json) {
        try {
            Person person = new Person(json.getString("firstName"), json.getString("lastName"),
                    json.getString("dateOfBirth"));
            return person;
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to parse person from provided json: " + json.toString());
        }
    }

    @Override
    public String toString() {
        return getFirstName();
    }

    // accessors


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(String dateOfBirth) {
        this.age = CalculateAge(dateOfBirth);
    }

}
