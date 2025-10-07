/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.metrics;

import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.ParallelExecutor;

import java.io.File;
import java.io.IOException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class BuildArchiverUtil {

	public static void archive(
		String startDateString, String endDateString, String outputDirPath) {

		try {
			String groovyScript = JenkinsResultsParserUtil.readInputStream(
				JenkinsResultsParserUtil.class.getResourceAsStream(
					"dependencies/get-build-data.groovy"));

			groovyScript = groovyScript.replaceFirst(
				"new Date\\(\\)",
				"Date.parse(\"yyyyMMdd hh:mm:ss\", \"" + endDateString +
					" 00:00:00\")");
			groovyScript = groovyScript.replaceFirst(
				"startDate\\.format\\(\"yyyyMMdd\"\\) \\+ \"",
				"\"" + startDateString);

			System.out.println(groovyScript);

			_recordGroovyScriptResponses(
				JenkinsResultsParserUtil.getJenkinsMasters(
					_buildProperties, 12, 2, "test-1"),
				groovyScript, new File(outputDirPath));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get get-build-data.groovy", ioException);
		}
	}

	public static void archiveOneDay(String startDateString) {
		String outputDirPath = null;

		String propertyKey = "archive.ci.build.data.tmp.dir";

		try {
			outputDirPath = _buildProperties.getProperty(propertyKey);
		}
		catch (Exception exception) {
			System.out.println("Unable to get property " + propertyKey);
		}

		if (outputDirPath == null) {
			Properties buildProperties = null;

			try {
				buildProperties = JenkinsResultsParserUtil.getBuildProperties();
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}

			outputDirPath = buildProperties.getProperty("jenkins.tmp.dir");
		}

		archiveOneDay(startDateString, outputDirPath);
	}

	public static void archiveOneDay(
		String startDateString, String outputDirPath) {

		LocalDateTime localDateTime = LocalDateTime.parse(
			startDateString + " 00:00:00",
			DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));

		localDateTime = localDateTime.plusDays(1);

		String endDateString = localDateTime.format(
			DateTimeFormatter.ofPattern("yyyyMMdd"));

		archive(
			startDateString, endDateString,
			outputDirPath + "/" + startDateString + "/");
	}

	public static boolean isValidJSON(String json) {
		try {
			new JSONObject(json);
		}
		catch (JSONException jsonException1) {
			try {
				new JSONArray(json);
			}
			catch (JSONException jsonException2) {
				return false;
			}
		}

		return true;
	}

	private static Boolean _call(
		JenkinsMaster jenkinsMaster, String groovyScript, File outputDir) {

		try {
			String response = JenkinsResultsParserUtil.executeJenkinsScript(
				jenkinsMaster.getName(), groovyScript, true);

			if (response == null) {
				return false;
			}

			response = response.trim();

			if (response.isEmpty()) {
				return false;
			}

			File file = new File(
				outputDir, jenkinsMaster.getName() + "_builds.json");

			if (file.exists()) {
				String fileContent = JenkinsResultsParserUtil.read(file);

				if ((fileContent != null) &&
					(fileContent.equals(response) ||
					 (fileContent.length() > response.length()))) {

					System.out.println(
						"Complete data for " + file + " already exists");

					return false;
				}
			}

			if (isValidJSON(response)) {
				System.out.println(
					"Writing CI data for " + jenkinsMaster.getName() + " to: " +
						file);

				JenkinsResultsParserUtil.write(file, response);

				return true;
			}
		}
		catch (Exception exception) {
			System.out.println(
				"Unable to get data for " + jenkinsMaster.getName());

			exception.printStackTrace();
		}

		return false;
	}

	private static void _recordGroovyScriptResponses(
		List<JenkinsMaster> jenkinsMasters, final String groovyScript,
		final File outputDir) {

		List<Callable<Boolean>> callables = new ArrayList<>();

		for (final JenkinsMaster jenkinsMaster : jenkinsMasters) {
			Callable<Boolean> callable = new Callable<Boolean>() {

				@Override
				public Boolean call() {
					return _call(jenkinsMaster, groovyScript, outputDir);
				}

			};

			callables.add(callable);
		}

		ParallelExecutor<Boolean> parallelExecutor = new ParallelExecutor<>(
			callables, _executorService, "recordGroovyScriptResponses");

		try {
			parallelExecutor.execute();
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	private static final Properties _buildProperties;
	private static final ExecutorService _executorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(8, true);

	static {
		try {
			_buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

}