package views;

import org.apache.cayenne.Cayenne;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.shared.Registration;

import applicationstuff.Broadcaster;
import authentication.AccessControlFactory;
import authentication.CurrentUser;
import database.Manager;
import objects.Group;

@Route("playing")
@PageTitle("Game Panel")
@Push
public class Game extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {
	private static final long serialVersionUID = 1L;
	Registration broadcasterRegistration;
	Div boardView;

	public Game() {
		loadUI();
	}

	public void loadUI() {
		// Labels
		Label label = new Label("Send the following Link to your friend:");
		Label label2 = new Label("localhost:8080/join/" + CurrentUser.get().getGroupId());
		add(label, label2, new Hr());

		// Song View
		boardView = new BoardView(CurrentUser.get());
		add(boardView);
	}

	public void reloadUI() {
		if (boardView != null) {
			((BoardView) boardView).reloadBoard();
		}
	}

	private String groupCode() {
		return Integer.toString(CurrentUser.get().getGroupId());
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		String groupCode = groupCode();

		UI ui = attachEvent.getUI();
		broadcasterRegistration = Broadcaster.register(newMessage -> {
			ui.access(() -> {
				if (newMessage.contentEquals(groupCode)) {
					reloadUI();
				}
			});
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		broadcasterRegistration.remove();
		broadcasterRegistration = null;
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		authentication.AccessControl accessControl = AccessControlFactory.getInstance().getAccessControl();
		if (!accessControl.isUserSignedIn()) {
			event.rerouteTo(MainView.class);
		}
	}

	@Override
	public void beforeLeave(BeforeLeaveEvent event) {
		authentication.AccessControl accessControl = AccessControlFactory.getInstance().getAccessControl();
		if (accessControl.isUserSignedIn()) {
			Manager m = new Manager();
			if (m.getNumberPlayers(CurrentUser.get().getGroupId()) == 2) {
				accessControl.signOut();
			} else {
				m.deleteGroup(Cayenne.intPKForObject(CurrentUser.get()));
				accessControl.signOut();
			}
		}
	}
}
