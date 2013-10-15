package elevator;


import elevator.models.Command;
import elevator.models.Direction;
import elevator.models.Elevator;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Handler extends AbstractHandler {

    private static final String DEFAULT_PORT = "8888";
    private final Elevator elevator;

    public Handler() {
        elevator = Elevator.getInstance();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        switch (target) {
            case "/nextCommand":
                synchronized (elevator) {
                    Command nextCommand = elevator.nextCommand();
                    if (Command.RESET.equals(nextCommand)) {
                        Logger.warning("Elevator reset cause loop detected");
                    } else {
                        baseRequest.getResponse().getWriter().println(nextCommand.name());
                    }
                }
                break;
            case "/call":
                synchronized (elevator) {
                    try {
                        int atFloor = Integer.valueOf(baseRequest.getParameter("atFloor"));
                        Direction to = Direction.valueOf(baseRequest.getParameter("to"));
                        elevator.call(atFloor, to);
                    } catch (NumberFormatException e) {
                        Logger.warning("'atFloor' or 'to' param is not a number");
                    }
                }
                break;
            case "/go":
                synchronized (elevator) {
                    try {
                        int floorToGo = Integer.valueOf(baseRequest.getParameter("floorToGo"));
                        elevator.go(floorToGo);
                    } catch (NumberFormatException e) {
                        Logger.warning("'floorToGo' param is not a number");
                    }
                }
                break;
            case "/userHasEntered":
                synchronized (elevator) {
                    elevator.userHasEntered();
                }
                break;
            case "/userHasExited":
                synchronized (elevator) {
                    elevator.userHasExited();
                }
                break;
            case "/reset":
                String cause = baseRequest.getParameter("cause");
                synchronized (elevator) {
                    elevator.reset(cause);
                }
                break;
            default:
                Logger.warning(target);
        }
        baseRequest.setHandled(true);
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        if (port == null) {
            port = System.getProperty("app.port", DEFAULT_PORT);
        }
        return Integer.valueOf(port);
    }

    public static void main(String... args) throws Exception {
        Server server = new Server(getPort());
        server.setHandler(new Handler());
        server.start();
        server.join();
    }
}
