package util;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe para ler/escrever arquivos INI. Preserva comentários (linhas iniciadas
 * por ';' ou '#', comentários inline após o valor) e a disposição original do
 * arquivo tanto quanto possível.
 */
public class IniFile {

	private Path file;
	private String fileName, lastReadVal = null;
	private List<String> fileBuffer;
	private LinkedHashMap<String, LinkedHashMap<String, String>> iniBody;
	private boolean wasModified;
	static LinkedHashMap<String, IniFile> openedIniFiles = new LinkedHashMap<>();

	static {
		Misc.addShutdownEvent(() -> IniFile.closeAllOpenedIniFiles());
	}

	private void runSaveTimer() {
		wasModified = true;
		Timer.createTimer("IniFileSaveToDisk@" + hashCode(), Duration.ofSeconds(1), () -> saveToDisk());
	}

	private void stopSaveTimer() {
		wasModified = false;
		Timer.stopTimer("IniFileSaveToDisk@" + hashCode());
	}

	private static void saveAllFilesToDisk() {
		for (IniFile iniFile : openedIniFiles.values())
			if (iniFile.wasModified)
				iniFile.saveToDisk();
	}

	void saveToDisk() {
		synchronized (this) {
			if (wasModified && openedIniFiles.containsKey(fileName)) {
				wasModified = false;
				updateFileBuffer();
				MyFile.writeAllLinesOnFile(fileBuffer, fileName);
			}
		}
	}

	private IniFile(String fileName) {
		this.fileName = fileName;
		wasModified = false;
		file = Paths.get(fileName);
		loadIniFromDisk(fileName);
		openedIniFiles.put(fileName, this);
		stopSaveTimer();
	}

	/**
	 * Retorna (ou cria) uma instância para o arquivo informado.
	 *
	 * @param fileName caminho do arquivo INI
	 * @return instância de IniFile associada ao caminho
	 */
	public static IniFile getNewIniFileInstance(String fileName) {
		if (!openedIniFiles.containsKey(fileName))
			return new IniFile(fileName);
		return openedIniFiles.get(fileName);
	}

	/**
	 * Lista de arquivos INI abertos.
	 *
	 * @return colessão com instâncias abertas
	 */
	public static Collection<IniFile> getOpenedIniFilesList() {
		return openedIniFiles.values();
	}

	/**
	 * Retorna o Path do arquivo associado.
	 *
	 * @return Path do arquivo
	 */
	public Path getFilePath() {
		return file;
	}

	/**
	 * Retorna o próximo nome numérico livre em uma sessão (1, 2, 3...).
	 *
	 * @param section nome da sessão
	 * @return próximo índice numérico livre como string
	 */
	public String getNextFreeNumericItem(String section) {
		int n = 1;
		for (; itemExists(section, "" + n); n++);
		return "" + n;
	}

	/**
	 * Remove o item numérico informado e reordena os itens numéricos (1..N).
	 * Preserva outros itens intactos.
	 *
	 * @param section sessão
	 * @param item    item a remover
	 */
	public void removeNumericItemAndReorderSection(String section, String item) {
		if (!sectionExists(section) || !itemExists(section, item))
			return;
		Map<String, String> newSection = new HashMap<>();
		int n = 1;
		for (String item2 : getItemList(section))
			if (!item.equals(item2))
				newSection.put("" + (n++), read(section, item2));
		iniBody.put(section, new LinkedHashMap<>(newSection));
		runSaveTimer();
	}

	/* --- Helpers para reconhecer linhas --- */

	private static Boolean stringIsSection(String s) {
		int i = s != null ? s.indexOf("]") : -1;
		return s != null && !s.isBlank() && s.charAt(0) == '[' && i > 1;
	}

	private static Boolean stringIsItem(String s) {
		return s != null && !s.isEmpty() && s.charAt(0) != '=' && s.contains("=");
	}

	private static String getSectionFromString(String s) {
		return s.split("]")[0].substring(1);
	}

	/**
	 * Carrega o INI do disco. Mantém fileBuffer (linhas originais) para preservar
	 * comentários e formato; popula iniBody com os valores (sem comentários
	 * inline).
	 *
	 * @param fileName caminho do arquivo
	 */
	public void loadIniFromDisk(String fileName) {
		iniBody = new LinkedHashMap<>();
		if (!fileName.isBlank()) {
			if (Files.exists(file)) {
				fileBuffer = MyFile.readAllLinesFromFile(fileName);
				String section = "";
				for (String s : fileBuffer) {
					// Ignora linhas vazias e comentários puros
					if (s.isBlank() || s.trim().startsWith(";") || s.trim().startsWith("#"))
						continue;

					if (stringIsSection(s)) {
						section = getSectionFromString(s);
						iniBody.put(section, new LinkedHashMap<>());
					}
					else if (!section.isBlank() && stringIsItem(s)) {
						String[] split = s.split("=", 2);
						String item = split[0];
						String val = split.length > 1 ? split[1].trim() : "";
						write(section, item, val);
					}
				}
			}
			else {
				fileBuffer = new ArrayList<>();
			}
			stopSaveTimer();
		}
	}

	/**
	 * Recarrega o arquivo associado.
	 */
	public void loadIniFromDisk() {
		loadIniFromDisk(fileName);
	}

	/**
	 * Insere itens que não existiam no arquivo físico (novas seções/itens). Usado
	 * durante a escrita final para garantir que o conteúdo de iniBody apareça no
	 * arquivo mesmo que não existisse antes.
	 *
	 * @param section    sessão atual (nome)
	 * @param fileBuffer buffer em construção
	 * @param addeds     mapa de seções já adicionadas (para evitar duplicidade)
	 */
	private void insertMissingItems(String section, List<String> fileBuffer, Map<String, List<String>> addeds) {
		if (!section.isBlank()) {
			if (!addeds.containsKey(section)) {
				if (!fileBuffer.isEmpty() && !fileBuffer.get(fileBuffer.size() - 1).isBlank())
					fileBuffer.add("");
				fileBuffer.add("[" + section + "]");
				addeds.put(section, new ArrayList<>());
			}
			for (String item : getItemList(section))
				if (!addeds.get(section).contains(item)) {
					fileBuffer.add(item + "=" + read(section, item));
					addeds.get(section).add(item);
				}
		}
		fileBuffer.add("");
	}

	/**
	 * Atualiza fileBuffer a partir do fileBuffer original preservando linhas que
	 * não são items (comentários, espaço em branco, cabeçalhos). Para linhas que
	 * são items existentes, substitui somente a porção do valor (preserva
	 * comentários inline e espaçamentos originais).
	 */
	private void updateFileBuffer() {
		Map<String, List<String>> addeds = new HashMap<>();
		List<String> newFileBuffer = new ArrayList<>();
		String section = "";
		for (int l = 0; l < fileBuffer.size(); l++) {
			String line = fileBuffer.get(l);
			if (!section.isBlank() && stringIsSection(line) && !section.equals(getSectionFromString(line))) {
				// Se estava com um SECTION aberto, e encontrou um novo SECTION, adiciona itens
				// novos ao final do SECTION atual
				while (!newFileBuffer.isEmpty() && newFileBuffer.get(newFileBuffer.size() - 1).isBlank())
					newFileBuffer.remove(newFileBuffer.size() - 1);
				insertMissingItems(section, newFileBuffer, addeds);
			}
			if (stringIsSection(line)) {
				section = getSectionFromString(line);
				if (sectionExists(section)) {
					newFileBuffer.add(line);
					addeds.put(section, new ArrayList<>());
				}
				else
					section = "";
			}
			else if (stringIsItem(line)) {
				if (!section.isBlank()) {
					String[] split = line.split("=", 2);
					String item = split[0];
					if (itemExists(section, item)) {
						String value = read(section, item);
						newFileBuffer.add(item + "=" + value);
						addeds.get(section).add(item);
					}
				}
			}
			else if (!line.isBlank() || !section.isBlank() || addeds.isEmpty())
				newFileBuffer.add(line);
		}
		if (sectionExists(section))
			insertMissingItems(section, newFileBuffer, addeds);
		for (String sec : getSectionList())
			if (!getItemList(sec).isEmpty() && !addeds.containsKey(sec))
				insertMissingItems(sec, newFileBuffer, addeds);
		// Remover linhas em branco no inicio e final do arquivo, caso existam
		while (!newFileBuffer.isEmpty() && newFileBuffer.get(0).isBlank())
			newFileBuffer.remove(0);
		while (!newFileBuffer.isEmpty() && newFileBuffer.get(newFileBuffer.size() - 1).isBlank())
			newFileBuffer.remove(newFileBuffer.size() - 1);
		fileBuffer = new ArrayList<>(newFileBuffer);
	}
	
	/**
	 * Escreve/atualiza um valor (string) em uma sessão/item.
	 *
	 * @param iniSection	Sessão
	 * @param iniItem	Item
	 * @param value Valor
	 */
	public void write(String iniSection, String iniItem, String value) {
		if (!iniBody.containsKey(iniSection))
			iniBody.put(iniSection, new LinkedHashMap<String, String>());
		iniBody.get(iniSection).put(iniItem, value);
		runSaveTimer();
	}

	/**
	 * Escreve um Boolean diretamente no arquivo
	 */
	public void write(String iniSection, String iniItem, Boolean value) {
		write(iniSection, iniItem, "" + value);
	}

	/**
	 * Escreve um número diretamente no arquivo
	 */
	public <T extends Number> void write(String iniSection, String iniItem, T number) {
		write(iniSection, iniItem, "" + number);
	}

	/**
	 * Escreve o conteudo de uma List<T> como texto em um item de uma sessão do arquivo ini
	 * @param section	Sessão onde será escrito os valores
	 * @param item	Item onde será escrito os valores
	 * @param list	List<T> de onde serão obtido os valores á serem escritos
	 * @param separator	Separador entre cada valor
	 */
	public <T> void write(String section, String item, List<T> list, String separator) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (T v : list) {
			sb.append((first ? "" : separator) + v.toString());
			first = false;
		}
		write(section, item, sb.toString());
	}

	public <T extends Number> void write(String section, String item, List<T> numberList) {
		write(section, item, numberList, " ");
	}

	/**
	 * Escreve o conteudo de uma array de <T extends Number> como texto em um item de uma sessão do arquivo ini
	 * @param section	Sessão onde será escrito os valores
	 * @param item	Item onde será escrito os valores
	 * @param array	Array de <T extends Number> de onde serão obtido os valores á serem escritos
	 * @param separator	Separador entre cada valor
	 */
	public <T extends Number> void write(String section, String item, T[] array, String separator) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (T v : array) {
			sb.append((first ? "" : separator) + v.toString());
			first = false;
		}
		write(section, item, sb.toString());
	}

	/**
	 * Sobrecarga de {@code write(String section, String item, Boolean[] numberArray, String separator)} que dispensa informar o separador.	
	 */
	public <T extends Number> void write(String section, String item, T[] array) {
		write(section, item, array, " ");
	}

	/**
	 * Escreve o conteudo de uma array de Boolean como texto em um item de uma sessão do arquivo ini
	 * @param section	Sessão onde será escrito os valores
	 * @param item	Item onde será escrito os valores
	 * @param array	Array de Boolean de onde serão obtido os valores á serem escritos
	 * @param separator	Separador entre cada valor
	 */
	public void write(String section, String item, Boolean[] array, String separator) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Boolean b : array) {
			sb.append((first ? "" : separator) + b.toString());
			first = false;
		}
		write(section, item, sb.toString());
	}

	public void write(String section, String item, Boolean[] numberArray) {
		write(section, item, numberArray, " ");
	}

	/**
	 * Retorna o último valor obtido com a última chamada de read()
	 *
	 * @return último valor obtido com a última chamada de read()
	 */
	public String getLastReadVal() {
		return lastReadVal;
	}

	/**
	 * Sobrecarga de {@code read(String iniSection, String iniItem)} contendo valor padrão
	 * @param defaultValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 */
	public String read(String iniSection, String iniItem, String defaultValue) {
		return read(iniSection, iniItem) != null ? lastReadVal : (lastReadVal = defaultValue);
	}

	/**
	 * Lê o valor de um item em uma sessão
	 *
	 * @param iniSection	Sessão
	 * @param iniItem	Item
	 * @return	valor de um item em uma sessão
	 */
	public String read(String iniSection, String iniItem) {
		if (itemExists(iniSection, iniItem))
			return (lastReadVal = iniBody.get(iniSection).get(iniItem));
		return (lastReadVal = null);
	}

	/**
	 * Retorna um Enum<T> a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param enumClass	.class da classe do Enum para obter seu tipo
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	Enum<T>
	 */
	public <T extends Enum<T>> T readAsEnum(String section, String item, Class<T> enumClass, T defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return T.valueOf(enumClass, val);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public <T extends Enum<T>> T readAsEnum(String section, String item, Class<T> enumClass) {
		return readAsEnum(section, item, enumClass, null);
	}

	/**
	 * Retorna um Boolean a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	Boolean
	 */
	public Boolean readAsBoolean(String section, String item, Boolean defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return Boolean.parseBoolean(val);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public Boolean readAsBoolean(String section, String item) {
		return readAsBoolean(section, item, null);
	}

	/**
	 * Retorna um Byte a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	Byte
	 */
	public Byte readAsByte(String section, String item, Byte defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return Byte.parseByte(val);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public Byte readAsByte(String section, String item) {
		return readAsByte(section, item, null);
	}

	/**
	 * Retorna um Short a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	Short
	 */
	public Short readAsShort(String section, String item, Short defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return Short.parseShort(val);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public Short readAsShort(String section, String item) {
		return readAsShort(section, item, null);
	}

	/**
	 * Retorna um Integer a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	Integer
	 */
	public Integer readAsInteger(String section, String item, Integer defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return Integer.parseInt(val);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public Integer readAsInteger(String section, String item) {
		return readAsInteger(section, item, null);
	}

	/**
	 * Retorna um Long a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	Long
	 */
	public Long readAsLong(String section, String item, Long defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return Long.parseLong(val);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public Long readAsLong(String section, String item) {
		return readAsLong(section, item, null);
	}

	/**
	 * Retorna um Float a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	Float
	 */
	public Float readAsFloat(String section, String item, Float defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return Float.parseFloat(val);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public Float readAsFloat(String section, String item) {
		return readAsFloat(section, item, null);
	}

	/**
	 * Retorna um Double a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	Double
	 */
	public Double readAsDouble(String section, String item, Double defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return Double.parseDouble(val);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public Double readAsDouble(String section, String item) {
		return readAsDouble(section, item, null);
	}

	/**
	 * Retorna um Character a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	Character
	 */
	public Character readAsCharacter(String section, String item, Character defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return val.charAt(0);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public Character readAsCharacter(String section, String item) {
		return readAsCharacter(section, item, null);
	}

	/**
	 * Retorna um BigInteger a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	BigInteger
	 */
	public BigInteger readAsBigInteger(String section, String item, BigInteger defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return new BigInteger(val);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public BigInteger readAsBigInteger(String section, String item) {
		return readAsBigInteger(section, item, null);
	}

	/**
	 * Retorna um BigDecimal a partir de um valor carregado do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnValue	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @return	BigDecimal
	 */
	public BigDecimal readAsBigDecimal(String section, String item, BigDecimal defaultReturnValue) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnValue;
		try {
			return new BigDecimal(val);
		}
		catch (Exception e) {
			return defaultReturnValue;
		}
	}

	public BigDecimal readAsBigDecimal(String section, String item) {
		return readAsBigDecimal(section, item, null);
	}

	/**
	 * Retorna uma array de Enum<T> a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param enumClass	.class da classe do Enum para obter seu tipo
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de Enum<T>
	 */
	public <T extends Enum<T>> List<T> readAsEnumList(String section, String item, Class<T> enumClass, List<T> defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			List<T> list = new ArrayList<>();
			for (int i = 0; i < split.length; i++)
				list.add(T.valueOf(enumClass, split[i]));
			return list;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public <T extends Enum<T>> List<T> readAsEnumList(String section, String item, Class<T> enumClass, List<T> defaultReturnArray) {
		return readAsEnumList(section, item, enumClass, defaultReturnArray, " ");
	}

	public <T extends Enum<T>> List<T> readAsEnumList(String section, String item, Class<T> enumClass, String separator) {
		return readAsEnumList(section, item, enumClass, null, separator);
	}

	public <T extends Enum<T>> List<T> readAsEnumList(String section, String item, Class<T> enumClass) {
		return readAsEnumList(section, item, enumClass, " ");
	}

	/**
	 * Retorna uma array de Boolean a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de Boolean
	 */
	public Boolean[] readAsBooleanArray(String section, String item, Boolean[] defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			Boolean[] array = new Boolean[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Boolean.parseBoolean(split[i]);
			return array;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public Boolean[] readAsBooleanArray(String section, String item, Boolean[] defaultReturnArray) {
		return readAsBooleanArray(section, item, defaultReturnArray, " ");
	}

	public Boolean[] readAsBooleanArray(String section, String item, String separator) {
		return readAsBooleanArray(section, item, null, separator);
	}

	public Boolean[] readAsBooleanArray(String section, String item) {
		return readAsBooleanArray(section, item, " ");
	}

	/**
	 * Retorna uma array de Byte a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de Byte
	 */
	public Byte[] readAsByteArray(String section, String item, Byte[] defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			Byte[] array = new Byte[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Byte.parseByte(split[i]);
			return array;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public Byte[] readAsByteArray(String section, String item, Byte[] defaultReturnArray) {
		return readAsByteArray(section, item, defaultReturnArray, " ");
	}

	public Byte[] readAsByteArray(String section, String item, String separator) {
		return readAsByteArray(section, item, null, separator);
	}

	public Byte[] readAsByteArray(String section, String item) {
		return readAsByteArray(section, item, " ");
	}

	/**
	 * Retorna uma array de Short a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de Short
	 */
	public Short[] readAsShortArray(String section, String item, Short[] defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			Short[] array = new Short[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Short.parseShort(split[i]);
			return array;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public Short[] readAsShortArray(String section, String item, Short[] defaultReturnArray) {
		return readAsShortArray(section, item, defaultReturnArray, " ");
	}

	public Short[] readAsShortArray(String section, String item, String separator) {
		return readAsShortArray(section, item, null, separator);
	}

	public Short[] readAsShortArray(String section, String item) {
		return readAsShortArray(section, item, " ");
	}

	/**
	 * Retorna uma array de Integer a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de Integer
	 */
	public Integer[] readAsIntArray(String section, String item, Integer[] defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			Integer[] array = new Integer[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Integer.parseInt(split[i]);
			return array;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public Integer[] readAsIntArray(String section, String item, Integer[] defaultReturnArray) {
		return readAsIntArray(section, item, defaultReturnArray, " ");
	}

	public Integer[] readAsIntArray(String section, String item, String separator) {
		return readAsIntArray(section, item, null, separator);
	}

	public Integer[] readAsIntArray(String section, String item) {
		return readAsIntArray(section, item, " ");
	}

	/**
	 * Retorna uma array de Long a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de Long
	 */
	public Long[] readAsLongArray(String section, String item, Long[] defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			Long[] array = new Long[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Long.parseLong(split[i]);
			return array;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public Long[] readAsLongArray(String section, String item, Long[] defaultReturnArray) {
		return readAsLongArray(section, item, defaultReturnArray, " ");
	}

	public Long[] readAsLongArray(String section, String item, String separator) {
		return readAsLongArray(section, item, null, separator);
	}

	public Long[] readAsLongArray(String section, String item) {
		return readAsLongArray(section, item, " ");
	}

	/**
	 * Retorna uma array de float a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de float
	 */
	public Float[] readAsFloatArray(String section, String item, Float[] defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			Float[] array = new Float[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Float.parseFloat(split[i]);
			return array;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public Float[] readAsFloatArray(String section, String item, Float[] defaultReturnArray) {
		return readAsFloatArray(section, item, defaultReturnArray, " ");
	}

	public Float[] readAsFloatArray(String section, String item, String separator) {
		return readAsFloatArray(section, item, null, separator);
	}

	public Float[] readAsFloatArray(String section, String item) {
		return readAsFloatArray(section, item, " ");
	}

	/**
	 * Retorna uma array de double a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de double
	 */
	public Double[] readAsDoubleArray(String section, String item, Double[] defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			Double[] array = new Double[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = Double.parseDouble(split[i]);
			return array;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public Double[] readAsDoubleArray(String section, String item, Double[] defaultReturnArray) {
		return readAsDoubleArray(section, item, defaultReturnArray, " ");
	}

	public Double[] readAsDoubleArray(String section, String item, String separator) {
		return readAsDoubleArray(section, item, null, separator);
	}

	public Double[] readAsDoubleArray(String section, String item) {
		return readAsDoubleArray(section, item, " ");
	}

	/**
	 * Retorna uma array de Character a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de Character
	 */
	public Character[] readAsCharArray(String section, String item, Character[] defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			Character[] array = new Character[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = split[i].charAt(0);
			return array;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public Character[] readAsCharArray(String section, String item, Character[] defaultReturnArray) {
		return readAsCharArray(section, item, defaultReturnArray, " ");
	}

	public Character[] readAsCharArray(String section, String item, String separator) {
		return readAsCharArray(section, item, null, separator);
	}

	public Character[] readAsCharArray(String section, String item) {
		return readAsCharArray(section, item, " ");
	}

	/**
	 * Retorna uma array de BigInteger a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de BigInteger
	 */
	public BigInteger[] readAsBigIntegerArray(String section, String item, BigInteger[] defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			BigInteger[] array = new BigInteger[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = new BigInteger(split[i]);
			return array;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public BigInteger[] readAsBigIntegerArray(String section, String item, BigInteger[] defaultReturnArray) {
		return readAsBigIntegerArray(section, item, defaultReturnArray, " ");
	}

	public BigInteger[] readAsBigIntegerArray(String section, String item, String separator) {
		return readAsBigIntegerArray(section, item, null, separator);
	}

	public BigInteger[] readAsBigIntegerArray(String section, String item) {
		return readAsBigIntegerArray(section, item, " ");
	}

	/**
	 * Retorna uma array de BigDecimal a partir de valores carregados do arquivo ini
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @param defaultReturnArray	Valor padrão á ser retornado caso ocorra erro ao tentar carregar o valor informado
	 * @param separator	Separador entre cada valor
	 * @return	array de BigDecimal
	 */
	public BigDecimal[] readAsBigDecimalArray(String section, String item, BigDecimal[] defaultReturnArray, String separator) {
		String val = read(section, item);
		if (val == null)
			return defaultReturnArray;
		try {
			String[] split = val.split(separator);
			BigDecimal[] array = new BigDecimal[split.length];
			for (int i = 0; i < split.length; i++)
				array[i] = new BigDecimal(split[i]);
			return array;
		}
		catch (Exception e) {
			return defaultReturnArray;
		}
	}

	public BigDecimal[] readAsBigDecimalArray(String section, String item, BigDecimal[] defaultReturnArray) {
		return readAsBigDecimalArray(section, item, defaultReturnArray, " ");
	}

	public BigDecimal[] readAsBigDecimalArray(String section, String item, String separator) {
		return readAsBigDecimalArray(section, item, null, separator);
	}

	/**
	 * Retorna uma array de BigDecimal a partir de valores carregados do arquivo ini, separados por espaço
	 * @param section	Sessão do arquivo ini
	 * @param item	Item do arquivo ini
	 * @return	array de BigDecimal
	 */
	public BigDecimal[] readAsBigDecimalArray(String section, String item) {
		return readAsBigDecimalArray(section, item, " ");
	}

	/**
	 * Remove um item se existir.
	 *
	 * @param iniSection	Sessão de onde o item será removido
	 * @param iniItem	Item á ser removido da sessão
	 * @return	Nome do item removido se existir, ou null se não existir
	 */
	public String remove(String iniSection, String iniItem) {
		if (itemExists(iniSection, iniItem)) {
			iniBody.get(iniSection).remove(iniItem);
			runSaveTimer();
			return iniItem;
		}
		return null;
	}

	/**
	 * Remove uma sessão inteira se existir
	 *
	 * @param iniSection Sessão á ser removida
	 * @return	Nome da sessão removida se existir, ou null se não existir
	 */
	public String remove(String iniSection) {
		if (sectionExists(iniSection)) {
			iniBody.remove(iniSection);
			runSaveTimer();
			return iniSection;
		}
		return null;
	}

	/**
	 * @return	Caminho do arquivo atualmente aberto.
	 */
	public String fileName() {
		return fileName;
	}

	/**
	 * @return	o total de sessões no arquivo atual
	 */
	public int getIniSize() {
		return !iniBody.isEmpty() ? iniBody.size() : 0;
	}

	/**
	 * Retorna a posição da sessão no arquivo atual
	 * @param iniSection	Sessão á ser retornado sua posição
	 * @return	a posição da sessão no arquivo atual
	 */
	public int getSectionPos(String iniSection) {
		if (sectionExists(iniSection)) {
			Iterator<String> it = iniBody.keySet().iterator();
			for (int n = 0; it.hasNext(); n++)
				if (it.next().equals(iniSection))
					return n + 1;
		}
		return 0;
	}

	/**
	 * Retorna a sessão da posição especificada
	 * @param coord	Posição da sessão á ser obtida
	 * @return	a sessão da posição especificada
	 */
	public String getSectionAtPos(int coord) {
		if (!iniBody.isEmpty()) {
			List<String> list = getSectionList();
			if (coord < 0 || coord >= list.size())
				return null;
			return list.get(coord);
		}
		return null;
	}

	/**
	 * Verifica se uma sessão existe no arquivo atual
	 * @param iniSection	Sessão á ser verificada
	 * @return {@code true} se a sessão existir no arquivo atual
	 */
	public Boolean sectionExists(String iniSection) {
		return iniBody.containsKey(iniSection);
	}

	/**
	 * Retorna o total de itens em uma sessão
	 * @param iniSection	Sessão á ser obtida o total de itens
	 * @return	o total de itens em uma sessão
	 */
	public int getSectionSize(String iniSection) {
		return sectionExists(iniSection) ? iniBody.get(iniSection).size() : -1;
	}

	/**
	 * Retorna a posição do item na sessão especificada
	 * @param iniSection	Sessão onde está o item desejado
	 * @param iniItem	Item á ser retornado sua posição
	 * @return	a posição do item na sessão especificada
	 */
	public int getItemPos(String iniSection, String iniItem) {
		if (itemExists(iniSection, iniItem)) {
			Iterator<String> it = iniBody.get(iniSection).keySet().iterator();
			for (int n = 0; it.hasNext(); n++)
				if (it.next().equals(iniItem))
					return n + 1;
		}
		return 0;
	}

	/**
	 * Retorna o item da posição especificada dentro de uma sessão
	 * @param iniSection	Sessão á ser obtido o item
	 * @param coord	Posição do item á ser obtido
	 * @return	o item na posição especificada
	 */
	public String getItemAtPos(String iniSection, int coord) {
		if (sectionExists(iniSection) && !iniBody.get(iniSection).isEmpty()) {
			List<String> list = getItemList(iniSection);
			if (coord < 0 || coord >= list.size())
				return null;
			return list.get(coord);
		}
		return null;
	}

	/**
	 * Verifica se um item existe em uma sessão
	 * @param iniSection	Sessão á ser verificada
	 * @param iniItem	Item á ser verificado
	 * @return	{@code true} se o item existir na sessão especificada
	 */
	public Boolean itemExists(String iniSection, String iniItem) {
		return sectionExists(iniSection) ? iniBody.get(iniSection).containsKey(iniItem) : false;
	}

	/**
	 * Apaga todos os itens da sessão informada
	 * @param iniSection	Sessão á ter os itens apagados
	 */
	public void clearSection(String iniSection) {
		if (iniBody.containsKey(iniSection)) {
			iniBody.get(iniSection).clear();
			runSaveTimer();
		}
	}

	/**
	 * Limpa o arquivo atual, sem removê-lo do disco.
	 */
	public void clearFile() {
		iniBody.clear();
		runSaveTimer();
	}

	/**
	 * Remove o arquivo carregado do disco
	 */
	public void deleteFile() {
		try {
			new File(fileName).delete();
			Timer.stopTimer("IniFileSaveToDisk@" + hashCode());
			openedIniFiles.remove(fileName);
		}
		catch (Exception e) {
			throw new RuntimeException("Unable to delete file \"" + fileName + "\"");
		}
	}

	/**
	 * Verifica se a instância de IniFileGPT informada aponta para o mesmo arquivo
	 * da instância atual
	 * 
	 * @param otherIniFile Instância de IniFileGPT á ser comparada
	 * @return {@code true} se ambas instâncias apontarem para o mesmo arquivo.
	 */
	public boolean isSameFile(IniFile otherIniFile) {
		return fileName().equals(otherIniFile.fileName());
	}

	/**
	 * Fecha o arquivo atual, salvando as alterações em disco.
	 */
	public void closeFile() {
		Timer.stopTimer("IniFileSaveToDisk@" + hashCode());
		saveToDisk();
		openedIniFiles.remove(fileName);
	}

	/**
	 * Fecha todos os arquivos atualmente abertos, salvando as alterações em disco.
	 */
	public static void closeAllOpenedIniFiles() {
		synchronized (IniFile.class) {
			saveAllFilesToDisk();
			openedIniFiles.values().forEach(IniFile::clearFile);
			openedIniFiles.clear();
		}
	}

	/**
	 * Retorna uma lista de String contendo o nome de todas as sessões do arquivo
	 * atual
	 * 
	 * @return lista de String
	 */
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

	/**
	 * Retorna uma lista de String contendo o nome de todos os itens da sessão
	 * informada
	 * 
	 * @param iniSection Sessão da qual se quer obter a lista de itens
	 * @return lista de String
	 */
	public List<String> getItemList(String iniSection) {
		List<String> list = new ArrayList<String>();
		if (sectionExists(iniSection) && !iniBody.get(iniSection).isEmpty()) {
			Iterator<String> it = iniBody.get(iniSection).keySet().iterator();
			String item;
			while (it.hasNext())
				if ((item = it.next()) != null && !item.isEmpty())
					list.add(item);
		}
		return list;
	}

	/**
	 * Renomeia o arquivo atualmente aberto.
	 * 
	 * @param newFileName Novo nome para o arquivo atualmente aberto.
	 */
	public void renameFileTo(String newFileName) {
		try {
			saveToDisk();
			Path source = Paths.get(fileName);
			Path target = source.resolveSibling(newFileName);
			Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
			openedIniFiles.put(newFileName, this);
			openedIniFiles.remove(fileName);
			fileName = newFileName;
			saveToDisk();
		}
		catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to rename file \"" + fileName + "\" to \"" + newFileName + "\"");
		}
	}

	/**
	 * Retorna um LinkedHashMap a partir de uma String formatada
	 * {Item=Valor}{Item2=Valor2}...
	 *
	 * @param val       String formatada
	 * @param enclosers pares de caracteres que delimitam cada Item=Valor (ex: "{}")
	 * @return LinkedHashMap contendo os valores Item=Valor encontrados na String
	 *         formatada
	 */
	public static LinkedHashMap<String, String> subItemStringToLinkedHashMap(String val, String enclosers) {
		LinkedHashMap<String, String> subItems = new LinkedHashMap<>();
		String open = Pattern.quote(enclosers.substring(0, 1));
		String close = Pattern.quote(enclosers.substring(1, 2));
		Pattern pattern = Pattern.compile("(" + open + "[^" + close + "]+" + close + ")", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(val);
		while (matcher.find()) {
			String[] split = matcher.group(0).substring(1, matcher.group(0).length() - 1).split("=", 2);
			subItems.put(split[0], split.length > 1 ? split[1] : "");
		}
		return subItems;
	}

	public static LinkedHashMap<String, String> subItemStringToLinkedHashMap(String val) {
		return subItemStringToLinkedHashMap(val, "{}");
	}

	/**
	 * Converte LinkedHashMap em String formatada {Item=Valor}{Item2=Valor2}...
	 *
	 * @param map       LinkedHashMap á ser convertido
	 * @param enclosers pares de caracteres que delimitam cada Item=Valor (ex: "{}")
	 * @return String formatada
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

	public static String linkedHashMapToSubItemString(LinkedHashMap<String, String> map) {
		return linkedHashMapToSubItemString(map, "{}");
	}

	@Override
	public int hashCode() {
		return Objects.hash(fileName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		String s1 = file.toAbsolutePath().normalize().toString();
		String s2 = ((IniFile) obj).file.toAbsolutePath().normalize().toString();
		return s1.equals(s2);
	}

}
