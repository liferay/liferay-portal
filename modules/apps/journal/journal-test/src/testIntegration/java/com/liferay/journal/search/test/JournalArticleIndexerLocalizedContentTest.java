/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprint;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.legacy.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.search.test.util.IndexerFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class JournalArticleIndexerLocalizedContentTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_indexerFixture = new IndexerFixture<>(
			JournalArticle.class, _searchRequestBuilderFactory);

		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			_ddmStructureLocalService, _journalArticleLocalService, _portal);

		_journalArticleSearchFixture.setUp();

		_journalArticles = _journalArticleSearchFixture.getJournalArticles();

		UserTestUtil.setUser(TestPropsValues.getUser());
	}

	@After
	public void tearDown() throws Exception {
		_journalArticleSearchFixture.tearDown();
	}

	@Test
	public void testIndexedFields() throws Exception {
		String originalTitle = "entity title";
		String translatedTitle = "entitas neve";

		String originalContent = RandomTestUtil.randomString();
		String translatedContent = RandomTestUtil.randomString();

		JournalArticle journalArticle = _journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(LocaleUtil.US, originalContent);
								put(LocaleUtil.HUNGARY, translatedContent);

								setDefaultLocale(LocaleUtil.US);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.US, originalTitle);
								put(LocaleUtil.HUNGARY, translatedTitle);
							}
						});
				}
			});

		SearchResponse searchResponse =
			_indexerFixture.searchOnlyOneSearchResponse(
				"nev", LocaleUtil.HUNGARY);

		FieldValuesAssert.assertFieldValues(
			HashMapBuilder.put(
				"title_en_US", originalTitle
			).put(
				"title_hu_HU", translatedTitle
			).build(),
			name -> name.startsWith("title_"), searchResponse);
		FieldValuesAssert.assertFieldValues(
			HashMapBuilder.put(
				"content_en_US", originalContent
			).put(
				"content_hu_HU", translatedContent
			).build(),
			name -> name.startsWith("content_"), searchResponse);
		FieldValuesAssert.assertFieldValues(
			_getLocalizedKeywordSorteableMap(
				originalTitle, "localized_title", false,
				locale -> {
					if (Objects.equals(locale, LocaleUtil.HUNGARY)) {
						return translatedTitle;
					}

					return originalTitle;
				}),
			name ->
				!name.contains(StringPool.PERIOD) &&
				name.startsWith("localized_title"),
			searchResponse);
		FieldValuesAssert.assertFieldValues(
			_getLocalizedKeywordSorteableMap(
				null, "urlTitle", true,
				locale -> journalArticle.getUrlTitle(locale)),
			name ->
				!name.contains(StringPool.PERIOD) &&
				name.startsWith("urlTitle_"),
			searchResponse);
	}

	@Test
	public void testIndexedFieldsInOnlyOneLanguage() throws Exception {
		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(LocaleUtil.US, "alpha");

								setDefaultLocale(LocaleUtil.US);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.US, "gamma");
							}
						});
				}
			});

		assertSearchOneDocumentOneField(
			"alpha", LocaleUtil.HUNGARY, "content_", "content_en_US");

		assertSearchOneDocumentOneField(
			"gamma", LocaleUtil.HUNGARY, "title_", "title_en_US");
	}

	@Test
	public void testIndexedFieldsMissingWhenContentIsEmpty() throws Exception {
		String originalTitle = "entity title";
		String translatedTitle = "título da entidade";

		JournalArticle journalArticle = _journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(new JournalArticleContent());
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.US, originalTitle);
								put(LocaleUtil.BRAZIL, translatedTitle);
							}
						});
				}
			});

		SearchResponse searchResponse =
			_indexerFixture.searchOnlyOneSearchResponse(
				journalArticle.getArticleId(), LocaleUtil.BRAZIL);

		FieldValuesAssert.assertFieldValues(
			HashMapBuilder.put(
				"title_en_US", originalTitle
			).put(
				"title_pt_BR", translatedTitle
			).build(),
			name -> name.startsWith("title"), searchResponse);
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), name -> name.startsWith("content"),
			searchResponse);

		FieldValuesAssert.assertFieldValues(
			_getLocalizedKeywordSorteableMap(
				originalTitle, "localized_title", false,
				locale -> {
					if (Objects.equals(locale, LocaleUtil.BRAZIL)) {
						return translatedTitle;
					}

					return originalTitle;
				}),
			name ->
				!name.contains(StringPool.PERIOD) &&
				name.startsWith("localized_title"),
			searchResponse);
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), name -> name.startsWith("ddm__text"),
			searchResponse);
	}

	@Test
	public void testJapaneseTitle() throws Exception {
		String title = "新規作成";

		String content = RandomTestUtil.randomString();

		_journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(LocaleUtil.JAPAN, content);

								setDefaultLocale(LocaleUtil.JAPAN);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(LocaleUtil.JAPAN, title);
							}
						});
				}
			});

		Map<String, String> titleStrings = Collections.singletonMap(
			"title_ja_JP", title);

		Map<String, String> contentStrings = Collections.singletonMap(
			"content_ja_JP", content);

		Map<String, String> localizedTitleStrings =
			_getLocalizedKeywordSorteableMap(
				title, "localized_title", false, locale -> title);

		for (String searchTerm : Arrays.asList("新規", "作成", "新", "作")) {
			SearchResponse searchResponse =
				_indexerFixture.searchOnlyOneSearchResponse(
					searchTerm, LocaleUtil.JAPAN);

			FieldValuesAssert.assertFieldValues(
				titleStrings, name -> name.startsWith("title_"),
				searchResponse);
			FieldValuesAssert.assertFieldValues(
				contentStrings, name -> name.startsWith("content_"),
				searchResponse);
			FieldValuesAssert.assertFieldValues(
				localizedTitleStrings,
				name ->
					!name.contains(StringPool.PERIOD) &&
					name.startsWith("localized_title"),
				searchResponse);
		}
	}

	@Test
	public void testJapaneseTitleFullWordOnly() throws Exception {
		Map<String, String> titleStrings = HashMapBuilder.put(
			"title_ja_JP",
			() -> {
				String full = "新規作成";

				for (String title : Arrays.asList(full, "新大阪", "作戦大成功")) {
					_journalArticleSearchFixture.addArticle(
						new JournalArticleBlueprint() {
							{
								setGroupId(_group.getGroupId());
								setJournalArticleContent(
									new JournalArticleContent() {
										{
											put(
												LocaleUtil.JAPAN,
												RandomTestUtil.randomString());

											setDefaultLocale(LocaleUtil.JAPAN);
											setName("content");
										}
									});
								setJournalArticleTitle(
									new JournalArticleTitle() {
										{
											put(LocaleUtil.JAPAN, title);
										}
									});
							}
						});
				}

				return full;
			}
		).build();

		for (String searchTerm : Arrays.asList("新規", "作成")) {
			FieldValuesAssert.assertFieldValues(
				titleStrings, "title_",
				_indexerFixture.searchOnlyOne(searchTerm, LocaleUtil.JAPAN),
				searchTerm);
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertSearchOneDocumentOneField(
		String fieldValue, Locale locale, String fieldPrefix,
		String fieldName) {

		Document document = _indexerFixture.searchOnlyOne(fieldValue, locale);

		FieldValuesAssert.assertFieldValues(
			Collections.singletonMap(fieldName, fieldValue), fieldPrefix,
			document, document.toString());
	}

	private Map<String, String> _getLocalizedKeywordSorteableMap(
			String defaultValue, String fieldName, boolean typify,
			UnsafeFunction<Locale, String, Exception> unsafeFunction)
		throws Exception {

		Map<String, String> localizedTitlesMap = new HashMap<>();

		for (Locale locale :
				_language.getAvailableLocales(_group.getGroupId())) {

			String localizedKey = StringBundler.concat(
				fieldName, StringPool.UNDERLINE, locale.getLanguage(),
				StringPool.UNDERLINE, locale.getCountry());

			String value = unsafeFunction.apply(locale);

			localizedTitlesMap.put(localizedKey, value);

			String localizedSortableKey = localizedKey + "_sortable";

			if (typify) {
				localizedSortableKey = localizedKey + "_String_sortable";
			}

			localizedTitlesMap.put(
				localizedSortableKey, StringUtil.toLowerCase(value));
		}

		if (defaultValue != null) {
			localizedTitlesMap.put(fieldName, defaultValue);
		}

		return localizedTitlesMap;
	}

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private IndexerFixture<JournalArticle> _indexerFixture;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	@Inject
	private Language _language;

	@Inject
	private Portal _portal;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

}