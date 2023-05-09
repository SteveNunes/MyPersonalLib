package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import enums.TextMatchType;

/**
 * 
 * @author Steve Nunes da Silva
 * 
 * Réplica das aliases e identifiers do mIRC Scripting (mSL)
 * relacionadas a manipulação de tokens.
 * 
 * Todos os métodos que envolvam informar uma posiçãoo para obter ou inserir
 * um token, o primeiro valor inicia na posição '1' ao invés de '0'.
 * 
 * Exemplo: get("Esta é uma frase de exemplo", 1, 1, " ")
 *          Retorna: "Esta"
 *          
 * Mëtodos e seus equivalentes em mIRC Scripting
 * 
 * tokenize() 											- /tokenize 
 * token(1), token(2), token(3) ... - $1 $2 $3 ... (Após o uso do /tokenize)
 * num()														- $numtok()
 * add()														- $addtok()
 * get()														- $gettok()
 * put()														- $puttok()
 * insert()													- $instok()
 * delete()													- $deltok()
 * remove()													- $remtok()
 * replace()												- $reptok()
 * find()														- $findtok()
 * ison()														- $istok()
 * wildcard() 											- $wildtok()
 * match()													- $matchtok()
 * sort()														- $sorttok()
 * 
 * Extras:
 * 
 * tokenList()	- Retorna uma array de String com os tokens obtidos após o uso de 'tokenize()'
 * size()				- Retorna o valor do total de tokens obtidos após o uso de 'tokenize()' 
 * lastTokens() - Retorna uma List de String com os tokes obtidos pelo último método chamado.
 */

public class Token {
	private static String lastDelimiter = "";
	private static String[] tokens;
	private static List<String> ltokens = new ArrayList<>();
	
	/**
	 * Quebra a String informada em tokens, que podem ser acessados
	 * pelos métodos 'token()', e através do método 'size()' é possível
	 * obter o valor total de tokens gerados pelo método atual. 
	 *
	 * @param	text					a String que será quebrada em tokens
	 * @param	delimiter			o delimitador dos tokens (Se especificar o 'espaço' por exemplo,
	 * 											a String "Esta é uma frase de exemplo" será quebrada no seguinte
	 * 											formato:
	 * 
	 *                      Esta
	 *                      �
	 *                      uma
	 *                      frase
	 *                      de
	 *                      exemplo
	 */
	public static void tokenize(String text, String delimiter)
		{ tokens = text.split(lastDelimiter = delimiter); }

	/**
	 * Sobrecarga do método 'tokenize(String text, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static void tokenize(String text)
		{ tokenize(text, " "); }
	
	/**
	 * Após quebrar uma String em tokens com o método 'tokenize()', use o
	 * método atual para retornar o valor total de tokens que foram gerados.
	 * 
	 * @return			Após usar tokenize("Esta é uma frase de exemplo", " ")
	 * 							.size() irá retornar o valor '6', pois essa String foi
	 * 							quebrada em 6 tokens, usando o 'espaço' como delimitador.
	 */
	public static int size()
		{	return tokens.length; }
	
	/**
	 * Após quebrar uma String em tokens com o método 'tokenize()',
	 * use o método atual para obter cada um desses tokens.
	 * 
	 * @param		startPos		posição inicial do(s) token(s) é ser(em) obtido(s).
	 * @param		endPos			posição final do(s) token(s) é ser(em) obtido(s).
	 * 											Se informar -1, retorna todos os tokens é partir
	 * 											de 'startPos'
	 * 
	 * @return				Após usar tokenize("Esta é uma frase de exemplo", " ")
	 * 								.token(1) retorna "Esta" 
	 * 								.token(3) retorna "uma" 
	 * 								.token(6) retorna "exemplo"
	 * 								.token(3, 4) retorna "uma frase"
	 * 								.token(3, -1) retorna "uma frase de exemplo"
	 * 								.token(-4) retorna "uma frase de exemplo"
	 */
	public static String token(int startPos, int endPos) {
		StringBuilder result = new StringBuilder();
		if (endPos == -1)
			endPos = size();
		for (int n = startPos - 1; n < endPos; n++) {
			result.append(result.length() > 0 ? lastDelimiter : "");
			result.append(tokens[n]);
		}
		return result.toString();
	}

	/**
	 * Sobrecarga do método 'token(int startPos, int endPos)'
	 * onde não é preciso informar o 'endPos' (É passado o valor de 'startPos' por padrão). 
	 */
	public static String token(int startPos) {
		int n = startPos, endPos;
		if (n < 1)
			startPos = -startPos;
		endPos = n < 0 ? -1 : n;
		return token(startPos, endPos);
	}
	/**
	 * Após quebrar uma String em tokens com o método 'tokenize()',
	 * retorna uma array de Strings com os tokens obtidos.
	 * 
	 * @return		uma array de Strings com os tokens obtidos com o método 'tokenize()'
	 */
	public static String[] tokenList()
		{ return tokens; }

	/**
	 * Após usar qualquer método que retorne uma String com certos tokens,
	 * ao chamar esse método, retornará uma List de Strings com os tokens
	 * que foram obtidos pelo último método chamado.
	 * 
	 * Exemplo: Ao usar: 'get("Esta é uma frase de exemplo", 3, 6, " ")'
	 * 					retorna a String "uma frase de exemplo" e ao chamar o método
	 * 					atual, retornará a List de Strings:
	 * 					{"uma", "frase", "de", "exemplo"}
	 * 
	 * @return		uma List de Strings com os tokens obtidos pelo último método chamado.
	 */
	public static List<String> LastTokens()
		{ return ltokens; }

	/**
	 * Retorna o total de tokens da String informada.
	 * 
	 * @param		text				a String á ser verificada.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.num("Esta é uma frase de exemplo", " ")
	 * 											irá retornar o valor '6', pois essa String possui
	 * 											6 palavras usando o 'espaço' como delimitador.
	 */
	public static int num(String text, String delimiter) {
		String[] ttokens = text.split(delimiter);
		return ttokens.length;
	}
	
	/**
	 * Sobrecarga do método 'num(String text, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static int num(String text)
		{ return num(text, " "); }

	/**
	 * Adiciona um token é String informada, caso esse
	 * token ainda não esteja presente nessa String.
	 * 
	 * @param		text				a String á ser verificada.
	 * @param		word				token á ser adicionado á String informada.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.add("Esta é uma frase de exemplo", "teste", " ")
	 * 											irá retornar "Esta é uma frase de exemplo teste"
	 * 											.add("Esta é uma frase de exemplo", "frase", " ")
	 * 											irá retornar "Esta é uma frase de exemplo"
	 * 											pois o token "frase" já está presente na String informada. 
	 */
	public static String add(String text, String word, String delimiter) {
		String[] ttokens = text.split(delimiter);
		ltokens = Arrays.asList(ttokens);
		for (int n = 0; n < ttokens.length; n++)
			if (ttokens[n].equals(word))
				return text;
		ltokens.add(word);
		StringBuilder result = new StringBuilder();
		result.append(text);
		result.append(delimiter);
		result.append(word);
		return result.toString();
	}

	/**
	 * Sobrecarga do método 'add(String text, String word, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String add(String text, String word)
		{ return add(text, word, " "); }
	
	/**
	 * Pega da String informada, os tokens desde 'startPos' até 'endPos'.
	 * 
	 * @param		text				a String de onde será pego os tokens.
	 * @param		startPos		a posição inicial de onde será pego os tokens.
	 * @param		endPos			a posição final de onde será pego os tokens.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.get("Esta é uma frase de exemplo", 4, 4, " ")
	 * 											irá retornar "frase"
	 * 											.get("Esta é uma frase de exemplo", 3, 5, " ")
	 * 											irá retornar "uma frase de"
	 * 											.get("Esta é uma frase de exemplo", -3, " ")
	 * 											irá retornar "uma frase de exemplo"
	 */
	public static String get(String text, int startPos, int endPos, String delimiter) {
		String[] ttokens = text.split(delimiter);
		StringBuilder result = new StringBuilder();
		ltokens.clear();
		if (startPos == 0)
			startPos = 1;
		else if (startPos < 0)
			startPos = -startPos;
		if (endPos < 1 || endPos > ttokens.length)
			endPos = ttokens.length;
		for (int n = startPos - 1; n < endPos; n++) {
			result.append(result.length() > 0 ? delimiter : "");
			result.append(ttokens[n]);
			ltokens.add(ttokens[n]);
		}
		return result.toString();
	}
	
	/**
	 * Sobrecarga do método 'get(String text, int startPos, int endPos, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String get(String text, int startPos, int endPos)
		{ return get(text, startPos, endPos, " "); }
	
	/**
	 * Sobrecarga do método 'get(String text, int startPos, int endPos, String delimiter)'
	 * onde não é preciso informar o 'endPos' (É passado o mesmo valor de 'startPos' por padrão).
	 */
	public static String get(String text, int startPos, String delimiter)
		{ return get(text, startPos, startPos, delimiter); }
	
	/**
	 * Sobrecarga do método 'get(String text, int startPos, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String get(String text, int startPos)
		{	return get(text, startPos, " "); }
	
	/**
	 * Substitui o token da posição informada
	 * dentro da String informada, por um novo token.
	 * 
	 * @param		text				a String que será alterada.
	 * @param		pos					a posição do token atual que será substituido pelo novo token.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.put("Esta é uma frase de exemplo", "eh", 2, " ")
	 * 											irá retornar "Esta eh uma frase de exemplo"
	 */
	public static String put(String text, String word, int pos, String delimiter) {
		String[] ttokens = text.split(delimiter);
		StringBuilder result = new StringBuilder();
		ltokens.clear();
		for (int n = 0, p = 1; n < ttokens.length; n++, p++) {
			result.append(result.length() > 0 ? delimiter : "");
			result.append(p == pos ? word : ttokens[n]);
			ltokens.add(p == pos ? word : ttokens[n]);
		}
		return result.toString();
	}

	/**
	 * Sobrecarga do método 'put(String text, String word, int pos, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String put(String text, String word, int pos)
		{ return put(text, word, pos, " "); }
	
	/**
	 * Insere o token informado na posição informada dentro da
	 * String informada, empurrando os tokens daquela posição
	 * em diante 1 posição para frente.
	 * 
	 * @param		text				a String que será alterada.
	 * @param		pos					a posição onde será inserido o novo token.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.insert("Esta é uma frase de exemplo", "não", 2, " ")
	 * 											irá retornar "Esta não é uma frase de exemplo"
	 */
	public static String insert(String text, String word, int pos, String delimiter) {
		String[] ttokens = text.split(delimiter);
		StringBuilder result = new StringBuilder();
		ltokens.clear();
		for (int n = 0, p = 1; n < ttokens.length; n++, p++) {
			result.append(result.length() > 0 ? delimiter : "");
			if (p == pos) {
				result.append(word);
				result.append(delimiter);
				result.append(ttokens[n]);
			}
			else
				result.append(ttokens[n]);
			ltokens.add(ttokens[n]);
		}
		return result.toString();
	}

	/**
	 * Sobrecarga do método 'insert(String text, String word, int pos, String delimiter)' 
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String insert(String text, String word, int pos)
		{ return insert(text, word, pos, " "); }
	
	/**
	 * Deleta da String informada, o token da posição informada.
	 * 
	 * @param		text				a String que será alterada.
	 * @param		startPos		a posição inicial de onde será deletado o(s) token(s).
	 * @param		endPos			a posição final de onde será deletado o(s) token(s).
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.delete("Esta é uma frase de exemplo", 5, " ")
	 * 											irá retornar "Esta é uma frase exemplo"
	 * 											.delete("Esta é uma frase de exemplo", 2, 5, " ")
	 * 											irá retornar "Esta exemplo"
	 */
	public static String delete(String text, int startPos, int endPos, String delimiter) {
		String[] ttokens = text.split(delimiter);
		StringBuilder result = new StringBuilder();
		ltokens.clear();
		if (startPos == 0)
			startPos = 1;
		else if (startPos < 0)
			startPos = -startPos;
		if (endPos < 1 || endPos > ttokens.length)
			endPos = ttokens.length;
		for (int n = 0, pos = 0; n < ttokens.length; n++)
			if (++pos < startPos || pos > endPos) {
				result.append(result.length() > 0 ? delimiter : "");
				result.append(ttokens[n]);
				ltokens.add(ttokens[n]);
			}
		return result.toString();
	}

	/**
	 * Sobrecarga do método 'delete(String text, int startPos, int endPos, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String delete(String text, int startPos, int lenght)
		{ return delete(text, startPos, lenght, " ");	}
	
	/**
	 * Sobrecarga do método 'delete(String text, int startPos, int endPos, String delimiter)'
	 * onde não é preciso informar o 'endPos' (É passado o valor de 'startPos' por padrão). 
	 */
	public static String delete(String text, int startPos, String delimiter)
		{ return delete(text, startPos, startPos, delimiter); }
	
	/**
	 * Sobrecarga do método 'delete(String text, int startPos, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String delete(String text, int startPos)
		{ return delete(text, startPos, " "); }
	
	/**
	 * Remove da String informada, o token informado,
	 * se ele fizer parte da String informada.
	 * 
	 * @param		text				a String que será alterada.
	 * @param		word				token é ser removido da String informada.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.remove("Esta é uma frase de exemplo", "de", " ")
	 * 											irá retornar "Esta é uma frase exemplo"
	 */
	public static String remove(String text, String word, String delimiter) {
		String[] ttokens = text.split(delimiter);
		StringBuilder result = new StringBuilder();
		ltokens.clear();
		for (int n = 0; n < ttokens.length; n++)
			if (!ttokens[n].equals(word)) {
				result.append(result.length() > 0 ? delimiter : "");
				result.append(ttokens[n]);
				ltokens.add(ttokens[n]);
			}
		return result.toString();
	}

	/**
	 * Sobrecarga do método 'remove(String text, String word, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String remove(String text, String word)
		{ return remove(text, word, " "); }
	
	/**
	 * Substitui um token por outro na String informada.
	 * 
	 * @param		text				a String que será alterada.
	 * @param		target			o token é ser localizado.
	 * @param		replace			o token é ser inserido no lugar do token localizado. 
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.replace("Esta é uma frase de exemplo", "frase", "mensagem", " ")
	 * 											irá retornar "Esta é uma mensagem exemplo"
	 */
	public static String replace(String text, String target, String replace, String delimiter) {
		String[] ttokens = text.split(delimiter);
		StringBuilder result = new StringBuilder();
		ltokens.clear();
		String s;
		for (int n = 0; n < ttokens.length; n++) {
			s = ttokens[n].equals(target) ? replace : ttokens[n];
			result.append(result.length() > 0 ? delimiter : "");
			result.append(s);
			ltokens.add(s);
		}
		return result.toString();
	}

	/**
	 * Sobrecarga do método 'replace(String text, String target, String replace, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String replace(String text, String target, String replace)
		{ return replace(text, target, replace, " "); }

	/**
	 * Localiza um token dentro da String informada, e retorna o valor da sua posição.
	 * Se 'pos' for '2' e houver mais de 1 ocorrência do token informado, retorna a
	 * posição da segunda ocorrência desse token.
	 * Se 'pos' for '0', retorna o total de vezes que o token informado foi encontrado.
	 * 
	 * @param		text				a String que será alterada.
	 * @param		word				o token é ser localizado.
	 * @param		pos					a ocorrência desse token que deverá retornar o valor. 
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.find("Esta é uma frase de exemplo de teste", "de", 0, " ")
	 * 											irá retornar '2' pois há 2 ocorrências do token 'de' na String informada.
	 * 											.find("Esta é uma frase de exemplo de teste", "frase", 1, " ")
	 * 											irá retornar '4' pois a primeira ocorrência do token 'frase'
	 * 											 está na posição 4 da String informada.
	 * 											.find("Esta é uma frase de exemplo de teste", "de", 2, " ")
	 * 											irá retornar '7' pois a segunda ocorrência do token 'de'
	 * 											 está na posição 7 da String informada.
	 */
	public static int find(String text, String word, int pos, String delimiter) {
		String[] ttokens = text.split(delimiter);
		if (pos < 0) pos = 1;
		int total = 0;
		for (int n = 0, p = 0; n < ttokens.length; n++)
			if (ttokens[n].equals(word)) {
				if (pos > 0 && ++p == pos)
					return n + 1;
				else if (pos == 0)
					total++;
			}
		return total;
	}

	/**
	 * Sobrecarga do método 'find(String text, String word, int pos, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static int find(String text, String word, int pos)
		{ return find(text, word, pos, " "); }

	/**
	 * Sobrecarga do método 'find(String text, String word, int pos, String delimiter)'
	 * onde não é preciso informar a 'pos' (É passado o valor '1' por padrão). 
	 */
	public static int find(String text, String word, String delimiter)
		{ return find(text, word, 1, delimiter); }

	/**
	 * Sobrecarga do método 'find(String text, String word, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static int find(String text, String word)
		{	return find(text, word, 1, " "); }

	/**
	 * Retorna 'true' se o token informado estiver presente na String informada.
	 * 
	 * @param		text				a String que será alterada.
	 * @param		word				o token é ser localizado.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.find("Esta é uma frase de exemplo", "frase", " ")
	 * 											irá retornar 'true' pois o token 'frase' esó presente na String informada.
	 */
	public static Boolean ison(String text, String word, String delimiter)
		{ return (find(text, word, delimiter) > 0); }

	/**
	 * Sobrecarga do método 'ison(String text, String word, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static Boolean ison(String text, String word)
		{ return ison(text, word, " "); }

	/**
	 * Localiza um token dentro da String informada, usando um 'wildcard'
	 * e retorna o valor da sua posição.
	 * Se 'pos' for '2' e houver mais de 1 ocorrência do token informado, retorna a
	 * posição da segunda ocorrência desse token.
	 * Se 'pos' for '0', retorna o total de vezes que o token informado foi encontrado.
	 * 
	 * @param		text				a String que será alterada.
	 * @param		wildcard		o wildcard á ser localizado dentro dos tokens da String informada.
	 * @param		pos					a ocorrência desse token que deverá retornar o valor. 
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.wildcard("Esta é uma frase de exemplo", "*s*", 0, " ")
	 * 											irá retornar '2' pois há 2 tokens que se encaixam com o wildcard '*s*'
	 * 											.wildcard("Esta é uma frase de exemplo", "*s*", 1, " ")
	 * 											irá retornar 'Esta' pois é o primeiro token encontrado na frase que
	 * 											se encaixa com o wildcard '*s*'
	 * 											.wildcard("Esta é uma frase de exemplo", "*s*", 2, " ")
	 * 											irá retornar 'frase' pois é o segundo token encontrado na frase que
	 * 											se encaixa com o wildcard '*s*'
	 */
	public static String wildcard(String text, String wildcard, int pos, String delimiter) {
		String[] ttokens = text.split(delimiter);
		if (pos < 0)
			pos = 1;
		int total = 0;
		ltokens.clear();
		for (int n = 0, p = 0; n < ttokens.length; n++)
			if (Misc.textMatch(ttokens[n], wildcard, TextMatchType.WILDCARD)) {
				if (pos > 0 && ++p == pos)
					return ttokens[n];
				else if (pos == 0) {
					total++;
					ltokens.add(ttokens[n]);
				}
			}
		return (pos == 0 ? Integer.toString(total) : new String());
	}
	
	/**
	 * Sobrecarga do método 'wildcard(String text, String wildcard, int pos, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String wildcard(String text, String wildcard, int pos)
	  { return wildcard(text, wildcard, pos, " "); }
	
	/**
	 * Sobrecarga do método 'wildcard(String text, String wildcard, String delimiter)'
	 * onde não é preciso informar a 'pos' (É passado o valor '1' por padrão). 
	 */
	public static String wildcard(String text, String wildcard, String delimiter)
		{ return wildcard(text, wildcard, 1, delimiter); }
	
	/**
	 * Sobrecarga do método 'wildcard(String text, String wildcard, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String wildcard(String text, String wildcard)
		{ return wildcard(text, wildcard, 1, " "); }
	
	/**
	 * Localiza um token dentro da String informada, que tenha parte
	 * do parâmetro 'match' no seu conteúdo.
	 * Se 'pos' for '2' e houver mais de 1 ocorrência do token informado, retorna a
	 * posição da segunda ocorrência desse token.
	 * Se 'pos' for '0', retorna o total de vezes que o token informado foi encontrado.
	 * 
	 * @param		text				a String que será alterada.
	 * @param		match				o conteudo á ser localizado dentro dos tokens da String informada.
	 * @param		pos					a ocorrência desse token que deverá retornar o valor. 
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.match("Esta é uma frase de exemplo", "s", 0, " ")
	 * 											irá retornar '2' pois há 2 tokens que possuem 's' no seu conteúdo.
	 * 											.match("Esta é uma frase de exemplo", "s", 1, " ")
	 * 											irá retornar 'Esta' pois é o primeiro token encontrado na frase que
	 * 											possui 's' no seu conteúdo.
	 * 											.match("Esta é uma frase de exemplo", "s", 2, " ")
	 * 											irá retornar 'frase' pois é o segundo token encontrado na frase que
	 * 											possui 's' no seu conteúdo.
	 */
	public static String match(String text, String match, int pos, String delimiter)
		{ return wildcard(text, "*" + match + "*", pos, delimiter); }
	
	/**
	 * Sobrecarga do método 'match(String text, String match, int pos, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String match(String text, String match, int pos)
	  { return match(text, match, pos, " "); }
	
	/**
	 * Sobrecarga do método 'match(String text, String match, int pos, String delimiter)'
	 * onde não é preciso informar a 'pos' (É passado o valor '1' por padrão). 
	 */
	public static String match(String text, String match, String delimiter)
		{ return match(text, match, 1, delimiter); }
	
	/**
	 * Sobrecarga do método 'match(String text, String match, String delimiter)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String match(String text, String match)
		{ return match(text, match, 1, " "); }
	
	/**
	 * Organiza os tokens em ordem alfabetica.
	 * 
	 * @param		text				a String que será alterada.
	 * @param		delimiter		o delimitador dos tokens.
	 * @param		reverse			especifique '1' para ordem decrescente.
	 * 
	 * @return							.sort("Steve Maneca João Pedro Alberto Maneca Iona José", " ")
	 * 											retorna "Alberto Iona José João Maneca Maneca Pedro Steve"
	 * 											.sort("Steve Maneca João Pedro Alberto Maneca Iona José", " ", 1)
	 * 											retorna "Steve Pedro Maneca Maneca João José Iona Alberto"
	 */
	public static String sort(String text, String delimiter, int reverse) {
		List<String> list = new ArrayList<String>(Arrays.asList(text.split(delimiter)));
		Collections.sort(list);
		StringBuilder result = new StringBuilder();
		for (int n = 0, n2 = list.size(); n < n2; n++) {
			result.append(result.length() > 0 ? delimiter : "");
			result.append(list.get(reverse == 1 ? n2 - 1 - n : n));
		}
		ltokens = list;
		return result.toString();
	}

	/**
	 * Sobrecarga do método 'sort(String text, String delimiter, int reverse)'
	 * onde não é preciso informar o valor de reverse (É passado '0' por padrão). 
	 */
	public static String sort(String text, String delimiter)
		{ return sort(text, delimiter, 0); }

	/**
	 * Sobrecarga do método 'sort(String text, String delimiter, int reverse)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String sort(String text, int reverse)
		{ return sort(text, " ", reverse); }

	/**
	 * Sobrecarga do método 'sort(String text, int reverse)'
	 * onde não é preciso informar o delimitador (É passado o 'espaço' por padrão). 
	 */
	public static String sort(String text)
		{ return sort(text, " ", 0); }
}