/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.content.security.policy.internal;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Antonio Ortega
 */
public class ContentSecurityPolicyNonceManagerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ReflectionTestUtil.setFieldValue(
			_contentSecurityPolicyNonceManager, "_portal", _portal);
	}

	@Test
	public void testSetNonceReusesExistingRequestNonce() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			_portal.getOriginalServletRequest(httpServletRequest)
		).thenReturn(
			httpServletRequest
		);

		String nonce = "existing-nonce";

		Mockito.when(
			httpServletRequest.getAttribute(_NONCE)
		).thenReturn(
			nonce
		);

		Assert.assertEquals(
			nonce,
			_contentSecurityPolicyNonceManager.setNonce(httpServletRequest));
		Assert.assertEquals(
			nonce,
			_contentSecurityPolicyNonceManager.getNonce(httpServletRequest));

		Mockito.verify(
			httpServletRequest, Mockito.never()
		).getSession();
	}

	private static final String _NONCE =
		ContentSecurityPolicyNonceManager.class.getName() + "#NONCE";

	private final ContentSecurityPolicyNonceManager
		_contentSecurityPolicyNonceManager =
			new ContentSecurityPolicyNonceManager();
	private final Portal _portal = Mockito.mock(Portal.class);

}