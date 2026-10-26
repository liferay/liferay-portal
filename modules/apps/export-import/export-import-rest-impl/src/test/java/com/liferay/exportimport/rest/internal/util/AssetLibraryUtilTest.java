/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.exportimport.rest.dto.v1_0.AssetLibrary;
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
public class AssetLibraryUtilTest {

	@ClassRule
	@Rule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@FeatureFlags(featureFlags = @FeatureFlag("LPD-41307"))
	@Test
	public void testGetExternalReferenceCodes() {
		String externalReferenceCode = RandomTestUtil.randomString();

		Assert.assertArrayEquals(
			new String[] {externalReferenceCode},
			AssetLibraryUtil.getExternalReferenceCodes(
				new AssetLibrary[] {_toAssetLibrary(externalReferenceCode)},
				_COMPANY_ID));

		Assert.assertArrayEquals(
			new String[0],
			AssetLibraryUtil.getExternalReferenceCodes(null, _COMPANY_ID));
	}

	@Test
	public void testGetExternalReferenceCodesWhenFeatureFlagIsDisabled() {
		Assert.assertArrayEquals(
			new String[0],
			AssetLibraryUtil.getExternalReferenceCodes(null, _COMPANY_ID));

		try {
			AssetLibraryUtil.getExternalReferenceCodes(
				new AssetLibrary[] {
					_toAssetLibrary(RandomTestUtil.randomString())
				},
				_COMPANY_ID);

			Assert.fail();
		}
		catch (BadRequestException badRequestException) {
			Assert.assertEquals(
				"Asset libraries require the feature flag \"LPD-41307\"",
				badRequestException.getMessage());
		}
	}

	private AssetLibrary _toAssetLibrary(String externalReferenceCode) {
		AssetLibrary assetLibrary = new AssetLibrary();

		assetLibrary.setExternalReferenceCode(externalReferenceCode);

		return assetLibrary;
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

}