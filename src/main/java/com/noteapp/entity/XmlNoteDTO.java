package com.noteapp.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.noteapp.dto.note.NoteDTO;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@XmlRootElement(name = "NoteDTO")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlNoteDTO {
    @JsonProperty("Title")
    @XmlElement(name = "Title")
    private String title;
    @JsonProperty("Content")
    @XmlElement(name = "Content")
    private String content;

    public XmlNoteDTO(String title, String content) {
        this.title = title;
        this.content = content;
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

    public XmlNoteDTO() {}

    public static XmlNoteDTO from(NoteDTO noteDTO) {
        return XmlNoteDTO.builder()
                .title(noteDTO.getTitle())
                .content(noteDTO.getContent())
                .build();
    }
}
