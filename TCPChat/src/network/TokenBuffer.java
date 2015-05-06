package network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

import com.sun.org.apache.xpath.internal.operations.String;

public class TokenBuffer implements CharSequence
//, Enumeration<String>
{
	private static final int STANDARD_INIT_CAPACITY = 128;
	private static final CharSequence STANDARD_DELIM = ";";
	private int initialCapacity;
	private char[] data;
	private final String startDelimiter;
	private final String endDelimiter;

	// public TokenBuffer() {
	// this("", STANDARD_INIT_CAPACITY,STANDARD_DELIM);
	// }
	//
	// public TokenBuffer(String initialText) {
	// this(initialText, STANDARD_INIT_CAPACITY, STANDARD_DELIM);
	// }

	public TokenBuffer(char[] initialText, int initialCapacity,
			String startDelim, String endDelim) {

		if (initialText.length > initialCapacity) {
			throw new IllegalArgumentException(
					"initialCapacity is to short for given string "
							+ initialCapacity + "<=" + initialText.length);
		}
		this.initialCapacity = initialCapacity;
		this.startDelimiter = startDelim;
		this.endDelimiter = endDelim;
		this.data = Arrays.copyOf(initialText, initialCapacity);
		if (new java.lang.String(data).startsWith(startDelimiter)) {
			
		}
	}

	// public TokenBuffer(int initialCapacity) {
	// this("", initialCapacity, STANDARD_DELIM);
	// }

	public boolean hasMoreTokens() {
		return false;
		
	}

	public String nextToken() {
		return endDelimiter;

	}

	@Override
	public int length() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public char charAt(int index) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public boolean hasMoreElements() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public String nextElement() {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
