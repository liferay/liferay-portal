/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.Workspace;
import com.liferay.jenkins.results.parser.WorkspaceGitRepository;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Brittney Nguyen
 */
public class WorkspaceBundlePersistentResourceTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetArtifacts() {
		for (String workspaceName : _getWorkspaceNames()) {
			List<String> artifactNames = new ArrayList<>();

			WorkspaceBundlePersistentResource
				workspaceBundlePersistentResource =
					new WorkspaceBundlePersistentResource(
						Mockito.mock(BuildDatabase.class), null, workspaceName);

			for (PersistentResource.Artifact artifact :
					workspaceBundlePersistentResource.getArtifacts()) {

				artifactNames.add(artifact.getName());
			}

			testEquals(
				Collections.singletonList(
					"liferay-docker-" + workspaceName + ".tar"),
				artifactNames);
		}
	}

	@Test
	public void testGetAxisVariable() {
		for (String workspaceName : _getWorkspaceNames()) {
			WorkspaceBundlePersistentResource
				workspaceBundlePersistentResource =
					new WorkspaceBundlePersistentResource(
						Mockito.mock(BuildDatabase.class), null, workspaceName);

			String axisVariable =
				workspaceBundlePersistentResource.getAxisVariable();

			Matcher matcher = _axisVariablePattern.matcher(axisVariable);

			Assert.assertTrue(axisVariable, matcher.matches());

			testEquals(workspaceName, matcher.group(1));
		}
	}

	@Test
	public void testGetBaseS3ObjectPath() {
		Properties buildProperties = new Properties();

		String archivesPath = RandomTestUtil.randomString();

		buildProperties.setProperty(
			"cloud.ci.s3.bucket.persistent.resources.archives.path",
			archivesPath);

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		String baseBranchSHA = RandomTestUtil.randomSHA();

		Mockito.doReturn(
			baseBranchSHA
		).when(
			workspaceGitRepository
		).getBaseBranchSHA();

		String name = RandomTestUtil.randomString();

		Mockito.doReturn(
			name
		).when(
			workspaceGitRepository
		).getName();

		String senderBranchSHA = RandomTestUtil.randomSHA();

		Mockito.doReturn(
			senderBranchSHA
		).when(
			workspaceGitRepository
		).getSenderBranchSHA();

		Workspace workspace = _mockWorkspace(null, workspaceGitRepository);

		_testGetBaseS3ObjectPath(
			archivesPath, baseBranchSHA, name, senderBranchSHA, workspace,
			RandomTestUtil.randomString());
		_testGetBaseS3ObjectPath(
			archivesPath, baseBranchSHA, name, senderBranchSHA, workspace,
			RandomTestUtil.randomString());
	}

	@Test
	public void testGetBundleWorkspaceGitRepository() throws Exception {
		String workspaceName = RandomTestUtil.randomString();

		WorkspaceGitRepository portalPrivateWorkspaceGitRepository =
			_mockWorkspaceGitRepository();
		WorkspaceGitRepository primaryWorkspaceGitRepository =
			_mockWorkspaceGitRepository();

		File portalPrivateWorkspacesDir = new File(
			portalPrivateWorkspaceGitRepository.getDirectory(), "workspaces");
		File primaryWorkspacesDir = new File(
			primaryWorkspaceGitRepository.getDirectory(), "workspaces");

		_testGetBundleWorkspaceGitRepository(
			primaryWorkspaceGitRepository,
			_mockWorkspace(null, primaryWorkspaceGitRepository), workspaceName);
		_testGetBundleWorkspaceGitRepository(
			primaryWorkspaceGitRepository,
			_mockWorkspace(
				portalPrivateWorkspaceGitRepository,
				primaryWorkspaceGitRepository),
			workspaceName);

		portalPrivateWorkspacesDir.mkdirs();

		File portalPrivateWorkspaceFile = new File(
			portalPrivateWorkspacesDir, workspaceName);

		portalPrivateWorkspaceFile.createNewFile();

		_testGetBundleWorkspaceGitRepository(
			primaryWorkspaceGitRepository,
			_mockWorkspace(
				portalPrivateWorkspaceGitRepository,
				primaryWorkspaceGitRepository),
			workspaceName);

		portalPrivateWorkspaceFile.delete();

		File portalPrivateWorkspaceDir = new File(
			portalPrivateWorkspacesDir, workspaceName);

		portalPrivateWorkspaceDir.mkdir();

		_testGetBundleWorkspaceGitRepository(
			portalPrivateWorkspaceGitRepository,
			_mockWorkspace(
				portalPrivateWorkspaceGitRepository,
				primaryWorkspaceGitRepository),
			workspaceName);

		File primaryWorkspaceDir = new File(
			primaryWorkspacesDir, workspaceName);

		primaryWorkspaceDir.mkdirs();

		_testGetBundleWorkspaceGitRepository(
			portalPrivateWorkspaceGitRepository,
			_mockWorkspace(
				portalPrivateWorkspaceGitRepository,
				primaryWorkspaceGitRepository),
			workspaceName);
	}

	@Test
	public void testGetType() {
		WorkspaceBundlePersistentResource workspaceBundlePersistentResource =
			new WorkspaceBundlePersistentResource(
				Mockito.mock(BuildDatabase.class), null,
				RandomTestUtil.randomString());

		PersistentResource.Type type =
			workspaceBundlePersistentResource.getType();

		testSame(
			PersistentResource.Type.WORKSPACE_BUNDLE,
			PersistentResource.Type.get(String.valueOf(type)));
		testEquals("workspace-bundle", String.valueOf(type));
	}

	@Test
	public void testWorkspaceBundlePersistentResource() {
		_testWorkspaceBundlePersistentResource("");
		_testWorkspaceBundlePersistentResource(null);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private List<String> _getWorkspaceNames() {
		List<String> workspaceNames = new ArrayList<>();

		File portalDir = JenkinsResultsParserUtil.getCanonicalFile(
			new File("."));

		File portalImplDir = new File(portalDir, "portal-impl");

		while (!portalImplDir.exists()) {
			portalDir = portalDir.getParentFile();

			portalImplDir = new File(portalDir, "portal-impl");
		}

		File workspacesDir = new File(portalDir, "workspaces");

		for (File workspaceDir : workspacesDir.listFiles()) {
			if (!workspaceDir.isDirectory()) {
				continue;
			}

			workspaceNames.add(workspaceDir.getName());
		}

		Assert.assertFalse(workspaceNames.isEmpty());

		return workspaceNames;
	}

	private Workspace _mockWorkspace(
		WorkspaceGitRepository portalPrivateWorkspaceGitRepository,
		WorkspaceGitRepository primaryWorkspaceGitRepository) {

		Workspace workspace = Mockito.mock(Workspace.class);

		Mockito.doReturn(
			primaryWorkspaceGitRepository
		).when(
			workspace
		).getPrimaryWorkspaceGitRepository();

		Mockito.doReturn(
			portalPrivateWorkspaceGitRepository
		).when(
			workspace
		).getWorkspaceGitRepository(
			"liferay-portal-master-private"
		);

		return workspace;
	}

	private WorkspaceGitRepository _mockWorkspaceGitRepository()
		throws Exception {

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		Mockito.doReturn(
			temporaryFolder.newFolder()
		).when(
			workspaceGitRepository
		).getDirectory();

		return workspaceGitRepository;
	}

	private WorkspaceBundlePersistentResource
		_newWorkspaceBundlePersistentResource(
			Workspace workspace, String workspaceName) {

		WorkspaceBundlePersistentResource workspaceBundlePersistentResource =
			Mockito.spy(
				new WorkspaceBundlePersistentResource(
					Mockito.mock(BuildDatabase.class), null, workspaceName));

		Mockito.doReturn(
			workspace
		).when(
			workspaceBundlePersistentResource
		).getWorkspace();

		return workspaceBundlePersistentResource;
	}

	private void _testGetBaseS3ObjectPath(
		String archivesPath, String baseBranchSHA, String name,
		String senderBranchSHA, Workspace workspace, String workspaceName) {

		WorkspaceBundlePersistentResource workspaceBundlePersistentResource =
			_newWorkspaceBundlePersistentResource(workspace, workspaceName);

		testEquals(
			JenkinsResultsParserUtil.combine(
				archivesPath, "/workspace-bundle/", workspaceName, "/", name,
				"/", baseBranchSHA, "/", senderBranchSHA),
			workspaceBundlePersistentResource.getBaseS3ObjectPath());
	}

	private void _testGetBundleWorkspaceGitRepository(
		WorkspaceGitRepository expectedWorkspaceGitRepository,
		Workspace workspace, String workspaceName) {

		WorkspaceBundlePersistentResource workspaceBundlePersistentResource =
			_newWorkspaceBundlePersistentResource(workspace, workspaceName);

		testSame(
			expectedWorkspaceGitRepository,
			workspaceBundlePersistentResource.
				getBundleWorkspaceGitRepository());
	}

	private void _testWorkspaceBundlePersistentResource(String workspaceName) {
		try {
			new WorkspaceBundlePersistentResource(
				Mockito.mock(BuildDatabase.class), null, workspaceName);

			Assert.fail();
		}
		catch (IllegalArgumentException illegalArgumentException) {
			testEquals(
				"Workspace name is null or empty",
				illegalArgumentException.getMessage());
		}
	}

	private static final Pattern _axisVariablePattern = Pattern.compile(
		"^workspace-bundle-(.+)$");

}