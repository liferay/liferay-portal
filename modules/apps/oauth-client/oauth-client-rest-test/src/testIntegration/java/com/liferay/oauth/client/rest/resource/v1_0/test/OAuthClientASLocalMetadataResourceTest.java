/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.oauth.client.rest.client.dto.v1_0.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.rest.client.permission.Permission;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuele Castro
 */
@FeatureFlags(featureFlags = @FeatureFlag(value = "LPD-49855"))
@RunWith(Arquillian.class)
public class OAuthClientASLocalMetadataResourceTest
	extends BaseOAuthClientASLocalMetadataResourceTestCase {

	@Override
	@Test
	public void testPutOAuthClientASLocalMetadataByExternalReferenceCode()
		throws Exception {

		super.testPutOAuthClientASLocalMetadataByExternalReferenceCode();

		_testPutOAuthClientASLocalMetadataByExternalReferenceCodeWithPermissions();
	}

	@Override
	protected OAuthClientASLocalMetadata randomOAuthClientASLocalMetadata()
		throws Exception {

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			super.randomOAuthClientASLocalMetadata();

		String issuer =
			"https://" + StringUtil.toLowerCase(RandomTestUtil.randomString());

		oAuthClientASLocalMetadata.setIssuer(issuer);
		oAuthClientASLocalMetadata.setMetadataJSON(
			JSONUtil.put(
				"issuer", issuer
			).put(
				"subject_types_supported", JSONUtil.put("public")
			).put(
				"token_endpoint", issuer + "/token"
			).toString());
		oAuthClientASLocalMetadata.setOAuthASMetadataJSON(
			JSONUtil.put(
				"registration_endpoint", issuer + "/registration"
			).toString());

		return oAuthClientASLocalMetadata;
	}

	@Override
	protected OAuthClientASLocalMetadata
			testBatchEngineDeleteImportTask_addOAuthClientASLocalMetadata()
		throws Exception {

		return oAuthClientASLocalMetadataResource.
			postOAuthClientASLocalMetadata(randomOAuthClientASLocalMetadata());
	}

	@Override
	protected OAuthClientASLocalMetadata
			testDeleteOAuthClientASLocalMetadataByExternalReferenceCode_addOAuthClientASLocalMetadata()
		throws Exception {

		return oAuthClientASLocalMetadataResource.
			postOAuthClientASLocalMetadata(randomOAuthClientASLocalMetadata());
	}

	@Override
	protected OAuthClientASLocalMetadata
			testGetOAuthClientASLocalMetadataByExternalReferenceCode_addOAuthClientASLocalMetadata()
		throws Exception {

		return oAuthClientASLocalMetadataResource.
			postOAuthClientASLocalMetadata(randomOAuthClientASLocalMetadata());
	}

	@Override
	protected OAuthClientASLocalMetadata
			testGetOAuthClientASLocalMetadatasPage_addOAuthClientASLocalMetadata(
				OAuthClientASLocalMetadata oAuthClientASLocalMetadata)
		throws Exception {

		return oAuthClientASLocalMetadataResource.
			postOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Override
	protected OAuthClientASLocalMetadata
			testPostOAuthClientASLocalMetadata_addOAuthClientASLocalMetadata(
				OAuthClientASLocalMetadata oAuthClientASLocalMetadata)
		throws Exception {

		return oAuthClientASLocalMetadataResource.
			postOAuthClientASLocalMetadata(oAuthClientASLocalMetadata);
	}

	@Override
	protected OAuthClientASLocalMetadata
			testPutOAuthClientASLocalMetadataByExternalReferenceCode_addOAuthClientASLocalMetadata()
		throws Exception {

		return oAuthClientASLocalMetadataResource.
			postOAuthClientASLocalMetadata(randomOAuthClientASLocalMetadata());
	}

	private void _testPutOAuthClientASLocalMetadataByExternalReferenceCodeWithPermissions()
		throws Exception {

		OAuthClientASLocalMetadata postOAuthClientASLocalMetadata =
			testPutOAuthClientASLocalMetadataByExternalReferenceCode_addOAuthClientASLocalMetadata();

		com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata
			serviceBuilderOAuthClientASLocalMetadata =
				_oAuthClientASLocalMetadataLocalService.
					getOAuthClientASLocalMetadataByExternalReferenceCode(
						postOAuthClientASLocalMetadata.
							getExternalReferenceCode(),
						testCompany.getCompanyId());

		String className =
			serviceBuilderOAuthClientASLocalMetadata.getModelClassName();
		String primKey = String.valueOf(
			serviceBuilderOAuthClientASLocalMetadata.
				getOAuthClientASLocalMetadataId());

		Role role1 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			testCompany.getCompanyId(), className,
			ResourceConstants.SCOPE_INDIVIDUAL, primKey, role1.getRoleId(),
			new String[] {ActionKeys.VIEW});

		OAuthClientASLocalMetadata randomOAuthClientASLocalMetadata =
			randomOAuthClientASLocalMetadata();
		Role role2 = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		randomOAuthClientASLocalMetadata.setPermissions(
			new Permission[] {
				new Permission() {
					{
						setActionIds(new String[] {ActionKeys.VIEW});
						setRoleName(role2.getName());
					}
				}
			});

		oAuthClientASLocalMetadataResource.
			putOAuthClientASLocalMetadataByExternalReferenceCode(
				postOAuthClientASLocalMetadata.getExternalReferenceCode(),
				randomOAuthClientASLocalMetadata);

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

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

}