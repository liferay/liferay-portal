/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.capabilities;

import com.liferay.portal.kernel.license.util.LicenseManager;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.search.capabilities.ElasticsearchLicenseInformation;
import com.liferay.portal.search.capabilities.InferenceEndpointCapabilityStatus;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Rodrigo Guedes de Souza
 */
@FeatureFlag("LPD-11319")
public class InferenceEndpointCapabilityCheckerImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		_inferenceEndpointCapabilityCheckerImpl =
			new InferenceEndpointCapabilityCheckerImpl();

		ReflectionTestUtil.setFieldValue(
			InferenceEndpointCapabilityCheckerImpl.class,
			"_elasticsearchLicenseInformationSnapshot",
			new Snapshot<ElasticsearchLicenseInformation>(
				InferenceEndpointCapabilityCheckerImpl.class,
				ElasticsearchLicenseInformation.class) {

				@Override
				public ElasticsearchLicenseInformation get() {
					return _elasticsearchLicenseInformation;
				}

			});
		ReflectionTestUtil.setFieldValue(
			_inferenceEndpointCapabilityCheckerImpl, "_licenseManager",
			_licenseManager);
		ReflectionTestUtil.setFieldValue(
			_inferenceEndpointCapabilityCheckerImpl, "_searchEngineInformation",
			_searchEngineInformation);

		Mockito.when(
			_licenseManager.isFreeTier()
		).thenReturn(
			false
		);

		Mockito.when(
			_searchEngineInformation.getClientVersionString()
		).thenReturn(
			"8.18.0"
		);

		Mockito.when(
			_elasticsearchLicenseInformation.isInferenceLicenseAvailable()
		).thenReturn(
			true
		);
	}

	@Test
	public void testCheckAvailable() {
		InferenceEndpointCapabilityStatus inferenceEndpointCapabilityStatus =
			_inferenceEndpointCapabilityCheckerImpl.check();

		Assert.assertTrue(inferenceEndpointCapabilityStatus.isAvailable());
		Assert.assertEquals("", inferenceEndpointCapabilityStatus.getReason());
	}

	@Test
	public void testCheckElasticsearchVersionBelowMinimum() {
		Mockito.when(
			_searchEngineInformation.getClientVersionString()
		).thenReturn(
			"8.17.4"
		);

		_assertUnavailable(
			"semantic-search.capability.elasticsearch-version-below-minimum");
	}

	@FeatureFlag(enable = false, value = "LPD-11319")
	@Test
	public void testCheckFeatureFlagDisabled() {
		_assertUnavailable("semantic-search.capability.feature-flag-disabled");
	}

	@Test
	public void testCheckMissingDXPEnterprise() {
		Mockito.when(
			_licenseManager.isFreeTier()
		).thenReturn(
			true
		);

		_assertUnavailable("semantic-search.capability.missing-dxp-enterprise");
	}

	@Test
	public void testCheckMissingElasticsearchLicense() {
		Mockito.when(
			_elasticsearchLicenseInformation.isInferenceLicenseAvailable()
		).thenReturn(
			false
		);

		_assertUnavailable(
			"semantic-search.capability.missing-elasticsearch-license");
	}

	@Test
	public void testCheckMissingElasticsearchLicenseWhenSourceUnavailable() {
		_elasticsearchLicenseInformation = null;

		_assertUnavailable(
			"semantic-search.capability.missing-elasticsearch-license");
	}

	private void _assertUnavailable(String expectedReason) {
		InferenceEndpointCapabilityStatus inferenceEndpointCapabilityStatus =
			_inferenceEndpointCapabilityCheckerImpl.check();

		Assert.assertFalse(inferenceEndpointCapabilityStatus.isAvailable());
		Assert.assertEquals(
			expectedReason, inferenceEndpointCapabilityStatus.getReason());
	}

	private ElasticsearchLicenseInformation _elasticsearchLicenseInformation =
		Mockito.mock(ElasticsearchLicenseInformation.class);
	private InferenceEndpointCapabilityCheckerImpl
		_inferenceEndpointCapabilityCheckerImpl;
	private final LicenseManager _licenseManager = Mockito.mock(
		LicenseManager.class);
	private final SearchEngineInformation _searchEngineInformation =
		Mockito.mock(SearchEngineInformation.class);

}