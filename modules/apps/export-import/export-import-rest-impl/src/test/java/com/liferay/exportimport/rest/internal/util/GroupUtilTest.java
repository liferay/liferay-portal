/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.rest.dto.v1_0.AssetLibrary;
import com.liferay.exportimport.rest.dto.v1_0.Site;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.FeatureFlags;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.ws.rs.BadRequestException;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Daniel Raposo
 */
public class GroupUtilTest {

	@ClassRule
	@Rule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@FeatureFlags(
		featureFlags = {
			@FeatureFlag(value = "LPD-41307"), @FeatureFlag("LPD-85946")
		}
	)
	@Test
	public void testGetGroupExternalReferenceCodes() {
		String assetLibraryExternalReferenceCode =
			RandomTestUtil.randomString();
		String siteExternalReferenceCode = RandomTestUtil.randomString();

		Assert.assertArrayEquals(
			new String[] {
				assetLibraryExternalReferenceCode, siteExternalReferenceCode
			},
			GroupUtil.getGroupExternalReferenceCodes(
				_COMPANY_ID,
				new AssetLibrary[] {
					_toAssetLibrary(assetLibraryExternalReferenceCode)
				},
				new Site[] {_toSite(siteExternalReferenceCode)}));

		Assert.assertArrayEquals(
			new String[0],
			GroupUtil.getGroupExternalReferenceCodes(_COMPANY_ID, null, null));
	}

	@Test
	public void testGetGroupExternalReferenceCodesWhenFeatureFlagIsDisabled() {
		Assert.assertArrayEquals(
			new String[0],
			GroupUtil.getGroupExternalReferenceCodes(_COMPANY_ID, null, null));

		try {
			GroupUtil.getGroupExternalReferenceCodes(
				_COMPANY_ID,
				new AssetLibrary[] {
					_toAssetLibrary(RandomTestUtil.randomString())
				},
				null);

			Assert.fail();
		}
		catch (BadRequestException badRequestException) {
			Assert.assertEquals(
				"Asset libraries require the feature flag \"LPD-41307\"",
				badRequestException.getMessage());
		}

		try {
			GroupUtil.getGroupExternalReferenceCodes(
				_COMPANY_ID, null,
				new Site[] {_toSite(RandomTestUtil.randomString())});

			Assert.fail();
		}
		catch (BadRequestException badRequestException) {
			Assert.assertEquals(
				"Sites require the feature flag \"LPD-85946\"",
				badRequestException.getMessage());
		}
	}

	private AssetLibrary _toAssetLibrary(String externalReferenceCode) {
		AssetLibrary assetLibrary = new AssetLibrary();

		assetLibrary.setExternalReferenceCode(externalReferenceCode);

		return assetLibrary;
	}

	private Site _toSite(String externalReferenceCode) {
		Site site = new Site();

		site.setExternalReferenceCode(externalReferenceCode);

		return site;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

}