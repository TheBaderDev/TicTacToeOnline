package authentication;

public class AccessControlFactory {
    private static final AccessControlFactory INSTANCE = new AccessControlFactory();
    private final AccessControl accessControl = new BasicAccessControl();

    private AccessControlFactory() {
    }

    public static AccessControlFactory getInstance() {
        return INSTANCE;
    }

    public AccessControl getAccessControl() {
        return accessControl;
    }
}
