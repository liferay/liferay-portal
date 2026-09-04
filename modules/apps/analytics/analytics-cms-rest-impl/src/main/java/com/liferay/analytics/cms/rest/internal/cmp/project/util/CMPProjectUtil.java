/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.cmp.project.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcos Martins
 */
public class CMPProjectUtil {

	public static Long[] getFilteredCMPProjectIds(
			String actionId, Long[] cmpProjectIds)
		throws PortalException {

		if (ArrayUtil.isEmpty(cmpProjectIds)) {
			return null;
		}

		return _getCMPProjectIds(actionId, cmpProjectIds);
	}

	public static String getFilterString(
		Long[] cmpProjectIds, String filterString) {

		if (ArrayUtil.isEmpty(cmpProjectIds)) {
			return filterString;
		}

		String cmpProjectFilterString = StringBundler.concat(
			"cmpProjects/id in ('", StringUtil.merge(cmpProjectIds, "', '"),
			"')");

		if (Validator.isNull(filterString)) {
			return cmpProjectFilterString;
		}

		return StringBundler.concat(
			"(", filterString, ") and ", cmpProjectFilterString);
	}

	public static boolean hasNoVisibleCMPProjects(
		Long[] filteredCMPProjectIds) {

		if ((filteredCMPProjectIds != null) &&
			ArrayUtil.isEmpty(filteredCMPProjectIds)) {

			return true;
		}

		return false;
	}

	private static Long[] _getCMPProjectIds(
			String actionId, Long[] cmpProjectIds)
		throws PortalException {

		List<Long> filteredCMPProjectIds = new ArrayList<>();

		DepotEntryLocalService depotEntryLocalService =
			_depotEntryLocalServiceSnapshot.get();
		ModelResourcePermission<DepotEntry> modelResourcePermission =
			_depotEntryModelResourcePermissionSnapshot.get();
		ObjectEntryLocalService objectEntryLocalService =
			_objectEntryLocalServiceSnapshot.get();
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		for (Long cmpProjectId : cmpProjectIds) {
			if (cmpProjectId == null) {
				continue;
			}

			ObjectEntry objectEntry = objectEntryLocalService.fetchObjectEntry(
				cmpProjectId);

			if (objectEntry == null) {
				continue;
			}

			DepotEntry depotEntry = depotEntryLocalService.fetchGroupDepotEntry(
				objectEntry.getGroupId());

			if (depotEntry == null) {
				continue;
			}

			if (modelResourcePermission.contains(
					permissionChecker, depotEntry, actionId)) {

				filteredCMPProjectIds.add(cmpProjectId);
			}
			else if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"User does not have the \"", actionId,
						"\" permission on CMP project ", cmpProjectId));
			}
		}

		return filteredCMPProjectIds.toArray(new Long[0]);
	}

	private static final Log _log = LogFactoryUtil.getLog(CMPProjectUtil.class);

	private static final Snapshot<DepotEntryLocalService>
		_depotEntryLocalServiceSnapshot = new Snapshot<>(
			CMPProjectUtil.class, DepotEntryLocalService.class);
	private static final Snapshot<ModelResourcePermission<DepotEntry>>
		_depotEntryModelResourcePermissionSnapshot = new Snapshot<>(
			CMPProjectUtil.class, Snapshot.cast(ModelResourcePermission.class),
			"(model.class.name=com.liferay.depot.model.DepotEntry)");
	private static final Snapshot<ObjectEntryLocalService>
		_objectEntryLocalServiceSnapshot = new Snapshot<>(
			CMPProjectUtil.class, ObjectEntryLocalService.class);

}