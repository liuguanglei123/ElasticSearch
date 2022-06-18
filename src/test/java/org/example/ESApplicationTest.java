package org.example;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.example.config.ElasticSearchConfig;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-06-18-18:13
 * @Description:
 */
@SpringBootTest
public class ESApplicationTest {

    @Autowired
    ElasticSearchConfig elasticSearchConfig;

    @Test
    public void contextLoads(){
        // 方式1.创建ES客户端对象
        // 不依赖springboot项目，单纯的创建一个client
//        String esHost = "192.168.0.0";
//        int esPort = 9200;
//        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(
//                new HttpHost(
//                        esHost,
//                        esPort,
//                        "http"
//                )
//        ));
        // 方式2，使用spring的控制反转，创建一个ElasticSearchConfig类，勇于创建ES客户端对象
        // 详见 ElasticSearchConfig 类


    }
}
