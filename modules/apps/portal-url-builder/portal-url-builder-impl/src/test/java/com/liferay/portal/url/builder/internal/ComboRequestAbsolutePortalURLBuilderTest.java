/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.ComboRequestAbsolutePortalURLBuilder;

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
public class ComboRequestAbsolutePortalURLBuilderTest
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

		_comboRequestAbsolutePortalURLBuilder =
			_absolutePortalURLBuilder.forComboRequest();
	}

	@After
	public void tearDown() {
		super.tearDown();
	}

	@Test
	public void test() {
		_comboRequestAbsolutePortalURLBuilder.addFile("/file.js");
		_comboRequestAbsolutePortalURLBuilder.addFile("/path/to/file2.js");

		Assert.assertEquals(
			_RESULTS[index], _comboRequestAbsolutePortalURLBuilder.build());
	}

	@Test
	public void testAddFileHonorsOrdering() {
		_comboRequestAbsolutePortalURLBuilder.addFile("/jquery/jquery.min.js");
		_comboRequestAbsolutePortalURLBuilder.addFile("/jquery/init.js");
		_comboRequestAbsolutePortalURLBuilder.addFile("/jquery/ajax.js");

		Assert.assertEquals(
			_RESULTS_ADD_FILE_HONORS_ORDERING[index],
			_comboRequestAbsolutePortalURLBuilder.build());
	}

	@Test
	public void testAddFileRemovesQueryString() {
		_comboRequestAbsolutePortalURLBuilder.addFile("/file.js?t=123");
		_comboRequestAbsolutePortalURLBuilder.addFile(
			"/path/to/file2.js?lang=es_ES&t=123");

		Assert.assertEquals(
			_RESULTS_ADD_FILE_REMOVES_QUERY_STRING[index],
			_comboRequestAbsolutePortalURLBuilder.build());
	}

	@Test
	public void testTimestamp() {
		_comboRequestAbsolutePortalURLBuilder.addFile("/file.js");
		_comboRequestAbsolutePortalURLBuilder.addFile("/path/to/file2.js");

		_comboRequestAbsolutePortalURLBuilder.setTimestamp(13);

		Assert.assertEquals(
			_RESULTS_TIMESTAMP[index],
			_comboRequestAbsolutePortalURLBuilder.build());
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
		"/combo?minifierType=js&t=0&/file.js&/path/to/file2.js",
		"/proxy/combo?minifierType=js&t=0&/file.js&/path/to/file2.js",
		"/context/combo?minifierType=js&t=0&/file.js&/path/to/file2.js",
		"/proxy/context/combo?minifierType=js&t=0&/file.js&/path/to/file2.js",
		"/combo?minifierType=js&t=0&/file.js&/path/to/file2.js"
	};

	private static final String[] _RESULTS_ADD_FILE_HONORS_ORDERING = {
		"/combo?minifierType=js&t=0&/jquery/jquery.min.js&/jquery/init.js" +
			"&/jquery/ajax.js",
		"/proxy/combo?minifierType=js&t=0&/jquery/jquery.min.js" +
			"&/jquery/init.js&/jquery/ajax.js",
		"/context/combo?minifierType=js&t=0&/jquery/jquery.min.js" +
			"&/jquery/init.js&/jquery/ajax.js",
		"/proxy/context/combo?minifierType=js&t=0&/jquery/jquery.min.js" +
			"&/jquery/init.js&/jquery/ajax.js",
		"/combo?minifierType=js&t=0&/jquery/jquery.min.js&/jquery/init.js" +
			"&/jquery/ajax.js"
	};

	private static final String[] _RESULTS_ADD_FILE_REMOVES_QUERY_STRING = {
		"/combo?minifierType=js&t=0&/file.js&/path/to/file2.js",
		"/proxy/combo?minifierType=js&t=0&/file.js&/path/to/file2.js",
		"/context/combo?minifierType=js&t=0&/file.js&/path/to/file2.js",
		"/proxy/context/combo?minifierType=js&t=0&/file.js&/path/to/file2.js",
		"/combo?minifierType=js&t=0&/file.js&/path/to/file2.js"
	};

	private static final String[] _RESULTS_TIMESTAMP = {
		"/combo?minifierType=js&t=13&/file.js&/path/to/file2.js",
		"/proxy/combo?minifierType=js&t=13&/file.js&/path/to/file2.js",
		"/context/combo?minifierType=js&t=13&/file.js&/path/to/file2.js",
		"/proxy/context/combo?minifierType=js&t=13&/file.js&/path/to/file2.js",
		"/combo?minifierType=js&t=13&/file.js&/path/to/file2.js"
	};

	private AbsolutePortalURLBuilder _absolutePortalURLBuilder;
	private ComboRequestAbsolutePortalURLBuilder
		_comboRequestAbsolutePortalURLBuilder;

}