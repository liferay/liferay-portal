/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.servlet.filters.virtualhost.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.struts.LastPath;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.servlet.filters.virtualhost.VirtualHostFilter;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Zsolt Oláh
 */
@RunWith(Arquillian.class)
public class VirtualHostFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws PortalException {
		_layoutSet = _layoutSetLocalService.getLayoutSet(
			TestPropsValues.getGroupId(), false);
	}

	@Before
	public void setUp() {
		_portalUtil.setPortal(
			new PortalImpl() {

				@Override
				public String getPathContext() {
					return GetterUtil.getString(_pathContext);
				}

				@Override
				public String getPathProxy() {
					return GetterUtil.getString(_pathProxy);
				}

			});
	}

	@After
	public void tearDown() {
		_portalUtil.setPortal(_portal);

		_virtualHostFilter.destroy();
	}

	@Test
	public void testProcessFilterForwardedURL() {
		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"COMPANY_DEFAULT_HOME_URL", StringPool.SLASH)) {

			Assert.assertNotEquals(
				StringPool.SLASH, _getForwardedURL(StringPool.SLASH));
		}
	}

	@Test
	public void testProcessFilterForwardedURLForLanguageIdWithoutTrailingSlash() {
		Assert.assertEquals(
			_getForwardedURL("/en-US/"), _getForwardedURL("/en-US"));
	}

	@Test
	public void testProcessFilterLastPath() {
		_testProcessFilterLastPath(
			_PATH_PROXY + _PATH_CONTEXT, _PATH_PROXY,
			_PATH_CONTEXT + _LAST_PATH);
		_testProcessFilterLastPath(_PATH_PROXY, StringPool.BLANK, _LAST_PATH);
		_testProcessFilterLastPath(_PATH_PROXY, _PATH_PROXY, _LAST_PATH);
	}

	private String _getForwardedURL(String requestURI) {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(requestURI);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_virtualHostFilter.init(new MockFilterConfig());

		ReflectionTestUtil.invoke(
			_virtualHostFilter, "processFilter",
			new Class<?>[] {
				HttpServletRequest.class, HttpServletResponse.class,
				FilterChain.class
			},
			mockHttpServletRequest, mockHttpServletResponse,
			new MockFilterChain());

		return mockHttpServletResponse.getForwardedUrl();
	}

	private String _getLastPath(String requestURI) {
		MockHttpServletRequest mockHttpServletRequest =
			_getMockHttpServletRequest(requestURI);

		_virtualHostFilter.init(new MockFilterConfig());

		ReflectionTestUtil.invoke(
			_virtualHostFilter, "processFilter",
			new Class<?>[] {
				HttpServletRequest.class, HttpServletResponse.class,
				FilterChain.class
			},
			mockHttpServletRequest, new MockHttpServletResponse(),
			new MockFilterChain());

		LastPath lastPath = (LastPath)mockHttpServletRequest.getAttribute(
			WebKeys.LAST_PATH);

		if (lastPath != null) {
			return lastPath.getPath();
		}

		return StringPool.BLANK;
	}

	private MockHttpServletRequest _getMockHttpServletRequest(
		String requestURI) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.VIRTUAL_HOST_LAYOUT_SET, _layoutSet);
		mockHttpServletRequest.setRequestURI(requestURI);

		return mockHttpServletRequest;
	}

	private void _testProcessFilterLastPath(
		String pathContext, String pathProxy, String requestURI) {

		_pathContext = pathContext;
		_pathProxy = pathProxy;

		Assert.assertEquals(_LAST_PATH, _getLastPath(requestURI));
	}

	private static final String _LAST_PATH =
		VirtualHostFilterTest._PATH_PROXY + "_last_path";

	private static final String _PATH_CONTEXT = "/context";

	private static final String _PATH_PROXY = "/proxy";

	private static LayoutSet _layoutSet;

	@Inject
	private static LayoutSetLocalService _layoutSetLocalService;

	private String _pathContext;
	private String _pathProxy;

	@Inject
	private Portal _portal;

	@Inject
	private PortalUtil _portalUtil;

	private final VirtualHostFilter _virtualHostFilter =
		new VirtualHostFilter();

}