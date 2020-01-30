package de.unituebingen.validator.fits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 * Wrapper for executing FITS with ProcessBuilder.
 *
 */
public class FitsValidation {
	/** File to be analyzed by fits */
	private String inputFilePath;
	/** Designated tool output file */
	private String outputFilePath;
	/** Temp file for holding process output */
	private File output;

	/**
	 * Constructor.
	 * 
	 * @param inputFilePath
	 *            The path of the file to be validated.
	 * @param outputFilePath
	 *            The path of the file to be used for fits output.
	 */
	public FitsValidation(String inputFilePath, String outputFilePath) {
		super();
		this.inputFilePath = inputFilePath;
		this.outputFilePath = outputFilePath;
	}

	/**
	 * Creates a new process builder to execute the fits shell script.
	 * 
	 * @param fitsScriptPath
	 *            The path to the start script.
	 * @return The newly created procees builder.
	 * @throws IOException
	 */
	public ProcessBuilder getProcessBuilder(String fitsScriptPath) throws IOException {
		List<String> commandList = new ArrayList<>();
		commandList.add("sh");
		commandList.add(fitsScriptPath);
		commandList.add("-i");
		commandList.add(inputFilePath);
		commandList.add("-o");
		commandList.add(outputFilePath);

		output = new File(outputFilePath + "_output");
		FileUtils.touch(output);
		ProcessBuilder processBuilder = new ProcessBuilder(commandList).redirectOutput(this.output)
				.redirectErrorStream(true);
		return processBuilder;
	}

	/**
	 * Delete output files (fits and process output)
	 */
	public void clearOutput() {
		FileUtils.deleteQuietly(this.output);
		FileUtils.deleteQuietly(new File(this.outputFilePath));
	}

	public String getInputFilePath() {
		return inputFilePath;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

}
