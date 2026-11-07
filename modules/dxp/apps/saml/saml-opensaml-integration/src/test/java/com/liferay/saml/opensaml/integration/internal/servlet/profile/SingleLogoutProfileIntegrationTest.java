/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.opensaml.integration.internal.servlet.profile;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.cookies.CookiesManager;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.struts.Definition;
import com.liferay.portal.struts.TilesUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.saml.constants.SamlWebKeys;
import com.liferay.saml.opensaml.integration.internal.BaseSamlTestCase;
import com.liferay.saml.opensaml.integration.internal.provider.CachingChainingMetadataResolver;
import com.liferay.saml.persistence.model.SamlIdpSpSession;
import com.liferay.saml.persistence.model.SamlSpSession;
import com.liferay.saml.persistence.model.impl.SamlIdpSpConnectionImpl;
import com.liferay.saml.persistence.model.impl.SamlIdpSpSessionImpl;
import com.liferay.saml.persistence.model.impl.SamlIdpSsoSessionImpl;
import com.liferay.saml.persistence.model.impl.SamlSpSessionImpl;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalService;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalServiceUtil;
import com.liferay.saml.persistence.service.SamlIdpSpSessionLocalService;
import com.liferay.saml.persistence.service.SamlIdpSpSessionLocalServiceUtil;
import com.liferay.saml.persistence.service.SamlSpSessionLocalService;
import com.liferay.saml.persistence.service.SamlSpSessionLocalServiceUtil;
import com.liferay.saml.util.JspUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.LogoutRequest;
import org.opensaml.saml.saml2.core.NameID;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Matthew Tambara
 * @author William Newbury
 */
public class SingleLogoutProfileIntegrationTest extends BaseSamlTestCase {

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

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		_samlIdpSpConnectionLocalService = getMockPortletService(
			SamlIdpSpConnectionLocalServiceUtil.class,
			SamlIdpSpConnectionLocalService.class);
		_samlIdpSpSessionLocalService = getMockPortletService(
			SamlIdpSpSessionLocalServiceUtil.class,
			SamlIdpSpSessionLocalService.class);
		_samlSpSessionLocalService = getMockPortletService(
			SamlSpSessionLocalServiceUtil.class,
			SamlSpSessionLocalService.class);

		_singleLogoutProfileImpl = new SingleLogoutProfileImpl();

		ReflectionTestUtil.setFieldValue(
			_singleLogoutProfileImpl, "credentialResolver", credentialResolver);
		ReflectionTestUtil.setFieldValue(
			_singleLogoutProfileImpl, "httpClientFactory", httpClientFactory);
		ReflectionTestUtil.setFieldValue(
			_singleLogoutProfileImpl, "localEntityManager",
			keyStoreLocalEntityManager);
		ReflectionTestUtil.setFieldValue(
			_singleLogoutProfileImpl, "portal", portal);
		ReflectionTestUtil.setFieldValue(
			_singleLogoutProfileImpl, "_samlPeerBindingLocalService",
			samlPeerBindingLocalService);
		ReflectionTestUtil.setFieldValue(
			_singleLogoutProfileImpl, "samlProviderConfigurationHelper",
			samlProviderConfigurationHelper);
		ReflectionTestUtil.setFieldValue(
			_singleLogoutProfileImpl, "samlSpSessionLocalService",
			_samlSpSessionLocalService);

		_singleLogoutProfileImpl.activate(SystemBundleUtil.getBundleContext());

		ReflectionTestUtil.invoke(
			_singleLogoutProfileImpl.getMetadataResolver(), "doDestroy",
			new Class<?>[0]);

		CachingChainingMetadataResolver cachingChainingMetadataResolver =
			(CachingChainingMetadataResolver)
				_singleLogoutProfileImpl.getMetadataResolver();

		cachingChainingMetadataResolver.addMetadataResolver(
			new MockMetadataResolver());

		prepareServiceProvider(SP_ENTITY_ID);
	}

	@Test
	public void testPerformIdpSpLogoutInvalidSloRequestInfo() throws Exception {
		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(SLO_LOGOUT_URL + "?cmd=logout");

		SamlSloContext samlSloContext = new SamlSloContext(null);

		_singleLogoutProfileImpl.performIdpSpLogout(
			mockHttpServletRequest, new MockHttpServletResponse(),
			samlSloContext);

		Definition definition = (Definition)mockHttpServletRequest.getAttribute(
			TilesUtil.DEFINITION);

		Map<String, String> definitionAttributes = definition.getAttributes();

		Assert.assertEquals(
			JspUtil.PATH_PORTAL_SAML_ERROR,
			definitionAttributes.get("content"));
		Assert.assertTrue(Boolean.valueOf(definitionAttributes.get("pop_up")));
	}

	@Test
	public void testPerformIdpSpLogoutValidSloRequestInfo() throws Exception {
		Mockito.when(
			_samlIdpSpConnectionLocalService.getSamlIdpSpConnection(
				COMPANY_ID, SP_ENTITY_ID)
		).thenReturn(
			new SamlIdpSpConnectionImpl()
		);

		List<SamlIdpSpSession> samlIdpSpSessions = new ArrayList<>();

		SamlIdpSpSessionImpl samlIdpSpSessionImpl = new SamlIdpSpSessionImpl();

		samlIdpSpSessionImpl.setCompanyId(COMPANY_ID);
		samlIdpSpSessionImpl.setSamlPeerBindingId(
			prepareSamlPeerBinding(SP_ENTITY_ID, null, null, null));

		samlIdpSpSessions.add(samlIdpSpSessionImpl);

		Mockito.when(
			_samlIdpSpSessionLocalService.getSamlIdpSpSessions(SESSION_ID)
		).thenReturn(
			samlIdpSpSessions
		);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(SLO_LOGOUT_URL + "?cmd=logout");

		mockHttpServletRequest.setParameter("entityId", SP_ENTITY_ID);

		SamlIdpSsoSessionImpl samlIdpSsoSessionImpl =
			new SamlIdpSsoSessionImpl();

		samlIdpSsoSessionImpl.setSamlIdpSsoSessionId(SESSION_ID);

		SamlSloContext samlSloContext = new SamlSloContext(
			samlIdpSsoSessionImpl);

		SamlSloRequestInfo samlSloRequestInfo =
			samlSloContext.getSamlSloRequestInfo(SP_ENTITY_ID);

		samlSloRequestInfo.setStatus(SamlSloRequestInfo.REQUEST_STATUS_SUCCESS);

		_singleLogoutProfileImpl.performIdpSpLogout(
			mockHttpServletRequest, new MockHttpServletResponse(),
			samlSloContext);

		Definition definition = (Definition)mockHttpServletRequest.getAttribute(
			TilesUtil.DEFINITION);

		Map<String, String> definitionAttributes = definition.getAttributes();

		Assert.assertEquals(
			JspUtil.PATH_PORTAL_SAML_SLO_SP_STATUS,
			definitionAttributes.get("content"));
		Assert.assertTrue(Boolean.valueOf(definitionAttributes.get("pop_up")));

		JSONObject jsonObject = (JSONObject)mockHttpServletRequest.getAttribute(
			SamlWebKeys.SAML_SLO_REQUEST_INFO);

		Assert.assertNotNull(jsonObject);
		Assert.assertEquals(SP_ENTITY_ID, jsonObject.getString("entityId"));
		Assert.assertEquals(
			SamlSloRequestInfo.REQUEST_STATUS_SUCCESS,
			jsonObject.getInt("status"));
	}

	@Test
	public void testSendIdpLogoutRequestHttpRedirect() throws Exception {
		prepareIdentityProvider(IDP_ENTITY_ID);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(SLO_LOGOUT_URL + "?cmd=logout");
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		SamlIdpSsoSessionImpl samlIdpSsoSessionImpl =
			new SamlIdpSsoSessionImpl();

		SamlSloContext samlSloContext = new SamlSloContext(
			samlIdpSsoSessionImpl);

		SamlIdpSpSessionImpl samlIdpSpSessionImpl = new SamlIdpSpSessionImpl();

		samlIdpSpSessionImpl.setSamlPeerBindingId(
			prepareSamlPeerBinding(
				SP_ENTITY_ID, NameID.EMAIL, null, "test@liferay.com"));

		SamlSloRequestInfo samlSloRequestInfo = new SamlSloRequestInfo();

		samlSloRequestInfo.setSamlIdpSpSession(samlIdpSpSessionImpl);
		samlSloRequestInfo.setSamlPeerBinding(
			samlPeerBindingLocalService.getSamlPeerBinding(
				samlIdpSpSessionImpl.getSamlPeerBindingId()));

		_singleLogoutProfileImpl.sendIdpLogoutRequest(
			mockHttpServletRequest, mockHttpServletResponse, samlSloContext,
			samlSloRequestInfo);

		String redirect = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertNotNull(redirect);

		mockHttpServletRequest = getMockHttpServletRequest(redirect);

		MessageContext<LogoutRequest> messageContext =
			(MessageContext<LogoutRequest>)
				_singleLogoutProfileImpl.decodeSamlMessage(
					mockHttpServletRequest, mockHttpServletResponse,
					_singleLogoutProfileImpl.getSamlBinding(
						SAMLConstants.SAML2_REDIRECT_BINDING_URI),
					true);

		InOutOperationContext<LogoutRequest, ?> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class);

		MessageContext<LogoutRequest> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		LogoutRequest logoutRequest = inboundMessageContext.getMessage();

		NameID nameID = logoutRequest.getNameID();

		Assert.assertEquals(NameID.EMAIL, nameID.getFormat());
		Assert.assertEquals("test@liferay.com", nameID.getValue());
	}

	@Test
	public void testSendSpLogoutRequestInvalidSpSession() throws Exception {
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_singleLogoutProfileImpl.sendSpLogoutRequest(
			getMockHttpServletRequest(LOGOUT_URL), mockHttpServletResponse);

		String redirect = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertNotNull(redirect);
		Assert.assertEquals(LOGOUT_URL, redirect);
	}

	@Test
	public void testSendSpLogoutRequestValidSpSession() throws Exception {
		SamlSpSession samlSpSession = new SamlSpSessionImpl();

		samlSpSession.setSamlPeerBindingId(
			prepareSamlPeerBinding(
				IDP_ENTITY_ID, NameID.EMAIL, null, "test@liferay.com"));

		Mockito.when(
			_samlSpSessionLocalService.fetchSamlSpSessionByJSessionId(
				Mockito.anyString())
		).thenReturn(
			samlSpSession
		);

		Mockito.when(
			_samlSpSessionLocalService.fetchSamlSpSessionBySamlSpSessionKey(
				Mockito.anyString())
		).thenReturn(
			samlSpSession
		);

		MockHttpServletRequest mockHttpServletRequest =
			getMockHttpServletRequest(LOGOUT_URL);
		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		_singleLogoutProfileImpl.sendSpLogoutRequest(
			mockHttpServletRequest, mockHttpServletResponse);

		String redirect = mockHttpServletResponse.getRedirectedUrl();

		Assert.assertNotNull(redirect);

		mockHttpServletRequest = getMockHttpServletRequest(redirect);

		MessageContext<?> messageContext =
			_singleLogoutProfileImpl.decodeSamlMessage(
				mockHttpServletRequest, mockHttpServletResponse,
				_singleLogoutProfileImpl.getSamlBinding(
					SAMLConstants.SAML2_REDIRECT_BINDING_URI),
				true);

		InOutOperationContext<?, ?> inOutOperationContext =
			messageContext.getSubcontext(InOutOperationContext.class);

		MessageContext<?> inboundMessageContext =
			inOutOperationContext.getInboundMessageContext();

		LogoutRequest logoutRequest =
			(LogoutRequest)inboundMessageContext.getMessage();

		NameID nameID = logoutRequest.getNameID();

		Assert.assertEquals(NameID.EMAIL, nameID.getFormat());
		Assert.assertEquals("test@liferay.com", nameID.getValue());
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static ServiceRegistration<CookiesManager>
		_cookiesManagerServiceRegistration;

	private SamlIdpSpConnectionLocalService _samlIdpSpConnectionLocalService;
	private SamlIdpSpSessionLocalService _samlIdpSpSessionLocalService;
	private SamlSpSessionLocalService _samlSpSessionLocalService;
	private SingleLogoutProfileImpl _singleLogoutProfileImpl;

}