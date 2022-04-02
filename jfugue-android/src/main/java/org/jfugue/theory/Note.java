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

import org.jfugue.midi.MidiDefaults;
import org.jfugue.pattern.Pattern;
import org.jfugue.pattern.PatternProducer;
import org.jfugue.provider.NoteProviderFactory;
import org.staccato.DefaultNoteSettingsManager;
import org.staccato.NoteSubparser;

public class Note implements PatternProducer
{
	private byte value;
	private double duration;
    private boolean wasOctaveExplicitlySet;
	private boolean wasDurationExplicitlySet;
	private byte onVelocity;
	private byte offVelocity;
	private boolean isRest;
	private boolean isStartOfTie;
	private boolean isEndOfTie;
	private boolean isFirstNote = true;
	private boolean isMelodicNote;
	private boolean isHarmonicNote;
	private boolean isPercussionNote;
	/**
	 * The original string value used to create this note, if present !
	 */
	public String originalString;
	
	/**
	 * Creates a new note (with default value for onVelocity and offVelocity, and nothing else is set)
	 */
	public Note() { 
		this.onVelocity = DefaultNoteSettingsManager.getInstance().getDefaultOnVelocity();
		this.offVelocity = DefaultNoteSettingsManager.getInstance().getDefaultOffVelocity();
	}
	
	/**
	 * Creates a new note from a note in Staccato String form
	 * @param note a note in Staccato String form
	 */
	public Note(String note) {
		this(NoteProviderFactory.getNoteProvider().createNote(note));  
	}
	
	/**
	 * Creates a new note from the given note
	 * @param note a note to duplicate in this new note
	 */
	public Note(Note note) {
		this.value = note.value;
		this.duration = note.duration;
        this.wasOctaveExplicitlySet = note.wasOctaveExplicitlySet;
		this.wasDurationExplicitlySet = note.wasDurationExplicitlySet;
		this.onVelocity = note.onVelocity;
		this.offVelocity = note.offVelocity;
		this.isRest = note.isRest;
		this.isStartOfTie = note.isStartOfTie;
		this.isEndOfTie = note.isEndOfTie;
		this.isFirstNote = note.isFirstNote;
		this.isMelodicNote = note.isMelodicNote;
		this.isHarmonicNote = note.isHarmonicNote;
		this.isPercussionNote = note.isPercussionNote;
		this.originalString = note.originalString;
	}
	
	/**
	 * Creates a new note from the given value
	 * @param value a value (
	 */
	public Note(int value) {
		this((byte)value);
	}
	
	public Note(byte value) {
		this();
		this.value = value;
		this.setOctaveExplicitlySet(false);
		useDefaultDuration();
	}
	
	public Note(int value, double duration) {
		this((byte)value, duration);
	}

	public Note(byte value, double duration) {
		this();
		this.value = value;
		setDuration(duration);
	}

	/**
	 * Sets the value of this note.
	 * @param value the value of this note
	 * @return this note for chaining
	 */
	public Note setValue(byte value) {
	    this.value = value;
		return this;
	}
	
	/**
	 * Gets the value of this note or 0 if it's a rest
	 * @return
	 */
	public byte getValue() {
		return isRest() ? 0 : this.value;
	}
	
	/**
	 * Changes the value of the note by adding the given delta to the current value. 
	 * @param delta the amount to add to the current value of this note.
	 * @return this note for chaining
	 */
	public Note changeValue(int delta) {
		this.setValue((byte)(getValue() + delta));
//		this.originalString = null;
		return this;
	}

	public Note setOctaveExplicitlySet(boolean set) {
	    this.wasOctaveExplicitlySet = set;
	    return this;
	}
	
	/**
	 * returns the octave of this note or 0 if it is a rest.
	 * @return octave number or 0 if it's a rest
	 */
	public byte getOctave() {
		return isRest() ? 0 : (byte)(this.getValue() / Note.OCTAVE);
	}
	
	public double getDuration() {
		return this.duration;
	}
	
	/**
	 * Sets the duration to the given value and sets wasDurationExplicitlySet to true 
	 * @param d duration to set to this note
	 * @return this note for chaining
	 */
	public Note setDuration(double d) {
	    this.duration = d;
		this.wasDurationExplicitlySet = true;
	    return this;
	}

	public Note useDefaultDuration() {
		this.duration = DefaultNoteSettingsManager.getInstance().getDefaultDuration();
		// And do not set wasDurationExplicitlySet
		return this;
	}

	public Note useSameDurationAs(Note note2) {
		this.duration = note2.duration;
		this.wasDurationExplicitlySet = note2.wasDurationExplicitlySet;
		return this;
	}

	/**
	 * Sets the same value to {@link #wasOctaveExplicitlySet} for this note than for note2 
	 * @param note2 the that is giving the value of wasOctaveExplicitlySet to this note 
	 * @return this note for chaining
	 */
    public Note useSameExplicitOctaveSettingAs(Note note2) {
        this.wasOctaveExplicitlySet = note2.wasOctaveExplicitlySet;
        return this;
    }

    /**
     * Sets the duration for this note to the value given by duration (staccato string)
     * @param duration the duration in Staccato String form
     * @return this note for chaining
     */
	public Note setDuration(String duration) {
		return setDuration(NoteProviderFactory.getNoteProvider().getDurationForString(duration));
	}
	
	public boolean isDurationExplicitlySet() {
		return this.wasDurationExplicitlySet;
	}
	
    public boolean isOctaveExplicitlySet() {
        return this.wasOctaveExplicitlySet;
    }
    
	/**
	 * FOR TESTING PURPOSES ONLY - avoids setting "isDurationExplicitlySet" - Please use setDuration instead!
	 */
	public Note setImplicitDurationForTestingOnly(double d) {
		this.duration = d;
		// And do not set wasDurationExplicitlySet
		return this;
	}

	public Note setRest(boolean rest) {
		this.isRest = rest;
		return this;
	}
	
	public boolean isRest() {
		return this.isRest;
	}
	
	public Note setPercussionNote(boolean perc) {
		this.isPercussionNote = perc;
		return this;
	}
	
	public boolean isPercussionNote() {
		return this.isPercussionNote;
	}

	public Note setOnVelocity(byte velocity) {
		this.onVelocity = velocity;
		return this;
	}

	public byte getOnVelocity() {
	    return this.onVelocity; 
	}
	
	public Note setOffVelocity(byte velocity) {
		this.offVelocity = velocity;
		return this;
	}

    public byte getOffVelocity() {
        return this.offVelocity; 
    }
    
	public Note setStartOfTie(boolean isStartOfTie) {
		this.isStartOfTie = isStartOfTie;
		return this;
	}

	public Note setEndOfTie(boolean isEndOfTie) {
		this.isEndOfTie = isEndOfTie;
		return this;
	}

	public boolean isStartOfTie() {
		return isStartOfTie;
	}
	
	public boolean isEndOfTie() {
		return isEndOfTie;
	}
	
	public Note setFirstNote(boolean isFirstNote) {
		this.isFirstNote = isFirstNote;
		return this;
	}

	public boolean isFirstNote() {
		return this.isFirstNote;
	}
	
	public Note setMelodicNote(boolean isMelodicNote) {
		this.isMelodicNote = isMelodicNote;
		return this;
	}

	public boolean isMelodicNote() {
		return this.isMelodicNote;
	}
	
	public Note setHarmonicNote(boolean isHarmonicNote) {
		this.isHarmonicNote = isHarmonicNote;
		return this;
	}

	public boolean isHarmonicNote() {
		return this.isHarmonicNote;
	}

	public Note setOriginalString(String originalString) {
		this.originalString = originalString;
		return this;
	}
	
	public String getOriginalString() {
		return this.originalString;
	}
	
	public double getMicrosecondDuration(double mpq) {
		return (this.duration * 4.0f) * mpq;
	}

	/**
	 * gets the rank number of this note in an octave. A rest is always 0.
	 * Eg : C5 has position 1, as C4.
	 * 
	 * @return the position of this note in an octave. 0 if this is a rest (else from 1 to 11)
	 */
	public byte getPositionInOctave() {
	    return isRest() ? 0 : (byte)(getValue() % Note.OCTAVE);
	}

	/**
	 * compares the two notes. G# and Ab are same notes
	 * @param note1
	 * @param note2
	 * @return
	 */
	public static boolean isSameNote(String note1, String note2) {
		if (note1.equalsIgnoreCase(note2)) return true;
		for (int i=0; i < NOTE_NAMES_COMMON.length; i++) {
			if (note1.equalsIgnoreCase(NOTE_NAMES_FLAT[i]) && note2.equalsIgnoreCase(NOTE_NAMES_SHARP[i])) return true;
			if (note1.equalsIgnoreCase(NOTE_NAMES_SHARP[i]) && note2.equalsIgnoreCase(NOTE_NAMES_FLAT[i])) return true;
		}
		return false;
	}
	
	/**
	 * This is just Bubble Sort, but allows you to pass a Note.SortingCallback that returns
	 * a value that you want to sort for a note. For example, to sort based on position in octave,
	 * your SortingCallback would return note.getPositionInOctave(). This lets you sort by
	 * note value, octave, position in octave, duration, velocity, and so on.
	 */
	public static void sortNotesBy(Note[] notes, Note.SortingCallback callback) {
        Note temp;
        for (int i = 0; i < notes.length - 1; i++) {
            for (int j = 1; j < notes.length - i; j++) {
                if (callback.getSortingValue(notes[j - 1]) > callback.getSortingValue(notes[j])) {
                    temp = notes[j - 1];
                    notes[j - 1] = notes[j];
                    notes[j] = temp;
                }
            }
        }
	}
	
	public static Note createRest(double duration) {
		return new Note().setRest(true).setDuration(duration);
	}
	
    /**
     * Returns a MusicString representation of the given MIDI note value,
     * which indicates a note and an octave.
     * 
     * @param noteValue this MIDI note value, like 61
     * @return a MusicString value, like Db5
     */
    public static String getToneString(byte noteValue) {
        StringBuilder buddy = new StringBuilder();
        buddy.append(getToneStringWithoutOctave(noteValue));
        buddy.append(noteValue / Note.OCTAVE); 
        return buddy.toString();
    }

    /**
     * Returns a MusicString representation of the given MIDI note value,
     * but just the note - not the octave. This means that the value returned
     * can not be used to accurately recalculate the noteValue, since information
     * will be missing. But this is useful for knowing what note within any octave
     * the corresponding value belongs to.
     * 
     * @param noteValue this MIDI note value, like 60
     * @return a MusicString value, like C
     */
    public static String getToneStringWithoutOctave(byte noteValue) {
        return NOTE_NAMES_COMMON[noteValue % Note.OCTAVE];
    }

    /**
     * Returns a MusicString representation of the given MIDI note value,
     * just the note (not the octave), disposed to use either flats or sharps.
     * Pass -1 to get a flat name and +1 to get a sharp name for any notes
     * that are accidentals.
     * 
     * @param dispose -1 to get a flat value, +1 to get a sharp value
     * @param noteValue this MIDI note value, like 61
     * @return a MusicString value, like Db if -1 or C# if +1
     */
    public static String getDispositionedToneStringWithoutOctave(int dispose, byte noteValue) {
        if (dispose == -1) {
        	return NOTE_NAMES_FLAT[noteValue % Note.OCTAVE];
        } else {
        	return NOTE_NAMES_SHARP[noteValue % Note.OCTAVE];
        }
    }

    /**
     * Returns a MusicString representation of the given MIDI note value,
     * that is the note and its octave, disposed to use either flats or sharps.
     * Pass -1 to get a flat name and +1 to get a sharp name for any notes.
     * 
     * @param dispose select -1 if you want the enharmonic with a flat or 1 if you want a sharp note enharmonic. 
     * @param noteValue a note value
     * @return a string representation of a note with octave indication, with the disposition of enharmonic note selected with dispose 
     * @author Etienne
     */
    public static String getDispositionedToneStringWithOctave(int dispose, byte noteValue) {
    	String note;
        if (dispose == -1) {
        	note = NOTE_NAMES_FLAT[noteValue % Note.OCTAVE];
        } else {
        	note = NOTE_NAMES_SHARP[noteValue % Note.OCTAVE];
        }
        int oct = noteValue / Note.OCTAVE;
        return note + oct;
    }

    
    
    /**
     * Returns a MusicString representation of the given MIDI note value
     * using the name of a percussion instrument.
     * @param noteValue this MIDI note value, like 60 (this value must be greater than 34).
     * @return a MusicString value, like [BASS_DRUM]
     */
    public static String getPercussionString(byte noteValue) {
        StringBuilder buddy = new StringBuilder();
        buddy.append("[");
        buddy.append(PERCUSSION_NAMES[noteValue-35]);
        buddy.append("]");
        return buddy.toString();
    }

    /**
     * Returns the frequency, in Hertz, for the given note.
     * For example, the frequency for A5 (MIDI note 69) is 440.0
     * @param noteValue the MIDI note value
     * @return frequency in Hertz
     */
    public static double getFrequencyForNote(String note) {
		return (note.toUpperCase().startsWith("R")) ? 0.0d : getFrequencyForNote(NoteProviderFactory.getNoteProvider().createNote(note).getValue());
    }
		
    /**
     * Returns the frequency, in Hertz, for the given note value.
     * For example, the frequency for A5 (MIDI note 69) is 440.0
     * @param noteValue the MIDI note value
     * @return frequency in Hertz
     */
    public static double getFrequencyForNote(int noteValue) {
        return truncateTo3DecimalPlaces(getPreciseFrequencyForNote(noteValue));
    }

    private static double truncateTo3DecimalPlaces(double preciseNumber) {
    	return Math.rint(preciseNumber * 10000.0) / 10000.0;
    }
    
    private static double getPreciseFrequencyForNote(int noteValue) {
    	return getFrequencyAboveBase(8.1757989156, noteValue / 12.0);
    }

    private static double getFrequencyAboveBase(double baseFrequency, double octavesAboveBase) {
    	return baseFrequency * Math.pow(2.0, octavesAboveBase);
    }

    public static boolean isValidNote(String candidateNote) {
        return NoteSubparser.getInstance().matches(candidateNote);
    }
    
    /**
     * Function not yet implemented
     * @param candidateQualifier a string
     * @return returns always true for the moment !
     */
    public static boolean isValidQualifier(String candidateQualifier) {
        return true; // TODO: Implement Note.isValidQualifier when necessary
    }
    
    /**
     * Returns a MusicString representation of a decimal duration.  This code
     * currently only converts single duration values representing whole, half,
     * quarter, eighth, etc. durations; and dotted durations associated with those
     * durations (such as "h.", equal to 0.75).  This method does not convert
     * combined durations (for example, "hi" for 0.625). For these values,
     * the original decimal duration is returned in a string, prepended with a "/"
     * to make the returned value a valid MusicString duration indicator.
     * It does handle durations greater than 1.0 (for example, "wwww" for 4.0).  
     *
     * @param decimalDuration The decimal value of the duration to convert
     * @return a MusicString fragment representing the duration
     */
    public static String getDurationString(double decimalDuration) {
        double originalDecimalDuration = decimalDuration;
        StringBuilder buddy = new StringBuilder();
        if (decimalDuration >= 1.0) {
            int numWholeDurations = (int)Math.floor(decimalDuration); 
            buddy.append("w");
            if (numWholeDurations > 1) {
            	buddy.append(numWholeDurations);
            }
            decimalDuration -= numWholeDurations;
        }
        if (decimalDuration == 0.75) buddy.append("h.");
        else if (decimalDuration == 0.5) buddy.append("h");
        else if (decimalDuration == 0.375) buddy.append("q.");
        else if (decimalDuration == 0.25) buddy.append("q");
        else if (decimalDuration == 0.1875) buddy.append("i.");
        else if (decimalDuration == 0.125) buddy.append("i");
        else if (decimalDuration == 0.09375) buddy.append("s.");
        else if (decimalDuration == 0.0625) buddy.append("s");
        else if (decimalDuration == 0.046875) buddy.append("t.");
        else if (decimalDuration == 0.03125) buddy.append("t");
        else if (decimalDuration == 0.0234375) buddy.append("x.");
        else if (decimalDuration == 0.015625) buddy.append("x");
        else if (decimalDuration == 0.01171875) buddy.append("o.");
        else if (decimalDuration == 0.0078125) buddy.append("o");
        else if (decimalDuration == 0.0) { }
        else {
            return "/" + originalDecimalDuration;    
        }
        return buddy.toString();
    }
    
    /**
     * returns h (for a beat of 2) , q(4),  i(8) or s(16) or /0.4 for example
     * @param beat the beat
     * @return duration string like h(for a beat of 2) , q(4),  i(8) or s(16) or /0.4 for example 
     */
    public static String getDurationStringForBeat(int beat) {
    	switch(beat) {
    		case 2 : return "h";
    		case 4 : return "q";
    		case 8 : return "i";
    		case 16 : return "s";
    		default : return "/"+(1.0/(double)beat);
    	}
    }

    /**
     * get the velocity string of this note. if this value is default, this method returns an empty string.
     * Otherwise, it returns a5 (for onVelocity) or d5 (for OffVelocity)
     * @return the velocity string of this note
     */
    public String getVelocityString() {
    	StringBuilder buddy = new StringBuilder();
	    if (this.onVelocity != DefaultNoteSettingsManager.getInstance().getDefaultOnVelocity()) {
	        buddy.append("a"+getOnVelocity());
	    }
        if (this.offVelocity != DefaultNoteSettingsManager.getInstance().getDefaultOffVelocity()) {
            buddy.append("d"+getOffVelocity());
        }
        return buddy.toString();
    }
    
    /**
     * @return a pattern representing this note + decorator string. Does not
     * return indicators of whether the note is harmonic
     * or melodic.
     */
    @Override
	public Pattern getPattern() {
	    StringBuilder buddy = new StringBuilder();
	    buddy.append(toStringWithoutDuration());
	    buddy.append(getDecoratorString());
	    return new Pattern(buddy.toString()); 
	}

    /**
     * returns the percussion pattern (if this note isn't a percussion, it returns the pattern),
     * in other words, the percussion name and all the decorators.
     * @return the percussion pattern, that is the percussion name and all the decorators.
     */
    public Pattern getPercussionPattern() {
    	if (getValue() < MidiDefaults.MIN_PERCUSSION_NOTE || getValue() > MidiDefaults.MAX_PERCUSSION_NOTE) return getPattern(); 
    	StringBuilder buddy = new StringBuilder();
	    buddy.append(Note.getPercussionString(getValue()));
	    buddy.append(getDecoratorString());
	    return new Pattern(buddy.toString()); 
    }
    
    /**
     * returns the Pattern of this note.
     */
	public String toString() {
		return getPattern().toString();
	}
	
	/**
	 * a string representing this note without decorations (durations or frequencies).
	 * If this note is a rest, this method returns "R". 
	 * If this note is a percussion string, this method returns the name of percussion (eg. "[BASS_DRUM]")
	 * 
	 * @return this note as a simple String (the originalString if set)
	 */
	public String toStringWithoutDuration() {
		if (isRest()) {
			return "R";
		} else if (isPercussionNote()) {
			return Note.getPercussionString(this.getValue());
		} else {
			return (originalString != null) ? this.originalString : Note.getToneString(this.getValue());
		}
	}
	
	/**
	 * Gets the tone of this note as a String. Eg : R or G# or BB4 if {@link #wasOctaveExplicitlySet}.
	 * This function take into account the value of {@link #originalString}.
	 * 
	 * @return the tone of this note or "R" if it's a rest.
	 * @author dmkoelle & Etienne introducing originalString value to get the relevant tone string 
	 */
	public String getToneString() {
	    if (isRest) {
	        return "R";
	    }
	    
        StringBuilder buddy = new StringBuilder();
        if(originalString != null) {
        	buddy.append(originalString);
        } else {
            buddy.append(Note.getToneStringWithoutOctave(getValue()));
        }
	    if (this.wasOctaveExplicitlySet) {
	        buddy.append(getOctave());
	    }
	    return buddy.toString();
	}
	
	/**
	 * gets the equivalent of {@link #getToneString()} but with french note names
	 * @return the tone of this note or "R" if it's a rest.
	 * @author Etienne
	 */
	public String getFrenchName() {
		String tone = getToneString();
		String letter = tone.charAt(0) + "";
		int acc = getAllAccidental();
		String acc_s = getAccidents(acc);
		String letterFR;
		
		switch (letter) {
		case "A":
			letterFR = "La";
			break;
		case "B":
			letterFR = "Si";
			break;
		case "C":
			letterFR = "Do";
			break;
		case "D":
			letterFR = "Ré";
			break;
		case "E":
			letterFR = "Mi";
			break;
		case "F":
			letterFR = "Fa";
			break;
		case "G":
			letterFR = "Sol";
			break;
		default:
			letterFR = letter;
			break;
		}
		return letterFR + acc_s;
	}
	
	
	/**
	 * Gets the accidental of this note.
	 * 
	 * That is : returns -1 (if the original String of this note contains one or more flats)
	 * or +1 (if the original String of this note contains one or more sharps)
	 * or 0 if not.
	 * 
	 * @return -1 (for a flat), 1(for a sharp) or 0. Eg : -1 for Dbb or 0 for E or 1 for D#7
	 * @author Etienne
	 */
	public int getAccidental() {
		String tone = getToneString();
		if(!isRest && !isPercussionNote) {
			if(tone.length() > 1) {
				String last = tone.substring(1);
				if(last.toUpperCase().contains("BB")) {
					return -1;
				} else if(last.toUpperCase().contains("B")) {
					return -1;
				} else if(last.toUpperCase().contains("##")) {
					return 1;
				} else if(last.toUpperCase().contains("#")) {
					return 1;
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}
		return 0;
	}

	/**
	 * Gets the number and the accidental of this note.
	 * 
	 * That is : returns -1 or -2, if the original String of this note contains 1 or 2 flats.
	 * or returns +1 or +2, if the original String of this note contains 1 or 2 sharps.
	 * or returns 0 if this note contains no sharps and no flats.
	 * 
	 * @return -1/-2 for flats || +1/+2 for sharps or 0. Eg : -2 for Dbb or 0 for E or 1 for D#7
	 * @author Etienne
	 */
	public int getAllAccidental() {
		String tone = getToneString();
		if(!isRest && !isPercussionNote) {
			if(tone.length()>1) {
				String last = tone.substring(1);
				if(last.toUpperCase().contains("BB")) {
					return -2;
				} else if(last.toUpperCase().contains("B")) {
					return -1;
				} else if(last.toUpperCase().contains("##")) {
					return 2;
				} else if(last.toUpperCase().contains("#")) {
					return 1;
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}
		return 0;
	}

	
	/**
	 * Returns the "decorators" to the base note, which includes the duration if one is explicitly 
	 * specified, and velocity dynamics if provided
	 */
	public String getDecoratorString() {
	    StringBuilder buddy = new StringBuilder();
	    if (isDurationExplicitlySet()) {
	    	buddy.append(Note.getDurationString(this.duration));
	    }
	    buddy.append(getVelocityString());
	    return buddy.toString(); 
	}
	
	
	
	public boolean equals(Object o) {
		if (!(o instanceof Note)) {
			return false;
		}
		
		Note n2 = (Note)o;
		boolean originalStringsMatchSufficientlyWell = ((n2.originalString == null) || (this.originalString == null)) ? true : n2.originalString.equalsIgnoreCase(this.originalString);
		return ((n2.value == this.value) &&
		        (n2.duration == this.duration) &&
                (n2.wasOctaveExplicitlySet == this.wasOctaveExplicitlySet) &&
		        (n2.wasDurationExplicitlySet == this.wasDurationExplicitlySet) &&
		        (n2.isEndOfTie == this.isEndOfTie) && 
		        (n2.isStartOfTie == this.isStartOfTie) &&
		        (n2.isMelodicNote == this.isMelodicNote) &&
		        (n2.isHarmonicNote == this.isHarmonicNote) &&
		        (n2.isPercussionNote == this.isPercussionNote) &&
		        (n2.isFirstNote == this.isFirstNote) &&
		        (n2.isRest == this.isRest) && 
		        (n2.onVelocity == this.onVelocity) &&
		        (n2.offVelocity == this.offVelocity) &&
		        originalStringsMatchSufficientlyWell);
	}

	public String toDebugString() {
		StringBuilder buddy = new StringBuilder();
		buddy.append("Note:");
		buddy.append(" value=").append(this.getValue());
		buddy.append(" duration=").append(this.getDuration());
        buddy.append(" wasOctaveExplicitlySet=").append(this.isOctaveExplicitlySet());
		buddy.append(" wasDurationExplicitlySet=").append(this.isDurationExplicitlySet());
        buddy.append(" isEndOfTie=").append(this.isEndOfTie()); 
        buddy.append(" isStartOfTie=").append(this.isStartOfTie());
        buddy.append(" isMelodicNote=").append(this.isMelodicNote());
        buddy.append(" isHarmonicNote=").append(this.isHarmonicNote());
        buddy.append(" isPercussionNote=").append(this.isPercussionNote()) ;
        buddy.append(" isFirstNote=").append(this.isFirstNote());
        buddy.append(" isRest=").append(this.isRest()); 
        buddy.append(" onVelocity=").append(this.getOnVelocity());
        buddy.append(" offVelocity=").append(this.getOffVelocity());
        buddy.append(" originalString=").append(this.getOriginalString());
        return buddy.toString();
	}
	
	/**
	 * utilise le compte d'accidents pour donner une représentation des accidents
	 * @author Etienne
	 * @param nombre le nombre d'accidents (-2 correspond à 2 bémols, 0 à aucun accident et 1 à un dièse
	 * @return l'accident correspondant au nombre indiqué
	 */
	private String getAccidents(int nombre) {
		switch(nombre) {
		case 1:
			return "#";
		case 2:
			return "##";
		case -1:
			return "B";
		case -2:
			return "BB";
		default:
			return "";
		}
	}
	
	/**
	 * adds the given Interval to this Note and returns the new Note. 
	 * This operation respects the accidents and works differently than methods in #Intervals class
	 * @param intervalle interval to add to this note
	 * @return a new note distant from this note by the given intervalle
	 * @author Etienne
	 */
	public Note addInterval(Interv intervalle) {
		if(isRest()) {
			return null;
		}
		String orig = getOriginalString();
		String ton = (orig == null) ? getToneString().charAt(0)+"" : orig.charAt(0)+"";
		int acc = this.getAllAccidental();
		int decal_halfsteps = intervalle.getValueInHalfSteps();
		int decal_notes = intervalle.getValueInNotes();
		char nv_note = (char) (ton.charAt(0) + decal_notes -1);
		if(nv_note > 'G') {
			nv_note = (char)('A' + nv_note - 'H');
		}
		// pour déterminer l'accident de la note d'arrivée, on va créer un intervalle et voir s'il manque
		// quelque chose pour avoir les halfsteps demandés.
		String note_depart,note_arrivee;
		note_depart = ton + getAccidents(acc);
		note_arrivee = nv_note+"";
		Intervals ecart = Intervals.createIntervalsFromNotes(note_depart + " " + note_arrivee);
		String prems = ecart.getNthInterval(1);
		int halfs_trouves = Intervals.getHalfsteps(prems);
		int delta = halfs_trouves - decal_halfsteps;
		// en fonction du delta, on ajoute un bémol ou un dièse
		String nv_acc;
		if(delta > 1) {
			nv_acc = "BB";
		} else if(delta > 0) {
			nv_acc = "B";
		} else if(delta < -1) {
			nv_acc = "##";
		} else if(delta < 0) {
			nv_acc = "#";
		} else {
			nv_acc = "";
		}
		String note_a_creeer = nv_note + nv_acc;
		Note resultat = new Note(note_a_creeer);
		resultat.setOriginalString(note_a_creeer);
		return resultat;
	}
	
    public final static String[] NOTE_NAMES_COMMON = new String[] { "C", "C#", "D", "Eb", "E", "F", "F#", "G", "G#", "A", "Bb", "B" };
    public final static String[] NOTE_NAMES_SHARP = new String[] { "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B" };
    public final static String[] NOTE_NAMES_FLAT = new String[] { "C", "Db", "D", "Eb", "E", "F", "Gb", "G", "Ab", "A", "Bb", "B" };
    
    /**
     * Intervals names and properties that permits to add or substract intervals to Notes.
     * @author Etienne
     *
     */
    public static enum Interv {
    	
    	SECONDE_DIM(0,"SECONDE DIMINUEE", "DIM 2ND",2),
    	SECONDE_MIN(1,"SECONDE MINEURE", "MINOR 2ND",2),
    	SECONDE_MAJ(2,"SECONDE MAJEURE", "MAJOR 2ND",2),
    	SECONDE_AUG(3,"SECONDE AUGMENTEE", "AUG 2ND",2),
    	
    	TIERCE_DIM(2, "TIERCE DIMINUEE", "DIM 3RD",3),
    	TIERCE_MIN(3, "TIERCE MINEURE", "MINOR 3RD",3),
    	TIERCE_MAJ(4, "TIERCE MAJEURE", "MAJOR 3RD",3),
    	TIERCE_AUG(5, "TIERCE AUGMENTEE", "AUG 3RD",3),
    	
    	QUARTE_DIM(4, "QUARTE DIMINUEE", "DIM 4TH",4),
    	QUARTE_JUSTE(5, "QUARTE JUSTE", "PERFECT 4TH",4),
    	QUARTE_AUG(6, "QUARTE AUGMENTEE", "AUG 4TH",4),
    	
    	QUINTE_DIM(6, "QUINTE DIMINUEE", "DIM 5TH",5),
    	QUINTE_JUSTE(7, "QUINTE JUSTE", "PERFECT 5TH",5),
    	QUINTE_AUG(8, "QUINTE AUGMENTEE", "AUG 5TH",5),
    	
    	SIXTE_DIM(7, "SIXTE DIMINUEE", "DIM 6TH",6),
    	SIXTE_MIN(8, "SIXTE MINEURE", "MINOR 6TH",6),
    	SIXTE_MAJ(9, "SIXTE MAJEURE", "MAJOR 6TH",6),
    	SIXTE_AUG(10, "SIXTE AUGMENTEE", "AUG 6TH",6),
    	
    	SEPTIEME_DIM(9, "SEPTIEME DIMINUEE", "DIM 7TH",7),
    	SEPTIEME_MIN(10, "SEPTIEME MINEURE", "MINOR 7TH",7),
    	SEPTIEME_MAJ(11, "SEPTIEME MAJEURE", "MAJOR 7TH",7),
    	SEPTIEME_AUG(12, "SEPTIEME AUGMENTEE", "AUG 7TH",7),

    	;

    	private int demi_ton;
    	private int ecart_notes;
    	private String nameFR;
    	private String nameUS;
    	
    	/**
    	 * private constructor that initialises values for a given name of interval
    	 * @param i number of halfsteps
    	 * @param nFR name in french
    	 * @param nUS name in GB/US language
    	 * @param ecart_notes number of notes difference
    	 * @author Etienne
    	 */
		Interv(int i, String nFR, String nUS, int ecart_notes) {
			demi_ton = i;
			nameFR = nFR;
			nameUS = nUS;
			this.ecart_notes = ecart_notes;
		}
		
		/**
		 * gets the number of semi-tones of this interval
		 * @return the number of semi-tones of this interval
		 * @author Etienne
		 */
		public int getValueInHalfSteps() {
			return demi_ton;
		}

		/**
		 * Gets the number of degrees for this interval
		 * @return number of degrees for this interval
		 * @author Etienne
		 */
		public int getValueInNotes() {
			return ecart_notes;
		}

		
		public String getUSName() {
			return nameUS;
		}
		public String getFRName() {
			return nameFR;
		}
		
		public static final String UNDEFINED = "undefined"; 
		
		/**
		 * retrouve le nom de l'intervalle à l'aide de l'écart entre notes et nombre de 1/2 tons.
		 * @param ecart_note
		 * @param num_halfsteps
		 * @return nom de l'intervalle
		 * @author Etienne
		 */
		public static String getName(int ecart_note, int num_halfsteps, boolean inFrench) {
			switch (ecart_note) {
			case 2:
				if(num_halfsteps == 0) {
					return (inFrench) ? SECONDE_DIM.nameFR : SECONDE_DIM.nameUS;
				} else if (num_halfsteps == 1) {
					return (inFrench) ? SECONDE_MIN.nameFR : SECONDE_MIN.nameUS;
				} else if (num_halfsteps == 2) {
					return (inFrench) ? SECONDE_MAJ.nameFR : SECONDE_MAJ.nameUS;
				} else if (num_halfsteps == 3) {
					return (inFrench) ? SECONDE_AUG.nameFR : SECONDE_AUG.nameUS;
				} else {
					return UNDEFINED;
				}
			case 3:
				if(num_halfsteps == 2) {
					return (inFrench) ? TIERCE_DIM.nameFR : TIERCE_DIM.nameUS;
				} else if (num_halfsteps == 3) {
					return (inFrench) ? TIERCE_MIN.nameFR : TIERCE_MIN.nameUS;
				} else if (num_halfsteps == 4) {
					return (inFrench) ? TIERCE_MAJ.nameFR : TIERCE_MAJ.nameUS;
				} else if (num_halfsteps == 5) {
					return (inFrench) ? TIERCE_AUG.nameFR : TIERCE_AUG.nameUS;
				} else {
					return UNDEFINED;
				}
			case 4:
				if(num_halfsteps == 4) {
					return (inFrench) ? QUARTE_DIM.nameFR : QUARTE_DIM.nameUS;
				} else if (num_halfsteps == 5) {
					return (inFrench) ? QUARTE_JUSTE.nameFR : QUARTE_JUSTE.nameUS;
				} else if (num_halfsteps == 6) {
					return (inFrench) ? QUARTE_AUG.nameFR : QUARTE_AUG.nameUS;
				} else {
					return UNDEFINED;
				}
			case 5:
				if(num_halfsteps == 6) {
					return (inFrench) ? QUINTE_DIM.nameFR : QUINTE_DIM.nameUS;
				} else if (num_halfsteps == 7) {
					return (inFrench) ? QUINTE_JUSTE.nameFR : QUINTE_JUSTE.nameUS;
				} else if (num_halfsteps == 8) {
					return (inFrench) ? QUINTE_AUG.nameFR : QUINTE_AUG.nameUS;
				} else {
					return UNDEFINED;
				}
			case 6:
				if(num_halfsteps == 7) {
					return (inFrench) ? SIXTE_DIM.nameFR : SIXTE_DIM.nameUS;
				} else if (num_halfsteps == 8) {
					return (inFrench) ? SIXTE_MIN.nameFR : SIXTE_MIN.nameUS;
				} else if (num_halfsteps == 9) {
					return (inFrench) ? SIXTE_MAJ.nameFR : SIXTE_MAJ.nameUS;
				} else if (num_halfsteps == 10) {
					return (inFrench) ? SIXTE_AUG.nameFR : SIXTE_AUG.nameUS;
				} else {
					return UNDEFINED;
				}
			case 7:
				if(num_halfsteps == 9) {
					return (inFrench) ? SEPTIEME_DIM.nameFR : SEPTIEME_DIM.nameUS;
				} else if (num_halfsteps == 10) {
					return (inFrench) ? SEPTIEME_MIN.nameFR : SEPTIEME_MIN.nameUS;
				} else if (num_halfsteps == 11) {
					return (inFrench) ? SEPTIEME_MAJ.nameFR : SEPTIEME_MAJ.nameUS;
				} else if (num_halfsteps == 12) {
					return (inFrench) ? SEPTIEME_AUG.nameFR : SEPTIEME_AUG.nameUS;
				} else {
					return UNDEFINED;
				}

			default:
				return UNDEFINED;
			}
		}
		
		
		public static Interv get(String nom) {
			if("SECONDE DIMINUEE".equals(nom) || "DIM 2ND".equals(nom)) {
				return SECONDE_DIM;
			}
			if("SECONDE MINEURE".equals(nom) || "MINOR 2ND".equals(nom)) {
				return SECONDE_MIN;
			}
			if("SECONDE MAJEURE".equals(nom) || "MAJOR 2ND".equals(nom)) {
				return SECONDE_MAJ;
			}
			if("SECONDE AUGMENTEE".equals(nom) || "AUG 2ND".equals(nom)) {
				return SECONDE_AUG;
			}
			
			if("TIERCE DIMINUEE".equals(nom) || "DIM 3RD".equals(nom)) {
				return TIERCE_DIM;
			}
			if("TIERCE MINEURE".equals(nom) || "MINOR 3RD".equals(nom)) {
				return TIERCE_MIN;
			}
			if("TIERCE MAJEURE".equals(nom) || "MAJOR 3RD".equals(nom)) {
				return TIERCE_MAJ;
			}
			if("TIERCE AUGMENTEE".equals(nom) || "AUG 3RD".equals(nom)) {
				return TIERCE_AUG;
			}

			if("QUARTE DIMINUEE".equals(nom) || "DIM 4TH".equals(nom)) {
				return QUARTE_DIM;
			}
			if("QUARTE JUSTE".equals(nom) || "PERFECT 4TH".equals(nom)) {
				return QUARTE_JUSTE;
			}
			if("QUARTE AUGMENTEE".equals(nom) || "AUG 4TH".equals(nom)) {
				return QUARTE_AUG;
			}

			if("QUINTE DIMINUEE".equals(nom) || "DIM 5TH".equals(nom)) {
				return QUINTE_DIM;
			}
			if("QUINTE JUSTE".equals(nom) || "PERFECT 5TH".equals(nom)) {
				return QUINTE_JUSTE;
			}
			if("QUINTE AUGMENTEE".equals(nom) || "AUG 5TH".equals(nom)) {
				return QUINTE_AUG;
			}

			if("SIXTE DIMINUEE".equals(nom) || "DIM 6TH".equals(nom)) {
				return SIXTE_DIM;
			}
			if("SIXTE MINEURE".equals(nom) || "MINOR 6TH".equals(nom)) {
				return SIXTE_MIN;
			}
			if("SIXTE MAJEURE".equals(nom) || "MAJOR 6TH".equals(nom)) {
				return SIXTE_MAJ;
			}
			if("SIXTE AUGMENTEE".equals(nom) || "AUG 6TH".equals(nom)) {
				return SIXTE_AUG;
			}

			if("SEPTIEME DIMINUEE".equals(nom) || "DIM 7TH".equals(nom)) {
				return SEPTIEME_DIM;
			}
			if("SEPTIEME MINEURE".equals(nom) || "MINOR 7TH".equals(nom)) {
				return SEPTIEME_MIN;
			}
			if("SEPTIEME MAJEURE".equals(nom) || "MAJOR 7TH".equals(nom)) {
				return SEPTIEME_MAJ;
			}
			if("SEPTIEME AUGMENTEE".equals(nom) || "AUG 7TH".equals(nom)) {
				return SEPTIEME_AUG;
			}
			
			return null;
		}
    	
    }
    

    public final static String[] PERCUSSION_NAMES = new String[] {
    	// Percussion Name		// MIDI Note Value
    	"ACOUSTIC_BASS_DRUM", 	//       35
    	"BASS_DRUM", 			//       36
    	"SIDE_STICK", 			//       37
    	"ACOUSTIC_SNARE",		//       38
    	"HAND_CLAP", 			//       39
    	"ELECTRIC_SNARE", 		//       40
    	"LO_FLOOR_TOM", 		//       41
    	"CLOSED_HI_HAT",		//       42
    	"HIGH_FLOOR_TOM", 		//       43
    	"PEDAL_HI_HAT", 		//       44
    	"LO_TOM", 				//       45
    	"OPEN_HI_HAT", 		    //       46
    	"LO_MID_TOM", 			//       47
    	"HI_MID_TOM", 			//       48
    	"CRASH_CYMBAL_1", 		//       49
    	"HI_TOM",				//       50
    	"RIDE_CYMBAL_1", 		//       51
    	"CHINESE_CYMBAL", 		//       52
    	"RIDE_BELL", 			//       53
    	"TAMBOURINE",			//       54
    	"SPLASH_CYMBAL", 		//       55
    	"COWBELL", 				//       56
    	"CRASH_CYMBAL_2", 		//       57
    	"VIBRASLAP",			//       58
    	"RIDE_CYMBAL_2", 		//       59
    	"HI_BONGO", 			//       60
    	"LO_BONGO", 			//       61
    	"MUTE_HI_CONGA",		//       62
    	"OPEN_HI_CONGA", 		//       63
    	"LO_CONGA", 			//       64
    	"HI_TIMBALE", 			//       65
    	"LO_TIMBALE",			//       66
    	"HI_AGOGO", 			//       67
    	"LO_AGOGO", 			//       68
    	"CABASA", 				//       69
    	"MARACAS", 				//       70
    	"SHORT_WHISTLE", 		//       71
    	"LONG_WHISTLE", 		//       72
    	"SHORT_GUIRO", 			//       73
    	"LONG_GUIRO",			//       74
    	"CLAVES", 				//       75
    	"HI_WOOD_BLOCK", 		//       76
    	"LO_WOOD_BLOCK", 		//       77
    	"MUTE_CUICA",			//       78
    	"OPEN_CUICA", 			//       79
    	"MUTE_TRIANGLE", 		//       80
    	"OPEN_TRIANGLE"			//       81
    };
    
    public static final Note REST = new Note(0).setRest(true);
    public static final byte OCTAVE = 12;
    public static final byte MIN_OCTAVE = 0;
    public static final byte MAX_OCTAVE = 10;

    /** For use with Note.sortNotesBy() */
    interface SortingCallback {
        /** Must return an int. If you want to sort by duration (which is decimal), you'll need to work around this. */
        public int getSortingValue(Note note);
    }

 
}

