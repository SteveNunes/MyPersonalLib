package util;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Backup {
	
  private int maxBackupSize = 5;
  private final String BACKUP_DIR;
  private final String FILES_TO_BACKUP_DIR;
  private IOException ioException;
  
  public Backup(String backupDir, String filesToBackupDir) {
  	BACKUP_DIR = backupDir;
  	FILES_TO_BACKUP_DIR = filesToBackupDir;
  }
  
  private void throwIOException(String exceptionStr) {
		ioException = new IOException(exceptionStr);
		throw new RuntimeException(exceptionStr);
  }
  
  public IOException getLastException()
  	{ return ioException; }
  
	public int getMaxBackupSize()
		{ return maxBackupSize; }

	public void setMaxBackupSize(int maxLimit) {
		maxBackupSize = maxLimit;
		List<File> dirs = FindFile.findDir(BACKUP_DIR);
		dirs.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
		while (dirs.size() > maxLimit) {
			File dir = dirs.get(0);
			MyFiles.deleteAllDirsAndFiles(dir.getAbsolutePath());
			dirs.remove(0);
		}
	}

	public void backupLoad(String backupDir) {
		ioException = null;
		if (!(new File(BACKUP_DIR + backupDir)).exists())
			throwIOException(backupDir + " -> Backup não encontrado.");
		backupDir = BACKUP_DIR + backupDir + "\\";
		String tempDir = ".\\src\\BackupTemp\\";
		try
			{ MyFiles.copyAllFiles(FILES_TO_BACKUP_DIR, tempDir); }
		catch (IOException ex) {
			MyFiles.deleteAllDirsAndFiles(tempDir);
			throwIOException("Falha ao carregar backup.");
		}
		try {
			MyFiles.copyAllFiles(backupDir, FILES_TO_BACKUP_DIR);
			MyFiles.deleteAllDirsAndFiles(tempDir);
		}
		catch (IOException ex) {
			MyFiles.deleteAllDirsAndFiles(FILES_TO_BACKUP_DIR);
			try {
				MyFiles.copyAllFiles(tempDir, FILES_TO_BACKUP_DIR);
				MyFiles.deleteAllDirsAndFiles(tempDir);
			}
			catch (IOException ex2)
				{ throwIOException("Falha ao carregar backup."); }
			throwIOException("Falha ao carregar backup.");
		}
	}

	public void backupSave(String backupDir) {
		ioException = null;
		backupDir = BACKUP_DIR + backupDir + "\\";
		try {
			MyFiles.deleteAllDirsAndFiles(backupDir);
			MyFiles.copyAllFiles(FILES_TO_BACKUP_DIR, backupDir);
		}
		catch (IOException ex) {
			MyFiles.deleteAllDirsAndFiles(backupDir);
			throwIOException("Falha ao salvar backup.");
		}
	}	

}
