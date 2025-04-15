/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.sharing.notifications.internal.notifications;

import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.UserNotificationEvent;
import com.liferay.portal.kernel.notifications.BaseModelUserNotificationHandler;
import com.liferay.portal.kernel.notifications.UserNotificationHandler;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.sharing.constants.SharingPortletKeys;
import com.liferay.sharing.model.SharingEntry;
import com.liferay.sharing.service.SharingEntryLocalService;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "jakarta.portlet.name=" + SharingPortletKeys.SHARING,
	service = UserNotificationHandler.class
)
public class SharingUserNotificationHandler
	extends BaseModelUserNotificationHandler {

	public SharingUserNotificationHandler() {
		setPortletId(SharingPortletKeys.SHARING);
	}

	@Override
	protected String getBody(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return _getMessage(
			_jsonFactory.createJSONObject(userNotificationEvent.getPayload()),
			userNotificationEvent);
	}

	@Override
	protected String getTitle(
			UserNotificationEvent userNotificationEvent,
			ServiceContext serviceContext)
		throws Exception {

		return _getMessage(
			_jsonFactory.createJSONObject(userNotificationEvent.getPayload()),
			userNotificationEvent);
	}

	private String _getMessage(
			JSONObject jsonObject, UserNotificationEvent userNotificationEvent)
		throws Exception {

		SharingEntry sharingEntry = _sharingEntryLocalService.fetchSharingEntry(
			jsonObject.getLong("classPK"));

		if (sharingEntry == null) {
			_userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent.getUserNotificationEventId());

			return null;
		}

		AssetRenderer<?> assetRenderer = getAssetRenderer(
			sharingEntry.getClassName(), sharingEntry.getClassPK());

		if ((assetRenderer == null) ||
			_isInTrash(
				sharingEntry.getClassName(), sharingEntry.getClassPK())) {

			_userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent.getUserNotificationEventId());

			return null;
		}

		String message = jsonObject.getString("message");

		if (Validator.isNull(message)) {
			_userNotificationEventLocalService.deleteUserNotificationEvent(
				userNotificationEvent);
		}

		return HtmlUtil.escape(message);
	}

	private boolean _isInTrash(String className, long classPK)
		throws Exception {

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			className);

		if (trashHandler == null) {
			return false;
		}

		return trashHandler.isInTrash(classPK);
	}

	@Reference
	private JSONFactory _jsonFactory;

	@Reference
	private SharingEntryLocalService _sharingEntryLocalService;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}