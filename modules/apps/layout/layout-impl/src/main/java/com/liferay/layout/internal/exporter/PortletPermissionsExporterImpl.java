/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.exporter;

import com.liferay.layout.exporter.PortletPermissionsExporter;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Lourdes Fernández Besada
 */
@Component(service = PortletPermissionsExporter.class)
public class PortletPermissionsExporterImpl
	implements PortletPermissionsExporter {

	@Override
	public Map<String, String[]> getPortletPermissions(
		long plid, String portletId) {

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return null;
		}

		String portletName = PortletIdCodec.decodePortletName(portletId);

		Portlet portlet = _portletLocalService.getPortletById(portletName);

		if (portlet == null) {
			return null;
		}

		String resourcePrimKey = PortletPermissionUtil.getPrimaryKey(
			plid, portletId);

		List<ResourcePermission> resourcePermissions =
			_resourcePermissionLocalService.getResourcePermissions(
				layout.getCompanyId(), portletName,
				ResourceConstants.SCOPE_INDIVIDUAL, resourcePrimKey);

		if (ListUtil.isEmpty(resourcePermissions)) {
			return null;
		}

		List<ResourceAction> resourceActions =
			_resourceActionLocalService.getResourceActions(portletName);

		if (ListUtil.isEmpty(resourceActions)) {
			return null;
		}

		Map<String, String[]> permissionsMap = new TreeMap<>();

		for (ResourcePermission resourcePermission : resourcePermissions) {
			Role role = _roleLocalService.fetchRole(
				resourcePermission.getRoleId());

			if (role == null) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						String.format(
							"Resource permission %s will not be exported " +
								"since no role was found with role ID %s",
							resourcePermission.getName(),
							resourcePermission.getRoleId()));
				}

				continue;
			}

			Set<String> actionIdsSet = new TreeSet<>();

			long actionIds = resourcePermission.getActionIds();

			for (ResourceAction resourceAction : resourceActions) {
				long bitwiseValue = resourceAction.getBitwiseValue();

				if ((actionIds & bitwiseValue) == bitwiseValue) {
					actionIdsSet.add(resourceAction.getActionId());
				}
			}

			String roleKey = role.getName();

			if (role.getClassNameId() == _portal.getClassNameId(Team.class)) {
				Team team = _teamLocalService.fetchTeam(role.getClassPK());

				if (team != null) {
					roleKey = team.getName();
				}
			}

			permissionsMap.put(roleKey, actionIdsSet.toArray(new String[0]));
		}

		return permissionsMap;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletPermissionsExporterImpl.class);

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private Portal _portal;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private ResourceActionLocalService _resourceActionLocalService;

	@Reference
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private TeamLocalService _teamLocalService;

}