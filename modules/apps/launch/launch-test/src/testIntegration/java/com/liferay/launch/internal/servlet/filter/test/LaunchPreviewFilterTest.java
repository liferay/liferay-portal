/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.internal.servlet.filter.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jose Luis Navarro
 */
@FeatureFlag("LPD-72278")
@RunWith(Arquillian.class)
public class LaunchPreviewFilterTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testProcessFilter() throws Exception {
		MockFilterChain mockFilterChain = new MockFilterChain();
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();
		long previewLaunchSetId = RandomTestUtil.randomLong();

		_processFilter(
			mockFilterChain, mockHttpServletResponse, previewLaunchSetId,
			TestPropsValues.getUser());

		Assert.assertNotNull(mockFilterChain.getRequest());
		Assert.assertNull(mockHttpServletResponse.getRedirectedUrl());

		mockFilterChain = new MockFilterChain();
		mockHttpServletResponse = new MockHttpServletResponse();

		_processFilter(
			mockFilterChain, mockHttpServletResponse, previewLaunchSetId, null);

		Assert.assertNull(mockFilterChain.getRequest());

		String redirectedURL = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertEquals(
			_portal.getPathMain() + "/portal/login",
			HttpComponentsUtil.getPath(redirectedURL));

		Assert.assertEquals(
			"http://localhost/web/guest/home?p_l_mode=preview&" +
				"previewLaunchSetId=" + previewLaunchSetId,
			HttpComponentsUtil.decodeURL(
				HttpComponentsUtil.getParameter(
					redirectedURL, "redirect", false)));
	}

	private void _processFilter(
			FilterChain filterChain,
			MockHttpServletResponse mockHttpServletResponse,
			long previewLaunchSetId, User user)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setParameter("p_l_mode", Constants.PREVIEW);
		mockHttpServletRequest.setParameter(
			"previewLaunchSetId", String.valueOf(previewLaunchSetId));
		mockHttpServletRequest.setQueryString(
			"p_l_mode=preview&previewLaunchSetId=" + previewLaunchSetId);
		mockHttpServletRequest.setRequestURI("/web/guest/home");

		if (user != null) {
			mockHttpServletRequest.setAttribute(WebKeys.USER, user);
		}

		ReflectionTestUtil.invoke(
			_filter, "processFilter",
			new Class<?>[] {
				HttpServletRequest.class, HttpServletResponse.class,
				FilterChain.class
			},
			mockHttpServletRequest, mockHttpServletResponse, filterChain);
	}

	@Inject(filter = "servlet-filter-name=Launch Preview Filter")
	private Filter _filter;

	@Inject
	private Portal _portal;

}