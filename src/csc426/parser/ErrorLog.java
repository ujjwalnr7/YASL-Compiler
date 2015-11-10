package csc426.parser;

import java.util.ArrayList;
import java.util.List;

public class ErrorLog {
	private List<String> messages;
	
	public ErrorLog() {
		messages = new ArrayList<>();
	}
	
	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (String message : messages) {
			result.append(message).append('\n');
		}
		return result.toString();
	}
	
	public void add(String message) {
		messages.add(message);
	}

	public boolean nonEmpty() {
		return !messages.isEmpty();
	}

	public void reset() {
		messages.clear();
	}
}
