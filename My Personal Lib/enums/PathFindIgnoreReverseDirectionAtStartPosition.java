package enums;

import java.util.Arrays;
import java.util.List;

public enum PathFindIgnoreReverseDirectionAtStartPosition {
	NO_IGNORE(0),
	ALWAYS_IGNORE(1),
	ONLY_IF_ITS_THE_ONLY_AVAILABLE_DIRECTION(2);
	
	final int value;
	
	final static List<PathFindIgnoreReverseDirectionAtStartPosition> listOfAll = 
		Arrays.asList(NO_IGNORE, ALWAYS_IGNORE, ONLY_IF_ITS_THE_ONLY_AVAILABLE_DIRECTION);
	
	PathFindIgnoreReverseDirectionAtStartPosition(int val)
		{ value = val; }
	
	public int getValue()
		{ return value; }
	
	public List<PathFindIgnoreReverseDirectionAtStartPosition> getListOfAll()
		{ return listOfAll; }	

}
