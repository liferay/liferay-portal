/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.jaxrs.container.request.filter;

import com.liferay.osb.faro.util.FaroThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.vulcan.internal.http.VulcanRequestForwarderHttpServletRequestWrapper;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ResourceInfo;

import java.lang.reflect.Method;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Leslie Wong
 */
public class OriginContainerRequestFilterTest {

	@After
	public void tearDown() {
		FaroThreadLocal.setOperationId(null);
		FaroThreadLocal.setOrigin(null);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(null);
	}

	@Test
	public void testFilterWithForwardedRequest() throws Exception {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.when(
			portal.getLocale(Mockito.any(HttpServletRequest.class))
		).thenReturn(
			LocaleUtil.US
		);

		portalUtil.setPortal(portal);

		ReflectionTestUtil.setFieldValue(
			_originContainerRequestFilter, "_httpServletRequest",
			new VulcanRequestForwarderHttpServletRequestWrapper(
				null, null, null, _httpServletRequest,
				RandomTestUtil.randomString(), RandomTestUtil.randomString(),
				null));

		Method method = Object.class.getMethod("toString");

		Mockito.when(
			_resourceInfo.getResourceMethod()
		).thenReturn(
			method
		);

		ReflectionTestUtil.setFieldValue(
			_originContainerRequestFilter, "_resourceInfo", _resourceInfo);

		_originContainerRequestFilter.filter(_containerRequestContext);

		Assert.assertEquals("toString", FaroThreadLocal.getOperationId());
		Assert.assertEquals("mcp", FaroThreadLocal.getOrigin());
	}

	@Test
	public void testFilterWithoutForwardedRequest() {
		ReflectionTestUtil.setFieldValue(
			_originContainerRequestFilter, "_httpServletRequest",
			_httpServletRequest);

		FaroThreadLocal.setOperationId(RandomTestUtil.randomString());

		_originContainerRequestFilter.filter(_containerRequestContext);

		Assert.assertNull(FaroThreadLocal.getOperationId());
		Assert.assertEquals("rest-client", FaroThreadLocal.getOrigin());
	}

	private final ContainerRequestContext _containerRequestContext =
		Mockito.mock(ContainerRequestContext.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final OriginContainerRequestFilter _originContainerRequestFilter =
		new OriginContainerRequestFilter();
	private final ResourceInfo _resourceInfo = Mockito.mock(ResourceInfo.class);

}