/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.security.auth.BaseAuthTokenWhitelist;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Tomas Polesovsky
 */
public class StrutsPortletAuthTokenWhitelist extends BaseAuthTokenWhitelist {

	@Override
	public boolean isPortletCSRFWhitelisted(
		HttpServletRequest httpServletRequest, Portlet portlet) {

		String portletId = portlet.getPortletId();

		String namespace = PortalUtil.getPortletNamespace(portletId);

		String strutsAction = httpServletRequest.getParameter(
			namespace.concat("struts_action"));

		if (Validator.isNull(strutsAction)) {
			return false;
		}

		Set<String> portletCSRFWhitelist =
			_portletCSRFWhitelistDCLSingleton.getSingleton(
				this::_createPortletCSRFWhitelist);

		if (portletCSRFWhitelist.contains(strutsAction) &&
			isValidStrutsAction(
				portlet.getCompanyId(),
				PortletIdCodec.decodePortletName(portletId), strutsAction)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isPortletInvocationWhitelisted(
		HttpServletRequest httpServletRequest, Portlet portlet) {

		String portletId = portlet.getPortletId();

		String namespace = PortalUtil.getPortletNamespace(portletId);

		String strutsAction = httpServletRequest.getParameter(
			namespace.concat("struts_action"));

		if (Validator.isNull(strutsAction)) {
			strutsAction = httpServletRequest.getParameter("struts_action");
		}

		if (Validator.isNull(strutsAction)) {
			return false;
		}

		Set<String> portletInvocationWhitelist =
			_portletInvocationWhitelistDCLSingleton.getSingleton(
				this::_createPortletInvocationWhitelist);

		if (portletInvocationWhitelist.contains(strutsAction) &&
			isValidStrutsAction(
				portlet.getCompanyId(), portletId, strutsAction)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isPortletURLCSRFWhitelisted(
		LiferayPortletURL liferayPortletURL) {

		String strutsAction = liferayPortletURL.getParameter("struts_action");

		if (Validator.isBlank(strutsAction)) {
			return false;
		}

		Set<String> portletCSRFWhitelist =
			_portletCSRFWhitelistDCLSingleton.getSingleton(
				this::_createPortletCSRFWhitelist);

		if (portletCSRFWhitelist.contains(strutsAction)) {
			long plid = liferayPortletURL.getPlid();

			Layout layout = LayoutLocalServiceUtil.fetchLayout(plid);

			if (layout == null) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to load layout " + plid);
				}

				return false;
			}

			String rootPortletId = PortletIdCodec.decodePortletName(
				liferayPortletURL.getPortletId());

			if (isValidStrutsAction(0, rootPortletId, strutsAction)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isPortletURLPortletInvocationWhitelisted(
		LiferayPortletURL liferayPortletURL) {

		String strutsAction = liferayPortletURL.getParameter("struts_action");

		if (Validator.isBlank(strutsAction)) {
			return false;
		}

		Set<String> portletInvocationWhitelist =
			_portletInvocationWhitelistDCLSingleton.getSingleton(
				this::_createPortletInvocationWhitelist);

		if (portletInvocationWhitelist.contains(strutsAction)) {
			long plid = liferayPortletURL.getPlid();

			Layout layout = LayoutLocalServiceUtil.fetchLayout(plid);

			if (layout == null) {
				if (_log.isDebugEnabled()) {
					_log.debug("Unable to load layout " + plid);
				}

				return false;
			}

			if (isValidStrutsAction(
					0, liferayPortletURL.getPortletId(), strutsAction)) {

				return true;
			}
		}

		return false;
	}

	protected boolean isValidStrutsAction(
		long companyId, String portletId, String strutsAction) {

		try {
			Portlet portlet = PortletLocalServiceUtil.getPortletById(
				companyId, portletId);

			if (portlet == null) {
				return false;
			}

			String strutsPath = strutsAction.substring(
				1, strutsAction.lastIndexOf(CharPool.SLASH));

			if (strutsPath.equals(portlet.getStrutsPath()) ||
				strutsPath.equals(portlet.getParentStrutsPath())) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}
		}

		return false;
	}

	private Set<String> _createPortletCSRFWhitelist() {
		Set<String> portletCSRFWhitelist = Collections.newSetFromMap(
			new ConcurrentHashMap<>());

		registerPortalProperty(PropsKeys.AUTH_TOKEN_IGNORE_ACTIONS);

		trackWhitelistServices(
			PropsKeys.AUTH_TOKEN_IGNORE_ACTIONS, portletCSRFWhitelist);

		return portletCSRFWhitelist;
	}

	private Set<String> _createPortletInvocationWhitelist() {
		Set<String> portletInvocationWhitelist = Collections.newSetFromMap(
			new ConcurrentHashMap<>());

		registerPortalProperty(
			PropsKeys.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_ACTIONS);

		trackWhitelistServices(
			PropsKeys.PORTLET_ADD_DEFAULT_RESOURCE_CHECK_WHITELIST_ACTIONS,
			portletInvocationWhitelist);

		return portletInvocationWhitelist;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		StrutsPortletAuthTokenWhitelist.class);

	private final DCLSingleton<Set<String>> _portletCSRFWhitelistDCLSingleton =
		new DCLSingleton<>();
	private final DCLSingleton<Set<String>>
		_portletInvocationWhitelistDCLSingleton = new DCLSingleton<>();

}