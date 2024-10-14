package com.keshaparrot.gptorganizier.domain;



import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.Objects;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(tableName = "records")
public class Record {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String header;
    private String content;
    private String description;
    private Date createDate;
    private Date updateTime;
    private TypeOfRecord type;
    private boolean marked;

    @Ignore
    public Record(Long id, String header, String content, String description, Date createDate, Date updateTime, String type, boolean marked) {
        this.id = id;
        this.header = header;
        this.content = content;
        this.description = description;
        this.createDate = createDate;
        this.updateTime = updateTime;
        this.type = convertType(type);
        this.marked = marked;
    }
    @Ignore
    public Record(Long id, String header, String content, String description, Date createDate, Date updateTime, TypeOfRecord type) {
        this.id = id;
        this.header = header;
        this.content = content;
        this.description = description;
        this.createDate = createDate;
        this.updateTime = updateTime;
        this.type = type;
    }

    /**
     * Converts a string representation of the type of record provided by the user
     * into the corresponding enum type of record.
     *
     * @param type the string representation of the type (should be either "PROMPT" or "LINK")
     * @return the corresponding enum type of record; returns {@link TypeOfRecord#LINK}
     *         if the input does not match "PROMPT"
     */
    public TypeOfRecord convertType(String type){
        if(type.equalsIgnoreCase(TypeOfRecord.PROMPT.toString())){
            return TypeOfRecord.PROMPT;
        }else {
            return TypeOfRecord.LINK;
        }
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return Objects.equals(id, record.id) && Objects.equals(header, record.header) && Objects.equals(content, record.content) && Objects.equals(description, record.description) && Objects.equals(createDate, record.createDate) && Objects.equals(updateTime, record.updateTime) && type == record.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, header, content, description, createDate, updateTime, type);
    }
}
