/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.internal.util;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.exportimport.rest.dto.v1_0.AssetLibrary;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

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

	@Test
	public void testToAssetLibraryType() {
		Assert.assertNull(
			AssetLibraryUtil.toAssetLibraryType(DepotConstants.TYPE_ANY));
		Assert.assertEquals(
			AssetLibrary.Type.ASSET_LIBRARY,
			AssetLibraryUtil.toAssetLibraryType(
				DepotConstants.TYPE_ASSET_LIBRARY));
		Assert.assertEquals(
			AssetLibrary.Type.DESIGN_LIBRARY,
			AssetLibraryUtil.toAssetLibraryType(
				DepotConstants.TYPE_DESIGN_LIBRARY));
		Assert.assertEquals(
			AssetLibrary.Type.PROJECT,
			AssetLibraryUtil.toAssetLibraryType(DepotConstants.TYPE_PROJECT));
		Assert.assertEquals(
			AssetLibrary.Type.SPACE,
			AssetLibraryUtil.toAssetLibraryType(DepotConstants.TYPE_SPACE));
	}

	@Test
	public void testToDepotEntryType() {
		Assert.assertEquals(
			DepotConstants.TYPE_ANY,
			AssetLibraryUtil.toDepotEntryType(RandomTestUtil.randomString()));
		Assert.assertEquals(
			DepotConstants.TYPE_ASSET_LIBRARY,
			AssetLibraryUtil.toDepotEntryType(
				AssetLibrary.Type.ASSET_LIBRARY.getValue()));
		Assert.assertEquals(
			DepotConstants.TYPE_DESIGN_LIBRARY,
			AssetLibraryUtil.toDepotEntryType(
				AssetLibrary.Type.DESIGN_LIBRARY.getValue()));
		Assert.assertEquals(
			DepotConstants.TYPE_PROJECT,
			AssetLibraryUtil.toDepotEntryType(
				AssetLibrary.Type.PROJECT.getValue()));
		Assert.assertEquals(
			DepotConstants.TYPE_SPACE,
			AssetLibraryUtil.toDepotEntryType(
				StringUtil.toLowerCase(AssetLibrary.Type.SPACE.getValue())));
	}

}