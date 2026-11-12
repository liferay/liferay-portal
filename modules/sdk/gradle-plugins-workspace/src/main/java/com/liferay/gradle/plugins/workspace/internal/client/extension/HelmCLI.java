/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.gradle.plugins.workspace.internal.client.extension;

import com.liferay.gradle.plugins.workspace.internal.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gradle.api.GradleException;

/**
 * @author Gregory Amerson
 */
public class HelmCLI {

	public HelmCLI(String executable) {
		_executable = executable;
	}

	public File packageChart(File chartDir, File destinationDir)
		throws InterruptedException, IOException {

		String output = _run(
			"package", chartDir.getPath(), "--destination",
			destinationDir.getPath());

		Matcher matcher = _savedToPattern.matcher(output);

		if (!matcher.find()) {
			throw new GradleException(
				"Unable to find the chart archive in the output of \"helm " +
					"package\":\n" + output);
		}

		return new File(matcher.group(1));
	}

	public File pull(String chart, String version, File destinationDir)
		throws InterruptedException, IOException {

		List<String> args = new ArrayList<>(
			Arrays.asList(
				"pull", chart, "--untar", "--untardir",
				destinationDir.getPath()));

		if (!StringUtil.isBlank(version)) {
			Collections.addAll(args, "--version", version);
		}

		_addPlainHTTP(args, chart);

		_run(args.toArray(new String[0]));

		File[] chartDirs = destinationDir.listFiles(File::isDirectory);

		if ((chartDirs == null) || (chartDirs.length != 1)) {
			throw new GradleException(
				"Unable to find the chart that \"helm pull " + chart +
					"\" extracted");
		}

		return chartDirs[0];
	}

	public String push(File chartFile, String repository)
		throws InterruptedException, IOException {

		List<String> args = new ArrayList<>(
			Arrays.asList("push", chartFile.getPath(), repository));

		_addPlainHTTP(args, repository);

		String output = _run(args.toArray(new String[0]));

		Matcher matcher = _digestPattern.matcher(output);

		if (!matcher.find()) {
			throw new GradleException(
				"Unable to find the digest in the output of \"helm push\":\n" +
					output);
		}

		return matcher.group(1);
	}

	public String version() throws InterruptedException, IOException {
		return _run("version", "--short");
	}

	private void _addPlainHTTP(List<String> args, String reference) {
		String host = reference.replaceFirst("^oci://", "");

		int index = host.indexOf('/');

		if (index >= 0) {
			host = host.substring(0, index);
		}

		index = host.lastIndexOf(':');

		if (index >= 0) {
			host = host.substring(0, index);
		}

		if (host.equals("127.0.0.1") || host.equals("localhost")) {
			args.add("--plain-http");
		}
	}

	private String _run(String... args)
		throws InterruptedException, IOException {

		List<String> command = new ArrayList<>();

		command.add(_executable);

		Collections.addAll(command, args);

		ProcessBuilder processBuilder = new ProcessBuilder(command);

		processBuilder.redirectErrorStream(true);

		Process process = null;

		try {
			process = processBuilder.start();
		}
		catch (IOException ioException) {
			throw new GradleException(
				"Unable to run \"" + _executable + "\". Install Helm, or set " +
					"the liferay.workspace.client.extension.helm property to " +
						"its path.",
				ioException);
		}

		String output = null;

		try (InputStream inputStream = process.getInputStream()) {
			output = StringUtil.read(inputStream);
		}

		if (process.waitFor() != 0) {
			throw new GradleException(
				"Unable to run \"" + String.join(" ", command) + "\":\n" +
					output);
		}

		return output;
	}

	private static final Pattern _digestPattern = Pattern.compile(
		"Digest: (sha256:[0-9a-f]+)");
	private static final Pattern _savedToPattern = Pattern.compile(
		"saved it to: (.+)");

	private final String _executable;

}