/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.BrowserModuleAbsolutePortalURLBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import org.mockito.Mockito;

import org.osgi.framework.Bundle;

/**
 * @author Iván Zaera Avellón
 */
@RunWith(Parameterized.class)
public class BrowserModuleAbsolutePortalURLBuilderTest
	extends BaseAbsolutePortalURLBuilderTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Parameterized.Parameters(name = "{0}: cdnHost={1}, context={2}, proxy={3}")
	public static Collection<Object[]> data() {
		return Arrays.asList(
			new Object[][] {
				{0, false, false, false}, {1, false, false, true},
				{2, false, true, false}, {3, false, true, true},
				{4, true, false, false}
			});
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();

		_absolutePortalURLBuilder = new AbsolutePortalURLBuilderImpl(
			mockCacheHelper(), mockHashedFilesRegistry(),
			mockPortal(context, proxy, cdnHost), mockHttpServletRequest());

		Bundle bundle = Mockito.mock(Bundle.class);

		Dictionary<String, String> headers = new Hashtable<>();

		headers.put("Web-ContextPath", "/bundle");

		Mockito.when(
			bundle.getHeaders(StringPool.BLANK)
		).thenReturn(
			headers
		);

		_browserModuleAbsolutePortalURLBuilder =
			_absolutePortalURLBuilder.forBrowserModule(
				"/o/js/resolved-module/frontend-js-web@1.0.0/index");
	}

	@After
	public void tearDown() {
		super.tearDown();
	}

	@Test
	public void test() {
		Assert.assertEquals(
			_RESULTS[index], _browserModuleAbsolutePortalURLBuilder.build());
	}

	@Parameterized.Parameter(1)
	public boolean cdnHost;

	@Parameterized.Parameter(2)
	public boolean context;

	@Parameterized.Parameter
	public int index;

	@Parameterized.Parameter(3)
	public boolean proxy;

	private static final String[] _RESULTS = {
		"/o/js/resolved-module/frontend-js-web@1.0.0/index",
		"/proxy/o/js/resolved-module/frontend-js-web@1.0.0/index",
		"/context/o/js/resolved-module/frontend-js-web@1.0.0/index",
		"/proxy/context/o/js/resolved-module/frontend-js-web@1.0.0/index",
		"/o/js/resolved-module/frontend-js-web@1.0.0/index"
	};

	private AbsolutePortalURLBuilder _absolutePortalURLBuilder;
	private BrowserModuleAbsolutePortalURLBuilder
		_browserModuleAbsolutePortalURLBuilder;

}