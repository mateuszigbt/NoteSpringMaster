package com.noteapp.dto.note;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class CreateNoteDTO {
    @NonNull
    private String title;
    @Size(max = 1000)
    private String content;
}
