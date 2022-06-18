package org.example.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-06-18-20:43
 * @Description:
 */
@Configuration
@ConfigurationProperties(prefix = "elasticsearch")
@Data
public class ElasticSearchConfig {

    private String host;

    private Integer port;

    @Bean
    public RestHighLevelClient client(){
        // 创建ES客户端对象
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
                new HttpHost(
                        host,
                        port,
                        "http"
                )
        ));

        return client;
    }
}
