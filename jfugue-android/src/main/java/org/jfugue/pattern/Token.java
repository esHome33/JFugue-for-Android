package org.jfugue.pattern;

public class Token implements PatternProducer 
{
    private String string;
    private TokenType type;

    /**
     * 
     * Creates a new Token
     * @param string a string that can be transformed in a Pattern
     * @param type the type of this token
     */
    public Token(String string, TokenType type) {
        this.string = string;
        this.type = type;
    }
    
    /** 
     * Involves the Staccato parsers to figure out what type of token this is
     */
    public TokenType getType() {
        return this.type;
    }
    
    @Override
    public String toString() {
        return this.string;
    }
    
    @Override
    public Pattern getPattern() {
        return new Pattern(string);
    }
    
    /**
     * An enum that sets the different token types :
     * VOICE, LAYER, INSTRUMENT, TEMPO, KEY_SIGNATURE, TIME_SIGNATURE,
     * BAR_LINE, TRACK_TIME_BOOKMARK, TRACK_TIME_BOOKMARK_REQUESTED,
     * LYRIC, MARKER, FUNCTION, NOTE,
     * WHITESPACE, ATOM,
     * UNKNOWN_TOKEN
     */
    public enum TokenType { 
        VOICE, LAYER, INSTRUMENT, TEMPO, KEY_SIGNATURE, TIME_SIGNATURE, 
        BAR_LINE, TRACK_TIME_BOOKMARK, TRACK_TIME_BOOKMARK_REQUESTED, 
        LYRIC, MARKER, FUNCTION, NOTE,
        WHITESPACE, ATOM,
        UNKNOWN_TOKEN };
}
