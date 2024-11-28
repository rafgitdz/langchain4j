package fr.rafdz.langchain4j.ai.csv;

import com.googlecode.jcsv.reader.CSVEntryParser;

public class AdresseEntryParser implements CSVEntryParser<Adresse> {

    @Override
    public Adresse parseEntry(String... data) {
        return Adresse.builder()
                .nomCommune(data[0])
                .build();
    }
}
