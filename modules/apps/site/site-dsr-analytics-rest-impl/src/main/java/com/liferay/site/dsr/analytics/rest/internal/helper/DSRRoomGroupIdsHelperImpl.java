/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.dsr.analytics.rest.internal.helper;

import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.site.dsr.analytics.rest.helper.DSRRoomGroupIdsHelper;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michele Vigilante
 */
@Component(service = DSRRoomGroupIdsHelper.class)
public class DSRRoomGroupIdsHelperImpl implements DSRRoomGroupIdsHelper {

	@Override
	public String[] filterVisibleGroupIds(String[] groupIds)
		throws PortalException {

		Set<String> visibleGroupIds = _getVisibleGroupIds();

		if (ArrayUtil.isEmpty(groupIds)) {
			return visibleGroupIds.toArray(new String[0]);
		}

		return ArrayUtil.filter(groupIds, visibleGroupIds::contains);
	}

	private Set<String> _getVisibleGroupIds() throws PortalException {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.
				fetchObjectDefinitionByExternalReferenceCode(
					"L_DSR_ROOM", permissionChecker.getCompanyId());

		if (objectDefinition == null) {
			return Collections.emptySet();
		}

		Set<String> visibleGroupIds = new HashSet<>();

		for (Group group :
				_groupService.search(
					permissionChecker.getCompanyId(),
					new long[] {
						_classNameLocalService.getClassNameId(
							objectDefinition.getClassName())
					},
					null,
					LinkedHashMapBuilder.<String, Object>put(
						"actionId", ActionKeys.VIEW
					).put(
						"active", Boolean.TRUE
					).put(
						"site", Boolean.TRUE
					).build(),
					QueryUtil.ALL_POS, QueryUtil.ALL_POS, null)) {

			if (permissionChecker.isCompanyAdmin() ||
				permissionChecker.isGroupOwner(group.getGroupId())) {

				visibleGroupIds.add(String.valueOf(group.getGroupId()));
			}
		}

		return visibleGroupIds;
	}

	@Reference
	private ClassNameLocalService _classNameLocalService;

	@Reference
	private GroupService _groupService;

	@Reference
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

}