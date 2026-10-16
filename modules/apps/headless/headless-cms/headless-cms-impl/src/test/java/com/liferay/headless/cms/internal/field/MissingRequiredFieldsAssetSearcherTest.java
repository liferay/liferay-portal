/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.internal.field;

import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Localization;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.pagination.Pagination;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Mikel Lorza
 */
public class MissingRequiredFieldsAssetSearcherTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testSearch() {
		LocalizationUtil localizationUtil = new LocalizationUtil();

		localizationUtil.setLocalization(Mockito.mock(Localization.class));

		SearchRequestBuilder searchRequestBuilder = Mockito.mock(
			SearchRequestBuilder.class, Mockito.RETURNS_SELF);

		SearchRequestBuilderFactory searchRequestBuilderFactory = Mockito.mock(
			SearchRequestBuilderFactory.class);

		Mockito.when(
			searchRequestBuilderFactory.builder()
		).thenReturn(
			searchRequestBuilder
		);

		MissingRequiredFieldsAssetSearcher missingRequiredFieldsAssetSearcher =
			new MissingRequiredFieldsAssetSearcher(
				Mockito.mock(Searcher.class), searchRequestBuilderFactory);

		missingRequiredFieldsAssetSearcher.search(
			RandomTestUtil.randomLong(),
			new Long[] {RandomTestUtil.randomLong()},
			RandomTestUtil.randomString(), Pagination.of(501, 20), null, null);

		Mockito.verify(
			searchRequestBuilder
		).from(
			10000
		);

		Mockito.verify(
			searchRequestBuilder
		).size(
			20
		);
	}

}