package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MyFiles {
	
	public static void copyAllFiles(String fromPath, String toPath) throws IOException {
		fromPath = (new File(fromPath)).getAbsolutePath().replace("\\.\\", "\\");
		toPath = (new File(toPath)).getAbsolutePath().replace("\\.\\", "\\");
		List<File> files = FindFile.findFile(fromPath, "*", 0);
		for (File file : files) {
			File f = new File(file.getAbsolutePath().replace(fromPath, toPath));
			mkdirs(f.getAbsolutePath());
			f.delete();
			copy(file.getAbsolutePath(), f.getAbsolutePath());
		}
	}
	
	public static void moveAllFiles(String fromPath, String toPath) {
		try {
			copyAllFiles(fromPath, toPath);
			deleteAllDirsAndFiles(fromPath);
		}
		catch (IOException e)
			{ throw new RuntimeException(e.getMessage()); }
	}

	public static void deleteAllDirsAndFiles(String path) {
		List<File> files = FindFile.findFile(path, "*", 0);
		for (File file : files)
			file.delete();
		do {
			files = FindFile.findDir(path, "*", 0);
			for (File file : files)
				file.delete();
		}
		while (!files.isEmpty());
		(new File(path)).delete();
	}
	
	public static void mkdirs(String absoluteFilePath)
		{ (new File(new File(absoluteFilePath).getParent() + "\\")).mkdirs(); }
	
	public static void copy(String fromPath, String toPath) throws IOException
		{ java.nio.file.Files.copy((new File(fromPath)).toPath(), (new File(toPath)).toPath()); }
	
	public static Boolean renameFile(String oldFileName, String newFileName) {
		File oldFile = new File(oldFileName);
		File newFile = new File(newFileName);
		try {
			if (newFile.exists())
			   throw new IOException("file exists");
		}
		catch (Exception e) {}
		return oldFile.renameTo(newFile);
	}

	public static List<String> readAllLinesFromFile(String filePath) {
		if (!new File(filePath).exists()) return null;
		List<String> result = new ArrayList<>();
		Path path = Paths.get(filePath);
		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line = br.readLine();
			while (line != null) {
				result.add(line);
				line = br.readLine();
			}
		}
		catch (IOException e) 
			{ throw new RuntimeException("Error trying to read file: " + filePath); }
		return result;
	}

	public static void writeAllLinesOnFile(List<String> list, String filePath) {
		Path path = Paths.get(filePath);
		try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			for (String s : list) {
				bw.write(s);
				bw.newLine();
			}
		}
		catch (IOException e) 
			{ throw new RuntimeException("Error trying to write file: " + filePath); }
	}

}
