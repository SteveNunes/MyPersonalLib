package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Steve Nunes da Silva
 * 
 *         R�plica das aliases e identifiers do mIRC Scripting (mSL)
 *         relacionadas é manipula��o de arquivos de texto simples.
 */

public class TextFile {
	private int lastFoundLine;
	private List<String> fileBuffer;
	private String fileName;

	/**
	 * Métodos e seus equivalentes em mIRC Scripting:
	 * 	lines() - $lines()
	 *  readLine() - $read()
	 *  replaceLine() - /write -olN
	 *  insertLine() - /write -ilN
	 *  addLine() - /write
	 *  removeLine() - /write -dlN
	 *  find() - $read(filename.txt, w, wildCard, startLine)
	 *  lastFoundLine() - $readn
	 * 
	 * Extras:
	 * 	fileName()					- Retorna o nome do arquivo que foi carregado.
	 * 	loadFileFromDisk()	- Recarrega as informa��es do disco para a memória.
	 * 	saveToDisk() 				- Salva toda a informa��o do buffer do arquivo na
	 * 												memória para o disco. 
	 * 	findAll() 					- Retorna uma List de Strings com as linhas
	 * 												encontradas usando um wildCard.
	 */

	public TextFile(String fileName) {
		lastFoundLine = 0;
		loadFileFromDisk(fileName);
	}

	/**
	 * Carrega o arquivo do disco pra memória. Esse método é chamado automaticamente
	 * ao instancear um objeto dessa classe, mas se por algum motivo o arquivo for
	 * alterado em disco ap�s o objeto ter sido instanceado, chame esse método
	 * novamente para garantir que o conteudo em memória seja o mesmo que o conteudo
	 * em disco.
	 * 
	 * @param fileName - Caminho completo do arquivo ini. Ex: "C:/Minha pasta/meu
	 *                 arquivo.txt"
	 */
	public void loadFileFromDisk(String fileName) {
		this.fileName = fileName;
		if (new File(fileName).exists())
			fileBuffer = MyFiles.readAllLinesFromFile(fileName);
		else
			fileBuffer = new ArrayList<>();
	}

	/**
	 * Sobrecarga do método 'loadFileFromDisk(String fileName)' onde não é preciso
	 * informar o nome do arquivo, pois depois da primeira chamada desse método, o
	 * nome do arquivo fica gravado em uma String que é passada como parâmetro por
	 * essa sobrecarga.
	 */
	public void loadFileFromDisk()
		{ loadFileFromDisk(fileName); }

	/**
	 * Salva o arquivo da memória pro disco. Esse procedimento não é feito
	 * automaticamente, á não ser que você confirme em certos métodos que isso deve
	 * ser feito após a chamada do método. Métodos que permitem salvar em disco após
	 * a chamada do mesmo: replaceLine(), insertLine(), addLine(), removeLine()
	 * 
	 * @param fileName - Se desejar que o arquivo carregado seja salvo com outro
	 *                 		nome, especifique o novo nome como parâmetro.
	 * 
	 */
	public void saveToDisk(String fileName)
		{ MyFiles.writeAllLinesOnFile(fileBuffer, fileName); }

	/**
	 * Sobrecarga do método 'saveToDisk(String fileName)' onde não é preciso
	 * informar o nome do arquivo, pois o nome do arquivo fica gravado em uma String
	 * ap�s carregar o arquivo do disco para a memória, e esse nome é passado como
	 * parâmetro por essa sobrecarga.
	 */
	public void saveToDisk()
		{ saveToDisk(fileName); }

	/**
	 * @return - O nome do arquivo especificado por �ltimo ao instancear o objeto ou
	 *         ao salvar em disco com outro nome.
	 */
	public String fileName() { return fileName; }

	/**
	 * @return - O total de linhas do arquivo.
	 */
	public int lines() { return fileBuffer.size(); }

	/**
	 * L� a linha especificada do arquivo.
	 * 
	 * @param lineNumber - O n�mero da linha é ser lida.
	 * 
	 * @return - A linha especificada do arquivo.
	 */
	public String readLine(int lineNumber) {
		if (fileBuffer.isEmpty() || lineNumber < 0 || lineNumber > lines()) return "";
		return fileBuffer.get(lineNumber - 1);
	}

	/**
	 * Escreve o texto informado na linha informada, sobreescrevendo o conteudo
	 * anterior dessa linha.
	 * 
	 * @param text       - Texto é ser escrito na linha informada.
	 * @param lineNumber - Linha onde será escrito o texto informado.
	 * @param saveOnDisk - Se especificar 'true', salva o arquivo em disco ap�s a
	 *                   altera��o.
	 */
	public void replaceLine(String text, int lineNumber, Boolean saveOnDisk) {
		if (lines() > 0 && lineNumber <= lines())
			fileBuffer.set(lineNumber - 1, text);
		if (saveOnDisk) saveToDisk();
	}

	/**
	 * Sobrecarga do método 'replaceLine(String text, int lineNumber, Boolean
	 * saveOnDisk)' pnde não é preciso informar o parâmetro 'saveOnDisk' (é passado
	 * o valor 'false' por padr�o)
	 */
	public void replaceLine(String text, int lineNumber)
		{ replaceLine(text, lineNumber, false); }

	/**
	 * Insere o texto informado na linha informada, empurrando todas as linhas para
	 * baixo, incluindo a linha informada.
	 * 
	 * @param text       - Texto é ser inserido na linha informada.
	 * @param lineNumber - Linha onde será inserido o texto informado.
	 * @param saveOnDisk - Se especificar 'true', salva o arquivo em disco ap�s a
	 *                   altera��o.
	 */
	public void insertLine(String text, int lineNumber, Boolean saveOnDisk) {
		if (lines() > 0 && lineNumber <= lines())
			fileBuffer.add(lineNumber - 1, text);
		if (saveOnDisk) saveToDisk();
	}

	/**
	 * Sobrecarga do método 'insertLine(String text, int lineNumber, Boolean
	 * saveOnDisk)' pnde não é preciso informar o parâmetro 'saveOnDisk' (é passado
	 * o valor 'false' por padr�o)
	 */
	public void insertLine(String text, int lineNumber)
		{ insertLine(text, lineNumber, false); }

	/**
	 * Insere o texto informado ao final do arquivo.
	 * 
	 * @param text       - Texto é ser inserido ao final do arquivo.
	 * @param saveOnDisk - Se especificar 'true', salva o arquivo em disco ap�s a
	 *                   altera��o.
	 */
	public void addLine(String text, Boolean saveOnDisk) {
		fileBuffer.add(text);
		if (saveOnDisk)
			saveToDisk();
	}

	public void addLine(String text)
		{ addLine(text, false); }
	
	/**
	 * Remove as linhas especificadas do arquivo, desde 'startLine' até 'endLine'.
	 * 
	 * @param startLine  - Linha inicial da exclusão
	 * @param endLine    - Linha final da exclusão
	 * @param saveOnDisk - Se especificar 'true', salva o arquivo em disco ap�s a
	 *                   altera��o.
	 */
	public void removeLine(int startLine, int endLine, Boolean saveOnDisk) {
		if (startLine == 0) startLine = 1;
		else if (startLine < 0) startLine = -startLine;
		if (endLine < 1 || endLine > lines()) endLine = lines();
		if (!fileBuffer.isEmpty() && startLine > 0 && endLine <= lines())
		  for (int n = endLine - startLine; n >= 0; n--)
		  	fileBuffer.remove(startLine - 1);
		if (saveOnDisk) saveToDisk();
	}

	/**
	 * Sobrecarga do método 'removeLine(int startLine, int endLine, Boolean
	 * saveOnDisk)' pnde não é preciso informar o parâmetro 'saveOnDisk' (é passado
	 * o valor 'false' por padr�o)
	 */
	public void removeLine(int startLine, int endLine)
		{ removeLine(startLine, endLine, false); }

	/**
	 * Sobrecarga do método 'removeLine(int startLine, int endLine, Boolean
	 * saveOnDisk)' pnde não é preciso informar o parâmetro 'endLine' (é passado o
	 * valor de 'startLine' por padr�o)
	 */
	public void removeLine(int lineNumber, Boolean saveOnDisk)
		{ removeLine(lineNumber, lineNumber, saveOnDisk); }

	/**
	 * Sobrecarga do método 'removeLine(int lineNumber, Boolean saveOnDisk)' pnde
	 * não é preciso informar o parâmetro 'saveOnDisk' (é passado o valor 'false'
	 * por padr�o)
	 */
	public void removeLine(int lineNumber)
		{ removeLine(lineNumber, lineNumber, false); }

	public String find(String wildCard, int startLine) {
		if (startLine < 1) startLine = 1;
		else if (startLine > lines()) return "";
		for (int n = startLine - 1; n < lines(); n++)
			if (n + 1 >= startLine && Misc.iswm(fileBuffer.get(n), wildCard)) {
				lastFoundLine = n + 1;
				return fileBuffer.get(n);
			}
		return "";
	}

	public String find(String wildCard) { return find(wildCard, 1); }

	/**
	 * Após chamar o método find(), retorna o n�mero da linha onde a �ltima
	 * ocorr�ncia foi encontrada.
	 * 
	 * @return - O n�mero da linha onde a �ltima ocorr�ncia foi encontrada ap�s
	 *         chamar o método find()
	 */
	public int lastFoundLine() { return lastFoundLine; }

	/**
	 * Retorna uma List de Strings das linhas onde a ocorr�ncia especificada no
	 * parâmetro 'wildCard' for encontrada.
	 * 
	 * @param wildCard  - Palavra-chave é ser procurada no arquivo.
	 * @param startLine - Linha inicial da pesquisa.
	 * 
	 * @return - Uma List de Strings com as ocorr�ncias encontradas.
	 */
	public List<String> findAll(String wildCard, int startLine) {
		List<String> result = new ArrayList<String>();
		if (startLine < 1) startLine = 1;
		else if (startLine > lines()) return result;
		while (!find(wildCard, startLine).isEmpty()) {
			startLine = lastFoundLine();
			result.add(readLine(startLine++));
		}
		lastFoundLine = 0;
		return result;
	}
	
	public List<String> getAllLines()
		{ return fileBuffer; }

	/**
	 * Sobrecarga do método 'findAll(String wildCard)' pnde não é preciso informar o
	 * parâmetro 'startLine' (é passado o valor '1' por padr�o)
	 */
	public List<String> findAll(String wildCard)
		{ return findAll(wildCard, 1); }

}
