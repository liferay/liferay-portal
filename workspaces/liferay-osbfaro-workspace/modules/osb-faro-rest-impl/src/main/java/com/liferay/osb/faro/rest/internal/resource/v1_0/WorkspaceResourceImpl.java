/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.resource.v1_0;

import com.liferay.osb.faro.constants.FaroUserConstants;
import com.liferay.osb.faro.model.FaroProject;
import com.liferay.osb.faro.rest.dto.v1_0.Workspace;
import com.liferay.osb.faro.rest.resource.v1_0.WorkspaceResource;
import com.liferay.osb.faro.service.FaroProjectLocalService;
import com.liferay.osb.faro.service.FaroUserLocalService;
import com.liferay.portal.vulcan.pagination.Page;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Leslie Wong
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/workspace.properties",
	scope = ServiceScope.PROTOTYPE, service = WorkspaceResource.class
)
public class WorkspaceResourceImpl extends BaseWorkspaceResourceImpl {

	@Override
	public Page<Workspace> getWorkspacesPage() throws Exception {
		return Page.of(
			transform(
				_faroUserLocalService.getFaroUsersByLiveUserId(
					contextUser.getUserId(), FaroUserConstants.STATUS_APPROVED),
				faroUser -> {
					FaroProject faroProject =
						_faroProjectLocalService.fetchFaroProjectByGroupId(
							faroUser.getGroupId());

					if (faroProject == null) {
						return null;
					}

					return new Workspace() {
						{
							setGroupId(
								() -> String.valueOf(faroProject.getGroupId()));
							setName(faroProject::getName);
						}
					};
				}));
	}

	@Reference
	private FaroProjectLocalService _faroProjectLocalService;

	@Reference
	private FaroUserLocalService _faroUserLocalService;

}