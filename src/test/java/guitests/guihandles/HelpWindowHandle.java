package guitests.guihandles;

import guitests.GuiRobot;
import javafx.stage.Stage;

/**
 * A handle to the {@code HelpWindow} of the application.
 */
public class HelpWindowHandle extends StageHandle {

    public static final String HELP_WINDOW_TITLE = "Help [Press Esc to close]";

    public static final String USERGUIDE_URL_ID = "#userGuideUrl";

    public HelpWindowHandle(Stage helpWindowStage) {
        super(helpWindowStage);
    }

    /**
     * Returns true if a help window is currently present in the application.
     */
    public static boolean isWindowPresent() {
        return new GuiRobot().isWindowShown(HELP_WINDOW_TITLE);
    }

    public String getUgUrl() {
        return new GuiRobot().lookup(USERGUIDE_URL_ID).queryLabeled().getText();
    }

}
