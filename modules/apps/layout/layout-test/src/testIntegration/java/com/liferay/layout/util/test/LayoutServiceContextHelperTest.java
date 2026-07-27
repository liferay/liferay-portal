/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.util.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.layout.util.LayoutServiceContextHelper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextThreadLocal;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Georgel Pop
 */
@RunWith(Arquillian.class)
public class LayoutServiceContextHelperTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_layout = LayoutTestUtil.addTypeContentLayout(_group);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());

		ServiceContextThreadLocal.pushServiceContext(_serviceContext);
	}

	@After
	public void tearDown() throws Exception {
		ServiceContextThreadLocal.popServiceContext();
	}

	@Test
	@TestInfo({"LPD-79722", "LPD-99386"})
	public void testGetServiceContextAutoCloseable() throws Exception {
		try (AutoCloseable autoCloseable =
				_layoutServiceContextHelper.getServiceContextAutoCloseable(
					_layout)) {

			ServiceContext serviceContext =
				ServiceContextThreadLocal.getServiceContext();

			HttpServletRequest httpServletRequest = serviceContext.getRequest();

			Assert.assertNotNull(httpServletRequest.getLocale());

			List<Locale> locales = Collections.list(
				httpServletRequest.getLocales());

			Assert.assertFalse(locales.isEmpty());

			Assert.assertEquals(
				HttpMethods.GET, httpServletRequest.getMethod());
			Assert.assertEquals(
				StringPool.SLASH, httpServletRequest.getRequestURI());
			Assert.assertEquals("http", httpServletRequest.getScheme());
			Assert.assertNotNull(httpServletRequest.getContextPath());
			Assert.assertNotNull(httpServletRequest.getServletContext());
			Assert.assertNotNull(httpServletRequest.getSession());
			Assert.assertEquals(0, httpServletRequest.getCookies().length);

			Map<String, String[]> parameterMap =
				httpServletRequest.getParameterMap();

			Assert.assertTrue(parameterMap.isEmpty());
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	private Layout _layout;

	@Inject
	private LayoutServiceContextHelper _layoutServiceContextHelper;

	private ServiceContext _serviceContext;

}