/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.depot.entry.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.model.DepotEntryTable;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.sql.dsl.DSLQueryFactoryUtil;
import com.liferay.petra.sql.dsl.expression.Predicate;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.ArrayUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Thiago Buarque
 */
public class DepotEntryUtil {

	public static List<DepotEntry> getDepotEntries(
			String actionId, long companyId, Long... depotEntryIds)
		throws PortalException {

		DepotEntryLocalService depotEntryLocalService =
			_depotEntryLocalServiceSnapshot.get();

		Predicate predicate = DepotEntryTable.INSTANCE.companyId.eq(companyId);

		Long[] filteredDepotEntryIds = ArrayUtil.filter(
			depotEntryIds, Objects::nonNull);

		if (ArrayUtil.isNotEmpty(filteredDepotEntryIds)) {
			predicate = predicate.and(
				DepotEntryTable.INSTANCE.depotEntryId.in(
					filteredDepotEntryIds));
		}

		List<DepotEntry> depotEntries = depotEntryLocalService.dslQuery(
			DSLQueryFactoryUtil.select(
				DepotEntryTable.INSTANCE
			).from(
				DepotEntryTable.INSTANCE
			).where(
				predicate
			));

		return _filterDepotEntries(actionId, depotEntries);
	}

	public static Long[] getGroupIds(List<DepotEntry> depotEntries) {
		List<Long> groupIds = new ArrayList<>();

		DepotEntryGroupRelLocalService depotEntryGroupRelLocalService =
			_depotEntryGroupRelLocalServiceSnapshot.get();

		for (DepotEntry depotEntry : depotEntries) {
			groupIds.add(depotEntry.getGroupId());

			List<DepotEntryGroupRel> depotEntryGroupRels =
				depotEntryGroupRelLocalService.getDepotEntryGroupRels(
					depotEntry);

			for (DepotEntryGroupRel depotEntryGroupRel : depotEntryGroupRels) {
				groupIds.add(depotEntryGroupRel.getGroupId());
			}
		}

		return groupIds.toArray(new Long[0]);
	}

	private static List<DepotEntry> _filterDepotEntries(
			String actionId, List<DepotEntry> depotEntries)
		throws PortalException {

		List<DepotEntry> filteredDepotEntries = new ArrayList<>();

		ModelResourcePermission<DepotEntry> modelResourcePermission =
			_depotEntryModelResourcePermissionSnapshot.get();
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		for (DepotEntry depotEntry : depotEntries) {
			if (modelResourcePermission.contains(
					permissionChecker, depotEntry, actionId)) {

				filteredDepotEntries.add(depotEntry);
			}
			else if (_log.isInfoEnabled()) {
				_log.info(
					StringBundler.concat(
						"User does not have the ", actionId,
						" permission on depot entry ",
						depotEntry.getDepotEntryId()));
			}
		}

		return filteredDepotEntries;
	}

	private static final Log _log = LogFactoryUtil.getLog(DepotEntryUtil.class);

	private static final Snapshot<DepotEntryGroupRelLocalService>
		_depotEntryGroupRelLocalServiceSnapshot = new Snapshot<>(
			DepotEntryUtil.class, DepotEntryGroupRelLocalService.class);
	private static final Snapshot<DepotEntryLocalService>
		_depotEntryLocalServiceSnapshot = new Snapshot<>(
			DepotEntryUtil.class, DepotEntryLocalService.class);
	private static final Snapshot<ModelResourcePermission<DepotEntry>>
		_depotEntryModelResourcePermissionSnapshot = new Snapshot<>(
			DepotEntryUtil.class, Snapshot.cast(ModelResourcePermission.class),
			"(model.class.name=com.liferay.depot.model.DepotEntry)");

}