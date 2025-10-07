/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.petra.string.CharPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.Collections;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 * @author Raymond Augé
 */
public class InterruptedPortletRequestWhitelistUtil {

	public static boolean isPortletInvocationWhitelisted(
		long companyId, String portletId, String strutsAction) {

		if (_portletInvocationWhitelist.contains(portletId)) {
			return true;
		}

		if (Validator.isNotNull(strutsAction) &&
			_portletInvocationWhitelistActions.contains(strutsAction) &&
			_isValidStrutsAction(companyId, portletId, strutsAction)) {

			return true;
		}

		return false;
	}

	public static Set<String> resetPortletInvocationWhitelist() {
		_portletInvocationWhitelist = SetUtil.fromArray(
			PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST);

		if (_portletInvocationWhitelist.isEmpty()) {
			_portletInvocationWhitelist = Collections.emptySet();
		}
		else {
			_portletInvocationWhitelist = Collections.unmodifiableSet(
				_portletInvocationWhitelist);
		}

		return _portletInvocationWhitelist;
	}

	public static Set<String> resetPortletInvocationWhitelistActions() {
		_portletInvocationWhitelistActions = SetUtil.fromArray(
			PropsValues.PORTLET_INTERRUPTED_REQUEST_WHITELIST_ACTIONS);

		if (_portletInvocationWhitelistActions.isEmpty()) {
			_portletInvocationWhitelistActions = Collections.emptySet();
		}
		else {
			_portletInvocationWhitelistActions = Collections.unmodifiableSet(
				_portletInvocationWhitelistActions);
		}

		return _portletInvocationWhitelistActions;
	}

	private static boolean _isValidStrutsAction(
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

	private static final Log _log = LogFactoryUtil.getLog(
		InterruptedPortletRequestWhitelistUtil.class);

	private static Set<String> _portletInvocationWhitelist;
	private static Set<String> _portletInvocationWhitelistActions;

	static {
		resetPortletInvocationWhitelist();
		resetPortletInvocationWhitelistActions();
	}

}