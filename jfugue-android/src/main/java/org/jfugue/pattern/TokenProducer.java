package org.jfugue.pattern;

import java.util.List;
/**
 * Interface that can produce Token
 */
public interface TokenProducer {
	/**
	 * @return returns a list of Token
	 */
    public List<Token> getTokens();
}
