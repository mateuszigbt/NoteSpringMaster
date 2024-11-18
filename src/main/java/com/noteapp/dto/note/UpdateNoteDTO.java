package com.noteapp.dto.note;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateNoteDTO {
    private String title;
    private String content;
}
