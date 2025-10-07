/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.testray.TestrayBuild;
import com.liferay.jenkins.results.parser.testray.TestrayCaseResult;
import com.liferay.jenkins.results.parser.testray.TestrayFactory;
import com.liferay.jenkins.results.parser.testray.TestrayServer;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Charlotte Wong
 * @author Kyle Miho
 */
public class GenerateTestrayCSVUtil {

	public static void generate(
		String projectBuildDir, long projectTestrayBuildId,
		URL testrayServerURL) {

		if (testrayServerURL == null) {
			try {
				testrayServerURL = new URL(
					JenkinsResultsParserUtil.getBuildProperty(
						"testray.server.url"));
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		TestrayServer testrayServer = TestrayFactory.newTestrayServer(
			String.valueOf(testrayServerURL));

		TestrayBuild testrayBuild = testrayServer.getTestrayBuildByID(
			projectTestrayBuildId);

		System.out.println("Generating Testray CSV.");

		StringBuilder sb = new StringBuilder();

		sb.append(
			JenkinsResultsParserUtil.join(
				_CSV_DELIMITER, "Case Name", "Case History URL", "Failure Type",
				"Error Message"));
		sb.append("\n");

		List<TestrayCaseResult> testrayCaseResults = new ArrayList<>();

		for (TestrayCaseResult testrayCaseResult :
				testrayBuild.getFailedTestrayCaseResults()) {

			if (Objects.equals(
					testrayCaseResult.getName(), "Top Level Build")) {

				continue;
			}

			testrayCaseResults.add(testrayCaseResult);
		}

		if (testrayCaseResults.isEmpty()) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"There are no Testray case results to report. Testray may ",
					"not have imported the results yet or the results ",
					"contained no failures."));

			return;
		}

		sb.append(
			_generate(testrayCaseResults, TestrayCaseResult.ErrorType.UNIQUE));
		sb.append("\n");
		sb.append(
			_generate(
				testrayCaseResults, TestrayCaseResult.ErrorType.DID_NOT_RUN));
		sb.append("\n");
		sb.append(
			_generate(testrayCaseResults, TestrayCaseResult.ErrorType.COMMON));

		try {
			System.out.println("Setting testray results to: " + sb.toString());

			JenkinsResultsParserUtil.write(
				new File(
					projectBuildDir,
					JenkinsResultsParserUtil.combine(
						"testray-results-",
						String.valueOf(testrayBuild.getID()), ".csv")),
				sb.toString());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public static void generate(
		String projectBuildDir, String projectTestrayBuildId) {

		generate(projectBuildDir, Long.valueOf(projectTestrayBuildId), null);
	}

	private static String _cleanCSVData(String string) {
		return string.replace(_CSV_DELIMITER, ".");
	}

	private static String _generate(
		List<TestrayCaseResult> testrayCaseResults,
		TestrayCaseResult.ErrorType testrayCaseResultErrorType) {

		System.out.println(
			"Parsing Testray case results for " +
				testrayCaseResultErrorType.toString() + ".");

		StringBuilder sb = new StringBuilder();

		for (TestrayCaseResult testrayCaseResult : testrayCaseResults) {
			if (testrayCaseResult.getErrorType() !=
					testrayCaseResultErrorType) {

				continue;
			}

			sb.append(
				_generateCSV(testrayCaseResult, testrayCaseResultErrorType));
			sb.append("\n");
		}

		if (sb.length() == 0) {
			sb.append(
				JenkinsResultsParserUtil.join(
					_CSV_DELIMITER, "NONE", "N/A",
					testrayCaseResultErrorType.toString(), "N/A"));
			sb.append("\n");
		}

		return JenkinsResultsParserUtil.combine(
			testrayCaseResultErrorType.toString(), " Failures\n",
			sb.toString());
	}

	private static String _generateCSV(
		TestrayCaseResult testrayCaseResult,
		TestrayCaseResult.ErrorType testrayCaseResultType) {

		return JenkinsResultsParserUtil.join(
			_CSV_DELIMITER, _cleanCSVData(testrayCaseResult.getName()),
			_cleanCSVData(String.valueOf(testrayCaseResult.getHistoryURL())),
			_cleanCSVData(String.valueOf(testrayCaseResultType)),
			_cleanCSVData(testrayCaseResult.getErrors()));
	}

	private static final String _CSV_DELIMITER = ",";

}