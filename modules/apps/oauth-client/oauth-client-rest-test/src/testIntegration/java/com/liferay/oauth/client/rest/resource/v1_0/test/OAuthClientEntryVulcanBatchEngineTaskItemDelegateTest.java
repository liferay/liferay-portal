/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.configuration.OAuthClientCompanyConfiguration;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.oauth.client.test.util.OpenIdConnectProviderHttpServer;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.batch.engine.VulcanBatchEngineTaskItemDelegate;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.Collection;
import java.util.Collections;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Álvaro Saugar
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-49855"))
@RunWith(Arquillian.class)
public class OAuthClientEntryVulcanBatchEngineTaskItemDelegateTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule liferayIntegrationTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testRead() throws Exception {
		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						OAuthClientCompanyConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"authServerLocalNetworkAccessEnabled", true
						).build());
			OpenIdConnectProviderHttpServer openIdConnectProviderHttpServer =
				new OpenIdConnectProviderHttpServer()) {

			_addOAuthClientEntry(openIdConnectProviderHttpServer.getURL());
			_addOAuthClientEntry(openIdConnectProviderHttpServer.getURL());
			_addOAuthClientEntry(openIdConnectProviderHttpServer.getURL());

			_vulcanBatchEngineTaskItemDelegate.setContextCompany(
				_companyLocalService.getCompany(
					TestPropsValues.getCompanyId()));

			Page<?> page = _vulcanBatchEngineTaskItemDelegate.read(
				null, Pagination.of(1, 2), null, Collections.emptyMap(), null);

			Collection<?> items = page.getItems();

			Assert.assertEquals(items.toString(), 2, items.size());
		}
	}

	private void _addOAuthClientEntry(String authServerWellKnownURI)
		throws Exception {

		_oAuthClientEntryLocalService.addOAuthClientEntry(
			null, TestPropsValues.getUserId(),
			JSONUtil.put(
				"response_type", "code"
			).put(
				"scope", "openid email profile"
			).toString(),
			authServerWellKnownURI, "{}",
			JSONUtil.put(
				"client_id", RandomTestUtil.randomString()
			).put(
				"client_name", RandomTestUtil.randomString()
			).put(
				"client_secret", RandomTestUtil.randomString()
			).put(
				"grant_types",
				JSONUtil.putAll("authorization_code", "refresh_token")
			).put(
				"response_types", JSONUtil.putAll("code")
			).put(
				"scope", "openid email profile"
			).toString(),
			"email", OAuthClientEntryConstants.METADATA_CACHE_TIME_DEFAULT,
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON,
			OAuthClientEntryConstants.TOKEN_CONNECTION_TIMEOUT_DEFAULT,
			JSONUtil.put(
				"grant_type", "authorization_code"
			).put(
				"scope", "openid email profile"
			).toString());
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.oauth.client.rest.internal.resource.v1_0.OAuthClientEntryResourceImpl",
		type = Inject.NoType.class
	)
	private VulcanBatchEngineTaskItemDelegate<?>
		_vulcanBatchEngineTaskItemDelegate;

}