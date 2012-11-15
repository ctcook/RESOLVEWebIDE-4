package models;

import play.db.jpa.Model;

/**
 *
 * @author Chuck
 */
public class CompileJob extends Model {
    public String job;
    public UserComponent target;
    public String project;
}
