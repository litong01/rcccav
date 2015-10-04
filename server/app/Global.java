
import play.GlobalSettings;
import play.Logger;
import play.Application;

public class Global extends GlobalSettings {

    public void onStart(Application app) {
        Logger.debug("RCCC AV System is ready!");
    }

    public void onStop(Application app) {
        Logger.debug("Application is shutting down!");
    }

}