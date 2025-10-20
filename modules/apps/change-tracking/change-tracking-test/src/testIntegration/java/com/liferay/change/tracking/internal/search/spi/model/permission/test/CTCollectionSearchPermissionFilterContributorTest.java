/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.internal.search.spi.model.permission.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.constants.CTRoleConstants;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Pei-Jung Lan
 */
@RunWith(Arquillian.class)
public class CTCollectionSearchPermissionFilterContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testWhenHasPublicationsRolePermissionSearch() throws Exception {
		CTCollection ctCollection = _ctCollectionLocalService.addCTCollection(
			null, TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
			0, RandomTestUtil.randomString(), RandomTestUtil.randomString());

		Group group = _groupLocalService.addGroup(
			StringPool.BLANK, TestPropsValues.getUserId(),
			GroupConstants.DEFAULT_PARENT_GROUP_ID,
			CTCollection.class.getName(), ctCollection.getCtCollectionId(),
			GroupConstants.DEFAULT_LIVE_GROUP_ID,
			RandomTestUtil.randomLocaleStringMap(), null,
			GroupConstants.TYPE_SITE_PRIVATE, null, false,
			GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, null, false, false,
			true, null);

		User user = UserTestUtil.addUser();

		_assertSearch(user);

		Role role = _roleLocalService.getRole(
			TestPropsValues.getCompanyId(),
			CTRoleConstants.PUBLICATIONS_REVIEWER);

		_userGroupRoleLocalService.addUserGroupRole(
			user.getUserId(), group.getGroupId(), role.getRoleId());

		_assertSearch(user, _uidFactory.getUID(ctCollection));
	}

	private void _assertSearch(User user, String... expectedCTCollectionIds)
		throws Exception {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).emptySearchEnabled(
				true
			).entryClassNames(
				CTCollection.class.getName()
			).modelIndexerClasses(
				CTCollection.class
			).withSearchContext(
				searchContext -> searchContext.setUserId(user.getUserId())
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		Assert.assertEquals(
			expectedCTCollectionIds.length, searchResponse.getTotalHits());

		if (expectedCTCollectionIds.length == 0) {
			return;
		}

		DocumentsAssert.assertValues(
			searchResponse.getResponseString(), searchResponse.getDocuments(),
			Field.UID,
			StringBundler.concat(
				"[", StringUtil.merge(expectedCTCollectionIds, ", "), "]"));
	}

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private UIDFactory _uidFactory;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}