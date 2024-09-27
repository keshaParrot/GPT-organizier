package com.example.gptorganizier.domain;



import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Record {

    private Long id;
    private String header;
    private String content;
    private String description;
    private Date createDate;
    private Date updateTime;
    private TypeOfRecord type;

    public Record(Long id, String header, String content, String description, Date createDate, Date updateTime, String type) {
        this.id = id;
        this.header = header;
        this.content = content;
        this.description = description;
        this.createDate = createDate;
        this.updateTime = updateTime;
        this.type = convertType(type);
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


}
