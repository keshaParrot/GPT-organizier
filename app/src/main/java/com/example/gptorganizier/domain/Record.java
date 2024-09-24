package com.example.gptorganizier.domain;



import java.util.Date;

public class Record {

    private Long id;
    private String header;
    private String content;
    private String description;
    private Date createDate;
    private Date updateTime;
    private TypeOfRecord type;

    public Record(Long id, String header, String content, String description, Date createDate, Date updateTime, TypeOfRecord type) {
        this.id = id;
        this.header = header;
        this.content = content;
        this.description = description;
        this.createDate = createDate;
        this.updateTime = updateTime;
        this.type = type;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Date getCreateDate() {
        return createDate;
    }
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public TypeOfRecord getType() {
        return type;
    }
    public void setType(TypeOfRecord type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", content='" + content + '\'' +
                ", description='" + description + '\'' +
                ", createDate=" + createDate +
                ", updateTime=" + updateTime +
                ", type=" + type +
                '}';
    }
}
