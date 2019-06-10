package applicationstuff;

import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import com.vaadin.flow.shared.Registration;

public class Broadcaster {
    static Executor executor = Executors.newSingleThreadExecutor();

    static LinkedList<Consumer<String>> listeners = new LinkedList<>();

    public static synchronized Registration register(Consumer<String> listener) {
        listeners.add(listener);

        return () -> {
            synchronized (Broadcaster.class) {
                listeners.remove(listener);
            }
        };
    }

    public static synchronized void broadcast(String message) {
        for (Consumer<String> listener : listeners) {
            executor.execute(() -> listener.accept(message));
        }
    }
}

/**
 * RECEIVING
 * 
 * @Push @Route("broadcaster") public class BroadcasterView extends Div { VerticalLayout messages = new
 *       VerticalLayout(); Registration broadcasterRegistration;
 * 
 *       // Creating the UI shown separately
 * 
 * @Override protected void onAttach(AttachEvent attachEvent) { UI ui = attachEvent.getUI(); broadcasterRegistration =
 *           Broadcaster.register(newMessage -> { ui.access(() -> messages.add(new Span(newMessage))); }); }
 * 
 * @Override protected void onDetach(DetachEvent detachEvent) { broadcasterRegistration.remove();
 *           broadcasterRegistration = null; } }
 */

/**
 * Sending Broadcasts
 * 
 * public BroadcasterView() { TextField message = new TextField(); Button send = new Button("Send", e -> {
 * Broadcaster.broadcast(message.getValue()); message.setValue(""); });
 * 
 * HorizontalLayout sendBar = new HorizontalLayout(message, send);
 * 
 * add(sendBar, messages); }
 */
