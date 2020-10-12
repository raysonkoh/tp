package guitests.guihandles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.control.ListView;
import seedu.address.model.client.Client;

/**
 * Provides a handle for {@code PersonListPanel} containing the list of {@code PersonCard}.
 */
public class ClientListPanelHandle extends NodeHandle<ListView<Client>> {
    public static final String CLIENT_LIST_VIEW_ID = "#clientListView";

    private static final String CARD_PANE_ID = "#cardPane";

    private Optional<Client> lastRememberedSelectedClientCard;

    public ClientListPanelHandle(ListView<Client> clientListPanelNode) {
        super(clientListPanelNode);
    }

    /**
     * Returns a handle to the selected {@code PersonCardHandle}.
     * A maximum of 1 item can be selected at any time.
     * @throws AssertionError if no card is selected, or more than 1 card is selected.
     * @throws IllegalStateException if the selected card is currently not in the scene graph.
     */
    public ClientCardHandle getHandleToSelectedCard() {
        List<Client> selectedPersonList = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedPersonList.size() != 1) {
            throw new AssertionError("Person list size expected 1.");
        }

        return getAllCardNodes().stream()
                .map(ClientCardHandle::new)
                .filter(handle -> handle.equals(selectedPersonList.get(0)))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    /**
     * Returns the index of the selected card.
     */
    public int getSelectedCardIndex() {
        return getRootNode().getSelectionModel().getSelectedIndex();
    }

    /**
     * Returns true if a card is currently selected.
     */
    public boolean isAnyCardSelected() {
        List<Client> selectedCardsList = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedCardsList.size() > 1) {
            throw new AssertionError("Card list size expected 0 or 1.");
        }

        return !selectedCardsList.isEmpty();
    }

    /**
     * Navigates the listview to display {@code person}.
     */
    public void navigateToCard(Client client) {
        if (!getRootNode().getItems().contains(client)) {
            throw new IllegalArgumentException("Client does not exist.");
        }

        guiRobot.interact(() -> {
            getRootNode().scrollTo(client);
        });
        guiRobot.pauseForHuman();
    }

    /**
     * Navigates the listview to {@code index}.
     */
    public void navigateToCard(int index) {
        if (index < 0 || index >= getRootNode().getItems().size()) {
            throw new IllegalArgumentException("Index is out of bounds.");
        }

        guiRobot.interact(() -> {
            getRootNode().scrollTo(index);
        });
        guiRobot.pauseForHuman();
    }

    /**
     * Selects the {@code ClientCard} at {@code index} in the list.
     */
    public void select(int index) {
        getRootNode().getSelectionModel().select(index);
    }

    /**
     * Returns the client card handle of a client associated with the {@code index} in the list.
     * @throws IllegalStateException if the selected card is currently not in the scene graph.
     */
    public ClientCardHandle getClientCardHandle(int index) {
        return getAllCardNodes().stream()
                .map(ClientCardHandle::new)
                .filter(handle -> handle.equals(getClient(index)))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    private Client getClient(int index) {
        return getRootNode().getItems().get(index);
    }

    /**
     * Returns all card nodes in the scene graph.
     * Card nodes that are visible in the listview are definitely in the scene graph, while some nodes that are not
     * visible in the listview may also be in the scene graph.
     */
    private Set<Node> getAllCardNodes() {
        return guiRobot.lookup(CARD_PANE_ID).queryAll();
    }

    /**
     * Remembers the selected {@code ClientCard} in the list.
     */
    public void rememberSelectedClientCard() {
        List<Client> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            lastRememberedSelectedClientCard = Optional.empty();
        } else {
            lastRememberedSelectedClientCard = Optional.of(selectedItems.get(0));
        }
    }

    /**
     * Returns true if the selected {@code ClientCard} is different from the value remembered by the most recent
     * {@code rememberSelectedClientCard()} call.
     */
    public boolean isSelectedPersonCardChanged() {
        List<Client> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            return lastRememberedSelectedClientCard.isPresent();
        } else {
            return !lastRememberedSelectedClientCard.isPresent()
                    || !lastRememberedSelectedClientCard.get().equals(selectedItems.get(0));
        }
    }

    /**
     * Returns the size of the list.
     */
    public int getListSize() {
        return getRootNode().getItems().size();
    }
}
