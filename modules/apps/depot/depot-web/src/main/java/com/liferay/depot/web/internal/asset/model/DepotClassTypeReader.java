/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.web.internal.asset.model;

import com.liferay.asset.kernel.model.ClassType;
import com.liferay.asset.kernel.model.ClassTypeReader;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.interval.IntervalActionProcessor;
import com.liferay.portal.kernel.util.ListUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Adolfo Pérez
 */
public class DepotClassTypeReader implements ClassTypeReader {

	public DepotClassTypeReader(
		ClassTypeReader classTypeReader,
		DepotEntryLocalService depotEntryLocalService) {

		_classTypeReader = classTypeReader;
		_depotEntryLocalService = depotEntryLocalService;
	}

	@Override
	public List<ClassType> getAvailableClassTypes(
		long[] groupIds, Locale locale) {

		try {
			return _classTypeReader.getAvailableClassTypes(
				_getGroupIds(groupIds), locale);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	@Override
	public ClassType getClassType(long classTypeId, Locale locale)
		throws PortalException {

		return _classTypeReader.getClassType(classTypeId, locale);
	}

	private long[] _getGroupIds(long[] siteGroupIds) throws PortalException {
		List<Long> groupIds = new ArrayList<>(ListUtil.fromArray(siteGroupIds));

		for (long siteGroupId : siteGroupIds) {
			IntervalActionProcessor<Void> intervalActionProcessor =
				new IntervalActionProcessor<>(
					_depotEntryLocalService.getGroupConnectedDepotEntriesCount(
						siteGroupId, DepotConstants.TYPE_ANY));

			intervalActionProcessor.setPerformIntervalActionMethod(
				(start, end) -> {
					List<DepotEntry> depotEntries =
						_depotEntryLocalService.getGroupConnectedDepotEntries(
							siteGroupId, DepotConstants.TYPE_ANY, start, end);

					for (DepotEntry depotEntry : depotEntries) {
						groupIds.add(depotEntry.getGroupId());
					}

					return null;
				});

			intervalActionProcessor.performIntervalActions();
		}

		return ListUtil.toLongArray(groupIds, groupId -> groupId);
	}

	private final ClassTypeReader _classTypeReader;
	private final DepotEntryLocalService _depotEntryLocalService;

}