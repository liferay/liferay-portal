/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.entry.processor.analytics;

import com.liferay.fragment.entry.processor.helper.InfoItemFieldMapped;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Marcos Martins
 */
public class AnalyticsAttributesContributorRegistryUtil {

	public static Map<String, Object> getAnalyticsAttributes(
		InfoItemFieldMapped infoItemFieldMapped, Locale locale) {

		Map<String, Object> analyticsAttributes = new HashMap<>();

		for (AnalyticsAttributesContributor analyticsAttributesContributor :
				_serviceTrackerList) {

			try {
				analyticsAttributes.putAll(
					analyticsAttributesContributor.getAnalyticsAttributes(
						infoItemFieldMapped, locale));
			}
			catch (PortalException portalException) {
				_log.error(portalException);
			}
		}

		return analyticsAttributes;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsAttributesContributorRegistryUtil.class);

	private static final ServiceTrackerList<AnalyticsAttributesContributor>
		_serviceTrackerList;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			AnalyticsAttributesContributorRegistryUtil.class);

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundle.getBundleContext(), AnalyticsAttributesContributor.class);
	}

}