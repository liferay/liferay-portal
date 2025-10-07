/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.suggestions.spi;

import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.search.spi.suggestions.SuggestionsContributor;
import com.liferay.portal.search.suggestions.Suggestion;
import com.liferay.portal.search.suggestions.SuggestionsContributorResults;
import com.liferay.portal.search.test.util.suggestions.BaseSuggestionsContributorTestCase;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class BasicSuggestionsContributorTest
	extends BaseSuggestionsContributorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetSuggestionWithAssetRenderer() throws Exception {
		int totalHits = 2;

		setUpAssetRendererFactoryRegistryUtil(
			false, "Class Name 2", "Asset Renderer Title",
			"Asset Renderer Summary");
		setUpSearcher(totalHits);

		SuggestionsContributorResults suggestionsContributorResults =
			getSuggestionsContributorResults();

		Assert.assertEquals(
			"testGetSuggestionWithAssetRenderer",
			suggestionsContributorResults.getDisplayGroupName());

		List<Suggestion> suggestions =
			suggestionsContributorResults.getSuggestions();

		for (int i = 1; i <= totalHits; i++) {
			Suggestion suggestion = suggestions.get(i - 1);

			Assert.assertEquals(
				"Asset Renderer Summary",
				suggestion.getAttribute("assetSearchSummary"));
			Assert.assertEquals(
				"Class Name 1_1", suggestion.getAttribute("assetURL"));
			Assert.assertEquals(
				HashMapBuilder.<String, Object>put(
					Field.ENTRY_CLASS_NAME, "Class Name 1"
				).build(),
				suggestion.getAttribute("fields"));
			Assert.assertEquals(i, suggestion.getScore(), i);
			Assert.assertEquals("Asset Renderer Title", suggestion.getText());
		}
	}

	@Test
	public void testGetSuggestionWithAssetRendererFactoryNull()
		throws Exception {

		setUpAssetRendererFactoryRegistryUtil(true, null, null);
		setUpSearcher(1);

		SuggestionsContributorResults suggestionsContributorResults =
			getSuggestionsContributorResults();

		Assert.assertEquals(
			"testGetSuggestionWithAssetRendererFactoryNull",
			suggestionsContributorResults.getDisplayGroupName());

		List<Suggestion> suggestions =
			suggestionsContributorResults.getSuggestions();

		Assert.assertEquals(suggestions.toString(), 0, suggestions.size());

		Mockito.verify(
			assetRendererFactory, Mockito.never()
		).getAssetRenderer(
			Mockito.anyLong()
		);
	}

	@Test
	public void testGetSuggestionWithAssetRendererTitleNull() throws Exception {
		int totalHits = 2;

		setUpAssetRendererFactoryRegistryUtil(null, null);
		setUpSearcher(totalHits);

		SuggestionsContributorResults suggestionsContributorResults =
			getSuggestionsContributorResults();

		Assert.assertEquals(
			"testGetSuggestionWithAssetRendererTitleNull",
			suggestionsContributorResults.getDisplayGroupName());

		List<Suggestion> suggestions =
			suggestionsContributorResults.getSuggestions();

		for (int i = 1; i <= totalHits; i++) {
			Suggestion suggestion = suggestions.get(i - 1);

			Assert.assertEquals(
				null, suggestion.getAttribute("assetSearchSummary"));
			Assert.assertEquals(
				HashMapBuilder.<String, Object>put(
					Field.ENTRY_CLASS_NAME, "Class Name 1"
				).build(),
				suggestion.getAttribute("fields"));
			Assert.assertEquals(i, suggestion.getScore(), i);
			Assert.assertEquals("Document Title " + i, suggestion.getText());
		}
	}

	@Override
	protected String getKeywords() {
		return "title";
	}

	@Override
	protected SuggestionsContributor getSuggestionsContributor() {
		return new BasicSuggestionsContributor();
	}

}