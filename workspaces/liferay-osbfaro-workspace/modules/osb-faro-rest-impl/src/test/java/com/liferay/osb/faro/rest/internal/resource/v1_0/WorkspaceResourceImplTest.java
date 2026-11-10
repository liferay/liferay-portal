/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.model.FaroUser;
import com.liferay.osb.faro.rest.dto.v1_0.Workspace;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.vulcan.pagination.Page;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Marcos Martins
 */
public class WorkspaceResourceImplTest {

	@Test
	public void testGetWorkspacesPage() throws Exception {
		User user = Mockito.mock(User.class);

		Mockito.when(
			user.getUserId()
		).thenReturn(
			_USER_ID
		);

		FaroUser faroUser = Mockito.mock(FaroUser.class);

		Mockito.when(
			faroUser.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		Mockito.when(
			_faroUserLocalService.getFaroUsersByLiveUserId(
				_USER_ID, FaroUserConstants.STATUS_APPROVED)
		).thenReturn(
			Collections.singletonList(faroUser)
		);

		FaroProject faroProject = Mockito.mock(FaroProject.class);

		Mockito.when(
			faroProject.getGroupId()
		).thenReturn(
			_GROUP_ID
		);

		String name = RandomTestUtil.randomString();

		Mockito.when(
			faroProject.getName()
		).thenReturn(
			name
		);

		Mockito.when(
			_faroProjectLocalService.fetchFaroProjectByGroupId(_GROUP_ID)
		).thenReturn(
			faroProject
		);

		_workspaceResourceImpl.setContextUser(user);

		ReflectionTestUtil.setFieldValue(
			_workspaceResourceImpl, "_faroProjectLocalService",
			_faroProjectLocalService);
		ReflectionTestUtil.setFieldValue(
			_workspaceResourceImpl, "_faroUserLocalService",
			_faroUserLocalService);

		Page<Workspace> workspacesPage =
			_workspaceResourceImpl.getWorkspacesPage();

		List<Workspace> workspaces = (List<Workspace>)workspacesPage.getItems();

		Assert.assertEquals(workspaces.toString(), 1, workspaces.size());

		Workspace workspace = workspaces.get(0);

		Assert.assertEquals(String.valueOf(_GROUP_ID), workspace.getGroupId());
		Assert.assertEquals(name, workspace.getName());
	}

	private static final long _GROUP_ID = RandomTestUtil.randomLong();

	private static final long _USER_ID = RandomTestUtil.randomLong();

	private final FaroProjectLocalService _faroProjectLocalService =
		Mockito.mock(FaroProjectLocalService.class);
	private final FaroUserLocalService _faroUserLocalService = Mockito.mock(
		FaroUserLocalService.class);
	private final WorkspaceResourceImpl _workspaceResourceImpl =
		new WorkspaceResourceImpl();

}