/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.faro.constants.FaroProjectConstants;
import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.client.dto.v1_0.Workspace;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;

import java.util.Collections;

import org.junit.runner.RunWith;

/**
 * @author Leslie Wong
 */
@RunWith(Arquillian.class)
public class WorkspaceResourceTest extends BaseWorkspaceResourceTestCase {

	@Override
	protected Workspace testGetWorkspacesPage_addWorkspace(Workspace workspace)
		throws Exception {

		User user = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		FaroProject faroProject = _faroProjectLocalService.addFaroProject(
			user.getUserId(), workspace.getName(), null, null, null, null,
			Collections.emptyList(), null, null, null, null,
			FaroProjectConstants.STATE_READY, "{}", "UTC", null);

		Role role = _roleLocalService.getRole(
			user.getCompanyId(), RoleConstants.SITE_OWNER);

		_faroUserLocalService.addFaroUser(
			user.getUserId(), faroProject.getGroupId(), user.getUserId(),
			role.getRoleId(), user.getEmailAddress(),
			FaroUserConstants.STATUS_APPROVED, false);

		return new Workspace() {
			{
				setGroupId(faroProject.getGroupId());
				setName(faroProject.getName());
			}
		};
	}

	@Inject
	private FaroProjectLocalService _faroProjectLocalService;

	@Inject
	private FaroUserLocalService _faroUserLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

}