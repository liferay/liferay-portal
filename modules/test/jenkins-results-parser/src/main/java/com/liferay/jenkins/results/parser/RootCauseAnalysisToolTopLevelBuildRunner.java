/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.test.clazz.FunctionalTestClass;
import com.liferay.jenkins.results.parser.test.clazz.JUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.ModulesTestClass;
import com.liferay.jenkins.results.parser.test.clazz.PlaywrightJUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.BatchTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.TestClassGroupFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public class RootCauseAnalysisToolTopLevelBuildRunner
	extends PortalTopLevelBuildRunner<PortalTopLevelBuildData> {

	@Override
	public void tearDown() {
		cleanUpHostServices();

		tearDownWorkspace();
	}

	protected RootCauseAnalysisToolTopLevelBuildRunner(
		PortalTopLevelBuildData portalTopLevelBuildData) {

		super(portalTopLevelBuildData);
	}

	@Override
	protected Element getJenkinsReportElement() {
		PortalTopLevelBuildData portalTopLevelBuildData = getBuildData();
		Workspace workspace = getWorkspace();

		if (workspace == null) {
			return Dom4JUtil.getNewElement(
				"html", null,
				Dom4JUtil.getNewElement(
					"h1", null, "Report building in progress for ",
					Dom4JUtil.getNewAnchorElement(
						portalTopLevelBuildData.getBuildURL(),
						portalTopLevelBuildData.getBuildURL())));
		}

		RootCauseAnalysisToolBuild rootCauseAnalysisToolBuild =
			(RootCauseAnalysisToolBuild)getTopLevelBuild();

		List<PortalBuildData> downstreamPortalBuildDataList = new ArrayList<>();

		for (BuildData downstreamBuildData :
				portalTopLevelBuildData.getDownstreamBuildDataList()) {

			if (downstreamBuildData instanceof PortalBuildData) {
				downstreamPortalBuildDataList.add(
					(PortalBuildData)downstreamBuildData);
			}
		}

		rootCauseAnalysisToolBuild.setDownstreamPortalBuildDataList(
			downstreamPortalBuildDataList);

		rootCauseAnalysisToolBuild.setWorkspaceGitRepository(
			workspace.getPrimaryWorkspaceGitRepository());

		return super.getJenkinsReportElement();
	}

	@Override
	protected void prepareInvocationBuildDataList() {
		PortalTopLevelBuildData portalTopLevelBuildData = getBuildData();

		String downstreamJobName =
			portalTopLevelBuildData.getJobName() + "-batch";

		for (String portalBranchSHA : _getPortalBranchSHAs()) {
			int retestCount = _getRetestCount();

			for (int i = 0; i < retestCount; i++) {
				BatchBuildData batchBuildData =
					BuildDataFactory.newBatchBuildData(
						null, downstreamJobName, null);

				if (!(batchBuildData instanceof PortalBatchBuildData)) {
					throw new RuntimeException("Invalid build data");
				}

				PortalBatchBuildData portalBatchBuildData =
					(PortalBatchBuildData)batchBuildData;

				portalBatchBuildData.setBuildDescription(
					_getDownstreamBuildDescription(portalBranchSHA));

				portalBatchBuildData.setBatchName(_getBatchName());
				portalBatchBuildData.setPortalBranchSHA(portalBranchSHA);
				portalBatchBuildData.setTestList(_getTestList());

				addInvocationBuildData(portalBatchBuildData);
			}
		}
	}

	@Override
	protected void setUpWorkspace() {
		super.setUpWorkspace();

		Workspace workspace = getWorkspace();

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		GitWorkingDirectory gitWorkingDirectory =
			workspaceGitRepository.getGitWorkingDirectory();

		List<String> portalBranchSHAs = _getPortalBranchSHAs();

		for (String portalBranchSHA : portalBranchSHAs) {
			if (gitWorkingDirectory.localSHAExists(portalBranchSHA)) {
				continue;
			}

			String portalGitHubURL = _getPortalGitHubURL();

			failBuildRunner(
				JenkinsResultsParserUtil.combine(
					_NAME_BUILD_PARAMETER_PORTAL_BRANCH_SHAS,
					" has SHAs that are not be found within the latest ",
					String.valueOf(
						WorkspaceGitRepository.COMMITS_HISTORY_SIZE_MAX),
					" commits of <a href=\"", portalGitHubURL, "\">",
					portalGitHubURL, "</a>"));

			return;
		}

		List<String> portalCherryPickSHAs = _getPortalCherryPickSHAs();

		for (String portalCherryPickSHA : portalCherryPickSHAs) {
			if (gitWorkingDirectory.localSHAExists(portalCherryPickSHA)) {
				continue;
			}

			String portalGitHubURL = _getPortalGitHubURL();

			failBuildRunner(
				JenkinsResultsParserUtil.combine(
					_NAME_BUILD_PARAMETER_PORTAL_CHERRY_PICK_SHAS,
					" has SHAs that are not be found within the latest ",
					String.valueOf(
						WorkspaceGitRepository.COMMITS_HISTORY_SIZE_MAX),
					" commits of <a href=\"", portalGitHubURL, "\">",
					portalGitHubURL, "</a>"));

			return;
		}

		List<String> commitSHAs = new ArrayList<>();

		commitSHAs.addAll(portalBranchSHAs);
		commitSHAs.addAll(portalCherryPickSHAs);

		try {
			workspaceGitRepository.storeCommitHistory(commitSHAs);
		}
		catch (Exception exception) {
			failBuildRunner("Unable to store the commit history", exception);
		}
	}

	@Override
	protected void validateBuildParameters() {
		_validateBuildParameterJenkinsGitHubURL();
		_validateBuildParameterPortalBatchName();
		_validateBuildParameterPortalBatchTestSelector();
		_validateBuildParameterPortalBranchSHAs();
		_validateBuildParameterPortalGitHubURL();
		_validateBuildParameterPortalUpstreamBranchName();
		_validateBuildParameterRetestCherryPickSHA();
		_validateBuildParameterRetestCount();
	}

	private void _failInvalidPortalRepositoryName(
		String buildParameter, String portalUpstreamBranchName) {

		String portalRepositoryName = "liferay-portal";

		if (!portalUpstreamBranchName.equals("master")) {
			portalRepositoryName += "-ee";
		}

		failBuildRunner(
			JenkinsResultsParserUtil.combine(
				buildParameter, " should point to a ", portalRepositoryName,
				" GitHub URL"));
	}

	private int _getAllowedPortalBranchSHACount() {
		String allowedPortalBranchSHACount = getJobPropertyValue(
			"allowed.portal.branch.shas");

		if ((allowedPortalBranchSHACount == null) ||
			allowedPortalBranchSHACount.isEmpty()) {

			return -1;
		}

		return Integer.valueOf(allowedPortalBranchSHACount);
	}

	private String _getBatchName() {
		BuildData buildData = getBuildData();

		return JenkinsResultsParserUtil.getBuildParameter(
			buildData.getBuildURL(), "PORTAL_BATCH_NAME");
	}

	private String _getDownstreamBuildDescription(String portalBranchSHA) {
		PortalTopLevelBuildData portalTopLevelBuildData = getBuildData();

		StringBuilder sb = new StringBuilder();

		sb.append(portalBranchSHA);
		sb.append(" - ");
		sb.append(_getBatchName());
		sb.append(" - ");
		sb.append("<a href=\"https://");
		sb.append(portalTopLevelBuildData.getTopLevelMasterHostname());
		sb.append(".liferay.com/userContent/");
		sb.append(portalTopLevelBuildData.getUserContentRelativePath());
		sb.append("jenkins-report.html\">Jenkins Report</a>");

		sb.append("<ul>");

		for (String test : _getTestList()) {
			sb.append("<li>");
			sb.append(test);
			sb.append("</li>");
		}

		sb.append("</ul>");

		return sb.toString();
	}

	private int _getMaxCommitGroupCount() {
		int maxCommitGroupCount = _getAllowedPortalBranchSHACount();

		if (maxCommitGroupCount != -1) {
			return maxCommitGroupCount;
		}

		return _COMMITS_GROUP_SIZE_MAX_DEFAULT;
	}

	private int _getMaxRetestCount() {
		String maxRetestCount = getJobPropertyValue("maximum.retest.count");

		if ((maxRetestCount == null) || maxRetestCount.isEmpty()) {
			return -1;
		}

		try {
			return Integer.valueOf(maxRetestCount);
		}
		catch (NumberFormatException numberFormatException) {
			numberFormatException.printStackTrace();

			return -1;
		}
	}

	private List<String> _getPortalBranchSHAs() {
		String portalBranchSHAsString = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_BRANCH_SHAS);

		if ((portalBranchSHAsString == null) ||
			portalBranchSHAsString.isEmpty()) {

			Workspace workspace = getWorkspace();

			WorkspaceGitRepository workspaceGitRepository =
				workspace.getPrimaryWorkspaceGitRepository();

			GitWorkingDirectory gitWorkingDirectory =
				workspaceGitRepository.getGitWorkingDirectory();

			List<LocalGitCommit> localGitCommits = gitWorkingDirectory.log(1);

			LocalGitCommit localGitCommit = localGitCommits.get(0);

			return Collections.singletonList(localGitCommit.getSHA());
		}

		Matcher matcher = _compareURLPattern.matcher(portalBranchSHAsString);

		if (matcher.find()) {
			Workspace workspace = getWorkspace();

			WorkspaceGitRepository workspaceGitRepository =
				workspace.getPrimaryWorkspaceGitRepository();

			List<LocalGitCommit> rangeLocalGitCommits = new ArrayList<>();

			try {
				rangeLocalGitCommits =
					workspaceGitRepository.getRangeLocalGitCommits(
						matcher.group("earliestSHA"),
						matcher.group("latestSHA"));
			}
			catch (Exception exception) {
				failBuildRunner(
					"Unable to store the commit history", exception);
			}

			List<List<LocalGitCommit>> localGitCommitsLists =
				workspaceGitRepository.partitionLocalGitCommits(
					rangeLocalGitCommits, _getMaxCommitGroupCount());

			List<String> portalBranchSHAs = new ArrayList<>();

			for (List<LocalGitCommit> localGitCommits : localGitCommitsLists) {
				LocalGitCommit localGitCommit = localGitCommits.get(0);

				portalBranchSHAs.add(localGitCommit.getSHA());
			}

			return portalBranchSHAs;
		}

		List<String> portalBranchSHAs = new ArrayList<>();

		for (String portalBranchSHA : portalBranchSHAsString.split(",")) {
			portalBranchSHAs.add(portalBranchSHA.trim());
		}

		return portalBranchSHAs;
	}

	private List<String> _getPortalCherryPickSHAs() {
		List<String> portalCherryPickSHAList = new ArrayList<>();

		String portalCherryPickSHAsString = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_CHERRY_PICK_SHAS);

		if (JenkinsResultsParserUtil.isNullOrEmpty(
				portalCherryPickSHAsString)) {

			return portalCherryPickSHAList;
		}

		for (String portalCherryPickSHA :
				portalCherryPickSHAsString.split(",")) {

			portalCherryPickSHAList.add(portalCherryPickSHA.trim());
		}

		return portalCherryPickSHAList;
	}

	private String _getPortalGitHubURL() {
		return getBuildParameter(_NAME_BUILD_PARAMETER_PORTAL_GITHUB_URL);
	}

	private int _getRetestCount() {
		String retestCount = getBuildParameter(
			_NAME_BUILD_PARAMETER_RETEST_COUNT);

		if ((retestCount == null) || retestCount.isEmpty()) {
			return 1;
		}

		try {
			return Integer.parseInt(retestCount);
		}
		catch (NumberFormatException numberFormatException) {
			numberFormatException.printStackTrace();

			return 1;
		}
	}

	private List<String> _getTestList() {
		String portalBatchTestSelector = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_BATCH_TEST_SELECTOR);

		List<String> list = new ArrayList<>();

		if (JenkinsResultsParserUtil.isNullOrEmpty(portalBatchTestSelector)) {
			if (!_isModulesBatch()) {
				return list;
			}

			BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

			buildDatabase.putProperty(
				"start.properties", "PORTAL_BATCH_TEST_SELECTOR", "**/*");
		}

		BatchTestClassGroup batchTestClassGroup =
			TestClassGroupFactory.newBatchTestClassGroup(
				_getBatchName(), getJob());

		for (TestClass testClass : batchTestClassGroup.getTestClasses()) {
			if (testClass instanceof FunctionalTestClass) {
				FunctionalTestClass functionalTestClass =
					(FunctionalTestClass)testClass;

				list.add(functionalTestClass.getTestClassMethodName());
			}
			else if ((testClass instanceof JUnitTestClass) &&
					 !(testClass instanceof PlaywrightJUnitTestClass)) {

				String testClassFilePath =
					JenkinsResultsParserUtil.getCanonicalPath(
						testClass.getTestClassFile());

				testClassFilePath = testClassFilePath.replaceAll(
					".*/(com/.*)\\.java", "$1.class");

				JUnitTestClass jUnitTestClass = (JUnitTestClass)testClass;

				List<String> testClassMethodNames =
					jUnitTestClass.getTestClassMethodNames();

				if (!testClassMethodNames.isEmpty()) {
					for (String testClassMethodName : testClassMethodNames) {
						list.add(testClassFilePath + "#" + testClassMethodName);
					}

					continue;
				}

				list.add(testClassFilePath);
			}
			else if (testClass instanceof ModulesTestClass) {
				for (TestClassMethod testClassMethod :
						testClass.getTestClassMethods()) {

					list.add(testClassMethod.getName());
				}
			}
			else if (testClass instanceof PlaywrightJUnitTestClass) {
				list.add(testClass.getName());
			}
		}

		return list;
	}

	private boolean _isFunctionalBatch() {
		String portalBatchName = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_BATCH);

		return portalBatchName.startsWith("functional");
	}

	private boolean _isJUnitBatch() {
		String portalBatchName = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_BATCH);

		if (portalBatchName.startsWith("integration") ||
			portalBatchName.startsWith("modules-integration") ||
			portalBatchName.startsWith("modules-unit") ||
			portalBatchName.startsWith("unit")) {

			return true;
		}

		return false;
	}

	private boolean _isModulesBatch() {
		String portalBatchName = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_BATCH);

		if (portalBatchName.startsWith("js-unit") ||
			portalBatchName.startsWith("modules-compile") ||
			portalBatchName.startsWith("modules-semantic-versioning") ||
			portalBatchName.startsWith("rest-builder") ||
			portalBatchName.startsWith("service-builder")) {

			return true;
		}

		return false;
	}

	private void _validateBuildParameterJenkinsGitHubURL() {
		String jenkinsGitHubURL = getBuildParameter(
			_NAME_BUILD_PARAMETER_JENKINS_GITHUB_URL);

		if ((jenkinsGitHubURL == null) || jenkinsGitHubURL.isEmpty()) {
			return;
		}

		String failureMessage = JenkinsResultsParserUtil.combine(
			_NAME_BUILD_PARAMETER_JENKINS_GITHUB_URL,
			" has an invalid Jenkins GitHub URL <a href=\"", jenkinsGitHubURL,
			"\">", jenkinsGitHubURL, "</a>");

		Matcher matcher = _portalURLPattern.matcher(jenkinsGitHubURL);

		if (!matcher.find()) {
			failBuildRunner(failureMessage);
		}

		String repositoryName = matcher.group("repositoryName");

		if (!repositoryName.equals("liferay-jenkins-ee")) {
			failBuildRunner(failureMessage);
		}
	}

	private void _validateBuildParameterPortalBatchName() {
		String portalBatchName = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_BATCH);

		if ((portalBatchName == null) || portalBatchName.isEmpty()) {
			failBuildRunner(_NAME_BUILD_PARAMETER_PORTAL_BATCH + " is null");
		}

		String allowedPortalBatchNames = getJobPropertyValue(
			JenkinsResultsParserUtil.combine(
				"allowed.portal.batch.names[",
				getBuildParameter(
					_NAME_BUILD_PARAMETER_PORTAL_UPSTREAM_BRANCH_NAME),
				"]"));

		if ((allowedPortalBatchNames == null) ||
			allowedPortalBatchNames.isEmpty()) {

			return;
		}

		List<String> allowedPortalBatchNamesList = Arrays.asList(
			allowedPortalBatchNames.split(","));

		if (!allowedPortalBatchNamesList.contains(portalBatchName)) {
			StringBuilder sb = new StringBuilder();

			sb.append(_NAME_BUILD_PARAMETER_PORTAL_BATCH);
			sb.append(" must match one of the following: ");

			sb.append("<ul>");

			for (String allowedPortalBatchName : allowedPortalBatchNamesList) {
				sb.append("<li>");
				sb.append(allowedPortalBatchName);
				sb.append("</li>");
			}

			sb.append("</ul>");

			failBuildRunner(sb.toString());
		}
	}

	private void _validateBuildParameterPortalBatchTestSelector() {
		if (!_isFunctionalBatch() && !_isJUnitBatch()) {
			return;
		}

		String portalBatchTestSelector = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_BATCH_TEST_SELECTOR);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalBatchTestSelector)) {
			return;
		}

		String portalBatchName = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_BATCH);

		failBuildRunner(
			JenkinsResultsParserUtil.combine(
				_NAME_BUILD_PARAMETER_PORTAL_BATCH_TEST_SELECTOR,
				" is required for ", portalBatchName));
	}

	private void _validateBuildParameterPortalBranchSHAs() {
		String portalBranchSHAs = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_BRANCH_SHAS);

		if ((portalBranchSHAs == null) || portalBranchSHAs.isEmpty()) {
			return;
		}

		int allowedPortalBranchSHACount = _getAllowedPortalBranchSHACount();

		if (allowedPortalBranchSHACount == -1) {
			return;
		}

		int portalBranchSHACount =
			StringUtils.countMatches(portalBranchSHAs, ",") + 1;

		int retestCount = _getRetestCount();

		if (retestCount != 1) {
			allowedPortalBranchSHACount = 1;
		}

		if (portalBranchSHACount > allowedPortalBranchSHACount) {
			failBuildRunner(
				JenkinsResultsParserUtil.combine(
					_NAME_BUILD_PARAMETER_PORTAL_BRANCH_SHAS,
					" may only reference ",
					String.valueOf(allowedPortalBranchSHACount),
					" portal branch SHAs"));
		}

		Matcher matcher = _compareURLPattern.matcher(portalBranchSHAs);

		if (matcher.find()) {
			String portalUpstreamBranchName = getBuildParameter(
				_NAME_BUILD_PARAMETER_PORTAL_UPSTREAM_BRANCH_NAME);
			String repositoryName = matcher.group("repositoryName");

			if ((repositoryName.equals("liferay-portal") &&
				 !portalUpstreamBranchName.equals("master")) ||
				(repositoryName.equals("liferay-portal-ee") &&
				 portalUpstreamBranchName.equals("master"))) {

				_failInvalidPortalRepositoryName(
					_NAME_BUILD_PARAMETER_PORTAL_BRANCH_SHAS,
					portalUpstreamBranchName);
			}
		}
	}

	private void _validateBuildParameterPortalGitHubURL() {
		String portalGitHubURL = _getPortalGitHubURL();

		if ((portalGitHubURL == null) || portalGitHubURL.isEmpty()) {
			failBuildRunner(
				_NAME_BUILD_PARAMETER_PORTAL_GITHUB_URL + " is null");
		}

		String failureMessage = JenkinsResultsParserUtil.combine(
			_NAME_BUILD_PARAMETER_PORTAL_GITHUB_URL,
			" has an invalid Portal GitHub URL <a href=\"", portalGitHubURL,
			"\">", portalGitHubURL, "</a>");

		Matcher matcher = _portalURLPattern.matcher(portalGitHubURL);

		if (!matcher.find()) {
			failBuildRunner(failureMessage);
		}

		String repositoryName = matcher.group("repositoryName");

		if (!repositoryName.equals("liferay-portal") &&
			!repositoryName.equals("liferay-portal-ee")) {

			failBuildRunner(failureMessage);
		}

		String portalUpstreamBranchName = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_UPSTREAM_BRANCH_NAME);

		if ((repositoryName.equals("liferay-portal") &&
			 !portalUpstreamBranchName.equals("master")) ||
			(repositoryName.equals("liferay-portal-ee") &&
			 portalUpstreamBranchName.equals("master"))) {

			_failInvalidPortalRepositoryName(
				_NAME_BUILD_PARAMETER_PORTAL_GITHUB_URL,
				portalUpstreamBranchName);
		}
	}

	private void _validateBuildParameterPortalUpstreamBranchName() {
		String portalUpstreamBranchName = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_UPSTREAM_BRANCH_NAME);

		if ((portalUpstreamBranchName == null) ||
			portalUpstreamBranchName.isEmpty()) {

			failBuildRunner(
				_NAME_BUILD_PARAMETER_PORTAL_UPSTREAM_BRANCH_NAME + " is null");
		}

		if (portalUpstreamBranchName.matches("release-\\d{4}.q\\d+")) {
			return;
		}

		String allowedPortalUpstreamBranchNames = getJobPropertyValue(
			"allowed.portal.upstream.branch.names");

		if ((allowedPortalUpstreamBranchNames == null) ||
			allowedPortalUpstreamBranchNames.isEmpty()) {

			return;
		}

		List<String> allowedPortalUpstreamBranchNamesList = Arrays.asList(
			allowedPortalUpstreamBranchNames.split(","));

		if (!allowedPortalUpstreamBranchNamesList.contains(
				portalUpstreamBranchName)) {

			StringBuilder sb = new StringBuilder();

			sb.append(_NAME_BUILD_PARAMETER_PORTAL_UPSTREAM_BRANCH_NAME);
			sb.append(" must match one of the following: ");

			sb.append("<ul>");

			for (String allowedPortalUpstreamBranchName :
					allowedPortalUpstreamBranchNamesList) {

				sb.append("<li>");
				sb.append(allowedPortalUpstreamBranchName);
				sb.append("</li>");
			}

			sb.append("</ul>");
			sb.append("or is not a valid release branch.");

			failBuildRunner(sb.toString());
		}
	}

	private void _validateBuildParameterRetestCherryPickSHA() {
		String cherryPickSHAs = getBuildParameter(
			_NAME_BUILD_PARAMETER_PORTAL_CHERRY_PICK_SHAS);

		if ((cherryPickSHAs == null) || cherryPickSHAs.isEmpty()) {
			return;
		}

		int retestCount = _getRetestCount();

		if (retestCount != 1) {
			failBuildRunner(
				JenkinsResultsParserUtil.combine(
					"Cherry-picked SHAs may not be used when retesting."));
		}
	}

	private void _validateBuildParameterRetestCount() {
		String retestCount = getBuildParameter(
			_NAME_BUILD_PARAMETER_RETEST_COUNT);

		if ((retestCount == null) || retestCount.isEmpty()) {
			return;
		}

		int retestCountInt = 0;

		try {
			retestCountInt = Integer.parseInt(retestCount);
		}
		catch (NumberFormatException numberFormatException) {
			failBuildRunner(
				JenkinsResultsParserUtil.combine(
					_NAME_BUILD_PARAMETER_RETEST_COUNT, " parameter value: \"",
					retestCount, "\" is not a number."));
		}

		int maxRetestCount = _getMaxRetestCount();

		if ((retestCountInt < 0) || (retestCountInt > maxRetestCount)) {
			failBuildRunner(
				JenkinsResultsParserUtil.combine(
					_NAME_BUILD_PARAMETER_RETEST_COUNT,
					" must be between 0 and ", String.valueOf(maxRetestCount),
					"."));
		}
	}

	private static final int _COMMITS_GROUP_SIZE_MAX_DEFAULT = 5;

	private static final String _NAME_BUILD_PARAMETER_JENKINS_GITHUB_URL =
		"JENKINS_GITHUB_URL";

	private static final String _NAME_BUILD_PARAMETER_PORTAL_BATCH =
		"PORTAL_BATCH_NAME";

	private static final String
		_NAME_BUILD_PARAMETER_PORTAL_BATCH_TEST_SELECTOR =
			"PORTAL_BATCH_TEST_SELECTOR";

	private static final String _NAME_BUILD_PARAMETER_PORTAL_BRANCH_SHAS =
		"PORTAL_BRANCH_SHAS";

	private static final String _NAME_BUILD_PARAMETER_PORTAL_CHERRY_PICK_SHAS =
		"PORTAL_CHERRY_PICK_SHAS";

	private static final String _NAME_BUILD_PARAMETER_PORTAL_GITHUB_URL =
		"PORTAL_GITHUB_URL";

	private static final String
		_NAME_BUILD_PARAMETER_PORTAL_UPSTREAM_BRANCH_NAME =
			"PORTAL_UPSTREAM_BRANCH_NAME";

	private static final String _NAME_BUILD_PARAMETER_RETEST_COUNT =
		"RETEST_COUNT";

	private static final Pattern _compareURLPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"https://github.com/(?<username>[^/]+)/(?<repositoryName>[^/]+)",
			"/compare/(?<earliestSHA>[0-9a-f]{5,40})\\.{3}",
			"(?<latestSHA>[0-9a-f]{5,40})"));
	private static final Pattern _portalURLPattern = Pattern.compile(
		"https://github.com/[^/]+/(?<repositoryName>[^/]+)/tree/.+");

}