/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.Properties;

import org.json.JSONObject;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.InOrder;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class SourceFormatBuildTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetSourceFormatterVersionFromConsoleText()
		throws Exception {

		_writeIvyXML("1.0.1049");

		Assert.assertEquals(
			"1.0.1622",
			_getSourceFormatterVersion(
				"[echo] Running com.liferay.source.formatter.jar 1.0.1622."));
	}

	@Test
	public void testGetSourceFormatterVersionFromIvyXML() throws Exception {
		_writeIvyXML("1.0.1049");

		Assert.assertEquals(
			"1.0.1049",
			_getSourceFormatterVersion(
				"[echo] Running com.liferay.source.formatter.jar from HEAD."));
	}

	@Test
	public void testGetSourceFormatterVersionWithConsoleTextFailure()
		throws Exception {

		_writeIvyXML("1.0.1049");

		SourceFormatBuild sourceFormatBuild = Mockito.mock(
			SourceFormatBuild.class);

		Mockito.when(
			sourceFormatBuild.getConsoleText()
		).thenThrow(
			new RuntimeException()
		);

		Assert.assertEquals(
			"1.0.1049", _getSourceFormatterVersion(sourceFormatBuild));
	}

	@Test
	public void testGetSourceFormatterVersionWithMultipleMatches()
		throws Exception {

		Assert.assertEquals(
			"1.0.1623",
			_getSourceFormatterVersion(
				"[echo] Running com.liferay.source.formatter.jar 1.0.1622.\n" +
					"[echo] Running com.liferay.source.formatter.jar " +
						"1.0.1623."));
	}

	@Test
	public void testGetSourceFormatterVersionWithoutVersion() throws Exception {
		Assert.assertNull(_getSourceFormatterVersion(""));
	}

	@Test
	public void testGetWorkspace() throws Exception {
		_testGetWorkspace(false, _newCompareJSON(1), false, true, "{}");
		_testGetWorkspace(
			false, _newCompareJSON(1), false, true, _newCompareJSON(0));
		_testGetWorkspace(false, _newCompareJSON(1), false, true, null);
		_testGetWorkspace(
			false, _newCompareJSON(3), false, false, _newCompareJSON(3));
		_testGetWorkspace(
			false, _newCompareJSON(5), false, true, _newCompareJSON(1));
		_testGetWorkspace(false, null, false, true, _newCompareJSON(5));
		_testGetWorkspace(
			true, _newCompareJSON(0), false, true, _newCompareJSON(5));
		_testGetWorkspace(
			true, _newCompareJSON(1), false, false, _newCompareJSON(5));
		_testGetWorkspace(
			true, _newCompareJSON(2), false, true, _newCompareJSON(3));
		_testGetWorkspace(true, null, true, false, null);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private String _getSourceFormatterVersion(
			SourceFormatBuild sourceFormatBuild)
		throws Exception {

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		Mockito.when(
			portalWorkspaceGitRepository.getDirectory()
		).thenReturn(
			temporaryFolder.getRoot()
		);

		Workspace workspace = Mockito.mock(Workspace.class);

		Mockito.when(
			sourceFormatBuild.getWorkspace()
		).thenReturn(
			workspace
		);

		Mockito.when(
			workspace.getPrimaryWorkspaceGitRepository()
		).thenReturn(
			portalWorkspaceGitRepository
		);

		return ReflectionTestUtil.invoke(
			sourceFormatBuild, "_getSourceFormatterVersion", new Class<?>[0]);
	}

	private String _getSourceFormatterVersion(String consoleText)
		throws Exception {

		SourceFormatBuild sourceFormatBuild = Mockito.mock(
			SourceFormatBuild.class);

		Mockito.when(
			sourceFormatBuild.getConsoleText()
		).thenReturn(
			consoleText
		);

		return _getSourceFormatterVersion(sourceFormatBuild);
	}

	private String _newCompareJSON(int aheadBy) {
		JSONObject compareJSONObject = new JSONObject();

		compareJSONObject.put("ahead_by", aheadBy);

		return compareJSONObject.toString();
	}

	private void _setCompareJSON(String compareJSON, String url)
		throws Exception {

		if (compareJSON == null) {
			setURLReaderException(
				new IOException(RandomTestUtil.randomString()), url);

			return;
		}

		setURLReaderOutput(compareJSON, url);
	}

	private void _testGetWorkspace(
			boolean expectedOwnerBase, String ownerCompareJSON,
			boolean ownerUpstream, boolean senderBranchSHAParameter,
			String upstreamCompareJSON)
		throws Exception {

		mockURLReaders();

		String ownerUsername = RandomTestUtil.randomString();
		String upstreamUsername = RandomTestUtil.randomString();

		if (ownerUpstream) {
			upstreamUsername = ownerUsername;
		}

		String upstreamRemoteGitBranchName = RandomTestUtil.randomString();

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"portal.upstream.username", RandomTestUtil.randomString());
		buildProperties.setProperty(
			JenkinsResultsParserUtil.combine(
				"portal.upstream.username[", upstreamRemoteGitBranchName, "]"),
			upstreamUsername);

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		BuildDatabaseUtil.setBuildDatabase(
			new DefaultBuildDatabase(BuildDatabaseTestUtil.newBuildDir()));

		String senderBranchSHA = null;

		if (senderBranchSHAParameter) {
			senderBranchSHA = RandomTestUtil.randomSHA();
		}

		String pullRequestSenderSHA = RandomTestUtil.randomSHA();

		String compareSHA = pullRequestSenderSHA;

		if (senderBranchSHAParameter) {
			compareSHA = senderBranchSHA;
		}

		String gitRepositoryName = JenkinsResultsParserUtil.combine(
			"liferay-portal-", RandomTestUtil.randomString());

		String ownerCompareURL = JenkinsResultsParserUtil.combine(
			"https://api.github.com/repos/", ownerUsername, "/",
			gitRepositoryName, "/compare/", ownerUsername, ":",
			upstreamRemoteGitBranchName, "...", compareSHA);
		String upstreamCompareURL = JenkinsResultsParserUtil.combine(
			"https://api.github.com/repos/", ownerUsername, "/",
			gitRepositoryName, "/compare/", upstreamUsername, ":",
			upstreamRemoteGitBranchName, "...", compareSHA);

		if (!ownerUpstream) {
			_setCompareJSON(ownerCompareJSON, ownerCompareURL);
			_setCompareJSON(upstreamCompareJSON, upstreamCompareURL);
		}

		String gitHubURL = RandomTestUtil.randomString();

		PullRequest pullRequest = Mockito.mock(PullRequest.class);

		Mockito.doReturn(
			gitRepositoryName
		).when(
			pullRequest
		).getGitRepositoryName();

		Mockito.doReturn(
			gitHubURL
		).when(
			pullRequest
		).getHtmlURL();

		Mockito.doReturn(
			ownerUsername
		).when(
			pullRequest
		).getOwnerUsername();

		Mockito.doReturn(
			pullRequestSenderSHA
		).when(
			pullRequest
		).getSenderSHA();

		Mockito.doReturn(
			upstreamRemoteGitBranchName
		).when(
			pullRequest
		).getUpstreamRemoteGitBranchName();

		SourceFormatBuild sourceFormatBuild = Mockito.mock(
			SourceFormatBuild.class);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			sourceFormatBuild
		).getJobName();

		Mockito.doReturn(
			senderBranchSHA
		).when(
			sourceFormatBuild
		).getParameterValue(
			"GITHUB_SENDER_BRANCH_SHA"
		);

		Mockito.doReturn(
			pullRequest
		).when(
			sourceFormatBuild
		).getPullRequest();

		Mockito.doCallRealMethod(
		).when(
			sourceFormatBuild
		).getWorkspace();

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		Workspace workspace = Mockito.mock(Workspace.class);

		Mockito.doReturn(
			workspaceGitRepository
		).when(
			workspace
		).getPrimaryWorkspaceGitRepository();

		try (MockedStatic<WorkspaceFactory> workspaceFactoryMockedStatic =
				Mockito.mockStatic(WorkspaceFactory.class)) {

			workspaceFactoryMockedStatic.when(
				() -> WorkspaceFactory.newWorkspace(
					Mockito.anyString(), Mockito.anyString(),
					Mockito.anyString())
			).thenReturn(
				workspace
			);

			sourceFormatBuild.getWorkspace();

			if (ownerUpstream) {
				verifyURLReaderAttemptsCount(0, "/compare");
			}
			else {
				_setCompareJSON(ownerCompareJSON, upstreamCompareURL);
				_setCompareJSON(upstreamCompareJSON, ownerCompareURL);
			}

			JenkinsResultsParserUtil.clearCache();

			sourceFormatBuild.getWorkspace();
		}

		String expectedBaseBranchUsername = upstreamUsername;

		if (expectedOwnerBase) {
			expectedBaseBranchUsername = ownerUsername;
		}

		Mockito.verify(
			workspaceGitRepository, Mockito.times(2)
		).setBaseBranchUsername(
			expectedBaseBranchUsername
		);

		InOrder inOrder = Mockito.inOrder(workspaceGitRepository);

		inOrder.verify(
			workspaceGitRepository
		).setBaseBranchUsername(
			expectedBaseBranchUsername
		);

		inOrder.verify(
			workspaceGitRepository
		).setGitHubURL(
			gitHubURL
		);

		if (senderBranchSHAParameter) {
			Mockito.verify(
				workspaceGitRepository, Mockito.times(2)
			).setSenderBranchSHA(
				senderBranchSHA
			);
		}
		else {
			Mockito.verify(
				workspaceGitRepository, Mockito.never()
			).setSenderBranchSHA(
				Mockito.anyString()
			);
		}
	}

	private void _writeIvyXML(String version) throws Exception {
		File ivyXMLFile = new File(
			temporaryFolder.getRoot(),
			"tools/sdk/dependencies/com.liferay.source.formatter/ivy.xml");

		JenkinsResultsParserUtil.write(
			ivyXMLFile,
			JenkinsResultsParserUtil.combine(
				"<ivy-module><dependencies>",
				"<dependency name=\"com.liferay.source.formatter\" rev=\"",
				version, "\" /></dependencies></ivy-module>"));
	}

}