/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.search.rest.client.dto.v1_0.Suggestion;
import com.liferay.portal.search.rest.client.dto.v1_0.SuggestionsContributorConfiguration;
import com.liferay.portal.search.rest.client.dto.v1_0.SuggestionsContributorResults;
import com.liferay.portal.search.rest.client.pagination.Page;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@RunWith(Arquillian.class)
public class SuggestionResourceTest extends BaseSuggestionResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(), StringUtil.randomString(),
			StringUtil.randomString());
		_layout = LayoutTestUtil.addTypePortletLayout(testGroup);
		_locale = LocaleUtil.getSiteDefault();
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testGroup, TestPropsValues.getUserId());
	}

	@Override
	@Test
	public void testPostSuggestionsPage() throws Exception {
		_testPostSuggestionsPageWithBasicSuggestionsContributor();
		_testPostSuggestionsPageWithBasicSuggestionsContributorWithAssetSearchSummary();
		_testPostSuggestionsPageWithBasicSuggestionsContributorWithCurrentURL();
		_testPostSuggestionsPageWithBasicSuggestionsContributorWithDestinationLayout();
		_testPostSuggestionsPageWithBasicSuggestionsContributorWithEverythingScope();
		_testPostSuggestionsPageWithBasicSuggestionsContributorWithGroupERCScope();
		_testPostSuggestionsPageWithBasicSuggestionsContributorWithSearchResultsPortlet();
		_testPostSuggestionsPageWithBasicSuggestionsContributorWithSearchResultsPortletWithViewInContext();
		_testPostSuggestionsPageWithBasicSuggestionsContributorWithSearchResultsPortletWithoutViewInContext();
		_testPostSuggestionsPageWithBasicSuggestionsContributorWithSearchResultsPortletWithoutViewInContextWithLayout();
		_testPostSuggestionsPageWithBasicSuggestionsContributorWithThisSiteScope();
		_testPostSuggestionsPageWithSuggestionsSizeLimit();
	}

	private FileEntry _addFileEntry() throws Exception {
		String title = RandomTestUtil.randomString();

		return _dlAppLocalService.addFileEntry(
			null, TestPropsValues.getUserId(), testGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, title + ".txt",
			ContentTypes.TEXT_PLAIN, title, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK, RandomTestUtil.randomBytes(), null, null, null,
			_serviceContext);
	}

	private void _assertSuggestionContributorResults(
			String displayGroupName, Page<SuggestionsContributorResults> page,
			String... expectedTexts)
		throws Exception {

		SuggestionsContributorResults suggestionsContributorResults1 = null;

		for (SuggestionsContributorResults suggestionsContributorResults2 :
				page.getItems()) {

			if (!StringUtil.equals(
					suggestionsContributorResults2.getDisplayGroupName(),
					displayGroupName)) {

				continue;
			}

			suggestionsContributorResults1 = suggestionsContributorResults2;
		}

		Assert.assertTrue(suggestionsContributorResults1 != null);

		Suggestion[] suggestions =
			suggestionsContributorResults1.getSuggestions();

		Assert.assertEquals(
			Arrays.toString(suggestions), expectedTexts.length,
			suggestions.length);

		_assertSuggestionTexts(suggestionsContributorResults1, expectedTexts);
	}

	private void _assertSuggestionTexts(
			SuggestionsContributorResults suggestionsContributorResults,
			String... expectedTexts)
		throws Exception {

		Suggestion[] suggestions =
			suggestionsContributorResults.getSuggestions();

		List<String> texts = new ArrayList<>();

		for (Suggestion suggestion : suggestions) {
			texts.add(suggestion.getText());
		}

		Arrays.sort(expectedTexts);

		Collections.sort(texts);

		Assert.assertEquals(
			Arrays.toString(expectedTexts), String.valueOf(texts));
	}

	private void _assertSuggestionsCounts(
			int[] expectedCounts, String search, int[] suggestionsSizes)
		throws Exception {

		SuggestionsContributorConfiguration[]
			suggestionsContributorConfigurations =
				new SuggestionsContributorConfiguration
					[suggestionsSizes.length];

		for (int i = 0; i < suggestionsSizes.length; i++) {
			int suggestionsSize = suggestionsSizes[i];

			suggestionsContributorConfigurations[i] =
				new SuggestionsContributorConfiguration() {
					{
						contributorName = "basic";
						displayGroupName = RandomTestUtil.randomString();
						size = suggestionsSize;
					}
				};
		}

		Page<SuggestionsContributorResults> suggestionsPage =
			_postSuggestionsPage(
				"http://localhost:" + PortalUtil.getPortalServerPort(false) +
					"/web/guest/home",
				"/search", null, "q", _layout.getPlid(), null, search,
				suggestionsContributorConfigurations);

		List<Integer> suggestionsCounts = TransformUtil.transform(
			suggestionsPage.getItems(),
			suggestionsContributorResults -> {
				Suggestion[] suggestions =
					suggestionsContributorResults.getSuggestions();

				return suggestions.length;
			});

		Assert.assertEquals(
			Arrays.toString(expectedCounts), suggestionsCounts.toString());
	}

	private String _getAssetURL(Layout destinationLayout, String search)
		throws Exception {

		Page<SuggestionsContributorResults> suggestionsPage =
			_postSuggestionsPage(
				"http://localhost:" + PortalUtil.getPortalServerPort(false) +
					"/web/guest/home",
				destinationLayout.getFriendlyURL(), null, "q",
				_layout.getPlid(), null, search,
				new SuggestionsContributorConfiguration[] {
					new SuggestionsContributorConfiguration() {
						{
							contributorName = "basic";
							displayGroupName = "Suggestions";
						}
					}
				});

		SuggestionsContributorResults suggestionsContributorResults =
			suggestionsPage.fetchFirstItem();

		Suggestion[] suggestions =
			suggestionsContributorResults.getSuggestions();

		JSONObject suggestionAttributesJSONObject =
			JSONFactoryUtil.createJSONObject(
				String.valueOf(suggestions[0].getAttributes()));

		return suggestionAttributesJSONObject.getString("assetURL");
	}

	private Page<SuggestionsContributorResults> _postSuggestionsPage(
			String currentURL, String destinationFriendlyURL, Long groupId,
			String keywordsParameterName, Long plid, String scope,
			String search,
			SuggestionsContributorConfiguration[]
				suggestionsContributorConfigurations)
		throws Exception {

		return suggestionResource.postSuggestionsPage(
			currentURL, destinationFriendlyURL, groupId, keywordsParameterName,
			plid, scope, search, suggestionsContributorConfigurations);
	}

	private Page<SuggestionsContributorResults>
			_postSuggestionsPageWithBasicSuggestionsContributor(
				String scope, String search,
				String suggestionsDisplayGroupGroupName)
		throws Exception {

		return _postSuggestionsPage(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/web/guest/home",
			"/search", null, "q", _layout.getPlid(), scope, search,
			new SuggestionsContributorConfiguration[] {
				new SuggestionsContributorConfiguration() {
					{
						contributorName = "basic";
						displayGroupName = suggestionsDisplayGroupGroupName;
					}
				}
			});
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributor()
		throws Exception {

		String displayGroupName = "Suggestions";

		Page<SuggestionsContributorResults> suggestionsPage =
			_postSuggestionsPageWithBasicSuggestionsContributor(
				null, _journalArticle.getArticleId(), displayGroupName);

		_assertSuggestionContributorResults(
			displayGroupName, suggestionsPage,
			_journalArticle.getTitle(_locale));
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributorWithAssetSearchSummary()
		throws Exception {

		Page<SuggestionsContributorResults> page =
			_postSuggestionsPageWithBasicSuggestionsContributor(
				null, _journalArticle.getArticleId(), "Suggestions");

		SuggestionsContributorResults suggestionsContributorResults =
			page.fetchFirstItem();

		Suggestion[] suggestions =
			suggestionsContributorResults.getSuggestions();

		Suggestion suggestion = suggestions[0];

		JSONObject suggestionAttributesJSONObject =
			JSONFactoryUtil.createJSONObject(
				String.valueOf(suggestion.getAttributes()));

		Assert.assertEquals(
			_journalArticle.getDescription(_locale),
			suggestionAttributesJSONObject.get("assetSearchSummary"));
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributorWithCurrentURL()
		throws Exception {

		String currentURL =
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/web/guest/home";

		Page<SuggestionsContributorResults> page = _postSuggestionsPage(
			HttpComponentsUtil.addParameters(
				currentURL, "p_l_back_url", currentURL, "p_l_back_url_title",
				RandomTestUtil.randomString()),
			"/search", null, "q", _layout.getPlid(), null,
			_journalArticle.getArticleId(),
			new SuggestionsContributorConfiguration[] {
				new SuggestionsContributorConfiguration() {
					{
						contributorName = "basic";
						displayGroupName = "Suggestions";
					}
				}
			});

		SuggestionsContributorResults suggestionsContributorResults =
			page.fetchFirstItem();

		Suggestion[] suggestions =
			suggestionsContributorResults.getSuggestions();

		JSONObject suggestionAttributesJSONObject =
			JSONFactoryUtil.createJSONObject(
				String.valueOf(suggestions[0].getAttributes()));

		String assetURL = suggestionAttributesJSONObject.getString("assetURL");

		Assert.assertEquals(
			URLCodec.encodeURL(currentURL),
			HttpComponentsUtil.getParameter(assetURL, "p_l_back_url", false));
		Assert.assertFalse(assetURL, assetURL.contains("_redirect="));
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributorWithDestinationLayout()
		throws Exception {

		Layout destinationLayout = LayoutTestUtil.addTypePortletLayout(
			testGroup);

		Page<SuggestionsContributorResults> page = _postSuggestionsPage(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/web/guest/home",
			destinationLayout.getFriendlyURL(), null, "q", _layout.getPlid(),
			null, _journalArticle.getArticleId(),
			new SuggestionsContributorConfiguration[] {
				new SuggestionsContributorConfiguration() {
					{
						contributorName = "basic";
						displayGroupName = "Suggestions";
					}
				}
			});

		SuggestionsContributorResults suggestionsContributorResults =
			page.fetchFirstItem();

		Suggestion[] suggestions =
			suggestionsContributorResults.getSuggestions();

		JSONObject suggestionAttributesJSONObject =
			JSONFactoryUtil.createJSONObject(
				String.valueOf(suggestions[0].getAttributes()));

		String assetURL = suggestionAttributesJSONObject.getString("assetURL");

		Assert.assertTrue(
			assetURL,
			assetURL.contains(destinationLayout.getFriendlyURL()) ||
			assetURL.contains("p_l_id=" + destinationLayout.getPlid()));
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributorWithEverythingScope()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			group.getGroupId(), StringUtil.randomString(),
			StringUtil.randomString());

		Page<SuggestionsContributorResults> page =
			_postSuggestionsPageWithBasicSuggestionsContributor(
				"everything",
				StringBundler.concat(
					_journalArticle.getTitle(_locale), StringPool.SPACE,
					journalArticle.getTitle(_locale)),
				"Suggestions");

		_assertSuggestionTexts(
			page.fetchFirstItem(), _journalArticle.getTitle(_locale),
			journalArticle.getTitle(_locale));

		GroupTestUtil.deleteGroup(group);
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributorWithGroupERCScope()
		throws Exception {

		Page<SuggestionsContributorResults> page =
			_postSuggestionsPageWithBasicSuggestionsContributor(
				testGroup.getExternalReferenceCode(),
				_journalArticle.getArticleId(), "Suggestions");

		_assertSuggestionTexts(
			page.fetchFirstItem(), _journalArticle.getTitle(_locale));
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributorWithSearchResultsPortlet()
		throws Exception {

		Layout destinationLayout = LayoutTestUtil.addTypePortletLayout(
			testGroup);

		String searchResultsPortletId = LayoutTestUtil.addPortletToLayout(
			destinationLayout,
			"com_liferay_portal_search_web_search_results_portlet_" +
				"SearchResultsPortlet");

		Page<SuggestionsContributorResults> page = _postSuggestionsPage(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/web/guest/home",
			destinationLayout.getFriendlyURL(), null, "q", _layout.getPlid(),
			null, _journalArticle.getArticleId(),
			new SuggestionsContributorConfiguration[] {
				new SuggestionsContributorConfiguration() {
					{
						contributorName = "basic";
						displayGroupName = "Suggestions";
					}
				}
			});

		SuggestionsContributorResults suggestionsContributorResults =
			page.fetchFirstItem();

		Suggestion[] suggestions =
			suggestionsContributorResults.getSuggestions();

		JSONObject suggestionAttributesJSONObject =
			JSONFactoryUtil.createJSONObject(
				String.valueOf(suggestions[0].getAttributes()));

		String assetURL = suggestionAttributesJSONObject.getString("assetURL");

		Assert.assertTrue(assetURL, assetURL.contains(searchResultsPortletId));
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributorWithSearchResultsPortletWithViewInContext()
		throws Exception {

		LayoutTestUtil.addPortletToLayout(
			LayoutTestUtil.addTypePortletLayout(testGroup),
			"com_liferay_document_library_web_portlet_DLPortlet");

		Layout destinationLayout = LayoutTestUtil.addTypePortletLayout(
			testGroup);

		LayoutTestUtil.addPortletToLayout(
			destinationLayout,
			"com_liferay_portal_search_web_search_results_portlet_" +
				"SearchResultsPortlet");

		FileEntry fileEntry = _addFileEntry();

		String assetURL = _getAssetURL(destinationLayout, fileEntry.getTitle());

		Assert.assertTrue(assetURL.contains("find_file_entry"));
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributorWithSearchResultsPortletWithoutViewInContext()
		throws Exception {

		LayoutTestUtil.addPortletToLayout(
			LayoutTestUtil.addTypePortletLayout(testGroup),
			"com_liferay_document_library_web_portlet_DLPortlet");

		Layout destinationLayout = LayoutTestUtil.addTypePortletLayout(
			testGroup);

		String searchResultsPortletId = LayoutTestUtil.addPortletToLayout(
			destinationLayout,
			"com_liferay_portal_search_web_search_results_portlet_" +
				"SearchResultsPortlet",
			HashMapBuilder.put(
				"viewInContext", new String[] {"false"}
			).build());

		FileEntry fileEntry = _addFileEntry();

		String assetURL = _getAssetURL(destinationLayout, fileEntry.getTitle());

		Assert.assertFalse(assetURL.contains("find_file_entry"));
		Assert.assertTrue(assetURL.contains(searchResultsPortletId));
		Assert.assertTrue(assetURL.contains("view_content.jsp"));
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributorWithSearchResultsPortletWithoutViewInContextWithLayout()
		throws Exception {

		Layout destinationLayout = LayoutTestUtil.addTypePortletLayout(
			testGroup);

		LayoutTestUtil.addPortletToLayout(
			destinationLayout,
			"com_liferay_portal_search_web_search_results_portlet_" +
				"SearchResultsPortlet",
			HashMapBuilder.put(
				"viewInContext", new String[] {"false"}
			).build());

		Layout layout = LayoutTestUtil.addTypePortletLayout(testGroup);

		String assetURL = _getAssetURL(
			destinationLayout, layout.getName(_locale));

		Assert.assertFalse(assetURL.contains("view_content.jsp"));
		Assert.assertTrue(assetURL.contains(layout.getFriendlyURL(_locale)));
	}

	private void _testPostSuggestionsPageWithBasicSuggestionsContributorWithThisSiteScope()
		throws Exception {

		Group group = GroupTestUtil.addGroup();

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			group.getGroupId(), StringUtil.randomString(),
			StringUtil.randomString());

		Page<SuggestionsContributorResults> page = _postSuggestionsPage(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/web/guest/home",
			"/search", testGroup.getGroupId(), "q", _layout.getPlid(),
			"this-site",
			StringBundler.concat(
				_journalArticle.getTitle(_locale), StringPool.SPACE,
				journalArticle.getTitle(_locale)),
			new SuggestionsContributorConfiguration[] {
				new SuggestionsContributorConfiguration() {
					{
						contributorName = "basic";
						displayGroupName = "Suggestions";
					}
				}
			});

		_assertSuggestionTexts(
			page.fetchFirstItem(), _journalArticle.getTitle(_locale));

		GroupTestUtil.deleteGroup(group);
	}

	private void _testPostSuggestionsPageWithSuggestionsSizeLimit()
		throws Exception {

		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			testGroup.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		String search = StringBundler.concat(
			_journalArticle.getTitle(_locale), StringPool.SPACE,
			journalArticle1.getTitle(_locale), StringPool.SPACE,
			journalArticle2.getTitle(_locale));

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						"com.liferay.portal.search.rest.configuration." +
							"SearchSuggestionsCompanyConfiguration",
						MapUtil.singletonDictionary(
							"suggestionsSizeLimit", 2))) {

			_assertSuggestionsCounts(new int[] {1}, search, new int[] {1});
			_assertSuggestionsCounts(
				new int[] {2, 1, 2}, search, new int[] {3, 1, 3});
			_assertSuggestionsCounts(new int[] {2}, search, new int[] {3});
		}

		_assertSuggestionsCounts(new int[] {3}, search, new int[] {3});
	}

	@Inject
	private DLAppLocalService _dlAppLocalService;

	private JournalArticle _journalArticle;
	private Layout _layout;
	private Locale _locale;
	private ServiceContext _serviceContext;

}