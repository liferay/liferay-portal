/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.impl;

import com.liferay.depot.constants.DepotActionKeys;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.base.DepotEntryServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.permission.GroupPermissionUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"json.web.service.context.name=depot",
		"json.web.service.context.path=DepotEntry"
	},
	service = AopService.class
)
public class DepotEntryServiceImpl extends DepotEntryServiceBaseImpl {

	@Override
	public DepotEntry addDepotEntry(
			Map<Locale, String> nameMap, Map<Locale, String> descriptionMap,
			int type, ServiceContext serviceContext)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			DepotActionKeys.ADD_DEPOT_ENTRY);

		return depotEntryLocalService.addDepotEntry(
			nameMap, descriptionMap, type, serviceContext);
	}

	@Override
	public DepotEntry deleteDepotEntry(long depotEntryId)
		throws PortalException {

		_depotEntryModelResourcePermission.check(
			getPermissionChecker(), depotEntryId, ActionKeys.DELETE);

		return depotEntryLocalService.deleteDepotEntry(depotEntryId);
	}

	@Override
	public DepotEntry fetchGroupDepotEntry(long groupId)
		throws PortalException {

		DepotEntry depotEntry = depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if (depotEntry != null) {
			_depotEntryModelResourcePermission.check(
				getPermissionChecker(), depotEntry, ActionKeys.VIEW);
		}

		return depotEntry;
	}

	@Override
	public List<DepotEntry> getCurrentAndGroupConnectedDepotEntries(
			long groupId, int type, int start, int end)
		throws PortalException {

		List<DepotEntry> filteredDepotEntries = getGroupConnectedDepotEntries(
			groupId, type, start, end);

		DepotEntry depotEntry = depotEntryLocalService.fetchGroupDepotEntry(
			groupId);

		if (depotEntry != null) {
			filteredDepotEntries.add(depotEntry);
		}

		return filteredDepotEntries;
	}

	@Override
	public DepotEntry getDepotEntry(long depotEntryId) throws PortalException {
		if (!_depotEntryModelResourcePermission.contains(
				getPermissionChecker(), depotEntryId, ActionKeys.VIEW) &&
			!_depotEntryModelResourcePermission.contains(
				getPermissionChecker(), depotEntryId,
				ActionKeys.VIEW_SITE_ADMINISTRATION)) {

			_depotEntryModelResourcePermission.check(
				getPermissionChecker(), depotEntryId, ActionKeys.VIEW);
		}

		return depotEntryLocalService.getDepotEntry(depotEntryId);
	}

	@Override
	public List<DepotEntry> getGroupConnectedDepotEntries(
			long groupId, boolean ddmStructuresAvailable, int start, int end)
		throws PortalException {

		if (!GroupPermissionUtil.contains(
				getPermissionChecker(), groupId, ActionKeys.VIEW)) {

			return Collections.emptyList();
		}

		return depotEntryLocalService.getGroupConnectedDepotEntries(
			groupId, ddmStructuresAvailable, start, end);
	}

	@Override
	public List<DepotEntry> getGroupConnectedDepotEntries(
			long groupId, int type, int start, int end)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (!GroupPermissionUtil.contains(
				permissionChecker, groupId, ActionKeys.VIEW)) {

			return Collections.emptyList();
		}

		List<DepotEntry> filteredDepotEntries = new ArrayList<>();

		for (DepotEntry depotEntry :
				depotEntryLocalService.getGroupConnectedDepotEntries(
					groupId, type, start, end)) {

			Group group = depotEntry.getGroup();

			if (group.isCompany() ||
				GroupPermissionUtil.contains(
					permissionChecker, group.getGroupId(), ActionKeys.VIEW) ||
				permissionChecker.isGroupAdmin(group.getGroupId())) {

				filteredDepotEntries.add(depotEntry);
			}
		}

		return filteredDepotEntries;
	}

	@Override
	public int getGroupConnectedDepotEntriesCount(long groupId, int type)
		throws PortalException {

		if (!GroupPermissionUtil.contains(
				getPermissionChecker(), groupId, ActionKeys.VIEW)) {

			return 0;
		}

		return depotEntryLocalService.getGroupConnectedDepotEntriesCount(
			groupId, type);
	}

	@Override
	public DepotEntry getGroupDepotEntry(long groupId) throws PortalException {
		DepotEntry depotEntry = depotEntryLocalService.getGroupDepotEntry(
			groupId);

		_depotEntryModelResourcePermission.check(
			getPermissionChecker(), depotEntry, ActionKeys.VIEW);

		return depotEntry;
	}

	@Override
	public DepotEntry updateDepotEntry(
			long depotEntryId, Map<Locale, String> nameMap,
			Map<Locale, String> descriptionMap,
			Map<String, Boolean> depotAppCustomizationMap,
			UnicodeProperties typeSettingsUnicodeProperties,
			ServiceContext serviceContext)
		throws PortalException {

		_depotEntryModelResourcePermission.check(
			getPermissionChecker(), depotEntryId, ActionKeys.UPDATE);

		return depotEntryLocalService.updateDepotEntry(
			depotEntryId, nameMap, descriptionMap, depotAppCustomizationMap,
			typeSettingsUnicodeProperties, serviceContext);
	}

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(model.class.name=com.liferay.depot.model.DepotEntry)"
	)
	private volatile ModelResourcePermission<DepotEntry>
		_depotEntryModelResourcePermission;

	@Reference(
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY,
		target = "(resource.name=" + DepotConstants.RESOURCE_NAME + ")"
	)
	private volatile PortletResourcePermission _portletResourcePermission;

}