package objects.auto;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.cayenne.BaseDataObject;
import org.apache.cayenne.exp.Property;

/**
 * Class _User was generated by Cayenne.
 * It is probably a good idea to avoid changing this class manually,
 * since it may be overwritten next time code is regenerated.
 * If you need to make any customizations, please use subclass.
 */
public abstract class _User extends BaseDataObject {

    private static final long serialVersionUID = 1L; 

    public static final String ID_PK_COLUMN = "ID";

    public static final Property<String> NICKNAME = Property.create("nickname", String.class);
    public static final Property<Integer> GROUP_ID = Property.create("groupId", Integer.class);

    protected String nickname;
    protected int groupId;


    public void setNickname(String nickname) {
        beforePropertyWrite("nickname", this.nickname, nickname);
        this.nickname = nickname;
    }

    public String getNickname() {
        beforePropertyRead("nickname");
        return this.nickname;
    }

    public void setGroupId(int groupId) {
        beforePropertyWrite("groupId", this.groupId, groupId);
        this.groupId = groupId;
    }

    public int getGroupId() {
        beforePropertyRead("groupId");
        return this.groupId;
    }

    @Override
    public Object readPropertyDirectly(String propName) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch(propName) {
            case "nickname":
                return this.nickname;
            case "groupId":
                return this.groupId;
            default:
                return super.readPropertyDirectly(propName);
        }
    }

    @Override
    public void writePropertyDirectly(String propName, Object val) {
        if(propName == null) {
            throw new IllegalArgumentException();
        }

        switch (propName) {
            case "nickname":
                this.nickname = (String)val;
                break;
            case "groupId":
                this.groupId = val == null ? 0 : (int)val;
                break;
            default:
                super.writePropertyDirectly(propName, val);
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        writeSerialized(out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        readSerialized(in);
    }

    @Override
    protected void writeState(ObjectOutputStream out) throws IOException {
        super.writeState(out);
        out.writeObject(this.nickname);
        out.writeInt(this.groupId);
    }

    @Override
    protected void readState(ObjectInputStream in) throws IOException, ClassNotFoundException {
        super.readState(in);
        this.nickname = (String)in.readObject();
        this.groupId = in.readInt();
    }

}
