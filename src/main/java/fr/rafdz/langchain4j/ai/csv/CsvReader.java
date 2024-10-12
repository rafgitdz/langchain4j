package fr.rafdz.langchain4j.ai.csv;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.exceptions.CsvException;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Component
public class CsvReader {

    public List<Adresse> addressList;

    @Tool
    public List<Adresse> getAddressList() throws IOException, CsvException, URISyntaxException {
        Path path = Paths.get(ClassLoader.getSystemResource("csv/adresses-01.csv").toURI());
        Reader reader = Files.newBufferedReader(path);
        CsvToBean<Adresse> cb = new CsvToBeanBuilder<Adresse>(reader)
                .withType(Adresse.class)
                .build();
        return cb.parse();
    }
}
