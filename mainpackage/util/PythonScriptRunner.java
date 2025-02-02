package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class PythonScriptRunner {
	
	private static String pythonPath;
	private static File pyExeFile = null;
	private static String pyExePath = null;
	private static boolean outputStreamToConsole = false;
	private static int processTimeOutInSecs = 0;
	
	public static void init() {
		if (pyExeFile == null) {
			String iniFormat = "\nIni format:\n\nC:\\Your\\Python\\Project\\Path";
			if (!new File("python.ini").exists())
				throw new RuntimeException("Unable to find 'python.ini' file on project root folder" + iniFormat);
			List<String> lines = MyFile.readAllLinesFromFile("python.ini");
			pythonPath = lines == null || lines.isEmpty() ? null : MyFile.readAllLinesFromFile("python.ini").get(0);
			if (pythonPath == null)
				throw new RuntimeException("Ensure to set the Python Project Path inside the 'python.ini' file" + iniFormat);
			if (!new File(pythonPath).exists())
				throw new RuntimeException("Unable to find \"" + pythonPath + "\" folder. Setup a valid Python Project path inside the 'python.ini' file");
			pyExeFile = new File(pythonPath.replace("\\", "/") + "/.venv/Scripts/python.exe");
			pyExePath = pyExeFile.getAbsolutePath();
			if (!pyExeFile.exists())
				throw new RuntimeException("Unable to find \"" + pyExePath + "\"");
		}
	}
	
	public static String getPythonPath() {
		return pythonPath;
	}

	public static List<String> runScript(File pythonScript) throws Exception {
		return runScript(pythonScript, "");
	}

	public static List<String> runScript(File pythonScript, String ... scriptArgs) throws Exception {
		if (pyExeFile == null)
			throw new RuntimeException("Run PythonScriptRunner.init() first");
		boolean haveArgs = !scriptArgs[0].isBlank();
		String[] args = new String[!haveArgs ? 2 : scriptArgs.length + 2];
		args[0] = pyExePath; 
		args[1] = pythonScript.getAbsolutePath();
		for (int n = 0; haveArgs && n < scriptArgs.length; n++)
			args[2 + n] = scriptArgs[n];
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		List<String> outputStream = new LinkedList<>();
    processBuilder.environment().put("PYTHONIOENCODING", "UTF-8");
    Process process = processBuilder.start();
    if (outputStreamToConsole) {
      processBuilder.redirectErrorStream(true);
	    InputStream inputStream = process.getInputStream();
	    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
	    String line = "";
	    while ((line = reader.readLine()) != null)
	    	outputStream.add(line);
    }
    if (processTimeOutInSecs == 0)
    	process.waitFor();
    else if (!process.waitFor(processTimeOutInSecs, TimeUnit.SECONDS))
    	process.destroy();
    if (outputStreamToConsole && outputStream != null)
    	for (String s : outputStream)
    		System.out.println(s);
    return outputStream.isEmpty() ? null : outputStream;
	}
	
	public static void setOutputStreamToConsole(boolean state) {
		outputStreamToConsole = state;
	}
	
	public static void setProcessTimeOutInSecs(int timeout) {
		processTimeOutInSecs = timeout;
	}
	
}
