package views;

import org.apache.log4j.Logger;

import com.vaadin.flow.component.PushConfiguration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.communication.PushMode;

import authentication.AccessControlFactory;
import database.Manager;

@Route(value = "joingame")
@PageTitle("Joing a Game")
public class JoinExistingGame extends VerticalLayout implements BeforeEnterObserver, HasUrlParameter<Integer> {
    private static final long serialVersionUID = 1L;
    protected static Logger logger = Logger.getLogger(JoinExistingGame.class);
    private int url;

    public JoinExistingGame() {
        logger.info("");

        Label label = new Label("TicTacToe Online");

        TextField nickname = new TextField("", "Nickname");
        nickname.focus();
        NativeButton confirmButton = new NativeButton("Join", e -> {
            if (nickname.getValue().contentEquals("")) {
                UI.getCurrent().navigate(StartNewGame.class);
            } else {
                // New User to be Made
                Manager m = new Manager();
                authentication.AccessControl accessControl = AccessControlFactory.getInstance().getAccessControl();
                boolean b = accessControl.signIn(nickname.getValue(), this.url, 2);

                // Navigation
                logger.info("");

                PushConfiguration config = UI.getCurrent().getPushConfiguration();

                logger.info("config: '" + config + "'");
                UI.getCurrent().getPushConfiguration().setPushMode(PushMode.AUTOMATIC);
                UI.getCurrent().setPollInterval(-1);
                UI.getCurrent().navigate(Game.class);
            }
        });
        NativeButton cancelButton = new NativeButton("Cancel", e -> {
            UI.getCurrent().getPage().reload();
        });

        add(nickname, confirmButton, cancelButton);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        authentication.AccessControl accessControl = AccessControlFactory.getInstance().getAccessControl();

        logger.info("");
        if (accessControl.isUserSignedIn()) {
            try {
                event.rerouteTo(Game.class);
            } catch (IllegalArgumentException e) {
                accessControl.signOut();
            } catch (NullPointerException e) {
                accessControl.signOut();
            }
        }
    }

    @Override
    public void setParameter(BeforeEvent event, Integer parameter) {
        Manager m = new Manager();

        logger.info("");
        if (m.isGroupActive(parameter)) {
            setUrl(parameter);
        } else {
            Notification.show("Game Code Doesn't Exist");
            event.rerouteTo(StartNewGame.class);
        }
    }

    private void setUrl(Integer parameter) {
        this.url = parameter;
    }
}
