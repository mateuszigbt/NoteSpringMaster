package com.noteapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.noteapp.dto.note.CreateNoteDTO;
import com.noteapp.dto.note.FileDTO;
import com.noteapp.dto.note.NoteDTO;
import com.noteapp.dto.note.UpdateNoteDTO;
import com.noteapp.entity.Note;
import com.noteapp.entity.User;
import com.noteapp.entity.XmlNoteDTO;
import com.noteapp.exception.note.NoteNotFoundException;
import com.noteapp.repository.NoteRepository;
import com.noteapp.security.UserDetailsImpl;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoteService {

    private final UserService userService;
    private final NoteRepository noteRepository;

    /**
     * Constructs a new NoteService with the specified UserService and NoteRepository.
     *
     * @param userService The service for managing user-related operations.
     * @param noteRepository The repository for accessing note data.
     */
    @Autowired
    public NoteService(UserService userService, NoteRepository noteRepository) {
        this.userService = userService;
        this.noteRepository = noteRepository;
    }

    /**
     * Retrieves notes belonging to the current user.
     *
     * @return A list of notes belonging to the current user.
     */
    public List<NoteDTO> getUserNotes() {
        User user = userService.getCurrentUser();
        List<Note> notes = noteRepository.findAllByUser(user);
        return notes.stream()
                .map(NoteDTO::from)
                .toList();
    }

    public FileDTO downloadUserNoteAsFile(long id, String format) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = userDetails.getUsername();

        Optional<Note> existingNoteOptional = noteRepository.findById(id);
        FileDTO fileNote = new FileDTO();

        if (existingNoteOptional.isPresent()) {
            Note existingNote = existingNoteOptional.get();
            if (!existingNote.getUser().getEmail().equals(userEmail)) {
                throw new NoteNotFoundException(id);
            }

            String fileContent;
            XmlNoteDTO xmlNoteDTO = new XmlNoteDTO();

            switch (format) {
                case "txt":
                    fileContent = existingNote.getTitle() + "\n" + existingNote.getContent();
                    fileNote.setFileByte(fileContent.getBytes(StandardCharsets.UTF_8));
                    fileNote.setFileName(LocalDateTime.now() + ".txt");
                    fileNote.setContentType("text/plain");
                    break;
                case "json": {
                    xmlNoteDTO.setTitle(existingNote.getTitle());
                    xmlNoteDTO.setContent(existingNote.getContent());
                    try {
                        fileContent = new ObjectMapper().writeValueAsString(xmlNoteDTO);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    fileNote.setFileByte(fileContent.getBytes(StandardCharsets.UTF_8));
                    fileNote.setFileName(LocalDateTime.now() + ".json");
                    fileNote.setContentType("application/json");
                    break;
                }
                case "xml": {
                    StringWriter stringWriter = new StringWriter();
                    JAXBContext context = null;
                    try {
                        context = JAXBContext.newInstance(XmlNoteDTO.class);
                    } catch (JAXBException e) {
                        throw new RuntimeException(e);
                    }
                    Marshaller marshaller = null;
                    try {
                        marshaller = context.createMarshaller();
                    } catch (JAXBException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        xmlNoteDTO.setTitle(existingNote.getTitle());
                        xmlNoteDTO.setContent(existingNote.getContent());
                        marshaller.marshal(xmlNoteDTO, stringWriter);
                    } catch (JAXBException e) {
                        throw new RuntimeException(e);
                    }
                    fileContent = stringWriter.toString();
                    fileNote.setFileByte(fileContent.getBytes(StandardCharsets.UTF_8));
                    fileNote.setFileName(LocalDateTime.now() + ".xml");
                    fileNote.setContentType("application/xml");
                    break;
                }
            }
            return fileNote;
        } else {
            throw new NoteNotFoundException(id);
        }
    }

    /**
     * Creates a new note for the current user.
     *
     * @param note The DTO containing the note details to create.
     * @return The created note DTO.
     */
    public NoteDTO createNote(CreateNoteDTO note) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());

        Note newNote = new Note();
        newNote.setTitle(note.getTitle());
        newNote.setContent(note.getContent());
        newNote.setUser(user);
        newNote.setCreationDate(LocalDateTime.now());
        newNote.setModifiedDate(LocalDateTime.now());
        noteRepository.save(newNote);
        return NoteDTO.builder()
                .userId(newNote.getUser().getId())
                .noteId(newNote.getId())
                .content(newNote.getContent())
                .title(newNote.getTitle())
                .creationDate(newNote.getCreationDate())
                .modifiedDate(newNote.getModifiedDate())
                .build();
    }

    public NoteDTO uploadFiles(MultipartFile file) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userService.getUserByEmail(userDetails.getUsername());
        String extensionFile = getFileExtension(file.getOriginalFilename());
        Note note = new Note();
        note.setUser(user);
        if (!file.isEmpty()) {
            switch (extensionFile) {
                case "txt":
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
                        note.setTitle(reader.readLine().trim());
                        note.setContent(reader.lines().reduce("", (acc, line) -> acc + line).trim());
                        note.setCreationDate(LocalDateTime.now());
                        note.setModifiedDate(LocalDateTime.now());
                        break;
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                case "json":
                    String jsonString = null;
                    try {
                        jsonString = new String(file.getBytes(), StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    NoteDTO tempNote = null;
                    try {
                        tempNote = new ObjectMapper().readValue(jsonString, NoteDTO.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    note.setTitle(tempNote.getTitle());
                        note.setContent(tempNote.getContent());
                        note.setCreationDate(LocalDateTime.now());
                        note.setModifiedDate(LocalDateTime.now());
                        break;
                case "xml":
                    JAXBContext jaxbContext = null;
                    try {
                        jaxbContext = JAXBContext.newInstance(XmlNoteDTO.class);
                    } catch (JAXBException e) {
                        throw new RuntimeException(e);
                    }
                    Unmarshaller unmarshaller = null;
                    try {
                        unmarshaller = jaxbContext.createUnmarshaller();
                    } catch (JAXBException e) {
                        throw new RuntimeException(e);
                    }
                    XmlNoteDTO tempXmlNote = null;
                    try {
                        tempXmlNote = (XmlNoteDTO) unmarshaller.unmarshal(file.getInputStream());
                    } catch (JAXBException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    note.setTitle(tempXmlNote.getTitle());
                    note.setContent(tempXmlNote.getContent());
                    note.setCreationDate(LocalDateTime.now());
                    note.setModifiedDate(LocalDateTime.now());
                    break;
            }

        }

        return NoteDTO.from(noteRepository.save(note));
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase()  ;
    }

    /**
     * Updates an existing note.
     *
     * @param id The ID of the note to update.
     * @param updatedNote The DTO containing the updated note details.
     * @return The updated note DTO.
     * @throws NoteNotFoundException if the note with the specified ID is not found.
     */
    public NoteDTO updateNote(Long id, UpdateNoteDTO updatedNote) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = userDetails.getUsername();

        Optional<Note> existingNoteOptional = noteRepository.findById(id);
        if (existingNoteOptional.isPresent()) {
            Note existingNote = existingNoteOptional.get();
            if (!existingNote.getUser().getEmail().equals(userEmail)) {
                throw new NoteNotFoundException(id);
            }
            existingNote.setTitle(updatedNote.getTitle());
            existingNote.setContent(updatedNote.getContent());
            existingNote.setModifiedDate(LocalDateTime.now());
            noteRepository.save(existingNote);
            return NoteDTO.builder()
                    .userId(existingNote.getUser().getId())
                    .content(existingNote.getContent())
                    .title(existingNote.getTitle())
                    .creationDate(existingNote.getCreationDate())
                    .modifiedDate(existingNote.getModifiedDate())
                    .build();
        } else {
            throw new NoteNotFoundException(id);
        }
    }

    /**
     * Deletes a note by ID.
     *
     * @param id The ID of the note to delete.
     * @throws NoteNotFoundException if the note with the specified ID is not found.
     */
    public void deleteNoteById(Long id) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<Note> noteOptional = noteRepository.findById(id);

        if (noteOptional.isPresent()) {
            Note note = noteOptional.get();
            if (!note.getUser().getEmail().equals(userDetails.getUsername())) {
                throw new NoteNotFoundException(id);
            }
        } else {
            throw new NoteNotFoundException(id);
        }

        if (noteRepository.existsById(id)) {
            noteRepository.deleteById(id);
        } else {
            throw new NoteNotFoundException(id);
        }
    }
}
