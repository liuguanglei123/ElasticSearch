package org.example;

import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import sun.misc.IOUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
    }


    /**
     * 添加索引，只做一个展示，没有验证过逻辑是否正确
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
        CreateIndexResponse response = indicesClient.create(createIndex, RequestOptions.DEFAULT);

        //3.根据返回值判断结果
        log.info(String.valueOf(response.isAcknowledged()));
    }

    /**
     * 删除索引，只做一个展示，没有验证过逻辑是否正确
     */
    @Test
    @Ignore
    public void deleteIndex() throws IOException {
        //1.使用client获取操作索引的对象
        IndicesClient indicesClient = client.indices();

        //删除索引
        DeleteIndexRequest deleteRequest = new DeleteIndexRequest("test_index");
        indicesClient.delete(deleteRequest, RequestOptions.DEFAULT);
    }

    /**
     * 判断索引是否存在
     */
    @Test
    @Ignore
    public void existIndex() throws IOException {
        // 1.使用client获取操作索引的对象
        IndicesClient indicesClient = client.indices();

        // 2.如果不报错，就表示获取索引成功了
        GetIndexRequest existRequest = new GetIndexRequest(".kibana_task_manager_1");
        indicesClient.get(existRequest, RequestOptions.DEFAULT);
    }

    /**
     * 添加文档
     */
    @Test
    public void addDoc() throws IOException {
        //数据对象
        Map data = new HashMap<>();

        data.put("content","this is content");
        data.put("email","123123123@126.com");
        data.put("name","this is name");
        data.put("type","this is type");
        data.put("user_name","this is user_name");
        data.put("tweeted_at",new Date(System.currentTimeMillis()));

        log.info(client.info(RequestOptions.DEFAULT).getClusterUuid());
        log.info(client.info(RequestOptions.DEFAULT).getClusterName().toString());

        //1.获取操作文档对象
//        IndexRequest request = new IndexRequest("test").id("00001").source(data);
        //添加数据
//        IndexResponse response = client.update(request, new Header[]{});

        //2.打印响应结果
//        log.info(response.getId());
    }

}
