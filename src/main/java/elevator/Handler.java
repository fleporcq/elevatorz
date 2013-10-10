package elevator;


import elevator.models.Command;
import elevator.models.Direction;
import elevator.models.Elevator;
import elevator.models.requests.Call;
import elevator.models.requests.Go;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class Handler extends AbstractHandler {

    private static final String DEFAULT_PORT = "8080";
    private final Logger logger;
    private final Elevator elevator;

    public Handler() {
        logger = Logger.getGlobal();
        elevator = Elevator.getInstance();
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        switch (target) {
            case "/nextCommand":
                synchronized (elevator) {
                    Command nextCommand = elevator.getNextCommand();
                    if (Command.RESET.equals(nextCommand)) {
                        logger.warning("RESET (Cause loop detected)");
                    } else {
                        baseRequest.getResponse().getWriter().println(nextCommand.name());
                    }
                }
                break;
            case "/call":
                synchronized (elevator) {
                    Integer atFloor = Integer.valueOf(baseRequest.getParameter("atFloor"));
                    Direction to = Direction.valueOf(baseRequest.getParameter("to"));
                    elevator.addCall(new Call(atFloor, to));
                }
                break;
            case "/go":
                synchronized (elevator) {
                    Integer floorToGo = Integer.valueOf(baseRequest.getParameter("floorToGo"));
                    elevator.addGo(new Go(floorToGo));
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
                logger.warning(target);
        }
        baseRequest.setHandled(true);
    }

    private static int getPort(){
        String port = System.getenv("PORT");
        if(port == null){
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
