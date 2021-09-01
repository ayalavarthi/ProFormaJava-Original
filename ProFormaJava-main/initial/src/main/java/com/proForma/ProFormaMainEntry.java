package com.proForma;

public class ProFormaMainEntry {

	public static void main(String[] args) {
		ProFormaParser parser = new ProFormaParser();
		
    // Replace 'parseThisString' with the string to parse
		ProFormaTerm term = parser.parseString("parseThisString");
		System.out.println(term.sequence);
	}
}
