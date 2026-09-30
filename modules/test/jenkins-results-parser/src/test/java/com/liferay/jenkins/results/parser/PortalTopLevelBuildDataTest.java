/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class PortalTopLevelBuildDataTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetPortalRemoteGitRef() {
		String portalGitHubURL =
			"https://github.com/liferay/liferay-portal/tree/" +
				RandomTestUtil.randomString();

		PortalTopLevelBuildData portalTopLevelBuildData = Mockito.mock(
			PortalTopLevelBuildData.class);

		Mockito.doCallRealMethod(
		).when(
			portalTopLevelBuildData
		).getPortalRemoteGitRef();

		Mockito.doCallRealMethod(
		).when(
			portalTopLevelBuildData
		).setPortalRemoteGitRef(
			Mockito.any()
		);

		Mockito.doReturn(
			portalGitHubURL
		).when(
			portalTopLevelBuildData
		).getPortalGitHubURL();

		RemoteGitRef lookedUpRemoteGitRef = Mockito.mock(RemoteGitRef.class);
		RemoteGitRef storedRemoteGitRef = Mockito.mock(RemoteGitRef.class);

		String sha = RandomTestUtil.randomSHA();

		Mockito.doReturn(
			sha
		).when(
			storedRemoteGitRef
		).getSHA();

		try (MockedStatic<GitUtil> gitUtilMockedStatic = Mockito.mockStatic(
				GitUtil.class)) {

			gitUtilMockedStatic.when(
				() -> GitUtil.getRemoteGitRef(portalGitHubURL)
			).thenReturn(
				lookedUpRemoteGitRef
			);

			testSame(
				lookedUpRemoteGitRef,
				portalTopLevelBuildData.getPortalRemoteGitRef());
			testSame(
				lookedUpRemoteGitRef,
				portalTopLevelBuildData.getPortalRemoteGitRef());

			portalTopLevelBuildData.setPortalRemoteGitRef(storedRemoteGitRef);

			testSame(
				storedRemoteGitRef,
				portalTopLevelBuildData.getPortalRemoteGitRef());

			gitUtilMockedStatic.verify(
				() -> GitUtil.getRemoteGitRef(portalGitHubURL));
		}

		Mockito.verify(
			portalTopLevelBuildData
		).setPortalBranchSHA(
			sha
		);
	}

}