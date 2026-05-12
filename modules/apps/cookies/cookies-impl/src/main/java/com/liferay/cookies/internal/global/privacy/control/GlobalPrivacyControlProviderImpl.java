/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.cookies.internal.global.privacy.control;

import com.liferay.cookies.configuration.CookiesConfigurationProvider;
import com.liferay.cookies.global.privacy.control.GlobalPrivacyControlProvider;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christian Moura
 */
@Component(service = GlobalPrivacyControlProvider.class)
public class GlobalPrivacyControlProviderImpl
	implements GlobalPrivacyControlProvider {

	@Override
	public boolean isEnabled(HttpServletRequest httpServletRequest) {
		ExtendedObjectClassDefinition.Scope scope =
			ExtendedObjectClassDefinition.Scope.COMPANY;
		long scopePK = _portal.getCompanyId(httpServletRequest);

		try {
			long groupId = _portal.getScopeGroupId(httpServletRequest);

			if (groupId > 0) {
				scope = ExtendedObjectClassDefinition.Scope.GROUP;
				scopePK = groupId;
			}
		}
		catch (PortalException portalException) {
			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}
		}

		if (_cookiesConfigurationProvider.isCookiesPreferenceHandlingEnabled(
				scope, scopePK) &&
			_cookiesConfigurationProvider.
				isCookiesPreferenceHandlingGlobalPrivacyControlEnabled(
					scope, scopePK)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isSignalActive(HttpServletRequest httpServletRequest) {
		if (!isEnabled(httpServletRequest)) {
			return false;
		}

		Enumeration<String> enumeration = httpServletRequest.getHeaders(
			"Sec-GPC");

		while (enumeration.hasMoreElements()) {
			if (Objects.equals(
					StringUtil.trim(enumeration.nextElement()), "1")) {

				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		GlobalPrivacyControlProviderImpl.class);

	@Reference
	private CookiesConfigurationProvider _cookiesConfigurationProvider;

	@Reference
	private Portal _portal;

}