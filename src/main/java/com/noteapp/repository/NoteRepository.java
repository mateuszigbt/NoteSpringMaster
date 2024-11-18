package com.noteapp.repository;

import com.noteapp.entity.Note;
import com.noteapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    /**
     * Finds notes by title.
     *
     * @param title The title of the note to find.
     * @return A list of notes with the specified title.
     */
    List<Note> findByTitle(String title);

    /**
     * Finds notes created between the specified start and end dates.
     *
     * @param startDate The start date of the period.
     * @param endDate The end date of the period.
     * @return A list of notes created between the start and end dates.
     */
    List<Note> findByCreationDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds notes containing the specified keyword in their content.
     *
     * @param keyword The keyword to search for in note content.
     * @return A list of notes containing the keyword in their content.
     */
    List<Note> findByContentContaining(String keyword);

    /**
     * Finds all notes belonging to the specified user.
     *
     * @param user The user whose notes to find.
     * @return A list of notes belonging to the specified user.
     */
    List<Note> findAllByUser(User user);
}