/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.configuration.OAuthClientCompanyConfiguration;
import com.liferay.oauth.client.persistence.constants.OAuthClientEntryConstants;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalService;
import com.liferay.oauth.client.rest.client.dto.v1_0.OAuthClientEntry;
import com.liferay.oauth.client.rest.client.permission.Permission;
import com.liferay.oauth.client.test.util.OpenIdConnectProviderHttpServer;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
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
	@Test
	public void testPutOAuthClientEntryByExternalReferenceCode()
		throws Exception {

		super.testPutOAuthClientEntryByExternalReferenceCode();

		_testPutOAuthClientEntryByExternalReferenceCodeWithPermissions();
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

	private void _testPutOAuthClientEntryByExternalReferenceCodeWithPermissions()
		throws Exception {

		OAuthClientEntry postOAuthClientEntry =
			testPutOAuthClientEntryByExternalReferenceCode_addOAuthClientEntry();

		com.liferay.oauth.client.persistence.model.OAuthClientEntry
			serviceBuilderOAuthClientEntry =
				_oAuthClientEntryLocalService.
					getOAuthClientEntryByExternalReferenceCode(
						postOAuthClientEntry.getExternalReferenceCode(),
						testCompany.getCompanyId());

		String className = serviceBuilderOAuthClientEntry.getModelClassName();
		String primKey = String.valueOf(
			serviceBuilderOAuthClientEntry.getOAuthClientEntryId());

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), className,
			ResourceConstants.SCOPE_INDIVIDUAL, primKey, role1.getRoleId(),
			new String[] {ActionKeys.VIEW});

		OAuthClientEntry randomOAuthClientEntry = randomOAuthClientEntry();
		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		randomOAuthClientEntry.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {ActionKeys.VIEW});
						setRoleName(role2.getName());
					}
				}
			});

		oAuthClientEntryResource.putOAuthClientEntryByExternalReferenceCode(
			postOAuthClientEntry.getExternalReferenceCode(),
			randomOAuthClientEntry);

		Assert.assertFalse(
			_resourcePermissionLocalService.hasResourcePermission(
				testCompany.getCompanyId(), className,
				ResourceConstants.SCOPE_INDIVIDUAL, primKey, role1.getRoleId(),
				ActionKeys.VIEW));
		Assert.assertTrue(
			_resourcePermissionLocalService.hasResourcePermission(
				testCompany.getCompanyId(), className,
				ResourceConstants.SCOPE_INDIVIDUAL, primKey, role2.getRoleId(),
				ActionKeys.VIEW));
	}

	private static CompanyConfigurationTemporarySwapper
		_companyConfigurationTemporarySwapper;
	private static OpenIdConnectProviderHttpServer
		_openIdConnectProviderHttpServer;

	@Inject
	private OAuthClientEntryLocalService _oAuthClientEntryLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}