/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.persistent.resource;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.PortalWorkspace;
import com.liferay.jenkins.results.parser.PortalWorkspaceGitRepository;
import com.liferay.jenkins.results.parser.RandomTestUtil;
import com.liferay.jenkins.results.parser.SubrepositoryWorkspace;
import com.liferay.jenkins.results.parser.Workspace;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class PortalBundlePersistentResourceTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetBaseS3ObjectPath() {
		Properties buildProperties = new Properties();

		String archivesPath = RandomTestUtil.randomString();

		buildProperties.setProperty(
			"cloud.ci.s3.bucket.persistent.resources.archives.path",
			archivesPath);

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		String s3ObjectPathName = RandomTestUtil.randomString();

		Mockito.doReturn(
			s3ObjectPathName
		).when(
			portalWorkspace
		).getS3ObjectPathName();

		PortalBundlePersistentResource portalBundlePersistentResource =
			_newPortalBundlePersistentResource(portalWorkspace);

		testEquals(
			JenkinsResultsParserUtil.combine(
				archivesPath, "/portal-bundle/", s3ObjectPathName),
			portalBundlePersistentResource.getBaseS3ObjectPath());
	}

	@Test
	public void testGetBundleWorkspaceGitRepository() {
		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		Mockito.doReturn(
			portalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getPortalWorkspaceGitRepository();

		PortalBundlePersistentResource portalBundlePersistentResource =
			_newPortalBundlePersistentResource(portalWorkspace);

		testSame(
			portalWorkspaceGitRepository,
			portalBundlePersistentResource.getBundleWorkspaceGitRepository());

		SubrepositoryWorkspace subrepositoryWorkspace = Mockito.mock(
			SubrepositoryWorkspace.class);

		Mockito.doReturn(
			portalWorkspaceGitRepository
		).when(
			subrepositoryWorkspace
		).getPortalWorkspaceGitRepository();

		portalBundlePersistentResource = _newPortalBundlePersistentResource(
			subrepositoryWorkspace);

		testSame(
			portalWorkspaceGitRepository,
			portalBundlePersistentResource.getBundleWorkspaceGitRepository());

		Mockito.verify(
			subrepositoryWorkspace, Mockito.never()
		).getWorkspaceGitRepository(
			"liferay-portal"
		);

		_testGetBundleWorkspaceGitRepositoryThrows(
			Mockito.mock(Workspace.class));
		_testGetBundleWorkspaceGitRepositoryThrows(null);
	}

	private PortalBundlePersistentResource _newPortalBundlePersistentResource(
		Workspace workspace) {

		PortalBundlePersistentResource portalBundlePersistentResource =
			Mockito.mock(PortalBundlePersistentResource.class);

		Mockito.doCallRealMethod(
		).when(
			portalBundlePersistentResource
		).getBaseS3ObjectPath();

		Mockito.doCallRealMethod(
		).when(
			portalBundlePersistentResource
		).getBundleWorkspaceGitRepository();

		Mockito.doCallRealMethod(
		).when(
			portalBundlePersistentResource
		).getS3ObjectPathName();

		Mockito.doCallRealMethod(
		).when(
			portalBundlePersistentResource
		).getType();

		Mockito.doReturn(
			workspace
		).when(
			portalBundlePersistentResource
		).getWorkspace();

		return portalBundlePersistentResource;
	}

	private void _testGetBundleWorkspaceGitRepositoryThrows(
		Workspace workspace) {

		PortalBundlePersistentResource portalBundlePersistentResource =
			_newPortalBundlePersistentResource(workspace);

		RuntimeException runtimeException = Assert.assertThrows(
			RuntimeException.class,
			portalBundlePersistentResource::getBundleWorkspaceGitRepository);

		testEquals(
			"The portal workspace is not set", runtimeException.getMessage());
	}

}