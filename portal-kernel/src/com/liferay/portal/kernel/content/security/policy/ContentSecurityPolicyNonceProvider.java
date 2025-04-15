/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.content.security.policy;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Iván Zaera Avellón
 */
public interface ContentSecurityPolicyNonceProvider {

	/**
	 * Get the nonce associated to a request.
	 *
	 * If the request is not available a fallback ThreadLocal managed by the
	 * ContentSecurityPolicyFilter is used instead.
	 *
	 * It is strongly discouraged to make the provider use the ThreadLocal if
	 * the request can be obtained by any mean in the calling code.
	 *
	 * @param httpServletRequest the current request or null if not available
	 * @return the nonce or "" if CSP is not active
	 * @review
	 */
	public String getNonce(HttpServletRequest httpServletRequest);

}