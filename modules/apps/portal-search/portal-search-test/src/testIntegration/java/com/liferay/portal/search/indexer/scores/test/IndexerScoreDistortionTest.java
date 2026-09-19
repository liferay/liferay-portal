/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.indexer.scores.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.blogs.test.util.search.BlogsEntryBlueprint.BlogsEntryBlueprintBuilder;
import com.liferay.blogs.test.util.search.BlogsEntrySearchFixture;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.search.JournalArticleBlueprintBuilder;
import com.liferay.journal.test.util.search.JournalArticleContent;
import com.liferay.journal.test.util.search.JournalArticleSearchFixture;
import com.liferay.journal.test.util.search.JournalArticleTitle;
import com.liferay.message.boards.constants.MBCategoryConstants;
import com.liferay.message.boards.constants.MBMessageConstants;
import com.liferay.message.boards.model.MBMessage;
import com.liferay.message.boards.service.MBMessageLocalService;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.sort.Sorts;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.security.permission.PermissionCheckerUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiNodeLocalService;
import com.liferay.wiki.service.WikiPageLocalService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 * @author André de Oliveira
 */
@RunWith(Arquillian.class)
public class IndexerScoreDistortionTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		BlogsEntrySearchFixture blogsEntrySearchFixture =
			new BlogsEntrySearchFixture(blogsEntryLocalService);

		FileEntrySearchFixture fileEntrySearchFixture =
			new FileEntrySearchFixture(dlAppLocalService);

		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		JournalArticleSearchFixture journalArticleSearchFixture =
			new JournalArticleSearchFixture(
				ddmStructureLocalService, journalArticleLocalService, portal);

		_blogsEntries = blogsEntrySearchFixture.getBlogsEntries();
		_blogsEntrySearchFixture = blogsEntrySearchFixture;
		_fileEntrySearchFixture = fileEntrySearchFixture;
		_group = groupSearchFixture.addGroup(new GroupBlueprint());
		_groups = groupSearchFixture.getGroups();
		_journalArticles = journalArticleSearchFixture.getJournalArticles();
		_journalArticleSearchFixture = journalArticleSearchFixture;

		_user = TestPropsValues.getUser();

		PermissionCheckerUtil.setThreadValues(_user);
	}

	@After
	public void tearDown() throws Exception {
		_fileEntrySearchFixture.tearDown();
	}

	@Test
	public void testTitleIndexedInTriplicateDistortsScores() throws Exception {
		Locale locale = LocaleUtil.US;

		String title = "collision";

		addBlogsEntry(title);
		addFileEntry(title);
		addJournalArticle(title, locale);
		addMessage(title);
		addWikiPage(title);

		Class<?>[] classes = new Class<?>[] {
			BlogsEntry.class, DLFileEntry.class, JournalArticle.class,
			MBMessage.class, WikiPage.class
		};

		SearchResponse searchResponse1 = search(title, locale, classes);

		assertValuesIgnoreRelevance(
			Field.ENTRY_CLASS_NAME, getClassNamesAsString(BlogsEntry.class),
			_sublist(searchResponse1.getDocuments(), 2, 3), searchResponse1);
		assertValuesIgnoreRelevance(
			Field.ENTRY_CLASS_NAME, getClassNamesAsString(DLFileEntry.class),
			_sublist(searchResponse1.getDocuments(), 1, 2), searchResponse1);
		assertValuesIgnoreRelevance(
			Field.ENTRY_CLASS_NAME,
			getClassNamesAsString(JournalArticle.class, MBMessage.class),
			_sublist(searchResponse1.getDocuments(), 3, 5), searchResponse1);
		assertValuesIgnoreRelevance(
			Field.ENTRY_CLASS_NAME, getClassNamesAsString(WikiPage.class),
			_sublist(searchResponse1.getDocuments(), 0, 1), searchResponse1);

		SearchResponse searchResponse2 = search(
			title, locale, classes,
			searchRequestBuilder -> searchRequestBuilder.sorts(
				sorts.field(Field.ENTRY_CLASS_NAME)));

		assertValues(
			Field.ENTRY_CLASS_NAME,
			getClassNamesAsString(
				BlogsEntry.class, DLFileEntry.class, JournalArticle.class,
				MBMessage.class, WikiPage.class),
			searchResponse2);
		assertValues("fileName", "[, collision, , , ]", searchResponse2);
		assertValues(
			Field.TITLE, "[collision, collision, , , collision]",
			searchResponse2);
		assertValues(
			Field.TITLE + "_en_US",
			"[collision, collision, collision, collision, collision]",
			searchResponse2);
		assertValues(
			Field.TITLE + "_hu_HU", "[collision, , , collision, collision]",
			searchResponse2);
		assertValues(
			Field.TITLE + "_ja_JP", "[collision, , , collision, collision]",
			searchResponse2);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected BlogsEntry addBlogsEntry(String title) {
		return _blogsEntrySearchFixture.addBlogsEntry(
			BlogsEntryBlueprintBuilder.builder(
			).content(
				RandomTestUtil.randomString()
			).groupId(
				_group.getGroupId()
			).title(
				title
			).userId(
				_user.getUserId()
			).build());
	}

	protected FileEntry addFileEntry(String title) {
		return _fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					setGroupId(_group.getGroupId());
					setTitle(title);
					setUserId(_user.getUserId());
				}
			});
	}

	protected JournalArticle addJournalArticle(String title, Locale locale) {
		return _journalArticleSearchFixture.addArticle(
			JournalArticleBlueprintBuilder.builder(
			).groupId(
				_group.getGroupId()
			).journalArticleContent(
				new JournalArticleContent() {
					{
						put(locale, RandomTestUtil.randomString());

						setDefaultLocale(locale);
						setName("content");
					}
				}
			).journalArticleTitle(
				new JournalArticleTitle() {
					{
						put(locale, title);
					}
				}
			).userId(
				_user.getUserId()
			).build());
	}

	protected MBMessage addMessage(String title) throws Exception {
		return mbMessageLocalService.addMessage(
			null, _user.getUserId(), RandomTestUtil.randomString(),
			_group.getGroupId(), MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			0L, MBMessageConstants.DEFAULT_PARENT_MESSAGE_ID, title,
			RandomTestUtil.randomString(), MBMessageConstants.DEFAULT_FORMAT,
			null, false, 0.0, false, _createServiceContext());
	}

	protected WikiPage addWikiPage(String title) throws Exception {
		ServiceContext serviceContext = _createServiceContext();

		WikiNode wikiNode = wikiNodeLocalService.addDefaultNode(
			_user.getUserId(), serviceContext);

		WikiPage wikiPage = wikiPageLocalService.addPage(
			_user.getUserId(), wikiNode.getNodeId(), title, "content",
			"Summary", false, serviceContext);

		_wikiPages.add(wikiPage);

		return wikiPage;
	}

	protected void assertValues(
		String fieldName, String expected, SearchResponse searchResponse) {

		DocumentsAssert.assertValues(
			getMessage(fieldName, searchResponse),
			searchResponse.getDocuments(), fieldName, expected);
	}

	protected void assertValuesIgnoreRelevance(
		String fieldName, String expected, List<Document> documents,
		SearchResponse searchResponse) {

		DocumentsAssert.assertValuesIgnoreRelevance(
			getMessage(fieldName, searchResponse), documents, fieldName,
			expected);
	}

	protected String getClassNamesAsString(Class<?>... classes) {
		List<String> classNames = TransformUtil.transformToList(
			classes, Class::getName);

		return classNames.toString();
	}

	protected String getMessage(
		String fieldName, SearchResponse searchResponse) {

		return StringBundler.concat(
			fieldName, StringPool.EIGHT_STARS,
			searchResponse.getRequestString(), StringPool.EIGHT_STARS,
			searchResponse.getResponseString());
	}

	protected SearchResponse search(
		String title, Locale locale, Class<?>[] classes,
		Consumer<SearchRequestBuilder>... searchRequestBuilderConsumers) {

		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).explain(
				true
			).fields(
				StringPool.STAR
			).groupIds(
				_group.getGroupId()
			).includeResponseString(
				true
			).locale(
				locale
			).modelIndexerClasses(
				classes
			).queryString(
				title
			).withSearchRequestBuilder(
				searchRequestBuilderConsumers
			).build());
	}

	@Inject
	protected static DDMStructureLocalService ddmStructureLocalService;

	@Inject
	protected static Portal portal;

	@Inject
	protected BlogsEntryLocalService blogsEntryLocalService;

	@Inject
	protected DLAppLocalService dlAppLocalService;

	@Inject
	protected JournalArticleLocalService journalArticleLocalService;

	@Inject
	protected MBMessageLocalService mbMessageLocalService;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	@Inject
	protected Sorts sorts;

	@Inject
	protected WikiNodeLocalService wikiNodeLocalService;

	@Inject
	protected WikiPageLocalService wikiPageLocalService;

	private ServiceContext _createServiceContext() throws Exception {
		return ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), _user.getUserId());
	}

	private List<Document> _sublist(
		List<Document> documents, int start, int end) {

		return documents.subList(start, end);
	}

	@DeleteAfterTestRun
	private List<BlogsEntry> _blogsEntries;

	private BlogsEntrySearchFixture _blogsEntrySearchFixture;
	private FileEntrySearchFixture _fileEntrySearchFixture;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private JournalArticleSearchFixture _journalArticleSearchFixture;

	@DeleteAfterTestRun
	private List<JournalArticle> _journalArticles;

	private User _user;

	@DeleteAfterTestRun
	private final List<WikiPage> _wikiPages = new ArrayList<>();

}