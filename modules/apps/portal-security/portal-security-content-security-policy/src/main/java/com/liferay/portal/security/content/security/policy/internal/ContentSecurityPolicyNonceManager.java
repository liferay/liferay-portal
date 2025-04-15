/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.content.security.policy.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.security.SecureRandom;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.security.content.security.policy.internal.configuration.ContentSecurityPolicyConfiguration;
import com.liferay.portal.security.content.security.policy.internal.configuration.ContentSecurityPolicyConfigurationUtil;
import com.liferay.portal.util.PropsValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = ContentSecurityPolicyNonceManager.class)
public class ContentSecurityPolicyNonceManager {

	public void cleanUpNonce(HttpServletRequest httpServletRequest) {
		httpServletRequest = _portal.getOriginalServletRequest(
			httpServletRequest);

		httpServletRequest.removeAttribute(_NONCE);

		_threadLocal.remove();
	}

	public String getNonce(HttpServletRequest httpServletRequest) {
		String nonce = _threadLocal.get();

		if (nonce != null) {
			return nonce;
		}

		if (httpServletRequest == null) {
			return StringPool.BLANK;
		}

		return GetterUtil.getString(httpServletRequest.getAttribute(_NONCE));
	}

	public String setNonce(HttpServletRequest httpServletRequest) {
		String nonce = null;

		httpServletRequest = _portal.getOriginalServletRequest(
			httpServletRequest);

		ContentSecurityPolicyConfiguration contentSecurityPolicyConfiguration =
			ContentSecurityPolicyConfigurationUtil.
				getContentSecurityPolicyConfiguration(httpServletRequest);

		if (!contentSecurityPolicyConfiguration.enabled()) {
			nonce = StringPool.BLANK;
		}
		else if (PropsValues.JAVASCRIPT_SINGLE_PAGE_APPLICATION_ENABLED) {
			HttpSession httpSession = httpServletRequest.getSession();

			nonce = (String)httpSession.getAttribute(_NONCE);

			if (nonce == null) {
				synchronized (httpSession) {
					nonce = (String)httpSession.getAttribute(_NONCE);

					if (nonce == null) {
						nonce = _generateNonce();

						httpSession.setAttribute(_NONCE, nonce);
					}
				}
			}
		}
		else {
			nonce = _generateNonce();
		}

		httpServletRequest.setAttribute(_NONCE, nonce);

		_threadLocal.set(nonce);

		return nonce;
	}

	private String _generateNonce() {
		SecureRandom secureRandom = new SecureRandom();

		byte[] bytes = new byte[16];

		secureRandom.nextBytes(bytes);

		return Base64.encode(bytes);
	}

	private static final String _NONCE =
		ContentSecurityPolicyNonceManager.class.getName() + "#NONCE";

	@Reference
	private Portal _portal;

	private final ThreadLocal<String> _threadLocal = new ThreadLocal<>();

}