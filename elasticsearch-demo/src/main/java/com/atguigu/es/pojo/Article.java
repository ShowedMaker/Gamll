package com.atguigu.es.pojo;

import java.io.Serializable;

/**
 * @ClassName User
 * @Description
 * @Author yzchao
 * @Date 2022/11/1 20:37
 * @Version V1.0
 */
public class Article implements Serializable {

    private long id;
    private String title;
    private String content;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
