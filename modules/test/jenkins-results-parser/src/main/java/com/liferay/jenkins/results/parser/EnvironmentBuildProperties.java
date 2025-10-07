/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import java.net.URI;
import java.net.URL;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Peter Yoo
 */
public class EnvironmentBuildProperties extends Properties {

	public static Environment getCurrentEnvironment() {
		String masterNetworkName = System.getenv("MASTER_NETWORK_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(masterNetworkName) &&
			(masterNetworkName.equals("aws-network") ||
			 masterNetworkName.equals("gcp-network"))) {

			return Environment.AWS;
		}

		return Environment.DB;
	}

	public static String toURLString(File file) throws IOException {
		URI uri = file.toURI();

		URL url = uri.toURL();

		return url.toString();
	}

	public EnvironmentBuildProperties(Environment environment, String urlString)
		throws IOException {

		this(environment, urlString, false);
	}

	public EnvironmentBuildProperties(
			Environment environment, String urlString, boolean checkCache)
		throws IOException {

		_environment = environment;

		urlString = JenkinsResultsParserUtil.getLocalURL(urlString);

		try {
			String contents = _toString(urlString, checkCache);

			load(new StringReader(contents));

			return;
		}
		catch (IOException ioException) {
		}

		Matcher matcher = _propertiesURLPattern.matcher(urlString);

		if (!matcher.matches()) {
			return;
		}

		try {
			String content = _toString(
				JenkinsResultsParserUtil.combine(
					matcher.group(1), "-shared", matcher.group(2)),
				checkCache);

			load(new StringReader(content));
		}
		catch (IOException ioException) {
			return;
		}

		try {
			String content = _toString(
				JenkinsResultsParserUtil.combine(
					matcher.group(1), "-", environment.getExtension(),
					matcher.group(2)),
				checkCache);

			load(new StringReader(content));
		}
		catch (IOException ioException) {
		}
	}

	public EnvironmentBuildProperties(File file) throws IOException {
		this(getCurrentEnvironment(), toURLString(file), false);
	}

	public EnvironmentBuildProperties(String urlString) throws IOException {
		this(getCurrentEnvironment(), urlString, false);
	}

	public Environment getEnvironment() {
		return _environment;
	}

	public void store(File file) throws IOException {
		String comments = _generateComments(file);

		if (file.exists()) {
			file.delete();
		}

		try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
			store(fileOutputStream, comments);
		}

		Matcher matcher = _environmentBuildPropertiesFileNamePattern.matcher(
			file.getName());

		if (!matcher.matches()) {
			throw new RuntimeException(
				"Unable to parse properties file name " + file.getName());
		}

		String xmlFileName = JenkinsResultsParserUtil.combine(
			matcher.group(1), "-", matcher.group(2), ".xml");

		File xmlFile = new File(file.getParentFile(), xmlFileName);

		if (xmlFile.exists()) {
			xmlFile.delete();
		}

		try (FileOutputStream xmlFileOutputStream = new FileOutputStream(
				xmlFile)) {

			storeToXML(xmlFileOutputStream, comments);
		}
	}

	public enum Environment {

		AWS("aws-master", "aws"), DB("master", "db");

		public String getBranchName() {
			return _branchName;
		}

		public String getExtension() {
			return _extension;
		}

		private Environment(String branchName, String extension) {
			_branchName = branchName;
			_extension = extension;
		}

		private final String _branchName;
		private final String _extension;

	}

	private String _generateComments(File file) {
		StringBuilder sb = new StringBuilder();

		sb.append("Generated ");
		sb.append(file.getPath());
		sb.append(" on ");
		sb.append(_simpleDateFormat.format(new Date()));

		return sb.toString();
	}

	private String _toString(String url, boolean checkCache)
		throws IOException {

		return JenkinsResultsParserUtil.toString(
			url, checkCache, 0, null, null, 0, 10 * 1000, null, true);
	}

	private static final Pattern _environmentBuildPropertiesFileNamePattern =
		Pattern.compile("(.+)\\.(properties)");
	private static final Pattern _propertiesURLPattern = Pattern.compile(
		"(^.*)(\\.properties[/]*$)");
	private static final SimpleDateFormat _simpleDateFormat =
		new SimpleDateFormat("yyyy-MM-dd hh:mm:ss zzzz");
	private static final long serialVersionUID = 1L;

	private final Environment _environment;

}