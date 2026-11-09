/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalService;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.StringUtil;
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
public class OAuthClientASLocalMetadataVulcanBatchEngineTaskItemDelegateTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule liferayIntegrationTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testRead() throws Exception {
		_addOAuthClientASLocalMetadata();
		_addOAuthClientASLocalMetadata();
		_addOAuthClientASLocalMetadata();

		_vulcanBatchEngineTaskItemDelegate.setContextCompany(
			_companyLocalService.getCompany(TestPropsValues.getCompanyId()));

		Page<?> page = _vulcanBatchEngineTaskItemDelegate.read(
			null, Pagination.of(1, 2), null, Collections.emptyMap(), null);

		Collection<?> items = page.getItems();

		Assert.assertEquals(items.toString(), 2, items.size());
	}

	private void _addOAuthClientASLocalMetadata() throws Exception {
		String issuer =
			"https://" + StringUtil.toLowerCase(RandomTestUtil.randomString());

		_oAuthClientASLocalMetadataLocalService.addOAuthClientASLocalMetadata(
			TestPropsValues.getUserId(),
			JSONUtil.put(
				"authorization_endpoint",
				issuer + "/protocol/openid-connect/auth"
			).put(
				"issuer", issuer
			).put(
				"jwks_uri", issuer + "/protocol/openid-connect/certs"
			).put(
				"registration_endpoint",
				issuer + "/clients-registrations/openid-connect"
			).put(
				"subject_types_supported", JSONUtil.putAll("public")
			).put(
				"token_endpoint", issuer + "/protocol/openid-connect/token"
			).put(
				"userinfo_endpoint",
				issuer + "/protocol/openid-connect/userinfo"
			).toString(),
			"openid-configuration");
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject
	private OAuthClientASLocalMetadataLocalService
		_oAuthClientASLocalMetadataLocalService;

	@Inject(
		filter = "component.name=com.liferay.oauth.client.rest.internal.resource.v1_0.OAuthClientASLocalMetadataResourceImpl",
		type = Inject.NoType.class
	)
	private VulcanBatchEngineTaskItemDelegate<?>
		_vulcanBatchEngineTaskItemDelegate;

}