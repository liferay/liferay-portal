/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.servlet.profile;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.opensaml.integration.internal.BaseSamlTestCase;
import com.liferay.saml.opensaml.integration.internal.provider.CachingChainingMetadataResolver;
import com.liferay.saml.opensaml.integration.internal.util.OpenSamlUtil;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.model.impl.SamlSpIdpConnectionImpl;
import com.liferay.saml.persistence.service.SamlSpAuthRequestLocalService;
import com.liferay.saml.persistence.service.SamlSpAuthRequestLocalServiceUtil;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalServiceUtil;
import com.liferay.saml.persistence.service.SamlSpSessionLocalService;
import com.liferay.saml.persistence.service.SamlSpSessionLocalServiceUtil;

import java.io.ByteArrayOutputStream;

import java.util.Base64;
import java.util.HashMap;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.apache.xml.security.algorithms.JCEMapper;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.crypto.SigningUtil;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mika Koivisto
 */
public class XMLSecurityTest extends BaseSamlTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_cookiesManagerServiceRegistration = _bundleContext.registerService(
			CookiesManager.class, Mockito.mock(CookiesManager.class), null);
	}

	@AfterClass
	public static void tearDownClass() {
		_cookiesManagerServiceRegistration.unregister();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		SamlSpAuthRequestLocalService samlSpAuthRequestLocalService =
			getMockPortletService(
				SamlSpAuthRequestLocalServiceUtil.class,
				SamlSpAuthRequestLocalService.class);

		SamlSpSessionLocalService samlSpSessionLocalService =
			getMockPortletService(
				SamlSpSessionLocalServiceUtil.class,
				SamlSpSessionLocalService.class);
		SamlSpIdpConnectionLocalService samlSpIdpConnectionLocalService =
			getMockPortletService(
				SamlSpIdpConnectionLocalServiceUtil.class,
				SamlSpIdpConnectionLocalService.class);

		SamlSpIdpConnection samlSpIdpConnection = new SamlSpIdpConnectionImpl();

		Mockito.when(
			samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				Mockito.eq(COMPANY_ID), Mockito.eq(IDP_ENTITY_ID))
		).thenReturn(
			samlSpIdpConnection
		);

		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "credentialResolver", credentialResolver);
		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "localEntityManager",
			keyStoreLocalEntityManager);
		ReflectionTestUtil.setFieldValue(_webSsoProfileImpl, "portal", portal);
		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "samlBindingProvider", samlBindingProvider);
		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "samlProviderConfigurationHelper",
			samlProviderConfigurationHelper);
		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "_samlSpAuthRequestLocalService",
			samlSpAuthRequestLocalService);
		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "samlSpIdpConnectionLocalService",
			samlSpIdpConnectionLocalService);
		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "samlSpSessionLocalService",
			samlSpSessionLocalService);

		_webSsoProfileImpl.activate(
			SystemBundleUtil.getBundleContext(), new HashMap<String, Object>());

		ReflectionTestUtil.invoke(
			_webSsoProfileImpl.getMetadataResolver(), "doDestroy",
			new Class<?>[0]);

		CachingChainingMetadataResolver cachingChainingMetadataResolver =
			(CachingChainingMetadataResolver)
				_webSsoProfileImpl.getMetadataResolver();

		cachingChainingMetadataResolver.addMetadataResolver(
			new MockMetadataResolver());

		prepareServiceProvider(SP_ENTITY_ID);
	}

	@Test(expected = MessageDecodingException.class)
	public void testMaximumMessageSizeExceeded() throws Exception {
		String redirectURL = _getAuthnRequestRedirectURL();

		String authnRequestXML = OpenSamlUtil.marshall(
			getAuthnRequest(redirectURL));

		Mockito.when(
			samlProviderConfiguration.maximumMessageSize()
		).thenReturn(
			100
		);

		decodeAuthnRequest(authnRequestXML, redirectURL);
	}

	@Test(expected = MessageDecodingException.class)
	public void testXMLBombBillionLaughs() throws Exception {
		String redirectURL = _getAuthnRequestRedirectURL();

		String authnRequestXML = OpenSamlUtil.marshall(
			getAuthnRequest(redirectURL));

		String samlMessageXML = authnRequestXML.substring(38);

		authnRequestXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE " +
				"saml2p:AuthnRequest [\n<!ENTITY lol1 \"lol\">\n";

		for (int i = 2; i < 10; i++) {
			String lol = "";

			for (int j = 0; j < 10; j++) {
				lol += "&lol" + (i - 1) + ";";
			}

			authnRequestXML = StringBundler.concat(
				authnRequestXML, " <!ENTITY lol", i, " \"", lol, "\">\n");
		}

		authnRequestXML += "]>" + samlMessageXML;

		authnRequestXML =
			authnRequestXML.substring(0, authnRequestXML.length() - 22) +
				"&lol9;</saml2p:AuthnRequest>";

		decodeAuthnRequest(authnRequestXML, redirectURL);
	}

	@Test(expected = MessageDecodingException.class)
	public void testXMLBombQuadraticBlowup() throws Exception {
		String redirectURL = _getAuthnRequestRedirectURL();

		String authnRequestXML = OpenSamlUtil.marshall(
			getAuthnRequest(redirectURL));

		String samlMessageXML = authnRequestXML.substring(38);

		authnRequestXML =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE " +
				"saml2p:AuthnRequest [\n<!ENTITY a \"";

		for (int i = 0; i < 5000; i++) {
			authnRequestXML += "aaaaaaaaaa";
		}

		authnRequestXML = authnRequestXML + "\">\n";

		authnRequestXML += "]>" + samlMessageXML;

		String entity = "";

		for (int i = 0; i < 5000; i++) {
			entity += "&a;&a;&a;&a;&a;&a;&a;&a;&a;&a;";
		}

		authnRequestXML =
			authnRequestXML.substring(0, authnRequestXML.length() - 22) +
				entity + "</saml2p:AuthnRequest>";

		decodeAuthnRequest(authnRequestXML, redirectURL);
	}

	@Test(expected = MessageDecodingException.class)
	public void testXXEGeneralEntities1() throws Exception {
		String redirectURL = _getAuthnRequestRedirectURL();

		String authnRequestXML = OpenSamlUtil.marshall(
			getAuthnRequest(redirectURL));

		authnRequestXML = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE ",
			"saml2p:AuthnRequest [\n<!ENTITY xxe SYSTEM ",
			"\"http://localhost/saml-request\">\n]>",
			authnRequestXML.substring(38));

		authnRequestXML =
			authnRequestXML.substring(0, authnRequestXML.length() - 22) +
				"&xxe;</saml2p:AuthnRequest>";

		decodeAuthnRequest(authnRequestXML, redirectURL);
	}

	@Test(expected = MessageDecodingException.class)
	public void testXXEGeneralEntities2() throws Exception {
		String redirectURL = _getAuthnRequestRedirectURL();

		String authnRequestXML = OpenSamlUtil.marshall(
			getAuthnRequest(redirectURL));

		authnRequestXML = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE ",
			"saml2p:AuthnRequest [\n<!ENTITY xxe PUBLIC \"SOME//PUBLIC//ID\" ",
			"\"http://localhost/saml-request\">\n]>",
			authnRequestXML.substring(38));

		authnRequestXML =
			authnRequestXML.substring(0, authnRequestXML.length() - 22) +
				"&xxe;</saml2p:AuthnRequest>";

		decodeAuthnRequest(authnRequestXML, redirectURL);
	}

	@Test(expected = MessageDecodingException.class)
	public void testXXEParameterEntities() throws Exception {
		String redirectURL = _getAuthnRequestRedirectURL();

		String authnRequestXML = OpenSamlUtil.marshall(
			getAuthnRequest(redirectURL));

		decodeAuthnRequest(
			StringBundler.concat(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE ",
				"saml2p:AuthnRequest [\n<!ENTITY % remote SYSTEM ",
				"\"http://localhost/saml-request\">\n%remote;\n]>",
				authnRequestXML.substring(38)),
			redirectURL);
	}

	protected void decodeAuthnRequest(
			String authnRequestXML, String redirectURL)
		throws Exception {

		Credential credential = _webSsoProfileImpl.getSigningCredential();

		String encodedAuthnRequest = _encodeRequest(authnRequestXML);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(redirectURL);

		mockHttpServletRequest.removeParameter("SAMLRequest");
		mockHttpServletRequest.removeParameter("Signature");

		mockHttpServletRequest.setParameter("SAMLRequest", encodedAuthnRequest);

		String signature = _generateSignature(
			credential, mockHttpServletRequest.getParameter("SigAlg"),
			mockHttpServletRequest.getQueryString());

		mockHttpServletRequest.setParameter("Signature", signature);

		_webSsoProfileImpl.decodeAuthnRequest(
			mockHttpServletRequest, new MockHttpServletResponse());
	}

	protected AuthnRequest getAuthnRequest(String redirectURL)
		throws Exception {

		SamlSsoRequestContext samlSsoRequestContext =
			_webSsoProfileImpl.decodeAuthnRequest(
				getMockHttpServletRequest(redirectURL),
				new MockHttpServletResponse());

		MessageContext<?> samlMessageContext =
			samlSsoRequestContext.getSAMLMessageContext();

		InOutOperationContext<AuthnRequest, ?> inOutOperationContext =
			samlMessageContext.getSubcontext(InOutOperationContext.class);

		MessageContext<AuthnRequest> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		return inboundMessageContext.getMessage();
	}

	private String _encodeRequest(String requestXML) throws Exception {
		Base64.Encoder encoder = _getEncoder();

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		Deflater deflater = new Deflater(Deflater.DEFLATED, true);

		DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(
			byteArrayOutputStream, deflater);

		deflaterOutputStream.write(requestXML.getBytes("UTF-8"));

		deflaterOutputStream.finish();

		return encoder.encodeToString(byteArrayOutputStream.toByteArray());
	}

	private String _generateSignature(
			Credential signingCredential, String algorithmURI,
			String queryString)
		throws Exception {

		Base64.Encoder encoder = _getEncoder();

		byte[] signatureBytes = SigningUtil.sign(
			signingCredential, JCEMapper.translateURItoJCEID(algorithmURI),
			false, queryString.getBytes("UTF-8"));

		return encoder.encodeToString(signatureBytes);
	}

	private String _getAuthnRequestRedirectURL() throws Exception {
		SamlSpIdpConnectionLocalService samlSpIdpConnectionLocalService =
			getMockPortletService(
				SamlSpIdpConnectionLocalServiceUtil.class,
				SamlSpIdpConnectionLocalService.class);

		SamlSpIdpConnection samlSpIdpConnection = new SamlSpIdpConnectionImpl();

		samlSpIdpConnection.setSamlIdpEntityId(IDP_ENTITY_ID);

		Mockito.when(
			samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				Mockito.eq(COMPANY_ID), Mockito.eq(IDP_ENTITY_ID))
		).thenReturn(
			samlSpIdpConnection
		);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(LOGIN_URL);

		mockHttpServletRequest.setAttribute(
			SamlWebKeys.SAML_SP_IDP_CONNECTION, samlSpIdpConnection);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webSsoProfileImpl.doSendAuthnRequest(
			mockHttpServletRequest, mockHttpServletResponse, RELAY_STATE);

		return mockHttpServletResponse.getRedirectedUrl();
	}

	private Base64.Encoder _getEncoder() {
		Base64.Encoder encoder = Base64.getEncoder();

		return encoder.withoutPadding();
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static ServiceRegistration<CookiesManager>
		_cookiesManagerServiceRegistration;

	private final WebSsoProfileImpl _webSsoProfileImpl =
		new WebSsoProfileImpl();

}