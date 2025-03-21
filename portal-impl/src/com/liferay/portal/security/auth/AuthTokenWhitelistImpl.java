/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.auth.BaseAuthTokenWhitelist;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Raymond Augé
 * @author Tomas Polesovsky
 */
public class AuthTokenWhitelistImpl extends BaseAuthTokenWhitelist {

	@Override
	public boolean isOriginCSRFWhitelisted(long companyId, String origin) {
		for (String whitelistedOrigin :
				_originCSRFWhitelist.getSingleton(
					this::_createOriginCSRFWhitelist)) {

			if (origin.startsWith(whitelistedOrigin)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isPortletCSRFWhitelisted(
		HttpServletRequest httpServletRequest, Portlet portlet) {

		Set<String> portletCSRFWhitelist = _portletCSRFWhitelist.getSingleton(
			this::_createPortletCSRFWhitelist);

		return portletCSRFWhitelist.contains(portlet.getRootPortletId());
	}

	@Override
	public boolean isPortletInvocationWhitelisted(
		HttpServletRequest httpServletRequest, Portlet portlet) {

		Set<String> portletInvocationWhitelist =
			_portletInvocationWhitelist.getSingleton(
				this::_createPortletInvocationWhitelist);

		return portletInvocationWhitelist.contains(portlet.getPortletId());
	}

	@Override
	public boolean isPortletURLCSRFWhitelisted(
		LiferayPortletURL liferayPortletURL) {

		String rootPortletId = PortletIdCodec.decodePortletName(
			liferayPortletURL.getPortletId());

		Set<String> portletCSRFWhitelist = _portletCSRFWhitelist.getSingleton(
			this::_createPortletCSRFWhitelist);

		return portletCSRFWhitelist.contains(rootPortletId);
	}

	@Override
	public boolean isPortletURLPortletInvocationWhitelisted(
		LiferayPortletURL liferayPortletURL) {

		Set<String> portletInvocationWhitelist =
			_portletInvocationWhitelist.getSingleton(
				this::_createPortletInvocationWhitelist);

		return portletInvocationWhitelist.contains(
			liferayPortletURL.getPortletId());
	}

	@Override
	public boolean isValidSharedSecret(String sharedSecret) {
		if (Validator.isNull(sharedSecret) ||
			Validator.isNull(PropsValues.AUTH_TOKEN_SHARED_SECRET)) {

			return false;
		}

		return sharedSecret.equals(
			DigesterUtil.digest(PropsValues.AUTH_TOKEN_SHARED_SECRET));
	}

	private Set<String> _createOriginCSRFWhitelist() {
		Set<String> originCSRFWhitelist = Collections.newSetFromMap(
			new ConcurrentHashMap<>());

		registerPortalProperty(PropsKeys.AUTH_TOKEN_IGNORE_ORIGINS);

		trackWhitelistServices(
			PropsKeys.AUTH_TOKEN_IGNORE_ORIGINS, originCSRFWhitelist);

		return originCSRFWhitelist;
	}

	private Set<String> _createPortletCSRFWhitelist() {
		Set<String> portletCSRFWhitelist = Collections.newSetFromMap(
			new ConcurrentHashMap<>());

		registerPortalProperty(PropsKeys.AUTH_TOKEN_IGNORE_PORTLETS);

		trackWhitelistServices(
			PropsKeys.AUTH_TOKEN_IGNORE_PORTLETS, portletCSRFWhitelist);

		return portletCSRFWhitelist;
	}

	private Set<String> _createPortletInvocationWhitelist() {
		Set<String> portletInvocationWhitelist = Collections.newSetFromMap(
			new ConcurrentHashMap<>());

		registerPortalProperty(
			PropsKeys.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST);

		trackWhitelistServices(
			PropsKeys.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST,
			portletInvocationWhitelist);

		return portletInvocationWhitelist;
	}

	private final DCLSingleton<Set<String>> _originCSRFWhitelist =
		new DCLSingleton<>();
	private final DCLSingleton<Set<String>> _portletCSRFWhitelist =
		new DCLSingleton<>();
	private final DCLSingleton<Set<String>> _portletInvocationWhitelist =
		new DCLSingleton<>();

}