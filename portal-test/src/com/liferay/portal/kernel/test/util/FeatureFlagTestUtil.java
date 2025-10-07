/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.feature.flag.FeatureFlagListener;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

/**
 * @author Thiago Buarque
 */
public class FeatureFlagTestUtil {

	public static void invokeFeatureFlagListeners(
		long companyId, boolean enabled, String key) {

		try (ServiceTrackerList<FeatureFlagListener> featureFlagListeners =
				ServiceTrackerListFactory.open(
					SystemBundleUtil.getBundleContext(),
					FeatureFlagListener.class,
					StringBundler.concat("(feature.flag.key=", key, ")"))) {

			for (FeatureFlagListener featureFlagListener :
					featureFlagListeners) {

				featureFlagListener.onValue(companyId, key, enabled);
			}
		}
	}

}