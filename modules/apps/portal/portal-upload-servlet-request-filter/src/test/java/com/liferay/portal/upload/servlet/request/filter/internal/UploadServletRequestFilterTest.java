/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.upload.servlet.request.filter.internal;

import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.portlet.PortletInstanceFactoryUtil;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Jürgen Kappler
 */
public class UploadServletRequestFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_uploadServletRequestFilter = new UploadServletRequestFilter();

		_portal = Mockito.mock(Portal.class);

		ReflectionTestUtil.setFieldValue(
			_uploadServletRequestFilter, "_portal", _portal);

		_portletLocalService = Mockito.mock(PortletLocalService.class);

		ReflectionTestUtil.setFieldValue(
			_uploadServletRequestFilter, "_portletLocalService",
			_portletLocalService);

		FilterConfig filterConfig = Mockito.mock(FilterConfig.class);

		Mockito.when(
			filterConfig.getServletContext()
		).thenReturn(
			Mockito.mock(ServletContext.class)
		);

		ReflectionTestUtil.setFieldValue(
			_uploadServletRequestFilter, "_filterConfig", filterConfig);
	}

	@Test
	public void testProcessFilterDoesNotUsePortletMultipartLocation()
		throws Exception {

		String portletId = RandomTestUtil.randomString();

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getParameter("p_p_id")
		).thenReturn(
			portletId
		);

		Portlet portlet = Mockito.mock(Portlet.class);

		Mockito.when(
			_portletLocalService.getPortletById(
				ArgumentMatchers.anyLong(), ArgumentMatchers.eq(portletId))
		).thenReturn(
			portlet
		);

		int fileSizeThreshold = RandomTestUtil.randomInt();

		Mockito.when(
			portlet.getMultipartFileSizeThreshold()
		).thenReturn(
			fileSizeThreshold
		);

		Mockito.when(
			portlet.getMultipartLocation()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		UploadServletRequest uploadServletRequest = Mockito.mock(
			UploadServletRequest.class);

		Mockito.when(
			_portal.getUploadServletRequest(
				httpServletRequest, fileSizeThreshold, null)
		).thenReturn(
			uploadServletRequest
		);

		try (MockedStatic<PortletInstanceFactoryUtil> mockedStatic =
				Mockito.mockStatic(PortletInstanceFactoryUtil.class)) {

			InvokerPortlet invokerPortlet = Mockito.mock(InvokerPortlet.class);

			LiferayPortletConfig liferayPortletConfig = Mockito.mock(
				LiferayPortletConfig.class);

			Mockito.when(
				invokerPortlet.getPortletConfig()
			).thenReturn(
				liferayPortletConfig
			);

			mockedStatic.when(
				() -> PortletInstanceFactoryUtil.create(
					ArgumentMatchers.eq(portlet), ArgumentMatchers.any())
			).thenReturn(
				invokerPortlet
			);

			_uploadServletRequestFilter.processFilter(
				httpServletRequest, Mockito.mock(HttpServletResponse.class),
				Mockito.mock(FilterChain.class));
		}

		Mockito.verify(
			_portal
		).getUploadServletRequest(
			httpServletRequest, fileSizeThreshold, null
		);
	}

	private Portal _portal;
	private PortletLocalService _portletLocalService;
	private UploadServletRequestFilter _uploadServletRequestFilter;

}