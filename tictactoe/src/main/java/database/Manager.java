package database;

import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.access.DataContext;
import org.apache.cayenne.configuration.server.ServerRuntime;
import org.apache.cayenne.datasource.DataSourceBuilder;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.SelectById;
import org.apache.log4j.Logger;
import com.mysql.cj.log.Log;
import objects.Group;
import objects.User;
import views.MainView;

public class Manager {

	public static ServerRuntime _runtime = null;

	public static synchronized ServerRuntime getRuntime() {
		if (_runtime == null) {
			// you can add this as argument to your project
			String mysqlPassword = System.getProperty("mysqlPassword", "Bkharbat74853200");

			_runtime = ServerRuntime.builder()
					.addConfig("cayenne-project.xml")
					.dataSource(DataSourceBuilder.url("jdbc:mysql://localhost/tictactoe?useLegacyDatetimeCode=false&serverTimezone=UTC&characterEncoding=UTF-8")
							.driver(com.mysql.cj.jdbc.Driver.class.getName())
							.userName("root")
							.password(mysqlPassword)
							.pool(1, 3).build())
					.build();
		}
		return _runtime;
	}

	public static synchronized void setRuntime(ServerRuntime serverRuntime) {
		_runtime = serverRuntime;
	}

	public static DataContext createContext() {
		return (DataContext) getRuntime().newContext();
	}

	public Manager() {
	}

	// Makes a user
	public User makeUser(String nickname, int ID) {
		// Creates User
		ObjectContext context = Manager.createContext();
		User rv = context.newObject(User.class);
		rv.setNickname(nickname);
		rv.setGroupId(ID);
		context.commitChanges();
		return rv;
	}
	
	public void addUserToGroup(int player1or2, int groupID, User user) {
		ObjectContext context = Manager.createContext();
		Group tempGroup = SelectById.query(Group.class, groupID).selectOne(context);
		if (player1or2 == 1) {
			tempGroup.setPlayer1Id(Cayenne.intPKForObject(user));
		} else {
			tempGroup.setPlayer2Id(Cayenne.intPKForObject(user));
		}
		context.commitChanges();
	}

	// Makes a group
	public int makeGroup() {
		ObjectContext context = Manager.createContext();
		Group rv = context.newObject(Group.class);
		rv.setPlayer1Id(0);
		rv.setPlayer2Id(0);
		rv.setP1(0);
		rv.setP2(0);
		rv.setP3(0);
		rv.setP4(0);
		rv.setP5(0);
		rv.setP6(0);
		rv.setP7(0);
		rv.setP8(0);
		rv.setP9(0);
		
		//Makes it so that 1 goes first
		rv.setLastToGo(2);
		context.commitChanges();
		return Cayenne.intPKForObject(rv);
	}

	public void setMark(int groupID, int player1or2, int markerNumber) {
		ObjectContext context = Manager.createContext();
		Group group = SelectById.query(Group.class, groupID).selectOne(context);
		if (group == null) {
			throw new IllegalArgumentException();
		}
		switch (markerNumber) {
		case (1):
			group.setP1(player1or2);
			break;
		case (2):
			group.setP2(player1or2);
			break;
		case (3):
			group.setP3(player1or2);
			break;
		case (4):
			group.setP4(player1or2);
			break;
		case (5):
			group.setP5(player1or2);
			break;
		case (6):
			group.setP6(player1or2);
			break;
		case (7):
			group.setP7(player1or2);
			break;
		case (8):
			group.setP8(player1or2);
			break;
		case (9):
			group.setP9(player1or2);
			break;
		}
		context.commitChanges();
	}
	
	public int[] getBoardState(int groupID) {
		ObjectContext context = Manager.createContext();
		Group group = SelectById.query(Group.class, groupID).selectOne(context);
		int[] rv = new int[9];
		rv[0] = group.getP1();
		rv[1] = group.getP2();
		rv[2] = group.getP3();
		rv[3] = group.getP4();
		rv[4] = group.getP5();
		rv[5] = group.getP6();
		rv[6] = group.getP7();
		rv[7] = group.getP8();
		rv[8] = group.getP9();
		return rv;
	}

	public Group deleteGroup(int playerID) {
		ObjectContext context = Manager.createContext();

		Group tempGroup = ObjectSelect.query(Group.class, Group.PLAYER1ID.eq(playerID)).selectOne(context);
		if (tempGroup == null) {
			tempGroup = ObjectSelect.query(Group.class, Group.PLAYER2ID.eq(playerID)).selectOne(context);
		}
		if (tempGroup == null) {
			throw new IllegalArgumentException("No Such Group Exists");
		}
		context.deleteObject(tempGroup);
		context.commitChanges();
		return tempGroup;
	}

	public int getNumberPlayers(int groupid) {
		ObjectContext context = Manager.createContext();
		Group tempGroup = SelectById.query(Group.class, groupid).selectOne(context);
		int p1 = tempGroup.getPlayer1Id();
		int p2 = tempGroup.getPlayer2Id();
		if (p1 == 0 && p2 == 0) {
			return 0;
		} else if (p1 == 0 || p2 == 0) {
			return 1;
		} else {
			return 2;
		}
	}

	public User deleteUser(User user) {
		int ID = (int) Cayenne.longPKForObject(user);

		// gets the user
		ObjectContext context = Manager.createContext();
		User tempUser = SelectById.query(User.class, ID).selectOne(context);
		if (tempUser == null) {
			throw new IllegalArgumentException("No Such User Exists");
		}

		// deletes the user ID from the group
		Group tempGroup = SelectById.query(Group.class, tempUser.getGroupId()).selectOne(context);
		try {
			if (getNumberPlayers(Cayenne.intPKForObject(tempGroup)) == 2) {
				if (tempGroup.getPlayer1Id() == (int) Cayenne.intPKForObject(tempUser)) {
					tempGroup.setPlayer1Id(0);
				} else if (tempGroup.getPlayer2Id() == (int) Cayenne.intPKForObject(tempUser)) {
					tempGroup.setPlayer2Id(0);
				}
			}
		} catch (IllegalArgumentException e) {
			
		}

		// deletes user
		context.deleteObject(tempUser);
		context.commitChanges();
		return tempUser;
	}

	public static User getUserById(int ID) {
		ObjectContext context = Manager.createContext();
		User rv = SelectById.query(User.class, ID).selectOne(context);
		if (rv == null) {
			throw new IllegalArgumentException("User does not exist");
		} else {
			return rv;
		}
	}
	
	public static Group getGroupById(int ID) {
		ObjectContext context = Manager.createContext();
		Group rv = SelectById.query(Group.class, ID).selectOne(context);
		if (rv == null) {
			throw new IllegalArgumentException("Group does not exist");
		} else {
			return rv;
		}
	}

	public static int getPlayerNumberInGroup(User user) {
		ObjectContext context = Manager.createContext();
		int groupID = user.getGroupId();
		Group tempGroup = SelectById.query(Group.class, groupID).selectOne(context);
		int userID = Cayenne.intPKForObject(user);
		if (tempGroup.getPlayer1Id() == userID) {
			return 1;
		} else if (tempGroup.getPlayer2Id() == userID) {
			return 2;
		} else {
			return 0;
		}
	}

	public boolean isGroupActive(Integer parameter) {
		ObjectContext context = Manager.createContext();
		Group tempGroup = SelectById.query(Group.class, parameter).selectOne(context);
		if(tempGroup == null) {
			return false;
		} else {
			return true;
		}
	}

	public int getLastMoved(int groupID) {
		ObjectContext context = Manager.createContext();
		Group tempGroup = SelectById.query(Group.class, groupID).selectOne(context);
		return tempGroup.getLastToGo();
	}

	public void setLastMoved(int groupID, int playerNumber) {
		ObjectContext context = Manager.createContext();
		Group tempGroup = SelectById.query(Group.class, groupID).selectOne(context);
		tempGroup.setLastToGo(playerNumber);
		context.commitChanges();
	}

	public String getPlayerName(int groupID, int playerNumber) {
		ObjectContext context = Manager.createContext();
		Group tempGroup = SelectById.query(Group.class, groupID).selectOne(context);
		User tempUser;
		if (playerNumber == 1) {
			tempUser = SelectById.query(User.class, tempGroup.getPlayer1Id()).selectOne(context);
		} else {
			tempUser = SelectById.query(User.class, tempGroup.getPlayer2Id()).selectOne(context);
		}
		return tempUser.getNickname();
	}

	public void resetGame(int groupID) {
		ObjectContext context = Manager.createContext();
		Group tempGroup = SelectById.query(Group.class, groupID).selectOne(context);
		tempGroup.setP1(0);
		tempGroup.setP2(0);
		tempGroup.setP3(0);
		tempGroup.setP4(0);
		tempGroup.setP5(0);
		tempGroup.setP6(0);
		tempGroup.setP7(0);
		tempGroup.setP8(0);
		tempGroup.setP9(0);
		context.commitChanges();
	}
}
