/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.internal.search.spi.model.permission.contributor.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.DocumentsAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Yuri Monteiro
 */
@RunWith(Arquillian.class)
public class OrganizationSearchPermissionFilterContributorTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	@TestInfo("LPD-97608")
	public void testSearch() throws Exception {
		_testSearchWithMultipleOrganizationMemberships();
		_testSearchWithOrganizationHierarchy();
		_testSearchWithoutOrganizationMembership();
		_testSearchWithSingleOrganizationMembership();
	}

	private void _assertSearch(User user, String... expectedUIDs)
		throws Exception {

		SearchRequestBuilder searchRequestBuilder =
			_searchRequestBuilderFactory.builder(
			).companyId(
				TestPropsValues.getCompanyId()
			).emptySearchEnabled(
				true
			).entryClassNames(
				Organization.class.getName()
			).modelIndexerClasses(
				Organization.class
			).withSearchContext(
				searchContext -> searchContext.setUserId(user.getUserId())
			);

		SearchResponse searchResponse = _searcher.search(
			searchRequestBuilder.build());

		Assert.assertEquals(expectedUIDs.length, searchResponse.getTotalHits());

		DocumentsAssert.assertValuesIgnoreRelevance(
			searchResponse.getResponseString(), searchResponse.getDocuments(),
			Field.UID, Arrays.asList(expectedUIDs));
	}

	private void _testSearchWithMultipleOrganizationMemberships()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization();
		Organization organization2 = OrganizationTestUtil.addOrganization();

		User user = UserTestUtil.addUser();

		_userLocalService.addOrganizationUser(
			organization1.getOrganizationId(), user);
		_userLocalService.addOrganizationUser(
			organization2.getOrganizationId(), user);

		_assertSearch(
			user, _uidFactory.getUID(organization1),
			_uidFactory.getUID(organization2));
	}

	private void _testSearchWithOrganizationHierarchy() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		Organization suborganization = OrganizationTestUtil.addOrganization(
			organization.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		User user = UserTestUtil.addUser();

		_userLocalService.addOrganizationUser(
			suborganization.getOrganizationId(), user);

		_assertSearch(user, _uidFactory.getUID(suborganization));

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), user);

		_assertSearch(
			user, _uidFactory.getUID(organization),
			_uidFactory.getUID(suborganization));
	}

	private void _testSearchWithoutOrganizationMembership() throws Exception {
		OrganizationTestUtil.addOrganization();

		_assertSearch(UserTestUtil.addUser());
	}

	private void _testSearchWithSingleOrganizationMembership()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		OrganizationTestUtil.addOrganization();

		User user = UserTestUtil.addUser();

		_assertSearch(user);

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), user);

		_assertSearch(user, _uidFactory.getUID(organization));
	}

	@Inject
	private Searcher _searcher;

	@Inject
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Inject
	private UIDFactory _uidFactory;

	@Inject
	private UserLocalService _userLocalService;

}