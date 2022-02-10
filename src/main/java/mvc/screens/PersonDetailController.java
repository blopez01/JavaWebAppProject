package mvc.screens;

import JavaFX.Alerts;
import gateway.InsertGateway;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import mvc.model.Person;
import mvc.model.Session;
import myexceptions.BadRequestException;
import myexceptions.UnauthorizedException;
import myexceptions.UnknownException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class PersonDetailController implements Initializable {
    private static final Logger LOGGER = LogManager.getLogger();

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField dateOfBirth;

    @FXML
    private TextField age;

    @FXML
    private TextField id;

    @FXML
    private ListView<Person> personListView;

    private Person person;
    private Session session;

    public PersonDetailController(Person person, Session session) {
        this.person = person;
        this.session = session;
    }

    @FXML
    void cancelSave(ActionEvent event) {
        LOGGER.info("Cancel clicked");
        MainController.getInstance().switchView(ScreenType.PERSONLIST, 0);
    }

    @FXML
    void save(ActionEvent event) {
        LOGGER.info("Save clicked");

        boolean newPerson = false;
        if(person.getId() == 0){
            newPerson = true;
        }

        if(firstName.getText() == "" || lastName.getText() == ""){
            LOGGER.debug("Please check information for firstname and lastname");
            Alerts.infoAlert("Check your input", "Please check the information for firstName and lastName");
        }
        if(firstName.getText().length() > 100 || lastName.getText().length() > 100){
            LOGGER.debug("Input cannot exceed 100 characters");
        }
        else {
            person.setFirstName(firstName.getText());
            person.setLastName(lastName.getText());
            person.setAge(dateOfBirth.getText());
            // insert new person
            if (newPerson) {
                Session session = this.session;

                //inserting new person
                try {
                    //receive 200
                    //session = InsertGateway.insert()//session, firstName.getText(), lastName.getText(), dateOfBirth.getText());
                    LOGGER.info(session);
                    //person.setId(session.getResponse());
                } catch (UnauthorizedException e) { //receive 401
                    Alerts.infoAlert("insert failed!", "Either your username or your password is incorrect.");
                    return;
                } catch (UnknownException e1) {
                    Alerts.infoAlert("insert failed!", " 404 Something bad happened: " + e1.getMessage());
                    return;
                }
                LOGGER.info("CREATING " + person.getFirstName() + " " + person.getLastName() + " with " ); //session.getResponse());
                //MainController.getInstance().setSession(session);
                //MainController.getInstance().switchView(ScreenType.PERSONLIST); //session.getSessionId());
                MainController.getInstance().switchView(ScreenType.PERSONLIST, person);
            }

            //updating existing person
            else {
                String oldName = firstName.getText();
                Session session = this.session;
                try {
                    //receive 200
                    //session = UpdateGateway.update(session, person.getId(), firstName.getText(), lastName.getText(), dateOfBirth.getText());
                } catch (UnauthorizedException e) { //receive 401
                    Alerts.infoAlert("updated failed!", "Something bad happened" + e.getMessage());
                    return;
                } catch (UnknownException e1) {
                    Alerts.infoAlert("updated failed!", " 404 Something bad happened: " + e1.getMessage());
                    return;
                } catch (BadRequestException e2) {
                    Alerts.infoAlert("update failed", "400 Something bad happend :" + e2.getMessage());
                }
                LOGGER.info("UPDATING " + person.getFirstName() + " " + person.getLastName());
                //MainController.getInstance().setSession(session);
                MainController.getInstance().switchView(ScreenType.PERSONLIST); //session.getSessionId());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // this is where we connect the model data to the GUI components like textfields
        firstName.setText(person.getFirstName());
        lastName.setText(person.getLastName());
//        if(person.getDateOfBirth() == null){
//            dateOfBirth.setText("");
//        }
//        else {
//            dateOfBirth.setText("" + person.getDateOfBirth());
//        }
        age.setText("" + person.getAge());
        id.setText("" + person.getId());
    }


}
