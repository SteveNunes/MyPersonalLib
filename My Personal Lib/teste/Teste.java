package teste;

public class Teste {

	public static void main(String[] args) {
		String text = "Steve 1Nunes \\(da Silva";
		System.out.println(text.matches(".*\\d+.*"));
	}

}
