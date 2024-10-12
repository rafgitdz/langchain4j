package fr.rafdz.langchain4j.ai.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Adresse {
    @CsvBindByName(column = "nom_commune")
    private String nomCommune;
}
