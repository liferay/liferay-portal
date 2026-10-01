/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.auth;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.security.auth.http.HttpAuthorizationHeader;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.util.FIPSModeTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Base64;
import com.liferay.portal.kernel.util.DigesterUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.auth.http.HttpAuthManagerUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalInstances;

import jakarta.servlet.http.HttpServletRequest;

import java.security.MessageDigest;

import java.util.Map;

import jodd.util.StringUtil;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Tomas Polesovsky
 */
public class HttpAuthManagerUtilTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGenerateChallengeDigest() throws Exception {
		Company company = Mockito.mock(Company.class);

		Mockito.when(
			company.getKey()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		try (MockedStatic<CompanyLocalServiceUtil>
				companyLocalServiceUtilMockedStatic = Mockito.mockStatic(
					CompanyLocalServiceUtil.class);
			MockedStatic<PortalInstances> portalInstancesMockedStatic =
				Mockito.mockStatic(PortalInstances.class)) {

			companyLocalServiceUtilMockedStatic.when(
				() -> CompanyLocalServiceUtil.getCompanyById(Mockito.anyLong())
			).thenReturn(
				company
			);

			portalInstancesMockedStatic.when(
				() -> PortalInstances.getCompanyId(
					Mockito.any(HttpServletRequest.class))
			).thenReturn(
				RandomTestUtil.randomLong()
			);

			FIPSModeTestUtil.assertAlgorithmSwitch(
				DigesterUtil.MD5, MessageDigest.class, DigesterUtil.SHA_256,
				MessageDigest::getInstance,
				() -> {
					MockHttpServletResponse mockHttpServletResponse =
						new MockHttpServletResponse();

					HttpAuthManagerUtil.generateChallenge(
						new MockHttpServletRequest(), mockHttpServletResponse,
						new HttpAuthorizationHeader(
							HttpAuthorizationHeader.SCHEME_DIGEST));

					String wwwAuthenticate = mockHttpServletResponse.getHeader(
						HttpHeaders.WWW_AUTHENTICATE);

					Assert.assertEquals(
						wwwAuthenticate, PropsValues.FIPS_ENABLED,
						wwwAuthenticate.contains("algorithm=\"SHA-256\""));
				});
		}
	}

	@Test
	public void testLPS88011() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			HttpHeaders.AUTHORIZATION,
			"Basic " + Base64.encode("test@liferay.com:te:st".getBytes()));

		HttpAuthorizationHeader httpAuthorizationHeader =
			HttpAuthManagerUtil.parse(mockHttpServletRequest);

		Assert.assertEquals(
			"te:st",
			httpAuthorizationHeader.getAuthParameter(
				HttpAuthorizationHeader.AUTH_PARAMETER_NAME_PASSWORD));
	}

	@Test
	public void testParseBasic() {
		_testParseBasic("test@liferay.com:test", "test", "test@liferay.com");
	}

	@Test
	public void testParseBasicNoCredentials() {
		_testParseBasic("test@liferay.com", null, "test@liferay.com");
		_testParseBasic(
			"test@liferay.com:", StringPool.BLANK, "test@liferay.com");
		_testParseBasic(":", StringPool.BLANK, StringPool.BLANK);
	}

	@Test
	public void testParseBasicTrimValues() {
		_testParseBasic(
			" test@liferay.com : test ", "test", "test@liferay.com");
	}

	@Test
	public void testParseBasicURLDecode() {
		_testParseBasic(
			"test%40liferay%253ecom:test%40", "test%40", "test@liferay%3ecom");
	}

	@Test
	public void testParseBasicWithEncodedPercentCharacter() {
		_testParseBasic(
			"test%25test%40liferay%253ecom:test", "test",
			"test%test@liferay%3ecom");
	}

	@Test
	public void testParseBasicWithEncodedPlusCharacter() {
		_testParseBasic(
			"test%20test%40liferay%253ecom:test", "test",
			"test+test@liferay%3ecom");
	}

	@Test
	public void testParseBasicWithPercentCharacter() {
		_testParseBasic(
			"%test%test@liferay.com:test", "test", "%test%test@liferay.com");
	}

	@Test
	public void testParseBasicWithPercentCharacterAndEncoding() {
		_testParseBasic(
			"test%test%40liferay%253ecom:test", "test",
			"test%test@liferay%3ecom");
	}

	@Test
	public void testParseBasicWithPlusCharacter() {
		_testParseBasic(
			"test+test@liferay.com:test", "test", "test+test@liferay.com");
	}

	@Test
	public void testParseDigest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		String[] digestParams = {
			"cnonce=\"0a4f113b\"", "nc=00000001",
			"nonce=\"dcd98b7102dd2f0e8b11d0f600bfb0c093\"",
			"opaque=\"5ccc069c403ebaf9f0171e9517f40e41", "qop=auth",
			"realm=\"testrealm@host.com\"",
			"response=\"6629fae49393a05397450978507c4ef1\"",
			"uri=\"/dir/index.html\"", "username=\"Mufasa\""
		};

		mockHttpServletRequest.addHeader(
			HttpHeaders.AUTHORIZATION,
			"DIGEST " + StringUtil.join(digestParams, ",\n"));

		HttpAuthorizationHeader httpAuthorizationHeader =
			HttpAuthManagerUtil.parse(mockHttpServletRequest);

		Assert.assertEquals(
			HttpAuthorizationHeader.SCHEME_DIGEST,
			httpAuthorizationHeader.getScheme());

		Map<String, String> authParameters =
			httpAuthorizationHeader.getAuthParameters();

		Assert.assertEquals(
			authParameters.toString(), digestParams.length,
			authParameters.size());

		Assert.assertEquals(
			"dcd98b7102dd2f0e8b11d0f600bfb0c093",
			httpAuthorizationHeader.getAuthParameter(
				HttpAuthorizationHeader.AUTH_PARAMETER_NAME_NONCE));

		Assert.assertEquals(
			"testrealm@host.com",
			httpAuthorizationHeader.getAuthParameter(
				HttpAuthorizationHeader.AUTH_PARAMETER_NAME_REALM));

		Assert.assertEquals(
			"/dir/index.html",
			httpAuthorizationHeader.getAuthParameter(
				HttpAuthorizationHeader.AUTH_PARAMETER_NAME_URI));

		Assert.assertEquals(
			"Mufasa",
			httpAuthorizationHeader.getAuthParameter(
				HttpAuthorizationHeader.AUTH_PARAMETER_NAME_USERNAME));
	}

	@Test
	public void testParseNullAuthorization() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		Assert.assertEquals(
			null, HttpAuthManagerUtil.parse(mockHttpServletRequest));

		mockHttpServletRequest = new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			HttpHeaders.AUTHORIZATION, StringPool.BLANK);

		Assert.assertEquals(
			null, HttpAuthManagerUtil.parse(mockHttpServletRequest));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testParseUnsupportedAuthorizationHeader() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			HttpHeaders.AUTHORIZATION, "Unsupported");

		HttpAuthManagerUtil.parse(mockHttpServletRequest);
	}

	private void _testParseBasic(
		String authorization, String expectedPassword,
		String expectedUserName) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(
			HttpHeaders.AUTHORIZATION,
			"Basic " + Base64.encode(authorization.getBytes()));

		HttpAuthorizationHeader httpAuthorizationHeader =
			HttpAuthManagerUtil.parse(mockHttpServletRequest);

		Assert.assertEquals(
			expectedPassword,
			httpAuthorizationHeader.getAuthParameter(
				HttpAuthorizationHeader.AUTH_PARAMETER_NAME_PASSWORD));
		Assert.assertEquals(
			expectedUserName,
			httpAuthorizationHeader.getAuthParameter(
				HttpAuthorizationHeader.AUTH_PARAMETER_NAME_USERNAME));

		Map<String, String> authParameters =
			httpAuthorizationHeader.getAuthParameters();

		Assert.assertEquals(
			authParameters.toString(), 2, authParameters.size());

		Assert.assertEquals(
			HttpAuthorizationHeader.SCHEME_BASIC,
			httpAuthorizationHeader.getScheme());
	}

}