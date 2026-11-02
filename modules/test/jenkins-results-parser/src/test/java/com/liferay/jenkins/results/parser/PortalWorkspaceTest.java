/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.Collections;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class PortalWorkspaceTest
	extends com.liferay.jenkins.results.parser.Test {

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray", null);
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			null);
	}

	@Test
	public void testGetBasePortalWorkspaceGitRepository() {
		_setGitDirectoriesJSONArray();

		PortalWorkspaceGitRepository basePortalWorkspaceGitRepository =
			_mockPortalWorkspaceGitRepository("master");

		Map<String, WorkspaceGitRepository> workspaceGitRepositories =
			Collections.singletonMap(
				"liferay-portal", basePortalWorkspaceGitRepository);

		PortalWorkspace portalWorkspace = _newPortalWorkspace(
			workspaceGitRepositories,
			"liferay-portal,liferay-portal-master-private");

		testSame(
			basePortalWorkspaceGitRepository,
			portalWorkspace.getBasePortalWorkspaceGitRepository());

		portalWorkspace = _newPortalWorkspace(
			workspaceGitRepositories, "liferay-portal-master-private");

		Assert.assertNull(
			portalWorkspace.getBasePortalWorkspaceGitRepository());

		Mockito.verify(
			portalWorkspace, Mockito.never()
		).getWorkspaceGitRepository(
			Mockito.anyString()
		);

		portalWorkspace = _newPortalWorkspace(
			Collections.singletonMap(
				"liferay-portal",
				_mockPortalWorkspaceGitRepository(
					RandomTestUtil.randomString())),
			"liferay-portal");

		Assert.assertNull(
			portalWorkspace.getBasePortalWorkspaceGitRepository());

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		Mockito.doReturn(
			"master"
		).when(
			workspaceGitRepository
		).getUpstreamBranchName();

		portalWorkspace = _newPortalWorkspace(
			Collections.singletonMap("liferay-portal", workspaceGitRepository),
			"liferay-portal");

		Assert.assertNull(
			portalWorkspace.getBasePortalWorkspaceGitRepository());
	}

	@Test
	public void testGetPortalWorkspaceGitRepository() {
		String gitDirectoryName = RandomTestUtil.randomString();
		String upstreamBranchName = RandomTestUtil.randomString();

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"branch", upstreamBranchName
				).put(
					"name", gitDirectoryName
				).put(
					"repository", "liferay-portal-ee"
				)
			));

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			new JSONArray());

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		PortalWorkspace portalWorkspace = _getPortalWorkspace(
			new JSONObject(), portalWorkspaceGitRepository);

		testSame(
			portalWorkspaceGitRepository,
			portalWorkspace.getPortalWorkspaceGitRepository());

		_testGetPortalWorkspaceGitRepository(
			"liferay-portal", "master", portalWorkspaceGitRepository);
		_testGetPortalWorkspaceGitRepository(
			gitDirectoryName, upstreamBranchName, portalWorkspaceGitRepository);

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		Mockito.doReturn(
			upstreamBranchName
		).when(
			workspaceGitRepository
		).getUpstreamBranchName();

		_testGetPortalWorkspaceGitRepository(
			gitDirectoryName, null, workspaceGitRepository);

		portalWorkspace = _getPortalWorkspace(
			new JSONObject(
			).put(
				"portal_upstream_branch_name", upstreamBranchName
			),
			workspaceGitRepository);

		RuntimeException runtimeException = Assert.assertThrows(
			RuntimeException.class,
			portalWorkspace::getPortalWorkspaceGitRepository);

		testEquals(
			"The portal workspace Git repository is not set",
			runtimeException.getMessage());
	}

	@Test
	public void testGetPrivatePortalWorkspaceGitRepository() {
		_setGitDirectoriesJSONArray();

		PortalWorkspaceGitRepository privatePortalWorkspaceGitRepository =
			_mockPortalWorkspaceGitRepository("master-private");

		Map<String, WorkspaceGitRepository> workspaceGitRepositories =
			Collections.singletonMap(
				"liferay-portal-master-private",
				privatePortalWorkspaceGitRepository);

		PortalWorkspace portalWorkspace = _newPortalWorkspace(
			workspaceGitRepositories,
			"liferay-portal,liferay-portal-master-private");

		testSame(
			privatePortalWorkspaceGitRepository,
			portalWorkspace.getPrivatePortalWorkspaceGitRepository());

		portalWorkspace = _newPortalWorkspace(
			workspaceGitRepositories, "liferay-portal");

		Assert.assertNull(
			portalWorkspace.getPrivatePortalWorkspaceGitRepository());

		Mockito.verify(
			portalWorkspace, Mockito.never()
		).getWorkspaceGitRepository(
			Mockito.anyString()
		);

		portalWorkspace = _newPortalWorkspace(
			Collections.singletonMap(
				"liferay-portal-master-private",
				_mockPortalWorkspaceGitRepository(
					RandomTestUtil.randomString())),
			"liferay-portal-master-private");

		Assert.assertNull(
			portalWorkspace.getPrivatePortalWorkspaceGitRepository());

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		Mockito.doReturn(
			"master-private"
		).when(
			workspaceGitRepository
		).getUpstreamBranchName();

		portalWorkspace = _newPortalWorkspace(
			Collections.singletonMap(
				"liferay-portal-master-private", workspaceGitRepository),
			"liferay-portal-master-private");

		Assert.assertNull(
			portalWorkspace.getPrivatePortalWorkspaceGitRepository());
	}

	@Test
	public void testGetS3ObjectPathName() {
		PortalWorkspaceGitRepository basePortalWorkspaceGitRepository =
			_mockPortalWorkspaceGitRepository(
				"3333333", "liferay-portal", "4444444", "master");
		PortalWorkspaceGitRepository privatePortalWorkspaceGitRepository =
			_mockPortalWorkspaceGitRepository(
				"1111111", "liferay-portal-master-private", "2222222",
				"master-private");

		PortalWorkspace portalWorkspace = _mockPortalWorkspace(
			basePortalWorkspaceGitRepository, basePortalWorkspaceGitRepository,
			privatePortalWorkspaceGitRepository);

		testEquals(
			"master_3333333_4444444/master-private_1111111_2222222",
			portalWorkspace.getS3ObjectPathName());

		portalWorkspace = _mockPortalWorkspace(
			basePortalWorkspaceGitRepository,
			privatePortalWorkspaceGitRepository,
			privatePortalWorkspaceGitRepository);

		testEquals(
			"master_3333333_4444444/master-private_1111111_2222222",
			portalWorkspace.getS3ObjectPathName());

		portalWorkspace = _mockPortalWorkspace(
			basePortalWorkspaceGitRepository,
			_mockPortalWorkspaceGitRepository(
				"5555555", "liferay-portal-7.0.x", "6666666", "7.0.x"),
			privatePortalWorkspaceGitRepository);

		testEquals(
			"7.0.x_5555555_6666666", portalWorkspace.getS3ObjectPathName());

		portalWorkspace = _mockPortalWorkspace(
			basePortalWorkspaceGitRepository, basePortalWorkspaceGitRepository,
			null);

		testEquals(
			"master_3333333_4444444", portalWorkspace.getS3ObjectPathName());

		portalWorkspace = _mockPortalWorkspace(
			null, privatePortalWorkspaceGitRepository,
			privatePortalWorkspaceGitRepository);

		testEquals(
			"master-private_1111111_2222222",
			portalWorkspace.getS3ObjectPathName());
	}

	@Test
	public void testSetBasePortalWorkspaceGitRepository() {
		PortalWorkspaceGitRepository basePortalWorkspaceGitRepository =
			_mockPortalWorkspaceGitRepository("liferay-portal", "master");
		PortalWorkspaceGitRepository privatePortalWorkspaceGitRepository =
			_mockPortalWorkspaceGitRepository(
				"liferay-portal-master-private", "master-private");

		_testSetBasePortalWorkspaceGitRepository(
			basePortalWorkspaceGitRepository,
			privatePortalWorkspaceGitRepository,
			privatePortalWorkspaceGitRepository);

		Mockito.verify(
			privatePortalWorkspaceGitRepository
		).setBasePortalWorkspaceGitRepository(
			basePortalWorkspaceGitRepository
		);

		Mockito.verify(
			privatePortalWorkspaceGitRepository
		).setPrimaryPortalBranchPrivate(
			true
		);

		privatePortalWorkspaceGitRepository = _mockPortalWorkspaceGitRepository(
			"liferay-portal-master-private", "master-private");

		_testSetBasePortalWorkspaceGitRepository(
			basePortalWorkspaceGitRepository, basePortalWorkspaceGitRepository,
			privatePortalWorkspaceGitRepository);

		Mockito.verify(
			privatePortalWorkspaceGitRepository
		).setBasePortalWorkspaceGitRepository(
			basePortalWorkspaceGitRepository
		);

		Mockito.verify(
			privatePortalWorkspaceGitRepository
		).setPrimaryPortalBranchPrivate(
			false
		);

		privatePortalWorkspaceGitRepository = Mockito.mock(
			PortalWorkspaceGitRepository.class);

		_testSetBasePortalWorkspaceGitRepository(
			null, privatePortalWorkspaceGitRepository,
			privatePortalWorkspaceGitRepository);

		Mockito.verifyNoMoreInteractions(privatePortalWorkspaceGitRepository);

		basePortalWorkspaceGitRepository = Mockito.mock(
			PortalWorkspaceGitRepository.class);

		_testSetBasePortalWorkspaceGitRepository(
			basePortalWorkspaceGitRepository, basePortalWorkspaceGitRepository,
			null);

		Mockito.verifyNoMoreInteractions(basePortalWorkspaceGitRepository);
	}

	@Test
	public void testSetBuildProfile() {
		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).getBuildProfile();

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).setBuildProfile(
			Mockito.any()
		);

		ReflectionTestUtil.setFieldValue(
			portalWorkspace, "jsonObject", new JSONObject());

		Mockito.verify(
			portalWorkspace, Mockito.never()
		).updateBuildDatabase();

		portalWorkspace.setBuildProfile(Job.BuildProfile.PORTAL);

		Mockito.verify(
			portalWorkspace
		).updateBuildDatabase();

		testSame(Job.BuildProfile.PORTAL, portalWorkspace.getBuildProfile());

		portalWorkspace.setBuildProfile(Job.BuildProfile.PORTAL);

		Mockito.verify(
			portalWorkspace
		).updateBuildDatabase();

		portalWorkspace.setBuildProfile(Job.BuildProfile.DXP);

		Mockito.verify(
			portalWorkspace, Mockito.times(2)
		).updateBuildDatabase();
	}

	@Test
	public void testSetPortalUpstreamBranchName() {
		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).getJSONObject();

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).setPortalUpstreamBranchName(
			Mockito.any()
		);

		ReflectionTestUtil.setFieldValue(
			portalWorkspace, "jsonObject", new JSONObject());

		String portalUpstreamBranchName = RandomTestUtil.randomString();

		Mockito.verify(
			portalWorkspace, Mockito.never()
		).updateBuildDatabase();

		portalWorkspace.setPortalUpstreamBranchName(portalUpstreamBranchName);

		Mockito.verify(
			portalWorkspace
		).updateBuildDatabase();

		JSONObject portalWorkspaceJSONObject = portalWorkspace.getJSONObject();

		testEquals(
			portalUpstreamBranchName,
			portalWorkspaceJSONObject.get("portal_upstream_branch_name"));

		portalWorkspace.setPortalUpstreamBranchName(portalUpstreamBranchName);

		Mockito.verify(
			portalWorkspace
		).updateBuildDatabase();

		portalWorkspace.setPortalUpstreamBranchName(null);

		Assert.assertFalse(
			portalWorkspaceJSONObject.has("portal_upstream_branch_name"));

		Mockito.verify(
			portalWorkspace, Mockito.times(2)
		).updateBuildDatabase();
	}

	private PortalWorkspace _getPortalWorkspace(
		JSONObject jsonObject, WorkspaceGitRepository workspaceGitRepository) {

		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).getPortalWorkspaceGitRepository();

		Mockito.doReturn(
			workspaceGitRepository
		).when(
			portalWorkspace
		).getPrimaryWorkspaceGitRepository();

		ReflectionTestUtil.setFieldValue(
			portalWorkspace, "jsonObject", jsonObject);

		return portalWorkspace;
	}

	private PortalWorkspace _mockPortalWorkspace(
		PortalWorkspaceGitRepository basePortalWorkspaceGitRepository,
		PortalWorkspaceGitRepository portalWorkspaceGitRepository,
		PortalWorkspaceGitRepository privatePortalWorkspaceGitRepository) {

		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).getS3ObjectPathName();

		Mockito.doReturn(
			basePortalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getBasePortalWorkspaceGitRepository();

		Mockito.doReturn(
			portalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getPortalWorkspaceGitRepository();

		Mockito.doReturn(
			privatePortalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getPrivatePortalWorkspaceGitRepository();

		return portalWorkspace;
	}

	private PortalWorkspaceGitRepository _mockPortalWorkspaceGitRepository(
		String upstreamBranchName) {

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		Mockito.doReturn(
			upstreamBranchName
		).when(
			portalWorkspaceGitRepository
		).getUpstreamBranchName();

		return portalWorkspaceGitRepository;
	}

	private PortalWorkspaceGitRepository _mockPortalWorkspaceGitRepository(
		String directoryName, String upstreamBranchName) {

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_mockPortalWorkspaceGitRepository(upstreamBranchName);

		Mockito.doReturn(
			directoryName
		).when(
			portalWorkspaceGitRepository
		).getDirectoryName();

		return portalWorkspaceGitRepository;
	}

	private PortalWorkspaceGitRepository _mockPortalWorkspaceGitRepository(
		String baseBranchSHAShort, String directoryName,
		String senderBranchSHAShort, String upstreamBranchName) {

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_mockPortalWorkspaceGitRepository(
				directoryName, upstreamBranchName);

		Mockito.doReturn(
			baseBranchSHAShort
		).when(
			portalWorkspaceGitRepository
		).getBaseBranchSHAShort();

		Mockito.doReturn(
			senderBranchSHAShort
		).when(
			portalWorkspaceGitRepository
		).getSenderBranchSHAShort();

		return portalWorkspaceGitRepository;
	}

	private PortalWorkspace _newPortalWorkspace(
		Map<String, WorkspaceGitRepository> workspaceGitRepositories,
		String workspaceRepositoryDirNames) {

		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).getBasePortalWorkspaceGitRepository();

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).getPrivatePortalWorkspaceGitRepository();

		Mockito.doCallRealMethod(
		).when(
			portalWorkspace
		).getWorkspaceRepositoryDirNames();

		for (Map.Entry<String, WorkspaceGitRepository> entry :
				workspaceGitRepositories.entrySet()) {

			Mockito.doReturn(
				entry.getValue()
			).when(
				portalWorkspace
			).getWorkspaceGitRepository(
				entry.getKey()
			);
		}

		ReflectionTestUtil.setFieldValue(
			portalWorkspace, "jsonObject",
			new JSONObject(
			).put(
				"workspace_repository_dir_names", workspaceRepositoryDirNames
			));

		return portalWorkspace;
	}

	private void _setGitDirectoriesJSONArray() {
		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitDirectoriesJSONArray",
			new JSONArray(
			).put(
				new JSONObject(
				).put(
					"branch", "master"
				).put(
					"name", "liferay-portal"
				).put(
					"repository", "liferay-portal"
				)
			).put(
				new JSONObject(
				).put(
					"branch", "master-private"
				).put(
					"name", "liferay-portal-master-private"
				).put(
					"repository", "liferay-portal-ee"
				)
			));

		ReflectionTestUtil.setFieldValue(
			JenkinsResultsParserUtil.class, "_gitWorkingDirectoriesJSONArray",
			new JSONArray());
	}

	private void _testGetPortalWorkspaceGitRepository(
		String gitDirectoryName, String portalUpstreamBranchName,
		WorkspaceGitRepository workspaceGitRepository) {

		PortalWorkspace portalWorkspace = _getPortalWorkspace(
			new JSONObject(
			).put(
				"portal_upstream_branch_name", portalUpstreamBranchName
			),
			workspaceGitRepository);

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		Mockito.doReturn(
			portalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getWorkspaceGitRepository(
			gitDirectoryName
		);

		testSame(
			portalWorkspaceGitRepository,
			portalWorkspace.getPortalWorkspaceGitRepository());
	}

	private void _testSetBasePortalWorkspaceGitRepository(
		PortalWorkspaceGitRepository basePortalWorkspaceGitRepository,
		WorkspaceGitRepository primaryWorkspaceGitRepository,
		PortalWorkspaceGitRepository privatePortalWorkspaceGitRepository) {

		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);

		Mockito.doReturn(
			basePortalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getBasePortalWorkspaceGitRepository();

		Mockito.doReturn(
			primaryWorkspaceGitRepository
		).when(
			portalWorkspace
		).getPrimaryWorkspaceGitRepository();

		Mockito.doReturn(
			privatePortalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getPrivatePortalWorkspaceGitRepository();

		ReflectionTestUtil.invoke(
			portalWorkspace, "_setBasePortalWorkspaceGitRepository",
			new Class<?>[0]);
	}

}