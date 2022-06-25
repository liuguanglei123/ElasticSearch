package org.example;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryShardContext;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.pojo.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-06-25-17:21
 * @Description:
 */
@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class ESAdvanceSearchTest {

    @Autowired
    @Qualifier("MyEsClient")
    RestHighLevelClient client;

    /**
     * 查询所有
     * 1.match_all
     * 2.将查询结果封装为Person类对象
     * 3.分页
     */
    @Test
    public void matchAllTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件
        QueryBuilder query = QueryBuilders.matchAllQuery();

        //将查询条件放入构建器
        sourceBuilder.query(query);

        //添加分页信息
        sourceBuilder.from(0);
        sourceBuilder.size(100);

        //将构建器放入请求对象
        searcRequest.source(sourceBuilder);

        //请求查询，获取结果
        SearchResponse searchResponse = client.search(searcRequest, RequestOptions.DEFAULT);

        //获取命中对象SearchHits
        SearchHits searchHits = searchResponse.getHits();

        //获取命中数量
        System.out.println(searchHits.getTotalHits());

        List<Person> personList = new ArrayList<>();
        //获取命中对象中的hits
        SearchHit[] hits = searchHits.getHits();

        for(SearchHit hit : hits){
            String sourceAsString = hit.getSourceAsString();
            Person person = JSON.parseObject(sourceAsString,Person.class);
            personList.add(person);
        }

        System.out.println(JSON.toJSONString(personList));
    }


    /**
     * termQuery查询
     * 1.match_all
     * 2.将查询结果封装为Person类对象
     * 3.分页
     */
    @Test
    public void termQueryTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件
        QueryBuilder query = QueryBuilders.termQuery("content","content21");

        //将查询条件放入构建器
        sourceBuilder.query(query);

        //将构建器放入请求对象
        searcRequest.source(sourceBuilder);

        //请求查询，获取结果
        SearchResponse searchResponse = client.search(searcRequest, RequestOptions.DEFAULT);

        //获取命中对象SearchHits
        SearchHits searchHits = searchResponse.getHits();

        //获取命中数量
        System.out.println(searchHits.getTotalHits());

        List<Person> personList = new ArrayList<>();
        //获取命中对象中的hits
        SearchHit[] hits = searchHits.getHits();

        for(SearchHit hit : hits){
            String sourceAsString = hit.getSourceAsString();
            Person person = JSON.parseObject(sourceAsString,Person.class);
            personList.add(person);
        }

        System.out.println(JSON.toJSONString(personList));
    }
}
