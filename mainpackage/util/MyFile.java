package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public abstract class MyFile {
	
	private static List<File> selectFilesJavaFX(Stage stage, String initialFolder, List<ExtensionFilter> extensionFilters, String dialogTitle, Boolean multiSel) {
	  FileChooser fileChooser = new FileChooser();
	  fileChooser.setTitle(dialogTitle);
	  if (initialFolder != null)
	  	fileChooser.setInitialDirectory(new File(initialFolder));
	  if (extensionFilters != null)
	  	for (ExtensionFilter ef : extensionFilters)
	  		fileChooser.getExtensionFilters().add(ef);
	  if (multiSel) {
			List<File> files = fileChooser.showOpenMultipleDialog(stage);
			return files != null ? files : null;
	  }
	  File file = fileChooser.showOpenDialog(stage);
		return file != null ? Arrays.asList(file) : null;
	}

	public static File selectDirectoryJavaFX(Stage stage, String initialFolder, String dialogTitle) {
	  DirectoryChooser dirChooser = new DirectoryChooser();
	  dirChooser.setTitle(dialogTitle);
	  if (initialFolder != null)
	  	dirChooser.setInitialDirectory(new File(initialFolder));
	  File file = dirChooser.showDialog(stage);
		return file;
	}

	public static List<File> selectFilesJavaFX(Stage stage, String initialFolder, List<ExtensionFilter> extensionFilters, String dialogTitle) {
		return selectFilesJavaFX(stage, initialFolder, extensionFilters, dialogTitle, true);
	}
	
	public static File selectFileJavaFX(Stage stage, String initialFolder, ExtensionFilter extensionFilter, String dialogTitle) {
		List<File> files = selectFilesJavaFX(stage, initialFolder, Arrays.asList(extensionFilter), dialogTitle, false);
		return files != null ? files.get(0) : null;
	}
	
	public static File selectFileJavaFX(Stage stage, String initialFolder, String dialogTitle) {
		return selectFileJavaFX(stage, initialFolder, new FileChooser.ExtensionFilter("Todos os arquivos", "*.*"), dialogTitle);
	}

	public static List<File> selectFilesJavaFX(Stage stage, List<ExtensionFilter> extensionFilters, String dialogTitle) {
		return selectFilesJavaFX(stage, null, extensionFilters, dialogTitle, true);
	}
	
	public static File selectFileJavaFX(Stage stage, ExtensionFilter extensionFilter, String dialogTitle) {
		List<File> files = selectFilesJavaFX(stage, null, Arrays.asList(extensionFilter), dialogTitle, false);
		return files != null ? files.get(0) : null;
	}
	
	public static File selectFileJavaFX(Stage stage, String dialogTitle) {
		return selectFileJavaFX(stage, new FileChooser.ExtensionFilter("Todos os arquivos", "*.*"), dialogTitle);
	}
	
	public static File selectDirectoryJavaFX(Stage stage, String dialogTitle) {
		return selectDirectoryJavaFX(stage, null, dialogTitle);
	}	
	
	private static List<File> selectFilesAndDirs(String initialFolder, String dialogTitle, Boolean listFiles, Boolean listDirs, Boolean multiSel) {
		File initPath = initialFolder == null ? FileSystemView.getFileSystemView().getHomeDirectory() : new File(initialFolder);
    JFileChooser filesChooser = new JFileChooser(initPath);
    filesChooser.setFileSelectionMode(listFiles && listDirs ? JFileChooser.FILES_AND_DIRECTORIES :
    																	 listFiles ? JFileChooser.FILES_ONLY : JFileChooser.DIRECTORIES_ONLY);
    filesChooser.setMultiSelectionEnabled(multiSel);
    filesChooser.setDialogTitle(dialogTitle);
    if (filesChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
    	if (multiSel)
    		return new ArrayList<>(Arrays.asList(filesChooser.getSelectedFiles()));
  		return new ArrayList<>(Arrays.asList(filesChooser.getSelectedFile()));
    }
    return null;
	}
	
	public static List<File> selectFiles(String initialFolder, String dialogTitle, Boolean multiSel)
		{ return selectFilesAndDirs(initialFolder, dialogTitle, true, false, multiSel); }
	
	public static List<File> selectFiles(String dialogTitle, Boolean multiSel)
		{ return selectFiles(null, dialogTitle, multiSel); }

	public static File selectFile(String dialogTitle, String initialFolder) {
		List<File> files = selectFiles(initialFolder, dialogTitle, false);
		return files == null ? null : files.get(0);
	}

	public static File selectFile(String dialogTitle) {
		List<File> files = selectFiles(null, dialogTitle, false);
		return files == null ? null : files.get(0);
	}

	public static List<File> selectDirs(String dialogTitle, String initialFolder, Boolean multiSel)
		{ return selectFilesAndDirs(initialFolder, dialogTitle, false, true, multiSel); }
	
	public static List<File> selectDirs(String dialogTitle, Boolean multiSel)
		{ return selectDirs(null, dialogTitle, multiSel); }
	
	public static File selectDir(String dialogTitle, String initialFolder) {
		List<File> dirs = selectDirs(dialogTitle, dialogTitle, false);
		return dirs == null ? null : dirs.get(0);
	}

	public static File selectDir(String dialogTitle) {
		List<File> dirs = selectDirs(null, dialogTitle, false);
		return dirs == null ? null : dirs.get(0);
	}
	
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
	
	public static void copy(String sourcePath, String destinationPath) {
		copy(sourcePath, destinationPath, false);
	}

	public static void copy(String sourcePath, String destinationPath, boolean replaceOldFile) {
		Path source = Path.of(sourcePath);
		Path destination = Path.of(destinationPath);
		try {
			if (replaceOldFile || !Files.exists(destination)) {
				if (!Files.exists(destination.getParent())) {
			    Files.createDirectories(destination.getParent());
			}				Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
			}
			else
				throw new RuntimeException("O arquivo de destino já existe");
		}
		catch (IOException e) {
			throw new RuntimeException("Erro ao copiar o arquivo: " + e.getMessage());
		}
	}
	
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

	public static String removeInvisibleChars(String string) // Para remover caracteres especiais que podem vir ao ler strings de um arquivo de texto, que apesar de não ser visiveis, contam como caractere e podem bugar o codigo baseado em ler o caractere de um determinado index
		{ return string == null ? null : string.isBlank() ? "" : string.replaceAll("[\\p{C}\\p{Z}&&[^\u0020]]", ""); }
	
	public static List<String> readAllLinesFromFile(String filePath) {
		if (!new File(filePath).exists()) return null;
		List<String> result = new ArrayList<>();
		Path path = Paths.get(filePath);
		try (BufferedReader br = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
			String line = removeInvisibleChars(br.readLine());
      while (line != null) {
				result.add(line);
				line = br.readLine();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error trying to read file: " + filePath);
		}
		return result;
	}

	public static void writeAllLinesOnFile(List<String> list, String filePath) {
		Path path = Paths.get(filePath);
		try (BufferedWriter bw = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			for (String s : list)
				if (s != null) {
					bw.write(s);
					bw.newLine();
				}
		}
		catch (IOException e) 
			{ throw new RuntimeException("Error trying to write file: " + filePath); }
	}

}
