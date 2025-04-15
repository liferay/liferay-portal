/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.content.security.policy;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Iván Zaera Avellón
 */
public class ContentSecurityPolicyNonceProviderUtil {

	public static ContentSecurityPolicyNonceProvider
		getContentSecurityPolicyNonceProvider() {

		return _serviceTracker.getService();
	}

	/**
	 * @see ContentSecurityPolicyNonceProvider#getNonce(HttpServletRequest)
	 */
	public static String getNonce(HttpServletRequest httpServletRequest) {
		ContentSecurityPolicyNonceProvider contentSecurityPolicyNonceProvider =
			getContentSecurityPolicyNonceProvider();

		if (contentSecurityPolicyNonceProvider == null) {
			_log.error("Content security policy nonce provider is null");

			return StringPool.BLANK;
		}

		return contentSecurityPolicyNonceProvider.getNonce(httpServletRequest);
	}

	public static String getNonceAttribute(
		HttpServletRequest httpServletRequest) {

		String nonce = getNonce(httpServletRequest);

		if (Validator.isNull(nonce)) {
			return StringPool.BLANK;
		}

		return " nonce=\"" + nonce + StringPool.QUOTE;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ContentSecurityPolicyNonceProviderUtil.class);

	private static final ServiceTracker
		<ContentSecurityPolicyNonceProvider, ContentSecurityPolicyNonceProvider>
			_serviceTracker =
				new ServiceTracker
					<ContentSecurityPolicyNonceProvider,
					 ContentSecurityPolicyNonceProvider>(
						 SystemBundleUtil.getBundleContext(),
						 ContentSecurityPolicyNonceProvider.class, null) {

					{
						open();
					}
				};

}