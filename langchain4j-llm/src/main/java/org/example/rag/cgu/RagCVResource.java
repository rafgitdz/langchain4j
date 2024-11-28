package org.example.rag.cgu;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

// @RestController
public class RagCVResource {

    private final ChatLanguageModel chatLanguageModel;
    private final EmbeddingModel embeddingModel;
    private final RagCVAssistant ragAssistant;
    private EmbeddingStore<TextSegment> embeddingStore;

    public RagCVResource(ChatLanguageModel chatLanguageModel,
                         EmbeddingModel embeddingModel) throws URISyntaxException {
        this.chatLanguageModel = chatLanguageModel;
        this.embeddingModel = embeddingModel;
        this.ragAssistant = createAssistant();
    }

    @CrossOrigin(origins = "*")
    @GetMapping("/rag_cv")
    public String assistant(@RequestParam(value = "message") String message) {
        return this.ragAssistant.chat(message);
    }

    private void indexDocument(Path documentPath, EmbeddingModel embeddingModel) {
        DocumentParser documentParser = new ApacheTikaDocumentParser();
        Document document = loadDocument(documentPath, documentParser);

        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);

        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        embeddingStore.addAll(embeddings, segments);
    }

    private RagCVAssistant createAssistant() throws URISyntaxException {
        String qdrantUrl = "http://localhost:6334";
        String qdrantHostname = new URI(qdrantUrl).getHost();
        int qdrantPort = new URI(qdrantUrl).getPort();
        embeddingStore = QdrantEmbeddingStore.builder()
                .collectionName("cv_embeddings")
                .host(qdrantHostname)
                .port(qdrantPort)
                .build();

        indexDocument(toPath("documents/cv/CV_TEMPLATE_0002.pdf"), embeddingModel);
        indexDocument(toPath("documents/cv/CV_TEMPLATE_0003.pdf"), embeddingModel);
        indexDocument(toPath("documents/cv/new_resume_001.pdf"), embeddingModel);

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(2)
                .minScore(0.8)
                .build();

        // Let's create a query router that will route each query to both retrievers.
        QueryRouter queryRouter = new DefaultQueryRouter(contentRetriever);
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryRouter(queryRouter)
                .build();

        return AiServices.builder(RagCVAssistant.class)
                .chatLanguageModel(this.chatLanguageModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

    }

    public static Path toPath(String relativePath) {
        try {
            URL fileUrl = RagCVResource.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static EmbeddingStore<TextSegment> embed(Path documentPath, EmbeddingModel embeddingModel) {
        DocumentParser documentParser = new ApacheTikaDocumentParser();
        Document document = loadDocument(documentPath, documentParser);

        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);

        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);
        return embeddingStore;
    }
}
