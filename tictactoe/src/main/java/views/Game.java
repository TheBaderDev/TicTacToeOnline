package views;

import org.apache.cayenne.Cayenne;
import org.apache.log4j.Logger;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.shared.Registration;

import applicationstuff.Broadcaster;
import authentication.AccessControl;
import authentication.AccessControlFactory;
import authentication.CurrentUser;
import database.Manager;

@Route("")
@PageTitle("Game Panel")
@Push
public class Game extends VerticalLayout implements BeforeEnterObserver, RouterLayout {
    private static final long serialVersionUID = 1L;
    protected static Logger logger = Logger.getLogger(Game.class);
    Registration broadcasterRegistration;
    private String groupID;
    private String player;
    BoardView boardView;

    public Game() {
        logger.info("");
        loadUI();
    }

    public void loadUI() {
        AccessControl accessControl = AccessControlFactory.getInstance().getAccessControl();

        if (accessControl.isUserSignedIn()) {
            groupID = "" + CurrentUser.get().getGroupId();
            player = CurrentUser.get().getNickname();

            // Labels
            Label label = new Label("Send the following Link to your friend:");

            Label label2 = new Label("localhost:8080/joingame/" + CurrentUser.get().getGroupId());
            add(label, label2, new Hr());

            // Song View
            boardView = new BoardView(CurrentUser.get());
            add(boardView);
        }
    }

    public void reloadUI() {
        logger.info("groupID: '" + groupID + "' player: '" + player + "'");
        boardView.reloadBoard();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        logger.info("groupID: '" + groupID + "' player: '" + player + "'");

        UI ui = attachEvent.getUI();
        broadcasterRegistration = Broadcaster.register(newMessage -> {
            logger.info("groupID: '" + groupID + "' newMessage: '" + newMessage + "'");

            if (newMessage.contentEquals(groupID)) {
                ui.access(() -> {
                    this.reloadUI();
                });
            }
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        broadcasterRegistration.remove();
        broadcasterRegistration = null;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        AccessControl accessControl = AccessControlFactory.getInstance().getAccessControl();

        logger.info("");
        if (!accessControl.isUserSignedIn()) {
            event.rerouteTo(StartNewGame.class);
        }
    }

//    @Override
//    public void beforeLeave(BeforeLeaveEvent event) {
//        authentication.AccessControl accessControl = AccessControlFactory.getInstance().getAccessControl();
//
//        if (accessControl.isUserSignedIn()) {
//            Manager m = new Manager();
//            if (m.getNumberPlayers(CurrentUser.get().getGroupId()) == 2) {
//                accessControl.signOut();
//            } else {
//                m.deleteGroup(Cayenne.intPKForObject(CurrentUser.get()));
//                accessControl.signOut();
//            }
//        }
//    }
}
