/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchEngine;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 * @author Alicia García
 */
@RunWith(Arquillian.class)
@Sync
public class DLFileEntryFileNameSearchWhenTitleDifferentThanFileNameTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		WorkflowThreadLocal.setEnabled(false);

		_fileEntrySearchFixture = new FileEntrySearchFixture(dlAppLocalService);

		_fileEntrySearchFixture.setUp();

		_userSearchFixture = new UserSearchFixture();

		_userSearchFixture.setUp();

		_assetTags = _userSearchFixture.getAssetTags();
		_groups = _userSearchFixture.getGroups();
		_users = _userSearchFixture.getUsers();
		_group = _userSearchFixture.addGroup();
	}

	@After
	public void tearDown() throws Exception {
		_fileEntrySearchFixture.tearDown();
		_userSearchFixture.tearDown();
	}

	@Test
	public void testDifferentTitleFileName() throws Exception {
		_createFileEntryFileNameTitle(
			_group, "important_document.txt", "Title");
		_createFileEntryFileNameTitle(
			_group, "other.doc", RandomTestUtil.randomString());
		_createFileEntryFileNameTitle(_group, "image.jpg", "Title Three");

		assertSearch("important", Collections.singletonList("Title"));
		assertSearch("important_document", Collections.singletonList("Title"));
		assertSearch("image.jpg", Collections.singletonList("Title Three"));
	}

	@Test
	public void testDifferentTitleFileNameExactMatch() throws Exception {
		_createFileEntryFileNameTitle(_group, "filename1", "Title One");
		_createFileEntryFileNameTitle(_group, "filename2", "Title Two");
		_createFileEntryFileNameTitle(_group, "filename3", "Title Three");

		assertSearch(
			"filename", Arrays.asList("Title One", "Title Two", "Title Three"));
		assertSearch("filename1", Collections.singletonList("Title One"));
		assertSearch("\"filename\"", Collections.emptyList());
		assertSearch("\"filename2\"", Collections.singletonList("Title Two"));
	}

	@Test
	public void testDifferentTitleFileNamePrefix() throws Exception {
		_createFileEntryFileNameTitle(_group, "document.jpg", "Title 1");
		_createFileEntryFileNameTitle(_group, "document(1).jpg", "Title 2");
		_createFileEntryFileNameTitle(_group, "document(2).jpg", "Title 3");

		if (_isSearchEngine("Elasticsearch")) {
			assertSearch(
				"document", Arrays.asList("Title 1", "Title 2", "Title 3"));
			assertSearch(
				"document(1)", Arrays.asList("Title 1", "Title 2", "Title 3"));
		}
		else if (_isSearchEngine("Solr")) {
			assertSearch(
				"document", Arrays.asList("Title 1", "Title 2", "Title 3"));
			assertSearch("document(1)", Arrays.asList("Title 1", "Title 2"));
		}
	}

	@Test
	public void testExtensionAloneMatchesPathAndExtensionFields()
		throws Exception {

		_createFileEntryFileNameTitle(_group, "One.jpg", "Title One");
		_createFileEntryFileNameTitle(_group, "Two.jpg", "Title Two");

		assertSearch("jpg", Arrays.asList("Title One", "Title Two"));
	}

	@Test
	public void testExtensionAloneSubstringMatchesExtensionAndPathFields()
		throws Exception {

		_createFileEntryFileNameTitle(_group, "document.jpg", "Title One");
		_createFileEntryFileNameTitle(_group, "image.JPG", "Title Two");

		assertSearch("jp", Arrays.asList("Title One", "Title Two"));
	}

	@Test
	public void testExtensionDoesNotSplitFromPlainBaseName() throws Exception {
		_createFileEntryFileNameTitle(
			_group, "Document_1.jpg", RandomTestUtil.randomString());
		_createFileEntryFileNameTitle(
			_group, "Document_2.jpg", RandomTestUtil.randomString());
		_createFileEntryFileNameTitle(_group, "Memorandum.jpg", "Title");

		assertSearch("Letter.jpg", Collections.emptyList());
		assertSearch("Memorandum", Collections.singletonList("Title"));
		assertSearch("Memorandum.jpg", Collections.singletonList("Title"));
	}

	@Test
	public void testExtensionSplitsFromUnusualBaseNameMatchingTitleField()
		throws Exception {

		_createFileEntryFileNameTitle(
			_group, "Document_1.png", RandomTestUtil.randomString());
		_createFileEntryFileNameTitle(
			_group, "Document_2345.png", RandomTestUtil.randomString());

		assertSearch("asdf.png", Collections.emptyList());
	}

	@Test
	public void testLPS73013() throws Exception {
		_createFileEntryFileNameTitle(_group, "myfile.txt", "Title");
		_createFileEntryFileNameTitle(_group, "MyFile (1).txt", "Title (1)");
		_createFileEntryFileNameTitle(_group, "MYFILE (2).txt", "Title (2)");

		if (_isSearchEngine("Elasticsearch")) {
			assertSearch(
				"myfile", Arrays.asList("Title", "Title (1)", "Title (2)"));
			assertSearch(
				"my", Arrays.asList("Title", "Title (1)", "Title (2)"));
		}
		else if (_isSearchEngine("Solr")) {
			assertSearch("myfile", Arrays.asList("Title"));
			assertSearch("my", Arrays.asList("Title"));
		}
	}

	@Test
	public void testLPS82588() throws Exception {
		_createFileEntryFileNameTitle(_group, "Document_1.jpg", "Title One");
		_createFileEntryFileNameTitle(_group, "Document_1.png", "Title One(1)");
		_createFileEntryFileNameTitle(_group, "Document_2.jpg", "Title Two");
		_createFileEntryFileNameTitle(
			_group, "Document_3.png", RandomTestUtil.randomString());

		assertSearch("Document_1", Arrays.asList("Title One", "Title One(1)"));
		assertSearch("asd.jpg", Collections.emptyList());
		assertSearch(
			"\"Document_1.jpg\"", Collections.singletonList("Title One"));

		if (_isSearchEngine("Elasticsearch")) {
			assertSearch(
				"Document_1.jpg",
				Arrays.asList("Title One", "Title One(1)", "Title Two"));
		}
		else if (_isSearchEngine("Solr")) {
			assertSearch("Document_1.jpg", Arrays.asList("Title One"));
		}
	}

	@Test
	public void testLPS82588Relevance() throws Exception {
		String keyword = "Document_3.jpg";
		String title = "Title";

		_createFileEntryFileNameTitle(
			_group, "Document_1.jpg", RandomTestUtil.randomString());
		_createFileEntryFileNameTitle(
			_group, "Document_1.png", RandomTestUtil.randomString());
		_createFileEntryFileNameTitle(
			_group, "Document_2.jpg", RandomTestUtil.randomString());
		_createFileEntryFileNameTitle(_group, keyword, title);
		_createFileEntryFileNameTitle(
			_group, "Document_3.png", RandomTestUtil.randomString());

		Indexer<DLFileEntry> indexer = indexerRegistry.getIndexer(
			DLFileEntry.class);

		SearchContext searchContext = getSearchContext(keyword);

		Hits hits = indexer.search(searchContext);

		Document[] docs = hits.getDocs();

		Document topHit = docs[0];

		Assert.assertEquals(
			(String)searchContext.getAttribute("queryString"), title,
			topHit.get(Field.TITLE));
	}

	@Test
	public void testUnusualBaseNameSplitsExtensionMatchingTitleFieldAsPhraseExact()
		throws Exception {

		_createFileEntryFileNameTitle(_group, "Document_1.jpg", "Title");

		assertSearch("Document_1.jpg", Collections.singletonList("Title"));
	}

	@Test
	public void testUnusualBaseNameSplitsExtensionMatchingTitleFieldAsPhrasePrefix()
		throws Exception {

		_createFileEntryFileNameTitle(_group, "Document_1.docx", "Title");

		assertSearch("Document_1.doc", Collections.singletonList("Title"));
	}

	@Test
	public void testUnusualBaseNameSplitsExtensionMismatchingTitleField()
		throws Exception {

		_createFileEntryFileNameTitle(
			_group, "Document_1234.jpg", RandomTestUtil.randomString());

		assertSearch("Document_1.PNG", Collections.emptyList());
	}

	protected void assertSearch(String keyword, List<String> titles)
		throws Exception {

		Indexer<DLFileEntry> indexer = indexerRegistry.getIndexer(
			DLFileEntry.class);

		SearchContext searchContext = getSearchContext(keyword);

		Hits hits = indexer.search(searchContext);

		DocumentsAssert.assertValuesIgnoreRelevance(
			(String)searchContext.getAttribute("queryString"), hits.getDocs(),
			Field.TITLE, titles);
	}

	protected long getAdminUserId(Group group) throws Exception {
		User user = UserTestUtil.getAdminUser(group.getCompanyId());

		return user.getUserId();
	}

	protected SearchContext getSearchContext(String keyword) throws Exception {
		SearchContext searchContext = _userSearchFixture.getSearchContext(
			keyword);

		Group currentGroup = _groups.get(0);

		searchContext.setGroupIds(new long[] {currentGroup.getGroupId()});

		return searchContext;
	}

	@Inject
	protected static DLAppLocalService dlAppLocalService;

	@Inject
	protected static IndexerRegistry indexerRegistry;

	@Inject
	protected static SearchEngineHelper searchEngineHelper;

	private void _createFileEntryFileNameTitle(
			Group group, String fileName, String title)
		throws Exception {

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					setFileName(fileName);
					setGroupId(group.getGroupId());
					setTitle(title);
					setUserId(getAdminUserId(group));
				}
			});
	}

	private boolean _isSearchEngine(String engine) {
		SearchEngine searchEngine = searchEngineHelper.getSearchEngine();

		String vendor = searchEngine.getVendor();

		return vendor.equals(engine);
	}

	@DeleteAfterTestRun
	private List<AssetTag> _assetTags;

	private FileEntrySearchFixture _fileEntrySearchFixture;
	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private UserSearchFixture _userSearchFixture;

	@DeleteAfterTestRun
	private List<User> _users;

}