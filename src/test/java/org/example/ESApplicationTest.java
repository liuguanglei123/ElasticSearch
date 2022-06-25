package org.example;

import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.example.pojo.Person;
import org.junit.Test;
import org.junit.runner.Request;
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
     * 添加文档，通过map添加
     */
    @Test
    public void addDocByMap() throws IOException {
        //构造一个简单的map
        Map<String, Object> data = new HashMap<>();
        data.put("content","this is content");
        data.put("email","123123123@126.com");
        data.put("name","this is name");
        data.put("type","this is type");
        data.put("user_name","this is user_name");
        data.put("tweeted_at",new Date(System.currentTimeMillis()));

        //获取操作文档的对象
        IndexRequest request = new IndexRequest("ittest").source(data);
        //将数据请求到es
        client.index(request, RequestOptions.DEFAULT);

//        如果没有报错，就可以在es中查询到添加的数据了
//        GET ittest/_search
//        {
//            "query":{
//              "match_all":{}
//            }
//        }
    }

    /**
     * 添加文档，通过构造对象添加
     */
    @Test
    public void addDocByJavaObject() throws IOException {
        //构造一个简单的JavaObject对象
        Person p = new Person();
        p.setContent("this is content2");
        p.setName("123123123@126.com2");
        p.setTweeted_at(new Date(System.currentTimeMillis()));
        p.setType("this is type2");
        p.setUser_name("this is name2");

        //将对象转为json
        String jsonData = JSON.toJSONString(p);

        //获取操作文档的对象,因为要操作的对象只是一个string，所以source方法需要加一个参数，表示传入的内容是json
        IndexRequest request = new IndexRequest("ittest").source(jsonData,XContentType.JSON);
        //将数据请求到es
        client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 通过id修改文档，文档存在则更新，文档不存在则添加，操作方式和上面一样
     */
    @Test
    public void updateDoc() throws IOException {
        //构造一个简单的JavaObject对象
        Person p = new Person();
        p.setContent("this is content21");
        p.setName("123123123@126.com21");
        p.setTweeted_at(new Date(System.currentTimeMillis()));
        p.setType("this is type21");
        p.setUser_name("this is name21");

        //将对象转为json
        String jsonData = JSON.toJSONString(p);

        //获取操作文档的对象,因为要操作的对象只是一个string，所以source方法需要加一个参数，表示传入的内容是json
        IndexRequest request = new IndexRequest("ittest").id("B4mjloEBSykPw5ERHiUc").source(jsonData,XContentType.JSON);
        //将数据请求到es
        client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 通过id查询文档
     */
    @Test
    public void findById() throws IOException {
        GetRequest getRequest = new GetRequest("ittest","B4mjloEBSykPw5ERHiUc");
        GetResponse getResponse = client.get(getRequest,RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
    }

    /**
     * 通过id删除文档
     */
    @Test
    public void deleteById() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("ittest","B4mjloEBSykPw5ERHiUc");
        DeleteResponse delete = client.delete(deleteRequest,RequestOptions.DEFAULT);
    }
}
