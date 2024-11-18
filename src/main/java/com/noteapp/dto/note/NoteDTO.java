package com.noteapp.dto.note;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.noteapp.entity.Note;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NoteDTO {
    private Long noteId;
    private Long userId;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Content")
    private String content;
    private LocalDateTime creationDate;
    private LocalDateTime modifiedDate;

    public NoteDTO(Long noteId, Long userId, String title, String content, LocalDateTime creationDate, LocalDateTime modifiedDate) {
        this.noteId = noteId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.creationDate = creationDate;
        this.modifiedDate = modifiedDate;
    }

    public NoteDTO() {}

    /**
     * Maps a Note entity to a NoteDTO.
     *
     * @param note The Note entity to map from.
     * @return The NoteDTO representing the mapped note.
     */
    public static NoteDTO from(Note note) {
        return NoteDTO.builder()
                .userId(note.getUser().getId())
                .noteId(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .creationDate(note.getCreationDate())
                .modifiedDate(note.getModifiedDate())
                .build();
    }
}