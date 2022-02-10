package mvc.model;

import org.json.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "audit")
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "change_msg")
    private String change_msg;

    @Column(name = "changed_by")
    private int changed_by;

    @Column(name = "person_id")
    private int person_id;

    @Column(name = "when_occurred")
    private String when_occurred;

    public Audit() {
    }

    public Audit(long id, String change_msg, int changed_by, int person_id, String when_occurred) {
        this.id = id;
        this.change_msg = change_msg;
        this.changed_by = changed_by;
        this.person_id = person_id;
        this.when_occurred = when_occurred;
    }

    public static Audit convertAuditJSONObject(JSONObject json) {
        try {
            Audit audit = new Audit(json.getLong("id"),json.getString("change_msg"), json.getInt("changed_by"), json.getInt("person_id"),
                    json.getString("when_occurred"));
            return audit;
        } catch(Exception e) {
            throw new IllegalArgumentException("Unable to parse person from provided json: " + json.toString());
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChange_msg() {
        return change_msg;
    }

    public void setChange_msg(String change_msg) {
        this.change_msg = change_msg;
    }

    public Integer getChanged_by() {
        return changed_by;
    }

    public void setChanged_by(Integer changed_by) {
        this.changed_by = changed_by;
    }

    public Integer getPerson_id() { return person_id; }

    public void setPerson_id(Integer person_id) { this.person_id = person_id; }

    public String getWhen_occurred() {
        return when_occurred;
    }

    public void setWhen_occurred(String when_occurred) {
        this.when_occurred = when_occurred;
    }
}
