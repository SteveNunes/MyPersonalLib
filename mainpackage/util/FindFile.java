package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class FindFile {

	private static int totalFound;

	public static List<File> findDir(String path, String filter, int deep)
		{ return findFD(path, filter, deep, FindFileEnum.DIR); }

	public static List<File> findFile(String path, String filter, int deep)
		{ return findFD(path, filter, deep, FindFileEnum.FILE); }

	public static List<File> findDirAndFile(String path, String filter, int deep)
		{ return findFD(path, filter, deep, FindFileEnum.ALL); }

	public static List<File> findDir(String path, String filter)
		{ return findDir(path, filter, 1); }

	public static List<File> findFile(String path, String filter)
		{ return findFile(path, filter, 1); }

	public static List<File> findDirAndFile(String path, String filter)
		{ return findDirAndFile(path, filter, 1); }

	public static List<File> findDir(String path)
		{ return findDir(path, "", 1); }

	public static List<File> findFile(String path)
		{ return findFile(path, "", 1); }

	public static List<File> findDirAndFile(String path)
		{ return findDirAndFile(path, "", 1); }

	private static Boolean filter(String file, String filter)
		{ return (filter.length() == 0 || file.matches(("\\Q" + filter + "\\E").replace("*", "\\E.*\\Q"))); }

	private static List<File> findFD(String path, String filter, int deep, FindFileEnum fft) {
		List<File> list = findAny(path, filter, deep, fft);
		totalFound = list.size();
		return list;
	}

	private static List<File> findAny(String path, String filter, int deep, FindFileEnum fft) {
		path = path.replace('\\', '/');
		filter = filter.replace('\\', '/');
		File[] dirs = new File(path).listFiles(File::isDirectory), temp1 = null;
		List<File> result = new ArrayList<>(), temp2 = null;
		if (fft == FindFileEnum.ALL || fft == FindFileEnum.DIR)
		  for (int n = 0; dirs != null && n < dirs.length; n++)
		    if (filter(dirs[n].getName(), filter)) 
		    	result.add(dirs[n]);
		if (fft == FindFileEnum.ALL || fft == FindFileEnum.FILE) {
			temp1 = new File(path).listFiles(File::isFile);
			for (int n = 0; temp1 != null && n < temp1.length; n++)
			  if (filter(temp1[n].getName(), filter)) 
			  	result.add(temp1[n]);
		}
		if (--deep != 0) {
			for (int n = 0; dirs != null && n < dirs.length; n++) {
				temp2 = findAny(dirs[n].toString() + "/", filter, deep, fft);
				for (File f : temp2) 
					result.add(f);
			}
		}
		return result;
	}

	public static int totalFounds() { return totalFound; }

}

enum FindFileEnum { FILE, DIR, ALL; }
