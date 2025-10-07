/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.constants;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import java.util.Map;
import java.util.Set;

/**
 * @author Carolina Barbosa
 */
public class ObjectActionConstants {

	public static final int STATUS_FAILED = 2;

	public static final int STATUS_NEVER_RAN = 0;

	public static final int STATUS_SUCCESS = 1;

	public static String getStatusLabel(int status) {
		if (status == STATUS_FAILED) {
			return "failed";
		}
		else if (status == STATUS_NEVER_RAN) {
			return "never-ran";
		}
		else if (status == STATUS_SUCCESS) {
			return "success";
		}

		return StringPool.BLANK;
	}

	public static String[] getSubscriptionObjectActionNames() {
		Set<String> subscriptionObjectActionNames =
			_subscriptionObjectActions.keySet();

		return subscriptionObjectActionNames.toArray(new String[0]);
	}

	public static Map<String, Map<String, String>>
		getSubscriptionObjectActions() {

		return _subscriptionObjectActions;
	}

	private static final Map<String, Map<String, String>>
		_subscriptionObjectActions =
			LinkedHashMapBuilder.<String, Map<String, String>>put(
				"SubscriptionAdded",
				HashMapBuilder.put(
					"conditionExpression", "objectEntryFolderId > 0"
				).put(
					"label", "Subscription Added"
				).put(
					"notificationTemplateExternalReferenceCode",
					"L_SUBSCRIPTION_ADDED_NOTIFICATION_TEMPLATE"
				).put(
					"objectActionTriggerKey",
					ObjectActionTriggerConstants.KEY_ON_AFTER_ADD
				).build()
			).put(
				"SubscriptionExpired",
				HashMapBuilder.put(
					"conditionExpression",
					"status == " + WorkflowConstants.STATUS_EXPIRED
				).put(
					"label", "Subscription Expired"
				).put(
					"notificationTemplateExternalReferenceCode",
					"L_SUBSCRIPTION_EXPIRED_NOTIFICATION_TEMPLATE"
				).put(
					"objectActionTriggerKey",
					ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE
				).build()
			).put(
				"SubscriptionUpdated",
				HashMapBuilder.put(
					"conditionExpression",
					"status != " + WorkflowConstants.STATUS_EXPIRED
				).put(
					"label", "Subscription Updated"
				).put(
					"notificationTemplateExternalReferenceCode",
					"L_SUBSCRIPTION_UPDATED_NOTIFICATION_TEMPLATE"
				).put(
					"objectActionTriggerKey",
					ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE
				).build()
			).build();

}