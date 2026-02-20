/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cmp.site.initializer.internal.bulk.selection;

import com.liferay.bulk.selection.BulkSelectionAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.rest.dto.v1_0.Assignee;
import com.liferay.object.service.ObjectEntryService;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.site.cms.site.initializer.bulk.selection.BaseObjectBulkSelectionAction;

import java.io.Serializable;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Sbarra
 */
@Component(
	property = "bulk.selection.action.key=assign.to.object",
	service = BulkSelectionAction.class
)
public class AssignToObjectBulkSelectionAction
	extends BaseObjectBulkSelectionAction {

	@Override
	protected void doExecute(
			User user, Map<String, Serializable> inputMap, Object object)
		throws Exception {

		if (!(object instanceof ObjectEntry)) {
			return;
		}

		ObjectEntry objectObjectEntry = (ObjectEntry)object;

		ObjectDefinition objectDefinition =
			objectDefinitionLocalService.getObjectDefinition(
				objectObjectEntry.getObjectDefinitionId());

		if (StringUtil.equals(objectDefinition.getName(), "CMPTask")) {
			Map<String, Serializable> properties = new HashMap<>();

			if (StringUtil.equalsIgnoreCase(
					(String)inputMap.get("type"),
					Assignee.Type.ROLE.toString())) {

				Role role = _roleService.getRoleByExternalReferenceCode(
					(String)inputMap.get("externalReferenceCode"),
					objectObjectEntry.getCompanyId());

				properties.put(
					"assignTo",
					HashMapBuilder.put(
						"classNameId", role.getClassNameId()
					).put(
						"classPK", role.getClassPK()
					).build());
			}
			else if (StringUtil.equalsIgnoreCase(
						(String)inputMap.get("type"),
						Assignee.Type.USER.toString())) {

				properties.put(
					"assignTo",
					HashMapBuilder.put(
						"classNameId",
						_portal.getClassNameId(User.class.getName())
					).put(
						"classPK",
						() -> {
							User assigneeUser =
								_userService.getUserByExternalReferenceCode(
									(String)inputMap.get(
										"externalReferenceCode"),
									objectObjectEntry.getCompanyId());

							return assigneeUser.getUserId();
						}
					).build());
			}
			else {
				properties.put(
					"assignTo",
					HashMapBuilder.put(
						"classNameId", Long.valueOf(0)
					).put(
						"classPK", Long.valueOf(0)
					).build());
			}

			_objectEntryService.partialUpdateObjectEntry(
				objectObjectEntry.getObjectEntryId(),
				objectObjectEntry.getObjectEntryFolderId(), properties,
				new ServiceContext());
		}
	}

	@Reference
	private ObjectEntryService _objectEntryService;

	@Reference
	private Portal _portal;

	@Reference
	private RoleService _roleService;

	@Reference
	private UserService _userService;

}