/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.web.internal.info.item.handler;

import com.liferay.asset.kernel.exception.AssetCategoryException;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.info.exception.InfoFormValidationException;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Jan Brychta
 */
public class ObjectEntryInfoItemExceptionRequestHandlerTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	@TestInfo("LPD-96532")
	public void testHandleInfoFormException() throws Exception {
		_testHandleInfoFormExceptionWhenAssetCategoryExceptionTypeIsAtLeastOneCategory();
		_testHandleInfoFormExceptionWhenAssetCategoryExceptionTypeIsTooManyCategories();
	}

	private void _testHandleInfoFormExceptionWhenAssetCategoryExceptionTypeIsAtLeastOneCategory()
		throws Exception {

		AssetVocabulary assetVocabulary = Mockito.mock(AssetVocabulary.class);

		try {
			ObjectEntryInfoItemExceptionRequestHandler.handleInfoFormException(
				new AssetCategoryException(
					assetVocabulary,
					AssetCategoryException.AT_LEAST_ONE_CATEGORY),
				0, null, null);

			Assert.fail();
		}
		catch (InfoFormValidationException.RequiredAssetCategory
					infoFormValidationException) {

			Assert.assertSame(
				assetVocabulary,
				infoFormValidationException.getAssetVocabulary());
		}
	}

	private void _testHandleInfoFormExceptionWhenAssetCategoryExceptionTypeIsTooManyCategories()
		throws Exception {

		AssetVocabulary assetVocabulary = Mockito.mock(AssetVocabulary.class);

		try {
			ObjectEntryInfoItemExceptionRequestHandler.handleInfoFormException(
				new AssetCategoryException(
					assetVocabulary,
					AssetCategoryException.TOO_MANY_CATEGORIES),
				0, null, null);

			Assert.fail();
		}
		catch (InfoFormValidationException.AssetTooManyCategories
					infoFormValidationException) {

			Assert.assertSame(
				assetVocabulary,
				infoFormValidationException.getAssetVocabulary());
		}
	}

}