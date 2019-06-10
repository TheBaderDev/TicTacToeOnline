package views;

import org.apache.log4j.Logger;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import applicationstuff.Broadcaster;
import authentication.CurrentUser;
import database.Manager;
import objects.User;

public class BoardView extends Div {
    protected static Logger logger = Logger.getLogger(BoardView.class);
    private int playerNumber;
    private int groupID;
    private String player;
    private int[] list;
    private int lastMoved;
    private int winner;

    public BoardView(User user) {
        this.groupID = user.getGroupId();
        this.player = CurrentUser.get().getNickname();

        Manager m = new Manager();
        this.playerNumber = Manager.getPlayerNumberInGroup(user);
        this.winner = 0;

        reloadBoard();
    }

    public void reloadBoard() {
        logger.info("groupCode: '" + groupID + "' player: '" + player + "'");

        removeAll();
        Manager m = new Manager();
        this.list = m.getBoardState(groupID);
        this.lastMoved = m.getLastMoved(groupID);

        logger.info("groupCode: '" + groupID + "' lastMoved: '" + lastMoved + "'");
        if (!checkEnd()) {
            int n;
            if (lastMoved == 1) {
                n = 2;
            } else {
                n = 1;
            }
            add(new Label(m.getPlayerName(groupID, n) + "'s turn!"));
        }

        HorizontalLayout top = new HorizontalLayout();
        for (int i = 0; i < 3; i++) {
            top.add(makeButton(i));
        }
        HorizontalLayout mid = new HorizontalLayout();
        for (int i = 3; i < 6; i++) {
            mid.add(makeButton(i));
        }
        HorizontalLayout bot = new HorizontalLayout();
        for (int i = 6; i < 9; i++) {
            bot.add(makeButton(i));
        }
        VerticalLayout all = new VerticalLayout();
        all.add(top, mid, bot);

        if (checkEnd()) {
            addReset();
        }
        add(all);
    }

    private boolean checkEnd() {
        int n = lastMoved;
        if ((list[0] == n && list[1] == n && list[2] == n)
                        || (list[3] == n && list[4] == n && list[5] == n)
                        || (list[6] == n && list[7] == n && list[8] == n)
                        || (list[0] == n && list[3] == n && list[6] == n)
                        || (list[1] == n && list[4] == n && list[7] == n)
                        || (list[2] == n && list[5] == n && list[8] == n)
                        || (list[0] == n && list[4] == n && list[8] == n)
                        || (list[2] == n && list[4] == n && list[6] == n)) {
            winner = n;
            return true;
        } else if (list[0] != 0 && list[1] != 0 && list[2] != 0 &&
                        list[3] != 0 && list[4] != 0 && list[5] != 0 &&
                        list[6] != 0 && list[7] != 0 && list[8] != 0) {
            return true;
        } else {
            return false;
        }
    }

    private void addReset() {
        Manager m = new Manager();
        Label label;
        if (winner != 0) {
            label = new Label(m.getPlayerName(groupID, winner) + " won!");
        } else {
            label = new Label("Tie Game!");
        }
        Button resetB = new Button("Reset Game", e -> {
            m.resetGame(groupID);
            reloadBoard();
        });
        add(label, resetB);
    }

    public void makeButtonClick(int value) {
        Manager m = new Manager();

        logger.info("groupID: '" + groupID + "' playerNumber: '" + playerNumber + "'");
        m.setMark(groupID, playerNumber, value + 1);
        m.setLastMoved(groupID, playerNumber);
        Broadcaster.broadcast(Integer.toString(groupID));
    }

    public Button makeButton(int value) {
        Button rv = new Button(getMarkSymbol(list[value]));

        rv.addClickListener(e -> {
            makeButtonClick(value);
        });
        if (list[value] != 0 || checkEnd() || playerNumber == lastMoved) {
            rv.setEnabled(false);
        }
        return rv;
    }

    public String getMarkSymbol(int i) {
        if (i == 0) {
            return "";
        } else if (i == 1) {
            return "X";
        } else {
            return "O";
        }
    }

}
