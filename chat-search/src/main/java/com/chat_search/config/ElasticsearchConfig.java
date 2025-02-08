package com.chat_search.config;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PreDestroy;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.chat_search.repository")
public class ElasticsearchConfig {

    private RestClient restClient;  // Store reference to close it in @PreDestroy

    @Bean
    public ElasticsearchClient elasticsearchClient() {
        this.restClient = RestClient.builder(new HttpHost("localhost", 9200)).build();
        ElasticsearchTransport transport = new RestClientTransport(
            restClient, new JacksonJsonpMapper()
        );
        return new ElasticsearchClient(transport);
    }

    @PreDestroy
    public void closeClient() {
        if (restClient != null) {
            try {
                restClient.close();
                System.out.println("Closed Elasticsearch RestClient successfully.");
            } catch (IOException e) {
                System.err.println("Error closing Elasticsearch RestClient: " + e.getMessage());
            }
        }
    }
}
