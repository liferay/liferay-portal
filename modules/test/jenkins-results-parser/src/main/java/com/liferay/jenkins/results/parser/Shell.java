/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * @author Kenji Heigel
 */
public class Shell {

	public static ExecutionResult execute(ExecutionRequest executionRequest)
		throws IOException, TimeoutException {

		return _shell.doExecute(executionRequest);
	}

	public static void setInstance(Shell shell) {
		_shell = shell;
	}

	public static class ExecutionRequest {

		public ExecutionRequest(
			File baseDir, String[] commands, boolean exitOnFirstFail,
			boolean printCommands, long timeout) {

			_baseDir = baseDir;
			_commands = commands;
			_exitOnFirstFail = exitOnFirstFail;
			_printCommands = printCommands;
			_timeout = timeout;
		}

		public File getBaseDir() {
			return _baseDir;
		}

		public String[] getCommands() {
			return _commands;
		}

		public long getTimeout() {
			return _timeout;
		}

		public boolean isExitOnFirstFail() {
			return _exitOnFirstFail;
		}

		public boolean isPrintCommands() {
			return _printCommands;
		}

		private final File _baseDir;
		private final String[] _commands;
		private final boolean _exitOnFirstFail;
		private final boolean _printCommands;
		private final long _timeout;

	}

	public static class ExecutionResult {

		public ExecutionResult(
			int exitValue, String standardError, String standardOut) {

			_exitValue = exitValue;
			_standardError = standardError;
			_standardOut = standardOut;
		}

		public int getExitValue() {
			return _exitValue;
		}

		public String getStandardError() {
			return _standardError;
		}

		public String getStandardOut() {
			return _standardOut;
		}

		private final int _exitValue;
		private final String _standardError;
		private final String _standardOut;

	}

	protected ExecutionResult doExecute(ExecutionRequest executionRequest)
		throws IOException, TimeoutException {

		String[] commands = executionRequest.getCommands();

		if (executionRequest.isPrintCommands()) {
			System.out.print("Executing commands: ");

			for (String command : commands) {
				System.out.println(command);
			}
		}

		String[] bashCommands = new String[3];

		if (JenkinsResultsParserUtil.isWindows()) {
			bashCommands[0] = "C:\\Program Files\\Git\\bin\\sh.exe";
		}
		else {
			bashCommands[0] = "/bin/sh";
		}

		bashCommands[1] = "-c";

		String commandTerminator = ";";

		if (executionRequest.isExitOnFirstFail()) {
			commandTerminator = "&&";
		}

		StringBuffer sb = new StringBuffer();

		if (JenkinsResultsParserUtil.isWindows()) {
			sb.append("export GIT_ASK_YESNO=false");
			sb.append(commandTerminator);
			sb.append(" ");
		}

		for (String command : commands) {
			if (JenkinsResultsParserUtil.isWindows()) {
				command = command.replaceAll("\\(", "\\\\\\\\(");
				command = command.replaceAll("\\)", "\\\\\\\\)");
			}

			sb.append(command);
			sb.append(commandTerminator);
			sb.append(" ");
		}

		sb.append("echo Finished executing Bash commands.\n");

		bashCommands[2] = sb.toString();

		ProcessBuilder processBuilder = new ProcessBuilder(bashCommands);

		File baseDir = executionRequest.getBaseDir();

		processBuilder.directory(baseDir.getAbsoluteFile());

		Process process = new BufferedProcess(processBuilder.start());

		long timeout = executionRequest.getTimeout();

		Thread currentThread = Thread.currentThread();
		long duration = 0;
		int returnCode = -1;
		long start = System.currentTimeMillis();

		while (true) {
			try {
				if (currentThread.isInterrupted()) {
					returnCode = 1;

					process.destroyForcibly();

					break;
				}

				returnCode = process.exitValue();

				if (returnCode == 0) {
					String standardOut =
						JenkinsResultsParserUtil.readInputStream(
							process.getInputStream(), true);

					duration = System.currentTimeMillis() - start;

					while (!standardOut.contains(
								"Finished executing Bash commands.") &&
						   (duration < timeout)) {

						JenkinsResultsParserUtil.sleep(10);

						standardOut = JenkinsResultsParserUtil.readInputStream(
							process.getInputStream(), true);

						duration = System.currentTimeMillis() - start;
					}
				}

				break;
			}
			catch (IllegalThreadStateException illegalThreadStateException) {
				duration = System.currentTimeMillis() - start;

				if (duration >= timeout) {
					process.destroy();

					throw new TimeoutException(
						"Timeout occurred while executing Bash commands: " +
							Arrays.toString(commands));
				}

				returnCode = -1;

				JenkinsResultsParserUtil.sleep(100);
			}
		}

		String standardError = JenkinsResultsParserUtil.readInputStream(
			process.getErrorStream(), true);

		String standardOut = JenkinsResultsParserUtil.readInputStream(
			process.getInputStream(), true);

		if (JenkinsResultsParserUtil.debug) {
			System.out.println("Output stream: " + standardOut);
		}

		if (JenkinsResultsParserUtil.debug && (returnCode != 0)) {
			System.out.println("Error stream: " + standardError);
		}

		return new ExecutionResult(returnCode, standardError, standardOut);
	}

	private static volatile Shell _shell = new Shell();

}