# 初识ElasticSearch

## 倒排索引

举一个例子：
1.请说出包含 光 的诗句？
2.请背诵静夜思

静夜思一诗中包含  光，但是为何在提到 光 的诗句时，没有立刻联想到静夜思？
在这个场景中，我们的大脑存储的是正向索引，诗的标题相当于一个key，诗的内容相当于value，只有给出了key，才能查找（联想）到value

### 倒排索引是什么？
首先将 床前明月光 这一句根据特定的规则，拆分为不同的词条：term 这个概念非常重要
先不考虑规则的细节，我们假定上面这句诗句拆分为： 床 前 床前 明 月 明月 光 月光
这样我们就能得到这样一张"表"

| Key(term)      | Value |
| --------- | -----:|
| 床  | 床前明月光 |
| 前     |   床前明月光 |
| 明月      |   床前明月光，明月几时有 |

微调一下 倒排索引的规则，在value存储诗的唯一标题：

| Key(term)      | Value |
| --------- | -----:|
| 床  | 《静夜思》 |
| 前     |   《静夜思》 |
| 明月      |   《静夜思》，《水调歌头》 |

### 定义
将文档中的内容进行分词，形成词条，然后记录词条和数据的唯一标识的对应关系

## Elasticsearch
### Elasticsearch特点
* Elasticsearch是一个基于Lucene的搜索服务器

> Lucene是一个开源的全文检索工具包，但并不是一个完整的检索引擎，更接近于一个全文检索引擎的框架，Lucene提供了一个简单却强大的应用程序接口（API），能够做全文索引和搜寻

* Elasticsearch是一个分布式，高扩展，高实时的搜索与数据分析引擎
* 基于RestFul Web接口
* Elasticsearch是用JAVA语言开发的开源搜索项目

### 应用场景
* 搜索：海量数据的查询
* 日志数据的分析
* 实时数据分析

### ES与Mysql可以互相代替吗？
* Mysql有事务性，ES没有
* ES没有物理外键的特性，如果对数据的强一致性要求高的场景不适用
* 一般使用场景下，Mysql负责存储数据，ES负责搜索数据


# ElasticSearch安装
中间件服务都可以通过docker进行安装，快且容易备份
## docker安装
centos7.9版本安装docker

#### 使用官方安装脚本自动安装

> curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun

安装后需要设置docker源，这样可以加快下载速度

1.aliyun提供镜像加速服务，只需要访问https://cr.console.aliyun.com/cn-huhehaote/instances/mirrors

2.找到镜像加速服务，复制加速器地址，格式为 https://xpo0iqxr.mirror.aliyuncs.com
然后按照页面中的提示进行配置

3.配置镜像加速器
可以通过修改daemon配置文件/etc/docker/daemon.json来使用加速器

> sudo mkdir -p /etc/docker
sudo tee /etc/docker/daemon.json <<-'EOF'
{
&emsp;"registry-mirrors": ["https://xpo0iqxr.mirror.aliyuncs.com"]
}
EOF
sudo systemctl daemon-reload
sudo systemctl restart docker

## ElasticSearch安装
#### 1.拉取镜像
> docker pull elasticsearch:7.7.0

#### 2.运行
> docker run -d --name es -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" b0e9f9f047e6

-d：后台启动
--name：容器名称
-p：端口映射
-e：设置环境变量 discovery.type=single-node：单机运行
b0e9f9f047e6：镜像id
如果启动不了，可以加大内存设置：-e ES_JAVA_OPTS="-Xms512m -Xmx512m"

#### 3.验证
浏览器输入地址 http://xxx.xxx.xxx.xxx:9200/
出现如下内容，即表示启动成功
```json
{
  "name" : "c21912ad63f7",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "mT4KzuGGTK2G1Zy8ANLbMQ",
  "version" : {
    "number" : "7.4.0",
    "build_flavor" : "default",
    "build_type" : "docker",
    "build_hash" : "22e1767283e61a198cb4db791ea66e3f11ab9910",
    "build_date" : "2019-09-27T08:36:48.569419Z",
    "build_snapshot" : false,
    "lucene_version" : "8.2.0",
    "minimum_wire_compatibility_version" : "6.8.0",
    "minimum_index_compatibility_version" : "6.0.0-beta1"
  },
  "tagline" : "You Know, for Search"
}
```

## ik分词器安装
IK版本一定要和es版本一致 比如：7.4.0
在线安装
> 进入容器
docker exec -it es /bin/bash <br>
在线下载安装
./bin/elasticsearch-plugin install https://github.com/medcl/elasticsearch-analysis-ik/releases/download/v7.7.0/elasticsearch-analysis-ik-7.7.0.zip <br>
重启es
docekr restart es

测试一下分词器
使用postman工具，请求如下地址：
```
curl --location --request POST 'http://192.168.170.128:9200/_analyze' \
--header 'Content-Type: application/json' \
--data-raw '{
    "analyzer":"ik_max_word",
    "text":"中华人民共和国合同法"
}'
```
出现合理的分词内容即表示成功

## 安装kibana
kibana是访问es的工具，既有可视化的各种图标，也可以执行es的各种命令，其版本也要和es完全对应
> 拉取镜像
docker pull kibana:7.4.0 <br>
启动kibana镜像
docker run -d --name=kibana --restart=always -p 5601:5601 kibana:7.4.0 <br>
将镜像中的/usr/share/kibana/config/kibana.yml复制到宿主机的/usr/local/kibana/，其中xxxxxxx是kibana镜像的id，用docker ps -a可以查看
docker cp xxxxxxxx:/usr/share/kibana/config/kibana.yml /usr/local/kibana/ <br>
修改文件，将kibana.yml文件中的es地址填写正确，再把文件复制回去
docker cp /usr/local/kibana/kibana.yml xxxxxxx:/usr/share/kibana/config <br>
退出容器，重启kibana
docker restart kibana

验证安装
访问如下地址 http://xxx.xxx.xxx.xxx:5601


# ElasticSearch浅析
## ElasticSearch核心概念
ES中有几个基本概念：索引(index)、类型(type)、文档(document)、映射(mapping)等。我们将这几个概念与传统的关系型数据库中的库、表、行、列等概念进行对比，如下表：

| RDBS                  | ES |
| ----------------------| -----:|
| 数据库(database)      | 索引（index） |
| 表(table)             |   类型（type）（ES6.0之后被废弃，es7中完全删除） |
| 表结构（schema）       |   映射（mapping）        |
| 行（row）             |   文档（document）       |
| 列（column）	         |   字段（field）          |
| 索引                  | 反向索引                 |
|SQL                   |  查询DSL                 |
|SELECT * FROM table    |GET http://.....         |
|UPDATE table SET       |PUT  http://......       |
|DELETE                 |DELETE  http://......    |

* 索引（index）
> es中存储数据的地方，可以理解成关系型数据库中的数据库概念

* 映射（mapping）
> mapping定义了每个字段的类型，字段所使用的的分词器等，相当于关系型数据库中的表结构

* 文档（document）
> es中的最小数据单元，常以json格式显示，一个document相当于关系型数据库中的一行数据

* 倒排索引
> 一个倒排索引由文档中所有不重复次的列表构成，对于其中每个词，对应一个包含它的文档id列表

* 类型（type）
> 一种typ就相当于一类表，在es7.x中默认type为_doc，由于已经不再使用，所以不再关注，相当于一个关系型数据库中只有一张表
 - es5中一个index可以有多种type
 - es6中一个index只能有一种type
 - es7以后将逐渐废弃这个概念

## 操作ES
### Restful风格介绍
REST：一种定义接口的规范
- 基于HTTP协议
- 可以使用xml格式定义或json格式定义
- 每一个uri代表一种资源
- 客户端使用GET POST PUT DELETE 四种动词表示不同的操作方式
 - GET 获取资源
 - POST 用来新建资源（也可以用来更新）
 - PUT 用来更新资源
 - DELETE 用来删除资源
 
### 操作索引

添加索引
> PUT /索引名称

查询索引信息
> GET /索引名称

删除索引
> DELETE /索引名称

关闭索引
> POST /索引名称/_close

打开索引
> POST /索引名称/_open

### 操作映射
ES支持的数据类型：
1. 简单数据类型

* 字符串
    * text：会分词，不支持聚合
    * keyword： 不会分词，将全部内容作为一个词条，支持聚合
* 数值
    * long 带符号的64位整数
	* integer 带符号的32位整数
	* short 带符号的16位整数
	* byte 带符号的8为整数
	* double 双精度64为IEEE 754浮点数
	* float 单精度32为IEEE 754浮点数
	* half_float 半精度16为IEEE 754浮点数
	* scaled_float 由a支持的有限浮点数long，由固定double比例因子缩放
* 布尔
	* boolean
* 二进制
	* binary
* 范围类型
	* integer_range float_range long_range double_range date_range
* 日期
	* date

2.复杂数据类型
* 数组 array []
* 对象 object {}

#### 如何操作？

查询映射

> GET 索引名称/_mapping


添加映射

> PUT 索引名称/_mapping<br />
{<br />
&emsp;"properties":{&emsp;//properties固定<br />
&emsp;&emsp;"name":{<br />
&emsp;&emsp;&emsp;"type":"keyword"<br />
&emsp;&emsp;},<br />
&emsp;&emsp;"address":{<br />
&emsp;&emsp;&emsp;"type":"text"<br />
&emsp;&emsp;&emsp;"analyzer":"ik_max_word"<br /> // 这里指定了分词器，如果不指定，默认使用standard分词器，对中文不友好
&emsp;&emsp;},<br />
&emsp;&emsp;"age":{<br />
&emsp;&emsp;&emsp;"type":"integer"<br />
&emsp;&emsp;},<br />
&emsp;}<br />
}

创建索引并添加映射
> PUT 索引名称<br />
{<br />
&emsp;"mappings":{<br />
&emsp;&emsp;"properties":{&emsp;//properties固定<br />
&emsp;&emsp;&emsp;&emsp;"name":{<br />
&emsp;&emsp;&emsp;&emsp;&emsp;"type":"keyword"<br />
&emsp;&emsp;&emsp;&emsp;},<br />
&emsp;&emsp;&emsp;&emsp;"age":{<br />
&emsp;&emsp;&emsp;&emsp;&emsp;"type":"integer"<br />
&emsp;&emsp;&emsp;&emsp;},<br />
&emsp;&emsp;&emsp;}<br />
&emsp;&emsp;}<br />
&emsp;}<br />
}

修改映射

* 添加字段：

> PUT 索引名称/_mapping<br />
{<br />
&emsp;"properties":{&emsp;//properties固定<br />
&emsp;&emsp;"address":{<br />
&emsp;&emsp;&emsp;"type":"text"<br />
&emsp;&emsp;},<br />
&emsp;}<br />
}

### 操作文档

添加文档，指定id

> PUT 索引名称/_doc/文档id&emsp;&emsp;//POST方法也可以
{
&emsp;"name":"zhangsan",
&emsp;"age":20,
&emsp;"address":"浙江杭州"
}

添加文档，不指定id

> POST 索引名称/_doc<br />
{<br />
&emsp;"name":"lisi",<br />
&emsp;"age":20,<br />
&emsp;"address":"浙江宁波"<br />
}

修改文档

> PUT 索引名称/_doc/文档id<br />
{<br />
&emsp;"name":"zhangsan",<br />
&emsp;"age":22,<br />
&emsp;"address":"江苏南京"<br />
}

根据id查询文档

> GET 索引名称/_doc/文档id

查询所有文档

> GET 索引名称/_search

根据id删除文档
> DELETE 索引名称/_doc/文档id

## 分词器
* 分词器（Analyzer）：将一段文档按照一定规则分析成多个词语的一种工具
* ES内置多种分词器，包括 standartAnalyzer SimpleAnalyzer 等等，内置的分词器对中文不友好，基本是一个字一个词

测试一下分词器：

> GET _analyze<br />
{<br />
&emsp;"analyzer":"standard",<br />
&emsp;"text":"这是分词器的测试"<br />
}

### IK分词器
* IKAnalyzer是一个开源的，基于java语言开发的轻量级的中文分词工具包
* 是一个基于Maven构建的项目
* 具有60万字/秒的高速处理能哪里
* 支持用户词典扩展定义

IK分词器的两种模式：
* ik_smart 粗粒度分词
> GET _analyze<br />
{<br />
&emsp;"analyzer":"ik_smart",<br />
&emsp;"text":"这是分词器的测试"<br />
}

* ik_max_word 细粒度分词
> GET _analyze<br />
{<br />
&emsp;"analyzer":"ik_max_word",<br />
&emsp;"text":"这是分词器的测试"<br />
}

### 查询文档的高级用法

* 词条查询：term
	* 词条查询不会分析查询条件，只有当词条和查询字符串完全匹配时才匹配搜索
* 全文查询：match
	* 全文查询会分析查询条件，先将查询条件进行分词，然后查询，并集

最基本的词条查询写法：
term查询：

> GET 索引名称/_search<br />
{<br />
&emsp;"query":{<br />
&emsp;&emsp;"term":{<br />
&emsp;&emsp;&emsp;"address":{<br />
&emsp;&emsp;&emsp;&emsp;"value":"手机"<br />
&emsp;&emsp;&emsp;}<br />
&emsp;&emsp;}<br />
&emsp;}<br />
}

match查询：

> GET 索引名称/_search<br />
{<br />
&emsp;"query":{<br />
&emsp;&emsp;"match":{<br />
&emsp;&emsp;&emsp;"address":"浙江杭州"<br />
&emsp;&emsp;}<br />
&emsp;}<br />
}

# SrpingBoot整合ES
实现步骤：
* 搭建SpringBoot工程
* 引入ES相关依赖
```     
<dependency>
	<groupId>org.elasticsearch.client</groupId>
	<artifactId>elasticsearch-rest-client</artifactId>
	<version>7.4.0</version>
</dependency>
<dependency>
	<groupId>org.elasticsearch.client</groupId>
	<artifactId>elasticsearch-rest-client</artifactId>
	<version>7.4.0</version>
</dependency>
<dependency>
	<groupId>org.elasticsearch</groupId>
	<artifactId>elasticsearch</artifactId>
	<version>7.4.0</version>
</dependency>
```

## 单请求操作
所有的单个请求的API操作在ESApplicationSingleTest测试类中

## 批量操作
所有的批量请求的API操作在ESApplicationBulkTest测试类中

# ElasticSearch高级搜索

所有高级搜索的操作在ESAdvanceSearchTest测试类中

## 查询所有
size为分页的大小
from为查询的起始位置
```
GET .kibana_1/_search
{
    "query":
        "match_all": {}
    },
    "size": 20,
    "from": 0
}
```
结果返回示例：
```
{
  "took" : 0,           //耗时
  "timed_out" : false,  //是否超时
  "_shards" : {         //分片信息
    "total" : 1,
    "successful" : 1,
    "skipped" : 0,
    "failed" : 0
  },
  "hits" : {            //命中数据
    "total" : {
      "value" : 65,        //命中数据总数
      "relation" : "eq"    //查询方式，这里表示等值查询
    },
    "max_score" : 1.0,     //得分/命中匹配度，得分越高越排在前面
    "hits" : [
      {
        "_index" : ".kibana_1",
        "_type" : "_doc",
        "_id" : "sample-data-telemetry:flights",
        "_score" : 1.0,
        "_source" : {
          "sample-data-telemetry" : {
            "installCount" : 1
          },
          "type" : "sample-data-telemetry",
          "updated_at" : "2022-06-24T04:11:13.605Z"
        }
      },
    ]
  }
}
```

## term查询
!!! term查询不会对查询条件进行分词
```
GET ittest/_search
{
    "query":{
        "term": {
            "字段名称":"查询条件"
        }
    }
}

例如：
GET ittest/_search
{
    "query":{
        "term": {
            "content":{
                "value":"this is content2"
            }
        }
    }
}
```

## match查询
!!! match查询会对查询条件进行分词后再查询
* match查询会对查询条件进行分词
* match查询会将分词后的查询条件和词条进行等值匹配
* 默认会取所有分词查询结果后的并集（OR）
```
GET ittest/_search
{
    "query":{
        "match": {
            "字段名称":"查询条件"
        }
    }
}

当我们不想让取查询结果的交集时怎么办？

GET ittest/_search
{
    "query":{
        "match": {
            "字段名称":{
                "query":"查询条件",
                "operator":"执行操作（or还是and）"
            }
        }
    }
}

示例：
GET ittest/_search
{
    "query":{
        "match": {
            "content":"is content21"
            
        }
    }
}

GET ittest/_search
{
    "query":{
        "match": {
            "content":{
              "query":"is content21",
              "operator": "and"
            }
        }
    }
}
```

## 模糊查询
match查询会将查询条件进行分词后进行等值匹配，但是如果期望模糊匹配如何操作？
* wildcard查询：会对查询条件进行分词，还可以使用通配符？（任意单个字符）和*（0到多个字符）
> wildcard查询不要使用* key *这种查询，否则会导致倒排索引失效进行全索引匹配，影响性能
* regexp查询：正则匹配
* prefix查询：前缀查询
> 前缀查询最好查询keyword类型的字符串，查询text类型的字符串时，会查询到并不是该词前缀的结果
```
wildcard查询:
GET ittest/_search
{
    "query":{
        "wildcard": {
            "content":{
              "value":"content*" //模糊查询content开头的分词
            }
        }
    }
}

GET ittest/_search
{
    "query":{
        "wildcard": {
            "content":{
              "value":"content？" //模糊查询content开头的分词，且content后面有一个字符
            }
        }
    }
}

//正则查询
GET ittest/_search
{
    "query":{
        "regexp": {
            "content":"\\w(+)(.)*"
        }
    }
}

//前缀查询
GET ittest/_search
{
    "query":{
        "prefix": {
            "content":{
                "value":"xxxx"
            }
        }
    }
}
```

## 范围&&排序查询

```
GET ittest/_search
{
  "query":{
    "range": {
      "字段名称": {
        "gte": 100,
        "lte": 200
      }
    }
  },
  "sort":[{
    "字段名称":{
        "order":"asc或者desc"
        }   
    }]
}

GET ittest/_search
{
  "query":{
    "range": {
      "price": {
        "gte": 100,
        "lte": 200
      }
    }
  },
  "sort":[{
    "price":{
        "order":"asc"
        }   
    }]
}
```

## queryString查询
queryString查询具有如下特点：
* 会对查询条件进行分词
* 然后将粉刺后的查询条件和索引词条进行等值匹配
* 默认取并集
* 可以同时指定多个查询字段（比如输入一个关键字，想同时查询邮箱和姓名匹配的数据）

```
GET 索引名称/_search
{
    "query":{
        "query_string":{
            "fields":["查询字段1","查询字段2"],
            "query":"查询条件1 or 查询条件2"
        }
    }
}

GET ittest/_search
{
    "query":{
        "query_string":{
            "fields":["name","content"],
            "query":"content21" //从name和content字段中匹配content21
        }
    }
}

GET ittest/_search
{
    "query":{
        "query_string":{
            "fields":["name","content"],
            "query":"this OR content21" //从name和content字段中匹配this或者content21
        }
    }
}

```

## 布尔查询
boolQuery：对多个查询条件进行连接，连接方式是：
* must（and）：条件必须成立
* must_not(not)：条件必须不成立
* should（or）：条件可以成立
* filter：条件必须成立，性能比must搞，不会计算匹配度（score）

```
GET 索引名称/_search
{
    "query":{
        "bool":{
            "must":[{查询条件}],
            "must_not":[{查询条件}],
            "should":[{查询条件}],
            "filter":[{查询条件}],
        }
    }
}
示例：
GET ittest/_search
GET ittest/_search
{
    "query":{
      "bool":{
        "must":[
          {
            "term":{
              "name":{
                "value": "name"
              }
            }
          },
          {
            "match":{
              "name": "is"
            }
          }
        ],
        "filter":[
          {
            "term":{
              "name":{
                "value": "name2"
              }
            }
          }
        ]
      }
    }
}
```

## 聚合查询
* 指标聚合：相当于mysql中的聚合函数，比如max，min，avg，sum等
* 桶聚合：相当于mysql中的group by操作，但不能对text类型的数据进行聚合，会失败，因为text会分词

```
指标聚合：
GET ittest/_search
{
    "query":{
        "match":{
            "name":"xxx"
        }
    },
    "aggs":{
        "max_time":{ //这个字段名字是自己取的，用于后续结果返回的名字，相当于mysql中的as后面的别名
            "max":{
                "field":"tweeted_at"
            }
        }
    }             
}
结果返回：
  "aggregations" : {
    "max_price" : { //这个字段名称就是上面备注的自定义的字段名
      "value" : 1.656089494174E12,
      "value_as_string" : "2022-06-24T16:51:34.174Z"
    }
  }

桶聚合：
GET ittest/_search
{
    "query":{
        "match":{
            "name":"xxx"
        }
    },
    "aggs":{
        "names":{ //这个字段名字是自己取的，用于后续结果返回的名字，相当于mysql中的as后面的别名
            "terms":{
                "field":"name"
            }
        }
    }             
}
结果返回样式：
  "aggregations" : {
    "names" : {
      "doc_count_error_upper_bound" : 0,
      "sum_other_doc_count" : 0,
      "buckets" : [
        {
          "key" : "this is type",
          "doc_count" : 1
        },
        {
          "key" : "this is type21",
          "doc_count" : 1
        }
      ]
    }
  }
```

## 高亮查询
高亮三要素：
* 高亮字段
* 前缀
*后缀

```
GET ittest/_search
{
  "query":{
    "match":{
      "content":"content"
    }
  },
  "highlight": {
    "fields":{
      "content": {
        "pre_tags": "xxx ",
        "post_tags": " yyy"
      }
    }
  }
}
结果返回：
        "highlight" : {
          "content" : [
            "this is xxx content yyy"
          ]
        }
```

## 重建索引
随着业务的变更，索引的结构可能会发生改变
ES的索引一旦创建，只允许添加字段，不允许改变字段，因为改编自断，需要重建倒排索引，影响内部缓存结构
遇到这样的场景，需要重建一个新的索引，并将原有的索引的数据导入到新索引中
一般的操作步骤是：
1.创建一个新的索引
2.将原来的索引的数据导入到新索引
```
POST /_reindex
{
    "source": {
        "index": "index_old"
    },
    "dest": {
        "index": "index_new"
    }
}
```
3.删除原来的索引
4.将新的索引增加一个别名
```
POST index_new/_alias/index_old
```

# ElasticSearch机群管理
## 集群介绍
集群和分布式：
* 集群：多个人做一样的事
* 分布式：多个人做不一样的事

原有的单点结构：
* 只有一个机器，部署了单一的es服务
集群架构：
* 提供多个机器，访问哪台机器都可以，多个机器存储相同的数据
分布式集群架构：
* 多个节点存储不同的数据，共同组成一个完整的服务，并且同时存在多组节点存储相同的数据，通过路由的方式获取正确的机器

ES集群分布式架构相关概念
* 集群（cluster）：一组拥有共同的cluster name的节点
* 节点（node）：集群中的一个Es实例
* 索引（index）：es存储数据的地方，相当于关系数据库中的database概念
* 分片（shard）：索引可以拆分为不同的部分进行存储，速成分片，在集群环境下，一个索引的不同分片可以拆分到不同的节点中
    * 主分片（primary shard）：相对于副本分片的定义
    * 副本分片（replica shard）：每个主分片可以有一个或者多个副本，数据和主分片一样

![](https://github.com/liuguanglei123/ElasticSearch/blob/main/images/es%E9%9B%86%E7%BE%A4.png)

## Es集群管理
集群原理-分片配置
* 在创建索引时，如果不指定分片配置，默认主分片1，副本分片1
* 在创建索引时，可以通过settings设置分片

```
"settings":{
    "number_of_shards":3, // 主分片
    "number_of_replicas":1 // 副本分片
}
//通过上面的配置，最终创建出来6个分片，每三个是一组，一组主分片，一组备分片

已创建的分片数量，可以通过以下方式查看
GET 索引名称
{
  "xxxx_order": {
    "aliases": {xxxx_order2},
    "mappings": {
      "doc": {
        "properties": {
    },
    "settings": {
      "index": {
        "number_of_shards": "5", //主分片数量
        "provided_name": "xxxx_order",
        "max_result_window": "20000",
        "creation_date": "1654593740960",
        "number_of_replicas": "1", //每个主分片有一个副本分片
        "uuid": "xxxxxxxxxxx",
        "version": {   /
          "created": "6080299"
        }
      }
    }
  }
}
```
* 分片与自平衡：当某一个节点挂掉后，挂掉的节点分片会自平衡到其他节点中
* 在ES中，每个查询在每个分片的单个线程中执行，但是可以并行处理多个分片
* 分片数量一旦确定好，不能修改

## ES路由原理
* 文档存入对应的分片，ES计算分片编号的过程，成为路由
* ES是怎么知道一个文档应该存到哪个分片中呢？
* 查询时，根据文档id查询文档，ES又该去哪个分片中查询数据呢？
    * 当主分片挂掉之后，利用取模算法的方式可能会失效，但是es可以通过主节点获取原节点的备份节点
* 路由算法：shard_index = hash(id) % number_of_primary_shards

## ElasticSearch脑裂
脑裂：
* 一个正常个的ES集群中只能有一个主节点（Master），主节点负责管理整个集群，如创建或者删除索引，跟踪哪些节点是机群的一部分，并决定哪些分片分配给相关的节点。
* 机群的所有节点都会选择同一个节点作为主节点
* 脑裂问题的出现就是以内从节点在选择主节点上出现分歧导致一个机群出现多个主节点，从而出现集群分裂，使得集群处于异常状态。

假设一个es集群中存在8个节点，其中node-1是主节点，node-2 -- node-8是从节点，其中node-4 到node-8节点因为某些原因导致误认为node-1不再是主节点，选择了node-3作为主节点，产生集群的分裂。

什么情况下会出现脑裂现象？

1.网络原因：网络延迟
* 一般es集群会在内网部署，也可能跟在外网部署
* 内网出现网络问题的概率较小，外网出现网络问题的可能性较大

2.节点负载
* 主节点的角色既为maste又作为数据节点保存数据，当数据访问量较大时，可能会导致msster节点停止响应
    
    * es启动文件中有两个配置项：
   
    > node.master：是否有资格成为主节点<br>
    node.data：是否存储数据                                                                           

3.JVM内存回收
* 当Master节点设置的jvm内存较小时，引发jvm的大规模内存回收，造成ES进程失去响应。

如何避免脑裂？

1.网络原因:discovery.zen.ping.timeout超时时间配置大一点，默认是3s

2.节点负载：角色分离策略

* 候选主节点配置为只作为主节点不存储数据
    * node.master:true
    * node.data:false
* 数据节点配置为只存储数据不作为主节点
    * node.master:false
    * node.data:true

3.设置合理的内存：修改config/jvm.options文件中的-Xms和-Xmx为服务器的一般
