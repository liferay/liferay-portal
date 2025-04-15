/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notifications.web.internal.bulk.selection;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.bulk.selection.BaseContainerEntryBulkSelection;
import com.liferay.bulk.selection.BulkSelection;
import com.liferay.bulk.selection.BulkSelectionFactory;
import com.liferay.bulk.selection.EmptyBulkSelection;
import com.liferay.notifications.web.internal.util.comparator.UserNotificationEventUserNotificationEventIdOrderByComparator;
import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.interval.IntervalActionProcessor;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.util.MapUtil;

import jakarta.portlet.ActionRequest;

import java.util.List;
import java.util.Map;

/**
 * @author Roberto Díaz
 */
public class UserUserNotificationEventBulkSelection
	extends BaseContainerEntryBulkSelection<UserNotificationEvent> {

	public UserUserNotificationEventBulkSelection(
		long userId, Map<String, String[]> parameterMap,
		UserNotificationEventLocalService userNotificationEventLocalService) {

		super(userId, parameterMap);

		_userId = userId;
		_userNotificationEventLocalService = userNotificationEventLocalService;
	}

	@Override
	public <E extends PortalException> void forEach(
			UnsafeConsumer<UserNotificationEvent, E> unsafeConsumer)
		throws PortalException {

		IntervalActionProcessor<UserNotificationEvent>
			userNotificationEventIntervalActionProcessor =
				new IntervalActionProcessor<>((int)getSize());

		userNotificationEventIntervalActionProcessor.
			setPerformIntervalActionMethod(
				(start, end) -> {
					List<UserNotificationEvent> userNotificationEvents =
						_userNotificationEventLocalService.
							getUserNotificationEvents(
								_userId, start, end,
								UserNotificationEventUserNotificationEventIdOrderByComparator.
									getInstance(true));

					for (UserNotificationEvent userNotificationEvent :
							userNotificationEvents) {

						unsafeConsumer.accept(userNotificationEvent);
					}

					String actionName = MapUtil.getString(
						getParameterMap(), ActionRequest.ACTION_NAME);

					if (!actionName.equals("deleteNotifications") &&
						!actionName.equals("deleteUserNotificationEvent")) {

						userNotificationEventIntervalActionProcessor.
							incrementStart(userNotificationEvents.size());
					}

					return null;
				});

		userNotificationEventIntervalActionProcessor.performIntervalActions();
	}

	@Override
	public Class<? extends BulkSelectionFactory>
		getBulkSelectionFactoryClass() {

		return UserNotificationEventBulkSelectionFactory.class;
	}

	@Override
	public long getSize() throws PortalException {
		return _userNotificationEventLocalService.
			getUserNotificationEventsCount(_userId);
	}

	@Override
	public BulkSelection<AssetEntry> toAssetEntryBulkSelection() {
		return new EmptyBulkSelection<>();
	}

	private final long _userId;
	private final UserNotificationEventLocalService
		_userNotificationEventLocalService;

}