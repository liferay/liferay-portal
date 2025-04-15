/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Tomas Polesovsky
 * @author Raymond Augé
 */
public class AuthTokenWhitelistUtil {

	public static boolean isOriginCSRFWhitelisted(
		long companyId, String origin) {

		for (AuthTokenWhitelist authTokenWhitelist : _authTokenWhitelists) {
			if (authTokenWhitelist.isOriginCSRFWhitelisted(companyId, origin)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isPortletCSRFWhitelisted(
		HttpServletRequest httpServletRequest, Portlet portlet) {

		for (AuthTokenWhitelist authTokenWhitelist : _authTokenWhitelists) {
			if (authTokenWhitelist.isPortletCSRFWhitelisted(
					httpServletRequest, portlet)) {

				return true;
			}
		}

		return false;
	}

	public static boolean isPortletInvocationWhitelisted(
		HttpServletRequest httpServletRequest, Portlet portlet) {

		for (AuthTokenWhitelist authTokenWhitelist : _authTokenWhitelists) {
			if (authTokenWhitelist.isPortletInvocationWhitelisted(
					httpServletRequest, portlet)) {

				return true;
			}
		}

		return false;
	}

	public static boolean isPortletURLCSRFWhitelisted(
		LiferayPortletURL liferayPortletURL) {

		for (AuthTokenWhitelist authTokenWhitelist : _authTokenWhitelists) {
			if (authTokenWhitelist.isPortletURLCSRFWhitelisted(
					liferayPortletURL)) {

				return true;
			}
		}

		return false;
	}

	public static boolean isPortletURLPortletInvocationWhitelisted(
		LiferayPortletURL liferayPortletURL) {

		for (AuthTokenWhitelist authTokenWhitelist : _authTokenWhitelists) {
			if (authTokenWhitelist.isPortletURLPortletInvocationWhitelisted(
					liferayPortletURL)) {

				return true;
			}
		}

		return false;
	}

	public static boolean isValidSharedSecret(String sharedSecret) {
		for (AuthTokenWhitelist authTokenWhitelist : _authTokenWhitelists) {
			if (authTokenWhitelist.isValidSharedSecret(sharedSecret)) {
				return true;
			}
		}

		return false;
	}

	private static final ServiceTrackerList<AuthTokenWhitelist>
		_authTokenWhitelists = ServiceTrackerListFactory.open(
			SystemBundleUtil.getBundleContext(), AuthTokenWhitelist.class);

}