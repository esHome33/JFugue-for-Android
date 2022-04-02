package org.jfugue.pattern;

import java.util.List;

import org.jfugue.theory.Note;
/**
 * Classes that implement this interface produce a List of Note with {@link #getNotes()} method.
 *
 */
public interface NoteProducer {
	/**
	 * 
	 * @return a List of Note
	 */
    public List<Note> getNotes();
}
