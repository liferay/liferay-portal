/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;

import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class PortletLayoutFinderRegistryUtil {

	public static PortletLayoutFinder getPortletLayoutFinder(String className) {
		List<PortletLayoutFinder> portletLayoutFinders =
			_serviceTrackerMap.getService(className);

		if (portletLayoutFinders == null) {
			return null;
		}

		return new CompositePortletLayoutFinder(portletLayoutFinders);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletLayoutFinderRegistryUtil.class);

	private static final ServiceTrackerMap<String, List<PortletLayoutFinder>>
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			SystemBundleUtil.getBundleContext(), PortletLayoutFinder.class,
			"model.class.name");

	private static class CompositePortletLayoutFinder
		implements PortletLayoutFinder {

		public CompositePortletLayoutFinder(
			Iterable<PortletLayoutFinder> portletLayoutFinders) {

			_portletLayoutFinders = portletLayoutFinders;
		}

		@Override
		public Result find(ThemeDisplay themeDisplay, long groupId) {
			try {
				for (PortletLayoutFinder portletLayoutFinder :
						_portletLayoutFinders) {

					Result result = portletLayoutFinder.find(
						themeDisplay, groupId);

					if (result != null) {
						return result;
					}
				}
			}
			catch (PortalException portalException) {
				if (_log.isDebugEnabled()) {
					_log.debug(portalException);
				}
			}

			return null;
		}

		private final Iterable<PortletLayoutFinder> _portletLayoutFinders;

	}

}