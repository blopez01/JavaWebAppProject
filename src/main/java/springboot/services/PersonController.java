package springboot.services;

import mvc.model.Audit;
import mvc.model.Person;
import mvc.model.Session;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pw_hash.HashUtils;
import springboot.repositories.AuditRepository;
import springboot.repositories.PersonRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class PersonController {
    private static final Logger logger = LogManager.getLogger();

    private PersonRepository personRepository;
    private AuditRepository auditRepository;

    public PersonController(PersonRepository personRepository, AuditRepository auditRepository) {
        this.personRepository = personRepository;
        this.auditRepository = auditRepository;
    }

    @GetMapping("/people")
    public ResponseEntity<List<Person>> fetchPeople(@RequestHeader Map<String, String> headers) {
        // get the list of person models from the orm's repository
        List<Person> people = personRepository.findAll();

        // check headers
        String token = UserController.getTOKEN();
        if(headers.containsKey("authorization")) {
            if(!headers.get("authorization").equals(token)){
                return new ResponseEntity<>(HttpStatus.valueOf(401));
            }
            // return people list
            else{
                return new ResponseEntity<>(people, HttpStatus.valueOf(200));
            }
        }
        return new ResponseEntity<>(HttpStatus.valueOf(401));
    }

    @GetMapping("/people/{personId}")
    public ResponseEntity<Person> fetchPersonById(@PathVariable long personId, @RequestHeader Map<String, String> headers) {
        // find person using path variable
        Optional<Person> person = personRepository.findById(personId);
        // check headers
        String token = UserController.getTOKEN();
        if(headers.containsKey("authorization")) {
            if(!headers.get("authorization").equals(token)) {
                return new ResponseEntity<>(HttpStatus.valueOf(401));
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.valueOf(401));
        }

        if(person.isPresent())
            return new ResponseEntity<>(person.get(), HttpStatus.valueOf(200));
        else
            return new ResponseEntity<>(HttpStatus.valueOf(404));
    }

    @GetMapping("/people/{personId}/audittrail")
    public ResponseEntity<List<Audit>> fetchPersonByIdAuditTrail(@PathVariable long personId, @RequestHeader Map<String, String> headers) {
        // find person using path variable
        Optional<Person> person = personRepository.findById(personId);
        List<Audit> audittrail = auditRepository.findAuditsById((int)personId);
        // check headers
        String token = UserController.getTOKEN();
        if(headers.containsKey("authorization")) {
            if(!headers.get("authorization").equals(token)) {
                return new ResponseEntity<>(HttpStatus.valueOf(401));
            }
        }
        else {
            return new ResponseEntity<>(HttpStatus.valueOf(401));
        }

        if(person.isPresent())
            return new ResponseEntity<>(audittrail, HttpStatus.valueOf(200));
        else
            return new ResponseEntity<>(HttpStatus.valueOf(404));
    }

    @DeleteMapping("/people/{personId}")
    public ResponseEntity<String> deletePersonById(@PathVariable long personId, @RequestHeader Map<String, String> headers){
        // find person using path variable
        Optional<Person> person = personRepository.findById(personId);

        // check headers
        String token = UserController.getTOKEN();
        if(headers.containsKey("authorization")) {
            if(!headers.get("authorization").equals(token)) {
                return new ResponseEntity<>(HttpStatus.valueOf(401));
            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.valueOf(401));
        }

        if(person.isPresent())
        {
            personRepository.deleteById(personId);
            return new ResponseEntity<>(HttpStatus.valueOf(200));
        }
        else
            return new ResponseEntity<>(HttpStatus.valueOf(404));
    }
    @PostMapping("/people")
    public ResponseEntity<String> insertPerson(@RequestHeader Map<String, String> headers, @RequestBody String body) {
        // check headers
        String token = UserController.getTOKEN();
        if(headers.containsKey("authorization")) {
            if(!headers.get("authorization").equals(token)) {
                return new ResponseEntity<>(HttpStatus.valueOf(401));
            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.valueOf(401));
        }

        // convert body to json object
        JSONObject jsonObj = new JSONObject(body);
        JSONObject errorList = new JSONObject();
        boolean validFN = true;
        boolean validLN = true;
        boolean validAge = true;

        // validate fields
        if(jsonObj.has("firstName") && jsonObj.has("lastName") && jsonObj.has("dateOfBirth")) {
            //validate dateOfBirth
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            if(!Person.checkIfDateIsValid(formatter, jsonObj.getString("dateOfBirth"))){
                errorList.put("incorrect date format", jsonObj.getString("dateOfBirth"));
                validAge = false;
            }
            else {
                int confirmAge = Person.CalculateAge(jsonObj.getString("dateOfBirth"));
                if (confirmAge == -1) {
                    validAge = false;
                    errorList.put("invalid age", jsonObj.getString("dateOfBirth"));
                }
            }
            //validate firstName
            String firstName = jsonObj.getString("firstName");
            if ((firstName.length() > 100) || (firstName.length() < 1)) {
                validFN = false;
                errorList.put("invalid firstName", firstName);
            }
            //validate lastName
            String lastName = jsonObj.getString("lastName");
            if ((lastName.length() > 100) || (lastName.length() < 1)) {
                validLN = false;
                errorList.put("invalid lastName", lastName);
            }

            // success
            if (validAge && validFN && validLN) {
                Person person = new Person();
                person = Person.fromJSONObjectSpring(jsonObj);
                personRepository.save(person);
                return new ResponseEntity<>(HttpStatus.valueOf(200));
            }
        }
        else {
            errorList.put("Missing or incorrect insert information", "");
        }
        // incorrect fields
        return new ResponseEntity<>(errorList.toString(), HttpStatus.valueOf(400));
    }
    @PutMapping("/people/{personId}")
    public ResponseEntity<String> updatePerson(@PathVariable long personId, @RequestHeader Map<String, String> headers, @RequestBody String body) {
        // check headers
        String token = UserController.getTOKEN();
        if(headers.containsKey("authorization")) {
            if(!headers.get("authorization").equals(token)) {
                return new ResponseEntity<>(HttpStatus.valueOf(401));
            }
        }
        else{
            return new ResponseEntity<>(HttpStatus.valueOf(401));
        }
        // check repository list
        Optional<Person> person = personRepository.findById(personId);

        if(person.isEmpty())
            return new ResponseEntity<>(HttpStatus.valueOf(404));

        // convert body to json object
        JSONObject jsonObj = new JSONObject(body);
        JSONObject errorList = new JSONObject();
        String firstName="", lastName="", dob ="";
        boolean validFN = true;
        boolean validLN = true;
        boolean validAge = true;

        // validate fields
        if(jsonObj.has("dateOfBirth") && jsonObj.has("firstName") && jsonObj.has("lastName")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            dob = jsonObj.getString("dateOfBirth");
            if (!Person.checkIfDateIsValid(formatter, dob)) {
                errorList.put("incorrect date format", dob);
                validAge = false;
            } else {
                int confirmAge = Person.CalculateAge(jsonObj.getString("dateOfBirth"));
                if (confirmAge == -1) {
                    validAge = false;
                    errorList.put("invalid age", jsonObj.getString("dateOfBirth"));
                }
            }
            firstName = jsonObj.getString("firstName");
            if ((firstName.length() > 100) || (firstName.length() < 1)) {
                validFN = false;
                errorList.put("invalid firstName", firstName);
            }
            lastName = jsonObj.getString("lastName");
            if ((lastName.length() > 100) || (lastName.length() < 1)) {
                validLN = false;
                errorList.put("invalid lastName", lastName);
            }

            // success
            if (validAge && validFN && validLN) {
                Person updatedPerson = personRepository.findById(personId).orElse(new Person());
                if (jsonObj.has("firstName")) {
                    updatedPerson.setFirstName(firstName);
                }

                if (jsonObj.has("lastName")) {
                    updatedPerson.setLastName(lastName);
                }

                if (jsonObj.has("dateOfBirth")) {
                    updatedPerson.setAge(dob);
                }
                personRepository.save(updatedPerson);
                return new ResponseEntity<>(HttpStatus.valueOf(200));
            }
        }
        else {
            errorList.put("Bad field name", "");
        }
        // incorrect fields
        return new ResponseEntity<>(errorList.toString(), HttpStatus.valueOf(400));
    }
}
