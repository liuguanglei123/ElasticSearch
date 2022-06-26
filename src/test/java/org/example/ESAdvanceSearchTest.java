package org.example;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
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
import java.util.Map;

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

    /**
     * matchQuery查询
     * 1.match_all
     * 2.将查询结果封装为Person类对象
     * 3.分页
     */
    @Test
    public void matchQueryTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件,默认取并集
        MatchQueryBuilder query = QueryBuilders.matchQuery("content","this is content21");
        //强制取交集
        // query.operator(Operator.AND);

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

    /**
     * wildcard模糊查询
     * 1.wildcard
     * 2.将查询结果封装为Person类对象
     * 3.分页
     */
    @Test
    public void wildcardQueryTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件,默认取并集
        QueryBuilder query = QueryBuilders.wildcardQuery("content","content*");

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

    /**
     * regexp查询
     * 1.regexpQuery
     * 2.将查询结果封装为Person类对象
     * 3.分页
     */
    @Test
    public void regexpQueryTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件,默认取并集
        QueryBuilder query = QueryBuilders.regexpQuery("content","\\w+(.)*");

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

    /**
     * perfixQuery查询
     * 1.perfixQuery
     * 2.将查询结果封装为Person类对象
     * 3.分页
     */
    @Test
    public void perfixQueryTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件,默认取并集
        QueryBuilder query = QueryBuilders.prefixQuery("content","content");

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

    /**
     * 1.range查询
     * 2.排序
     */
    @Test
    public void rangeQueryTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件,默认取并集
        RangeQueryBuilder query = QueryBuilders.rangeQuery("tweeted_at");

        //指定下限
        query.gte(1656089968330L);
        //指定上限
        query.lte(1656089968337L);
        //将查询条件放入构建器
        sourceBuilder.query(query);

        //将构建器放入请求对象
        searcRequest.source(sourceBuilder);

        sourceBuilder.sort("price", SortOrder.ASC);

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
     * 1.queryString查询
     * 2.排序
     */
    @Test
    public void queryStringQueryTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件,默认取并集
        QueryStringQueryBuilder query = QueryBuilders.queryStringQuery("this content") //同事查询this和content两个条件，因为会分词
                .queryName("this")
                .field("name")  //从name字段中查找 xxxx
                .field("content")  //从content字段中查找 xxxx
                .defaultOperator(Operator.OR); // 从结果中取并集

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

    /**
     * bool查询
     * 1.查询name为xxx
     * 2.查询content包含yyy
     * 3.查询加个在2000-3000
     */
    @Test
    public void boolStringQueryTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件,默认取并集
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        //构建各个查询条件
        //1.name未xxx
        QueryBuilder termQuery = QueryBuilders.termQuery("name","xxx");
        //2.content包含yyy
        QueryBuilder matchQuery = QueryBuilders.matchQuery("content","yyy");
        //3.价格在某个范围内
        RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("price");
        rangeQuery.lte(3000);
        rangeQuery.gte(2000);

        query.must(termQuery);
        query.filter(matchQuery);
        query.filter(rangeQuery);

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

    /**
     * 聚合查询--桶聚合
     * 1.查询name为xxx
     * 2.对所有的content进行分组操作（类似distinct）
     */
    @Test
    public void aggsStringQueryTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件,默认取并集
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        //构建各个查询条件
        //1.name未xxx
        QueryBuilder termQuery = QueryBuilders.matchQuery("content","is");

        query.must(termQuery);

        //将查询条件放入构建器
        sourceBuilder.query(query);

        //2.进行聚合查询
        AggregationBuilder agg = AggregationBuilders.terms("contents")//自定义字段名
                .field("type") //将要分组的字段
                ;

        //将构建器放入请求对象
        searcRequest.source(sourceBuilder);
        sourceBuilder.aggregation(agg);

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

        //获取聚合结果
        Aggregations aggregations = searchResponse.getAggregations();
        Map<String, Aggregation> stringAggregationMap = aggregations.asMap();
        Terms contentsTerms = (Terms)stringAggregationMap.get("contents");//map的key就是上面自定义的名称
        List<? extends Terms.Bucket> buckets = contentsTerms.getBuckets();

        List contents = new ArrayList();
        for(Terms.Bucket each: buckets){
            Object key = each.getKey();
            contents.add(key);
        }

        System.out.println(JSON.toJSONString(contents));
    }

    /**
     * 高亮查询
     * 1.查询name为xxx
     * 2.对所有的content进行高亮操作
     */
    @Test
    public void highlightQueryTest() throws IOException {
        //构建查询请求对象，指定查询的索引名称
        SearchRequest searcRequest = new SearchRequest("ittest");

        //创建查询条件构建器SearchSourceBuilder
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //指定查询条件,默认取并集
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        //构建各个查询条件
        //1.name未xxx
        QueryBuilder termQuery = QueryBuilders.matchQuery("content","is");

        query.must(termQuery);

        //将查询条件放入构建器
        sourceBuilder.query(query);

        //2.进行高亮操作查询
        HighlightBuilder builder = new HighlightBuilder();
        //设置三要素
        builder.field("content");
        builder.preTags("xxx ");
        builder.postTags(" yyy");

        //将构建器放入请求对象
        searcRequest.source(sourceBuilder);
        sourceBuilder.highlighter(builder);

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
            //获取高亮结果，替换person中的content
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField highlightfield = highlightFields.get("content");
            Text[] fragments = highlightfield.fragments();

            person.setContent(fragments[0].toString());

            personList.add(person);
        }

        System.out.println(JSON.toJSONString(personList));

    }
}
