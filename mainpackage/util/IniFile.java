package util;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IniFile {
	
	long changedTime;
	private Path file;
	private String fileName, lastReadVal = null;
	private List<String> fileBuffer;
	private LinkedHashMap<String, LinkedHashMap<String, String>> iniBody;
	static LinkedHashMap<String, IniFile> openedIniFiles = new LinkedHashMap<>();
	
	static void saveAllFilesToDisk() {
		for (IniFile iniFile : openedIniFiles.values())
			iniFile.saveToDisk();
	}
	
	void saveToDisk() {
		if (openedIniFiles.containsKey(fileName)) {
			changedTime = 0;
			updateFileBuffer();
			MyFile.writeAllLinesOnFile(fileBuffer, fileName);
		}
	}

	private IniFile(String fileName) {
		this.fileName = fileName;
		refresh();
		loadIniFromDisk(fileName);
		openedIniFiles.put(fileName, this);
		changedTime = 0;
	}
	
	static { TextFile.enableAutoSave(); }
	
	public static IniFile getNewIniFileInstance(String fileName) {
		if (!openedIniFiles.containsKey(fileName)) 
			return new IniFile(fileName);
		openedIniFiles.get(fileName).refresh();
		return openedIniFiles.get(fileName);
	}
	
	private void refresh() {
		file = Paths.get(fileName);
		loadIniFromDisk(fileName);
	}

	public static List<IniFile> getOpenedIniFilesList()
		{ return (List<IniFile>)openedIniFiles.values(); }

	public Path getFilePath()
		{ return file; }
	
	public String getNextFreeNumericItem(String section) {
		int n = 1;
		for (; isItem(section, "" + n); n++);
		return "" + n;
	}
	
	public void removeNumericItemAndReorderSection(String section, String item) {
		Map<String, String> newSection = new HashMap<>();
		int n = 1;
		for (String s : getItemList(section))
			if (!read(section, s).equals(item))
				newSection.put("" + (n++), read(section, s));
		while (!getItemList(section).isEmpty())
			remove(section, getItemList(section).get(0));
		for (String s : newSection.keySet())
			write(section, s, newSection.get(s));
		changedTime = System.currentTimeMillis();
	}

	private static Boolean stringIsSection(String s) {
		String[] split = s.split(" ");
		return split.length == 0 ? false : s.split(" ")[0].matches(("\\Q" + "[*]*" + "\\E").replace("*", "\\E.*\\Q"));
	}

	private static Boolean stringIsItem(String s) 
		{ return !s.isEmpty() && s.charAt(0) != '=' && s.contains("="); }

	private static String getSectionFromString(String s)
		{ return s.split("]")[0].split(" ")[0].substring(1); }

	public void loadIniFromDisk(String fileName) {
		iniBody = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		if (!fileName.isEmpty()) {
			if (Files.exists(file)) {
				fileBuffer = MyFile.readAllLinesFromFile(fileName);
				String section = "", item, val;
				for (String s : fileBuffer)
					if (stringIsSection(s)) {
						section = getSectionFromString(s);
					  iniBody.put(section, new LinkedHashMap<String, String>());
					}
					else if (!section.isEmpty() && stringIsItem(s)) {
						String[] split = s.split("=");
						item = split[0];
						val = MyConverters.arrayToString(split, 1, "=");
						write(section, item, val);
					}
			}
			else
				fileBuffer = new ArrayList<>();
		}
	}

	public void loadIniFromDisk() 
		{ loadIniFromDisk(fileName); }
	
	private void insertMissingItens(String section, List<String> fileBuffer, Map<String, List<String>> items) {
		if (!section.isEmpty()) {
			if (!items.containsKey(section)) {
				if (!fileBuffer.isEmpty() && !fileBuffer.get(fileBuffer.size() - 1).trim().isEmpty())
					fileBuffer.add("");
				fileBuffer.add("[" + section + "]");
				items.put(section, new ArrayList<>());
			}
			for (String i : getItemList(section))
				if (!items.get(section).contains(i)) {
					fileBuffer.add(i + "=" + read(section, i));
					items.get(section).add(i);
				}
		}
	}
	
	private void updateFileBuffer() {
		Map<String, List<String>> addeds = new HashMap<>();
		List<String> newFileBuffer = new ArrayList<>();
		String section = "", item, value, line;
		for (int l = 0, lmax = fileBuffer.size(); l < lmax; l++) {
			line = fileBuffer.get(l);
			if (line.isEmpty()) {
				insertMissingItens(section, newFileBuffer, addeds);
				if (getItemList(section).isEmpty())
					newFileBuffer.remove(newFileBuffer.size() - 1);
			}
			if (stringIsSection(line)) {
				if (newFileBuffer.size() == 1 && newFileBuffer.get(0).isEmpty())
					newFileBuffer.remove(0);
				else if (newFileBuffer.size() > 1 && getItemList(section).isEmpty())
					for (int n = 0; n < 2; n++)
						newFileBuffer.remove(newFileBuffer.size() - 1);
				section = getSectionFromString(line);
				if (!getItemList(section).isEmpty()) {
					if (isSection(section)) {
						newFileBuffer.add(line);
						addeds.put(section, new ArrayList<>());
					}
					else {
						section = line = "";
						while (++l < lmax && !stringIsSection(line = fileBuffer.get(l)));
						if (stringIsSection(line))
							l--;
					}
				}
			}
			else if (!section.isEmpty() && stringIsItem(line)) {
				String[] split = line.split("=");
				item = split[0];
				if (isItem(section, item)) {
					value = read(section, item);
					newFileBuffer.add(item + "=" + value);
					addeds.get(section).add(item);
				}
			}
			else
				newFileBuffer.add(line);
		}
		insertMissingItens(section, newFileBuffer, addeds);
		for (String sec : getSectionList())
			if (!getItemList(sec).isEmpty())
				insertMissingItens(sec, newFileBuffer, addeds);
		fileBuffer = new ArrayList<>(newFileBuffer);
	}
	
	public void write(String iniSection, String iniItem, String value) {
		if (!iniBody.containsKey(iniSection))
		  iniBody.put(iniSection, new LinkedHashMap<String, String>());
		iniBody.get(iniSection).put(iniItem, value);
		changedTime = System.currentTimeMillis();
	}

	public <T> void write(String section, String item, List<T> numberList, String separator) {
		StringBuilder sb = new StringBuilder();
		for (T v : numberList)
			sb.append((sb.isEmpty() ? "" : separator) + v.toString());
		write(section, item, sb.toString());
	}
	
	public <T extends Number> void write(String section, String item, List<T> numberList)
		{ write(section, item, numberList, " "); }
	
	public <T extends Number> void write(String section, String item, T[] numberArray, String separator) {
		StringBuilder sb = new StringBuilder();
		for (T v : numberArray)
			sb.append((sb.isEmpty() ? "" : separator) + v.toString());
		write(section, item, sb.toString());
	}
	
	public <T extends Number> void write(String section, String item, T[] numberArray)
		{ write(section, item, numberArray, " "); }
	
	public void write(String section, String item, Boolean[] numberArray, String separator) {
		StringBuilder sb = new StringBuilder();
		for (Boolean b : numberArray)
			sb.append((sb.isEmpty() ? "" : separator) + b.toString());
		write(section, item, sb.toString());
	}
	
	public void write(String section, String item, Boolean[] numberArray)
		{ write(section, item, numberArray, " "); }
	
	public String getLastReadVal()
		{ return lastReadVal; }
	
	public String read(String iniSection, String iniItem) {
		if (isItem(iniSection, iniItem)) 
			return (lastReadVal = iniBody.get(iniSection).get(iniItem));
		return (lastReadVal = null);
	}

	public String read(String iniSection, String iniItem, String defaultValue)
		{ return read(iniSection, iniItem) != null ? lastReadVal : (lastReadVal = defaultValue); }

  public <T extends Enum<T>> T readAsEnum(String section, String item, Class<T> enumClass, T defaultReturnValue) {
		try
			{ return T.valueOf(enumClass, read(section, item)); }
		catch (Exception e)
			{ return defaultReturnValue; }
  }
	
  public <T extends Enum<T>> T readAsEnum(String section, String item, Class<T> enumClass)
  	{ return readAsEnum(section, item, enumClass, null); }
  
	public Boolean readAsBoolean(String section, String item, Boolean defaultReturnValue) {
		try
			{ return Boolean.parseBoolean(read(section, item)); }
		catch (Exception e)
			{ return defaultReturnValue; }
	}
	
	public Boolean readAsBoolean(String section, String item)
		{ return readAsBoolean(section, item, null); }
	
	public Byte readAsByte(String section, String item, Byte defaultReturnValue) {
		try
			{ return Byte.parseByte(read(section, item)); }
		catch (Exception e)
			{ return defaultReturnValue; }
	}
	
	public Byte readAsByte(String section, String item)
		{ return readAsByte(section, item, null); }
	
	public Short readAsShort(String section, String item, Short defaultReturnValue) {
		try
			{ return Short.parseShort(read(section, item)); }
		catch (Exception e)
			{ return defaultReturnValue; }
	}
	
	public Short readAsShort(String section, String item)
		{ return readAsShort(section, item, null); }
	
	public Integer readAsInteger(String section, String item, Integer defaultReturnValue) {
		try
			{ return Integer.parseInt(read(section, item)); }
		catch (Exception e)
			{ return defaultReturnValue; }
	}
	
	public Integer readAsInteger(String section, String item)
		{ return readAsInteger(section, item, null); }

	public Long readAsLong(String section, String item, Long defaultReturnValue) {
		try
			{ return Long.parseLong(read(section, item)); }
		catch (Exception e)
			{ return defaultReturnValue; }
	}
	
	public Long readAsLong(String section, String item)
		{ return readAsLong(section, item, null); }

	public Float readAsFloat(String section, String item, Float defaultReturnValue) {
		try
			{ return Float.parseFloat(read(section, item)); }
		catch (Exception e)
			{ return defaultReturnValue; }
	}
	
	public Float readAsFloat(String section, String item)
		{ return readAsFloat(section, item, null); }

	public Double readAsDouble(String section, String item, Double defaultReturnValue) {
		try
			{ return Double.parseDouble(read(section, item)); }
		catch (Exception e)
			{ return defaultReturnValue; }
	}
	
	public Double readAsDouble(String section, String item)
		{ return readAsDouble(section, item, null); }

	public Character readAsCharacter(String section, String item, Character defaultReturnChar) {
		try
			{ return read(section, item).charAt(0); }
		catch (Exception e)
			{ return defaultReturnChar; }
	}

	public Character readAsCharacter(String section, String item)
		{ return readAsCharacter(section, item, null); }

	public BigInteger readAsBigInteger(String section, String item, BigInteger defaultReturnChar) {
		try
			{ return new BigInteger(read(section, item)); }
		catch (Exception e)
			{ return defaultReturnChar; }
	}

	public BigInteger readAsBigInteger(String section, String item)
		{ return readAsBigInteger(section, item, null); }

	public BigDecimal readAsBigDecimal(String section, String item, BigDecimal defaultReturnChar) {
		try
			{ return new BigDecimal(read(section, item)); }
		catch (Exception e)
			{ return defaultReturnChar; }
	}

	public BigDecimal readAsBigDecimal(String section, String item)
		{ return readAsBigDecimal(section, item, null); }

	public <T extends Enum<T>> List<T> readAsEnumList(String section, String item, Class<T> enumClass, List<T> defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			List<T> list = new ArrayList<>();
			for (int i = 0; i < split.length; i++)
				list.add(T.valueOf(enumClass, split[i]));
			return list;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}

	public <T extends Enum<T>> List<T> readAsEnumList(String section, String item, Class<T> enumClass, List<T> defaultReturnArray)
		{ return readAsEnumList(section, item, enumClass, defaultReturnArray, " "); }

	public <T extends Enum<T>> List<T> readAsEnumList(String section, String item, Class<T> enumClass, String separator)
		{ return readAsEnumList(section, item, enumClass, null, separator); }

	public <T extends Enum<T>> List<T> readAsEnumList(String section, String item, Class<T> enumClass)
		{ return readAsEnumList(section, item, enumClass, " "); }

	public Boolean[] readAsBooleanArray(String section, String item, Boolean[] defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			Boolean[] array = new Boolean[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Boolean.parseBoolean(split[i]);
			return array;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}

	public Boolean[] readAsBooleanArray(String section, String item, Boolean[] defaultReturnArray)
		{ return readAsBooleanArray(section, item, defaultReturnArray, " "); }

	public Boolean[] readAsBooleanArray(String section, String item, String separator)
		{ return readAsBooleanArray(section, item, null, separator); }

	public Boolean[] readAsBooleanArray(String section, String item)
		{ return readAsBooleanArray(section, item, " "); }

	public byte[] readAsByteArray(String section, String item, byte[] defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			byte[] array = new byte[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Byte.parseByte(split[i]);
			return array;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}

	public byte[] readAsByteArray(String section, String item, byte[] defaultReturnArray)
		{ return readAsByteArray(section, item, defaultReturnArray, " "); }

	public byte[] readAsByteArray(String section, String item, String separator)
		{ return readAsByteArray(section, item, null, separator); }

	public byte[] readAsByteArray(String section, String item)
		{ return readAsByteArray(section, item, " "); }

	public short[] readAsShortArray(String section, String item, short[] defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			short[] array = new short[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Short.parseShort(split[i]);
			return array;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}

	public short[] readAsShortArray(String section, String item, short[] defaultReturnArray)
		{ return readAsShortArray(section, item, defaultReturnArray, " "); }

	public short[] readAsShortArray(String section, String item, String separator)
		{ return readAsShortArray(section, item, null, separator); }

	public short[] readAsShortArray(String section, String item)
		{ return readAsShortArray(section, item, " "); }

	public int[] readAsIntArray(String section, String item, int[] defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			int[] array = new int[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Integer.parseInt(split[i]);
			return array;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}
	
	public int[] readAsIntArray(String section, String item, int[] defaultReturnArray)
		{ return readAsIntArray(section, item, defaultReturnArray, " "); }
	
	public int[] readAsIntArray(String section, String item, String separator)
		{ return readAsIntArray(section, item, null, separator); }
	
	public int[] readAsIntArray(String section, String item)
		{ return readAsIntArray(section, item, " "); }

	public long[] readAsLongArray(String section, String item, long[] defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			long[] array = new long[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Long.parseLong(split[i]);
			return array;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}
	
	public long[] readAsLongArray(String section, String item, long[] defaultReturnArray)
		{ return readAsLongArray(section, item, defaultReturnArray, " "); }
	
	public long[] readAsLongArray(String section, String item, String separator)
		{ return readAsLongArray(section, item, null, separator); }
	
	public long[] readAsLongArray(String section, String item)
		{ return readAsLongArray(section, item, " "); }

	public float[] readAsFloatArray(String section, String item, float[] defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			float[] array = new float[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Float.parseFloat(split[i]);
			return array;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}
	
	public float[] readAsFloatArray(String section, String item, float[] defaultReturnArray)
		{ return readAsFloatArray(section, item, defaultReturnArray, " "); }
	
	public float[] readAsFloatArray(String section, String item, String separator)
		{ return readAsFloatArray(section, item, null, separator); }
	
	public float[] readAsFloatArray(String section, String item)
		{ return readAsFloatArray(section, item, " "); }

	public double[] readAsDoubleArray(String section, String item, double[] defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			double[] array = new double[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Double.parseDouble(split[i]);
			return array;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}
	
	public double[] readAsDoubleArray(String section, String item, double[] defaultReturnArray)
		{ return readAsDoubleArray(section, item, defaultReturnArray, " "); }
	
	public double[] readAsDoubleArray(String section, String item, String separator)
		{ return readAsDoubleArray(section, item, null, separator); }
	
	public double[] readAsDoubleArray(String section, String item)
		{ return readAsDoubleArray(section, item, " "); }

	public char[] readAsCharArray(String section, String item, char[] defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			char[] array = new char[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = split[i].charAt(0);
			return array;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}
	
	public char[] readAsCharArray(String section, String item, char[] defaultReturnArray)
		{ return readAsCharArray(section, item, defaultReturnArray, " "); }
	
	public char[] readAsCharArray(String section, String item, String separator)
		{ return readAsCharArray(section, item, null, separator); }
	
	public char[] readAsCharArray(String section, String item)
		{ return readAsCharArray(section, item, " "); }

	public BigInteger[] readAsBigIntegerArray(String section, String item, BigInteger[] defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			BigInteger[] array = new BigInteger[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = new BigInteger(split[i]);
			return array;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}
	
	public BigInteger[] readAsBigIntegerArray(String section, String item, BigInteger[] defaultReturnArray)
		{ return readAsBigIntegerArray(section, item, defaultReturnArray, " "); }
	
	public BigInteger[] readAsBigIntegerArray(String section, String item, String separator)
		{ return readAsBigIntegerArray(section, item, null, separator); }
	
	public BigInteger[] readAsBigIntegerArray(String section, String item)
		{ return readAsBigIntegerArray(section, item, " "); }

	public BigDecimal[] readAsBigDecimalArray(String section, String item, BigDecimal[] defaultReturnArray, String separator) {
		try {
			String[] split = read(section, item).split(separator);
			BigDecimal[] array = new BigDecimal[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = new BigDecimal(split[i]);
			return array;
		}
		catch (Exception e)
			{ return defaultReturnArray; }
	}
	
	public BigDecimal[] readAsBigDecimalArray(String section, String item, BigDecimal[] defaultReturnArray)
		{ return readAsBigDecimalArray(section, item, defaultReturnArray, " "); }
	
	public BigDecimal[] readAsBigDecimalArray(String section, String item, String separator)
		{ return readAsBigDecimalArray(section, item, null, separator); }
	
	public BigDecimal[] readAsBigDecimalArray(String section, String item)
		{ return readAsBigDecimalArray(section, item, " "); }

	public String remove(String iniSection, String iniItem) {
		if (isItem(iniSection, iniItem)) {
			iniBody.get(iniSection).remove(iniItem);
			return iniItem;
		}
		return null;
	}

	public String remove(String iniSection) {
		if (isSection(iniSection)) {
			iniBody.remove(iniSection);
			changedTime = System.currentTimeMillis();
			return iniSection;
		}
		return null;
	}

	public String fileName()
		{ return fileName; }

	public int getIniSize()
		{ return !iniBody.isEmpty() ? iniBody.size() : 0; }

	public int getSectionPos(String iniSection) {
		if (isSection(iniSection)) {
			Iterator<String> it = iniBody.keySet().iterator();
			for (int n = 0; it.hasNext(); n++)
				if (it.next().equals(iniSection))
					return n + 1;
		}
		return 0;
	}

	public String getSectionAtPos(int pos) {
		if (!iniBody.isEmpty()) {
			List<String> list = getSectionList();
			if (pos - 1 < 0 || pos - 1 >= list.size())
				return null;
			return list.get(pos - 1);
		}
		return null;
	}

	public Boolean isSection(String iniSection)
		{ return !iniBody.isEmpty() ? iniBody.containsKey(iniSection) : false; }

	public int getSectionSize(String iniSection)
		{ return isSection(iniSection) ? iniBody.get(iniSection).size() : -1; }

	public int getItemPos(String iniSection, String iniItem) {
		if (isItem(iniSection, iniItem)) {
			Iterator<String> it = iniBody.get(iniSection).keySet().iterator();
			for (int n = 0; it.hasNext(); n++) 
				if (it.next().equals(iniItem))
					return n + 1;
		}
		return 0;
	}

	public String getItemAtPos(String iniSection, int pos) {
		if (!iniBody.get(iniSection).isEmpty()) {
			List<String> list = getItemList(iniSection);
			if (pos - 1 < 0 || pos - 1 >= list.size())
				return null;
			return list.get(pos - 1);
		}
		return null;
	}

	public Boolean isItem(String iniSection, String iniItem)
		{ return isSection(iniSection) ? iniBody.get(iniSection).containsKey(iniItem) : false; }

	public void clearSection(String iniSection) {
		iniBody.get(iniSection).clear();
		changedTime = System.currentTimeMillis();
	}

	public void clearFile() {
		iniBody.clear();
		changedTime = System.currentTimeMillis();
	}

	public void closeFile() {
		openedIniFiles.remove(fileName);
		clearFile();
	}
	
	public static void closeAllOpenedIniFiles() {
		for (IniFile ini : openedIniFiles.values())
			ini.clearFile();
		openedIniFiles.clear();
	}

	public List<String> getSectionList() {
		List<String> list = new ArrayList<String>();
		if (!iniBody.isEmpty()) {
			Iterator<String> it = iniBody.keySet().iterator();
			String item;
			while (it.hasNext())
			  if ((item = it.next()) != null && !item.isEmpty())
			  	list.add(item);
		}
		return list;
	}

	public List<String> getItemList(String iniSection) {
		List<String> list = new ArrayList<String>();
		if (isSection(iniSection) && !iniBody.get(iniSection).isEmpty()) {
			Iterator<String> it = iniBody.get(iniSection).keySet().iterator();
			String item;
			while (it.hasNext())
			  if ((item = it.next()) != null && !item.isEmpty())
			  	list.add(item);
		}
		return list;
	}

	public void renameFileTo(String newFileName) {
		File file = new File(fileName);
		if (file.exists() && !file.delete())
			throw new RuntimeException("Não foi possível renomear o arquivo " + fileName);
		newFileName = file.getParent().toString() + "\\" + newFileName;
		File file2 = new File(newFileName);
		if (file2.exists() && !file2.delete())
			throw new RuntimeException("Não foi possível renomear o arquivo " + fileName);
		openedIniFiles.put(newFileName, this);
		openedIniFiles.remove(fileName);
		fileName = newFileName;
		saveToDisk();
	}
	
	/**
	 * Retorna um LinkedHashMap, contendo vários itens=valores provindos de uma
	 * string no formato: {ITEM=VAL}{ITEM2=VAL}{ITEM3=VAL}
	 * @enclosers - Caracteres que isolam o grupo de ITEM=VAL.
	 * 							 Exemplo: Se o formato for {ITEM=VAL} use no {@code enclosers} "{}"
	 * 							 ou simplesmente nem especifique o {@code enclosers}, pois é passado
	 * 							 o valor "{}" por padrão.
	 */
	public static LinkedHashMap<String, String> subItemStringToLinkedHashMap(String val, String enclosers) {
		LinkedHashMap<String, String> subItems = new LinkedHashMap<>();
		Pattern pattern = Pattern.compile("(\\" + enclosers.substring(0, 1) + "[^" + enclosers.substring(1, 2) + "]+\\" + enclosers.substring(1, 2) + ")", Pattern.CASE_INSENSITIVE);
//		Pattern pattern = Pattern.compile("(\\" + enclosers.substring(0, 1) + "[^\\" + enclosers.substring(1, 2) + ".]+\\" + enclosers.substring(1, 2) + ")", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(val);
		StringBuilder result;
		while (matcher.find()) {
			String[] split = matcher.group(0).substring(1, matcher.group(0).length() - 1).split("=");
			val = val.replace(matcher.group(0), "");
			matcher = pattern.matcher(val);
			result = new StringBuilder();
			if (split.length > 1) {
				result.append(split[1]);
				for (int n = 2; n < split.length; n++) {
					result.append("=");
					result.append(split[n]);
				}
			}
			subItems.put(split[0], result.toString());
		}
		return subItems;
	}

	public static LinkedHashMap<String, String> subItemStringToLinkedHashMap(String val)
		{ return subItemStringToLinkedHashMap(val, "{}"); }
	
	/** Converte uma string no formato {ITEM=VAL}{ITEM2=VAL}{ITEM3=VAL} em um
	 *  LinkedHashMap<String, String> contendo esses itens e seus valores respectivamente.
	 * @enclosers - Caracteres que isolam o grupo de ITEM=VAL.
	 * 							 Exemplo: Se o formato for {ITEM=VAL} use no {@code enclosers} "{}"
	 * 							 ou simplesmente nem especifique o {@code enclosers}, pois é passado
	 * 							 o valor "{}" por padrão.
	 */
	
	public static String linkedHashMapToSubItemString(LinkedHashMap<String, String> map, String enclosers) {
		StringBuilder str = new StringBuilder();
		map.forEach((k, v) -> {
			str.append(enclosers.substring(0, 1));
			str.append(k);
			str.append("=");
			str.append(v);
			str.append(enclosers.substring(1, 2));
		});
		return str.toString();
	}
	
	public static String linkedHashMapToSubItemString(LinkedHashMap<String, String> map)
		{ return linkedHashMapToSubItemString(map, "{}"); }

}