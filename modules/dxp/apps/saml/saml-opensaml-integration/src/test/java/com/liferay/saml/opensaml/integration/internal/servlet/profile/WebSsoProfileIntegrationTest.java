/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.servlet.profile;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LRUMap;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.opensaml.integration.internal.BaseSamlTestCase;
import com.liferay.saml.opensaml.integration.internal.bootstrap.SecurityConfigurationBootstrap;
import com.liferay.saml.opensaml.integration.internal.identifier.IdentifierGeneratorStrategyFactory;
import com.liferay.saml.opensaml.integration.internal.provider.CachingChainingMetadataResolver;
import com.liferay.saml.opensaml.integration.internal.util.OpenSamlUtil;
import com.liferay.saml.opensaml.integration.internal.util.SamlUtil;
import com.liferay.saml.persistence.model.SamlSpAuthRequest;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.model.impl.SamlSpAuthRequestImpl;
import com.liferay.saml.persistence.model.impl.SamlSpIdpConnectionImpl;
import com.liferay.saml.persistence.service.SamlSpAuthRequestLocalService;
import com.liferay.saml.persistence.service.SamlSpAuthRequestLocalServiceUtil;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalService;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalServiceUtil;
import com.liferay.saml.persistence.service.SamlSpMessageLocalService;
import com.liferay.saml.persistence.service.SamlSpMessageLocalServiceUtil;
import com.liferay.saml.persistence.service.SamlSpSessionLocalService;
import com.liferay.saml.persistence.service.SamlSpSessionLocalServiceUtil;
import com.liferay.saml.runtime.exception.AssertionException;
import com.liferay.saml.runtime.exception.AudienceException;
import com.liferay.saml.runtime.exception.DestinationException;
import com.liferay.saml.runtime.exception.ExpiredException;
import com.liferay.saml.runtime.exception.InResponseToException;
import com.liferay.saml.runtime.exception.IssuerException;
import com.liferay.saml.runtime.exception.SignatureException;
import com.liferay.saml.runtime.exception.SubjectException;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.security.IdentifierGenerationStrategy;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.handler.MessageHandlerException;
import org.opensaml.saml.common.messaging.context.AbstractSAMLEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.messaging.context.SAMLMessageInfoContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSubjectNameIdentifierContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AudienceRestriction;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.NameIDType;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.saml.saml2.core.SubjectConfirmationData;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.signature.Signature;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Mika Koivisto
 * @author Matthew Tambara
 * @author William Newbury
 */
public class WebSsoProfileIntegrationTest extends BaseSamlTestCase {

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

		_samlSpAuthRequestLocalService = getMockPortletService(
			SamlSpAuthRequestLocalServiceUtil.class,
			SamlSpAuthRequestLocalService.class);
		_samlSpSessionLocalService = getMockPortletService(
			SamlSpSessionLocalServiceUtil.class,
			SamlSpSessionLocalService.class);

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
			_samlSpAuthRequestLocalService);
		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "samlSpSessionLocalService",
			_samlSpSessionLocalService);
		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "_samlSpMessageLocalService",
			getMockPortletService(
				SamlSpMessageLocalServiceUtil.class,
				SamlSpMessageLocalService.class));
		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "samlSpIdpConnectionLocalService",
			getMockPortletService(
				SamlSpIdpConnectionLocalServiceUtil.class,
				SamlSpIdpConnectionLocalService.class));
		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "_userLocalService",
			getMockPortletService(
				UserLocalServiceUtil.class, UserLocalService.class));

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

	@Test
	public void testAssertionSignatureAlgorithmIsNegotiated() throws Exception {
		Assertion assertion = OpenSamlUtil.buildAssertion();

		Credential credential = getCredential(IDP_ENTITY_ID);

		MetadataResolver metadataResolver =
			_webSsoProfileImpl.getMetadataResolver();

		EntityDescriptor entityDescriptor = metadataResolver.resolveSingle(
			new CriteriaSet(new EntityIdCriterion(SP_ENTITY_ID)));

		SPSSODescriptor spSSODescriptor = entityDescriptor.getSPSSODescriptor(
			SAMLConstants.SAML20P_NS);

		Extensions extensions = spSSODescriptor.getExtensions();

		List<XMLObject> unknownXMLObjects = extensions.getUnknownXMLObjects();

		Iterator<XMLObject> iterator = unknownXMLObjects.iterator();

		// Peer only allows SHA512

		while (iterator.hasNext()) {
			XMLObject xmlObject = iterator.next();

			if (xmlObject instanceof SigningMethod) {
				SigningMethod signingMethod = (SigningMethod)xmlObject;

				String algorithm = signingMethod.getAlgorithm();

				if (!algorithm.equals(
						"http://www.w3.org/2001/04/xmldsig-more#rsa-sha512")) {

					iterator.remove();
				}
			}
		}

		OpenSamlUtil.signObject(assertion, credential, spSSODescriptor);

		Signature signature = assertion.getSignature();

		Assert.assertEquals(
			"http://www.w3.org/2001/04/xmldsig-more#rsa-sha512",
			signature.getSignatureAlgorithm());

		// Blacklist SHA512 and check that it is not used even though it is the
		// only one signaled by the peer

		ReflectionTestUtil.invoke(
			new SecurityConfigurationBootstrap(), "activate",
			new Class<?>[] {Map.class},
			HashMapBuilder.<String, Object>put(
				"blacklisted.algorithms",
				new String[] {
					"http://www.w3.org/2001/04/xmldsig-more#rsa-sha512"
				}
			).build());

		assertion = OpenSamlUtil.buildAssertion();

		OpenSamlUtil.signObject(assertion, credential, spSSODescriptor);

		signature = assertion.getSignature();

		Assert.assertNotEquals(
			"http://www.w3.org/2001/04/xmldsig-more#rsa-sha512",
			signature.getSignatureAlgorithm());

		// Check that SHA512 was actually negotiated and that the default is
		// different

		assertion = OpenSamlUtil.buildAssertion();

		OpenSamlUtil.signObject(assertion, credential, null);

		signature = assertion.getSignature();

		Assert.assertEquals(
			"http://www.w3.org/2001/04/xmldsig-more#rsa-sha256",
			signature.getSignatureAlgorithm());
	}

	@Test
	public void testAssertionSignatureDefaultAlgorithmIsNotSHA1()
		throws Exception {

		Assertion assertion = OpenSamlUtil.buildAssertion();

		OpenSamlUtil.signObject(assertion, getCredential(IDP_ENTITY_ID), null);

		Signature signature = assertion.getSignature();

		Assert.assertNotEquals(
			"http://www.w3.org/2000/09/xmldsig#rsa-sha1",
			signature.getSignatureAlgorithm());
	}

	@Test
	public void testDecodeAuthnRequestIdpInitiatedSso() throws Exception {
		prepareIdentityProvider(IDP_ENTITY_ID);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(SSO_URL);

		mockHttpServletRequest.setParameter("entityId", SP_ENTITY_ID);
		mockHttpServletRequest.setParameter("RelayState", RELAY_STATE);

		SamlSsoRequestContext samlSsoRequestContext =
			_webSsoProfileImpl.decodeAuthnRequest(
				mockHttpServletRequest, new MockHttpServletResponse());

		_assertAbstractSAMLEntityContext(
			_getMessageContextSubcontext(
				samlSsoRequestContext, SAMLSelfEntityContext.class),
			IDP_ENTITY_ID, IDPSSODescriptor.class);
		_assertAbstractSAMLEntityContext(
			_getMessageContextSubcontext(
				samlSsoRequestContext, SAMLPeerEntityContext.class),
			SP_ENTITY_ID, SPSSODescriptor.class);

		SAMLBindingContext samlBindingContext = _getMessageContextSubcontext(
			samlSsoRequestContext, SAMLBindingContext.class);

		Assert.assertEquals(RELAY_STATE, samlBindingContext.getRelayState());

		Assert.assertTrue(samlSsoRequestContext.isNewSession());
	}

	@Test
	public void testDecodeAuthnRequestIdpInitiatedSsoAfterAuthentication()
		throws Exception {

		prepareIdentityProvider(IDP_ENTITY_ID);

		String idpInitiatedSamlMessageId =
			_webSsoProfileImpl.generateIdentifier(20);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(
				StringBundler.concat(
					SSO_URL, "?entityId=", SP_ENTITY_ID, "&samlMessageId=",
					idpInitiatedSamlMessageId));

		_setSamlSsoRequestContexts(
			mockHttpServletRequest.getSession(),
			new ObjectValuePair<>(
				idpInitiatedSamlMessageId,
				new SamlSsoRequestContext(SP_ENTITY_ID, RELAY_STATE, null)));

		SamlSsoRequestContext samlSsoRequestContext =
			_webSsoProfileImpl.decodeAuthnRequest(
				mockHttpServletRequest, new MockHttpServletResponse());

		_assertAbstractSAMLEntityContext(
			_getMessageContextSubcontext(
				samlSsoRequestContext, SAMLSelfEntityContext.class),
			IDP_ENTITY_ID, IDPSSODescriptor.class);
		_assertAbstractSAMLEntityContext(
			_getMessageContextSubcontext(
				samlSsoRequestContext, SAMLPeerEntityContext.class),
			SP_ENTITY_ID, SPSSODescriptor.class);

		SAMLBindingContext samlBindingContext = _getMessageContextSubcontext(
			samlSsoRequestContext, SAMLBindingContext.class);

		Assert.assertEquals(RELAY_STATE, samlBindingContext.getRelayState());

		Assert.assertTrue(samlSsoRequestContext.isNewSession());
	}

	@Test
	public void testDecodeAuthnRequestIdpInitiatedSsoAfterAuthenticationWithConcurrentAuthnRequest()
		throws Exception {

		prepareIdentityProvider(IDP_ENTITY_ID);

		String idpInitiatedSamlMessageId =
			_webSsoProfileImpl.generateIdentifier(20);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(
				StringBundler.concat(
					SSO_URL, "?entityId=", SP_ENTITY_ID, "&samlMessageId=",
					idpInitiatedSamlMessageId));

		_setSamlSsoRequestContexts(
			mockHttpServletRequest.getSession(),
			new ObjectValuePair<>(
				idpInitiatedSamlMessageId,
				new SamlSsoRequestContext(SP_ENTITY_ID, RELAY_STATE, null)),
			new ObjectValuePair<>(
				_webSsoProfileImpl.generateIdentifier(20),
				new SamlSsoRequestContext(SP_ENTITY_ID, RELAY_STATE, null)));

		SamlSsoRequestContext samlSsoRequestContext =
			_webSsoProfileImpl.decodeAuthnRequest(
				mockHttpServletRequest, new MockHttpServletResponse());

		_assertAbstractSAMLEntityContext(
			_getMessageContextSubcontext(
				samlSsoRequestContext, SAMLSelfEntityContext.class),
			IDP_ENTITY_ID, IDPSSODescriptor.class);
		_assertAbstractSAMLEntityContext(
			_getMessageContextSubcontext(
				samlSsoRequestContext, SAMLPeerEntityContext.class),
			SP_ENTITY_ID, SPSSODescriptor.class);

		_assertSamlSsoRequestContexts(1, mockHttpServletRequest.getSession());

		SAMLBindingContext samlBindingContext = _getMessageContextSubcontext(
			samlSsoRequestContext, SAMLBindingContext.class);

		Assert.assertEquals(RELAY_STATE, samlBindingContext.getRelayState());

		Assert.assertTrue(samlSsoRequestContext.isNewSession());
	}

	@Test
	public void testDecodeAuthnRequestStageAuthenticated() throws Exception {
		SamlSpIdpConnection samlSpIdpConnection = new SamlSpIdpConnectionImpl();

		samlSpIdpConnection.setSamlIdpEntityId(IDP_ENTITY_ID);

		_setUpWebSsoProfilerImpl(samlSpIdpConnection);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(LOGIN_URL);

		mockHttpServletRequest.setAttribute(
			SamlWebKeys.SAML_SP_IDP_CONNECTION, samlSpIdpConnection);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webSsoProfileImpl.doSendAuthnRequest(
			mockHttpServletRequest, mockHttpServletResponse, RELAY_STATE);

		prepareIdentityProvider(IDP_ENTITY_ID);

		mockHttpServletRequest = getMockHttpServletRequest(
			mockHttpServletResponse.getRedirectedUrl());

		SamlSsoRequestContext samlSsoRequestContext =
			_webSsoProfileImpl.decodeAuthnRequest(
				mockHttpServletRequest, new MockHttpServletResponse());

		AtomicReference<String> messageIdAtomicReference =
			new AtomicReference<>();

		_assertInboundMessageContext(
			inboundMessageContext -> {
				SAMLMessageInfoContext samlMessageInfoContext =
					inboundMessageContext.getSubcontext(
						SAMLMessageInfoContext.class, false);

				messageIdAtomicReference.set(
					samlMessageInfoContext.getMessageId());

				Assert.assertNotNull(messageIdAtomicReference.get());
			},
			samlSsoRequestContext);

		mockHttpServletRequest = getMockHttpServletRequest(
			SSO_URL + "?samlMessageId=" + messageIdAtomicReference.get());

		samlSsoRequestContext.setSAMLMessageContext(null);

		_setSamlSsoRequestContexts(
			mockHttpServletRequest.getSession(),
			new ObjectValuePair<>(
				messageIdAtomicReference.get(), samlSsoRequestContext));

		Mockito.when(
			portal.getUserId(Mockito.any(MockHttpServletRequest.class))
		).thenReturn(
			1000L
		);

		samlSsoRequestContext = _webSsoProfileImpl.decodeAuthnRequest(
			mockHttpServletRequest, new MockHttpServletResponse());

		Assert.assertEquals(
			SamlSsoRequestContext.STAGE_AUTHENTICATED,
			samlSsoRequestContext.getStage());
		Assert.assertEquals(1000, samlSsoRequestContext.getUserId());

		_assertInboundMessageContext(
			inboundMessageContext -> {
				Assert.assertNotNull(inboundMessageContext.getMessage());

				SAMLMessageInfoContext samlMessageInfoContext =
					inboundMessageContext.getSubcontext(
						SAMLMessageInfoContext.class, false);

				Assert.assertEquals(
					messageIdAtomicReference.get(),
					samlMessageInfoContext.getMessageId());
			},
			samlSsoRequestContext);

		_assertSamlSsoRequestContexts(0, mockHttpServletRequest.getSession());
	}

	@Test
	public void testDecodeAuthnRequestStageAuthenticatedWithConcurrentAuthnRequest()
		throws Exception {

		SamlSpIdpConnection samlSpIdpConnection = new SamlSpIdpConnectionImpl();

		samlSpIdpConnection.setSamlIdpEntityId(IDP_ENTITY_ID);

		_setUpWebSsoProfilerImpl(samlSpIdpConnection);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(LOGIN_URL);

		mockHttpServletRequest.setAttribute(
			SamlWebKeys.SAML_SP_IDP_CONNECTION, samlSpIdpConnection);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webSsoProfileImpl.doSendAuthnRequest(
			mockHttpServletRequest, mockHttpServletResponse, RELAY_STATE);

		prepareIdentityProvider(IDP_ENTITY_ID);

		SamlSsoRequestContext samlSsoRequestContext =
			_webSsoProfileImpl.decodeAuthnRequest(
				getMockHttpServletRequest(
					mockHttpServletResponse.getRedirectedUrl()),
				new MockHttpServletResponse());

		AtomicReference<String> messageIdAtomicReference =
			new AtomicReference<>();

		_assertInboundMessageContext(
			inboundMessageContext -> {
				SAMLMessageInfoContext samlMessageInfoContext =
					inboundMessageContext.getSubcontext(
						SAMLMessageInfoContext.class, false);

				messageIdAtomicReference.set(
					samlMessageInfoContext.getMessageId());

				Assert.assertNotNull(messageIdAtomicReference.get());
			},
			samlSsoRequestContext);

		mockHttpServletRequest = getMockHttpServletRequest(
			SSO_URL + "?samlMessageId=" + messageIdAtomicReference.get());

		samlSsoRequestContext.setSAMLMessageContext(null);

		_setSamlSsoRequestContexts(
			mockHttpServletRequest.getSession(),
			new ObjectValuePair<>(
				messageIdAtomicReference.get(), samlSsoRequestContext),
			new ObjectValuePair<>(
				RandomTestUtil.randomString(),
				new SamlSsoRequestContext(
					RandomTestUtil.randomString(), SP_ENTITY_ID, RELAY_STATE,
					null)));

		Mockito.when(
			portal.getUserId(Mockito.any(MockHttpServletRequest.class))
		).thenReturn(
			1000L
		);

		samlSsoRequestContext = _webSsoProfileImpl.decodeAuthnRequest(
			mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			SamlSsoRequestContext.STAGE_AUTHENTICATED,
			samlSsoRequestContext.getStage());
		Assert.assertEquals(1000, samlSsoRequestContext.getUserId());

		_assertInboundMessageContext(
			inboundMessageContext -> {
				Assert.assertNotNull(inboundMessageContext.getMessage());

				SAMLMessageInfoContext messageInfoContext =
					inboundMessageContext.getSubcontext(
						SAMLMessageInfoContext.class);

				Assert.assertEquals(
					messageIdAtomicReference.get(),
					messageInfoContext.getMessageId());
			},
			samlSsoRequestContext);

		_assertSamlSsoRequestContexts(1, mockHttpServletRequest.getSession());
	}

	@Test
	public void testDecodeAuthnRequestStageInitial() throws Exception {
		SamlSpIdpConnection samlSpIdpConnection = new SamlSpIdpConnectionImpl();

		samlSpIdpConnection.setSamlIdpEntityId(IDP_ENTITY_ID);

		_setUpWebSsoProfilerImpl(samlSpIdpConnection);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(LOGIN_URL);

		mockHttpServletRequest.setAttribute(
			SamlWebKeys.SAML_SP_IDP_CONNECTION, samlSpIdpConnection);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webSsoProfileImpl.doSendAuthnRequest(
			mockHttpServletRequest, mockHttpServletResponse, RELAY_STATE);

		String redirect = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertNotNull(redirect);

		prepareIdentityProvider(IDP_ENTITY_ID);

		SamlSsoRequestContext samlSsoRequestContext =
			_webSsoProfileImpl.decodeAuthnRequest(
				getMockHttpServletRequest(redirect),
				new MockHttpServletResponse());

		_assertAbstractSAMLEntityContext(
			_getMessageContextSubcontext(
				samlSsoRequestContext, SAMLSelfEntityContext.class),
			IDP_ENTITY_ID, IDPSSODescriptor.class);
		_assertAbstractSAMLEntityContext(
			_getMessageContextSubcontext(
				samlSsoRequestContext, SAMLPeerEntityContext.class),
			SP_ENTITY_ID, SPSSODescriptor.class);
		_assertInboundMessageContext(
			inboundMessageContext -> {
				AuthnRequest authnRequest =
					(AuthnRequest)inboundMessageContext.getMessage();

				Assert.assertEquals(identifiers.get(0), authnRequest.getID());
				Assert.assertFalse(authnRequest.isForceAuthn());
			},
			samlSsoRequestContext);

		Assert.assertEquals(2, identifiers.size());
		Assert.assertTrue(samlSsoRequestContext.isNewSession());
	}

	@Test(expected = MessageHandlerException.class)
	public void testDecodeAuthnRequestVerifiesSignature() throws Exception {
		SamlSpIdpConnection samlSpIdpConnection = new SamlSpIdpConnectionImpl();

		samlSpIdpConnection.setSamlIdpEntityId(IDP_ENTITY_ID);

		_setUpWebSsoProfilerImpl(samlSpIdpConnection);

		ReflectionTestUtil.invoke(
			_webSsoProfileImpl.getMetadataResolver(), "doDestroy",
			new Class<?>[0]);

		CachingChainingMetadataResolver cachingChainingMetadataResolver =
			(CachingChainingMetadataResolver)
				_webSsoProfileImpl.getMetadataResolver();

		cachingChainingMetadataResolver.addMetadataResolver(
			new MockMetadataResolver(false));

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(LOGIN_URL);

		mockHttpServletRequest.setAttribute(
			SamlWebKeys.SAML_SP_IDP_CONNECTION, samlSpIdpConnection);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webSsoProfileImpl.doSendAuthnRequest(
			mockHttpServletRequest, mockHttpServletResponse, RELAY_STATE);

		prepareIdentityProvider(IDP_ENTITY_ID);

		mockHttpServletRequest = getMockHttpServletRequest(
			mockHttpServletResponse.getRedirectedUrl());

		Mockito.when(
			samlProviderConfiguration.authnRequestSignatureRequired()
		).thenReturn(
			true
		);

		_webSsoProfileImpl.decodeAuthnRequest(
			mockHttpServletRequest, new MockHttpServletResponse());
	}

	@Test
	public void testForceAuthn() throws Exception {
		SamlSpIdpConnection samlSpIdpConnection = new SamlSpIdpConnectionImpl();

		samlSpIdpConnection.setForceAuthn(true);

		samlSpIdpConnection.setSamlIdpEntityId(IDP_ENTITY_ID);

		_setUpWebSsoProfilerImpl(samlSpIdpConnection);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(LOGIN_URL);

		mockHttpServletRequest.setAttribute(
			SamlWebKeys.SAML_SP_IDP_CONNECTION, samlSpIdpConnection);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webSsoProfileImpl.doSendAuthnRequest(
			mockHttpServletRequest, mockHttpServletResponse, RELAY_STATE);

		String redirect = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertNotNull(redirect);

		prepareIdentityProvider(IDP_ENTITY_ID);

		SamlSsoRequestContext samlSsoRequestContext =
			_webSsoProfileImpl.decodeAuthnRequest(
				getMockHttpServletRequest(redirect),
				new MockHttpServletResponse());

		_assertInboundMessageContext(
			inboundMessageContext -> {
				AuthnRequest authnRequest =
					(AuthnRequest)inboundMessageContext.getMessage();

				Assert.assertTrue(authnRequest.isForceAuthn());
			},
			samlSsoRequestContext);
	}

	@Test
	public void testIsPassive() throws Exception {
		SamlSpIdpConnection samlSpIdpConnection = new SamlSpIdpConnectionImpl();

		samlSpIdpConnection.setForceAuthn(true);

		samlSpIdpConnection.setSamlIdpEntityId(IDP_ENTITY_ID);

		_setUpWebSsoProfilerImpl(samlSpIdpConnection);

		prepareIdentityProvider(IDP_ENTITY_ID);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(
				StringBundler.concat(
					SSO_URL, "?SAMLRequest=", _SAML_REQUEST_ENCODED));

		mockHttpServletRequest.setAttribute(
			SamlWebKeys.SAML_SP_IDP_CONNECTION, samlSpIdpConnection);

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_webSsoProfileImpl.processAuthnRequest(
			mockHttpServletRequest, mockHttpServletResponse);

		String content = mockHttpServletResponse.getContentAsString();

		Assert.assertTrue(content.contains("SAMLResponse"));
	}

	@Test(expected = SignatureException.class)
	public void testVerifyAssertionSignatureInvalidSignature()
		throws Exception {

		_testVerifyAssertionSignature(UNKNOWN_ENTITY_ID);
	}

	@Test
	public void testVerifyAssertionSignatureNoSignatureNotRequired()
		throws Exception {

		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		SAMLMetadataContext samlMetadataContext =
			samlSelfEntityContext.getSubcontext(SAMLMetadataContext.class);

		EntityDescriptor entityDescriptor =
			samlMetadataContext.getEntityDescriptor();

		SPSSODescriptor spSSODescriptor = entityDescriptor.getSPSSODescriptor(
			SAMLConstants.SAML20P_NS);

		spSSODescriptor.setWantAssertionsSigned(false);

		samlMetadataContext.setRoleDescriptor(spSSODescriptor);

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(IDP_ENTITY_ID);

		_webSsoProfileImpl.verifyAssertionSignature(
			null, messageContext, _webSsoProfileImpl.getSignatureTrustEngine());
	}

	@Test(expected = SignatureException.class)
	public void testVerifyAssertionSignatureNoSignatureRequired()
		throws Exception {

		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		SAMLMetadataContext samlSelfMetadataContext =
			samlSelfEntityContext.getSubcontext(SAMLMetadataContext.class);

		EntityDescriptor entityDescriptor =
			samlSelfMetadataContext.getEntityDescriptor();

		SPSSODescriptor spSSODescriptor = entityDescriptor.getSPSSODescriptor(
			SAMLConstants.SAML20P_NS);

		spSSODescriptor.setWantAssertionsSigned(true);

		samlSelfMetadataContext.setRoleDescriptor(spSSODescriptor);

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(IDP_ENTITY_ID);

		_webSsoProfileImpl.verifyAssertionSignature(
			null, messageContext, _webSsoProfileImpl.getSignatureTrustEngine());
	}

	@Test
	public void testVerifyAssertionSignatureValidSignature() throws Exception {
		_testVerifyAssertionSignature(IDP_ENTITY_ID);
	}

	@Test
	public void testVerifyAudienceRestrictionsAllow() throws Exception {
		List<AudienceRestriction> audienceRestrictions = new ArrayList<>();

		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		AudienceRestriction audienceRestriction =
			_webSsoProfileImpl.getSuccessAudienceRestriction(
				samlSelfEntityContext.getEntityId());

		audienceRestrictions.add(audienceRestriction);

		_webSsoProfileImpl.verifyAudienceRestrictions(
			audienceRestrictions, messageContext);
	}

	@Test(expected = AudienceException.class)
	public void testVerifyAudienceRestrictionsDeny() throws Exception {
		List<AudienceRestriction> audienceRestrictions = new ArrayList<>();

		AudienceRestriction audienceRestriction =
			_webSsoProfileImpl.getSuccessAudienceRestriction(UNKNOWN_ENTITY_ID);

		audienceRestrictions.add(audienceRestriction);

		_webSsoProfileImpl.verifyAudienceRestrictions(
			audienceRestrictions,
			_webSsoProfileImpl.getMessageContext(
				getMockHttpServletRequest(ACS_URL),
				new MockHttpServletResponse()));
	}

	@Test(expected = AssertionException.class)
	public void testVerifyConditionNotOnBefore() throws Exception {
		prepareIdentityProvider(IDP_ENTITY_ID);

		MessageContext<?> idpMessageContext =
			_webSsoProfileImpl.getMessageContext(
				getMockHttpServletRequest(SSO_URL),
				new MockHttpServletResponse());

		SAMLPeerEntityContext samlPeerEntityContext =
			idpMessageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(SP_ENTITY_ID);

		SamlSsoRequestContext samlSsoRequestContext = new SamlSsoRequestContext(
			SP_ENTITY_ID, null, idpMessageContext);

		DateTime dateTime = new DateTime(DateTimeZone.UTC);

		Conditions conditions = _webSsoProfileImpl.getSuccessConditions(
			samlSsoRequestContext, dateTime.plusDays(1), null);

		prepareServiceProvider(SP_ENTITY_ID);

		MessageContext<?> spMessageContext =
			_webSsoProfileImpl.getMessageContext(
				getMockHttpServletRequest(ACS_URL),
				new MockHttpServletResponse());

		_webSsoProfileImpl.verifyConditions(spMessageContext, conditions);
	}

	@Test(expected = ExpiredException.class)
	public void testVerifyConditionNotOnOrAfter() throws Exception {
		prepareIdentityProvider(IDP_ENTITY_ID);

		MessageContext<?> idpMessageContext =
			_webSsoProfileImpl.getMessageContext(
				getMockHttpServletRequest(SSO_URL),
				new MockHttpServletResponse());

		SAMLPeerEntityContext samlPeerEntityContext =
			idpMessageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(SP_ENTITY_ID);

		SamlSsoRequestContext samlSsoRequestContext = new SamlSsoRequestContext(
			SP_ENTITY_ID, null, idpMessageContext);

		DateTime dateTime = new DateTime(DateTimeZone.UTC);

		Conditions conditions = _webSsoProfileImpl.getSuccessConditions(
			samlSsoRequestContext, null, dateTime.minusYears(1));

		prepareServiceProvider(SP_ENTITY_ID);

		MessageContext<?> spMessageContext =
			_webSsoProfileImpl.getMessageContext(
				getMockHttpServletRequest(ACS_URL),
				new MockHttpServletResponse());

		_webSsoProfileImpl.verifyConditions(spMessageContext, conditions);
	}

	@Test
	public void testVerifyConditionsNoDates() throws Exception {
		prepareIdentityProvider(IDP_ENTITY_ID);

		MessageContext<?> idpMessageContext =
			_webSsoProfileImpl.getMessageContext(
				getMockHttpServletRequest(SSO_URL),
				new MockHttpServletResponse());

		SAMLPeerEntityContext samlPeerEntityContext =
			idpMessageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(SP_ENTITY_ID);

		SamlSsoRequestContext samlSsoRequestContext = new SamlSsoRequestContext(
			SP_ENTITY_ID, null, idpMessageContext);

		Conditions conditions = _webSsoProfileImpl.getSuccessConditions(
			samlSsoRequestContext, null, null);

		prepareServiceProvider(SP_ENTITY_ID);

		_webSsoProfileImpl.verifyConditions(
			_webSsoProfileImpl.getMessageContext(
				getMockHttpServletRequest(ACS_URL),
				new MockHttpServletResponse()),
			conditions);
	}

	@Test
	public void testVerifyDestinationAllow() throws Exception {
		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLBindingContext samlBindingContext = messageContext.getSubcontext(
			SAMLBindingContext.class);

		samlBindingContext.setBindingUri(SAMLConstants.SAML2_POST_BINDING_URI);

		_webSsoProfileImpl.verifyDestination(messageContext, ACS_URL);
	}

	@Test(expected = DestinationException.class)
	public void testVerifyDestinationDeny() throws Exception {
		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLBindingContext samlBindingContext = messageContext.getSubcontext(
			SAMLBindingContext.class);

		samlBindingContext.setBindingUri(SAMLConstants.SAML2_POST_BINDING_URI);

		_webSsoProfileImpl.verifyDestination(
			messageContext, "http://www.fail.com/c/portal/saml/acs");
	}

	@Test
	public void testVerifyInResponseToInvalidResponse() throws Exception {
		Response response = OpenSamlUtil.buildResponse();

		response.setIssuer(OpenSamlUtil.buildIssuer(IDP_ENTITY_ID));

		Assert.assertNull(_webSsoProfileImpl.verifyInResponseTo(response));
	}

	@Test(expected = InResponseToException.class)
	public void testVerifyInResponseToNoAuthnRequest() throws Exception {
		Response response = OpenSamlUtil.buildResponse();

		response.setInResponseTo("responseto");
		response.setIssuer(OpenSamlUtil.buildIssuer(IDP_ENTITY_ID));

		_webSsoProfileImpl.verifyInResponseTo(response);
	}

	@Test
	public void testVerifyInResponseToValidResponse() throws Exception {
		Response response = OpenSamlUtil.buildResponse();

		SamlSpAuthRequest samlSpAuthRequest = new SamlSpAuthRequestImpl();

		samlSpAuthRequest.setSamlIdpEntityId(IDP_ENTITY_ID);
		samlSpAuthRequest.setSamlRelayState(RELAY_STATE);

		IdentifierGenerationStrategy identifierGenerationStrategy =
			IdentifierGeneratorStrategyFactory.create(30);

		String samlSpAuthRequestKey =
			identifierGenerationStrategy.generateIdentifier();

		samlSpAuthRequest.setSamlSpAuthRequestKey(samlSpAuthRequestKey);

		Mockito.when(
			_samlSpAuthRequestLocalService.fetchSamlSpAuthRequest(
				Mockito.any(String.class), Mockito.any(String.class))
		).thenReturn(
			samlSpAuthRequest
		);

		response.setInResponseTo(samlSpAuthRequestKey);
		response.setIssuer(OpenSamlUtil.buildIssuer(IDP_ENTITY_ID));

		Assert.assertEquals(
			RELAY_STATE, _webSsoProfileImpl.verifyInResponseTo(response));
	}

	@Test(expected = IssuerException.class)
	public void testVerifyIssuerInvalidFormat() throws Exception {
		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(IDP_ENTITY_ID);

		Issuer issuer = OpenSamlUtil.buildIssuer(IDP_ENTITY_ID);

		issuer.setFormat(NameIDType.UNSPECIFIED);

		_webSsoProfileImpl.verifyIssuer(messageContext, issuer);
	}

	@Test(expected = IssuerException.class)
	public void testVerifyIssuerInvalidIssuer() throws Exception {
		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(IDP_ENTITY_ID);

		_webSsoProfileImpl.verifyIssuer(
			messageContext, OpenSamlUtil.buildIssuer(UNKNOWN_ENTITY_ID));
	}

	@Test
	public void testVerifyIssuerValidIssuer() throws Exception {
		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(IDP_ENTITY_ID);

		_webSsoProfileImpl.verifyIssuer(
			messageContext, OpenSamlUtil.buildIssuer(IDP_ENTITY_ID));
	}

	@Test
	public void testVerifyReplayNoConditionsDate() throws Exception {
		prepareIdentityProvider(IDP_ENTITY_ID);

		MessageContext<?> idpMessageContext =
			_webSsoProfileImpl.getMessageContext(
				getMockHttpServletRequest(SSO_URL),
				new MockHttpServletResponse());

		SAMLPeerEntityContext samlPeerEntityContext =
			idpMessageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(SP_ENTITY_ID);

		SamlSsoRequestContext samlSsoRequestContext = new SamlSsoRequestContext(
			SP_ENTITY_ID, null, idpMessageContext);

		Conditions conditions = _webSsoProfileImpl.getSuccessConditions(
			samlSsoRequestContext, null, null);

		prepareServiceProvider(SP_ENTITY_ID);

		MessageContext<?> spMessageContext =
			_webSsoProfileImpl.getMessageContext(
				getMockHttpServletRequest(ACS_URL),
				new MockHttpServletResponse());

		Assertion assertion = OpenSamlUtil.buildAssertion();

		assertion.setConditions(conditions);

		assertion.setIssuer(OpenSamlUtil.buildIssuer(IDP_ENTITY_ID));

		String messageId = samlIdentifierGenerator.generateIdentifier();

		assertion.setID(messageId);

		SamlSpMessageLocalService samlSpMessageLocalService =
			getMockPortletService(
				SamlSpMessageLocalServiceUtil.class,
				SamlSpMessageLocalService.class);

		Mockito.when(
			samlSpMessageLocalService.fetchSamlSpMessage(
				Mockito.eq(IDP_ENTITY_ID), Mockito.eq(messageId))
		).thenReturn(
			null
		);

		_webSsoProfileImpl.verifyReplay(spMessageContext, assertion);
	}

	@Test(expected = ExpiredException.class)
	public void testVerifySubjectExpired() throws Exception {
		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLBindingContext samlBindingContext = messageContext.getSubcontext(
			SAMLBindingContext.class);

		samlBindingContext.setBindingUri(SAMLConstants.SAML2_POST_BINDING_URI);

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(IDP_ENTITY_ID);

		NameID nameID = OpenSamlUtil.buildNameId(
			NameIDType.UNSPECIFIED, "test");

		DateTime issueDate = new DateTime(DateTimeZone.UTC);

		issueDate = issueDate.minusYears(1);

		_webSsoProfileImpl.verifySubject(
			messageContext, getSubject(messageContext, nameID, issueDate));
	}

	@Test(expected = SubjectException.class)
	public void testVerifySubjectNoBearerSubjectConfirmation()
		throws Exception {

		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLBindingContext samlBindingContext = messageContext.getSubcontext(
			SAMLBindingContext.class);

		samlBindingContext.setBindingUri(SAMLConstants.SAML2_POST_BINDING_URI);

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(IDP_ENTITY_ID);

		NameID nameID = OpenSamlUtil.buildNameId(
			NameIDType.UNSPECIFIED, "test");

		Subject subject = getSubject(
			messageContext, nameID, new DateTime(DateTimeZone.UTC));

		List<SubjectConfirmation> subjectConfirmations =
			subject.getSubjectConfirmations();

		SubjectConfirmation subjectConfirmation = subjectConfirmations.get(0);

		subjectConfirmation.setMethod(
			SubjectConfirmation.METHOD_SENDER_VOUCHES);

		_webSsoProfileImpl.verifySubject(messageContext, subject);
	}

	@Test
	public void testVerifySubjectValidSubject() throws Exception {
		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLBindingContext samlBindingContext = messageContext.getSubcontext(
			SAMLBindingContext.class);

		samlBindingContext.setBindingUri(SAMLConstants.SAML2_POST_BINDING_URI);

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(IDP_ENTITY_ID);

		NameID nameID = OpenSamlUtil.buildNameId(
			NameIDType.UNSPECIFIED, "test");

		Subject subject = getSubject(
			messageContext, nameID, new DateTime(DateTimeZone.UTC));

		_webSsoProfileImpl.verifySubject(messageContext, subject);

		SAMLSubjectNameIdentifierContext samlSubjectNameIdentifierContext =
			messageContext.getSubcontext(
				SAMLSubjectNameIdentifierContext.class);

		NameID resolvedNameID =
			samlSubjectNameIdentifierContext.getSAML2SubjectNameID();

		Assert.assertNotNull(resolvedNameID);
		Assert.assertEquals(nameID.getFormat(), resolvedNameID.getFormat());
		Assert.assertEquals(nameID.getValue(), resolvedNameID.getValue());
	}

	protected Subject getSubject(
			MessageContext<?> messageContext, NameID nameID, DateTime issueDate)
		throws Exception {

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		SAMLBindingContext samlBindingContext = messageContext.getSubcontext(
			SAMLBindingContext.class);

		SamlSsoRequestContext samlSsoRequestContext = new SamlSsoRequestContext(
			samlPeerEntityContext.getEntityId(),
			samlBindingContext.getRelayState(), messageContext);

		SAMLSelfEntityContext samlSelfEntityContext =
			messageContext.getSubcontext(SAMLSelfEntityContext.class);

		SAMLMetadataContext samlSelfMetadataContext =
			samlSelfEntityContext.getSubcontext(SAMLMetadataContext.class);

		SPSSODescriptor spSSODescriptor =
			(SPSSODescriptor)samlSelfMetadataContext.getRoleDescriptor();

		AssertionConsumerService assertionConsumerService =
			SamlUtil.getAssertionConsumerServiceForBinding(
				spSSODescriptor, SAMLConstants.SAML2_POST_BINDING_URI);

		SubjectConfirmationData subjectConfirmationData =
			_webSsoProfileImpl.getSuccessSubjectConfirmationData(
				samlSsoRequestContext, assertionConsumerService, issueDate);

		return _webSsoProfileImpl.getSuccessSubject(
			nameID, subjectConfirmationData);
	}

	private void _assertAbstractSAMLEntityContext(
		AbstractSAMLEntityContext abstractSAMLEntityContext,
		String expectedEntityId, Class<?> expectedRoleDescriptorClass) {

		Assert.assertEquals(
			expectedEntityId, abstractSAMLEntityContext.getEntityId());

		SAMLMetadataContext samlMetadataContext =
			abstractSAMLEntityContext.getSubcontext(SAMLMetadataContext.class);

		Assert.assertNotNull(samlMetadataContext.getEntityDescriptor());
		Assert.assertNotNull(samlMetadataContext.getRoleDescriptor());
		Assert.assertTrue(
			expectedRoleDescriptorClass.isInstance(
				samlMetadataContext.getRoleDescriptor()));
	}

	private void _assertInboundMessageContext(
		Consumer<MessageContext<?>> consumer,
		SamlSsoRequestContext samlSsoRequestContext) {

		InOutOperationContext<?, ?> inOutOperationContext =
			_getMessageContextSubcontext(
				samlSsoRequestContext, InOutOperationContext.class);

		consumer.accept(inOutOperationContext.getInboundMessageContext());
	}

	private void _assertSamlSsoRequestContexts(
		int expectedSize, HttpSession httpSession) {

		Map<String, SamlSsoRequestContext> samlSsoRequestContexts =
			(Map<String, SamlSsoRequestContext>)httpSession.getAttribute(
				SamlWebKeys.SAML_SSO_REQUEST_CONTEXT);

		Assert.assertEquals(
			samlSsoRequestContexts.toString(), expectedSize,
			samlSsoRequestContexts.size());
	}

	private <T extends BaseContext> T _getMessageContextSubcontext(
		SamlSsoRequestContext samlSsoRequestContext, Class<T> subcontextClass) {

		MessageContext<AuthnRequest> messageContext =
			(MessageContext<AuthnRequest>)
				samlSsoRequestContext.getSAMLMessageContext();

		return messageContext.getSubcontext(subcontextClass, false);
	}

	private void _setSamlSsoRequestContexts(
		HttpSession httpSession,
		ObjectValuePair<String, SamlSsoRequestContext>... objectValuePairs) {

		Map<String, SamlSsoRequestContext> samlSsoRequestContexts =
			new LRUMap<>(objectValuePairs.length);

		for (ObjectValuePair<String, SamlSsoRequestContext> objectValuePair :
				objectValuePairs) {

			samlSsoRequestContexts.put(
				objectValuePair.getKey(), objectValuePair.getValue());
		}

		httpSession.setAttribute(
			SamlWebKeys.SAML_SSO_REQUEST_CONTEXT, samlSsoRequestContexts);
	}

	private void _setUpWebSsoProfilerImpl(
			SamlSpIdpConnection samlSpIdpConnection)
		throws Exception {

		SamlSpIdpConnectionLocalService samlSpIdpConnectionLocalService =
			getMockPortletService(
				SamlSpIdpConnectionLocalServiceUtil.class,
				SamlSpIdpConnectionLocalService.class);

		Mockito.when(
			samlSpIdpConnectionLocalService.getSamlSpIdpConnection(
				Mockito.eq(COMPANY_ID), Mockito.eq(IDP_ENTITY_ID))
		).thenReturn(
			samlSpIdpConnection
		);

		ReflectionTestUtil.setFieldValue(
			_webSsoProfileImpl, "samlSpIdpConnectionLocalService",
			samlSpIdpConnectionLocalService);
	}

	private void _testVerifyAssertionSignature(String entityId)
		throws Exception {

		Assertion assertion = OpenSamlUtil.buildAssertion();

		OpenSamlUtil.signObject(assertion, getCredential(entityId), null);

		MessageContext<?> messageContext = _webSsoProfileImpl.getMessageContext(
			getMockHttpServletRequest(ACS_URL), new MockHttpServletResponse());

		SAMLPeerEntityContext samlPeerEntityContext =
			messageContext.getSubcontext(SAMLPeerEntityContext.class);

		samlPeerEntityContext.setEntityId(IDP_ENTITY_ID);

		_webSsoProfileImpl.verifyAssertionSignature(
			assertion.getSignature(), messageContext,
			_webSsoProfileImpl.getSignatureTrustEngine());
	}

	/**
	 * Encoded value of below SAMLRequest
	 * <pre>
	 * {@code
	 *
	 * samlp:AuthnRequest xmlns:samlp="urn:oasis:names:tc:SAML:2.0:protocol"
	 *                     AssertionConsumerServiceURL="http://localhost:8080/c/portal/saml/acs"
	 *                     Destination="http://localhost:8080/c/portal/saml/sso"
	 *                     ID="_ab1c2d3e4f5g6h7i8j9k0l"
	 *                     IsPassive="true"
	 *                     IssueInstant="2025-09-25T12:00:00Z"
	 *                     ProtocolBinding="urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST"
	 *                     Version="2.0">
	 *     saml:Issuer xmlns:saml="urn:oasis:names:tc:SAML:2.0:assertion">testsp /saml:Issuer>
	 *     samlp:NameIDPolicy AllowCreate="true" Format="urn:oasis:names:tc:SAML:2.0:nameid-format:transient"/>
	 * /samlp:AuthnRequest>
	 * }
	 * </pre>
	 */
	private static final String _SAML_REQUEST_ENCODED = StringBundler.concat(
		"jVJdT8IwFH33Vyx9ny1TFBqGQQhxiR8LDB98MaVUV%2B3a2XuH%2B",
		"u8dQxISB7HpU3vuOfeecwdXX4UJ1sqDdjYmnVNGAmWlW2n7GpNFNg175Gp4MgBRmJ",
		"KPKsztTH1UCjCoCy3w5iMmlbfcCdDArSgUcJR8Prq75dEp46V36KQz5CRoOSMA5bF",
		"WHzsLVaH8XPm1lmoxu41JjlhySo2TwuQOkPdYj1FJS%2BdRGLoRp0JCO%2FOk7",
		"lJbgc1k%2F2ECcO1MySQmz2LZkdHqTJ2%2FdF8v8kvde%2Bu%2FswNDJZAKAL1WMU",
		"FfqUMgqFRiAYXFmEQs6oasH0bdrBNxxur71F6X%2Fvp5re02pmPmL7cg4DdZlobpw",
		"zxrJ33crUBdRIYNpAmdN136vbSP64ldnGSItf9QDugezR5xye%2Fr4mSSOqPldzAy",
		"xn2OvRK48yyYOl8IPC63edGr8KWBcvTCglYWCa13lv5d2uEP");

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static ServiceRegistration<CookiesManager>
		_cookiesManagerServiceRegistration;

	private SamlSpAuthRequestLocalService _samlSpAuthRequestLocalService;
	private SamlSpSessionLocalService _samlSpSessionLocalService;

	private final WebSsoProfileImpl _webSsoProfileImpl =
		new WebSsoProfileImpl() {

			@Override
			public String generateIdentifier(int length) {
				return identifierGenerationStrategy.generateIdentifier();
			}

		};

}