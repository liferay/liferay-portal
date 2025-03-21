/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.content.security.policy.internal;

import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProvider;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = ContentSecurityPolicyNonceProvider.class)
public class ContentSecurityPolicyNonceProviderImpl
	implements ContentSecurityPolicyNonceProvider {

	@Override
	public String getNonce(HttpServletRequest httpServletRequest) {
		return _contentSecurityPolicyNonceManager.getNonce(httpServletRequest);
	}

	@Reference
	private ContentSecurityPolicyNonceManager
		_contentSecurityPolicyNonceManager;

}