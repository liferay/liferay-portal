/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.internal.servlet.filter.auto.login;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.security.sso.openid.connect.OpenIdConnectAuthenticationHandler;
import com.liferay.portal.security.sso.openid.connect.internal.session.manager.OfflineOpenIdConnectSessionManager;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Pedro Victor Silvestre
 */
public class OpenIdConnectAutoLoginFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_setUpOfflineOpenIdConnectionManager();
		_setUpOpenIdConnectAuthenticationHandler();
		_setUpPortal();
	}

	@Test
	public void testProcessFilter() throws Exception {
		HttpServletResponse httpServletResponse = Mockito.mock(
			HttpServletResponse.class);

		_openIdConnectAutoLoginFilter.processFilter(
			_mockHttpServletRequest(), httpServletResponse,
			Mockito.mock(FilterChain.class));

		Mockito.verify(
			httpServletResponse, Mockito.times(2)
		).sendRedirect(
			Mockito.any()
		);

		Mockito.verify(
			_portal, Mockito.never()
		).sendError(
			Mockito.any(), Mockito.any(HttpServletRequest.class),
			Mockito.any(HttpServletResponse.class)
		);
	}

	private HttpServletRequest _mockHttpServletRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		HttpSession httpSession = Mockito.mock(HttpSession.class);

		Mockito.when(
			httpServletRequest.getSession(false)
		).thenReturn(
			httpSession
		);

		Mockito.when(
			httpSession.getAttribute(Mockito.any())
		).thenReturn(
			RandomTestUtil.randomString()
		);

		return httpServletRequest;
	}

	private void _setUpOfflineOpenIdConnectionManager() {
		OfflineOpenIdConnectSessionManager offlineOpenIdConnectSessionManager =
			Mockito.mock(OfflineOpenIdConnectSessionManager.class);

		Mockito.when(
			offlineOpenIdConnectSessionManager.isOpenIdConnectSession(
				Mockito.any())
		).thenReturn(
			false
		);

		ReflectionTestUtil.setFieldValue(
			_openIdConnectAutoLoginFilter,
			"_offlineOpenIdConnectSessionManager",
			offlineOpenIdConnectSessionManager);
	}

	private void _setUpOpenIdConnectAuthenticationHandler() throws Exception {
		OpenIdConnectAuthenticationHandler openIdConnectAuthenticationHandler =
			Mockito.mock(OpenIdConnectAuthenticationHandler.class);

		Mockito.doThrow(
			IllegalArgumentException.class
		).when(
			openIdConnectAuthenticationHandler
		).processAuthenticationResponse(
			Mockito.any(), Mockito.any(), Mockito.any()
		);

		ReflectionTestUtil.setFieldValue(
			_openIdConnectAutoLoginFilter,
			"_openIdConnectAuthenticationHandler",
			openIdConnectAuthenticationHandler);
	}

	private void _setUpPortal() {
		_portal = Mockito.mock(Portal.class);

		Mockito.doAnswer(
			invocation -> new String[] {
				invocation.getArgument(0, String.class), StringPool.BLANK
			}
		).when(
			_portal
		).stripURLAnchor(
			Mockito.anyString(), Mockito.anyString()
		);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	private final OpenIdConnectAutoLoginFilter _openIdConnectAutoLoginFilter =
		new OpenIdConnectAutoLoginFilter();
	private Portal _portal;

}