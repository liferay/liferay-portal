/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsConsoleTextLoader;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang.StringEscapeUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseStandaloneBuildTestrayCaseResult
	extends BuildTestrayCaseResult {

	public BaseStandaloneBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuildReport topLevelBuildReport) {

		super(testrayBuild, topLevelBuildReport);
	}

	@Override
	public String getComponentName() {
		try {
			return JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"testray.case.component", getBatchName());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public String getErrors() {
		return null;
	}

	@Override
	public int getPriority() {
		try {
			String testrayCasePriority = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"testray.case.priority", getBatchName());

			if ((testrayCasePriority != null) &&
				testrayCasePriority.matches("\\d+")) {

				return Integer.parseInt(testrayCasePriority);
			}

			return 5;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public String getTeamName() {
		try {
			return JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"testray.case.team", getBatchName());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		testrayAttachments.add(getTopLevelBuildDatabaseTestrayAttachment());
		testrayAttachments.add(getTopLevelBuildReportTestrayAttachment());
		testrayAttachments.add(getTopLevelJenkinsConsoleTestrayAttachment());
		testrayAttachments.add(getTopLevelJenkinsReportTestrayAttachment());
		testrayAttachments.add(getTopLevelJobSummaryTestrayAttachment());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	@Override
	public String getType() {
		try {
			return JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"testray.case.type", getBatchName());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public String[] getWarnings() {
		return null;
	}

	public void recordTestrayCaseResult(Job job) {
		TestrayBuild testrayBuild = getTestrayBuild();

		TestrayRun testrayRun = TestrayFactory.newTestrayRun(
			testrayBuild, getBatchName(), job.getJobPropertiesFiles());

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		Document document = DocumentHelper.createDocument();

		Element rootElement = document.addElement("testsuite");

		Element environmentsElement = rootElement.addElement("environments");

		for (TestrayRun.Factor factor : testrayRun.getFactors()) {
			Element environmentElement = environmentsElement.addElement(
				"environment");

			environmentElement.addAttribute("type", factor.getName());
			environmentElement.addAttribute("option", factor.getValue());
		}

		Map<String, String> propertiesMap = new HashMap<>();

		TopLevelBuildReport testTopLevelBuildReport = getTopLevelBuildReport();

		propertiesMap.put(
			"testray.build.date",
			testTopLevelBuildReport.getTestrayBuildDateString());

		propertiesMap.put("testray.build.name", testrayBuild.getName());

		TestrayRoutine testrayRoutine = testrayBuild.getTestrayRoutine();

		propertiesMap.put("testray.build.type", testrayRoutine.getName());

		TestrayProductVersion testrayProductVersion =
			testrayBuild.getTestrayProductVersion();

		if (testrayProductVersion != null) {
			propertiesMap.put(
				"testray.product.version", testrayProductVersion.getName());
		}

		TestrayProject testrayProject = testrayBuild.getTestrayProject();

		propertiesMap.put("testray.project.name", testrayProject.getName());

		propertiesMap.put("testray.run.id", testrayRun.getRunIDString());

		addPropertyElements(
			rootElement.addElement("properties"), propertiesMap);

		List<TestrayCaseResult> testrayCaseResults = new ArrayList<>();

		testrayCaseResults.add(this);

		for (TestrayCaseResult testrayCaseResult : testrayCaseResults) {
			try {
				Element testcaseElement = Dom4JUtil.getNewElement("testcase");

				Map<String, String> testcasePropertiesMap = new HashMap<>();

				testcasePropertiesMap.put(
					"testray.case.type.name", testrayCaseResult.getType());
				testcasePropertiesMap.put(
					"testray.component.names",
					testrayCaseResult.getSubcomponentNames());
				testcasePropertiesMap.put(
					"testray.main.component.name",
					testrayCaseResult.getComponentName());
				testcasePropertiesMap.put(
					"testray.team.name", testrayCaseResult.getTeamName());
				testcasePropertiesMap.put(
					"testray.testcase.duration",
					String.valueOf(testrayCaseResult.getDuration()));

				String testrayCaseName = testrayCaseResult.getName();

				if (testrayCaseName.length() > 150) {
					testrayCaseName = testrayCaseName.substring(0, 150);
				}

				testcasePropertiesMap.put(
					"testray.testcase.name", testrayCaseName);

				testcasePropertiesMap.put(
					"testray.testcase.priority",
					String.valueOf(testrayCaseResult.getPriority()));

				TestrayCaseResult.Status testrayCaseStatus =
					testrayCaseResult.getStatus();

				testcasePropertiesMap.put(
					"testray.testcase.status", testrayCaseStatus.getName());

				Element propertiesElement = testcaseElement.addElement(
					"properties");

				addPropertyElements(propertiesElement, testcasePropertiesMap);

				String[] warnings = testrayCaseResult.getWarnings();

				if ((warnings != null) && (warnings.length > 0)) {
					Element warningsPropertyElement =
						propertiesElement.addElement("property");

					warningsPropertyElement.addAttribute(
						"name", "testray.testcase.warnings");
					warningsPropertyElement.addAttribute(
						"value", String.valueOf(warnings.length));

					for (String warning : warnings) {
						Element warningPropertyElement =
							warningsPropertyElement.addElement("value");

						warningPropertyElement.addText(
							StringEscapeUtils.escapeHtml(warning));
					}
				}

				Element attachmentsElement = testcaseElement.addElement(
					"attachments");

				for (TestrayAttachment testrayAttachment :
						testrayCaseResult.getTestrayAttachments()) {

					Element attachmentFileElement =
						attachmentsElement.addElement("file");

					attachmentFileElement.addAttribute(
						"name", testrayAttachment.getName());
					attachmentFileElement.addAttribute(
						"url", testrayAttachment.getURL() + "?authuser=0");
					attachmentFileElement.addAttribute(
						"value", testrayAttachment.getKey() + "?authuser=0");
				}

				String errors = testrayCaseResult.getErrors();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(errors)) {
					Element failureElement = testcaseElement.addElement(
						"failure");

					failureElement.addAttribute("message", errors);
				}

				rootElement.add(testcaseElement);
			}
			catch (RuntimeException runtimeException) {
				System.out.println(runtimeException);
			}
		}

		TestrayServer testrayServer = testrayBuild.getTestrayServer();

		try {
			testrayServer.writeCaseResult(
				getFileName(), Dom4JUtil.format(rootElement));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		long end = JenkinsResultsParserUtil.getCurrentTimeMillis();

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Recorded ", String.valueOf(testrayCaseResults.size()),
				" case results for ", getBatchName(), " in ",
				JenkinsResultsParserUtil.toDurationString(end - start)));
	}

	protected abstract String getBatchName();

	protected abstract String getFileName();

	@Override
	protected TestrayAttachment getTopLevelBuildReportTestrayAttachment() {
		String key = getTopLevelBuildReportKey();
		String name = getTopLevelBuildReportName();

		TestrayAttachment testrayAttachment = getTestrayAttachment(
			getTopLevelBuildReport(), name, key);

		if (testrayAttachment != null) {
			return testrayAttachment;
		}

		final TopLevelBuildReport topLevelBuildReport =
			getTopLevelBuildReport();

		return uploadTestrayAttachment(
			name, key,
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File file = new File(
						getTestrayUploadBaseDir(), "build-report.json");
					File gzipFile = new File(
						getTestrayUploadBaseDir(), "build-report.json.gz");

					if (topLevelBuildReport == null) {
						return null;
					}

					JSONObject buildReportJSONObject =
						topLevelBuildReport.getBuildReportJSONObject();

					if (buildReportJSONObject == null) {
						return null;
					}

					try {
						JenkinsResultsParserUtil.write(
							file, buildReportJSONObject.toString());

						JenkinsResultsParserUtil.gzip(file, gzipFile);
					}
					catch (IOException ioException) {
						throw new RuntimeException(ioException);
					}
					finally {
						JenkinsResultsParserUtil.delete(file);
					}

					if (gzipFile.exists()) {
						return gzipFile;
					}

					return null;
				}

			});
	}

	@Override
	protected TestrayAttachment getTopLevelJenkinsConsoleTestrayAttachment() {
		String key = getTopLevelJenkinsConsoleKey();
		String name = getTopLevelJenkinsConsoleName();

		TestrayAttachment testrayAttachment = getTestrayAttachment(
			getTopLevelBuildReport(), name, key);

		if (testrayAttachment != null) {
			return testrayAttachment;
		}

		final TopLevelBuildReport topLevelBuildReport =
			getTopLevelBuildReport();

		return uploadTestrayAttachment(
			name, key,
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File file = new File(
						getTestrayUploadBaseDir(), "jenkins-console.txt");
					File gzipFile = new File(
						getTestrayUploadBaseDir(), "jenkins-console.txt.gz");

					try {
						String topLevelBuildURL = String.valueOf(
							topLevelBuildReport.getBuildURL());

						JenkinsConsoleTextLoader jenkinsConsoleTextLoader =
							JenkinsConsoleTextLoader.getInstance(
								topLevelBuildURL);

						JenkinsResultsParserUtil.write(
							file, jenkinsConsoleTextLoader.getConsoleText());

						JenkinsResultsParserUtil.gzip(file, gzipFile);
					}
					catch (IOException ioException) {
						throw new RuntimeException(ioException);
					}
					finally {
						JenkinsResultsParserUtil.delete(file);
					}

					if (gzipFile.exists()) {
						return gzipFile;
					}

					return null;
				}

			});
	}

	@Override
	protected TestrayAttachment getTopLevelJenkinsReportTestrayAttachment() {
		String key = getTopLevelJenkinsReportKey();
		String name = getTopLevelJenkinsReportName();

		return getTestrayAttachment(getTopLevelBuildReport(), name, key);
	}

	@Override
	protected TestrayAttachment getTopLevelJobSummaryTestrayAttachment() {
		String key = getTopLevelJobSummaryKey();
		String name = getTopLevelJobSummaryName();

		return getTestrayAttachment(getTopLevelBuildReport(), name, key);
	}

}