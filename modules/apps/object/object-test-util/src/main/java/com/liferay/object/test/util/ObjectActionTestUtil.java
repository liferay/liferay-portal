/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.test.util;

import com.liferay.notification.model.NotificationTemplate;
import com.liferay.object.constants.ObjectActionExecutorConstants;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.object.model.ObjectAction;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectActionLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

/**
 * @author Carolina Barbosa
 */
public class ObjectActionTestUtil {

	public static ObjectAction addObjectAction(
			NotificationTemplate notificationTemplate,
			ObjectDefinition objectDefinition)
		throws Exception {

		return addObjectAction(
			notificationTemplate, ObjectActionTriggerConstants.KEY_ON_AFTER_ADD,
			objectDefinition);
	}

	public static ObjectAction addObjectAction(
			NotificationTemplate notificationTemplate,
			String objectActionTriggerKey, ObjectDefinition objectDefinition)
		throws Exception {

		return addObjectAction(
			ObjectActionExecutorConstants.KEY_NOTIFICATION,
			objectActionTriggerKey, objectDefinition,
			UnicodePropertiesBuilder.put(
				"notificationTemplateId",
				notificationTemplate.getNotificationTemplateId()
			).build());
	}

	public static ObjectAction addObjectAction(
			String objectActionExecutorKey, String objectActionTriggerKey,
			ObjectDefinition objectDefinition,
			UnicodeProperties unicodeProperties)
		throws Exception {

		return ObjectActionLocalServiceUtil.addObjectAction(
			null, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true, StringPool.BLANK,
			RandomTestUtil.randomString(),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			RandomTestUtil.randomString(), objectActionExecutorKey,
			objectActionTriggerKey, unicodeProperties, false);
	}

}