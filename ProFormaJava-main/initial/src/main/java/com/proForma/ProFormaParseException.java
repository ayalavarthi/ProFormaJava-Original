package com.proForma;

// Base ProForma parsing exception <see cref = "Exception" />
public class ProFormaParseException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// Initializes a new instance of the <see cref="ProFormaParseException"/> class
	public ProFormaParseException() {
		super();
	}

	// Initializes a new instance of the <see cref = "ProFormaParseException" />
	// class
	// @param name = "message" (the message that describes the error
	public ProFormaParseException(String message) {
		super(message);   
	}
}
