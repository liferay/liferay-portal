/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.failure.message.generator.FailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.FormatFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GenericFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.NodeSourceFormatFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.PoshiValidationFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.RebaseFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.RelevantRuleValidationFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.SourceFormatFailureMessageGenerator;

import java.io.File;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Cesar Polanco
 */
public class SourceFormatBuild
	extends DefaultTopLevelBuild
	implements PortalBranchInformationBuild, PullRequestBuild, WorkspaceBuild {

	public boolean bypassCITestRelevant() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository == null) {
			return false;
		}

		return portalWorkspaceGitRepository.bypassCITestRelevant();
	}

	@Override
	public String getBaseGitRepositoryName() {
		PullRequest pullRequest = getPullRequest();

		return pullRequest.getGitHubRemoteGitRepositoryName();
	}

	@Override
	public String getBaseGitRepositorySHA(String gitRepositoryName) {
		if (_baseGitRepositorySHA != null) {
			return _baseGitRepositorySHA;
		}

		if (!fromArchive) {
			Workspace workspace = getWorkspace();

			WorkspaceGitRepository primaryWorkspaceGitRepository =
				workspace.getPrimaryWorkspaceGitRepository();

			_baseGitRepositorySHA =
				primaryWorkspaceGitRepository.getBaseBranchSHA();

			return _baseGitRepositorySHA;
		}

		String consoleText = getConsoleText();

		for (String line : consoleText.split("\\s*\\n\\s*")) {
			Matcher matcher = _gitHubUpstreamBranchShaPattern.matcher(line);

			if (matcher.find()) {
				_baseGitRepositorySHA = matcher.group("sha");

				return _baseGitRepositorySHA;
			}
		}

		throw new RuntimeException(
			"Unable to find Source Format Base Git Repository SHA");
	}

	@Override
	public String getBranchName() {
		PullRequest pullRequest = getPullRequest();

		return pullRequest.getUpstreamRemoteGitBranchName();
	}

	@Override
	public Element[] getBuildFailureElements() {
		return new Element[] {getFailureMessageElement()};
	}

	@Override
	public Job.BuildProfile getBuildProfile() {
		return Job.BuildProfile.DXP;
	}

	@Override
	public BranchInformation getPortalBaseBranchInformation() {
		return null;
	}

	@Override
	public BranchInformation getPortalBranchInformation() {
		Workspace workspace = getWorkspace();

		return new WorkspaceBranchInformation(
			workspace.getPrimaryWorkspaceGitRepository());
	}

	@Override
	public PullRequest getPullRequest() {
		if (_pullRequest != null) {
			return _pullRequest;
		}

		_pullRequest = PullRequestFactory.newPullRequest(
			getParameterValue("PULL_REQUEST_URL"));

		return _pullRequest;
	}

	@Override
	public String getTestSuiteName() {
		return "sf";
	}

	@Override
	public Element getTopGitHubMessageElement() {
		update();

		Element detailsElement = Dom4JUtil.getNewElement(
			"details", null,
			Dom4JUtil.getNewElement(
				"summary", null, "Click here for more details."),
			Dom4JUtil.getNewElement("h4", null, "Base Branch:"),
			getBaseBranchDetailsElement(),
			Dom4JUtil.getNewElement("h4", null, "Sender Branch:"),
			getSenderBranchDetailsElement());

		String result = getResult();
		int successCount = 0;

		if (Objects.equals(result, "SUCCESS")) {
			successCount++;
		}

		Dom4JUtil.addToElement(
			detailsElement, String.valueOf(successCount), " out of ",
			String.valueOf(getDownstreamBuildCountByResult(null) + 1),
			" jobs PASSED");

		if (Objects.equals(result, "SUCCESS")) {
			Dom4JUtil.addToElement(
				detailsElement, getSuccessfulJobSummaryElement());
		}
		else {
			Dom4JUtil.addToElement(
				detailsElement, getFailedJobSummaryElement());
		}

		Dom4JUtil.addToElement(detailsElement, getMoreDetailsElement());

		if (!Objects.equals(result, "SUCCESS")) {
			Dom4JUtil.addToElement(
				detailsElement, (Object[])getBuildFailureElements());
		}

		return Dom4JUtil.getNewElement(
			"html", null, getResultElement(),
			getSourceFormatterVersionElement(), detailsElement);
	}

	@Override
	public Workspace getWorkspace() {
		PullRequest pullRequest = getPullRequest();

		Workspace workspace = WorkspaceFactory.newWorkspace(
			pullRequest.getGitRepositoryName(),
			pullRequest.getUpstreamRemoteGitBranchName(), getJobName());

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		workspaceGitRepository.setGitHubURL(pullRequest.getHtmlURL());

		String senderBranchSHA = getParameterValue("GITHUB_SENDER_BRANCH_SHA");

		if (JenkinsResultsParserUtil.isSHA(senderBranchSHA)) {
			workspaceGitRepository.setSenderBranchSHA(senderBranchSHA);
		}

		String upstreamBranchSHA = getParameterValue(
			"GITHUB_UPSTREAM_BRANCH_SHA");

		if (JenkinsResultsParserUtil.isSHA(upstreamBranchSHA)) {
			workspaceGitRepository.setBaseBranchSHA(upstreamBranchSHA);
		}

		return workspace;
	}

	protected SourceFormatBuild(String url) {
		this(url, null);
	}

	protected SourceFormatBuild(String url, TopLevelBuild topLevelBuild) {
		super(url, topLevelBuild);
	}

	@Override
	protected FailureMessageGenerator[] getFailureMessageGenerators() {
		return new FailureMessageGenerator[] {
			new NodeSourceFormatFailureMessageGenerator(),
			//
			new FormatFailureMessageGenerator(),
			new PoshiValidationFailureMessageGenerator(),
			new RebaseFailureMessageGenerator(),
			new RelevantRuleValidationFailureMessageGenerator(),
			new SourceFormatFailureMessageGenerator(),
			//
			new GenericFailureMessageGenerator()
		};
	}

	protected Element getSenderBranchDetailsElement() {
		PullRequest pullRequest = getPullRequest();

		String gitHubRemoteGitRepositoryName =
			pullRequest.getGitHubRemoteGitRepositoryName();
		String senderBranchName = pullRequest.getSenderBranchName();
		String senderUsername = pullRequest.getSenderUsername();

		String senderBranchURL = JenkinsResultsParserUtil.combine(
			"https://github.com/", senderUsername, "/",
			gitHubRemoteGitRepositoryName, "/tree/", senderBranchName);

		String senderSHA = pullRequest.getSenderSHA();

		String senderCommitURL = JenkinsResultsParserUtil.combine(
			"https://github.com/", senderUsername, "/",
			gitHubRemoteGitRepositoryName, "/commit/", senderSHA);

		return Dom4JUtil.getNewElement(
			"p", null, "Branch Name: ",
			Dom4JUtil.getNewAnchorElement(senderBranchURL, senderBranchName),
			Dom4JUtil.getNewElement("br"), "Branch GIT ID: ",
			Dom4JUtil.getNewAnchorElement(senderCommitURL, senderSHA));
	}

	protected Element getSourceFormatterVersionElement() {
		Element sourceFormatterVersionElement = Dom4JUtil.getNewElement("p");

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository == null) {
			return sourceFormatterVersionElement;
		}

		if (_isSourceFormatterBuilt()) {
			return Dom4JUtil.getNewElement(
				"span", sourceFormatterVersionElement, "Ran ",
				_SOURCE_FORMATTER_PACKAGE_NAME, " built at ",
				portalWorkspaceGitRepository.getSenderBranchSHA(), ".");
		}

		String sourceFormatterVersion = _getSourceFormatterVersion();

		if (sourceFormatterVersion == null) {
			return sourceFormatterVersionElement;
		}

		Dom4JUtil.getNewElement(
			"span", sourceFormatterVersionElement, "Ran ",
			_SOURCE_FORMATTER_PACKAGE_NAME, " at released version ",
			Dom4JUtil.getNewAnchorElement(
				JenkinsResultsParserUtil.combine(
					"https://repository.liferay.com/nexus/content/",
					"repositories/liferay-public-releases/com/liferay/",
					_SOURCE_FORMATTER_PACKAGE_NAME, "/",
					sourceFormatterVersion),
				sourceFormatterVersion),
			".");

		if (_isLatestSourceFormatterReleased()) {
			return sourceFormatterVersionElement;
		}

		Dom4JUtil.getNewElement("br", sourceFormatterVersionElement);

		Dom4JUtil.getNewElement(
			"em", sourceFormatterVersionElement, "*The ",
			Dom4JUtil.getNewAnchorElement(
				JenkinsResultsParserUtil.combine(
					"https://github.com/", _GITHUB_USER_NAME, "/",
					_GITHUB_REPOSITORY_NAME, "/commits/", _GITHUB_BRANCH_NAME,
					"/", _SOURCE_FORMATTER_PATH),
				"latest version"),
			" has not been released.");

		return sourceFormatterVersionElement;
	}

	private PortalWorkspaceGitRepository _getPortalWorkspaceGitRepository() {
		Workspace workspace = getWorkspace();

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		if (!(workspaceGitRepository instanceof PortalWorkspaceGitRepository)) {
			return null;
		}

		return (PortalWorkspaceGitRepository)workspaceGitRepository;
	}

	private String _getSourceFormatterVersion() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository == null) {
			return null;
		}

		File ivyXMLFile = new File(
			portalWorkspaceGitRepository.getDirectory(),
			JenkinsResultsParserUtil.combine(
				"tools/sdk/dependencies/", _SOURCE_FORMATTER_PACKAGE_NAME,
				"/ivy.xml"));

		if (!ivyXMLFile.exists()) {
			return null;
		}

		try {
			Document document = Dom4JUtil.parse(
				JenkinsResultsParserUtil.read(ivyXMLFile));

			Element rootElement = document.getRootElement();

			Element dependenciesElement = rootElement.element("dependencies");

			for (Element dependencyElement :
					dependenciesElement.elements("dependency")) {

				if (!Objects.equals(
						dependencyElement.attributeValue("name"),
						_SOURCE_FORMATTER_PACKAGE_NAME)) {

					continue;
				}

				return dependencyElement.attributeValue("rev");
			}

			return null;
		}
		catch (Exception exception) {
			return null;
		}
	}

	private boolean _isLatestSourceFormatterReleased() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository == null) {
			return false;
		}

		try {
			GitWorkingDirectory gitWorkingDirectory =
				portalWorkspaceGitRepository.getGitWorkingDirectory();

			File sourceFormatterFile = new File(
				gitWorkingDirectory.getWorkingDirectory(),
				_SOURCE_FORMATTER_PATH);

			List<LocalGitCommit> localGitCommits = gitWorkingDirectory.log(
				1, sourceFormatterFile);

			if (localGitCommits.isEmpty()) {
				return false;
			}

			LocalGitCommit localGitCommit = localGitCommits.get(0);

			return localGitCommit.isFileChanged(
				new File(sourceFormatterFile, "bnd.bnd"));
		}
		catch (Exception exception) {
			return false;
		}
	}

	private boolean _isSourceFormatterBuilt() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository == null) {
			return false;
		}

		Matcher matcher = _upstreamBranchNamePattern.matcher(
			portalWorkspaceGitRepository.getUpstreamBranchName());

		if (matcher.matches()) {
			return true;
		}

		GitWorkingDirectory gitWorkingDirectory =
			portalWorkspaceGitRepository.getGitWorkingDirectory();

		for (File modifiedFile : gitWorkingDirectory.getModifiedFilesList()) {
			String modifiedFilePath = JenkinsResultsParserUtil.getCanonicalPath(
				modifiedFile);

			if (modifiedFilePath.contains(_SOURCE_FORMATTER_PATH) ||
				modifiedFilePath.contains("source-formatter.properties")) {

				return true;
			}
		}

		return false;
	}

	private static final String _GITHUB_BRANCH_NAME = "master";

	private static final String _GITHUB_REPOSITORY_NAME = "liferay-portal";

	private static final String _GITHUB_USER_NAME = "liferay";

	private static final String _SOURCE_FORMATTER_PACKAGE_NAME =
		"com.liferay.source.formatter";

	private static final String _SOURCE_FORMATTER_PATH =
		"modules/util/source-formatter";

	private static final Pattern _gitHubUpstreamBranchShaPattern =
		Pattern.compile(
			"\\[beanshell\\] GITHUB_UPSTREAM_BRANCH_SHA=" +
				"(?<sha>[0-9a-f]{7,40})");
	private static final Pattern _upstreamBranchNamePattern = Pattern.compile(
		"release-((\\d{4})\\.q([1-4])|(7\\.[0-4]\\.[0-9]?[0-9]\\.\\d+))");

	private String _baseGitRepositorySHA;
	private PullRequest _pullRequest;

}