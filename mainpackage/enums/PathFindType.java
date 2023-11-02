package enums;

import java.util.Arrays;
import java.util.List;

public enum PathFindType {
	LONGEST_PATH(0),
	SHORTEST_PATH(1),
	AVERAGE_PATH(2);
	
	final int value;
	
	final static List<PathFindType> listOfAll = 
		Arrays.asList(LONGEST_PATH, SHORTEST_PATH, AVERAGE_PATH);
	
	PathFindType(int val)
		{ value = val; }
	
	public int getValue()
		{ return value; }
	
	public List<PathFindType> getListOfAll()
		{ return listOfAll; }	

}
