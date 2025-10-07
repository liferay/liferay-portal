/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.suggestions.spi;

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
 * @author Gustavo Lima
 */
public class SXPBlueprintSuggestionsContributorTest
	extends BaseSuggestionsContributorTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetSuggestionsContributorResults() throws Exception {
		int totalHits = 2;

		setUpAssetRendererFactoryRegistryUtil(
			false, "Class Name 2", "Asset Renderer Title",
			"Asset Renderer Summary");
		setUpSearcher(totalHits);
		setUpSuggestionsContributorConfiguration("testField");

		SuggestionsContributorResults suggestionsContributorResults =
			getSuggestionsContributorResults();

		Assert.assertEquals(
			"testGetSuggestionsContributorResults",
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
			Assert.assertEquals(i, suggestion.getScore(), i);
			Assert.assertEquals("Document Text " + i, suggestion.getText());
		}
	}

	@Test
	public void testGetSuggestionsContributorResultsWithNullTextFieldName()
		throws Exception {

		int totalHits = 1;

		setUpAssetRendererFactoryRegistryUtil(
			"Asset Renderer Title", "Asset Renderer Summary");
		setUpSearcher(totalHits);
		setUpSuggestionsContributorConfiguration(null);

		SuggestionsContributorResults suggestionsContributorResults =
			getSuggestionsContributorResults();

		List<Suggestion> suggestions =
			suggestionsContributorResults.getSuggestions();

		for (int i = 1; i <= totalHits; i++) {
			Suggestion suggestion = suggestions.get(i - 1);

			Assert.assertEquals(
				"Asset Renderer Summary",
				suggestion.getAttribute("assetSearchSummary"));
			Assert.assertEquals(i, suggestion.getScore(), i);
			Assert.assertEquals("Asset Renderer Title", suggestion.getText());
		}
	}

	@Override
	@Test
	public void testSearchTuningRankingsIsContributed() throws Exception {
		setUpSuggestionsContributorConfiguration(null);

		super.testSearchTuningRankingsIsContributed();
	}

	@Test
	public void testSuggestionsContributorConfigurationWithAssetRendererNull()
		throws Exception {

		int totalHits = 1;

		setUpSearcher(totalHits);

		setUpAssetRendererFactoryRegistryUtil(true, null, null);

		setUpSuggestionsContributorConfiguration("testField");

		SuggestionsContributorResults suggestionsContributorResults =
			getSuggestionsContributorResults();

		Assert.assertEquals(
			"testSuggestionsContributorConfigurationWithAssetRendererNull",
			suggestionsContributorResults.getDisplayGroupName());

		List<Suggestion> suggestions =
			suggestionsContributorResults.getSuggestions();

		for (int i = 1; i <= totalHits; i++) {
			Suggestion suggestion = suggestions.get(i - 1);

			Assert.assertNull(suggestion.getAttribute("assetSearchSummary"));
			Assert.assertEquals(i, suggestion.getScore(), i);
			Assert.assertEquals("Document Text " + i, suggestion.getText());
		}
	}

	@Test
	public void testSuggestionsContributorConfigurationWithNullAttributes() {
		Assert.assertNull(getSuggestionsContributorResults());

		Mockito.verify(
			searcher, Mockito.never()
		).search(
			Mockito.any()
		);
	}

	@Override
	protected String getKeywords() {
		return "test";
	}

	@Override
	protected SuggestionsContributor getSuggestionsContributor() {
		return new SXPBlueprintSuggestionsContributor();
	}

}