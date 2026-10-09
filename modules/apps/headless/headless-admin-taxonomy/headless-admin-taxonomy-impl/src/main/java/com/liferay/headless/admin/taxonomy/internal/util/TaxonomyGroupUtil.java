/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.admin.taxonomy.dto.v1_0.AssetLibrary;
import com.liferay.headless.admin.taxonomy.dto.v1_0.Project;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class TaxonomyGroupUtil {

	public static long[] getAssetLibraryGroupIds(
			AssetLibrary[] assetLibraries, long companyId)
		throws PortalException {

		if (ArrayUtil.isEmpty(assetLibraries)) {
			return _GROUP_IDS_ALL;
		}

		List<Long> groupIds = new ArrayList<>();

		for (AssetLibrary assetLibrary : assetLibraries) {
			if (assetLibrary == null) {
				continue;
			}

			Group group = _fetchGroup(
				companyId, assetLibrary.getExternalReferenceCode(),
				assetLibrary.getId(), assetLibrary.getScopeKey());

			if ((group == null) ||
				!_isGroupDepotEntryType(group, DepotConstants.TYPE_SPACE)) {

				continue;
			}

			groupIds.add(group.getGroupId());
		}

		if (groupIds.isEmpty()) {
			return _GROUP_IDS_ALL;
		}

		return ArrayUtil.toLongArray(groupIds);
	}

	public static long getCMSGroupId(long companyId) throws PortalException {
		Group group = GroupLocalServiceUtil.getGroup(
			companyId, GroupConstants.CMS);

		return group.getGroupId();
	}

	public static long[] getProjectGroupIds(Project[] projects, long companyId)
		throws PortalException {

		if (ArrayUtil.isEmpty(projects)) {
			return _GROUP_IDS_ALL;
		}

		List<Long> groupIds = new ArrayList<>();

		for (Project project : projects) {
			if (project == null) {
				continue;
			}

			Group group = _fetchGroup(
				companyId, project.getExternalReferenceCode(), project.getId(),
				project.getScopeKey());

			if ((group == null) ||
				!_isGroupDepotEntryType(group, DepotConstants.TYPE_PROJECT)) {

				continue;
			}

			groupIds.add(group.getGroupId());
		}

		if (groupIds.isEmpty()) {
			return _GROUP_IDS_ALL;
		}

		return ArrayUtil.toLongArray(groupIds);
	}

	private static Group _fetchGroup(
			long companyId, String externalReferenceCode, Long id,
			String scopeKey)
		throws PortalException {

		if (Validator.isNotNull(externalReferenceCode)) {
			Group group =
				GroupLocalServiceUtil.fetchGroupByExternalReferenceCode(
					externalReferenceCode, companyId);

			if (group != null) {
				return group;
			}
		}

		if (Validator.isNotNull(scopeKey)) {
			Group group = GroupLocalServiceUtil.fetchGroup(companyId, scopeKey);

			if (group != null) {
				return group;
			}
		}

		if ((id == null) || (id == GroupConstants.ANY_PARENT_GROUP_ID)) {
			return null;
		}

		Group group = GroupLocalServiceUtil.fetchGroup(id);

		if (group != null) {
			return group;
		}

		DepotEntry depotEntry = DepotEntryLocalServiceUtil.fetchDepotEntry(id);

		if (depotEntry != null) {
			return depotEntry.getGroup();
		}

		return null;
	}

	private static boolean _isGroupDepotEntryType(
		Group group, int depotEntryType) {

		int groupDepotEntryType = GetterUtil.getInteger(
			group.getTypeSettingsProperty("depotEntryType"));

		if (groupDepotEntryType == depotEntryType) {
			return true;
		}

		return false;
	}

	private static final long[] _GROUP_IDS_ALL = {-1L};

}