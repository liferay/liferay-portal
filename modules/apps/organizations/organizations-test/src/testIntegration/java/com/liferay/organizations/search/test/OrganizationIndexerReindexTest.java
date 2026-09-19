/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.search.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.SearchEngineHelper;
import com.liferay.portal.kernel.service.CountryService;
import com.liferay.portal.kernel.service.ListTypeService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.RegionService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.rule.SynchronousDestinationTestRule;
import com.liferay.portal.search.model.uid.UIDFactory;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.search.test.util.FieldValuesAssert;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.users.admin.test.util.search.GroupBlueprint;
import com.liferay.users.admin.test.util.search.GroupSearchFixture;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Igor Fabiano Nazar
 * @author Luan Maoski
 */
@RunWith(Arquillian.class)
public class OrganizationIndexerReindexTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE,
			SynchronousDestinationTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		GroupSearchFixture groupSearchFixture = new GroupSearchFixture();

		Group group = groupSearchFixture.addGroup(new GroupBlueprint());

		OrganizationFixture organizationFixture = new OrganizationFixture(
			countryService, language, listTypeService, organizationService,
			regionService);

		organizationFixture.setUp();

		organizationFixture.setGroup(group);

		_group = group;
		_groups = groupSearchFixture.getGroups();

		_organizationFixture = organizationFixture;
		_organizations = organizationFixture.getOrganizations();
	}

	@Test
	public void testIndexedFields() throws Exception {
		String searchTerm = "abcd efgh";

		Organization organization = _organizationFixture.createOrganization(
			searchTerm);

		assertFieldValue(Field.NAME, searchTerm, searchTerm);

		deleteDocument(
			organization.getCompanyId(), uidFactory.getUID(organization));

		assertNoHits(searchTerm);

		reindexAllIndexerModels();

		assertFieldValue(Field.NAME, searchTerm, searchTerm);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertFieldValue(
		String fieldName, String fieldValue, String searchTerm) {

		FieldValuesAssert.assertFieldValue(
			fieldName, fieldValue, search(searchTerm));
	}

	protected void assertNoHits(String searchTerm) {
		FieldValuesAssert.assertFieldValues(
			Collections.emptyMap(), search(searchTerm));
	}

	protected void deleteDocument(long companyId, String uid) throws Exception {
		indexWriterHelper.deleteDocument(companyId, uid, true);
	}

	protected void reindexAllIndexerModels() throws Exception {
		indexer.reindexCompany(_group.getCompanyId());
	}

	protected SearchResponse search(String searchTerm) {
		return searcher.search(
			searchRequestBuilderFactory.builder(
			).companyId(
				_group.getCompanyId()
			).fields(
				StringPool.STAR
			).modelIndexerClasses(
				Organization.class
			).queryString(
				searchTerm
			).build());
	}

	@Inject
	protected CountryService countryService;

	@Inject
	protected IndexWriterHelper indexWriterHelper;

	@Inject(
		filter = "indexer.class.name=com.liferay.portal.kernel.model.Organization"
	)
	protected Indexer<Organization> indexer;

	@Inject
	protected Language language;

	@Inject
	protected ListTypeService listTypeService;

	@Inject
	protected OrganizationService organizationService;

	@Inject
	protected RegionService regionService;

	@Inject
	protected ResourcePermissionLocalService resourcePermissionLocalService;

	@Inject
	protected SearchEngineHelper searchEngineHelper;

	@Inject
	protected SearchRequestBuilderFactory searchRequestBuilderFactory;

	@Inject
	protected Searcher searcher;

	@Inject
	protected UIDFactory uidFactory;

	@Inject
	protected UserLocalService userLocalService;

	private Group _group;

	@DeleteAfterTestRun
	private List<Group> _groups;

	private OrganizationFixture _organizationFixture;

	@DeleteAfterTestRun
	private List<Organization> _organizations;

}