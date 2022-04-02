/*
 * JFugue, an Application Programming Interface (API) for Music Programming
 * http://www.jfugue.org
 *
 * Copyright (C) 2003-2014 David Koelle
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jfugue.theory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.PatternProducer;
import org.jfugue.provider.ChordProviderFactory;

/**
 * 
 * A chord is an ensemble of notes played together. It's given by a root note and intervals that describe 
 * the at which distance all the notes of the chord are from the root. 
 *
 */
public class Chord implements PatternProducer, Comparable<Chord>
{
	public static Map<String, Intervals> chordMap;
    public static Map<String, String> humanReadableMap;

    static {
        // @formatter:off
	    chordMap = new TreeMap<String, Intervals>(new Comparator<String>() {
			@Override
			public int compare(String s1, String s2) {
				int result = compareLength(s1, s2);
				if (result == 0) { result = s1.compareTo(s2); }
				return result;
			}
			
			/** Compare two strings and the bigger of the two is deemed to come first in order */
			private int compareLength(String s1, String s2) {
				if (s1.length() < s2.length()) {
					return 1;
				} else if (s1.length() > s2.length()) {
					return -1;
				} else {
					return 0;
				}
			}
	    });
		
		// Major Chords
		chordMap.put("MAJ",    new Intervals("1 3 5"));
		chordMap.put("MAJ6",   new Intervals("1 3 5 6"));
		chordMap.put("MAJ7",   new Intervals("1 3 5 7"));
		chordMap.put("MAJ9",   new Intervals("1 3 5 7 9"));
		chordMap.put("ADD9",   new Intervals("1 3 5 9"));
		chordMap.put("MAJ6%9", new Intervals("1 3 5 6 9"));
		chordMap.put("MAJ7%6", new Intervals("1 3 5 6 7"));
		chordMap.put("MAJ13",  new Intervals("1 3 5 7 9 13"));

		// Minor Chords
		chordMap.put("MIN",     new Intervals("1 b3 5"));
		chordMap.put("MIN6",    new Intervals("1 b3 5 6"));
		chordMap.put("MIN7",    new Intervals("1 b3 5 b7"));
		chordMap.put("MIN9",    new Intervals("1 b3 5 b7 9"));
		chordMap.put("MIN11",   new Intervals("1 b3 5 b7 9 11"));
		chordMap.put("MIN7%11", new Intervals("1 b3 5 b7 11"));
		chordMap.put("MINADD9", new Intervals("1 b3 5 9"));
		chordMap.put("MIN6%9",  new Intervals("1 b3 5 6"));
		chordMap.put("MINMAJ7", new Intervals("1 b3 5 7"));
		chordMap.put("MINMAJ9", new Intervals("1 b3 5 7 9"));

		// Dominant Chords
		chordMap.put("DOM7",      new Intervals("1 3 5 b7"));
		chordMap.put("DOM7%6",    new Intervals("1 3 5 6 b7"));
		chordMap.put("DOM7%11",   new Intervals("1 3 5 b7 11"));
		chordMap.put("DOM7SUS",   new Intervals("1 4 5 b7"));
		chordMap.put("DOM7%6SUS", new Intervals("1 4 5 6 b7"));
		chordMap.put("DOM9",      new Intervals("1 3 5 b7 9")); 
		chordMap.put("DOM11",     new Intervals("1 3 5 b7 9 11"));
		chordMap.put("DOM13",     new Intervals("1 3 5 b7 9 13"));
		chordMap.put("DOM13SUS",  new Intervals("1 3 5 b7 11 13"));
		chordMap.put("DOM7%6%11", new Intervals("1 3 5 b7 9 11 13"));

		// Augmented Chords
		chordMap.put("AUG",  new Intervals("1 3 b6"));
		chordMap.put("AUG7", new Intervals("1 3 b6 b7"));
		
		// Diminished Chords
		chordMap.put("DIM",  new Intervals("1 b3 b5"));
		chordMap.put("DIM7", new Intervals("1 b3 b5 6"));

		// Suspended Chords
		chordMap.put("SUS4", new Intervals("1 4 5"));
		chordMap.put("SUS2", new Intervals("1 2 5"));
		
		//Added Chords
		chordMap.put("ADD2", new Intervals("1 2 3 5"));
		chordMap.put("ADD4", new Intervals("1 3 4 5"));

		// Human readable names for some of the more cryptic chord strings
		humanReadableMap = new HashMap<String, String>();
		humanReadableMap.put("MAJ6%9", "6/9");
        humanReadableMap.put("MAJ7%6", "7/6");
		
		// @formatter:on
	}
	
    /**
     * Get all the Chord Names that are stored internally
     * @return an array of Chord Names (String)
     */
	public static String[] getChordNames() {
		return chordMap.keySet().toArray(new String[0]);
	}
	
	/**
	 * Adds a new name and interval in the map {@link #chordMap}.
	 * 
	 * @param name a name for the intervalPattern
	 * @param intervalPattern a string containing the intervals
	 */
	public static void addChord(String name, String intervalPattern) {
		Chord.addChord(name, new Intervals(intervalPattern));
	}
	
	/**
	 * Adds a new name and interval in the map {@link #chordMap}.
	 * @param name a name for the intervalPattern
	 * @param intervalPattern intervals
	 */
	public static void addChord(String name, Intervals intervalPattern) {
		chordMap.put(name, intervalPattern);
	}
	
	/**
	 * Gets the corresponding interval for the given name 
	 * @param name a name of a chord, like "MAJ6"
	 * @return the interval or <code>null</code> if the name isn't in the internal map {@link #chordMap}
	 */
	public static Intervals getIntervals(String name) {
		return chordMap.get(name);
	}
	
	/**
	 * remove the interval and name from the internal map {@link #chordMap}. If this name
	 * doesn't exist in the map, nothing is done and no exception is fired.
	 * @param name the name to remove
	 */
	public static void removeChord(String name) {
		chordMap.remove(name);
	}
	
	/**
	 * Gets the name of a given interval or null if this interval isn't in the internal 
	 * map {@link #chordMap}.
	 * @param intervals an interval
	 * @return the name of this interval or <code>null</code>
	 */
    public static String getChordType(Intervals intervals) {
        for (Map.Entry<String, Intervals> entry : chordMap.entrySet()) {
            if (intervals.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
	  
    /**
     * Insert a new value in the internal {@link #humanReadableMap}. This permits to 
     * associate a human readable name to a chordName (chord names are contained in
     * the internal chord map {@link #chordMap}).
     * 
     * @param chordName the chord to be associated with the given human readable name 
     * @param humanReadableName the given human readable name
     */
    public static void putHumanReadable(String chordName, String humanReadableName) {
        humanReadableMap.put(chordName, humanReadableName);
    }

    /** 
     * Returns a human readable chord name if one exists, otherwise returns the
     * same chord name that was passed in
     */
    public static String getHumanReadableName(String chordName) {
        if (humanReadableMap.containsKey(chordName)) {
            return humanReadableMap.get(chordName);
        }
        return chordName;
    }
    
    /**
     * Returns true if the passed string contains a note, a known chord, and optionally an octave or duration.
     */
    public static boolean isValidChord(String candidateChordMusicString) {
    	String musicString = candidateChordMusicString.toUpperCase();
    	for (String chordName : chordMap.keySet()) {
    		if (musicString.contains(chordName)) {
    			int index = musicString.indexOf(chordName);
    			String possibleNote = musicString.substring(0, index);
    			String qualifiers = musicString.substring(index+chordName.length()-1, musicString.length()-1);
    			if ((Note.isValidNote(possibleNote)) && (Note.isValidQualifier(qualifiers))) {
    				return true;
    			}
    		}		
    	}
    	return false;
    }
    
	private Note rootNote;
	private Intervals intervals;
	private int inversion;
	
	/**
	 * Creates a new chord from the Staccato string given in parameter
	 * @param s a staccato string containing information for the chord to be created
	 */
	public Chord(String s) {
		this(ChordProviderFactory.getChordProvider().createChord(s));
	}

	/**
	 * Creates a new chord from the given chord.
	 * @param chord the chord to duplicate.
	 */
	public Chord(Chord chord) {
		this.rootNote = chord.getRoot();
		this.intervals = chord.getIntervals();
		this.inversion = chord.getInversion();
	}
	
	/**
	 * Creates a chord from the root note and intervals
	 * @param root root note
	 * @param intervals intervals between the root note and the notes in the chord.
	 */
	public Chord(Note root, Intervals intervals) {
		this.rootNote = root;
		this.intervals = intervals;
	}
	
	/**
	 * constructs a new Chord from the Key (a Key is a root and a scale (intervals)).
	 * 
	 * @param key the key
	 * 
	 */
	public Chord(Key key) {
		this.rootNote = key.getRoot();
		this.intervals = key.getScale().getIntervals();
	}
	
	/**
	 * creates a chord from a string
	 * @param noteString a string containing the notes to set in this chord. e.g. "C# F G#" or "Eb5 G#4 C6"
	 * @return the first Chord that reflects the intervals between the given notes ... or null if no Chord was found.
	 */
	public static Chord fromNotes(String noteString) {
	    return fromNotes(noteString.split(" "));
	}
	
	/**
	 * creates a chord from different strings describing a note e.g. "C# F G#" or "Eb5 G#4 C6"
	 * @param noteStrings a array of notes in string form
	 * @return the first Chord that reflects the intervals between the given notes ... or null if no Chord was found.
	 */
	public static Chord fromNotes(String[] noteStrings) {
	    List<Note> notes = new ArrayList<Note>();
	    for (String noteString : noteStrings) {
	        notes.add(new Note(noteString));
	    }
	    return fromNotes(notes.toArray(new Note[notes.size()]));
	}
	/**
	 * creates a chord from different notes e.g. "C# F G#" or "Eb5 G#4 C6"
	 * @param notes an array of Notes
	 * @return the first Chord that reflects the intervals between the given notes ... or null if no Chord was found.
	 */
	public static Chord fromNotes(Note[] notes) {
      return new Chord(getChordFromNotes(notes));
	}
	
	/**
	 * Flatten the notes - meaning, multiple of the same pitches in different durations should 
	 * be represented only once, but maintain their position relative each other
	 * so the chord has the right bass note
	 */
	private static Note[] flattenNotesByPositionInOctave(Note[] notes) {
	    Map<Integer, Note> noteMap = new HashMap<Integer, Note>();
	    List<Integer> noteOrder = new ArrayList<Integer>();
	    for (Note note : notes) {
	        int positionInOctave = note.getPositionInOctave();
	        if (!noteMap.containsKey(positionInOctave)) {
	            noteMap.put(positionInOctave, note);
	            noteOrder.add(positionInOctave);
	        }
	    }
	    
	    Note[] retVal = new Note[noteMap.size()];
	    int counter = 0;
	    for (Integer positionInOctave : noteOrder) {
	        retVal[counter++] = noteMap.get(positionInOctave);
	    }
	    return retVal;
	}
	
	/**
	 * indicates which is the predominant accident in the array of notes
	 * @param notes an array of notes
	 * @return -1 if there are more flats, +1 if there are more sharps or 0 if there are no accidents
	 */
	private static int getDisposition(Note[] notes) {
		int resu = 0;
		for(Note n : notes) {
			String ns = n.originalString;
			if(ns == null) {
				ns = Note.getToneString(n.getValue());
			}
			if(ns.length() > 1) {
				String reste = ns.substring(1).toUpperCase();
				if(reste.contains("BB")) {
					// nothing to count
				} else if (reste.contains("B")) {
					resu--;
				} else if (reste.contains("##")) {
					// nothing to count
				} else if (reste.contains("#")) {
					resu++;
				}
			} else {
				// nothing to count
			}
		}
		if(resu < 0) {
			resu = -1;
		} else if (resu > 0) {
			resu = 1;
		}
		return resu;
	}
	
	/**
	 * Returns best-matching (1st non null) chord type with the given set of intervals
	 * @param notes an array of different notes
	 * @return the name of the chord represented by the given notes or <code>null</code>
	 */
	private static String getChordFromNotes(Note[] notes) {
        boolean returnNonOctaveNotes = false;
        int disp = getDisposition(notes);

	    // Sorting notes by their value will let us know which is the bass note
        Note.sortNotesBy(notes, new Note.SortingCallback() {
            @Override
            public int getSortingValue(Note note) {
                return note.getValue();
            }
        });
        
        // If the distance between the lowest note and the highest note is greater than 12, 
        // we have a chord that spans octaves and we should return a chord in which the
        // notes have no octave.
        if (notes[notes.length-1].getValue() - notes[0].getValue() > Note.OCTAVE) {
            returnNonOctaveNotes = true;
        }
        Note bassNote = notes[0];
        
	    // Sorting notes by position in octave will let us know which chord we have
        Note.sortNotesBy(notes, new Note.SortingCallback() {
            @Override
            public int getSortingValue(Note note) {
                return note.getPositionInOctave();
            }
        });
	    notes = flattenNotesByPositionInOctave(notes);
	    
	    String[] possibleChords = new String[notes.length];
	    for (int i=0; i < notes.length; i++) {
	        Note[] notesToCheck = new Note[notes.length];
	        for (int u=0; u < notes.length; u++) {
	            notesToCheck[u] = notes[(i+u)%notes.length];
	        }
	        possibleChords[i] = Chord.getChordType(Intervals.createIntervalsFromNotes(notesToCheck));
	    }
	    
	    // Now, return the first non-null string
	    for (int i=0; i < possibleChords.length; i++) {
	        if (possibleChords[i] != null) {
	            StringBuilder sb = new StringBuilder();
	            if (returnNonOctaveNotes) {
	                sb.append(Note.getDispositionedToneStringWithoutOctave(disp, notes[i].getValue()));
	            } else {
                    sb.append(Note.getDispositionedToneStringWithoutOctave(disp, notes[i].getValue()));
	            }
	            sb.append(possibleChords[i]);
	            if (!bassNote.equals(notes[i])) {
   	                sb.append("^");
   	                sb.append(bassNote);
	            }
	            return sb.toString();
	        }
	    }
	    
	    return null;
	}
	
	public Note getRoot() {
		return this.rootNote;
	}
	
	public Intervals getIntervals() {
		return this.intervals;
	}
	
	public int getInversion() {
		return this.inversion;
	}
	
	public Chord setInversion(int nth) {
		this.inversion = nth;
		return this;
	}
	
    /**
     * @see setBassNote(Note newBass) for details.
     */
	public Chord setBassNote(String newBass) {
		return setBassNote(new Note(newBass));
	}
	
	/**
	 * Although setBassNote takes a Note, it doesn't just set a local value to the incoming note.
	 * Instead, it uses the incoming note to compute the inversion for this chord, and sets the inversion.
	 * getBassNote() reconstructs the bass note using the inversion.
	 * If the rootNote is null, this method returns without taking any action.
	 */
	public Chord setBassNote(Note newBass) {
		if (rootNote == null) {
			return this; 
		}
		
		for (int i=0; i < intervals.size(); i++) {
			if (newBass.getValue() % 12 == (rootNote.getValue() + Intervals.getHalfsteps(intervals.getNthInterval(i))) % 12) {
				this.inversion = i;
			}
		}
		
		return this;
	}
	
	public Note getBassNote() {
	    int bassNoteValue = rootNote.getValue() - Note.OCTAVE + Intervals.getHalfsteps(this.intervals.getNthInterval(this.inversion));
//	    Note r = new Note(bassNoteValue).setOriginalString(Note.NOTE_NAMES_COMMON[bassNoteValue % Note.OCTAVE]).useSameExplicitOctaveSettingAs(getRoot());
	    Note r = new Note(Note.NOTE_NAMES_COMMON[bassNoteValue % Note.OCTAVE]).useSameExplicitOctaveSettingAs(getRoot());
	    return r;
	}
	
	public Chord setOctave(int octave) {
	    this.rootNote.setValue((byte)(this.rootNote.getPositionInOctave() + octave*Note.OCTAVE));
	    return this;
	}
	
	/**
	 * Using this intervals and the root note, returns an array of notes contained in this chord 
	 * @return an array of notes contained in this chord 
	 */
	public Note[] getNotes() {
		int[] halfsteps = this.intervals.toHalfstepArray();
		Note[] retVal = new Note[halfsteps.length];
		
		Note newrootnote = new Note(rootNote);
		
		retVal[0] = newrootnote;
		int disp_of_root = rootNote.getAccidental();
		for (int i=0; i < halfsteps.length-1; i++) {
			retVal[i+1] = new Note(retVal[i].getValue() + halfsteps[i+1] - halfsteps[i]).setFirstNote(false).setMelodicNote(false).setHarmonicNote(true).useSameDurationAs(getRoot()).useSameExplicitOctaveSettingAs(getRoot());
			if (!this.getRoot().isOctaveExplicitlySet()) {
				if(disp_of_root != 0) {
					retVal[i+1].setOriginalString(Note.getDispositionedToneStringWithoutOctave(disp_of_root, (byte)(retVal[i].getValue() + halfsteps[i+1] - halfsteps[i])));
				} else {
					retVal[i+1].setOriginalString(Note.getToneStringWithoutOctave((byte)(retVal[i].getValue() + halfsteps[i+1] - halfsteps[i])));
				}
			}
		}
		
		// Now calculate inversion
		// 2017-02-17: It looks like this is putting notes up, instead of moving other notes down
		for (int i=0; i < getInversion(); i++) {
			if (i < retVal.length) {
				retVal[i].setValue((byte)(retVal[i].getValue() + Note.OCTAVE));
			}
		}
		
		// Rotate the returned notes based on the inversion
		// Cmaj should return C E G, but Cmaj^^ should return G C E
		Note[] retVal2 = new Note[retVal.length];
		for (int i=0; i < retVal.length; i++) {
		    retVal2[i] = retVal[(i + getInversion()) % retVal.length];		    
		}
		
		return retVal2;
	}

	private String insertChordNameIntoNote(Note note, String chordName) {
		StringBuilder buddy = new StringBuilder();
//		buddy.append(Note.getToneString(note.getValue()));
        buddy.append(note.getToneString());
		buddy.append(chordName);
		if (note.isDurationExplicitlySet()) {
			buddy.append(Note.getDurationString(note.getDuration()));
		}
		buddy.append(note.getVelocityString());
		return buddy.toString();
	}
	
	/**
	 * Gets the name of this chord : MAJ, MIN, ... or <code>null</code>.
	 * @return the name of this chord if it's in the {@link #chordMap} or <code>null</code> if not.
	 */
	public String getChordType() {
        for (Map.Entry<String, Intervals> entry : chordMap.entrySet()) {
            if (this.getIntervals().equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
	}
	

	
	/** 
	 * Returns a count of the number of carets at the end of the chord string.
	 * Given Cmaj^^, this will return 2.
	 * @TODO: Does not give the correct value of the inversion is indicated with a bass note, like Cmaj^E
	 */
	public static int getInversionFromChordString(String chordString) {
	    int counter = 0;
	    for (char c : chordString.toCharArray()) {
	        if (c == '^') { counter++; }
	    }
	    return counter;
	}
	
	@Override
	public Pattern getPattern() {
		Pattern pattern = new Pattern();
		boolean foundChord = false;
		String chordName = getChordType();
		if (chordName != null) {
		    StringBuilder sb = new StringBuilder();
		    sb.append(insertChordNameIntoNote(this.rootNote, chordName));
		    for (int i=0; i < getInversion(); i++) {
		      sb.append("^");  
		    }
		    pattern.add(sb.toString());
			foundChord = true;
		}
		if (!foundChord) {
			return getPatternWithNotes();
		} 
		return pattern;
	}
	
	public Pattern getPatternWithNotes() {
		// A better way of creating a Chord: Check to see if the intervals are in the map; if so, use the associated name. 
		// (Then you'd need to check for inversions, too)
		StringBuilder buddy = new StringBuilder();
		Note[] notes = getNotes();
		for (int i=0; i < notes.length-1; i++) {
			buddy.append(notes[i].getPattern());
			buddy.append("+");
		}
		buddy.append(notes[notes.length-1]);
		return new Pattern(buddy.toString());
	}

    public Pattern getPatternWithNotesExceptRoot() {
        StringBuilder buddy = new StringBuilder();
        Note[] notes = getNotes();
        for (int i = 0; i < notes.length; i++) {
            if (notes[i].getPositionInOctave() != getRoot().getPositionInOctave()) {
                buddy.append(notes[i].getPattern());
                buddy.append("+");
            }
        }
        buddy.deleteCharAt(buddy.length()-1);
        return new Pattern(buddy.toString());
    }

    public Pattern getPatternWithNotesExceptBass() {
        StringBuilder buddy = new StringBuilder();
        Note[] notes = getNotes();
        for (int i = 0; i < notes.length - 1; i++) {
            if (notes[i].getValue() % Note.OCTAVE != getBassNote().getValue() % Note.OCTAVE) {
                buddy.append(notes[i].getPattern());
                buddy.append("+");
            }
        }
        buddy.append(notes[notes.length - 1]);
        return new Pattern(buddy.toString());
    }
    
	public boolean isMajor() {
		return this.intervals.equals(MAJOR_INTERVALS);
	}
	
	public boolean isMinor() {
		return this.intervals.equals(MINOR_INTERVALS);
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Chord)) {
			return false;
		}
		
		Chord c2 = (Chord)o;
		return (c2.rootNote.equals(rootNote) && 
				c2.intervals.equals(intervals) &&
				(c2.inversion == inversion));		
	}
	
	/**
	 * Checks if this chord's notes and the other given chord's notes are the same, not taking in 
	 * account the inversions.
	 * 
	 * @param other an other chord to check against this Chord
	 * @return true if the two chords contains the same notes, false otherwise.
	 */
	public boolean equalsNotes(Chord other) {
		TreeSet<String> co1 = new TreeSet<>();
		TreeSet<String> co2 = new TreeSet<>();
		for(Note n : getNotes()) {
			String ns = Note.getToneStringWithoutOctave(n.getValue());
			co1.add(ns);
		}
		for(Note n : other.getNotes()) {
			String ns = Note.getToneStringWithoutOctave(n.getValue());
			co2.add(ns);
		}

		// on parcourt les notes de co1 et on essaye de l'oter de co2 ... si elle n'y est pas, c'est que les deux accord
		// ne sont pas egaux.
		for(String s : co1) {
			if(!co2.isEmpty()) {
				boolean res = co2.remove(s);
				if(!res) {
					return false;
				}
			}
		}
		
		return true;
	}
	

	@Override
	public String toString() {
		return getPattern().toString();
	}
	
	/** 
	 * Returns a string consisting of the notes in the chord.
	 * For example, new Chord("Cmaj").toNoteString() returns "(C+E+G)"
	 * Update with Java 8 String Joiner done !
	 */
	public String toNoteString() {
/*		StringBuilder buddy = new StringBuilder();
		buddy.append("(");
		for (Note note : getNotes()) {
			buddy.append(note.toString());
			buddy.append("+");
		}
		buddy.deleteCharAt(buddy.length()-1);
		buddy.append(")");
		return buddy.toString(); */
		return useStringJoiner();
	}
	
	/**
	 * append with Java8 StringJoiner
	 * @return the joined string !
	 */
	private String useStringJoiner() {
		StringJoiner sb = new StringJoiner("+", "(", ")");
		for (Note note : getNotes()) {
			sb.add(note.toString());
		}
		return sb.toString();
	}
	
	public String toHumanReadableString() {
	    return this.rootNote + Chord.getHumanReadableName(this.getChordType());
	}

	public String toDebugString() {
		StringBuilder buddy = new StringBuilder();
		int counter = 0;
		for (Note note : getNotes()) {
			buddy.append("Note ").append(counter++).append(": ").append(note.toDebugString()).append("\n");
		}
		buddy.append("Chord Intervals = "+getIntervals().toString()).append("\n");
		buddy.append("Inversion = ").append(inversion);
		return buddy.toString();
	}
	
	public static final Intervals MAJOR_INTERVALS = new Intervals("1 3 5");
	public static final Intervals MINOR_INTERVALS = new Intervals("1 b3 5");
	public static final Intervals DIMINISHED_INTERVALS = new Intervals("1 b3 b5");
    public static final Intervals MAJOR_SEVENTH_INTERVALS = new Intervals("1 3 5 7"); 
    public static final Intervals MINOR_SEVENTH_INTERVALS = new Intervals("1 b3 5 b7");
    public static final Intervals DIMINISHED_SEVENTH_INTERVALS = new Intervals("1 b3 b5 6");
    public static final Intervals MAJOR_SEVENTH_SIXTH_INTERVALS = new Intervals("1 3 5 6 7"); 
    public static final Intervals MINOR_SEVENTH_SIXTH_INTERVALS = new Intervals("1 3 5 6 7");

	@Override
	public int compareTo(Chord other) {
		String thisroot;
		String otherroot;
		byte rootnote_this = this.getRoot().getValue();
		byte rootnote_other = other.getRoot().getValue();
		int diff = rootnote_other - rootnote_this;
		if(diff == 0) {
			thisroot = this.getChordType();
			otherroot = other.getChordType();
			return thisroot.compareTo(otherroot);
		} else {
			return diff;
		}
	} 
}
