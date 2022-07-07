package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Steve Nunes da Silva
 * 
 * R�plica das aliases e identifiers do mIRC Scripting (mSL)
 * relacionadas a manipula��o de tokens.
 * 
 * Todos os m�todos que envolvam informar uma posi��o para obter ou inserir
 * um token, o primeiro valor inicia na posi��o '1' ao inv�s de '0'.
 * 
 * Exemplo: get("Esta � uma frase de exemplo", 1, 1, " ")
 *          ir� retornar 'Esta'
 *          
 * M�todos e seus equivalentes em mIRC Scripting
 * 
 * tokenize() 											- /tokenize 
 * token(1), token(2), token(3) ... - $1 $2 $3 ... (Ap�s o uso do /tokenize)
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
 * tokenList()	- Retorna uma array de String com os tokens obtidos ap�s o uso de 'tokenize()'
 * size()				- Retorna o valor do total de tokens obtidos ap�s o uso de 'tokenize()' 
 * lastTokens() - Retorna uma List de String com os tokes obtidos pelo �ltimo m�todo chamado.
 */

public class Token {
	private static String lastDelimiter = "";
	private static String[] tokens;
	private static List<String> ltokens = new ArrayList<>();
	
	/**
	 * Quebra a String informada em tokens, que podem ser acessados
	 * pelos m�todos 'token()', e atrav�s do m�todo 'size()' � poss�vel
	 * obter o valor total de tokens gerados pela fun��o atual. 
	 *
	 * @param	text					a String que ser� quebrada em tokens
	 * @param	delimiter			o delimitador dos tokens (Se especificar o 'espa�o' por exemplo,
	 * 											a String "Esta � uma frase de exemplo" ser� quebrada no seguinte
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
	 * Sobrecarga do m�todo 'tokenize(String text, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static void tokenize(String text)
		{ tokenize(text, " "); }
	
	/**
	 * Ap�s quebrar uma String em tokens com o m�todo 'tokenize()', use o
	 * m�todo atual para retornar o valor total de tokens que foram gerados.
	 * 
	 * @return			Ap�s usar tokenize("Esta � uma frase de exemplo", " ")
	 * 							.size() ir� retornar o valor '6', pois essa String foi
	 * 							quebrada em 6 tokens, usando o 'espa�o' como delimitador.
	 */
	public static int size()
		{	return tokens.length; }
	
	/**
	 * Ap�s quebrar uma String em tokens com o m�todo 'tokenize()',
	 * use o m�todo atual para obter cada um desses tokens.
	 * 
	 * @param		startPos		posi��o inicial do(s) token(s) � ser(em) obtido(s).
	 * @param		endPos			posi��o final do(s) token(s) � ser(em) obtido(s).
	 * 											Se informar -1, retorna todos os tokens � partir
	 * 											de 'startPos'
	 * 
	 * @return				Ap�s usar tokenize("Esta � uma frase de exemplo", " ")
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
	 * Sobrecarga do m�todo 'token(int startPos, int endPos)'
	 * onde n�o � preciso informar o 'endPos' (� passado o valor de 'startPos' por padr�o). 
	 */
	public static String token(int startPos) {
		int n = startPos, endPos;
		if (n < 1)
			startPos = -startPos;
		endPos = n < 0 ? -1 : n;
		return token(startPos, endPos);
	}
	/**
	 * Ap�s quebrar uma String em tokens com o m�todo 'tokenize()',
	 * retorna uma array de Strings com os tokens obtidos.
	 * 
	 * @return		uma array de Strings com os tokens obtidos com o m�todo 'tokenize()'
	 */
	public static String[] tokenList()
		{ return tokens; }

	/**
	 * Ap�s usar qualquer m�todo que retorne uma String com certos tokens,
	 * ao chamar esse m�todo, retornar� uma List de Strings com os tokens
	 * que foram obtidos pelo �ltimo m�todo chamado.
	 * 
	 * Exemplo: Ao usar: 'get("Esta � uma frase de exemplo", 3, 6, " ")'
	 * 					retorna a String "uma frase de exemplo" e ao chamar o m�todo
	 * 					atual, retornar� a List de Strings:
	 * 					{"uma", "frase", "de", "exemplo"}
	 * 
	 * @return		uma List de Strings com os tokens obtidos pelo �ltimo m�todo chamado.
	 */
	public static List<String> LastTokens()
		{ return ltokens; }

	/**
	 * Retorna o total de tokens da String informada.
	 * 
	 * @param		text				a String � ser verificada.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.num("Esta � uma frase de exemplo", " ")
	 * 											ir� retornar o valor '6', pois essa String possui
	 * 											6 palavras usando o 'espa�o' como delimitador.
	 */
	public static int num(String text, String delimiter) {
		String[] ttokens = text.split(delimiter);
		return ttokens.length;
	}
	
	/**
	 * Sobrecarga do m�todo 'num(String text, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static int num(String text)
		{ return num(text, " "); }

	/**
	 * Adiciona um token � String informada, caso esse
	 * token ainda n�o esteja presente nessa String.
	 * 
	 * @param		text				a String � ser verificada.
	 * @param		word				token � ser adicionado � String informada.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.add("Esta � uma frase de exemplo", "teste", " ")
	 * 											ir� retornar "Esta � uma frase de exemplo teste"
	 * 											.add("Esta � uma frase de exemplo", "frase", " ")
	 * 											ir� retornar "Esta � uma frase de exemplo"
	 * 											pois o token "frase" j� est� presente na String informada. 
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
	 * Sobrecarga do m�todo 'add(String text, String word, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String add(String text, String word)
		{ return add(text, word, " "); }
	
	/**
	 * Pega da String informada, os tokens desde 'startPos' at� 'endPos'.
	 * 
	 * @param		text				a String de onde ser� pego os tokens.
	 * @param		startPos		a posi��o inicial de onde ser� pego os tokens.
	 * @param		endPos			a posi��o final de onde ser� pego os tokens.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.get("Esta � uma frase de exemplo", 4, 4, " ")
	 * 											ir� retornar "frase"
	 * 											.get("Esta � uma frase de exemplo", 3, 5, " ")
	 * 											ir� retornar "uma frase de"
	 * 											.get("Esta � uma frase de exemplo", -3, " ")
	 * 											ir� retornar "uma frase de exemplo"
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
	 * Sobrecarga do m�todo 'get(String text, int startPos, int endPos, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String get(String text, int startPos, int endPos)
		{ return get(text, startPos, endPos, " "); }
	
	/**
	 * Sobrecarga do m�todo 'get(String text, int startPos, int endPos, String delimiter)'
	 * onde n�o � preciso informar o 'endPos' (� passado o mesmo valor de 'startPos' por padr�o).
	 */
	public static String get(String text, int startPos, String delimiter)
		{ return get(text, startPos, startPos, delimiter); }
	
	/**
	 * Sobrecarga do m�todo 'get(String text, int startPos, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String get(String text, int startPos)
		{	return get(text, startPos, " "); }
	
	/**
	 * Substitui o token da posi��o informada
	 * dentro da String informada, por um novo token.
	 * 
	 * @param		text				a String que ser� alterada.
	 * @param		pos					a posi��o do token atual que ser� substituido pelo novo token.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.put("Esta � uma frase de exemplo", "eh", 2, " ")
	 * 											ir� retornar "Esta eh uma frase de exemplo"
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
	 * Sobrecarga do m�todo 'put(String text, String word, int pos, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String put(String text, String word, int pos)
		{ return put(text, word, pos, " "); }
	
	/**
	 * Insere o token informado na posi��o informada dentro da
	 * String informada, empurrando os tokens daquela posi��o
	 * em diante 1 posi��o para frente.
	 * 
	 * @param		text				a String que ser� alterada.
	 * @param		pos					a posi��o onde ser� inserido o novo token.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.insert("Esta � uma frase de exemplo", "n�o", 2, " ")
	 * 											ir� retornar "Esta n�o � uma frase de exemplo"
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
	 * Sobrecarga do m�todo 'insert(String text, String word, int pos, String delimiter)' 
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String insert(String text, String word, int pos)
		{ return insert(text, word, pos, " "); }
	
	/**
	 * Deleta da String informada, o token da posi��o informada.
	 * 
	 * @param		text				a String que ser� alterada.
	 * @param		startPos		a posi��o inicial de onde ser� deletado o(s) token(s).
	 * @param		endPos			a posi��o final de onde ser� deletado o(s) token(s).
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.delete("Esta � uma frase de exemplo", 5, " ")
	 * 											ir� retornar "Esta � uma frase exemplo"
	 * 											.delete("Esta � uma frase de exemplo", 2, 5, " ")
	 * 											ir� retornar "Esta exemplo"
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
	 * Sobrecarga do m�todo 'delete(String text, int startPos, int endPos, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String delete(String text, int startPos, int lenght)
		{ return delete(text, startPos, lenght, " ");	}
	
	/**
	 * Sobrecarga do m�todo 'delete(String text, int startPos, int endPos, String delimiter)'
	 * onde n�o � preciso informar o 'endPos' (� passado o valor de 'startPos' por padr�o). 
	 */
	public static String delete(String text, int startPos, String delimiter)
		{ return delete(text, startPos, startPos, delimiter); }
	
	/**
	 * Sobrecarga do m�todo 'delete(String text, int startPos, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String delete(String text, int startPos)
		{ return delete(text, startPos, " "); }
	
	/**
	 * Remove da String informada, o token informado,
	 * se ele fizer parte da String informada.
	 * 
	 * @param		text				a String que ser� alterada.
	 * @param		word				token � ser removido da String informada.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.remove("Esta � uma frase de exemplo", "de", " ")
	 * 											ir� retornar "Esta � uma frase exemplo"
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
	 * Sobrecarga do m�todo 'remove(String text, String word, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String remove(String text, String word)
		{ return remove(text, word, " "); }
	
	/**
	 * Substitui um token por outro na String informada.
	 * 
	 * @param		text				a String que ser� alterada.
	 * @param		target			o token � ser localizado.
	 * @param		replace			o token � ser inserido no lugar do token localizado. 
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.replace("Esta � uma frase de exemplo", "frase", "mensagem", " ")
	 * 											ir� retornar "Esta � uma mensagem exemplo"
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
	 * Sobrecarga do m�todo 'replace(String text, String target, String replace, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String replace(String text, String target, String replace)
		{ return replace(text, target, replace, " "); }

	/**
	 * Localiza um token dentro da String informada, e retorna o valor da sua posi��o.
	 * Se 'pos' for '2' e houver mais de 1 ocorr�ncia do token informado, retorna a
	 * posi��o da segunda ocorr�ncia desse token.
	 * Se 'pos' for '0', retorna o total de vezes que o token informado foi encontrado.
	 * 
	 * @param		text				a String que ser� alterada.
	 * @param		word				o token � ser localizado.
	 * @param		pos					a ocorr�ncia desse token que dever� retornar o valor. 
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.find("Esta � uma frase de exemplo de teste", "de", 0, " ")
	 * 											ir� retornar '2' pois h� 2 ocorr�ncias do token 'de' na String informada.
	 * 											.find("Esta � uma frase de exemplo de teste", "frase", 1, " ")
	 * 											ir� retornar '4' pois a primeira ocorr�ncia do token 'frase'
	 * 											 est� na posi��o 4 da String informada.
	 * 											.find("Esta � uma frase de exemplo de teste", "de", 2, " ")
	 * 											ir� retornar '7' pois a segunda ocorr�ncia do token 'de'
	 * 											 est� na posi��o 7 da String informada.
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
	 * Sobrecarga do m�todo 'find(String text, String word, int pos, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static int find(String text, String word, int pos)
		{ return find(text, word, pos, " "); }

	/**
	 * Sobrecarga do m�todo 'find(String text, String word, int pos, String delimiter)'
	 * onde n�o � preciso informar a 'pos' (� passado o valor '1' por padr�o). 
	 */
	public static int find(String text, String word, String delimiter)
		{ return find(text, word, 1, delimiter); }

	/**
	 * Sobrecarga do m�todo 'find(String text, String word, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static int find(String text, String word)
		{	return find(text, word, 1, " "); }

	/**
	 * Retorna 'true' se o token informado estiver presente na String informada.
	 * 
	 * @param		text				a String que ser� alterada.
	 * @param		word				o token � ser localizado.
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.find("Esta � uma frase de exemplo", "frase", " ")
	 * 											ir� retornar 'true' pois o token 'frase' es� presente na String informada.
	 */
	public static Boolean ison(String text, String word, String delimiter)
		{ return (find(text, word, delimiter) > 0); }

	/**
	 * Sobrecarga do m�todo 'ison(String text, String word, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static Boolean ison(String text, String word)
		{ return ison(text, word, " "); }

	/**
	 * Localiza um token dentro da String informada, usando um 'wildcard'
	 * e retorna o valor da sua posi��o.
	 * Se 'pos' for '2' e houver mais de 1 ocorr�ncia do token informado, retorna a
	 * posi��o da segunda ocorr�ncia desse token.
	 * Se 'pos' for '0', retorna o total de vezes que o token informado foi encontrado.
	 * 
	 * @param		text				a String que ser� alterada.
	 * @param		wildcard		o wildcard � ser localizado dentro dos tokens da String informada.
	 * @param		pos					a ocorr�ncia desse token que dever� retornar o valor. 
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.wildcard("Esta � uma frase de exemplo", "*s*", 0, " ")
	 * 											ir� retornar '2' pois h� 2 tokens que se encaixam com o wildcard '*s*'
	 * 											.wildcard("Esta � uma frase de exemplo", "*s*", 1, " ")
	 * 											ir� retornar 'Esta' pois � o primeiro token encontrado na frase que
	 * 											se encaixa com o wildcard '*s*'
	 * 											.wildcard("Esta � uma frase de exemplo", "*s*", 2, " ")
	 * 											ir� retornar 'frase' pois � o segundo token encontrado na frase que
	 * 											se encaixa com o wildcard '*s*'
	 */
	public static String wildcard(String text, String wildcard, int pos, String delimiter) {
		String[] ttokens = text.split(delimiter);
		if (pos < 0)
			pos = 1;
		int total = 0;
		ltokens.clear();
		for (int n = 0, p = 0; n < ttokens.length; n++)
			if (Misc.iswm(ttokens[n], wildcard)) {
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
	 * Sobrecarga do m�todo 'wildcard(String text, String wildcard, int pos, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String wildcard(String text, String wildcard, int pos)
	  { return wildcard(text, wildcard, pos, " "); }
	
	/**
	 * Sobrecarga do m�todo 'wildcard(String text, String wildcard, String delimiter)'
	 * onde n�o � preciso informar a 'pos' (� passado o valor '1' por padr�o). 
	 */
	public static String wildcard(String text, String wildcard, String delimiter)
		{ return wildcard(text, wildcard, 1, delimiter); }
	
	/**
	 * Sobrecarga do m�todo 'wildcard(String text, String wildcard, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String wildcard(String text, String wildcard)
		{ return wildcard(text, wildcard, 1, " "); }
	
	/**
	 * Localiza um token dentro da String informada, que tenha parte
	 * do par�metro 'match' no seu conte�do.
	 * Se 'pos' for '2' e houver mais de 1 ocorr�ncia do token informado, retorna a
	 * posi��o da segunda ocorr�ncia desse token.
	 * Se 'pos' for '0', retorna o total de vezes que o token informado foi encontrado.
	 * 
	 * @param		text				a String que ser� alterada.
	 * @param		match				o conteudo � ser localizado dentro dos tokens da String informada.
	 * @param		pos					a ocorr�ncia desse token que dever� retornar o valor. 
	 * @param		delimiter		o delimitador dos tokens.
	 * 
	 * @return							.match("Esta � uma frase de exemplo", "s", 0, " ")
	 * 											ir� retornar '2' pois h� 2 tokens que possuem 's' no seu conte�do.
	 * 											.match("Esta � uma frase de exemplo", "s", 1, " ")
	 * 											ir� retornar 'Esta' pois � o primeiro token encontrado na frase que
	 * 											possui 's' no seu conte�do.
	 * 											.match("Esta � uma frase de exemplo", "s", 2, " ")
	 * 											ir� retornar 'frase' pois � o segundo token encontrado na frase que
	 * 											possui 's' no seu conte�do.
	 */
	public static String match(String text, String match, int pos, String delimiter)
		{ return wildcard(text, "*" + match + "*", pos, delimiter); }
	
	/**
	 * Sobrecarga do m�todo 'match(String text, String match, int pos, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String match(String text, String match, int pos)
	  { return match(text, match, pos, " "); }
	
	/**
	 * Sobrecarga do m�todo 'match(String text, String match, int pos, String delimiter)'
	 * onde n�o � preciso informar a 'pos' (� passado o valor '1' por padr�o). 
	 */
	public static String match(String text, String match, String delimiter)
		{ return match(text, match, 1, delimiter); }
	
	/**
	 * Sobrecarga do m�todo 'match(String text, String match, String delimiter)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String match(String text, String match)
		{ return match(text, match, 1, " "); }
	
	/**
	 * Organiza os tokens em ordem alfabetica.
	 * 
	 * @param		text				a String que ser� alterada.
	 * @param		delimiter		o delimitador dos tokens.
	 * @param		reverse			especifique '1' para ordem decrescente.
	 * 
	 * @return							.sort("Steve Maneca Jo�o Pedro Alberto Maneca Iona Jos�", " ")
	 * 											retorna "Alberto Iona Jos� Jo�o Maneca Maneca Pedro Steve"
	 * 											.sort("Steve Maneca Jo�o Pedro Alberto Maneca Iona Jos�", " ", 1)
	 * 											retorna "Steve Pedro Maneca Maneca Jo�o Jos� Iona Alberto"
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
	 * Sobrecarga do m�todo 'sort(String text, String delimiter, int reverse)'
	 * onde n�o � preciso informar o valor de reverse (� passado '0' por padr�o). 
	 */
	public static String sort(String text, String delimiter)
		{ return sort(text, delimiter, 0); }

	/**
	 * Sobrecarga do m�todo 'sort(String text, String delimiter, int reverse)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String sort(String text, int reverse)
		{ return sort(text, " ", reverse); }

	/**
	 * Sobrecarga do m�todo 'sort(String text, int reverse)'
	 * onde n�o � preciso informar o delimitador (� passado o 'espa�o' por padr�o). 
	 */
	public static String sort(String text)
		{ return sort(text, " ", 0); }
}