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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.mockserver.client.server.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.mockserver.verify.VerificationTimes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
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
@TestPropertySource("LiferayOAuth2ClientConfigurationExternalTest.properties")
@WebMvcTest
public class LiferayOAuth2ClientConfigurationExternalTest {

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
		new MockServerClient(
			"localhost", 63636
		).verify(
			HttpRequest.request(
			).withMethod(
				"GET"
			).withPath(
				"/o/oauth2/application"
			),
			VerificationTimes.exactly(0)
		);

		_clientAndServer.stop();

		_mockedStatic.close();
	}

	@Test
	public void testFindRegistrationIdForDefaultHeadlessServer() {
		ClientRegistrationRepository clientRegistrationRepository =
			_liferayOAuth2ClientConfiguration.clientRegistrationRepository();

		ClientRegistration clientRegistration =
			clientRegistrationRepository.findByRegistrationId(
				"default-headless-server");

		Assert.assertEquals("123456789", clientRegistration.getClientId());
		Assert.assertEquals("Shibboleth", clientRegistration.getClientSecret());

		ClientRegistration.ProviderDetails providerDetails =
			clientRegistration.getProviderDetails();

		Assert.assertEquals(
			"http://localhost:63636/o/oauth2/token",
			providerDetails.getTokenUri());
	}

	@Test
	public void testFindRegistrationIdForExternalHeadlessServer() {
		ClientRegistrationRepository clientRegistrationRepository =
			_liferayOAuth2ClientConfiguration.clientRegistrationRepository();

		ClientRegistration clientRegistration =
			clientRegistrationRepository.findByRegistrationId(
				"external-headless-server");

		Assert.assertEquals("987654321", clientRegistration.getClientId());
		Assert.assertEquals("htelobbihS", clientRegistration.getClientSecret());

		ClientRegistration.ProviderDetails providerDetails =
			clientRegistration.getProviderDetails();

		Assert.assertEquals(
			"https://external-headless-server.com/oauth2/token",
			providerDetails.getTokenUri());
	}

	private static ClientAndServer _clientAndServer;
	private static MockedStatic<JWSAlgorithmFamilyJWSKeySelector> _mockedStatic;

	@Autowired
	private LiferayOAuth2ClientConfiguration _liferayOAuth2ClientConfiguration;

}