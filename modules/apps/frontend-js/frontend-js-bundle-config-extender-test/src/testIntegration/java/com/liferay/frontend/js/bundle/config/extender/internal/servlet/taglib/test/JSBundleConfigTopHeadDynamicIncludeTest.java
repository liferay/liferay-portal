/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.bundle.config.extender.internal.servlet.taglib.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Servlet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Shuyang Zhou
 */
@RunWith(Arquillian.class)
public class JSBundleConfigTopHeadDynamicIncludeTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testInclude() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, new ThemeDisplay());

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_dynamicInclude.include(
			mockHttpServletRequest, mockHttpServletResponse, null);

		Matcher matcher = _pattern.matcher(
			mockHttpServletResponse.getContentAsString());

		Assert.assertTrue(matcher.matches());

		long lastModified = GetterUtil.getLong(matcher.group(1));

		mockHttpServletResponse = _service(lastModified);

		Assert.assertEquals(
			"immutable, max-age=31536000, public",
			mockHttpServletResponse.getHeader(HttpHeaders.CACHE_CONTROL));

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(
			content.contains(
				"var MODULE_MAIN='@liferay/frontend-js-react-web@"));

		mockHttpServletResponse = _service(lastModified - 1);

		Assert.assertEquals(
			"no-cache",
			mockHttpServletResponse.getHeader(HttpHeaders.CACHE_CONTROL));
		Assert.assertEquals(
			content, mockHttpServletResponse.getContentAsString());
	}

	private MockHttpServletResponse _service(long lastModified)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("t", String.valueOf(lastModified));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_servlet.service(mockHttpServletRequest, mockHttpServletResponse);

		return mockHttpServletResponse;
	}

	private static final Pattern _pattern = Pattern.compile(
		"<script data-senna-track=\"temporary\" " +
			"src=\"/o/js_bundle_config\\?t=(\\d+)\" " +
				"type=\"text/javascript\"></script>\\s*");

	@Inject(
		filter = "component.name=com.liferay.frontend.js.bundle.config.extender.internal.servlet.taglib.JSBundleConfigTopHeadDynamicInclude"
	)
	private DynamicInclude _dynamicInclude;

	@Inject(
		filter = "component.name=com.liferay.frontend.js.bundle.config.extender.internal.JSBundleConfigServlet"
	)
	private Servlet _servlet;

}