/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.access.control;

import com.liferay.portal.kernel.security.access.control.AccessControlUtil;
import com.liferay.portal.kernel.security.access.control.AccessControlled;
import com.liferay.portal.kernel.security.access.control.BaseAccessControlPolicy;
import com.liferay.portal.kernel.security.auth.AccessControlContext;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Method;

import java.util.Map;
import java.util.Set;

/**
 * @author Tomas Polesovsky
 * @author Igor Spasic
 * @author Michael C. Han
 * @author Raymond Augé
 */
public class AllowedHostsAccessControlPolicy extends BaseAccessControlPolicy {

	@Override
	public void onServiceRemoteAccess(
			Method method, Object[] arguments,
			AccessControlled accessControlled)
		throws SecurityException {

		if (!accessControlled.hostAllowedValidationEnabled()) {
			return;
		}

		AccessControlContext accessControlContext =
			AccessControlUtil.getAccessControlContext();

		if (accessControlContext == null) {
			return;
		}

		Map<String, Object> settings = accessControlContext.getSettings();

		int serviceDepth = (Integer)settings.get(
			AccessControlContext.Settings.SERVICE_DEPTH.toString());

		if (serviceDepth > 1) {
			return;
		}

		HttpServletRequest httpServletRequest =
			accessControlContext.getRequest();

		String hostsAllowedString = MapUtil.getString(
			accessControlContext.getSettings(), "hosts.allowed");

		String[] hostsAllowed = StringUtil.split(hostsAllowedString);

		Set<String> hostsAllowedSet = SetUtil.fromArray(hostsAllowed);

		if (!AccessControlUtil.isAccessAllowed(
				httpServletRequest, hostsAllowedSet)) {

			throw new SecurityException(
				"Access denied for " + httpServletRequest.getRemoteAddr());
		}
	}

}