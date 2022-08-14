package enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TextMatchType {

	EXACTLY(1),
	WILDCARD(2),
	REGEX(3);
	
	private final int value;
	private static Map<TextMatchType, String> name;

	TextMatchType(int value)
		{ this.value = value; }

	public int getValue()
		{ return value; }
	
	public static List<TextMatchType> getListOfAll() {
		List<TextMatchType> list = new ArrayList<>();
		list.add(EXACTLY);
		list.add(WILDCARD);
		list.add(REGEX);
		return list;
	}
	
	public String getName()
		{ return getName(this); }
	
	public static String getName(TextMatchType s) {
		if (name == null) {
			name = new HashMap<>();
			name.put(EXACTLY, "Valor exato");
			name.put(WILDCARD, "Palavra-chave");
			name.put(REGEX, "Expressão regular");
		}
		return name.get(s);
	}
	
}
