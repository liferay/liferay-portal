/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.AxisBuild;
import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.BuildDatabaseUtil;
import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.DownstreamBuild;
import com.liferay.jenkins.results.parser.GitRepositoryFactory;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalWorkspaceGitRepository;
import com.liferay.jenkins.results.parser.QAWebsitesWorkspaceGitRepository;
import com.liferay.jenkins.results.parser.TestClassResult;
import com.liferay.jenkins.results.parser.TestResult;
import com.liferay.jenkins.results.parser.TopLevelBuild;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public class TestrayAttachmentRecorder {

	public void record() {
		if (_recorded) {
			return;
		}

		JenkinsResultsParserUtil.delete(getRecordedFilesBaseDir());

		try {
			_recordJenkinsConsole();

			if (_build instanceof TopLevelBuild) {
				_recordBuildDatabase();
				_recordJobSummary();
				_recordJenkinsReport();
			}
			else {
				_recordDockerLogs();
				_recordFailureMessages();
				_recordGCLogs();
				_recordGradlePluginsFiles();
				_recordJStacks();
				_recordLiferayLogs();
				_recordLiferayOSGiLogs();
				_recordPlaywrightReportFile();
				_recordPoshiReportFiles();
				_recordWarnings();
			}
		}
		catch (IllegalArgumentException illegalArgumentException) {
			System.out.println(
				"Unable to record attachments for: " + _build.getBuildURL());

			illegalArgumentException.printStackTrace();
		}

		_recorded = true;
	}

	protected TestrayAttachmentRecorder(Build build) {
		if (build == null) {
			throw new RuntimeException("Please set a build");
		}

		_build = build;

		BuildDatabase buildDatabase = build.getBuildDatabase();

		String jobVariant = build.getParameterValue("JOB_VARIANT");

		if (jobVariant == null) {
			jobVariant = "";
		}
		else {
			jobVariant += "/";
		}

		_startProperties = buildDatabase.getProperties(
			jobVariant + "start.properties");
	}

	protected File getRecordedFilesBaseDir() {
		String workspace = System.getenv("WORKSPACE");

		if (JenkinsResultsParserUtil.isNullOrEmpty(workspace)) {
			throw new RuntimeException("Please set WORKSPACE");
		}

		return new File(workspace, "testray/recorded_logs");
	}

	protected String getRelativeBuildDirPath() {
		StringBuilder sb = new StringBuilder();

		Date topLevelStartDate = new Date(
			Long.parseLong(
				_startProperties.getProperty("TOP_LEVEL_START_TIME")));

		sb.append(
			JenkinsResultsParserUtil.toDateString(
				topLevelStartDate, "yyyy-MM", "America/Los_Angeles"));

		sb.append("/");
		sb.append(_startProperties.getProperty("TOP_LEVEL_MASTER_HOSTNAME"));
		sb.append("/");
		sb.append(_startProperties.getProperty("TOP_LEVEL_JOB_NAME"));
		sb.append("/");
		sb.append(_startProperties.getProperty("TOP_LEVEL_BUILD_NUMBER"));

		if (!(_build instanceof TopLevelBuild)) {
			sb.append("/");

			sb.append(_build.getJobVariant());

			if (_build instanceof AxisBuild) {
				AxisBuild axisBuild = (AxisBuild)_build;

				sb.append("/");
				sb.append(axisBuild.getAxisNumber());
			}
			else if (_build instanceof DownstreamBuild) {
				DownstreamBuild downstreamBuild = (DownstreamBuild)_build;

				sb.append("/");
				sb.append(downstreamBuild.getAxisVariable());
			}
		}

		return sb.toString();
	}

	private void _copyToRecordedFilesBuildDir(File sourceFile) {
		if (!sourceFile.exists()) {
			return;
		}

		try {
			JenkinsResultsParserUtil.copy(
				sourceFile,
				new File(_getRecordedFilesBuildDir(), sourceFile.getName()));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private List<File> _getLiferayBundlesDirs() {
		List<File> liferayBundlesDirs = new ArrayList<>();

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository == null) {
			return liferayBundlesDirs;
		}

		Properties appServerProperties =
			portalWorkspaceGitRepository.getAppServerProperties();

		File appServerParentDir = new File(
			JenkinsResultsParserUtil.getProperty(
				appServerProperties, "app.server.parent.dir"));

		for (File siblingFile :
				JenkinsResultsParserUtil.findSiblingFiles(
					appServerParentDir, true)) {

			if (!siblingFile.isDirectory()) {
				continue;
			}

			String siblingFileName = siblingFile.getName();

			Matcher matcher = _bundlesDirNamePattern.matcher(siblingFileName);

			if (!matcher.find()) {
				continue;
			}

			liferayBundlesDirs.add(siblingFile);
		}

		return liferayBundlesDirs;
	}

	private int _getLiferayLogMaxSize() {
		int liferayLogMaxSizeInMB = 10;

		try {
			liferayLogMaxSizeInMB = Integer.parseInt(
				JenkinsResultsParserUtil.getBuildProperty(
					"testray.liferay.log.max.size.in.mb"));
		}
		catch (IOException ioException) {
		}

		return liferayLogMaxSizeInMB * 1024 * 1024;
	}

	private List<String> _getPortalLogWarnings() {
		List<String> portalLogWarnings = new ArrayList<>();

		if (_build == null) {
			return portalLogWarnings;
		}

		TestClassResult testClassResult = _build.getTestClassResult(
			"com.liferay.portal.log.assertor.PortalLogAssertorTest");

		if (testClassResult == null) {
			return portalLogWarnings;
		}

		for (TestResult testResult : testClassResult.getTestResults()) {
			String errorDetails = testResult.getErrorDetails();

			if (JenkinsResultsParserUtil.isNullOrEmpty(errorDetails)) {
				continue;
			}

			portalLogWarnings.add(errorDetails);
		}

		return portalLogWarnings;
	}

	private PortalWorkspaceGitRepository _getPortalWorkspaceGitRepository() {
		if (_portalWorkspaceGitRepository != null) {
			return _portalWorkspaceGitRepository;
		}

		String portalUpstreamBranchName = _startProperties.getProperty(
			"PORTAL_UPSTREAM_BRANCH_NAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalUpstreamBranchName)) {
			return null;
		}

		try {
			String portalDirPath = JenkinsResultsParserUtil.getBuildProperty(
				JenkinsResultsParserUtil.combine(
					"portal.dir[", portalUpstreamBranchName, "]"));

			File portalDir = new File(portalDirPath);

			if (!portalDir.exists()) {
				return null;
			}

			_portalWorkspaceGitRepository =
				(PortalWorkspaceGitRepository)
					GitRepositoryFactory.getWorkspaceGitRepository(
						portalDir.getName());

			return _portalWorkspaceGitRepository;
		}
		catch (IOException ioException) {
			return null;
		}
	}

	private List<String> _getPoshiWarnings() {
		List<String> poshiWarnings = new ArrayList<>();

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository == null) {
			return poshiWarnings;
		}

		File poshiWarningsFile = new File(
			portalWorkspaceGitRepository.getDirectory(), "poshi-warnings.xml");

		if (!poshiWarningsFile.exists()) {
			return poshiWarnings;
		}

		String content = null;

		try {
			content = JenkinsResultsParserUtil.read(poshiWarningsFile);

			content = content.trim();
		}
		catch (IOException ioException) {
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(content)) {
			return poshiWarnings;
		}

		try {
			Document document = Dom4JUtil.parse("<html>" + content + "</html>");

			Element rootElement = document.getRootElement();

			for (Element valueElement : rootElement.elements()) {
				String valueElementText = valueElement.getText();

				valueElementText = valueElementText.trim();

				if (JenkinsResultsParserUtil.isNullOrEmpty(valueElementText)) {
					continue;
				}

				poshiWarnings.add(valueElementText);
			}
		}
		catch (DocumentException documentException) {
		}

		return poshiWarnings;
	}

	private QAWebsitesWorkspaceGitRepository
		_getQAWebsitesWorkspaceGitRepository() {

		if (_qaWebsitesWorkspaceGitRepository != null) {
			return _qaWebsitesWorkspaceGitRepository;
		}

		Properties buildProperties;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		String qaWebsitesDirectoryPath = JenkinsResultsParserUtil.getProperty(
			buildProperties, "qa.websites.dir[master]");

		File qaWebsitesDir = new File(qaWebsitesDirectoryPath);

		if (!qaWebsitesDir.exists()) {
			return null;
		}

		_qaWebsitesWorkspaceGitRepository =
			(QAWebsitesWorkspaceGitRepository)
				GitRepositoryFactory.getWorkspaceGitRepository(
					qaWebsitesDir.getName());

		return _qaWebsitesWorkspaceGitRepository;
	}

	private File _getRecordedFilesBuildDir() {
		return new File(getRecordedFilesBaseDir(), getRelativeBuildDirPath());
	}

	private void _recordBuildDatabase() {
		if (!(_build instanceof TopLevelBuild)) {
			return;
		}

		TopLevelBuild topLevelBuild = (TopLevelBuild)_build;

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase(
			topLevelBuild);

		File buildDatabaseFile = buildDatabase.getBuildDatabaseFile();

		if (!buildDatabaseFile.exists()) {
			return;
		}

		try {
			JenkinsResultsParserUtil.copy(
				buildDatabaseFile,
				new File(_getRecordedFilesBuildDir(), "build-database.json"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _recordDockerLogs() {
		File sourceDockerLogsDir = new File(
			System.getenv("BUILD_DIR"), "docker-logs");

		if (!sourceDockerLogsDir.exists()) {
			return;
		}

		File destinationDockerLogsDir = new File(
			_getRecordedFilesBuildDir(), "docker-logs");

		for (File sourceDockerLogFile : sourceDockerLogsDir.listFiles()) {
			try {
				JenkinsResultsParserUtil.copy(
					sourceDockerLogFile,
					new File(
						destinationDockerLogsDir,
						sourceDockerLogFile.getName() + ".txt"));
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
	}

	private void _recordFailureMessages() {
		String batchName = null;

		if (_build instanceof AxisBuild) {
			AxisBuild axisBuild = (AxisBuild)_build;

			batchName = axisBuild.getBatchName();
		}
		else if (_build instanceof DownstreamBuild) {
			DownstreamBuild downstreamBuild = (DownstreamBuild)_build;

			batchName = downstreamBuild.getBatchName();
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(batchName)) {
			return;
		}

		if (!batchName.startsWith("integration-") &&
			!batchName.startsWith("modules-integration-") &&
			!batchName.startsWith("modules-unit-") &&
			!batchName.startsWith("unit-")) {

			return;
		}

		File testResultsFile = new File(
			System.getenv("WORKSPACE"), "test-results/TESTS-TestSuites.xml");

		if (!testResultsFile.exists()) {
			return;
		}

		try {
			Document document = Dom4JUtil.parse(
				JenkinsResultsParserUtil.read(testResultsFile));

			Element rootElement = document.getRootElement();

			Map<String, List<Element>> testcaseElementsMap = new TreeMap<>();

			for (Element testsuiteElement : rootElement.elements("testsuite")) {
				for (Element testcaseElement :
						testsuiteElement.elements("testcase")) {

					if (testcaseElement.element("failure") == null) {
						continue;
					}

					String testClassName = testcaseElement.attributeValue(
						"classname");

					if (testClassName.contains("$")) {
						testClassName = testClassName.substring(
							0, testClassName.indexOf("$"));
					}

					List<Element> testcaseElements = testcaseElementsMap.get(
						testClassName);

					if (testcaseElements == null) {
						testcaseElements = new ArrayList<>();

						testcaseElementsMap.put(
							testClassName, testcaseElements);
					}

					testcaseElements.add(testcaseElement);
				}
			}

			for (Map.Entry<String, List<Element>> testcaseElementsEntry :
					testcaseElementsMap.entrySet()) {

				StringBuilder sb = new StringBuilder();

				List<Element> testcaseElements =
					testcaseElementsEntry.getValue();

				for (Element testcaseElement : testcaseElements) {
					sb.append("##\n## ");
					sb.append(testcaseElement.attributeValue("classname"));
					sb.append(" > ");
					sb.append(testcaseElement.attributeValue("name"));
					sb.append("\n##\n\n");

					Element failureElement = testcaseElement.element("failure");

					if (failureElement == null) {
						sb.append("\tFailed for unknown reason\n\n");

						continue;
					}

					String failedElementText = failureElement.getText();

					for (String line : failedElementText.split("\n")) {
						sb.append("\t");
						sb.append(line);
						sb.append("\n");
					}

					sb.append("\n\n");
				}

				if (sb.length() <= 0) {
					continue;
				}

				JenkinsResultsParserUtil.write(
					new File(
						_getRecordedFilesBuildDir(),
						testcaseElementsEntry.getKey() + ".txt"),
					sb.toString());
			}
		}
		catch (DocumentException | IOException exception) {
		}
	}

	private void _recordGCLogs() {
		File sourceGCLogsDir = new File(System.getenv("BUILD_DIR"), "gc");

		if (!sourceGCLogsDir.exists()) {
			return;
		}

		File destinationGCLogsDir = new File(_getRecordedFilesBuildDir(), "gc");

		for (File sourceGCLogFile : sourceGCLogsDir.listFiles()) {
			try {
				JenkinsResultsParserUtil.copy(
					sourceGCLogFile,
					new File(
						destinationGCLogsDir,
						sourceGCLogFile.getName() + ".txt"));
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
	}

	private void _recordGradlePluginsFiles() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		File gradlePluginsFile = new File(
			portalWorkspaceGitRepository.getDirectory(),
			"tmp/gradle_plugins.tar");

		if (!gradlePluginsFile.exists()) {
			return;
		}

		try {
			JenkinsResultsParserUtil.copy(
				gradlePluginsFile,
				new File(_getRecordedFilesBuildDir(), "gradle_plugins.tar"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _recordJenkinsConsole() {
		File jenkinsConsoleFile = new File(
			_getRecordedFilesBuildDir(), "jenkins-console.txt");

		try {
			JenkinsResultsParserUtil.write(
				jenkinsConsoleFile, _build.getConsoleText());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _recordJenkinsReport() {
		if (!(_build instanceof TopLevelBuild)) {
			return;
		}

		TopLevelBuild topLevelBuild = (TopLevelBuild)_build;

		Element jenkinsReportElement = topLevelBuild.getJenkinsReportElement();

		File jenkinsReportFile = new File(
			_getRecordedFilesBuildDir(), "jenkins-report.html");

		try {
			JenkinsResultsParserUtil.write(
				jenkinsReportFile,
				StringEscapeUtils.unescapeXml(
					Dom4JUtil.format(jenkinsReportElement, true)));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _recordJobSummary() {
		if (!(_build instanceof TopLevelBuild)) {
			return;
		}

		TopLevelBuild topLevelBuild = (TopLevelBuild)_build;

		File jobSummaryFile = new File(
			topLevelBuild.getJobSummaryDir(), "index.html");

		if (!jobSummaryFile.exists()) {
			return;
		}

		try {
			JenkinsResultsParserUtil.copy(
				jobSummaryFile,
				new File(
					_getRecordedFilesBuildDir(), "job-summary/index.html"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _recordJStacks() {
		File sourceJStacksDir = new File(System.getenv("BUILD_DIR"), "jstacks");

		if (!sourceJStacksDir.exists()) {
			return;
		}

		File destinationJStacksDir = new File(
			_getRecordedFilesBuildDir(), "jstacks");

		for (File sourceJStackFile : sourceJStacksDir.listFiles()) {
			try {
				JenkinsResultsParserUtil.copy(
					sourceJStackFile,
					new File(
						destinationJStacksDir,
						sourceJStackFile.getName() + ".txt"));
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
	}

	private void _recordLiferayLogs() {
		for (File liferayBundlesDir : _getLiferayBundlesDirs()) {
			File liferayLogsDir = new File(liferayBundlesDir, "logs");

			if (!liferayLogsDir.exists()) {
				continue;
			}

			StringBuilder sb = new StringBuilder();

			for (File liferayLogFile :
					JenkinsResultsParserUtil.findFiles(
						liferayLogsDir, "liferay\\..*\\.log")) {

				try {
					sb.append(JenkinsResultsParserUtil.read(liferayLogFile));

					sb.append("\n");
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}

			int liferayLogMaxSizeInMB = _getLiferayLogMaxSize();

			if (sb.length() > liferayLogMaxSizeInMB) {
				sb.setLength(liferayLogMaxSizeInMB);
			}

			Matcher matcher = _bundlesDirNamePattern.matcher(
				liferayBundlesDir.getName());

			if (!matcher.find()) {
				continue;
			}

			File liferayLogFile = new File(
				_getRecordedFilesBuildDir(),
				JenkinsResultsParserUtil.combine(
					"liferay-log", matcher.group("bundlesSuffix"), ".txt"));

			try {
				JenkinsResultsParserUtil.write(liferayLogFile, sb.toString());
			}
			catch (IOException ioException) {
			}
		}
	}

	private void _recordLiferayOSGiLogs() {
		for (File liferayBundlesDir : _getLiferayBundlesDirs()) {
			File liferayOSGiStateDir = new File(
				liferayBundlesDir, "osgi/state");

			if (!liferayOSGiStateDir.exists()) {
				continue;
			}

			StringBuilder sb = new StringBuilder();

			for (File liferayOSGiLogFile :
					JenkinsResultsParserUtil.findFiles(
						liferayOSGiStateDir.getParentFile(), ".*\\.log")) {

				try {
					sb.append(
						JenkinsResultsParserUtil.read(liferayOSGiLogFile));

					sb.append("\n");
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}

			int liferayLogMaxSize = _getLiferayLogMaxSize();

			if (sb.length() > liferayLogMaxSize) {
				sb.setLength(liferayLogMaxSize);
			}

			String liferayOSGiLogFileContent = sb.toString();

			liferayOSGiLogFileContent = liferayOSGiLogFileContent.trim();

			if (liferayOSGiLogFileContent.length() == 0) {
				continue;
			}

			Matcher matcher = _bundlesDirNamePattern.matcher(
				liferayBundlesDir.getName());

			if (!matcher.find()) {
				continue;
			}

			File liferayOSGiLogFile = new File(
				_getRecordedFilesBuildDir(),
				JenkinsResultsParserUtil.combine(
					"liferay-osgi-log", matcher.group("bundlesSuffix"),
					".txt"));

			try {
				JenkinsResultsParserUtil.write(
					liferayOSGiLogFile, liferayOSGiLogFileContent);
			}
			catch (IOException ioException) {
			}
		}
	}

	private void _recordPlaywrightReportFile() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository != null) {
			File playwrightReportFile = new File(
				portalWorkspaceGitRepository.getDirectory(),
				"modules/test/playwright/playwright-report/index.html");

			if (playwrightReportFile.exists()) {
				_copyToRecordedFilesBuildDir(
					playwrightReportFile.getParentFile());

				File playwrightTestResultsDir = new File(
					portalWorkspaceGitRepository.getDirectory(),
					"modules/test/playwright/test-results");

				if (playwrightTestResultsDir.exists()) {
					_copyToRecordedFilesBuildDir(playwrightTestResultsDir);
				}

				return;
			}
		}

		QAWebsitesWorkspaceGitRepository qaWebsitesWorkspaceGitRepository =
			_getQAWebsitesWorkspaceGitRepository();

		if (qaWebsitesWorkspaceGitRepository != null) {
			File playwrightReportFile = new File(
				qaWebsitesWorkspaceGitRepository.getDirectory(),
				"playwright/playwright-report/index.html");

			if (playwrightReportFile.exists()) {
				_copyToRecordedFilesBuildDir(
					playwrightReportFile.getParentFile());
			}
		}
	}

	private void _recordPoshiReportFiles() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		List<File> testResultsDirs = new ArrayList<>();

		if (portalWorkspaceGitRepository != null) {
			File testResultsDir = new File(
				portalWorkspaceGitRepository.getDirectory(),
				"portal-web/test-results");

			if (testResultsDir.exists()) {
				testResultsDirs.add(testResultsDir);
			}

			File workspaceTestResultsDir = new File(
				portalWorkspaceGitRepository.getDirectory(),
				JenkinsResultsParserUtil.combine(
					"workspaces/", System.getenv("TEST_WORKSPACE_NAME"),
					"/poshi/test-results"));

			if (workspaceTestResultsDir.exists()) {
				testResultsDirs.add(workspaceTestResultsDir);
			}
		}

		if (testResultsDirs.isEmpty()) {
			QAWebsitesWorkspaceGitRepository qaWebsitesWorkspaceGitRepository =
				_getQAWebsitesWorkspaceGitRepository();

			if (qaWebsitesWorkspaceGitRepository != null) {
				testResultsDirs.addAll(
					JenkinsResultsParserUtil.findDirs(
						qaWebsitesWorkspaceGitRepository.getDirectory(),
						"test-results"));
			}
		}

		if (testResultsDirs.isEmpty()) {
			return;
		}

		for (File testResultsDir : testResultsDirs) {
			List<File> poshiReportIndexFiles =
				JenkinsResultsParserUtil.findFiles(
					testResultsDir, "index.html");

			for (File poshiReportIndexFile : poshiReportIndexFiles) {
				File sourcePoshiReportDir =
					poshiReportIndexFile.getParentFile();

				File poshiReportDir = new File(
					_getRecordedFilesBuildDir(),
					sourcePoshiReportDir.getName());

				try {
					JenkinsResultsParserUtil.copy(
						sourcePoshiReportDir, poshiReportDir);
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}

				File indexFile = new File(poshiReportDir, "index.html");

				if (!indexFile.exists()) {
					continue;
				}

				try {
					String content = JenkinsResultsParserUtil.read(indexFile);

					for (File poshiReportJPGFile :
							JenkinsResultsParserUtil.findFiles(
								poshiReportDir, ".*\\.jpg")) {

						String poshiReportJPGFileName =
							poshiReportJPGFile.getName();

						if (content.contains("/" + poshiReportJPGFileName)) {
							continue;
						}

						System.out.println(
							"Removing unreferenced file " + poshiReportJPGFile);

						JenkinsResultsParserUtil.delete(poshiReportJPGFile);
					}
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
		}
	}

	private void _recordWarnings() {
		List<String> warnings = new ArrayList<>();

		warnings.addAll(_getPortalLogWarnings());
		warnings.addAll(_getPoshiWarnings());

		if (warnings.isEmpty()) {
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("<html>\n");

		for (String warning : warnings) {
			sb.append("<pre>");
			sb.append(StringEscapeUtils.escapeHtml(warning));
			sb.append("</pre>\n");
		}

		sb.append("</html>");

		try {
			JenkinsResultsParserUtil.write(
				new File(_getRecordedFilesBuildDir(), "warnings.html"),
				sb.toString());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static final Pattern _bundlesDirNamePattern = Pattern.compile(
		"bundles(?<bundlesSuffix>.*)");

	private final Build _build;
	private PortalWorkspaceGitRepository _portalWorkspaceGitRepository;
	private QAWebsitesWorkspaceGitRepository _qaWebsitesWorkspaceGitRepository;
	private boolean _recorded;
	private final Properties _startProperties;

}