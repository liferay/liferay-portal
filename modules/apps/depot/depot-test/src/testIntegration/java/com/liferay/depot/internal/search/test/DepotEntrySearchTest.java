/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.internal.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryService;
import com.liferay.depot.test.util.DepotTestUtil;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistryUtil;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.SearchResult;
import com.liferay.portal.kernel.search.SearchResultUtil;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.UserService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.Sync;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.SearchContextTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
@Sync
public class DepotEntrySearchTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Test
	public void testSearchBothWithoutPermissions() throws Exception {
		_addDepotEntry(TestPropsValues.getUser(), "Depot Entry 1");
		_addDepotEntry(TestPropsValues.getUser(), "Depot Entry 2");

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				Indexer<DepotEntry> indexer = IndexerRegistryUtil.getIndexer(
					DepotEntry.class);

				SearchContext searchContext =
					SearchContextTestUtil.getSearchContext(
						TestPropsValues.getGroupId());

				searchContext.setKeywords("Depot Entry");

				Hits hits = indexer.search(searchContext);

				Assert.assertEquals(hits.toString(), 0, hits.getLength());
			});
	}

	@Ignore
	@Test
	public void testSearchBothWithPermissions() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry(
			TestPropsValues.getUser(), "Depot Entry 1");
		DepotEntry depotEntry2 = _addDepotEntry(
			TestPropsValues.getUser(), "Depot Entry 2");

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				RoleTestUtil.addResourcePermission(
					role, DepotEntry.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.VIEW);

				Indexer<DepotEntry> indexer = IndexerRegistryUtil.getIndexer(
					DepotEntry.class);

				SearchContext searchContext =
					SearchContextTestUtil.getSearchContext(
						TestPropsValues.getGroupId());

				searchContext.setGroupIds(null);
				searchContext.setKeywords("Depot Entry");

				Hits hits = indexer.search(searchContext);

				Assert.assertEquals(hits.toString(), 2, hits.getLength());

				List<SearchResult> searchResults =
					SearchResultUtil.getSearchResults(
						hits, LocaleUtil.getDefault());

				SearchResult firstSearchResult = searchResults.get(0);
				SearchResult secondSearchResult = searchResults.get(1);

				Assert.assertEquals(
					depotEntry1,
					_depotEntryService.getDepotEntry(
						firstSearchResult.getClassPK()));
				Assert.assertEquals(
					depotEntry2,
					_depotEntryService.getDepotEntry(
						secondSearchResult.getClassPK()));
			});
	}

	@Test
	public void testSearchOneWithPermissions() throws Exception {
		DepotEntry depotEntry1 = _addDepotEntry(
			TestPropsValues.getUser(), "Depot Entry 1");

		_addDepotEntry(TestPropsValues.getUser(), "Depot Entry 2");

		DepotTestUtil.withRegularUser(
			(user, role) -> {
				RoleTestUtil.addResourcePermission(
					role, DepotEntry.class.getName(),
					ResourceConstants.SCOPE_COMPANY,
					String.valueOf(TestPropsValues.getCompanyId()),
					ActionKeys.VIEW);

				Indexer<DepotEntry> indexer = IndexerRegistryUtil.getIndexer(
					DepotEntry.class);

				SearchContext searchContext =
					SearchContextTestUtil.getSearchContext(
						TestPropsValues.getGroupId());

				searchContext.setGroupIds(null);
				searchContext.setKeywords("1");

				Hits hits = indexer.search(searchContext);

				Assert.assertEquals(hits.toString(), 1, hits.getLength());

				List<SearchResult> searchResults =
					SearchResultUtil.getSearchResults(
						hits, LocaleUtil.getDefault());

				SearchResult firstSearchResult = searchResults.get(0);

				Assert.assertEquals(
					depotEntry1,
					_depotEntryService.getDepotEntry(
						firstSearchResult.getClassPK()));
			});
	}

	@Ignore
	@Test
	public void testSearchWithDepotEntryMembershipAndOrganizationMembership()
		throws Exception {

		User adminUser = TestPropsValues.getUser();

		DepotEntry depotEntry = _addDepotEntry(adminUser, "Depot Entry 1");

		Organization organization = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		DepotTestUtil.withAssetLibraryMember(
			depotEntry,
			user -> {
				_userService.addOrganizationUsers(
					organization.getOrganizationId(),
					new long[] {user.getUserId()});

				Indexer<DepotEntry> indexer = IndexerRegistryUtil.getIndexer(
					DepotEntry.class);

				SearchContext searchContext =
					SearchContextTestUtil.getSearchContext(
						TestPropsValues.getGroupId());

				searchContext.setGroupIds(null);
				searchContext.setKeywords(null);

				Hits hits = indexer.search(searchContext);

				Assert.assertEquals(hits.toString(), 1, hits.getLength());

				List<SearchResult> searchResults =
					SearchResultUtil.getSearchResults(
						hits, LocaleUtil.getDefault());

				SearchResult searchResult = searchResults.get(0);

				Assert.assertEquals(
					depotEntry,
					_depotEntryService.getDepotEntry(
						searchResult.getClassPK()));
			});
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private DepotEntry _addDepotEntry(User user, String name) throws Exception {
		DepotEntry depotEntry = _depotEntryService.addDepotEntry(
			Collections.singletonMap(LocaleUtil.getDefault(), name),
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext(
				TestPropsValues.getGroupId(), user.getUserId()));

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryService _depotEntryService;

	@Inject
	private UserService _userService;

}