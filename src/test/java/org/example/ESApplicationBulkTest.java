package org.example;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.Index;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-06-25-13:02
 * @Description:
 */
@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class ESApplicationBulkTest {

    @Autowired
    @Qualifier("MyEsClient")
    RestHighLevelClient client;

    @Test
    public void testBulk() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();

        DeleteRequest deleteRequest = new DeleteRequest("ittest","idxxxxx");
        bulkRequest.add(deleteRequest);

        Map<String,String> map = new HashMap<>();
        map.put("content","this is content");
        IndexRequest indexRequest = new IndexRequest("ittest").id("idxxxxx").source(map);
        bulkRequest.add(indexRequest);

        //下面这行代码有点小问题，doc需要传递偶数个参数，这里只做演示，不再执行
        map.put("content","this is content2");
        UpdateRequest updateRequest = new UpdateRequest("ittest","idxxxxx").doc(map);
        bulkRequest.add(updateRequest);

        client.bulk(bulkRequest, RequestOptions.DEFAULT);
    }


}
