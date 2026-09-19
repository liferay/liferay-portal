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
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.SummaryFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Vagner B.C
 */
@RunWith(Arquillian.class)
public class JournalArticleMultiLanguageSearchJapaneseSummaryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		UserTestUtil.setUser(TestPropsValues.getUser());

		_indexer = indexerRegistry.getIndexer(JournalArticle.class);

		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			ddmStructureLocalService, journalArticleLocalService, portal);

		_journalArticleSearchFixture.setUp();

		_journalArticles = _journalArticleSearchFixture.getJournalArticles();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_groups = _userSearchFixture.getGroups();
		_users = _userSearchFixture.getUsers();

		_group = _userSearchFixture.addGroup();

		_user = _userSearchFixture.addUser(
			RandomTestUtil.randomString(), _group);

		_summaryFixture = new SummaryFixture<>(
			JournalArticle.class, _group, LocaleUtil.US, _user);
	}

	@After
	public void tearDown() {
		_journalArticleSearchFixture.tearDown();

		_userSearchFixture.tearDown();
	}

	@Test
	public void testJapaneseSummaryHighlightedTermWithWordBoundaries()
		throws Exception {

		String highlightedContent = StringBundler.concat(
			"新規", HighlightUtil.HIGHLIGHT_TAG_OPEN, "作成",
			HighlightUtil.HIGHLIGHT_TAG_CLOSE);
		String highlightedTitle = StringBundler.concat(
			"新規", HighlightUtil.HIGHLIGHT_TAG_OPEN, "作成",
			HighlightUtil.HIGHLIGHT_TAG_CLOSE);

		Locale locale = LocaleUtil.JAPAN;

		Document document = getDocument(_KEYWORD, _KEYWORD, locale);

		setSnippets(highlightedTitle, highlightedContent, document, locale);

		_summaryFixture.assertSummary(
			highlightedTitle, highlightedContent, document);
	}

	@Test
	public void testJapaneseSummaryHighlightedTermWithoutWordBoundaries()
		throws Exception {

		String highlightedContent = StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, "新規",
			HighlightUtil.HIGHLIGHT_TAG_CLOSE, "作成");
		String highlightedTitle = StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, "新規",
			HighlightUtil.HIGHLIGHT_TAG_CLOSE, "作成");

		Locale locale = LocaleUtil.JAPAN;

		Document document = getDocument(_KEYWORD, _KEYWORD, locale);

		setSnippets(highlightedTitle, highlightedContent, document, locale);

		_summaryFixture.assertSummary(
			highlightedTitle, highlightedContent, document);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected JournalArticle addArticle(
		String title, String content, Locale locale) {

		return _journalArticleSearchFixture.addArticle(
			new JournalArticleBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setJournalArticleContent(
						new JournalArticleContent() {
							{
								put(locale, content);

								setDefaultLocale(locale);
								setName("content");
							}
						});
					setJournalArticleTitle(
						new JournalArticleTitle() {
							{
								put(locale, title);
							}
						});
				}
			});
	}

	protected Document getDocument(String title, String content, Locale locale)
		throws Exception {

		return _indexer.getDocument(addArticle(title, content, locale));
	}

	protected String getSnippetFieldName(String field, Locale locale) {
		return StringBundler.concat(
			Field.SNIPPET, StringPool.UNDERLINE, field, StringPool.UNDERLINE,
			LocaleUtil.toLanguageId(locale));
	}

	protected void setSnippets(
		String highlightedTitle, String highlightedContent, Document document,
		Locale locale) {

		document.addText(
			getSnippetFieldName(Field.CONTENT, locale), highlightedContent);
		document.addText(
			getSnippetFieldName(Field.TITLE, locale), highlightedTitle);
	}

	@Inject
	protected static DDMStructureLocalService ddmStructureLocalService;

	@Inject
	protected static Portal portal;

	@Inject
	protected IndexerRegistry indexerRegistry;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	private static final String _KEYWORD = "新規作成";

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private Indexer<JournalArticle> _indexer;
	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private SummaryFixture<JournalArticle> _summaryFixture;
	private User _user;
	private UserSearchFixture _userSearchFixture;

	@DeleteAfterTestRun
	private List<User> _users;

}