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

import org.jfugue.provider.KeyProviderFactory;

/**
 * A Key contains a root {@link Note} and a {@link Scale} (Intervals and a major/minor qualifier).
 */
public class Key 
{
	private Note root;
	private Scale scale;

	/**
	 * Creates a key from a root Note and a Scale
	 * @param root the root Note
	 * @param scale the scale
	 */
	public Key(Note root, Scale scale) {
		this.root = root;
		this.scale = scale;
	}
	
	/**
	 * Creates a new key from a Chord. Set the root and the scale from the given chord
	 * @param chord the chord form which the root and the scale is set.
	 */
	public Key(Chord chord) {
		this.root = chord.getRoot();
		if (chord.isMajor()) {
			this.scale = Scale.MAJOR;
		} else if (chord.isMinor()) {
			this.scale = Scale.MINOR;
		}
	}
	
	/** 
	 * Creates a new key from the given key signature 
	 * @param keySignature a string that gives a chord name (like "Cmaj") 
	 * 		or 'K' followed by sharps or flats (like "K####" for E Major)
	 * 
	 */
	public Key(String keySignature) {
        this(KeyProviderFactory.getKeyProvider().createKey(keySignature));
	}
	
	public Key(Key key) {
	    this.root = key.root;
	    this.scale = key.scale;
	}
	
	/**
	 * 
	 * @return returns the root note and the name of the scale (min, maj or the name)
	 */
    public String getKeySignature() {
        return this.root.toString() + this.scale.toString(); 
    }

    public Note getRoot() {
    	return this.root;
    }

    public Scale getScale() {
    	return this.scale;
    }
    
    /**
     * Default key is C4maj
     */
    public static final Key DEFAULT_KEY = new Key("C4maj");
}
