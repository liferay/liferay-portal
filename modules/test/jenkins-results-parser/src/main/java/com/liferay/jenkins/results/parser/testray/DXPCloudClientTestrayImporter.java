/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * @author Michael Hashimoto
 */
public class DXPCloudClientTestrayImporter {

	public static void main(String[] args) throws Exception {
		_initEnvironmentVariables();

		Element rootElement = Dom4JUtil.getNewElement("testsuite");

		rootElement.add(_getTestSuiteEnvironmentsElement());
		rootElement.add(_getTestSuitePropertiesElement());

		for (Element testCaseResultElement : _getTestCaseResultElements()) {
			Element testCaseElement = rootElement.addElement("testcase");

			Matcher matcher = _pattern.matcher(
				testCaseResultElement.attributeValue("name"));

			if (matcher.find()) {
				System.out.println("Importing " + matcher.group("testName"));
			}

			testCaseElement.add(
				_getTestCaseAttachmentsElement(testCaseResultElement));
			testCaseElement.add(
				_getTestCasePropertiesElement(testCaseResultElement));

			Element testCaseFailureElement = _getTestCaseFailureElement(
				testCaseResultElement);

			if (testCaseFailureElement != null) {
				testCaseElement.add(testCaseFailureElement);
			}
		}

		TestrayBuild testrayBuild = _getTestrayBuild();

		File testrayResultsDir = new File("testray-results");

		File resultsTarGzFile = new File(
			JenkinsResultsParserUtil.combine(
				String.valueOf(JenkinsResultsParserUtil.getCurrentTimeMillis()),
				"-", String.valueOf(testrayBuild.getID()), "-results.tar.gz"));

		try {
			JenkinsResultsParserUtil.delete(testrayResultsDir);

			testrayResultsDir.mkdirs();

			File resultsFile = new File(
				testrayResultsDir,
				JenkinsResultsParserUtil.combine(
					"TESTS-dxp-cloud-client-",
					String.valueOf(testrayBuild.getID()), ".xml"));

			JenkinsResultsParserUtil.write(
				resultsFile, Dom4JUtil.format(rootElement));

			JenkinsResultsParserUtil.tarGzip(
				testrayResultsDir, resultsTarGzFile);

			if (_testrayCloudBucket == null) {
				throw new RuntimeException(
					"ERROR: Testray 2 requires GCP to be configured");
			}

			_testrayCloudBucket.createTestrayCloudObject(
				"inbox/" + resultsTarGzFile.getName(), resultsTarGzFile);
		}
		catch (Exception exception) {
			throw new RuntimeException(exception);
		}
		finally {
			JenkinsResultsParserUtil.delete(testrayResultsDir);
			JenkinsResultsParserUtil.delete(resultsTarGzFile);
		}

		System.out.println("Imported results to " + testrayBuild.getURL());
	}

	private static void _fixImageURLs(File htmlFile) {
		try {
			String htmlFileContent = JenkinsResultsParserUtil.read(htmlFile);

			File parentFile = htmlFile.getParentFile();

			JenkinsResultsParserUtil.write(
				htmlFile,
				htmlFileContent.replaceAll(
					"(screenshots/(?:after|before|screenshot)\\d+)\\.jpg",
					JenkinsResultsParserUtil.combine(
						_testrayCloudBucket.getTestrayCloudBaseURL(), "/",
						_getRelativeURLPath(), "/", parentFile.getName(),
						"/$1.jpg.gz")));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static String _getEnvVarValue(String varName) {
		String varValue = System.getenv(varName);

		if (JenkinsResultsParserUtil.isNullOrEmpty(varValue)) {
			varValue = System.getProperty(varName);
		}

		return varValue;
	}

	private static Element _getPoshiLogAttachmentElement(
		Element testCaseResultElement) {

		if (_testrayCloudBucket == null) {
			return null;
		}

		File xmlFile = new File(
			_projectDir,
			"test-results/TEST-com.liferay.poshi.runner.PoshiRunner.xml");

		if (!xmlFile.exists()) {
			xmlFile = new File(
				_projectDir,
				"test-results" +
					"/TEST-com.liferay.poshi.runner.ParallelPoshiRunner.xml");
		}

		if (!xmlFile.exists()) {
			return null;
		}

		File testResultDir = new File(_projectDir, "test-results");

		Matcher matcher = _pattern.matcher(
			testCaseResultElement.attributeValue("name"));

		if (!matcher.find()) {
			return null;
		}

		String testName = matcher.group("testName");

		File testDir = new File(testResultDir, testName.replace("#", "_"));

		File poshiLogGzipFile = new File(testDir, "poshi-log.txt.gz");

		String key = JenkinsResultsParserUtil.combine(
			_getRelativeURLPath(), "/",
			JenkinsResultsParserUtil.getPathRelativeTo(
				poshiLogGzipFile, testResultDir));

		try {
			Document document = Dom4JUtil.parse(
				JenkinsResultsParserUtil.read(xmlFile));

			Element rootElement = document.getRootElement();

			Element targetTestcaseElement = null;

			for (Element testcaseElement : rootElement.elements("testcase")) {
				String testcaseName = testcaseElement.attributeValue("name");

				if (testcaseName.equals("test[" + testName + "]")) {
					targetTestcaseElement = testcaseElement;

					break;
				}
			}

			if (targetTestcaseElement == null) {
				return null;
			}

			StringBuilder sb = new StringBuilder();

			for (Element systemOutElement :
					targetTestcaseElement.elements("system-out")) {

				sb.append(systemOutElement.getText());
			}

			for (Element systemErrElement :
					targetTestcaseElement.elements("system-err")) {

				sb.append(systemErrElement.getText());
			}

			String poshiLogFileContent = sb.toString();

			poshiLogFileContent = poshiLogFileContent.trim();

			if (JenkinsResultsParserUtil.isNullOrEmpty(poshiLogFileContent)) {
				return null;
			}

			File poshiLogFile = new File(testDir, "poshi-log.txt");

			JenkinsResultsParserUtil.write(poshiLogFile, poshiLogFileContent);

			JenkinsResultsParserUtil.gzip(poshiLogFile, poshiLogGzipFile);

			_testrayCloudBucket.createTestrayCloudObject(key, poshiLogGzipFile);
		}
		catch (DocumentException | IOException exception) {
			exception.printStackTrace();

			return null;
		}

		Element attachmentElement = Dom4JUtil.getNewElement("file");

		attachmentElement.addAttribute("name", "Poshi Log");
		attachmentElement.addAttribute(
			"url",
			JenkinsResultsParserUtil.combine(
				_testrayCloudBucket.getTestrayCloudBaseURL(), "/", key,
				"?authuser=0"));
		attachmentElement.addAttribute("value", key + "?authuser=0");

		return attachmentElement;
	}

	private static Element _getPropertiesElement(Properties properties) {
		Element element = Dom4JUtil.getNewElement("properties");

		for (String propertyName : properties.stringPropertyNames()) {
			Element propertyElement = element.addElement("property");

			String propertyValue = JenkinsResultsParserUtil.getProperty(
				properties, propertyName);

			if (JenkinsResultsParserUtil.isNullOrEmpty(propertyName) ||
				JenkinsResultsParserUtil.isNullOrEmpty(propertyValue)) {

				continue;
			}

			propertyElement.addAttribute("name", propertyName);
			propertyElement.addAttribute("value", propertyValue);
		}

		return element;
	}

	private static String _getRelativeURLPath() {
		if (_relativeURLPath != null) {
			return _relativeURLPath;
		}

		TestrayBuild testrayBuild = _getTestrayBuild();

		_relativeURLPath = JenkinsResultsParserUtil.combine(
			"gcp/", _localDate.format(DateTimeFormatter.ofPattern("yyyy-MM")),
			"/dxp-cloud/", String.valueOf(testrayBuild.getID()));

		return _relativeURLPath;
	}

	private static Element _getTestCaseAttachmentsElement(
		Element testCaseResultElement) {

		Element attachmentsElement = Dom4JUtil.getNewElement("attachments");

		if (_testrayCloudBucket == null) {
			return attachmentsElement;
		}

		File testDir = new File(_projectDir, "playwright-report");

		if (_testType.equals("poshi")) {
			Matcher matcher = _pattern.matcher(
				testCaseResultElement.attributeValue("name"));

			if (!matcher.find()) {
				return attachmentsElement;
			}

			String testName = matcher.group("testName");

			testDir = new File(
				_projectDir, "test-results/" + testName.replace("#", "_"));
		}

		if (!testDir.exists()) {
			return attachmentsElement;
		}

		for (File htmlFile :
				JenkinsResultsParserUtil.findFiles(testDir, ".*\\.html")) {

			_fixImageURLs(htmlFile);
		}

		_removeUnreferencedImages(new File(testDir, "index.html"));

		for (File file : JenkinsResultsParserUtil.findFiles(testDir, ".*")) {
			String fileName = file.getName();

			String parentFile = file.getParent();

			if (parentFile.contains("trace")) {
				continue;
			}

			if (!fileName.endsWith(".gz") && !fileName.endsWith(".zip")) {
				File gzipFile = new File(parentFile, file.getName() + ".gz");

				JenkinsResultsParserUtil.gzip(file, gzipFile);

				JenkinsResultsParserUtil.delete(file);

				file = gzipFile;
			}

			fileName = file.getName();

			String key = JenkinsResultsParserUtil.combine(
				_getRelativeURLPath(), "/",
				JenkinsResultsParserUtil.getPathRelativeTo(
					file, testDir.getParentFile()));

			_testrayCloudBucket.createTestrayCloudObject(key, file);

			String attachmentName;

			if (_testType.equals("poshi")) {
				if (fileName.equals("console.txt.gz")) {
					attachmentName = "Poshi Console";
				}
				else if (fileName.equals("index.html.gz")) {
					attachmentName = "Poshi Report";
				}
				else if (fileName.equals("summary.html.gz")) {
					attachmentName = "Poshi Summary";
				}
				else {
					continue;
				}
			}
			else if (_testType.equals("playwright")) {
				if (fileName.equals("index.html.gz")) {
					attachmentName = "Playwright Report";
				}
				else if (fileName.endsWith(".zip")) {
					attachmentName = "Trace Zip";
				}
				else if (fileName.endsWith(".png.gz")) {
					attachmentName = "Failure Screenshot";
				}
				else {
					continue;
				}
			}
			else {
				continue;
			}

			Element attachmentElement = attachmentsElement.addElement("file");

			attachmentElement.addAttribute("name", attachmentName);

			attachmentElement.addAttribute(
				"url",
				JenkinsResultsParserUtil.combine(
					_testrayCloudBucket.getTestrayCloudBaseURL(), "/", key,
					"?authuser=0"));
			attachmentElement.addAttribute("value", key + "?authuser=0");
		}

		if (_testType.equals("poshi")) {
			Element poshiLogAttachmentElement = _getPoshiLogAttachmentElement(
				testCaseResultElement);

			if (poshiLogAttachmentElement != null) {
				attachmentsElement.add(poshiLogAttachmentElement);
			}
		}

		return attachmentsElement;
	}

	private static Element _getTestCaseFailureElement(
		Element testCaseResultElement) {

		Element failureElement = testCaseResultElement.element("failure");

		if (failureElement == null) {
			return null;
		}

		String failureMessage = failureElement.attributeValue("message");

		if (JenkinsResultsParserUtil.isNullOrEmpty(failureMessage)) {
			return null;
		}

		Element testCaseFailureElement = Dom4JUtil.getNewElement("failure");

		testCaseFailureElement.addAttribute("message", failureMessage);

		return testCaseFailureElement;
	}

	private static Element _getTestCasePropertiesElement(
		Element testCaseResultElement) {

		Properties properties = new Properties();

		String duration = testCaseResultElement.attributeValue("duration");

		if (duration == null) {
			duration = "0";
		}
		else {
			duration = duration.replaceAll("[\\.,]", "");
		}

		Matcher matcher = _pattern.matcher(
			testCaseResultElement.attributeValue("name"));

		String className = testCaseResultElement.attributeValue("classname");

		if (!className.endsWith("spec.ts")) {
			if (!matcher.find()) {
				return _getPropertiesElement(properties);
			}

			className = matcher.group("testName");
		}

		TestrayCaseResult.Status status = TestrayCaseResult.Status.PASSED;

		Element failureElement = testCaseResultElement.element("failure");

		if (failureElement != null) {
			status = TestrayCaseResult.Status.FAILED;
		}

		properties.setProperty(
			"testray.case.type.name", "Automated Functional Test");
		properties.setProperty(
			"testray.component.names", _testrayComponentName);
		properties.setProperty(
			"testray.main.component.name", _testrayComponentName);
		properties.setProperty("testray.team.name", _testrayTeamName);
		properties.setProperty("testray.testcase.duration", duration);
		properties.setProperty("testray.testcase.name", className);
		properties.setProperty(
			"testray.testcase.priority", String.valueOf(_testrayCasePriority));
		properties.setProperty("testray.testcase.status", status.getName());

		return _getPropertiesElement(properties);
	}

	private static List<Element> _getTestCaseResultElements() {
		File xmlFile = new File(
			_projectDir,
			"test-results/TEST-com.liferay.poshi.runner.PoshiRunner.xml");

		if (!xmlFile.exists()) {
			xmlFile = new File(
				_projectDir,
				"test-results" +
					"/TEST-com.liferay.poshi.runner.ParallelPoshiRunner.xml");
		}

		if (_testType.equals("playwright")) {
			xmlFile = new File(_projectDir, "test-results/TEST-playwright.xml");

			_splitTestSuitesJUnitReport(xmlFile.getPath());

			List<Element> testCaseElements = new ArrayList<>();

			File parentDir = new File(_projectDir, "test-results");

			for (File childFile : parentDir.listFiles()) {
				String childFileName = childFile.getName();

				if (childFileName.endsWith(".xml")) {
					try {
						Document document = Dom4JUtil.parse(
							JenkinsResultsParserUtil.read(childFile));

						Element rootElement = document.getRootElement();

						for (Element testCaseElement :
								rootElement.elements("testcase")) {

							testCaseElements.add(testCaseElement);
						}
					}
					catch (Exception exception) {
						if (childFile.exists()) {
							File xmlGzipFile = new File(
								childFile.getParentFile(),
								childFile.getName() + ".gz");

							JenkinsResultsParserUtil.gzip(
								childFile, xmlGzipFile);

							_testrayCloudBucket.createTestrayCloudObject(
								_getRelativeURLPath() + "/" +
									xmlGzipFile.getName(),
								xmlGzipFile);
						}

						throw new RuntimeException(exception);
					}
				}
			}

			return testCaseElements;
		}

		try {
			Document document = Dom4JUtil.parse(
				JenkinsResultsParserUtil.read(xmlFile));

			Element rootElement = document.getRootElement();

			return rootElement.elements("testcase");
		}
		catch (Exception exception) {
			if (xmlFile.exists()) {
				File xmlGzipFile = new File(
					xmlFile.getParentFile(), xmlFile.getName() + ".gz");

				JenkinsResultsParserUtil.gzip(xmlFile, xmlGzipFile);

				_testrayCloudBucket.createTestrayCloudObject(
					_getRelativeURLPath() + "/" + xmlGzipFile.getName(),
					xmlGzipFile);
			}

			throw new RuntimeException(exception);
		}
	}

	private static TestrayBuild _getTestrayBuild() {
		if (_testrayBuild != null) {
			return _testrayBuild;
		}

		TestrayServer testrayServer = TestrayFactory.newTestrayServer(
			_testrayServerURL);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_testrayOAuth2ClientId) &&
			!JenkinsResultsParserUtil.isNullOrEmpty(
				_testrayOAuth2ClientSecret)) {

			URL tokenURL;

			try {
				tokenURL = new URL(testrayServer.getURL() + "/o/oauth2/token");
			}
			catch (MalformedURLException malformedURLException) {
				throw new RuntimeException(malformedURLException);
			}

			testrayServer.setHTTPAuthorization(
				new JenkinsResultsParserUtil.ClientCredentialsHTTPAuthorization(
					_testrayOAuth2ClientId, _testrayOAuth2ClientSecret,
					tokenURL));
		}

		TestrayProject testrayProject = testrayServer.getTestrayProjectByName(
			_testrayProjectName);

		TestrayRoutine testrayRoutine = testrayProject.createTestrayRoutine(
			_testrayRoutineName);

		TestrayProductVersion testrayProductVersion =
			testrayProject.createTestrayProductVersion(_testrayProductVersion);

		_testrayBuild = testrayRoutine.createTestrayBuild(
			testrayProductVersion, _getTestrayBuildName(),
			new Date(_START_TIME), null, _testrayBuildSHA);

		return _testrayBuild;
	}

	private static String _getTestrayBuildName() {
		return _testrayBuildName.replace(
			"$(start.time)",
			JenkinsResultsParserUtil.toDateString(
				new Date(_START_TIME), "yyyy-MM-dd[HH:mm:ss]",
				"America/Los_Angeles"));
	}

	private static Element _getTestSuiteEnvironmentsElement() {
		Element environmentsElement = Dom4JUtil.getNewElement("environments");

		Element browserEnvironmentElement = environmentsElement.addElement(
			"environment");

		browserEnvironmentElement.addAttribute(
			"option", _environmentBrowserName);
		browserEnvironmentElement.addAttribute("type", "Browser");

		Element operatingSystemEnvironmentElement =
			environmentsElement.addElement("environment");

		operatingSystemEnvironmentElement.addAttribute(
			"option", _environmentOperatingSystemName);
		operatingSystemEnvironmentElement.addAttribute(
			"type", "Operating System");

		return environmentsElement;
	}

	private static Element _getTestSuitePropertiesElement() {
		TestrayBuild testrayBuild = _getTestrayBuild();

		TestrayProductVersion testrayProductVersion =
			testrayBuild.getTestrayProductVersion();
		TestrayProject testrayProject = testrayBuild.getTestrayProject();
		TestrayRoutine testrayRoutine = testrayBuild.getTestrayRoutine();

		Properties properties = new Properties();

		properties.setProperty(
			"testray.build.date",
			JenkinsResultsParserUtil.toDateString(
				new Date(), "yyy-MM-dd HH:mm:ss", "America/Los_Angeles"));
		properties.setProperty("testray.build.name", testrayBuild.getName());
		properties.setProperty("testray.build.type", testrayRoutine.getName());
		properties.setProperty(
			"testray.product.version", testrayProductVersion.getName());
		properties.setProperty(
			"testray.project.name", testrayProject.getName());
		properties.setProperty(
			"testray.run.id",
			JenkinsResultsParserUtil.join(
				"|", _environmentBrowserName, _environmentOperatingSystemName));

		return _getPropertiesElement(properties);
	}

	private static void _initEnvironmentVariables() {
		String projectDirPath = _getEnvVarValue("projectDir");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(projectDirPath)) {
			_projectDir = new File(projectDirPath);
		}

		if (!_projectDir.exists()) {
			throw new RuntimeException(
				"Could not find '" + projectDirPath + "'");
		}

		String environmentBrowserName = _getEnvVarValue(
			"environmentBrowserName");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(environmentBrowserName)) {
			_environmentBrowserName = environmentBrowserName;
		}

		String environmentOperatingSystemName = _getEnvVarValue(
			"environmentOperatingSystemName");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(
				environmentOperatingSystemName)) {

			_environmentOperatingSystemName = environmentOperatingSystemName;
		}

		String testType = _getEnvVarValue("testType");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testType)) {
			_testType = testType;
		}
		else {
			_setTestType();
		}

		String testrayBuildName = _getEnvVarValue("testrayBuildName");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayBuildName)) {
			_testrayBuildName = testrayBuildName;
		}

		String testrayBuildSHA = _getEnvVarValue("testrayBuildSHA");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayBuildSHA)) {
			_testrayBuildSHA = testrayBuildSHA;
		}

		String testrayCasePriority = _getEnvVarValue("testrayCasePriority");

		if ((testrayCasePriority != null) &&
			testrayCasePriority.matches("\\d+")) {

			_testrayCasePriority = Integer.valueOf(testrayCasePriority);
		}

		String testrayComponentName = _getEnvVarValue("testrayComponentName");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayComponentName)) {
			_testrayComponentName = testrayComponentName;
		}

		String testrayOAuth2ClientId = _getEnvVarValue("testrayOAuth2ClientId");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayOAuth2ClientId)) {
			_testrayOAuth2ClientId = testrayOAuth2ClientId;
		}

		String testrayOAuth2ClientSecret = _getEnvVarValue(
			"testrayOAuth2ClientSecret");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(
				testrayOAuth2ClientSecret)) {

			_testrayOAuth2ClientSecret = testrayOAuth2ClientSecret;
		}

		String testrayProductVersion = _getEnvVarValue("testrayProductVersion");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayProductVersion)) {
			_testrayProductVersion = testrayProductVersion;
		}

		String testrayProjectName = _getEnvVarValue("testrayProjectName");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayProjectName)) {
			_testrayProjectName = testrayProjectName;
		}

		String testrayRoutineName = _getEnvVarValue("testrayRoutineName");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayRoutineName)) {
			_testrayRoutineName = testrayRoutineName;
		}

		String testrayCloudBucketName = _getEnvVarValue(
			"testrayCloudBucketName");

		if (JenkinsResultsParserUtil.isNullOrEmpty(testrayCloudBucketName)) {
			testrayCloudBucketName = _getEnvVarValue("testrayS3BucketName");
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(testrayCloudBucketName)) {
			testrayCloudBucketName = TestrayCloudBucket.DEFAULT_BUCKET_NAME;
		}

		if (TestrayCloudBucket.hasGoogleApplicationCredentials(
				testrayCloudBucketName)) {

			_testrayCloudBucket = TestrayCloudBucket.getInstance(
				testrayCloudBucketName);
		}

		String testrayServerURL = _getEnvVarValue("testrayServerURL");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayServerURL)) {
			_testrayServerURL = testrayServerURL;
		}

		String testrayTeamName = _getEnvVarValue("testrayTeamName");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayTeamName)) {
			_testrayTeamName = testrayTeamName;
		}
	}

	private static void _removeUnreferencedImages(File htmlFile) {
		if (!htmlFile.exists()) {
			return;
		}

		try {
			String htmlFileContent = JenkinsResultsParserUtil.read(htmlFile);

			List<File> jpgFiles = JenkinsResultsParserUtil.findFiles(
				htmlFile.getParentFile(), ".*\\.jpg");

			for (File jpgFile : jpgFiles) {
				String jpgFileName = jpgFile.getName();

				if (htmlFileContent.contains("/" + jpgFileName)) {
					continue;
				}

				System.out.println("Removing unreferenced file " + jpgFile);

				JenkinsResultsParserUtil.delete(jpgFile);
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static void _setTestType() {
		File xmlFile = new File(
			_projectDir,
			"test-results/TEST-com.liferay.poshi.runner.PoshiRunner.xml");

		_testType = "poshi";

		if (!xmlFile.exists()) {
			xmlFile = new File(
				_projectDir,
				"test-results" +
					"/TEST-com.liferay.poshi.runner.ParallelPoshiRunner.xml");
		}

		if (!xmlFile.exists()) {
			_testType = "playwright";
		}
	}

	private static void _splitTestSuitesJUnitReport(String filePath) {
		File testSuitesReportFile = new File(filePath);

		if (!testSuitesReportFile.exists()) {
			return;
		}

		try {
			String content = JenkinsResultsParserUtil.read(
				testSuitesReportFile);

			Document document = Dom4JUtil.parse(content);

			List<Node> nodes = Dom4JUtil.getNodesByXPath(
				document, "//testsuite");

			if ((nodes != null) && !nodes.isEmpty()) {
				int i = 1;

				for (Node node : nodes) {
					if (!(node instanceof Element)) {
						continue;
					}

					Element element = (Element)node;

					File partitionedTestResultsFile = new File(
						filePath.replace(".xml", i + ".xml"));

					JenkinsResultsParserUtil.write(
						partitionedTestResultsFile, Dom4JUtil.format(element));

					i++;
				}
			}

			testSuitesReportFile.delete();
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private static final long _START_TIME = System.currentTimeMillis();

	private static String _environmentBrowserName = "Google Chrome 86";
	private static String _environmentOperatingSystemName = "CentOS 7";
	private static final LocalDate _localDate = LocalDate.now();
	private static final Pattern _pattern = Pattern.compile(
		"test\\[(?<testName>[^\\]]{1,150})[^\\]]*\\]");
	private static File _projectDir = new File(".");
	private static String _relativeURLPath;
	private static TestrayBuild _testrayBuild;
	private static String _testrayBuildName =
		"DXP Cloud Client Build - $(start.time)";
	private static String _testrayBuildSHA;
	private static Integer _testrayCasePriority = 1;
	private static TestrayCloudBucket _testrayCloudBucket;
	private static String _testrayComponentName = "DXP Cloud Client Component";
	private static String _testrayOAuth2ClientId;
	private static String _testrayOAuth2ClientSecret;
	private static String _testrayProductVersion = "1.x";
	private static String _testrayProjectName = "DXP Cloud Client";
	private static String _testrayRoutineName = "DXP Cloud Client Routine";
	private static String _testrayServerURL = "https://testray.liferay.com";
	private static String _testrayTeamName = "DXP Cloud Client Team";
	private static String _testType;

}