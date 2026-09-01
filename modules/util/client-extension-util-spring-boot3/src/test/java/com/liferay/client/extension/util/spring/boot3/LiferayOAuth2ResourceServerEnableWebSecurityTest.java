/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.client.extension.util.spring.boot3;

import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2AccessTokenManager;
import com.liferay.client.extension.util.spring.boot3.client.LiferayOAuth2ClientConfiguration;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;

import java.net.URL;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * @author Gregory Amerson
 * @author Allen Ziegenfus
 */
@ContextConfiguration(
	classes = {
		LiferayOAuth2AccessTokenManager.class,
		LiferayOAuth2ClientConfiguration.class,
		LiferayOAuth2ResourceServerEnableWebSecurity.class,
		LiferayWebMvcConfigurer.class
	}
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(
	"LiferayOAuth2ResourceServerEnableWebSecurityTest.properties"
)
@WebMvcTest
public class LiferayOAuth2ResourceServerEnableWebSecurityTest {

	@BeforeClass
	public static void setUpClass() throws Exception {
		_mockedStatic = Mockito.mockStatic(
			JWSAlgorithmFamilyJWSKeySelector.class);

		_mockedStatic.when(
			(MockedStatic.Verification)
				JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(Mockito.any())
		).thenReturn(
			new JWSAlgorithmFamilyJWSKeySelector<>(
				JWSAlgorithm.Family.RSA,
				new RemoteJWKSet<>(
					new URL("http://localhost:63636/o/oauth2/jwks")))
		);

		_clientAndServer = ClientAndServer.startClientAndServer(63636);

		new MockServerClient(
			"localhost", 63636
		).when(
			HttpRequest.request(
			).withMethod(
				"GET"
			).withPath(
				"/o/oauth2/jwks"
			),
			Times.unlimited()
		).respond(
			HttpResponse.response(
			).withBody(
				JWTAssertionUtil.JWKS
			).withHeader(
				new Header("Content-Type", "application/json")
			).withStatusCode(
				200
			)
		);
	}

	@AfterClass
	public static void tearDownClass() {
		_clientAndServer.stop();

		_mockedStatic.close();
	}

	@Test
	public void testGetClientIdLogMessage() {
		LiferayOAuth2ResourceServerEnableWebSecurity
			liferayOAuth2ResourceServerEnableWebSecurity =
				new LiferayOAuth2ResourceServerEnableWebSecurity();

		MockEnvironment mockEnvironment = new MockEnvironment();

		mockEnvironment.setProperty(
			"test-headless-server.oauth2.headless.server.client.id",
			String.valueOf(UUID.randomUUID()));

		ReflectionTestUtils.setField(
			liferayOAuth2ResourceServerEnableWebSecurity, "_environment",
			mockEnvironment);

		Assert.assertEquals(
			"Unable to get user agent client ID for external reference code " +
				"test-external",
			ReflectionTestUtils.invokeMethod(
				liferayOAuth2ResourceServerEnableWebSecurity,
				"_getClientIdLogMessage", null, "test-external"));
		Assert.assertEquals(
			"Using user agent client ID user-agent-id for external reference " +
				"code test-user-agent",
			ReflectionTestUtils.invokeMethod(
				liferayOAuth2ResourceServerEnableWebSecurity,
				"_getClientIdLogMessage", "user-agent-id", "test-user-agent"));
		Assert.assertNull(
			ReflectionTestUtils.invokeMethod(
				liferayOAuth2ResourceServerEnableWebSecurity,
				"_getClientIdLogMessage", null, "test-headless-server"));
	}

	@Test
	public void testGetJWTWithClientIdFromERC() throws Exception {
		try (CloseableMockServerClient closeableMockServerClient =
				new CloseableMockServerClient("localhost", 63636)) {

			closeableMockServerClient.when(
				HttpRequest.request(
				).withMethod(
					"GET"
				).withPath(
					"/o/oauth2/application"
				).withQueryStringParameter(
					"externalReferenceCode", "foo-baker"
				),
				Times.unlimited()
			).respond(
				HttpResponse.response(
				).withBody(
					"{\"client_id\": \"987654321\"}"
				).withHeader(
					new Header("Content-Type", "application/json")
				).withStatusCode(
					200
				)
			);

			_jwtDecoder.decode(
				JWTAssertionUtil.getJWTWithClientId("987654321"));
		}
	}

	@Test
	public void testGetJWTWithClientIdInProperties() throws Exception {
		_jwtDecoder.decode(JWTAssertionUtil.getJWTWithClientId("123456789"));
	}

	@Test
	public void testGetJWTWithClientIdNotInProperties() throws Exception {
		expectedException.expect(JwtValidationException.class);
		expectedException.expectMessage(
			"An error occurred while attempting to decode the Jwt: The " +
				"client_id does not match");

		_jwtDecoder.decode(JWTAssertionUtil.getJWTWithClientId("987654321"));
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private static ClientAndServer _clientAndServer;
	private static MockedStatic<JWSAlgorithmFamilyJWSKeySelector> _mockedStatic;

	@Autowired
	private JwtDecoder _jwtDecoder;

	private static class CloseableMockServerClient
		extends MockServerClient implements AutoCloseable {

		public CloseableMockServerClient(String remoteHost, int remotePort) {
			super(remoteHost, remotePort);
		}

		@Override
		public void close() {
			stop();
		}

	}

}