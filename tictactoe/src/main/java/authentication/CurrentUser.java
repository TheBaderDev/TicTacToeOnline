package authentication;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.query.ObjectSelect;

import com.mysql.cj.jdbc.DatabaseMetaData;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;

import database.Manager;
import objects.User;

/**
 * Class for retrieving and setting the name of the current user of the current session (without using JAAS). All
 * methods of this class require that a {@link VaadinRequest} is bound to the current thread.
 * 
 * 
 * @see VaadinService#getCurrentRequest()
 */
public final class CurrentUser {

    /**
     * The attribute key used to store the username in the session.
     */
    public static final String CURRENT_USER_SESSION_ATTRIBUTE_KEY = CurrentUser.class.getCanonicalName();
    
    private CurrentUser() {
    }

    /**
     * Returns the name of the current user stored in the current session, or an empty string if no user name is stored.
     * 
     * @throws IllegalStateException
     *             if the current session cannot be accessed.
     */
    public static User get() throws IllegalArgumentException {
        String currentUser = (String) getCurrentRequest().getWrappedSession().getAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY);

        if (currentUser == null || currentUser.equals("")) {
            throw new IllegalArgumentException();
        } else {
        	Manager m = new Manager();
            return m.getUserById(Integer.parseInt(currentUser));
        }
    }

	/**
     * Sets the name of the current user and stores it in the current session. Using a {@code null} username will remove the
     * username from the session.
     * 
     * @throws IllegalStateException
     *             if the current session cannot be accessed.
     */
    public static void set(User user) {
    	int ID = (int) Cayenne.longPKForObject(user);
    	getCurrentRequest().getWrappedSession().setAttribute(CURRENT_USER_SESSION_ATTRIBUTE_KEY, Integer.toString(ID));
    }

    private static VaadinRequest getCurrentRequest() {
        VaadinRequest request = VaadinService.getCurrentRequest();

        if (request == null) {
            throw new IllegalStateException("No request bound to current thread.");
        }
        return request;
    }
}
