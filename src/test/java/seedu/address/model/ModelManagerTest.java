package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_CLIENTS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalClients.ALICE;
import static seedu.address.testutil.TypicalClients.BENSON;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.client.Client;
import seedu.address.model.client.NameContainsKeywordsPredicate;
import seedu.address.model.country.Country;
import seedu.address.model.note.CountryNote;
import seedu.address.model.note.Note;
import seedu.address.model.note.TagNoteMap;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.ClientBuilder;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void setUserPrefs_nullUserPrefs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setUserPrefs(null));
    }

    @Test
    public void setUserPrefs_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setAddressBookFilePath(Paths.get("address/book/file/path"));
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager.setUserPrefs(userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setAddressBookFilePath(Paths.get("new/address/book/file/path"));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void setAddressBookFilePath_nullPath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setAddressBookFilePath(null));
    }

    @Test
    public void setAddressBookFilePath_validPath_setsAddressBookFilePath() {
        Path path = Paths.get("address/book/file/path");
        modelManager.setAddressBookFilePath(path);
        assertEquals(path, modelManager.getAddressBookFilePath());
    }

    @Test
    public void deleteClient_deleteExistingClient_returnsTrue() {
        Client target = new ClientBuilder(ALICE).build();
        modelManager.addClient(target);
        assertTrue(modelManager.hasClient(ALICE));
        modelManager.deleteClient(target);
        assertFalse(modelManager.hasClient(target));
    }

    @Test
    public void hasClient_nullClient_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasClient(null));
    }

    @Test
    public void hasClient_clientNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasClient(ALICE));
    }

    @Test
    public void hasClient_clientInAddressBook_returnsTrue() {
        Client client = new ClientBuilder(ALICE).build();
        assertFalse(modelManager.hasClient(client));
        modelManager.addClient(client);
        assertTrue(modelManager.hasClient(client));
    }

    @Test
    public void addAndHasCountryNote_validCountry_updatesCorrectly() {
        Country country = new Country("SG");
        CountryNote genericNote = new CountryNote("generic note", country);
        assertFalse(modelManager.hasCountryNote(genericNote));

        modelManager.addCountryNote(genericNote);
        assertTrue(modelManager.hasCountryNote(genericNote));
    }

    @Test
    public void getFilteredClientList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredClientList().remove(0));
    }

    @Test
    public void addAndHasClientNote_validSyntax_updatesCorrectly() {
        Client client = new ClientBuilder(ALICE).build();
        Note clientNote = new Note("this be a client note");
        assertFalse(modelManager.hasClientNote(client, clientNote));
        modelManager.addClientNote(client, clientNote);
        assertTrue(modelManager.hasClientNote(client, clientNote));
    }

    @Test
    public void deleteClientNote_validSyntax_deletesSuccessfully() {
        Client client = new ClientBuilder(ALICE).build();
        UserPrefs userPrefs = new UserPrefs();
        AddressBook addressBook = new AddressBookBuilder().withClient(client).build();
        modelManager = new ModelManager(addressBook, userPrefs);
        Note clientNote = new Note("this be a client note");
        modelManager.addClientNote(client, clientNote);
        assertTrue(modelManager.hasClientNote(client, clientNote));
        modelManager.initialiseTagNoteMap();
        modelManager.deleteClientNote(client, clientNote);
        assertFalse(modelManager.hasClientNote(client, clientNote));
    }

    @Test
    public void getTagNoteMap_returnUninitialisedTagNoteMap_returnsTrue() {
        TagNoteMap expected = new TagNoteMap();
        assertTrue(modelManager.getTagNoteMap().equals(expected));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withClient(ALICE).withClient(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredClientList(new NameContainsKeywordsPredicate(Arrays.asList(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredClientList(PREDICATE_SHOW_ALL_CLIENTS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setAddressBookFilePath(Paths.get("differentFilePath"));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    @Test
    public void initializeTagNoteMap_validInputs_successful() {
        Set<Tag> tags = new HashSet<>();
        tags.add(new Tag("tagName"));
        Note taggedNote = new Note("jurong hill was a nice place");
        taggedNote.setTags(tags);
        Client aliceTagged = new ClientBuilder(ALICE).build();
        aliceTagged.addClientNote(taggedNote);
        this.modelManager.addClient(aliceTagged);
        assertDoesNotThrow(() -> this.modelManager.initialiseTagNoteMap());
    }

    @Test
    public void addCountryNote_validCountryNote_addCountryNote() {
        CountryNote countryNote = new CountryNote("some", new Country("SG"));
        assertFalse(modelManager.hasCountryNote(countryNote));
        modelManager.addCountryNote(countryNote);
        assertTrue(modelManager.hasCountryNote(countryNote));
    }

    @Test
    public void updateFilteredCountryNoteList_truePredicate_showAllCountryNotes() {
        int initialSize = modelManager.getFilteredCountryNoteList().size();
        modelManager.updateFilteredCountryNoteList(countryNote -> true);
        assertEquals(initialSize, modelManager.getFilteredCountryNoteList().size());
    }

    @Test
    public void updateFilteredCountryNoteList_falsePredicate_showNoneCountryNotes() {
        modelManager.updateFilteredCountryNoteList(countryNote -> false);
        assertEquals(0, modelManager.getFilteredCountryNoteList().size());
    }

    @Test
    public void updateFilteredCountryNoteList_countryPredicate_showRelevantCountryNotes() {
        modelManager.updateFilteredCountryNoteList(countryNote -> true);
        int expect = (int) modelManager.getFilteredCountryNoteList()
                .stream()
                .filter(countryNote -> countryNote.getCountry().equals(new Country("SG")))
                .count();
        modelManager.updateFilteredCountryNoteList(countryNote -> countryNote.equals(new Country("SG")));
        assertEquals(expect, modelManager.getFilteredCountryNoteList().size());
    }

    /* todo future tests:
     *  run coverage for model manager test and see what's missing:
     * 1. setClient
     * 2. widgetContent setter and gettter
     * 3. getFilteredClientNotesList
     *
     * */

}
