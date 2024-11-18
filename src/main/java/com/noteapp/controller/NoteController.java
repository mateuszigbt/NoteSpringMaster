package com.noteapp.controller;

import com.noteapp.dto.note.CreateNoteDTO;
import com.noteapp.dto.note.FileDTO;
import com.noteapp.dto.note.NoteDTO;
import com.noteapp.dto.note.UpdateNoteDTO;
import com.noteapp.entity.Note;
import com.noteapp.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller class handling CRUD operations for notes.
 * Endpoints for retrieving, creating, updating, and deleting notes are provided.
 */
@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    /**
     * Constructor for NoteController, injecting an instance of NoteService.
     * @param noteService The service responsible for note-related operations.
     */
    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Endpoint for retrieving all notes belonging to the current user.
     * @return ResponseEntity containing a list of NoteDTO objects representing user notes.
     */
    @GetMapping("")
    public ResponseEntity<List<NoteDTO>> getUserNotes() {
        List<NoteDTO> notes = noteService.getUserNotes();
        return ResponseEntity.ok(notes);
    }

    /**
     * Endpoint for creating a new note.
     * @param note CreateNoteDTO object containing note details.
     * @return ResponseEntity containing the created NoteDTO object.
     */
    @PostMapping("")
    public ResponseEntity<NoteDTO> createNote(@Valid @RequestBody CreateNoteDTO note) {
        NoteDTO savedNote = noteService.createNote(note);
        return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
    }

    /**
     * Endpoint for updating an existing note.
     * @param id The ID of the note to be updated.
     * @param note UpdateNoteDTO object containing updated note details.
     * @return ResponseEntity containing the updated NoteDTO object.
     */
    @PutMapping("/{id}")
    public ResponseEntity<NoteDTO> updateNote(@PathVariable Long id, @RequestBody UpdateNoteDTO note) {
        NoteDTO updatedNote = noteService.updateNote(id, note);
        return ResponseEntity.ok(updatedNote);
    }

    /**
     * Endpoint for deleting a note by its ID.
     * @param id The ID of the note to be deleted.
     * @return ResponseEntity indicating successful deletion with no content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable Long id) {
        noteService.deleteNoteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<NoteDTO> uploadFiles(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String extensionFile = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        if (!extensionFile.equals(".txt") && !extensionFile.equals(".json") && !extensionFile.equals(".xml")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }
        NoteDTO uploadNote = noteService.uploadFiles(file);
        return ResponseEntity.ok(uploadNote);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadNoteById(@PathVariable long id, @RequestParam String format) {
        if (!format.equals("txt") && !format.equals("json") && !format.equals("xml")) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).build();
        }
        FileDTO fileNote = noteService.downloadUserNoteAsFile(id, format);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileNote.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(fileNote.getContentType()))
                .body(fileNote.getFileByte());
    }
}