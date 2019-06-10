package authentication;

import org.apache.cayenne.query.ObjectSelect;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;

import database.Manager;
import objects.Group;
import objects.User;

/**
 * Default mock implementation of {@link AccessControl}. This implementation
 * accepts any string as a password, and considers the user "admin" as the only
 * administrator.
 */
public class BasicAccessControl implements AccessControl {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isUserSignedIn() {
		try {
			CurrentUser.get();
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	@Override
	public boolean isUserInRole(String role) {
		if ("admin".equals(role)) {
			// Only the "admin" user is in the "admin" role
			return getPrincipalName().equals("admin");
		}
		return true;
	}

	@Override
	public String getPrincipalName() {
		return CurrentUser.get().getNickname();
	}

	@Override
	public void signOut() {
		Manager m = new Manager();
		m.deleteUser(CurrentUser.get());
		VaadinSession.getCurrent().getSession().invalidate();
	}

	@Override
	public boolean signIn(String name, int ID, int playerNumber) {
		Manager m = new Manager();
		User tempUser = m.makeUser(name, ID);
		CurrentUser.set(tempUser);
		m.addUserToGroup(playerNumber, ID,tempUser);
		return true;
	}

	@Override
	public boolean signIn(String name) {
		// TODO Auto-generated method stub
		return false;
	}
}
