/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.internal.resource.v1_0;

import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.headless.cmp.dto.v1_0.TaskAssignee;
import com.liferay.headless.cmp.resource.v1_0.TaskAssigneeResource;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.comparator.UserFirstNameComparator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.site.cms.site.initializer.users.provider.CMSUsersProvider;

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
	public Page<TaskAssignee> getProjectTaskAssigneesPage(
			Long projectId, String search, String type)
		throws Exception {

		return _getTaskAssigneesPage(
			_objectEntryService.getObjectEntry(projectId), search, type);
	}

	@Override
	public Page<TaskAssignee> getTaskAssigneesPage(String search, String type) {
		return _getTaskAssigneesPage(null, search, type);
	}

	private Page<TaskAssignee> _getTaskAssigneesPage(
		ObjectEntry objectEntry, String search, String type) {

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

			List<User> users = null;

			if (objectEntry == null) {
				users = _cmsUsersProvider.getUsers(search, 0, 20);
			}
			else {
				users = _userLocalService.search(
					contextCompany.getCompanyId(), search,
					WorkflowConstants.STATUS_APPROVED,
					LinkedHashMapBuilder.<String, Object>put(
						"inherit", Boolean.TRUE
					).put(
						"usersGroups", objectEntry.getGroupId()
					).build(),
					0, 20, UserFirstNameComparator.getInstance(true));
			}

			taskAssignees.addAll(
				transform(
					users,
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
	private CMSUsersProvider _cmsUsersProvider;

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private RoleService _roleService;

	@Reference
	private UserLocalService _userLocalService;

}