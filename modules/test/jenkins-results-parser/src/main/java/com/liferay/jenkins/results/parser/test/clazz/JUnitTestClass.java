/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz;

import com.liferay.jenkins.results.parser.DownstreamBuildReport;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.TestClassReport;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JUnitBatchTestClassGroup;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JUnitTestClass extends BaseTestClass {

	public DownstreamBuildReport getCachedDownstreamBuildReport() {
		if (isBuildCachingEnabled() && !_cachedTestReportSearched) {
			getCachedTestClassReports();
		}

		return _cachedDownstreamBuildReport;
	}

	public List<TestClassReport> getCachedTestClassReports() {
		if (!isBuildCachingEnabled() || _cachedTestReportSearched) {
			return _cachedTestClassReports;
		}

		BatchTestClassGroup batchTestClassGroup = getBatchTestClassGroup();

		String testClassName = getTestClassName();

		List<TestClassReport> cachedTestClassReports = new ArrayList<>();

		TestClassReport testClassReport =
			batchTestClassGroup.getCachedTestClassReport(testClassName);

		if (testClassReport != null) {
			cachedTestClassReports.add(
				batchTestClassGroup.getCachedTestClassReport(testClassName));
		}

		cachedTestClassReports.addAll(
			batchTestClassGroup.getCachedTestClassReportByPrefix(
				testClassName + "$"));

		if ((cachedTestClassReports != null) &&
			!cachedTestClassReports.isEmpty()) {

			_cachedTestClassReports = cachedTestClassReports;

			for (TestClassReport cachedTestClassReport :
					cachedTestClassReports) {

				_cachedDownstreamBuildReport =
					cachedTestClassReport.getDownstreamBuildReport();

				break;
			}

			_cachedTestReportSearched = true;
		}

		return _cachedTestClassReports;
	}

	@Override
	public JSONObject getJSONObject() {
		JSONObject jsonObject = super.getJSONObject();

		if ((_testPropertiesFile != null) && _testPropertiesFile.exists()) {
			jsonObject.put("test_properties_file", _testPropertiesFile);
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(
				_testrayMainComponentName)) {

			jsonObject.put(
				"testray_main_component_name", _testrayMainComponentName);
		}

		return jsonObject;
	}

	@Override
	public long getSharedWeight() {
		return getAverageTestTaskDuration();
	}

	@Override
	public String getSharedWeightName() {
		return getTestTaskName();
	}

	public List<String> getTestClassMethodNames() {
		return _testClassMethodNames;
	}

	@Override
	public String getTestClassName() {
		return JenkinsResultsParserUtil.combine(
			_getPackageName(), ".", _getClassName());
	}

	public String getTestrayMainComponentName() {
		return _testrayMainComponentName;
	}

	@Override
	public boolean isIgnored() {
		return _classIgnored;
	}

	protected JUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile) {

		super(batchTestClassGroup, testClassFile);

		File modulesBaseDir = _getPortalModulesBaseDir();

		if ((modulesBaseDir != null) && modulesBaseDir.exists()) {
			_modulesBaseDir = modulesBaseDir;
		}
		else {
			_modulesBaseDir = new File(".");
		}

		File testPropertiesBaseDir = getTestPropertiesBaseDir(
			getTestClassFile());

		if ((testPropertiesBaseDir != null) && testPropertiesBaseDir.exists()) {
			_testPropertiesFile = new File(
				testPropertiesBaseDir, "test.properties");

			String testrayMainComponentName =
				JenkinsResultsParserUtil.getProperty(
					JenkinsResultsParserUtil.getProperties(_testPropertiesFile),
					"testray.main.component.name");

			if ((testrayMainComponentName == null) &&
				_modulesBaseDir.exists()) {

				testrayMainComponentName = JenkinsResultsParserUtil.getProperty(
					JenkinsResultsParserUtil.getProperties(
						_getParentTestPropertiesFile(testPropertiesBaseDir)),
					"testray.main.component.name");
			}

			_testrayMainComponentName = testrayMainComponentName;
		}
		else {
			_testPropertiesFile = null;
			_testrayMainComponentName = null;
		}

		String testClassFileName = testClassFile.getName();

		if (!testClassFileName.endsWith(".java")) {
			return;
		}

		try {
			_initTestClassMethods(
				JenkinsResultsParserUtil.read(getTestClassFile()));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected JUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, File testClassFile,
		List<String> testClassMethodNames) {

		super(batchTestClassGroup, testClassFile);

		File modulesBaseDir = _getPortalModulesBaseDir();

		if ((modulesBaseDir != null) && modulesBaseDir.exists()) {
			_modulesBaseDir = modulesBaseDir;
		}
		else {
			_modulesBaseDir = new File(".");
		}

		File testPropertiesBaseDir = getTestPropertiesBaseDir(
			getTestClassFile());

		if ((testPropertiesBaseDir != null) && testPropertiesBaseDir.exists()) {
			_testPropertiesFile = new File(
				testPropertiesBaseDir, "test.properties");

			String testrayMainComponentName =
				JenkinsResultsParserUtil.getProperty(
					JenkinsResultsParserUtil.getProperties(_testPropertiesFile),
					"testray.main.component.name");

			if ((testrayMainComponentName == null) &&
				_modulesBaseDir.exists()) {

				testrayMainComponentName = JenkinsResultsParserUtil.getProperty(
					JenkinsResultsParserUtil.getProperties(
						_getParentTestPropertiesFile(testPropertiesBaseDir)),
					"testray.main.component.name");
			}

			_testrayMainComponentName = testrayMainComponentName;
		}
		else {
			_testPropertiesFile = null;
			_testrayMainComponentName = null;
		}

		String testClassFileName = testClassFile.getName();

		if (!testClassFileName.endsWith(".java")) {
			return;
		}

		boolean methodIgnored = false;

		for (String testClassMethodName : testClassMethodNames) {
			_testClassMethodNames.add(testClassMethodName);

			addTestClassMethod(methodIgnored, testClassMethodName);
		}
	}

	protected JUnitTestClass(
		BatchTestClassGroup batchTestClassGroup, JSONObject jsonObject) {

		super(batchTestClassGroup, jsonObject);

		_classIgnored = jsonObject.getBoolean("ignored");

		File modulesBaseDir = _getPortalModulesBaseDir();

		if ((modulesBaseDir != null) && modulesBaseDir.exists()) {
			_modulesBaseDir = modulesBaseDir;
		}
		else {
			_modulesBaseDir = null;
		}

		if (jsonObject.has("test_properties_file")) {
			_testPropertiesFile = new File(
				jsonObject.getString("test_properties_file"));
		}
		else {
			_testPropertiesFile = null;
		}

		File testPropertiesBaseDir = getTestPropertiesBaseDir(
			getTestClassFile());

		String testrayMainComponentName = jsonObject.optString(
			"testray_main_component_name");

		if ((testrayMainComponentName == null) && _modulesBaseDir.exists()) {
			testrayMainComponentName = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getProperties(
					_getParentTestPropertiesFile(testPropertiesBaseDir)),
				"testray.main.component.name");
		}

		_testrayMainComponentName = testrayMainComponentName;
	}

	@Override
	protected String getTestName() {
		return _getPackageName() + "." + _getClassName();
	}

	private String _getClassName() {
		File testClassFile = getTestClassFile();

		String testClassFileName = testClassFile.getName();

		return testClassFileName.substring(
			0, testClassFileName.lastIndexOf("."));
	}

	private String _getPackageName() {
		String testClassFilePath = JenkinsResultsParserUtil.getCanonicalPath(
			getTestClassFile());

		int x = testClassFilePath.indexOf("/com/");
		int y = testClassFilePath.lastIndexOf("/");

		testClassFilePath = testClassFilePath.substring(x + 1, y);

		return testClassFilePath.replaceAll("/", ".");
	}

	private String _getParentClassName(String fileContent) {
		Pattern classHeaderPattern = Pattern.compile(
			JenkinsResultsParserUtil.combine(
				"public\\s+(abstract\\s+)?(class|interface)\\s+",
				_getClassName(),
				"(\\<[^\\<]+\\>)?(?<classHeaderEntities>[^\\{]+)\\{"));

		Matcher classHeaderMatcher = classHeaderPattern.matcher(fileContent);

		if (!classHeaderMatcher.find()) {
			throw new RuntimeException(
				"No class header found in " + getTestClassFile());
		}

		String classHeaderEntities = classHeaderMatcher.group(
			"classHeaderEntities");

		Pattern parentClassPattern = Pattern.compile(
			JenkinsResultsParserUtil.combine(
				"extends\\s+(?<parentClassName>[^\\s\\<]+)"));

		Matcher parentClassMatcher = parentClassPattern.matcher(
			classHeaderEntities);

		if (parentClassMatcher.find()) {
			return parentClassMatcher.group("parentClassName");
		}

		return null;
	}

	private String _getParentFullClassName(String fileContent) {
		String parentClassName = _getParentClassName(fileContent);

		if (parentClassName == null) {
			return null;
		}

		if (parentClassName.contains(".") &&
			parentClassName.matches("[a-z].*")) {

			if (!parentClassName.startsWith("com.liferay")) {
				return null;
			}

			return parentClassName;
		}

		String parentPackageName = _getParentPackageName(
			fileContent, parentClassName);

		if (parentPackageName == null) {
			return null;
		}

		return parentPackageName + "." + parentClassName;
	}

	private String _getParentPackageName(
		String fileContent, String parentClassName) {

		Pattern parentImportClassPattern = Pattern.compile(
			JenkinsResultsParserUtil.combine(
				"import\\s+(?<parentPackageName>[^;]+)\\.", parentClassName,
				";"));

		Matcher parentImportClassMatcher = parentImportClassPattern.matcher(
			fileContent);

		if (parentImportClassMatcher.find()) {
			String parentPackageName = parentImportClassMatcher.group(
				"parentPackageName");

			if (!parentPackageName.startsWith("com.liferay")) {
				return null;
			}

			return parentPackageName;
		}

		return _getPackageName();
	}

	private File _getParentTestPropertiesFile(File currentDir) {
		if ((currentDir == null) ||
			(currentDir.compareTo(_modulesBaseDir) == 0)) {

			return null;
		}

		File parentDir = currentDir.getParentFile();

		File parentPropertiesFile = new File(parentDir, "test.properties");

		if (parentPropertiesFile.exists()) {
			return parentPropertiesFile;
		}

		return _getParentTestPropertiesFile(parentDir);
	}

	private File _getPortalModulesBaseDir() {
		PortalGitWorkingDirectory portalGitWorkingDirectory =
			getPortalGitWorkingDirectory();

		return new File(
			portalGitWorkingDirectory.getWorkingDirectory(), "modules");
	}

	private void _initTestClassMethods(String fileContent) {
		Matcher classHeaderMatcher = _classHeaderPattern.matcher(fileContent);

		_classIgnored = false;

		if (classHeaderMatcher.find()) {
			String annotations = classHeaderMatcher.group("annotations");

			if ((annotations != null) && annotations.contains("@Ignore")) {
				_classIgnored = true;
			}
		}

		Matcher methodHeaderMatcher = _methodHeaderPattern.matcher(fileContent);

		while (methodHeaderMatcher.find()) {
			String annotations = methodHeaderMatcher.group("annotations");

			boolean methodIgnored = false;

			if (_classIgnored || annotations.contains("@Ignore")) {
				methodIgnored = true;
			}

			if (annotations.contains("@Test")) {
				String methodName = methodHeaderMatcher.group("methodName");

				if (annotations.contains("@TestInfo")) {
					List<String> issuesList = new ArrayList<>();

					String testInfo = annotations.substring(
						annotations.indexOf("@TestInfo"));

					Matcher matcher = _issuesPattern.matcher(testInfo);

					while (matcher.find()) {
						issuesList.add(matcher.group());
					}

					addTestClassMethod(
						methodIgnored, methodName,
						String.join(", ", issuesList));
				}
				else {
					addTestClassMethod(methodIgnored, methodName);
				}
			}
		}

		String parentFullClassName = _getParentFullClassName(fileContent);

		if (parentFullClassName == null) {
			return;
		}

		JUnitBatchTestClassGroup jUnitBatchTestClassGroup =
			(JUnitBatchTestClassGroup)getBatchTestClassGroup();

		File parentJavaFile =
			jUnitBatchTestClassGroup.getJavaFileFromFullClassName(
				parentFullClassName);

		if (parentJavaFile == null) {
			System.out.println(
				"No matching files found for " + parentFullClassName);

			return;
		}

		TestClass parentTestClass = TestClassFactory.newTestClass(
			getBatchTestClassGroup(), parentJavaFile);

		if (parentTestClass == null) {
			return;
		}

		for (TestClassMethod testClassMethod :
				parentTestClass.getTestClassMethods()) {

			addTestClassMethod(_classIgnored, testClassMethod.getName());
		}
	}

	private static final Pattern _classHeaderPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"\\*/(?<annotations>[^/]*)public\\s+class\\s+",
			"(?<className>[^\\(\\s]+)"));
	private static final Pattern _issuesPattern = Pattern.compile(
		"[A-Z]+-\\d+");
	private static final Pattern _methodHeaderPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"\\t(?<annotations>(@[\\s\\S]+?))public\\s+void\\s+",
			"(?<methodName>[^\\(\\s]+)"));

	private DownstreamBuildReport _cachedDownstreamBuildReport;
	private List<TestClassReport> _cachedTestClassReports;
	private boolean _cachedTestReportSearched;
	private boolean _classIgnored;
	private final File _modulesBaseDir;
	private final List<String> _testClassMethodNames = new ArrayList<>();
	private final File _testPropertiesFile;
	private final String _testrayMainComponentName;

}