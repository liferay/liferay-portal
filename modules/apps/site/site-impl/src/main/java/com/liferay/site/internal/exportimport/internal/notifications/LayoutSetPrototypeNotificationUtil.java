/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.exportimport.internal.notifications;

import com.liferay.exportimport.kernel.background.task.constants.LayoutSetPrototypeBackgroundTaskConstants;
import com.liferay.layout.set.prototype.constants.LayoutSetPrototypePortletKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.backgroundtask.constants.BackgroundTaskConstants;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.notifications.NotificationEvent;
import com.liferay.portal.kernel.service.UserNotificationEventLocalServiceUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.uuid.PortalUUIDUtil;

import java.io.Serializable;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Carlos Correa
 */
public class LayoutSetPrototypeNotificationUtil {

	public static final String STATUS_COMPLETED_WITH_ERRORS =
		"completed-with-errors";

	public static final String STATUS_FAILED = "failed";

	public static final String STATUS_SUCCESSFUL = "successful";

	public static Map<String, Serializable> buildTaskContextMap(
		List<LayoutSet> layoutSets, LayoutSetPrototype layoutSetPrototype,
		boolean preValidationErrors, long userId) {

		return HashMapBuilder.<String, Serializable>put(
			_LAYOUT_SET_GROUP_IDS,
			TransformUtil.transformToArray(
				layoutSets, LayoutSet::getGroupId, Long.class)
		).put(
			_LAYOUT_SET_PROTOTYPE_ID,
			layoutSetPrototype.getLayoutSetPrototypeId()
		).put(
			_PRE_VALIDATION_ERRORS, preValidationErrors
		).put(
			_USER_ID, userId
		).put(
			LayoutSetPrototypeBackgroundTaskConstants.SESSION_ID,
			PortalUUIDUtil.generate()
		).build();
	}

	public static Long[] getLayoutSetGroupIds(
		Map<String, Serializable> taskContextMap) {

		Object[] layoutSetGroupIds = (Object[])taskContextMap.get(
			_LAYOUT_SET_GROUP_IDS);

		if (layoutSetGroupIds == null) {
			return null;
		}

		return TransformUtil.transform(
			layoutSetGroupIds,
			object -> {
				Number number = (Number)object;

				return number.longValue();
			},
			Long.class);
	}

	public static long getLayoutSetPrototypeId(
		Map<String, Serializable> taskContextMap) {

		return MapUtil.getLong(taskContextMap, _LAYOUT_SET_PROTOTYPE_ID);
	}

	public static long getUserId(Map<String, Serializable> taskContextMap) {
		return MapUtil.getLong(taskContextMap, _USER_ID);
	}

	public static boolean hasPreValidationErrors(
		Map<String, Serializable> taskContextMap) {

		return MapUtil.getBoolean(taskContextMap, _PRE_VALIDATION_ERRORS);
	}

	public static boolean isNotificationProcessed(
		Map<String, Serializable> taskContextMap) {

		return MapUtil.getBoolean(taskContextMap, _NOTIFICATION_PROCESSED);
	}

	public static Map<String, Serializable> putNotificationProcessed(
		Map<String, Serializable> taskContextMap) {

		taskContextMap.put(_NOTIFICATION_PROCESSED, Boolean.TRUE);

		return taskContextMap;
	}

	public static void sendMergeCompletedNotification(
		Set<Integer> backgroundTaskStatuses,
		LayoutSetPrototype layoutSetPrototype, long userId) {

		NotificationEvent notificationEvent = new NotificationEvent(
			System.currentTimeMillis(),
			LayoutSetPrototypePortletKeys.LAYOUT_SET_PROTOTYPE,
			JSONUtil.put(
				"layoutSetPrototypeNameMap",
				_toJSONObject(layoutSetPrototype.getNameMap())
			).put(
				"result", _toResult(backgroundTaskStatuses)
			));

		notificationEvent.setDeliveryType(
			UserNotificationDeliveryConstants.TYPE_WEBSITE);

		try {
			UserNotificationEventLocalServiceUtil.addUserNotificationEvent(
				userId, notificationEvent);
		}
		catch (PortalException portalException) {
			_log.error(
				"Unable to post merge notification for user " + userId,
				portalException);
		}
	}

	private static JSONObject _toJSONObject(Map<Locale, String> nameMap) {
		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		if (nameMap != null) {
			for (Map.Entry<Locale, String> entry : nameMap.entrySet()) {
				jsonObject.put(
					LocaleUtil.toLanguageId(entry.getKey()), entry.getValue());
			}
		}

		return jsonObject;
	}

	private static String _toResult(Set<Integer> backgroundTaskStatuses) {
		if (SetUtil.isEmpty(backgroundTaskStatuses)) {
			return STATUS_SUCCESSFUL;
		}

		if (backgroundTaskStatuses.contains(
				BackgroundTaskConstants.STATUS_FAILED)) {

			return STATUS_FAILED;
		}

		if (backgroundTaskStatuses.contains(
				BackgroundTaskConstants.STATUS_COMPLETED_WITH_ERRORS)) {

			return STATUS_COMPLETED_WITH_ERRORS;
		}

		return STATUS_SUCCESSFUL;
	}

	private static final String _LAYOUT_SET_GROUP_IDS = "layoutSetGroupIds";

	private static final String _LAYOUT_SET_PROTOTYPE_ID =
		"layoutSetPrototypeId";

	private static final String _NOTIFICATION_PROCESSED =
		"notification-processed";

	private static final String _PRE_VALIDATION_ERRORS = "preValidationErrors";

	private static final String _USER_ID = "userId";

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutSetPrototypeNotificationUtil.class);

}