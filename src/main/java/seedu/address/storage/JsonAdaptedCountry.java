package seedu.address.storage;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.country.Country;
import seedu.address.model.note.Note;

/**
 * Jackson-friendly version of {@link Country}.
 */
public class JsonAdaptedCountry {
    private final String countryCode;
    private final String countryName;
    private Set<JsonAdaptedNote> countryJsonNotes = new LinkedHashSet<>();

    @JsonCreator
    public JsonAdaptedCountry(
            @JsonProperty("countryCode") String countryCode,
            @JsonProperty("countryName") String countryName,
            @JsonProperty("countryNotes") List<JsonAdaptedNote> countryJsonNotes) {
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.countryJsonNotes = countryJsonNotes.stream().collect(Collectors.toSet());
    }

    /**
     * Converts a given {@code Country} into this class for Jackson use.
     */
    public JsonAdaptedCountry(Country source) {
        this.countryCode = source.getCountryCode();
        this.countryName = source.getCountryName();
        this.countryJsonNotes = source.getCountryNotes().stream().map(jsonNote -> jsonNote.toModelType()).collect(Collectors.toSet());
    }

    /**
     * Converts this Jackson-friendly adapted country object into the model's {@code Country} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted client.
     */
    public Country toModelType() {
        Set<Note> countryNotes = countryJsonNotes.map(jsonNote -> jsonNote.toModelType()).collect(Collectors.toSet());
        return new Country(countryCode, countryName, countryNotes);
    }

}
