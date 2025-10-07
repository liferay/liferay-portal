/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.internal.action.trigger.util;

import com.liferay.object.action.trigger.ObjectActionTrigger;
import com.liferay.object.constants.ObjectActionTriggerConstants;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class ObjectActionTriggerUtil {

	public static List<ObjectActionTrigger> getDefaultObjectActionTriggers() {
		if (!FeatureFlagManagerUtil.isEnabled("LPD-34594")) {
			return ListUtil.filter(
				_defaultObjectActionTriggers,
				objectActionTrigger -> !StringUtil.equals(
					objectActionTrigger.getKey(),
					ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE));
		}

		return _defaultObjectActionTriggers;
	}

	public static List<ObjectActionTrigger> sort(
		List<ObjectActionTrigger> objectActionTriggers) {

		objectActionTriggers.sort(
			(ObjectActionTrigger objectActionTrigger1,
			 ObjectActionTrigger objectActionTrigger2) -> {

				String key1 = objectActionTrigger1.getKey();
				String key2 = objectActionTrigger2.getKey();

				return key1.compareTo(key2);
			});

		return objectActionTriggers;
	}

	private static final List<ObjectActionTrigger>
		_defaultObjectActionTriggers = Collections.unmodifiableList(
			sort(
				Arrays.asList(
					new ObjectActionTrigger(
						ObjectActionTriggerConstants.KEY_ON_AFTER_ADD),
					new ObjectActionTrigger(
						ObjectActionTriggerConstants.
							KEY_ON_AFTER_ATTACHMENT_DOWNLOAD),
					new ObjectActionTrigger(
						ObjectActionTriggerConstants.KEY_ON_AFTER_DELETE),
					new ObjectActionTrigger(
						ObjectActionTriggerConstants.KEY_ON_AFTER_LOGIN),
					new ObjectActionTrigger(
						ObjectActionTriggerConstants.KEY_ON_AFTER_ROOT_UPDATE),
					new ObjectActionTrigger(
						ObjectActionTriggerConstants.KEY_ON_AFTER_UPDATE),
					new ObjectActionTrigger(
						ObjectActionTriggerConstants.KEY_STANDALONE))));

}