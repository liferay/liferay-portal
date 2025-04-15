/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.redirect;

import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author Raymond Augé
 */
public class OAuth2RedirectURIInterpolatorTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testInterpolateRedirectURIsList() {

		// Not secure, default port

		_testInterpolateRedirectURIsList(
			80, "http://localhost/foo", "http://localhost/foo",
			"http://localhost/foo",
			Arrays.asList(
				"http://localhost/foo", "@protocol@://localhost/foo",
				"http://localhost@port-with-colon@/foo"),
			false);

		// Not secure, nondefault port

		_testInterpolateRedirectURIsList(
			8080, "http://localhost/foo", "http://localhost/foo",
			"http://localhost:8080/foo",
			Arrays.asList(
				"http://localhost/foo", "@protocol@://localhost/foo",
				"http://localhost@port-with-colon@/foo"),
			false);

		// Secure, default port

		_testInterpolateRedirectURIsList(
			443, "https://localhost/foo", "https://localhost/foo",
			"https://localhost/foo",
			Arrays.asList(
				"https://localhost/foo", "@protocol@://localhost/foo",
				"https://localhost@port-with-colon@/foo"),
			true);

		// Secure, nondefault port

		_testInterpolateRedirectURIsList(
			6443, "https://localhost/foo", "https://localhost/foo",
			"https://localhost:6443/foo",
			Arrays.asList(
				"https://localhost/foo", "@protocol@://localhost/foo",
				"https://localhost@port-with-colon@/foo"),
			true);

		// Secure, port 80

		_testInterpolateRedirectURIsList(
			80, "https://localhost/foo", "https://localhost/foo",
			"https://localhost/foo",
			Arrays.asList(
				"https://localhost/foo", "@protocol@://localhost/foo",
				"https://localhost@port-with-colon@/foo"),
			true);
	}

	private void _testInterpolateRedirectURIsList(
		int forwardedPort, String interpolatedRedirectURI1,
		String interpolatedRedirectURI2, String interpolatedRedirectURI3,
		List<String> redirectURIsList, boolean secure) {

		Mockito.when(
			_portal.getForwardedPort(_httpServletRequest)
		).then(
			new Answer<Integer>() {

				@Override
				public Integer answer(InvocationOnMock invocation)
					throws Throwable {

					return forwardedPort;
				}

			}
		);

		Mockito.when(
			_portal.isSecure(_httpServletRequest)
		).then(
			new Answer<Boolean>() {

				@Override
				public Boolean answer(InvocationOnMock invocation)
					throws Throwable {

					return secure;
				}

			}
		);

		List<String> interpolatedRedirectURIsList =
			OAuth2RedirectURIInterpolator.interpolateRedirectURIsList(
				_httpServletRequest, redirectURIsList, _portal);

		Assert.assertEquals(
			interpolatedRedirectURI1, interpolatedRedirectURIsList.get(0));
		Assert.assertEquals(
			interpolatedRedirectURI2, interpolatedRedirectURIsList.get(1));
		Assert.assertEquals(
			interpolatedRedirectURI3, interpolatedRedirectURIsList.get(2));
	}

	@Mock
	private HttpServletRequest _httpServletRequest;

	@Mock
	private Portal _portal;

}