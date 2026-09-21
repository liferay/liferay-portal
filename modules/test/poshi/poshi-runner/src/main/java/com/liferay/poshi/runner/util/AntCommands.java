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
import java.io.InputStream;
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

		Process process = new BufferedProcess(
			_BUFFER_SIZE,
			runtime.exec(sb.toString(), null, new File(projectDirName)));

		process.waitFor();

		int exitValue = process.exitValue();

		StringBuilder outputSB = new StringBuilder();

		_readInputStream(process.getInputStream(), outputSB);
		_readInputStream(process.getErrorStream(), outputSB);

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

	private void _readInputStream(InputStream inputStream, StringBuilder sb)
		throws Exception {

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream))) {

			String line = null;

			while ((line = bufferedReader.readLine()) != null) {
				System.out.println(line);

				sb.append(line);
				sb.append("\n");
			}
		}
	}

	private static final int _BUFFER_SIZE = 2000000;

	private static final int _MAX_OUTPUT_LENGTH = 5000;

	private final String _fileName;
	private final String _target;

}