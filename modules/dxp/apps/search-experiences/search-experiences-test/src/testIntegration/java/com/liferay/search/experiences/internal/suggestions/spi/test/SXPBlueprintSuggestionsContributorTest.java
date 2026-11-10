/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.internal.suggestions.spi.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.rest.client.dto.v1_0.Suggestion;
import com.liferay.portal.search.rest.client.dto.v1_0.SuggestionsContributorConfiguration;
import com.liferay.portal.search.rest.client.dto.v1_0.SuggestionsContributorResults;
import com.liferay.portal.search.rest.client.pagination.Page;
import com.liferay.portal.search.rest.client.resource.v1_0.SuggestionResource;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.search.experiences.model.SXPBlueprint;
import com.liferay.search.experiences.service.SXPBlueprintLocalService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Petteri Karttunen
 */
@RunWith(Arquillian.class)
public class SXPBlueprintSuggestionsContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		Company company = _companyLocalService.getCompany(
			_group.getCompanyId());

		User user = UserTestUtil.getAdminUser(company.getCompanyId());

		_suggestionResource = SuggestionResource.builder(
		).authentication(
			user.getEmailAddress(),
			PropsUtil.get(PropsKeys.DEFAULT_ADMIN_PASSWORD)
		).endpoint(
			company.getVirtualHostname(), PortalUtil.getPortalServerPort(false),
			"http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		_journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(), StringUtil.randomString(),
			StringUtil.randomString());
		_layout = LayoutTestUtil.addTypePortletLayout(_group);
		_locale = LocaleUtil.getSiteDefault();
		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group, TestPropsValues.getUserId());
	}

	@Test
	public void testPostSuggestionsPage() throws Exception {
		_testPostSuggestionsPageWithSXPBlueprintSuggestionsContributor();
		_testPostSuggestionsPageWithSXPBlueprintSuggestionsContributorWithGroupERCScope();
		_testPostSuggestionsPageWithSXPBlueprintSuggestionsContributorWithSearchExperiencesAttributes();
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

	private Page<SuggestionsContributorResults> _postSuggestionsPage(
			String currentURL, String destinationFriendlyURL, Long groupId,
			String keywordsParameterName, Long plid, String scope,
			String search,
			SuggestionsContributorConfiguration[]
				suggestionsContributorConfigurations)
		throws Exception {

		return _suggestionResource.postSuggestionsPage(
			currentURL, destinationFriendlyURL, groupId, keywordsParameterName,
			plid, scope, search, suggestionsContributorConfigurations);
	}

	private void _testPostSuggestionsPageWithSXPBlueprintSuggestionsContributor()
		throws Exception {

		String suggestionsDisplayGroupGroupName = "Suggestions";

		SXPBlueprint sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
			null, TestPropsValues.getUserId(), "{}",
			Collections.singletonMap(LocaleUtil.US, ""), null, "",
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			_serviceContext);

		_sxpBlueprints.add(sxpBlueprint);

		Page<SuggestionsContributorResults> page = _postSuggestionsPage(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/web/guest/home",
			"/search", _group.getGroupId(), "q", _layout.getPlid(), null,
			_journalArticle.getArticleId(),
			new SuggestionsContributorConfiguration[] {
				new SuggestionsContributorConfiguration() {
					{
						attributes = JSONUtil.put(
							"sxpBlueprintExternalReferenceCode",
							sxpBlueprint.getExternalReferenceCode());
						contributorName = "sxpBlueprint";
						displayGroupName = suggestionsDisplayGroupGroupName;
					}
				}
			});

		_assertSuggestionContributorResults(
			suggestionsDisplayGroupGroupName, page,
			_journalArticle.getTitle(_locale));
	}

	private void _testPostSuggestionsPageWithSXPBlueprintSuggestionsContributorWithGroupERCScope()
		throws Exception {

		SXPBlueprint sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
			null, TestPropsValues.getUserId(), "{}",
			Collections.singletonMap(LocaleUtil.US, ""), null, "",
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			_serviceContext);

		_sxpBlueprints.add(sxpBlueprint);

		String suggestionsDisplayGroupGroupName = "Suggestions";

		Page<SuggestionsContributorResults> page = _postSuggestionsPage(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/web/guest/home",
			"/search", null, "q", _layout.getPlid(),
			_group.getExternalReferenceCode(), _journalArticle.getArticleId(),
			new SuggestionsContributorConfiguration[] {
				new SuggestionsContributorConfiguration() {
					{
						attributes = JSONUtil.put(
							"sxpBlueprintExternalReferenceCode",
							sxpBlueprint.getExternalReferenceCode());
						contributorName = "sxpBlueprint";
						displayGroupName = suggestionsDisplayGroupGroupName;
					}
				}
			});

		_assertSuggestionContributorResults(
			suggestionsDisplayGroupGroupName, page,
			_journalArticle.getTitle(_locale));
	}

	private void _testPostSuggestionsPageWithSXPBlueprintSuggestionsContributorWithSearchExperiencesAttributes()
		throws Exception {

		Class<?> clazz = getClass();

		SXPBlueprint sxpBlueprint = _sxpBlueprintLocalService.addSXPBlueprint(
			null, TestPropsValues.getUserId(),
			StringUtil.read(
				clazz,
				StringBundler.concat(
					"dependencies/", clazz.getSimpleName(),
					"._testPostSuggestionsPageWithSXPBlueprintSuggestions",
					"ContributorWithSearchExperiencesAttributes.json")),
			Collections.singletonMap(LocaleUtil.US, StringPool.BLANK), null,
			StringPool.BLANK,
			Collections.singletonMap(
				LocaleUtil.US, RandomTestUtil.randomString()),
			_serviceContext);

		_sxpBlueprints.add(sxpBlueprint);

		String suggestionsDisplayGroupGroupName = "Suggestions";

		Page<SuggestionsContributorResults> page = _postSuggestionsPage(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/web/guest/home",
			"/search", _group.getGroupId(), "q", _layout.getPlid(), null,
			_journalArticle.getArticleId(),
			new SuggestionsContributorConfiguration[] {
				new SuggestionsContributorConfiguration() {
					{
						attributes = JSONUtil.put(
							"search.experiences.entry.class.pk",
							_journalArticle.getResourcePrimKey()
						).put(
							"sxpBlueprintExternalReferenceCode",
							sxpBlueprint.getExternalReferenceCode()
						);
						contributorName = "sxpBlueprint";
						displayGroupName = suggestionsDisplayGroupGroupName;
					}
				}
			});

		_assertSuggestionContributorResults(
			suggestionsDisplayGroupGroupName, page,
			_journalArticle.getTitle(_locale));
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private JournalArticle _journalArticle;
	private Layout _layout;
	private Locale _locale;
	private ServiceContext _serviceContext;
	private SuggestionResource _suggestionResource;

	@Inject
	private SXPBlueprintLocalService _sxpBlueprintLocalService;

	@DeleteAfterTestRun
	private final List<SXPBlueprint> _sxpBlueprints = new ArrayList<>();

}