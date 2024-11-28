package org.example.rag;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.net.URI;
import java.net.URISyntaxException;

public class EmbeddingStoreProducer {

  @Value("AZURE_SEARCH_INDEX")
  String azureSearchIndexName;

  @Value("QDRANT_URL")
  String qdrantUrl;

  @Bean
  public EmbeddingStore<TextSegment> embeddingStore() throws URISyntaxException {
    String qdrantHostname = new URI(qdrantUrl).getHost();
    int qdrantPort = new URI(qdrantUrl).getPort();
    return QdrantEmbeddingStore.builder()
      .collectionName(azureSearchIndexName)
      .host(qdrantHostname)
      .port(qdrantPort)
      .build();
  }
}

