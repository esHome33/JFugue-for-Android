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


/** 
 * A scale is a sequence of notes.
 * MAJOR and MINOR are two examples of scales.
 * A scale in a particular key, such as C Major or A Minor, can provide the notes of the scale
 */
public class Scale {
	/**
	 * the intervals describing this scale.
	 */
    private Intervals intervals;
    /**
     * a name for this scale
     */
    private String name;
    /**
     * indicates wether this scale is Minor or Major 
     */
    private byte majorMinorIndicator;
    
    /**
     * Creates a new scale from intervals given in the parameter. Doesn't set the name and the Min/Maj indicator.
     * @param intervalString a string containing intervals such as "1 2 3b 4" ...
     */
    public Scale(String intervalString) {
        this(new Intervals(intervalString));
    }
    
    /**
     * Creates a new scale from intervals given in the 1st parameter and gives it the name
     * @param intervalString  a string containing intervals such as "1 2 3b 4" ...
     * @param name the name to give to this scale
     */
    public Scale(String intervalString, String name) {
        this(new Intervals(intervalString), name);
    }

    /**
     * Creates a new scale from the given {@link Intervals}. Doesn't set the name and the Min/Maj indicator.
     * @param pattern the intervals pattern
     */
    public Scale(Intervals pattern) {
       this.intervals = pattern; 
    }

    /**
     * Sets the intervals and the name of this scale 
     * @param pattern an interval pattern as a string eg: "1 3 b5 7"
     * @param name the name to give to this new scale.
     */
    public Scale(Intervals pattern, String name) {
        this.intervals = pattern; 
        this.name = name;
    }

    /**
     * Sets the name of this scale
     * @param name the new name
     * @return this scale to enable chaining
     */
    public Scale setName(String name) {
    	this.name = name;
    	return this;
    }
    
    /**
     * Gets the name of this scale
     * @return the name of this scale
     */
    public String getName() {
    	return this.name;
    }
    
    /**
     * gets the intervals of this scale
     * @return the intervals of this scale
     */
    public Intervals getIntervals() {
        return this.intervals;
    }
    
    /**
     * Sets the Maj/min indicator of this scale
     * @param indicator either Scale.MAJOR_INDICATOR or Scale.MINOR_INDICATOR
     * @return this scale for method chaining
     */
    public Scale setMajorOrMinorIndicator(byte indicator) {
    	this.majorMinorIndicator = indicator;
    	return this;
    }
    
    /**
     * Gets this maj/min indicator
     * @return maj/min indicator (either Scale.MAJOR_INDICATOR or Scale.MINOR_INDICATOR)
     */
    public byte getMajorOrMinorIndicator() {
    	return this.majorMinorIndicator;
    }
    
    /** 
     * Returns +1 for MAJOR or -1 for MINOR
     * @return 1 for Major or -1 for Minor
     */
    public int getDisposition() {
    	return (this.majorMinorIndicator == MAJOR_INDICATOR ? 1 : -1);
    }
    
    /**
     * gives a string representation of this Scale : if  {@link #majorMinorIndicator} is set, returns "maj" or "min".
     * Else returns the name of this scale ({@link #name})
     */
    @Override
    public String toString() {
    	if (this.majorMinorIndicator == MAJOR_INDICATOR) {
    		return "maj";
    	} else if (this.majorMinorIndicator == MINOR_INDICATOR) {
    		return "min";
    	} else {
    		return this.name;
    	}
    }
    
    
    /**
     * if the objet in parameter isn't a scale or is null, this method returns false. 
     * If the object is a Scale, this method returns wether the intervals of these two scales are equal.
     * @param o an object
     */
    @Override
    public boolean equals(Object o) {
    	if ((o == null) || (!(o instanceof Scale))) return false;
    	return (((Scale)o).intervals.equals(this.intervals));
    }

    @Override
    public int hashCode() {
    	return this.intervals.hashCode();
    }
    
	public static final Scale MAJOR = new Scale(new Intervals("1 2 3 4 5 6 7")).setMajorOrMinorIndicator(Scale.MAJOR_INDICATOR);
    public static final Scale MINOR = new Scale(new Intervals("1 2 b3 4 5 b6 b7")).setMajorOrMinorIndicator(Scale.MINOR_INDICATOR);
	public static final Scale CIRCLE_OF_FIFTHS = new Scale(new Intervals("1 2 3b 4 5 6 7b"));
	
	public static final byte MAJOR_INDICATOR = 1;
	public static final byte MINOR_INDICATOR = -1;
}
