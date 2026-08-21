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

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
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
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ClientCredentialsOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Gregory Amerson
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
@TestPropertySource("LiferayOAuth2ClientConfigurationDefaultTest.properties")
@WebMvcTest
public class LiferayOAuth2ClientConfigurationDefaultTest {

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

	@Before
	public void setUp() {
		ClientCredentialsOAuth2AuthorizedClientProvider
			clientCredentialsOAuth2AuthorizedClientProvider =
				new ClientCredentialsOAuth2AuthorizedClientProvider();

		_oAuth2AccessTokenResponseClient = Mockito.mock(
			OAuth2AccessTokenResponseClient.class);

		clientCredentialsOAuth2AuthorizedClientProvider.
			setAccessTokenResponseClient(_oAuth2AccessTokenResponseClient);

		_authorizedClientServiceOAuth2AuthorizedClientManager.
			setAuthorizedClientProvider(
				clientCredentialsOAuth2AuthorizedClientProvider);
	}

	@Test
	public void testExtraRegistrations() {
		InMemoryClientRegistrationRepository
			inMemoryClientRegistrationRepository =
				(InMemoryClientRegistrationRepository)
					_clientRegistrationRepository;

		Assert.assertNull(
			inMemoryClientRegistrationRepository.findByRegistrationId("extra"));

		List<ClientRegistration> clientRegistrations = new ArrayList<>();

		inMemoryClientRegistrationRepository.forEach(clientRegistrations::add);

		Assert.assertEquals(
			clientRegistrations.toString(), 2, clientRegistrations.size());

		InMemoryReactiveClientRegistrationRepository
			inMemoryReactiveClientRegistrationRepository =
				(InMemoryReactiveClientRegistrationRepository)
					_reactiveClientRegistrationRepository;

		Assert.assertNull(
			inMemoryReactiveClientRegistrationRepository.findByRegistrationId(
				"extra"
			).block());

		clientRegistrations = new ArrayList<>();

		inMemoryReactiveClientRegistrationRepository.forEach(
			clientRegistrations::add);

		Assert.assertEquals(
			clientRegistrations.toString(), 2, clientRegistrations.size());
	}

	@Test
	public void testGetAuthorizationFailure() {
		expectedException.expect(IllegalArgumentException.class);
		expectedException.expectMessage(
			"Could not find ClientRegistration with id 'none'");

		_liferayOAuth2AccessTokenManager.getAuthorization("none");
	}

	@Test
	public void testGetAuthorizationSuccess() {
		OAuth2AccessTokenResponse oAuth2AccessTokenResponse =
			OAuth2AccessTokenResponse.withToken(
				"token"
			).tokenType(
				OAuth2AccessToken.TokenType.BEARER
			).build();

		BDDMockito.given(
			_oAuth2AccessTokenResponseClient.getTokenResponse(
				ArgumentMatchers.any())
		).willReturn(
			oAuth2AccessTokenResponse
		);

		OAuth2AccessToken oAuth2AccessToken =
			oAuth2AccessTokenResponse.getAccessToken();

		String expected = "Bearer " + oAuth2AccessToken.getTokenValue();

		Assert.assertEquals(
			expected,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"foo-able-headless-server"));
		Assert.assertEquals(
			expected,
			_liferayOAuth2AccessTokenManager.getAuthorization(
				"foo-baker-headless-server"));
	}

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private static ClientAndServer _clientAndServer;
	private static MockedStatic<JWSAlgorithmFamilyJWSKeySelector> _mockedStatic;

	@Autowired
	private AuthorizedClientServiceOAuth2AuthorizedClientManager
		_authorizedClientServiceOAuth2AuthorizedClientManager;

	@Autowired
	private ClientRegistrationRepository _clientRegistrationRepository;

	@Autowired
	private LiferayOAuth2AccessTokenManager _liferayOAuth2AccessTokenManager;

	private OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest>
		_oAuth2AccessTokenResponseClient;

	@Autowired
	private ReactiveClientRegistrationRepository
		_reactiveClientRegistrationRepository;

}