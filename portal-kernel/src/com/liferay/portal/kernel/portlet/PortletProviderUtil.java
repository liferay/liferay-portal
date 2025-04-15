/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortalUtil;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class PortletProviderUtil {

	public static String getPortletId(
		String className, PortletProvider.Action action) {

		PortletProvider portletProvider = _getPortletProvider(
			className, action);

		if (portletProvider != null) {
			return portletProvider.getPortletName();
		}

		return StringPool.BLANK;
	}

	public static PortletURL getPortletURL(
			HttpServletRequest httpServletRequest, Group group,
			String className, PortletProvider.Action action)
		throws PortalException {

		PortletProvider portletProvider = _getPortletProvider(
			className, action);

		if (portletProvider != null) {
			return portletProvider.getPortletURL(httpServletRequest, group);
		}

		return null;
	}

	public static PortletURL getPortletURL(
			HttpServletRequest httpServletRequest, String className,
			PortletProvider.Action action)
		throws PortalException {

		PortletProvider portletProvider = _getPortletProvider(
			className, action);

		if (portletProvider != null) {
			return portletProvider.getPortletURL(httpServletRequest);
		}

		return null;
	}

	public static PortletURL getPortletURL(
			PortletRequest portletRequest, Group group, String className,
			PortletProvider.Action action)
		throws PortalException {

		return getPortletURL(
			PortalUtil.getHttpServletRequest(portletRequest), group, className,
			action);
	}

	public static PortletURL getPortletURL(
			PortletRequest portletRequest, String className,
			PortletProvider.Action action)
		throws PortalException {

		return getPortletURL(
			PortalUtil.getHttpServletRequest(portletRequest), className,
			action);
	}

	private static PortletProvider _getPortletProvider(
		PortletProvider.Action action, List<PortletProvider> portletProviders) {

		if (portletProviders == null) {
			return null;
		}

		for (PortletProvider portletProvider : portletProviders) {
			if (ArrayUtil.contains(
					portletProvider.getSupportedActions(), action)) {

				return portletProvider;
			}
		}

		return null;
	}

	private static PortletProvider _getPortletProvider(
		String className, PortletProvider.Action action) {

		PortletProvider portletProvider = _getPortletProvider(
			action, _serviceTrackerMap.getService(className));

		if ((portletProvider == null) && _isAssetObject(className)) {
			portletProvider = _getPortletProvider(
				action,
				_serviceTrackerMap.getService(AssetEntry.class.getName()));
		}

		return portletProvider;
	}

	private static boolean _isAssetObject(String className) {
		AssetRendererFactory<?> assetRendererFactory =
			AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(
				className);

		if (assetRendererFactory != null) {
			return true;
		}

		return false;
	}

	private static final ServiceTrackerMap<String, List<PortletProvider>>
		_serviceTrackerMap = ServiceTrackerMapFactory.openMultiValueMap(
			SystemBundleUtil.getBundleContext(), PortletProvider.class,
			"model.class.name");

}