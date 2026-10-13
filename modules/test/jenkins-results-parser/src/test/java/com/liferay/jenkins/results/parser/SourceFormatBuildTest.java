/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

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
		String gitHubURL = RandomTestUtil.randomString();

		PullRequest pullRequest = Mockito.mock(PullRequest.class);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			pullRequest
		).getGitRepositoryName();

		Mockito.doReturn(
			gitHubURL
		).when(
			pullRequest
		).getHtmlURL();

		Mockito.doReturn(
			RandomTestUtil.randomString()
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
		}

		InOrder inOrder = Mockito.inOrder(workspaceGitRepository);

		inOrder.verify(
			workspaceGitRepository
		).setUsePullRequestBaseRepository(
			true
		);

		inOrder.verify(
			workspaceGitRepository
		).setGitHubURL(
			gitHubURL
		);
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