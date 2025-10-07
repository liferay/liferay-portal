/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.content.security.policy.internal.configuration;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Iván Zaera Avellón
 */
public class ContentSecurityPolicyConfigurationUtil {

	public static ContentSecurityPolicyConfiguration
		getContentSecurityPolicyConfiguration(
			HttpServletRequest httpServletRequest) {

		return (ContentSecurityPolicyConfiguration)
			httpServletRequest.getAttribute(
				ContentSecurityPolicyConfigurationUtil.class.getName());
	}

	public static ContentSecurityPolicyConfiguration
		setContentSecurityPolicyConfiguration(
			ConfigurationProvider configurationProvider,
			HttpServletRequest httpServletRequest, Portal portal) {

		ContentSecurityPolicyConfiguration contentSecurityPolicyConfiguration;

		try {
			long groupId = portal.getScopeGroupId(httpServletRequest);

			if (groupId > 0) {
				contentSecurityPolicyConfiguration =
					configurationProvider.getGroupConfiguration(
						ContentSecurityPolicyConfiguration.class, groupId);
			}
			else {
				contentSecurityPolicyConfiguration =
					configurationProvider.getCompanyConfiguration(
						ContentSecurityPolicyConfiguration.class,
						portal.getCompanyId(httpServletRequest));
			}
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}

		httpServletRequest.setAttribute(
			ContentSecurityPolicyConfigurationUtil.class.getName(),
			contentSecurityPolicyConfiguration);

		return contentSecurityPolicyConfiguration;
	}

}