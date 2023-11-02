package gui.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import util.Backup;
import util.FindFile;
import util.MyFile;

public class BackupMenu {

  private Menu menu;
  private Menu menuCarregarBackup;
  private MenuItem menuItemSalvarBackup;
	private Menu menuLimiteDeBackups;
  private MenuItem menuItemLimparBackup;
  
  private Consumer<?> consumerAntesDeSalvarBackup;
  private Consumer<?> consumerAposCarregarBackup;
  private Consumer<?> consumerAposSalvarBackup;
  private Consumer<?> consumerAposLimparBackup;
  private Consumer<?> consumerAposDefinirLimiteDeBackup;
  private Consumer<?> consumerAposFalharCarregarBackup;
  private Consumer<?> consumerAposFalharSalvarBackup;
  private Consumer<?> consumerAposFalharLimparBackup;
  private Consumer<?> consumerAposFalharDefinirLimiteDeBackup;
  
  private Backup backup;
  
  private String backupDir;
  private String filesToBackupDir;
  private int maxBackupSize = 15;
  
  public BackupMenu(String backupDir, String filesToBackupDir) {
  	File dir = new File(filesToBackupDir);
  	if (!dir.exists())
  		throw new RuntimeException(filesToBackupDir + " -> Invalid directory.");
  	
  	this.backupDir = backupDir;
  	this.filesToBackupDir = filesToBackupDir;
  	backup = new Backup(backupDir, filesToBackupDir);
  	
  	menu = new Menu("Backup");
  	menuCarregarBackup = new Menu("Carregar");
  	menuItemSalvarBackup = new MenuItem("Salvar");
  	Menu menuOpcoes = new Menu("Opções");
  	menuLimiteDeBackups = new Menu("Limite de backups");
  	menuItemLimparBackup = new MenuItem("Limpar backup");
  	menuOpcoes.getItems().addAll(Arrays.asList(menuLimiteDeBackups, menuItemLimparBackup));
  	menu.getItems().addAll(Arrays.asList(menuCarregarBackup, menuItemSalvarBackup, new SeparatorMenuItem(), menuOpcoes));
  	
  	dir = new File(backupDir);
  	if (!dir.exists())
  		dir.mkdirs();
  	
  	addMenuListeners();
  	updateBackupMenuList();
  }
  
	public void setConsumerAntesDeSalvarBackup(Consumer<String> consumerAntesDeSalvarBackup)
		{ this.consumerAntesDeSalvarBackup = consumerAntesDeSalvarBackup; }

	public void setConsumerAposCarregarBackup(Consumer<String> consumerAposCarregarBackup)
		{ this.consumerAposCarregarBackup = consumerAposCarregarBackup; }

	public void setConsumerAposSalvarBackup(Consumer<String> consumerAposSalvarBackup)
		{ this.consumerAposSalvarBackup = consumerAposSalvarBackup; }

	public void setConsumerAposLimparBackup(Consumer<String> consumerAposLimparBackup)
		{ this.consumerAposLimparBackup = consumerAposLimparBackup; }

	public void setConsumerAposDefinirLimiteDeBackup(Consumer<String> consumerAposDefinirLimiteDeBackup)
		{ this.consumerAposDefinirLimiteDeBackup = consumerAposDefinirLimiteDeBackup; }

	public void setConsumerAposFalharLimparBackup(Consumer<String> consumerAposFalharLimparBackup)
		{ this.consumerAposFalharLimparBackup = consumerAposFalharLimparBackup; }
	
	public void setConsumerAposFalharDefinirLimiteDeBackup(Consumer<String> consumerAposFalharDefinirLimiteDeBackup)
		{ this.consumerAposFalharDefinirLimiteDeBackup = consumerAposFalharDefinirLimiteDeBackup; }
	
	public void setConsumerAposFalharCarregarBackup(Consumer<String> consumerAposFalharCarregarBackup)
		{ this.consumerAposFalharCarregarBackup = consumerAposFalharCarregarBackup; }
	
	public void setConsumerAposFalharSalvarBackup(Consumer<String> consumerAposFalharSalvarBackup)
		{ this.consumerAposFalharSalvarBackup = consumerAposFalharSalvarBackup; }

	public String getBackupDir()
		{ return backupDir; }

	public void setBackupDir(String backupDir)
		{ this.backupDir = backupDir; }

	public String getFilesToBackupDir()
		{ return filesToBackupDir; }

	public void setFilesToBackupDir(String filesToBackupDir)
		{ this.filesToBackupDir = filesToBackupDir; }

  public Menu getMenu()
		{ return menu; }

	public int getMaxBackupSize()
		{ return maxBackupSize; }
	
	public void setMaxBackupSize(int maxBackupSize) {
		if (maxBackupSize < 5 || maxBackupSize > 60 || maxBackupSize % 5 != 0) {
			if (consumerAposFalharDefinirLimiteDeBackup != null)
				consumerAposFalharDefinirLimiteDeBackup.accept(null);
			return;
		}
		this.maxBackupSize = maxBackupSize;
		List<File> dirs = FindFile.findDir(backupDir);
		dirs.sort((f1, f2) -> f1.getName().compareTo(f2.getName()));
		while (dirs.size() > maxBackupSize) {
			MyFile.deleteAllDirsAndFiles(dirs.get(0).getAbsolutePath());
			dirs.remove(0);
		}
		updateBackupMenuList();
		if (consumerAposDefinirLimiteDeBackup != null)
			consumerAposDefinirLimiteDeBackup.accept(null);
	}

	private void addMenuListeners() {
  	menuItemLimparBackup.setOnAction(e -> {
  		try {
				MyFile.deleteAllDirsAndFiles(backupDir);
				updateBackupMenuList();
				if (consumerAposLimparBackup != null)
					consumerAposLimparBackup.accept(null);
  		}
  		catch (Exception ex) {
  			if (consumerAposFalharLimparBackup != null)
  				consumerAposFalharLimparBackup.accept(null);
  		}
		});
  }
	
	private void backupSave(String date, Boolean silent) {
		try {
			if (!silent && consumerAntesDeSalvarBackup != null)
				consumerAntesDeSalvarBackup.accept(null);
			backup.backupSave(date);
			if (!silent && consumerAposSalvarBackup != null)
				consumerAposSalvarBackup.accept(null);
		}
		catch (Exception ex) {
			if (consumerAposFalharSalvarBackup != null)
				consumerAposFalharSalvarBackup.accept(null);
		}
	}
  
	private void updateBackupMenuList() {
		menuCarregarBackup.getItems().clear();
		String sdf = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
		if (!(new File(backupDir + sdf + "\\")).exists())
			backupSave(sdf, true);
		menuItemSalvarBackup.setOnAction(e -> backupSave(sdf, false));
		List<File> dirs = FindFile.findDir(backupDir);
		for (File f : dirs) {
			MenuItem menuItem = new MenuItem(f.getName());
			menuItem.setOnAction(e -> {
				try {
					backup.backupLoad(f.getName());
					if (consumerAposCarregarBackup != null)
						consumerAposCarregarBackup.accept(null);
				}
				catch (Exception ex) {
					if (consumerAposFalharCarregarBackup != null)
						consumerAposFalharCarregarBackup.accept(null);
				}
			});
			menuCarregarBackup.getItems().add(menuItem);
		}
		menuCarregarBackup.setDisable(dirs.isEmpty());
		updateMenuLimiteDeBackups();
	}

	private void updateMenuLimiteDeBackups() {
		menuLimiteDeBackups.getItems().clear();
		for (int n = 5; n <= 60; n += 5) {
			CheckMenuItem menuItem = new CheckMenuItem("" + n + " items");
			final int x = n;
			menuItem.setOnAction(e -> {
				setMaxBackupSize(x);
				updateMenuLimiteDeBackups();
			});
			menuItem.setSelected(maxBackupSize == n);
			menuLimiteDeBackups.getItems().add(menuItem);			
		}
	}

}
