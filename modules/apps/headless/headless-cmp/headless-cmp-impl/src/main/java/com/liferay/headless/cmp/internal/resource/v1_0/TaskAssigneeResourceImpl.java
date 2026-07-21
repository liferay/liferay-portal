/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.internal.resource.v1_0;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.headless.cmp.dto.v1_0.TaskAssignee;
import com.liferay.headless.cmp.resource.v1_0.TaskAssigneeResource;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.site.cms.site.initializer.util.CMSUserUtil;

import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Carolina Barbosa
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/task-assignee.properties",
	scope = ServiceScope.PROTOTYPE, service = TaskAssigneeResource.class
)
public class TaskAssigneeResourceImpl extends BaseTaskAssigneeResourceImpl {

	@Override
	public Page<TaskAssignee> getTaskAssigneesPage(String search, String type) {
		List<TaskAssignee> taskAssignees = new ArrayList<>();

		if (Validator.isNull(type) ||
			StringUtil.equalsIgnoreCase(type, "Role")) {

			taskAssignees.addAll(
				transform(
					_roleService.search(
						contextCompany.getCompanyId(), search,
						new Integer[] {RoleConstants.TYPE_DEPOT}, null, 0, 20,
						null),
					role -> {
						if (!StringUtil.equals(
								role.getSubtype(),
								DepotRolesConstants.SUBTYPE_PROJECT)) {

							return null;
						}

						return new TaskAssignee() {
							{
								setExternalReferenceCode(
									role::getExternalReferenceCode);
								setId(role::getRoleId);
								setName(role::getName);
								setType(() -> "Role");
							}
						};
					}));
		}

		if (Validator.isNull(type) ||
			StringUtil.equalsIgnoreCase(type, "User")) {

			taskAssignees.addAll(
				transform(
					CMSUserUtil.getUsers(search, 0, 20),
					user -> new TaskAssignee() {
						{
							setExternalReferenceCode(
								user::getExternalReferenceCode);
							setId(user::getUserId);
							setName(user::getFullName);
							setPortrait(
								() -> {
									if (user.getPortraitId() == 0) {
										return null;
									}

									return user.getPortraitURL(
										new ThemeDisplay() {
											{
												setPathImage(
													_portal.getPathImage());
											}
										});
								});
							setType(() -> "User");
						}
					}));
		}

		return Page.of(taskAssignees);
	}

	@Reference
	private Portal _portal;

	@Reference
	private RoleService _roleService;

}