package org.example.rag.cv;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@RestController
public class RagCGUResource {

    private final ChatLanguageModel chatLanguageModel;
    private final EmbeddingModel embeddingModel;
    private final RagCGUAssistant ragCGUAssistant;

    public RagCGUResource(ChatLanguageModel chatLanguageModel, EmbeddingModel embeddingModel) {
        this.chatLanguageModel = chatLanguageModel;
        this.embeddingModel = embeddingModel;
        this.ragCGUAssistant = createAssistant();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/rag_cgu")
    public String assistant(@RequestParam(value = "message") String message) {
        return this.ragCGUAssistant.chat(message);
    }

    private RagCGUAssistant createAssistant() {

        // Now, let's load a document that we want to use for RAG.
        DocumentParser documentParser = new ApacheTikaDocumentParser();
        Document document = loadDocument(
                toPath("documents/sncf/tarifs-voyageurs-version-3-mai-2024-VDEF.pdf"),
                documentParser
        );

        // on met tout ça en Segments
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);

        // on Vectorise :p
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        // on stocke tout ça dans une base Vectorielle
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);

        // on met ensemble tout ça pour permettre la recherche approchante du segment de la requête User au LLM
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2) // on each interaction we will retrieve the 2 most relevant segments
                .minScore(0.5) // we want to retrieve segments at least somewhat similar to user query
                .build();

        // une Mémoire pour aider notre char LLM de se souvenir de la discussion
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        // et hop un AIService avec tout ça
        return AiServices.builder(RagCGUAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .contentRetriever(contentRetriever)
                .chatMemory(chatMemory)
                .build();

    }

    public static Path toPath(String relativePath) {
        try {
            URL fileUrl = RagCGUResource.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
