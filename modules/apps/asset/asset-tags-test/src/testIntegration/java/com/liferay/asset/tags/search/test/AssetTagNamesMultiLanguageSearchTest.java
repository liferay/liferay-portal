/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.tags.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetTag;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLAppLocalService;
import com.liferay.document.library.test.util.search.FileEntryBlueprint;
import com.liferay.document.library.test.util.search.FileEntrySearchFixture;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.workflow.WorkflowThreadLocal;
import com.liferay.portal.search.facet.tag.AssetTagNamesFacetFactory;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.UserSearchFixture;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
@Sync
public class AssetTagNamesMultiLanguageSearchTest {

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
	}

	@After
	public void tearDown() throws Exception {
		_userSearchFixture.tearDown();
		_fileEntrySearchFixture.tearDown();
	}

	@Test
	public void testChineseTags() throws Exception {
		Locale locale = LocaleUtil.CHINA;
		String title = "title should not match";
		String tag = "你好";

		Group group = _userSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					setDefaultLocale(locale);
				}
			});

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					setAssetTagNames(new String[] {tag});
					setFileName(title);
					setGroupId(group.getGroupId());
					setTitle(title);
					setUserId(getAdminUserId(group));
				}
			});

		assertSearch(tag, locale);
	}

	@Test
	public void testDisplayLocaleDifferentFromDefaultLocale() throws Exception {
		Locale indexLocale = LocaleUtil.US;
		Locale queryLocale = LocaleUtil.JAPAN;
		String title = "title should not match";
		String tag = "searchtag";

		Group group = _userSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					setDefaultLocale(indexLocale);
				}
			});

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					setAssetTagNames(new String[] {tag});
					setFileName(title);
					setGroupId(group.getGroupId());
					setTitle(title);
					setUserId(getAdminUserId(group));
				}
			});

		assertSearch(tag, group, queryLocale);
	}

	@Test
	public void testEnglishTags() throws Exception {
		Locale locale = LocaleUtil.US;
		String title = "title should not match";
		String tag = "searchtag";

		Group group = _userSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					setDefaultLocale(locale);
				}
			});

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					setAssetTagNames(new String[] {tag});
					setFileName(title);
					setGroupId(group.getGroupId());
					setTitle(title);
					setUserId(getAdminUserId(group));
				}
			});

		assertSearch(tag, locale);
	}

	@Test
	public void testJapaneseTags() throws Exception {
		Locale locale = LocaleUtil.JAPAN;
		String title1 = "first title should not match";
		String title2 = "second title should not match";
		String tag1 = "東京";
		String tag2 = "出前京丁";

		Group group = _userSearchFixture.addGroup(
			new GroupBlueprint() {
				{
					setDefaultLocale(locale);
				}
			});

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					setAssetTagNames(new String[] {tag1});
					setFileName(title1);
					setGroupId(group.getGroupId());
					setTitle(title1);
					setUserId(getAdminUserId(group));
				}
			});

		_fileEntrySearchFixture.addFileEntry(
			new FileEntryBlueprint() {
				{
					setAssetTagNames(new String[] {tag2});
					setFileName(title2);
					setGroupId(group.getGroupId());
					setTitle(title2);
					setUserId(getAdminUserId(group));
				}
			});

		assertSearch(tag1, locale);
		assertSearch(tag2, locale);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertDLFileEntryIndexer(
			String tagName, Group group, Locale locale)
		throws Exception {

		Indexer<DLFileEntry> indexer = indexerRegistry.getIndexer(
			DLFileEntry.class);

		SearchContext searchContext = getSearchContext(tagName, group, locale);

		Hits hits = indexer.search(searchContext);

		assertHits(tagName, hits, searchContext);
	}

	protected void assertFacetedSearcher(
			String tagName, Group group, Locale locale)
		throws Exception {

		FacetedSearcher facetedSearcher =
			facetedSearcherManager.createFacetedSearcher();

		SearchContext searchContext = getSearchContext(tagName, group, locale);

		searchContext.setEntryClassNames(
			new String[] {DLFileEntry.class.getName()});

		Hits hits = facetedSearcher.search(searchContext);

		assertHits(tagName, hits, searchContext);
	}

	protected void assertHits(
		String tagName, Hits hits, SearchContext searchContext) {

		DocumentsAssert.assertValuesIgnoreRelevance(
			(String)searchContext.getAttribute("queryString"), hits.getDocs(),
			Field.ASSET_TAG_NAMES, Arrays.asList(tagName));
	}

	protected void assertSearch(String tagName, Group group, Locale locale)
		throws Exception {

		assertDLFileEntryIndexer(tagName, group, locale);
		assertFacetedSearcher(tagName, group, locale);
	}

	protected void assertSearch(String tagName, Locale locale)
		throws Exception {

		assertDLFileEntryIndexer(tagName, null, locale);
		assertFacetedSearcher(tagName, null, locale);
	}

	protected long getAdminUserId(Group group) {
		try {
			User user = UserTestUtil.getAdminUser(group.getCompanyId());

			return user.getUserId();
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	protected SearchContext getSearchContext(
			String keywords, Group group, Locale locale)
		throws Exception {

		SearchContext searchContext = _userSearchFixture.getSearchContext(
			keywords);

		if (group != null) {
			searchContext.setGroupIds(new long[] {group.getGroupId()});
		}

		searchContext.setLocale(locale);

		return searchContext;
	}

	@Inject
	protected static AssetTagNamesFacetFactory assetTagNamesFacetFactory;

	@Inject
	protected static DLAppLocalService dlAppLocalService;

	@Inject
	protected static FacetedSearcherManager facetedSearcherManager;

	@Inject
	protected static IndexerRegistry indexerRegistry;

	@DeleteAfterTestRun
	private List<AssetTag> _assetTags;

	private FileEntrySearchFixture _fileEntrySearchFixture;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private UserSearchFixture _userSearchFixture;

	@DeleteAfterTestRun
	private List<User> _users;

}