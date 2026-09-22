/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.web.internal.constants;

import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

/**
 * @author Caio Pinheiro
 */
public class FaroNotificationTemplateConstants {

	public static final String EXTERNAL_REFERENCE_CODE_LIFECYCLE_NOTIFICATION =
		"com.liferay.osb.faro.notification.lifecycle";

	public static final String EXTERNAL_REFERENCE_CODE_SEGMENT_NOTIFICATION =
		"com.liferay.osb.faro.notification.segment";

	public static Map<Locale, String> getDefaultLocalizedMap(
		String externalReferenceCode, String key) {

		Map<String, String> defaultNotificationTemplate =
			_defaultNotificationTemplates.get(externalReferenceCode);

		return Collections.singletonMap(
			LocaleUtil.getDefault(),
			MapUtil.getString(defaultNotificationTemplate, key));
	}

	public static Map<String, Map<String, String>>
		getDefaultNotificationTemplates() {

		return _defaultNotificationTemplates;
	}

	private static final Map<String, Map<String, String>>
		_defaultNotificationTemplates =
			HashMapBuilder.<String, Map<String, String>>put(
				EXTERNAL_REFERENCE_CODE_LIFECYCLE_NOTIFICATION,
				HashMapBuilder.put(
					"body",
					"<p>[%LIFECYCLE_RESULT_TEXT%]</p><p><a " +
						"href=\"[%LIFECYCLE_URL%]\">View the lifecycle</a>" +
							"</p><p>[%SENDER_NAME%]</p>"
				).put(
					"name", "Lifecycle Notification"
				).put(
					"subject", "[%LIFECYCLE_TRIGGER_LABEL%]: [%LIFECYCLE_NAME%]"
				).build()
			).put(
				EXTERNAL_REFERENCE_CODE_SEGMENT_NOTIFICATION,
				HashMapBuilder.put(
					"body",
					"<p><strong>[%SEGMENT_NEW_MEMBERS_COUNT%]</strong> new " +
						"members were added to your segment.</p><p><a " +
							"href=\"[%SEGMENT_URL%]\">View the segment</a>" +
								"</p><p>[%SENDER_NAME%]</p>"
				).put(
					"name", "Segment Notification"
				).put(
					"subject",
					"[%SEGMENT_NEW_MEMBERS_COUNT%] new members joined your " +
						"segment"
				).build()
			).build();

}