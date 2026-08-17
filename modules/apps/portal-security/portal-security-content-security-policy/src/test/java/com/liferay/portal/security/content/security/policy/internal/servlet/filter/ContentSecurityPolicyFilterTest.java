/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.content.security.policy.internal.servlet.filter;

import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.security.content.security.policy.internal.configuration.ContentSecurityPolicyConfiguration;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Antonio Ortega
 */
public class ContentSecurityPolicyFilterTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_contentSecurityPolicyFilter = new ContentSecurityPolicyFilter();
	}

	@Test
	public void testIsExcludedURIPath() {
		_testIsExcludedURIPath(
			new String[] {"/group"}, false, null, "/c/portal/layout");
		_testIsExcludedURIPath(
			new String[] {"/group"}, false, "/web/mysite/home",
			"/c/portal/layout");
		_testIsExcludedURIPath(
			new String[] {"/group"}, true, "/group/guest/home",
			"/c/portal/layout");
		_testIsExcludedURIPath(
			new String[] {"/group"}, true, null, "/group/guest/home");
		_testIsExcludedURIPath(new String[0], true, null, "/GROUP/guest/home");
	}

	@Test
	public void testIsFilterEnabledSkipsAlreadyFilteredRequest() {
		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(
				ContentSecurityPolicyFilter.SKIP_FILTER)
		).thenReturn(
			Boolean.TRUE
		);

		Assert.assertFalse(
			_contentSecurityPolicyFilter.isFilterEnabled(
				httpServletRequest, Mockito.mock(HttpServletResponse.class)));
	}

	private void _testIsExcludedURIPath(
		String[] excludedPaths, boolean excludedURIPath,
		String forwardRequestURI, String requestURI) {

		ContentSecurityPolicyConfiguration contentSecurityPolicyConfiguration =
			Mockito.mock(ContentSecurityPolicyConfiguration.class);

		Mockito.when(
			contentSecurityPolicyConfiguration.excludedPaths()
		).thenReturn(
			excludedPaths
		);

		HttpServletRequest httpServletRequest = Mockito.mock(
			HttpServletRequest.class);

		Mockito.when(
			httpServletRequest.getAttribute(
				JavaConstants.JAKARTA_SERVLET_FORWARD_REQUEST_URI)
		).thenReturn(
			forwardRequestURI
		);

		Mockito.when(
			httpServletRequest.getRequestURI()
		).thenReturn(
			requestURI
		);

		Assert.assertEquals(
			excludedURIPath,
			ReflectionTestUtil.invoke(
				_contentSecurityPolicyFilter, "_isExcludedURIPath",
				new Class<?>[] {
					ContentSecurityPolicyConfiguration.class,
					HttpServletRequest.class
				},
				contentSecurityPolicyConfiguration, httpServletRequest));
	}

	private ContentSecurityPolicyFilter _contentSecurityPolicyFilter;

}