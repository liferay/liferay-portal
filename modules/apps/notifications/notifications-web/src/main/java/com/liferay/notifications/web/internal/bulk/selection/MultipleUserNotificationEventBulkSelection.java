/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.bulk.selection.BaseMultipleEntryBulkSelection;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.EmptyBulkSelection;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;

import java.util.Map;

/**
 * @author Roberto Díaz
 */
public class MultipleUserNotificationEventBulkSelection
	extends BaseMultipleEntryBulkSelection<UserNotificationEvent> {

	public MultipleUserNotificationEventBulkSelection(
		long[] entryIds, Map<String, String[]> parameterMap,
		UserNotificationEventLocalService userNotificationEventLocalService) {

		super(entryIds, parameterMap);

		_userNotificationEventLocalService = userNotificationEventLocalService;
	}

	@Override
	public Class<? extends BulkSelectionFactory>
		getBulkSelectionFactoryClass() {

		return UserNotificationEventBulkSelectionFactory.class;
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		return new EmptyBulkSelection<>();
	}

	@Override
	protected UserNotificationEvent fetchEntry(long entryId) {
		return _userNotificationEventLocalService.fetchUserNotificationEvent(
			entryId);
	}

	private final UserNotificationEventLocalService
		_userNotificationEventLocalService;

}