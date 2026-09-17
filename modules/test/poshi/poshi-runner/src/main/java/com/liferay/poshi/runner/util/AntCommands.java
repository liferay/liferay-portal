/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.util;

import com.liferay.poshi.core.PoshiGetterUtil;
import com.liferay.poshi.core.PoshiProperties;
import com.liferay.poshi.core.util.OSDetector;
import com.liferay.poshi.core.util.StringUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Brian Wing Shun Chan
 * @author Michael Hashimoto
 */
public class AntCommands implements Callable<Void> {

	public static void runCommand(String fileName, String target)
		throws Exception {

		AntCommands antCommands = new AntCommands(fileName, target);

		ExecutorService executorService = Executors.newCachedThreadPool();

		Future<Void> future = executorService.submit(antCommands);

		try {
			future.get(600, TimeUnit.SECONDS);
		}
		catch (ExecutionException | TimeoutException exception) {
			throw exception;
		}
	}

	public AntCommands(String fileName, String target) {
		_fileName = fileName;
		_target = target;
	}

	@Override
	public Void call() throws Exception {
		Runtime runtime = Runtime.getRuntime();

		StringBuilder sb = new StringBuilder();

		String projectDirName = PoshiGetterUtil.getProjectDirName();

		PoshiProperties poshiProperties = PoshiProperties.getPoshiProperties();

		if (!OSDetector.isWindows()) {
			projectDirName = StringUtil.replace(projectDirName, "\\", "//");

			sb.append("/bin/bash ant -f ");
			sb.append(_fileName);
			sb.append(" ");
			sb.append(_target);
			sb.append(" -Dtest.ant.launched.by.selenium=true -Dtest.class=");
			sb.append(poshiProperties.testName);
		}
		else {
			sb.append("cmd /c ant -f ");
			sb.append(_fileName);
			sb.append(" ");
			sb.append(_target);
			sb.append(" -Dtest.ant.launched.by.selenium=true -Dtest.class=");
			sb.append(poshiProperties.testName);
		}

		Process process = runtime.exec(
			sb.toString(), null, new File(projectDirName));

		InputStreamReader inputStreamReader = new InputStreamReader(
			process.getInputStream());

		BufferedReader inputBufferedReader = new BufferedReader(
			inputStreamReader);

		StringBuilder outputSB = new StringBuilder();

		String line = null;

		while ((line = inputBufferedReader.readLine()) != null) {
			System.out.println(line);

			outputSB.append(line);
			outputSB.append("\n");
		}

		InputStreamReader errorStreamReader = new InputStreamReader(
			process.getErrorStream());

		BufferedReader errorBufferedReader = new BufferedReader(
			errorStreamReader);

		while ((line = errorBufferedReader.readLine()) != null) {
			System.out.println(line);

			outputSB.append(line);
			outputSB.append("\n");
		}

		int exitValue = process.waitFor();

		if (exitValue != 0) {
			String outputString = outputSB.toString();

			if (outputString.length() > _MAX_OUTPUT_LENGTH) {
				outputString = outputString.substring(
					outputString.length() - _MAX_OUTPUT_LENGTH);
			}

			throw new Exception(
				StringUtil.combine(
					"Ant command \"", sb.toString(),
					"\" failed with exit value ", String.valueOf(exitValue),
					"\n", outputString));
		}

		return null;
	}

	private static final int _MAX_OUTPUT_LENGTH = 5000;

	private final String _fileName;
	private final String _target;

}