package views;

import org.apache.log4j.Logger;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Hr;
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

import authentication.AccessControlFactory;
import database.Manager;

@Route(value = "joingame")
@PageTitle("Joing a Game")
public class JoinExistingGame extends VerticalLayout implements BeforeEnterObserver, HasUrlParameter<Integer> {
    private static final long serialVersionUID = 1L;
    protected static Logger logger = Logger.getLogger(JoinExistingGame.class);
    private int url;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        authentication.AccessControl accessControl = AccessControlFactory.getInstance().getAccessControl();

        if (!accessControl.isUserSignedIn()) {
            // Popup Dialog
            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);
            TextField nickname = new TextField("", "Nickname");

            NativeButton confirmButton = new NativeButton("Join", e -> {
                if (nickname.getValue().contentEquals("")) {
                    dialog.close();
                    event.rerouteTo(StartNewGame.class);
                    UI.getCurrent().getPage().reload();

                } else {
                    // New User to be Made
                    Manager m = new Manager();
                    boolean b = accessControl.signIn(nickname.getValue(), this.url, 2);

                    // Navigation
                    dialog.close();
                    event.rerouteTo(Game.class);
                    //                    UI.getCurrent().navigate(Game.class);
                }
            });
            NativeButton cancelButton = new NativeButton("Cancel", e -> {
                dialog.close();
                event.rerouteTo(StartNewGame.class);
                //                UI.getCurrent().getPage().reload();

            });

            dialog.add(nickname, new Hr(), confirmButton, cancelButton);
            dialog.open();
        } else {
            event.rerouteTo(Game.class);
        }
    }

    @Override
    public void setParameter(BeforeEvent event, Integer parameter) {

        Manager m = new Manager();
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
