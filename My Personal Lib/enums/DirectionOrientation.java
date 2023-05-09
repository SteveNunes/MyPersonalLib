package enums;

import java.util.Arrays;
import java.util.List;

public enum DirectionOrientation {
	CLOCKWISE(0),
	REVERSE_CLOCKWISE(1);
	
	final int value;
	
	final static List<DirectionOrientation> listOfAll = 
		Arrays.asList(CLOCKWISE, REVERSE_CLOCKWISE);
	
	DirectionOrientation(int val)
		{ value = val; }
	
	public int getValue()
		{ return value; }
	
	public List<DirectionOrientation> getListOfAll()
		{ return listOfAll; }
	
	public static DirectionOrientation getReverseDirectionType(DirectionOrientation orientation)
		{ return orientation == CLOCKWISE ? REVERSE_CLOCKWISE : CLOCKWISE; }
	
	public DirectionOrientation getReverseDirectionType()
		{ return getReverseDirectionType(this); }

}
