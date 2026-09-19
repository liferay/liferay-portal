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
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.highlight.HighlightUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.SummaryFixture;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author André de Oliveira
 * @author Bryan Engler
 */
@RunWith(Arquillian.class)
public class JournalArticleIndexerSummaryTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_user = UserTestUtil.addUser();

		_journalArticleSearchFixture = new JournalArticleSearchFixture(
			_ddmStructureLocalService, _journalArticleLocalService, _portal);

		_journalArticleSearchFixture.setUp();

		_journalArticles = _journalArticleSearchFixture.getJournalArticles();

		UserTestUtil.setUser(TestPropsValues.getUser());

		_indexer = IndexerRegistryUtil.getIndexer(JournalArticle.class);

		_summaryFixture = new SummaryFixture<>(
			JournalArticle.class, _group, LocaleUtil.US, _user);
	}

	@After
	public void tearDown() throws Exception {
		_journalArticleSearchFixture.tearDown();
	}

	@Test
	public void testGetSummary() throws Exception {
		String content = "test content";
		String title = "test title";

		_summaryFixture.assertSummary(
			title, content, getDocument(title, content));
	}

	@Test
	public void testGetSummaryHighlighted() throws Exception {
		String content = "test content";
		String title = "test title";

		Document document = getDocument(title, content);

		String highlightedContent = StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, "test",
			HighlightUtil.HIGHLIGHT_TAG_CLOSE, " content");
		String highlightedTitle = StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, "test",
			HighlightUtil.HIGHLIGHT_TAG_CLOSE, " title");

		setSnippets(highlightedTitle, highlightedContent, document);

		_summaryFixture.assertSummary(
			highlightedTitle, highlightedContent, document);
	}

	@Test
	public void testStaleTitleFreshContent() throws Exception {
		String content = "test content";
		String title = "test title";

		Document document = getDocument(title, content);

		String staleContent = "stale content";
		String staleTitle = "stale title";

		setFields(staleTitle, staleContent, document);

		_summaryFixture.assertSummary(staleTitle, staleContent, document);
	}

	@Test
	public void testStaleTitleFreshContentHighlighted() throws Exception {
		String content = "test content";
		String title = "test title";

		Document document = getDocument(title, content);

		String staleHighlightedContent = StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, "test",
			HighlightUtil.HIGHLIGHT_TAG_CLOSE, " stale content");
		String staleHighlightedTitle = StringBundler.concat(
			HighlightUtil.HIGHLIGHT_TAG_OPEN, "test",
			HighlightUtil.HIGHLIGHT_TAG_CLOSE, " stale title");

		setSnippets(staleHighlightedTitle, staleHighlightedContent, document);

		_summaryFixture.assertSummary(
			staleHighlightedTitle, staleHighlightedContent, document);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected Document getDocument(String title, String content)
		throws Exception {

		return _indexer.getDocument(
			_journalArticleSearchFixture.addArticle(
				new JournalArticleBlueprint() {
					{
						setGroupId(_group.getGroupId());
						setJournalArticleContent(
							new JournalArticleContent() {
								{
									put(LocaleUtil.US, content);

									setDefaultLocale(LocaleUtil.US);
									setName("content");
								}
							});
						setJournalArticleTitle(
							new JournalArticleTitle() {
								{
									put(LocaleUtil.US, title);
								}
							});
					}
				}));
	}

	protected String getFieldName(String field) {
		return StringBundler.concat(
			field, StringPool.UNDERLINE,
			LocaleUtil.toLanguageId(LocaleUtil.US));
	}

	protected String getSnippetFieldName(String field) {
		return StringBundler.concat(
			Field.SNIPPET, StringPool.UNDERLINE, field, StringPool.UNDERLINE,
			LocaleUtil.toLanguageId(LocaleUtil.US));
	}

	protected void setFields(String title, String content, Document document) {
		document.addText(getFieldName(Field.CONTENT), content);
		document.addText(getFieldName(Field.TITLE), title);
	}

	protected void setSnippets(
		String highlightedTitle, String highlightedContent, Document document) {

		document.addText(
			getSnippetFieldName(Field.CONTENT), highlightedContent);
		document.addText(getSnippetFieldName(Field.TITLE), highlightedTitle);
	}

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	@DeleteAfterTestRun
	private Group _group;

	private Indexer<JournalArticle> _indexer;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	@Inject
	private Portal _portal;

	private SummaryFixture<JournalArticle> _summaryFixture;

	@DeleteAfterTestRun
	private User _user;

}