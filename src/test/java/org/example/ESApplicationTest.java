package org.example;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpPost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.example.config.ElasticSearchConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-06-18-18:13
 * @Description:
 */
@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class ESApplicationTest {

    @Autowired
    @Qualifier("MyEsClient")
    RestHighLevelClient client;

    @Test
    public void contextLoads() {
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

        log.info(client.toString());
        System.out.println(client);
    }


    /**
     * 添加索引
     */
    @Test
    @Ignore
    public void addIndex() throws IOException {
        //1.使用client获取操作索引的对象
        IndicesClient indicesClient = client.indices();
        //2.具体操作，获取返回值
        CreateIndexRequest createIndex = new CreateIndexRequest("test_index");
        //2.1设置mapping
        String mapping = "{\n" +
                "\n" +
                "  ”properties”:{ //properties固定\n" +
                "    ”name”:{\n" +
                "     ”type”:”keyword”\n" +
                "    },\n" +
                "    ”age”:{\n" +
                "     ”type”:”integer”\n" +
                "    },\n" +
                "   }\n" +
                "  }\n" +
                " }";
        createIndex.mapping(mapping, XContentType.JSON);
        CreateIndexResponse response = indicesClient.create(createIndex, new Header[]{});

        //3.根据返回值判断结果
        log.info(String.valueOf(response.isAcknowledged()));
    }

    @Test
    @Ignore
    public void deleteIndex() throws IOException {
        //1.使用client获取操作索引的对象
        IndicesClient indicesClient = client.indices();

        //删除索引
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest("test_index");
        indicesClient.delete(deleteRequest, new Header[]{});
    }

    //判断索引是否存在
    @Test
    @Ignore
    public void existIndex() throws IOException {
        ///1.使用client获取操作索引的对象
        IndicesClient indicesClient = client.indices();

        GetIndexRequest existRequest = new GetIndexRequest();
        indicesClient.exists(existRequest, new Header[]{});
    }

    /**
     * 添加文档
     */
    public void addDoc(){
        IndicesClient indicesClient = client.indices();
    }

}
