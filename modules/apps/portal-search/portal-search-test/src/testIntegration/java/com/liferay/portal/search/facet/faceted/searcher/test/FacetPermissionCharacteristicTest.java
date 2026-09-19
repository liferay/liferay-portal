/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.facet.faceted.searcher.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.model.JournalFolder;
import com.liferay.journal.service.JournalFolderServiceUtil;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.function.UnsafeRunnable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcher;
import com.liferay.portal.kernel.search.facet.faceted.searcher.FacetedSearcherManager;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.test.context.ContextUserReplace;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.facet.Facet;
import com.liferay.portal.search.facet.folder.FolderFacetFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Wade Cao
 */
@RunWith(Arquillian.class)
public class FacetPermissionCharacteristicTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_groupA = GroupTestUtil.addGroup();
		_groupB = GroupTestUtil.addGroup(
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			RandomTestUtil.randomString(), _serviceContext);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_groupA, TestPropsValues.getUserId());

		_journalFolderA = JournalFolderServiceUtil.addFolder(
			null, _groupA.getGroupId(), 0, RandomTestUtil.randomString(),
			StringPool.BLANK, _serviceContext);

		_journalArticleA = JournalTestUtil.addArticle(
			_groupA.getGroupId(), _journalFolderA.getFolderId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, "article a"
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.US, ""
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		_journalFolderB = JournalFolderServiceUtil.addFolder(
			null, _groupA.getGroupId(), 0, RandomTestUtil.randomString(),
			StringPool.BLANK, _serviceContext);

		_journalArticleB = JournalTestUtil.addArticle(
			_groupA.getGroupId(), _journalFolderB.getFolderId(),
			PortalUtil.getClassNameId(JournalArticle.class),
			HashMapBuilder.put(
				LocaleUtil.US, "article b"
			).build(),
			null,
			HashMapBuilder.put(
				LocaleUtil.US, ""
			).build(),
			LocaleUtil.getSiteDefault(), false, true, _serviceContext);

		_roleA = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);
		_roleB = RoleTestUtil.addRole(
			RandomTestUtil.randomString(), RoleConstants.TYPE_REGULAR);
	}

	@Test
	public void testFolderChildAssetWithTheChildAssetRoleOnly()
		throws Exception {

		_setUp(
			HashMapBuilder.<Role, Object[]>put(
				_roleA, new Object[] {_journalArticleA}
			).put(
				_roleB, new Object[] {_journalArticleB}
			).build());

		_test(
			new long[0],
			() -> _assertSearch("[article a, article b]", "article"));
		_test(
			new long[] {_roleA.getRoleId()},
			() -> _assertFacetSearch(
				Arrays.asList("article a"),
				String.valueOf(_journalFolderA.getFolderId())));
		_test(
			new long[] {_roleB.getRoleId()},
			() -> _assertFacetSearch(
				Arrays.asList("article a"),
				String.valueOf(_journalFolderA.getFolderId())));
	}

	@Test
	public void testFolderChildAssetWithTheDifferentRoles() throws Exception {
		_setUp(
			HashMapBuilder.<Role, Object[]>put(
				_roleA, new Object[] {_journalFolderA, _journalArticleB}
			).put(
				_roleB, new Object[] {_journalFolderB, _journalArticleA}
			).build());

		_test(new long[0], () -> _assertSearch("[]", "article"));
		_test(
			new long[] {_roleA.getRoleId(), _roleB.getRoleId()},
			() -> _assertSearch("[article a, article b]", "article"));
		_test(
			new long[] {_roleA.getRoleId()},
			() -> _assertFacetSearch(
				null, String.valueOf(_journalFolderB.getFolderId())));
		_test(
			new long[] {_roleA.getRoleId(), _roleB.getRoleId()},
			() -> _assertFacetSearch(
				Arrays.asList("article a"),
				String.valueOf(_journalFolderA.getFolderId())));
		_test(
			new long[] {_roleB.getRoleId()},
			() -> _assertFacetSearch(
				Arrays.asList("article b"),
				String.valueOf(_journalFolderB.getFolderId())));
	}

	@Test
	public void testFolderChildAssetWithTheFolderRoleOnly() throws Exception {
		_setUp(
			HashMapBuilder.<Role, Object[]>put(
				_roleA, new Object[] {_journalFolderA}
			).put(
				_roleB, new Object[] {_journalFolderB}
			).build());

		_test(new long[0], () -> _assertSearch("[]", "article"));
		_test(
			new long[] {_roleA.getRoleId(), _roleB.getRoleId()},
			() -> _assertSearch("[article a, article b]", "article"));
		_test(
			new long[] {_roleA.getRoleId()},
			() -> _assertFacetSearch(
				Arrays.asList("article a"),
				String.valueOf(_journalFolderA.getFolderId())));
		_test(
			new long[] {_roleB.getRoleId()},
			() -> _assertFacetSearch(
				null, String.valueOf(_journalFolderA.getFolderId())));
	}

	@Test
	public void testFolderChildAssetWithTheSameRoles() throws Exception {
		_setUp(
			HashMapBuilder.<Role, Object[]>put(
				_roleA, new Object[] {_journalFolderA, _journalArticleA}
			).put(
				_roleB, new Object[] {_journalFolderB, _journalArticleB}
			).build());

		_test(new long[0], () -> _assertSearch("[]", "article"));
		_test(
			new long[] {_roleA.getRoleId(), _roleB.getRoleId()},
			() -> _assertSearch("[article a, article b]", "article"));
		_test(
			new long[] {_roleA.getRoleId()},
			() -> _assertFacetSearch(
				null, String.valueOf(_journalFolderB.getFolderId())));
		_test(
			new long[] {_roleA.getRoleId(), _roleB.getRoleId()},
			() -> _assertFacetSearch(
				Arrays.asList("article a"),
				String.valueOf(_journalFolderA.getFolderId())));
	}

	private void _assertFacetSearch(List<String> expected, String... selects)
		throws Exception {

		SearchContext searchContext = new SearchContext();

		Facet facet = _folderFacetFactory.newInstance(searchContext);

		facet.select(selects);

		searchContext.addFacet(facet);

		searchContext.setCompanyId(TestPropsValues.getCompanyId());
		searchContext.setKeywords("article");
		searchContext.setUserId(_serviceContext.getUserId());

		FacetedSearcher facetedSearcher =
			_facetedSearcherManager.createFacetedSearcher();

		Hits hits = facetedSearcher.search(searchContext);

		if (expected == null) {
			Assert.assertEquals(hits.toString(), 0, hits.getLength());
		}
		else {
			DocumentsAssert.assertValuesIgnoreRelevance(
				(String)searchContext.getAttribute("queryString"),
				hits.getDocs(), "title_en_US", expected);
		}
	}

	private void _assertSearch(String expected, String queryString) {
		SearchResponse searchResponse = _searcher.search(
			_searchRequestBuilderFactory.builder(
			).companyId(
				_groupA.getCompanyId()
			).queryString(
				queryString
			).withSearchContext(
				_searchContext -> _searchContext.setUserId(
					_serviceContext.getUserId())
			).build());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getRequestString(), searchResponse.getDocuments(),
			"title_en_US", expected);
	}

	private String _getName(Object object) {
		if (object instanceof JournalFolder) {
			return JournalFolder.class.getName();
		}
		else if (object instanceof JournalArticle) {
			return JournalArticle.class.getName();
		}

		return null;
	}

	private String _getPrimKey(Object object) {
		if (object instanceof JournalArticle) {
			JournalArticle journalArticle = (JournalArticle)object;

			return String.valueOf(journalArticle.getId());
		}
		else if (object instanceof JournalFolder) {
			JournalFolder journalFolder = (JournalFolder)object;

			return String.valueOf(journalFolder.getFolderId());
		}

		return null;
	}

	private void _setUp(Map<Role, Object[]> map) throws Exception {
		for (Map.Entry<Role, Object[]> entry : map.entrySet()) {
			Role role = entry.getKey();

			Object[] objects = entry.getValue();

			for (Object object : objects) {
				ResourcePermissionLocalServiceUtil.setResourcePermissions(
					_groupB.getCompanyId(), _getName(object),
					ResourceConstants.SCOPE_INDIVIDUAL, _getPrimKey(object),
					role.getRoleId(), new String[] {ActionKeys.VIEW});
			}
		}

		List<Role> roles = RoleLocalServiceUtil.getRoles(
			TestPropsValues.getCompanyId());

		for (Role role : roles) {
			if (RoleConstants.OWNER.equals(role.getName()) ||
				(map.get(role) != null)) {

				continue;
			}

			for (Map.Entry<Role, Object[]> entry : map.entrySet()) {
				Object[] objects = entry.getValue();

				for (Object object : objects) {
					ResourcePermissionLocalServiceUtil.removeResourcePermission(
						TestPropsValues.getCompanyId(), _getName(object),
						ResourceConstants.SCOPE_INDIVIDUAL, _getPrimKey(object),
						role.getRoleId(), ActionKeys.VIEW);
				}
			}
		}
	}

	private void _test(long[] roleIds, UnsafeRunnable<Exception> unsafeRunnable)
		throws Exception {

		User user = UserTestUtil.addUser(_groupB.getGroupId());

		for (long roleId : roleIds) {
			UserLocalServiceUtil.addRoleUser(roleId, user);
		}

		_serviceContext.setUserId(user.getUserId());

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(user);

		try (ContextUserReplace contextUserReplace = new ContextUserReplace(
				user, permissionChecker)) {

			unsafeRunnable.run();
		}
	}

	@Inject
	private FacetedSearcherManager _facetedSearcherManager;

	@Inject
	private FolderFacetFactory _folderFacetFactory;

	@DeleteAfterTestRun
	private Group _groupA;

	@DeleteAfterTestRun
	private Group _groupB;

	private JournalArticle _journalArticleA;
	private JournalArticle _journalArticleB;
	private JournalFolder _journalFolderA;
	private JournalFolder _journalFolderB;
	private Role _roleA;
	private Role _roleB;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private Searcher _searcher;

	private ServiceContext _serviceContext;

}