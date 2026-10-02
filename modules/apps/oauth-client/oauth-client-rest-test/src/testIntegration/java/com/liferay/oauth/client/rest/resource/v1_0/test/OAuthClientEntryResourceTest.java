/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.configuration.OAuthClientCompanyConfiguration;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.rest.client.dto.v1_0.OAuthClientEntry;
import com.liferay.oauth.client.test.util.OpenIdConnectProviderHttpServer;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-49855"))
@RunWith(Arquillian.class)
public class OAuthClientEntryResourceTest
	extends BaseOAuthClientEntryResourceTestCase {

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseOAuthClientEntryResourceTestCase.setUpClass();

		_companyConfigurationTemporarySwapper =
			new CompanyConfigurationTemporarySwapper(
				TestPropsValues.getCompanyId(),
				OAuthClientCompanyConfiguration.class.getName(),
				HashMapDictionaryBuilder.<String, Object>put(
					"authServerLocalNetworkAccessEnabled", true
				).build());

		_openIdConnectProviderHttpServer =
			new OpenIdConnectProviderHttpServer();
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		if (_companyConfigurationTemporarySwapper != null) {
			_companyConfigurationTemporarySwapper.close();
		}

		if (_openIdConnectProviderHttpServer != null) {
			_openIdConnectProviderHttpServer.close();
		}
	}

	@Override
	protected OAuthClientEntry randomOAuthClientEntry() throws Exception {
		OAuthClientEntry oAuthClientEntry = super.randomOAuthClientEntry();

		oAuthClientEntry.setAuthRequestParametersJSON(
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).toString());
		oAuthClientEntry.setAuthServerWellKnownURI(
			_openIdConnectProviderHttpServer.getURL());
		oAuthClientEntry.setCustomClaims(
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).toString());
		oAuthClientEntry.setInfoJSON(
			JSONUtil.put(
				"client_id", oAuthClientEntry.getClientId()
			).put(
				"client_name", "example_client"
			).put(
				"client_secret", RandomTestUtil.randomString()
			).put(
				"scope", "openid email profile"
			).put(
				"subject_type", "public"
			).toString());
		oAuthClientEntry.setMatcherField("email");
		oAuthClientEntry.setOidcUserInfoMapperJSON(
			OAuthClientEntryConstants.OIDC_USER_INFO_MAPPER_JSON);
		oAuthClientEntry.setTokenRequestParametersJSON(
			JSONUtil.put(
				RandomTestUtil.randomString(), RandomTestUtil.randomString()
			).toString());

		return oAuthClientEntry;
	}

	@Override
	protected OAuthClientEntry
			testBatchEngineDeleteImportTask_addOAuthClientEntry()
		throws Exception {

		return oAuthClientEntryResource.postOAuthClientEntry(
			randomOAuthClientEntry());
	}

	@Override
	protected OAuthClientEntry
			testDeleteOAuthClientEntryByExternalReferenceCode_addOAuthClientEntry()
		throws Exception {

		return oAuthClientEntryResource.postOAuthClientEntry(
			randomOAuthClientEntry());
	}

	@Override
	protected OAuthClientEntry
			testGetOAuthClientEntriesPage_addOAuthClientEntry(
				OAuthClientEntry oAuthClientEntry)
		throws Exception {

		return oAuthClientEntryResource.postOAuthClientEntry(oAuthClientEntry);
	}

	@Override
	protected OAuthClientEntry
			testGetOAuthClientEntryByExternalReferenceCode_addOAuthClientEntry()
		throws Exception {

		return oAuthClientEntryResource.postOAuthClientEntry(
			randomOAuthClientEntry());
	}

	@Override
	protected OAuthClientEntry testPostOAuthClientEntry_addOAuthClientEntry(
			OAuthClientEntry oAuthClientEntry)
		throws Exception {

		return oAuthClientEntryResource.postOAuthClientEntry(oAuthClientEntry);
	}

	@Override
	protected OAuthClientEntry
			testPutOAuthClientEntryByExternalReferenceCode_addOAuthClientEntry()
		throws Exception {

		return oAuthClientEntryResource.postOAuthClientEntry(
			randomOAuthClientEntry());
	}

	private static CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;
	private static OpenIdConnectProviderHttpServer
		_openIdConnectProviderHttpServer;

}