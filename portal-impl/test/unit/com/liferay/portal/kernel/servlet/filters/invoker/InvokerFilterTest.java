/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.filters.invoker;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.servlet.filters.BasePortalFilter;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.tools.ToolDependencies;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;

/**
 * @author Mika Koivisto
 */
public class InvokerFilterTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		ToolDependencies.wireCaches();
	}

	@Test
	public void testGetInvokerFilterChain() throws Exception {
		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(ProxyFactory.newDummyInstance(Portal.class));

		InvokerFilter invokerFilter = new InvokerFilter();

		MockServletContext mockServletContext = new MockServletContext();

		MockFilterConfig mockFilterConfig = new MockFilterConfig(
			mockServletContext, "Include Invoker Filter");

		mockFilterConfig.addInitParameter("dispatcher", "INCLUDE");

		invokerFilter.init(mockFilterConfig);

		List<ServiceRegistration<Filter>> serviceRegistrations =
			new ArrayList<>();

		try {
			BasePortalFilter basePortalFilter = new BasePortalFilter() {
			};

			basePortalFilter.setFilterEnabled(false);

			serviceRegistrations.add(
				_registerFilter(
					basePortalFilter, RandomTestUtil.randomString(),
					mockServletContext.getServletContextName()));

			_getInvokerFilterChain(invokerFilter, "p=1");
			_getInvokerFilterChain(invokerFilter, "p=2");

			PortalCache<String, InvokerFilterChain> portalCache =
				PortalCacheHelperUtil.getPortalCache(
					PortalCacheManagerNames.SINGLE_VM,
					"Include Invoker Filter");

			Assert.assertEquals(
				Collections.singletonList("/test.jsp"), portalCache.getKeys());

			serviceRegistrations.add(
				_registerFilter(
					ProxyFactory.newDummyInstance(Filter.class),
					RandomTestUtil.randomString(),
					mockServletContext.getServletContextName()));

			_getInvokerFilterChain(invokerFilter, "p=1");
			_getInvokerFilterChain(invokerFilter, "p=2");

			List<String> keys = portalCache.getKeys();

			Collections.sort(keys);

			Assert.assertEquals(
				Arrays.asList("/test.jsp", "/test.jsp?p=1", "/test.jsp?p=2"),
				keys);
		}
		finally {
			for (ServiceRegistration<Filter> serviceRegistration :
					serviceRegistrations) {

				serviceRegistration.unregister();
			}

			invokerFilter.destroy();
		}
	}

	@Test
	public void testGetURIWithDoubleSlash() {
		InvokerFilter invokerFilter = new InvokerFilter();

		Assert.assertEquals(
			"/c/portal/login",
			invokerFilter.getURI(
				invokerFilter.getOriginalRequestURI(
					new MockHttpServletRequest(
						HttpMethods.GET,
						"/c///portal/%2e/login;jsessionid=ae01b0f2af." +
							"worker1"))));

		Assert.assertEquals(
			"/c/portal/login",
			invokerFilter.getURI(
				invokerFilter.getOriginalRequestURI(
					new MockHttpServletRequest(
						HttpMethods.GET,
						"/c///portal/%2e/../login;jsessionid=ae01b0f2af." +
							"worker1"))));
	}

	@Test
	public void testGetURIWithJSessionId() {
		InvokerFilter invokerFilter = new InvokerFilter();

		Assert.assertEquals(
			"/c/portal/login",
			invokerFilter.getURI(
				invokerFilter.getOriginalRequestURI(
					new MockHttpServletRequest(
						HttpMethods.GET,
						"/c/portal/login;jsessionid=ae01b0f2af.worker1"))));
	}

	@Test
	public void testLongURLsWithPath() throws Exception {
		testLongURL("/c/portal/login/");
	}

	@Test
	public void testLongURLsWithPathParameters() throws Exception {
		testLongURL("/c/portal/login/;");
	}

	@Test
	public void testLongURLsWithQueryString() throws Exception {
		testLongURL("/c/portal/login?param=");
	}

	protected void testLongURL(String urlPrefix) throws Exception {
		InvokerFilter invokerFilter = new InvokerFilter();

		int invokerFilterUriMaxLength = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.INVOKER_FILTER_URI_MAX_LENGTH));

		char[] chars = new char[invokerFilterUriMaxLength];

		for (int i = 0; i < chars.length; i++) {
			chars[i] = '0';
		}

		String payload = urlPrefix.concat(new String(chars));

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(HttpMethods.GET, payload);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		MockFilterChain mockFilterChain = new MockFilterChain();

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				InvokerFilter.class.getName(), LoggerTestUtil.WARN)) {

			invokerFilter.doFilter(
				mockHttpServletRequest, mockHttpServletResponse,
				mockFilterChain);

			Assert.assertEquals(
				HttpServletResponse.SC_REQUEST_URI_TOO_LONG,
				mockHttpServletResponse.getStatus());

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			String message = logEntry.getMessage();

			Assert.assertTrue(message.startsWith("Rejected " + urlPrefix));
		}
	}

	private void _getInvokerFilterChain(
		InvokerFilter invokerFilter, String queryString) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(HttpMethods.GET, "/test.jsp");

		mockHttpServletRequest.setQueryString(queryString);

		invokerFilter.getInvokerFilterChain(
			mockHttpServletRequest, "/test.jsp", new MockFilterChain());
	}

	private ServiceRegistration<Filter> _registerFilter(
		Filter filter, String filterName, String servletContextName) {

		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		return bundleContext.registerService(
			Filter.class, filter,
			HashMapDictionaryBuilder.<String, Object>put(
				"dispatcher", "INCLUDE"
			).put(
				"init-param.url-regex-ignore-pattern", ".+/ignore/.+"
			).put(
				"servlet-context-name", servletContextName
			).put(
				"servlet-filter-name", filterName
			).put(
				"url-pattern", "*.jsp"
			).build());
	}

}