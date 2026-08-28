/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet.action.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.exception.OAuthClientASLocalMetadataLocalWellKnownURIException;
import com.liferay.oauth.client.persistence.exception.OAuthClientASLocalMetadataMetadataJSONException;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCActionCommand;
import com.liferay.portal.kernel.security.auth.GuestOrUserUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionRequest;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletActionResponse;
import com.liferay.portal.kernel.test.portlet.MockPortletSession;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.as.AuthorizationServerMetadata;
import com.nimbusds.oauth2.sdk.id.Issuer;
import com.nimbusds.openid.connect.sdk.SubjectType;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;

import java.net.URI;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Alvaro Saugar
 */
@FeatureFlag("LPD-63415")
@RunWith(Arquillian.class)
public class UpdateOAuthClientASLocalMetadataMVCActionCommandTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		_user = UserTestUtil.addUser(_company);
	}

	@After
	public void tearDown() throws Exception {
		for (OAuthClientASLocalMetadata oAuthClientASLocalMetadata :
				_oAuthClientASLocalMetadataLocalService.
					getCompanyOAuthClientASLocalMetadata(
						TestPropsValues.getCompanyId())) {

			_oAuthClientASLocalMetadataLocalService.
				deleteOAuthClientASLocalMetadata(
					oAuthClientASLocalMetadata.
						getOAuthClientASLocalMetadataId());
		}
	}

	@Test
	public void testCreateAndUpdateOAuthClientASLocalMetadata()
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			_getMockLiferayPortletActionRequest(
				HashMapBuilder.put(
					"authorizationEndpoint",
					new String[] {RandomTestUtil.randomString()}
				).build());

		_assertOAuthClientASLocalMetadata(mockLiferayPortletActionRequest);

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest(
			HashMapBuilder.put(
				"issuer", new String[] {RandomTestUtil.randomString()}
			).build());

		_assertOAuthClientASLocalMetadata(mockLiferayPortletActionRequest);

		String issuer =
			Http.HTTPS_WITH_SLASH + RandomTestUtil.randomString() + ".com";

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest(
			HashMapBuilder.put(
				"issuer", new String[] {issuer}
			).put(
				"jwksURI", new String[] {RandomTestUtil.randomString()}
			).build());

		_assertOAuthClientASLocalMetadata(mockLiferayPortletActionRequest);

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest(
			HashMapBuilder.put(
				"issuer", new String[] {issuer}
			).put(
				"registrationEndpoint",
				new String[] {RandomTestUtil.randomString()}
			).build());

		_assertOAuthClientASLocalMetadata(mockLiferayPortletActionRequest);

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest(
			HashMapBuilder.put(
				"issuer", new String[] {issuer}
			).put(
				"tokenEndpoint", new String[] {RandomTestUtil.randomString()}
			).build());

		_assertOAuthClientASLocalMetadata(mockLiferayPortletActionRequest);

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest(
			HashMapBuilder.put(
				"issuer", new String[] {issuer}
			).put(
				"userInfoEndpoint", new String[] {RandomTestUtil.randomString()}
			).build());

		_assertOAuthClientASLocalMetadata(mockLiferayPortletActionRequest);

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest(
			HashMapBuilder.put(
				"issuer", new String[] {issuer}
			).build());

		Assert.assertFalse(
			_editMVCActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));
		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest,
				OAuthClientASLocalMetadataMetadataJSONException.class));

		User user = _userLocalService.getUser(GuestOrUserUtil.getUserId());

		List<OAuthClientASLocalMetadata> oAuthClientASLocalMetadatas =
			_oAuthClientASLocalMetadataLocalService.
				getCompanyOAuthClientASLocalMetadata(user.getCompanyId());

		int count = oAuthClientASLocalMetadatas.size();

		Assert.assertTrue(
			_editMVCActionCommand.processAction(
				_getMockLiferayPortletActionRequest(
					HashMapBuilder.put(
						"issuer", new String[] {issuer}
					).put(
						"supportedSubjectTypes", new String[] {"public"}
					).build()),
				new MockLiferayPortletActionResponse()));

		oAuthClientASLocalMetadatas =
			_oAuthClientASLocalMetadataLocalService.
				getCompanyOAuthClientASLocalMetadata(user.getCompanyId());

		Assert.assertEquals(oAuthClientASLocalMetadatas.size(), count + 1);

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			oAuthClientASLocalMetadatas.get(0);

		Assert.assertEquals(issuer, oAuthClientASLocalMetadata.getIssuer());

		long oAuthClientASLocalMetadataId =
			oAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId();

		mockLiferayPortletActionRequest = _getMockLiferayPortletActionRequest(
			HashMapBuilder.put(
				"issuer", new String[] {RandomTestUtil.randomString()}
			).put(
				"oAuthClientASLocalMetadataId",
				new String[] {String.valueOf(oAuthClientASLocalMetadataId)}
			).build());

		_assertOAuthClientASLocalMetadata(mockLiferayPortletActionRequest);

		oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(oAuthClientASLocalMetadataId);

		Assert.assertEquals(issuer, oAuthClientASLocalMetadata.getIssuer());

		String urlString =
			Http.HTTPS_WITH_SLASH + RandomTestUtil.randomString() + ".com";

		String supportedGrantType = RandomTestUtil.randomString();
		String supportedScope = RandomTestUtil.randomString();

		Assert.assertTrue(
			_editMVCActionCommand.processAction(
				_getMockLiferayPortletActionRequest(
					HashMapBuilder.put(
						"authorizationEndpoint", new String[] {urlString}
					).put(
						"issuer", new String[] {urlString}
					).put(
						"jwksURI", new String[] {urlString}
					).put(
						"localWellKnownEnabled", new String[] {"true"}
					).put(
						"oAuthClientASLocalMetadataId",
						new String[] {
							String.valueOf(oAuthClientASLocalMetadataId)
						}
					).put(
						"registrationEndpoint", new String[] {urlString}
					).put(
						"supportedGrantTypes", new String[] {supportedGrantType}
					).put(
						"supportedScopes", new String[] {supportedScope}
					).put(
						"supportedSubjectTypes", new String[] {"public"}
					).put(
						"tokenEndpoint", new String[] {urlString}
					).put(
						"userInfoEndpoint", new String[] {urlString}
					).build()),
				new MockLiferayPortletActionResponse()));

		oAuthClientASLocalMetadata =
			_oAuthClientASLocalMetadataLocalService.
				getOAuthClientASLocalMetadata(oAuthClientASLocalMetadataId);

		Assert.assertEquals(urlString, oAuthClientASLocalMetadata.getIssuer());
		Assert.assertTrue(oAuthClientASLocalMetadata.isLocalWellKnownEnabled());

		OIDCProviderMetadata oidcProviderMetadata = OIDCProviderMetadata.parse(
			oAuthClientASLocalMetadata.getMetadataJSON());

		URI url = URI.create(urlString);

		Assert.assertEquals(
			URI.create(urlString),
			oidcProviderMetadata.getAuthorizationEndpointURI());
		Assert.assertEquals(
			Issuer.parse(urlString), oidcProviderMetadata.getIssuer());
		Assert.assertEquals(url, oidcProviderMetadata.getJWKSetURI());

		Assert.assertEquals(
			Scope.parse(supportedScope), oidcProviderMetadata.getScopes());

		List<SubjectType> subjectTypes = oidcProviderMetadata.getSubjectTypes();

		Assert.assertEquals(SubjectType.parse("public"), subjectTypes.get(0));

		Assert.assertEquals(url, oidcProviderMetadata.getTokenEndpointURI());
		Assert.assertEquals(url, oidcProviderMetadata.getUserInfoEndpointURI());

		Assert.assertEquals(
			urlString + "/.well-known/oauth-authorization-server",
			oAuthClientASLocalMetadata.getOAuthASLocalWellKnownURI());

		AuthorizationServerMetadata authorizationServerMetadata =
			AuthorizationServerMetadata.parse(
				oAuthClientASLocalMetadata.getOAuthASMetadataJSON());

		Assert.assertEquals(
			url, authorizationServerMetadata.getAuthorizationEndpointURI());

		List<GrantType> grantTypes =
			authorizationServerMetadata.getGrantTypes();

		Assert.assertEquals(
			GrantType.parse(supportedGrantType), grantTypes.get(0));

		Assert.assertEquals(
			Issuer.parse(urlString), authorizationServerMetadata.getIssuer());
		Assert.assertEquals(url, authorizationServerMetadata.getJWKSetURI());
		Assert.assertEquals(
			Scope.parse(supportedScope),
			authorizationServerMetadata.getScopes());
		Assert.assertEquals(
			url, authorizationServerMetadata.getTokenEndpointURI());
	}

	private void _assertOAuthClientASLocalMetadata(
			MockLiferayPortletActionRequest mockLiferayPortletActionRequest)
		throws Exception {

		Assert.assertFalse(
			_editMVCActionCommand.processAction(
				mockLiferayPortletActionRequest,
				new MockLiferayPortletActionResponse()));
		Assert.assertTrue(
			SessionErrors.contains(
				mockLiferayPortletActionRequest,
				new Class<?>[] {
					OAuthClientASLocalMetadataLocalWellKnownURIException.
						MustBeValidHTTPSURL.class,
					OAuthClientASLocalMetadataLocalWellKnownURIException.
						MustProduceValidURI.class
				}));
	}

	private MockLiferayPortletActionRequest _getMockLiferayPortletActionRequest(
			Map<String, String[]> parameters)
		throws Exception {

		MockLiferayPortletActionRequest mockLiferayPortletActionRequest =
			new MockLiferayPortletActionRequest();

		mockLiferayPortletActionRequest.setAttribute(
			WebKeys.THEME_DISPLAY, _getThemeDisplay());

		for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
			mockLiferayPortletActionRequest.setParameter(
				entry.getKey(), entry.getValue());
		}

		mockLiferayPortletActionRequest.setPortletSession(
			new MockPortletSession());

		return mockLiferayPortletActionRequest;
	}

	private ThemeDisplay _getThemeDisplay() throws Exception {
		ThemeDisplay themeDisplay = new ThemeDisplay();

		themeDisplay.setCompany(_company);
		themeDisplay.setPermissionChecker(
			PermissionThreadLocal.getPermissionChecker());
		themeDisplay.setRequest(new MockHttpServletRequest());
		themeDisplay.setUser(_user);

		return themeDisplay;
	}

	private static Company _company;

	@Inject
	private static CompanyLocalService _companyLocalService;

	private static User _user;

	@Inject(
		filter = "mvc.command.name=/oauth_client_admin/update_oauth_client_as_local_metadata"
	)
	private MVCActionCommand _editMVCActionCommand;

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	@Inject
	private UserLocalService _userLocalService;

}