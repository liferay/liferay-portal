/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.internal.depot.entry.util;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.vulcan.util.SearchUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Thiago Buarque
 */
public class DepotEntryUtil {

	public static List<DepotEntry> getDepotEntries(
			long companyId, Long depotEntryId)
		throws Exception {

		List<DepotEntry> depotEntries = new ArrayList<>();

		DepotEntryService depotEntryService = _depotEntryServiceSnapshot.get();

		if (depotEntryId == null) {
			depotEntries.addAll(
				_getViewableDepotEntries(companyId, depotEntryService));
		}
		else {
			depotEntries.add(depotEntryService.getDepotEntry(depotEntryId));
		}

		return depotEntries;
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

	private static List<DepotEntry> _getViewableDepotEntries(
			long companyId, DepotEntryService depotEntryService)
		throws Exception {

		List<DepotEntry> depotEntries = new ArrayList<>();

		SearchUtil.search(
			Collections.emptyMap(),
			booleanQuery -> {
			},
			null, DepotEntry.class.getName(), null,
			Pagination.of(QueryUtil.ALL_POS, QueryUtil.ALL_POS),
			queryConfig -> {
			},
			searchContext -> searchContext.setCompanyId(companyId), null,
			document -> {
				try {
					depotEntries.add(
						depotEntryService.getDepotEntry(
							GetterUtil.getLong(
								document.get(Field.ENTRY_CLASS_PK))));
				}
				catch (PortalException portalException) {
					if (_log.isInfoEnabled()) {
						_log.info(
							"User does not have access to view space " +
								document.get(Field.ENTRY_CLASS_PK),
							portalException);
					}
				}

				return null;
			});

		return depotEntries;
	}

	private static final Log _log = LogFactoryUtil.getLog(DepotEntryUtil.class);

	private static final Snapshot<DepotEntryGroupRelLocalService>
		_depotEntryGroupRelLocalServiceSnapshot = new Snapshot<>(
			DepotEntryUtil.class, DepotEntryGroupRelLocalService.class);
	private static final Snapshot<DepotEntryService>
		_depotEntryServiceSnapshot = new Snapshot<>(
			DepotEntryUtil.class, DepotEntryService.class);

}