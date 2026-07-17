/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.cms.site.initializer.internal.frontend.data.set.filter;

import com.liferay.frontend.data.set.constants.FDSEntityFieldTypes;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Fábio Alves
 */
public class AssetCategorySelectionPersonaFDSFilterTest
	extends BaseAssetCategorySelectionFDSFilterTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetProperties() {
		Assert.assertEquals(
			FDSEntityFieldTypes.INTEGER,
			_assetCategorySelectionPersonaFDSFilter.getEntityFieldType());
		Assert.assertEquals(
			"cmpPersonaCategoryIds",
			_assetCategorySelectionPersonaFDSFilter.getId());
		Assert.assertEquals(
			"persona", _assetCategorySelectionPersonaFDSFilter.getLabel());
	}

	@Override
	protected String getAssetVocabularyExternalReferenceCode() {
		return "L_CMP_PERSONAS";
	}

	@Override
	protected BaseAssetCategorySelectionFDSFilter
		getBaseAssetCategorySelectionFDSFilter() {

		return _assetCategorySelectionPersonaFDSFilter;
	}

	private final AssetCategorySelectionPersonaFDSFilter
		_assetCategorySelectionPersonaFDSFilter =
			new AssetCategorySelectionPersonaFDSFilter();

}