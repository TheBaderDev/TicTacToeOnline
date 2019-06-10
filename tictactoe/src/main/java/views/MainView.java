package views;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import applicationstuff.Broadcaster;
import authentication.AccessControlFactory;
import authentication.CurrentUser;
import database.Manager;
import objects.Group;
import objects.User;

@Route("")
@PageTitle("MainView")
public class MainView extends VerticalLayout implements BeforeEnterObserver {
	private static final long serialVersionUID = 1L;
    protected static Logger logger = Logger.getLogger(MainView.class);

	public MainView() {
		Label label = new Label("TicTacToe Online");

		TextField nickname = new TextField("", "Nickname");
		Button create = new Button("Create a game", e -> {
			if (nickname.getValue().contentEquals("")) {
				Notification.show("Nickname cannot be blank");
			} else {
				Manager m = new Manager();				
				authentication.AccessControl accessControl = AccessControlFactory.getInstance().getAccessControl();
				accessControl.signIn(nickname.getValue(), m.makeGroup(), 1);
				
				UI.getCurrent().navigate("join/" + Integer.toString(CurrentUser.get().getGroupId()));
			}
		});

		HorizontalLayout sendBar = new HorizontalLayout(nickname, create);
		add(label, sendBar);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		authentication.AccessControl accessControl = AccessControlFactory.getInstance().getAccessControl();
		if (accessControl.isUserSignedIn()) {
			accessControl.signOut();
		}
	}

//	@Override
//	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
//		event.rerouteTo("");
//		return HttpServletResponse.SC_NOT_FOUND;
//	}
}
