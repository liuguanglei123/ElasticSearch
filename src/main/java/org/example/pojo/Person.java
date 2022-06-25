package org.example.pojo;

import lombok.Data;

import java.util.Date;

/**
 * @Author: Yeman
 * @CreatedDate: 2022-06-25-0:30
 * @Description:
 */
@Data
public class Person {
    private String content;

    private String name;

    private String type;

    private String user_name;

    private Date tweeted_at;

    @Override
    public String toString(){
        return "content" + content +
                "name" + name +
                "type" + type +
                "user_name" + user_name +
                "tweeted_at" + tweeted_at.toLocaleString();
    }

}
