/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.provider.test;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.expando.kernel.service.ExpandoValueLocalService;
import com.liferay.expando.test.util.ExpandoTestUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.odata.normalizer.Normalizer;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.segments.constants.SegmentsEntryConstants;
import com.liferay.segments.context.Context;
import com.liferay.segments.criteria.Criteria;
import com.liferay.segments.criteria.CriteriaSerializer;
import com.liferay.segments.criteria.contributor.SegmentsCriteriaContributor;
import com.liferay.segments.model.SegmentsEntry;
import com.liferay.segments.provider.SegmentsEntryProvider;
import com.liferay.segments.service.SegmentsEntryRelLocalService;
import com.liferay.segments.test.util.SegmentsTestUtil;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Eduardo García
 */
@RunWith(Arquillian.class)
public class DefaultSegmentsEntryProviderTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();
	}

	@Test
	public void testGetSegmentsEntryClassPKsWithMultipleCriterion()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		_organizations.add(organization);

		_user1 = UserTestUtil.addOrganizationUser(
			organization, RoleConstants.ORGANIZATION_USER);

		_user2 = UserTestUtil.addUser();

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		_userOrganizationSegmentsCriteriaContributor.contribute(
			criteria, String.format("(name eq '%s')", organization.getName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Assert.assertEquals(
			1,
			_segmentsEntryProvider.getSegmentsEntryClassPKsCount(
				segmentsEntry.getSegmentsEntryId()));
		Assert.assertArrayEquals(
			new long[] {_user1.getUserId()},
			_segmentsEntryProvider.getSegmentsEntryClassPKs(
				segmentsEntry.getSegmentsEntryId(), 0, 1));
	}

	@Test
	public void testGetSegmentsEntryClassPKsWithMultipleCriterionNotMatching()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		_organizations.add(organization);

		_user1 = UserTestUtil.addUser();
		_user2 = UserTestUtil.addUser();

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		_userOrganizationSegmentsCriteriaContributor.contribute(
			criteria, String.format("(name eq '%s')", organization.getName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Assert.assertEquals(
			0,
			_segmentsEntryProvider.getSegmentsEntryClassPKsCount(
				segmentsEntry.getSegmentsEntryId()));
	}

	@Test
	public void testGetSegmentsEntryClassPKsWithSingleCriterion()
		throws Exception {

		_user1 = UserTestUtil.addUser(_group.getGroupId());
		_user2 = UserTestUtil.addUser(_group.getGroupId());

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Assert.assertEquals(
			1,
			_segmentsEntryProvider.getSegmentsEntryClassPKsCount(
				segmentsEntry.getSegmentsEntryId()));
		Assert.assertArrayEquals(
			new long[] {_user1.getUserId()},
			_segmentsEntryProvider.getSegmentsEntryClassPKs(
				segmentsEntry.getSegmentsEntryId(), 0, 1));
	}

	@Test
	public void testGetSegmentsEntryClassPKsWithoutCriteria() throws Exception {
		_user1 = UserTestUtil.addUser(_group.getGroupId());
		_user2 = UserTestUtil.addUser(_group.getGroupId());

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(new Criteria()));

		_segmentsEntryRelLocalService.addSegmentsEntryRel(
			segmentsEntry.getSegmentsEntryId(),
			_portal.getClassNameId(User.class.getName()), _user1.getUserId(),
			ServiceContextTestUtil.getServiceContext(_group.getGroupId()));

		Assert.assertEquals(
			1,
			_segmentsEntryProvider.getSegmentsEntryClassPKsCount(
				segmentsEntry.getSegmentsEntryId()));
		Assert.assertArrayEquals(
			new long[] {_user1.getUserId()},
			_segmentsEntryProvider.getSegmentsEntryClassPKs(
				segmentsEntry.getSegmentsEntryId(), 0, 1));
	}

	@Test
	@TestInfo("LPD-86103")
	public void testGetSegmentsEntryIdsWithBooleanCustomField()
		throws Exception {

		ExpandoTable expandoTable = _expandoTableLocalService.addDefaultTable(
			TestPropsValues.getCompanyId(), User.class.getName());

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			expandoTable, RandomTestUtil.randomString(),
			ExpandoColumnConstants.BOOLEAN);

		_expandoValueLocalService.addValue(
			TestPropsValues.getCompanyId(), User.class.getName(),
			expandoTable.getName(), expandoColumn.getName(),
			TestPropsValues.getUserId(), true);

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			StringBundler.concat(
				"(customField/_", expandoColumn.getColumnId(), "_",
				Normalizer.normalizeIdentifier(expandoColumn.getName()),
				" eq true)"),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Assert.assertArrayEquals(
			new long[] {segmentsEntry.getSegmentsEntryId()},
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				TestPropsValues.getUserId(), new Context()));
	}

	@Test
	public void testGetSegmentsEntryIdsWithContextCriterionAndGuestUser()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "en");
		context.put(Context.SIGNED_IN, false);

		Assert.assertArrayEquals(
			new long[] {segmentsEntry.getSegmentsEntryId()},
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	@TestInfo("LPS-127109")
	public void testGetSegmentsEntryIdsWithContextCriterionAndGuestUserWithoutSignedInContext()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "en");

		Assert.assertArrayEquals(
			new long[] {segmentsEntry.getSegmentsEntryId()},
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithContextCriterionAndModelCriterion()
		throws Exception {

		_user1 = UserTestUtil.addUser(_group.getGroupId());
		_user2 = UserTestUtil.addUser(_group.getGroupId());

		Criteria criteria1 = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria1, "(languageId eq 'en')", Criteria.Conjunction.AND);

		_userSegmentsCriteriaContributor.contribute(
			criteria1,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry1 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), _user1.getUserId());
		SegmentsEntry segmentsEntry2 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria1));

		Criteria criteria2 = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria2, "(languageId eq 'en')", Criteria.Conjunction.OR);

		_userSegmentsCriteriaContributor.contribute(
			criteria2,
			String.format("(firstName eq '%s')", _user2.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry3 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria2));

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "en");
		context.put(Context.SIGNED_IN, true);

		long[] segmentsEntryIds = _segmentsEntryProvider.getSegmentsEntryIds(
			_group.getGroupId(), User.class.getName(), _user1.getUserId(),
			context);

		Assert.assertEquals(
			StringUtil.merge(segmentsEntryIds, StringPool.COMMA), 3,
			segmentsEntryIds.length);
		Assert.assertTrue(
			ArrayUtil.containsAll(
				new long[] {
					segmentsEntry1.getSegmentsEntryId(),
					segmentsEntry2.getSegmentsEntryId(),
					segmentsEntry3.getSegmentsEntryId()
				},
				segmentsEntryIds));
	}

	@Test
	public void testGetSegmentsEntryIdsWithContextCriterionAndModelCriterionAndGuestUser()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.AND);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", guestUser.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "en");
		context.put(Context.SIGNED_IN, false);

		Assert.assertArrayEquals(
			new long[0],
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithContextCriterionAndModelCriterionAndGuestUserWithoutSignedInContext()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.AND);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", guestUser.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "en");

		Assert.assertArrayEquals(
			new long[0],
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithContextCriterionOrModelCriterionAndGuestUser()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.OR);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", guestUser.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "en");
		context.put(Context.SIGNED_IN, false);

		Assert.assertArrayEquals(
			new long[] {segmentsEntry.getSegmentsEntryId()},
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithContextCriterionOrModelCriterionAndGuestUserWithoutSignedInContext()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.OR);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", guestUser.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "en");

		Assert.assertArrayEquals(
			new long[] {segmentsEntry.getSegmentsEntryId()},
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithModelCriterionAndGuestUser()
		throws Exception {

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", guestUser.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Context context = new Context();

		context.put(Context.SIGNED_IN, false);

		Assert.assertArrayEquals(
			new long[0],
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithMultipleModelCriterion()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		_organizations.add(organization);

		_user1 = UserTestUtil.addOrganizationUser(
			organization, RoleConstants.ORGANIZATION_USER);

		_user2 = UserTestUtil.addUser(_group.getGroupId());

		Criteria criteria1 = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria1,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		_userOrganizationSegmentsCriteriaContributor.contribute(
			criteria1, String.format("(name eq '%s')", organization.getName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry1 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), _user1.getUserId());
		SegmentsEntry segmentsEntry2 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria1));

		Criteria criteria2 = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria2,
			String.format("(firstName eq '%s')", _user2.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), _user2.getUserId());
		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria2));

		long[] segmentsEntryIds = _segmentsEntryProvider.getSegmentsEntryIds(
			_group.getGroupId(), User.class.getName(), _user1.getUserId());

		Assert.assertEquals(
			StringUtil.merge(segmentsEntryIds, StringPool.COMMA), 2,
			segmentsEntryIds.length);
		Assert.assertTrue(
			ArrayUtil.containsAll(
				new long[] {
					segmentsEntry1.getSegmentsEntryId(),
					segmentsEntry2.getSegmentsEntryId()
				},
				segmentsEntryIds));
	}

	@Test
	public void testGetSegmentsEntryIdsWithNonmatchingContextCriterionAndGuestUser()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "es");
		context.put(Context.SIGNED_IN, false);

		Assert.assertArrayEquals(
			new long[0],
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithNonmatchingContextCriterionAndGuestUserWithoutSignedInContext()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "es");

		Assert.assertArrayEquals(
			new long[0],
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithNonmatchingContextCriterionAndMatchingModelCriterion()
		throws Exception {

		_user1 = UserTestUtil.addUser(_group.getGroupId());

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.AND);

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Context context = new Context();

		context.put("languageId", "es");

		Assert.assertArrayEquals(
			new long[0],
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(), _user1.getUserId(),
				context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithNonmatchingContextCriterionAndModelCriterionAndGuestUser()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.AND);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", guestUser.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "es");
		context.put(Context.SIGNED_IN, false);

		Assert.assertArrayEquals(
			new long[0],
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithNonmatchingContextCriterionAndModelCriterionAndGuestUserWithoutSignedInContext()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.AND);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", guestUser.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "es");

		Assert.assertArrayEquals(
			new long[0],
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithNonmatchingContextCriterionOrModelCriterionAndGuestUser()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.OR);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", guestUser.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "es");
		context.put(Context.SIGNED_IN, false);

		Assert.assertArrayEquals(
			new long[0],
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithNonmatchingContextCriterionOrModelCriterionAndGuestUserWithoutSignedInContext()
		throws Exception {

		Criteria criteria = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria, "(languageId eq 'en')", Criteria.Conjunction.OR);

		Company company = _companyLocalService.getCompany(
			TestPropsValues.getCompanyId());

		User guestUser = company.getGuestUser();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format("(firstName eq '%s')", guestUser.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "es");

		Assert.assertArrayEquals(
			new long[0],
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				guestUser.getUserId(), context));
	}

	@Test
	public void testGetSegmentsEntryIdsWithSingleContextCriterion()
		throws Exception {

		_user1 = UserTestUtil.addUser(_group.getGroupId());
		_user2 = UserTestUtil.addUser(_group.getGroupId());

		Criteria criteria1 = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria1, "(languageId eq 'en')", Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry1 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), _user1.getUserId());
		SegmentsEntry segmentsEntry2 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria1));

		Criteria criteria2 = new Criteria();

		_contextSegmentsCriteriaContributor.contribute(
			criteria2, "(languageId eq 'fr')", Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria2));

		Context context = new Context();

		context.put(Context.LANGUAGE_ID, "en");
		context.put(Context.SIGNED_IN, true);

		long[] segmentsEntryIds = _segmentsEntryProvider.getSegmentsEntryIds(
			_group.getGroupId(), User.class.getName(), _user1.getUserId(),
			context);

		Assert.assertEquals(
			StringUtil.merge(segmentsEntryIds, StringPool.COMMA), 2,
			segmentsEntryIds.length);
		Assert.assertTrue(
			ArrayUtil.containsAll(
				segmentsEntryIds,
				new long[] {
					segmentsEntry1.getSegmentsEntryId(),
					segmentsEntry2.getSegmentsEntryId()
				}));
	}

	@Test
	public void testGetSegmentsEntryIdsWithSingleModelCriterion()
		throws Exception {

		_user1 = UserTestUtil.addUser(_group.getGroupId());
		_user2 = UserTestUtil.addUser(_group.getGroupId());

		Criteria criteria1 = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria1,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry1 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), _user1.getUserId());
		SegmentsEntry segmentsEntry2 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria1));

		Criteria criteria2 = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria2,
			String.format("(firstName eq '%s')", _user2.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), _user2.getUserId());
		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria2));

		long[] segmentsEntryIds = _segmentsEntryProvider.getSegmentsEntryIds(
			_group.getGroupId(), User.class.getName(), _user1.getUserId(), null,
			new long[0], new long[0]);

		Assert.assertEquals(
			StringUtil.merge(segmentsEntryIds, StringPool.COMMA), 2,
			segmentsEntryIds.length);
		Assert.assertTrue(
			ArrayUtil.containsAll(
				new long[] {
					segmentsEntry1.getSegmentsEntryId(),
					segmentsEntry2.getSegmentsEntryId()
				},
				segmentsEntryIds));
	}

	@Test
	public void testGetSegmentsEntryIdsWithSingleModelCriterionAndFilterSegmentEntryIds()
		throws Exception {

		_user1 = UserTestUtil.addUser(_group.getGroupId());
		_user2 = UserTestUtil.addUser(_group.getGroupId());

		Criteria criteria1 = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria1,
			String.format("(firstName eq '%s')", _user1.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry1 = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), _user1.getUserId());

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria1));

		Criteria criteria2 = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria2,
			String.format("(firstName eq '%s')", _user2.getFirstName()),
			Criteria.Conjunction.AND);

		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), _user2.getUserId());
		SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria2));

		long[] segmentsEntryIds = _segmentsEntryProvider.getSegmentsEntryIds(
			_group.getGroupId(), User.class.getName(), _user1.getUserId(), null,
			new long[] {segmentsEntry1.getSegmentsEntryId()}, new long[0]);

		Assert.assertTrue(
			StringUtil.merge(segmentsEntryIds, StringPool.COMMA),
			ArrayUtil.containsAll(
				new long[] {segmentsEntry1.getSegmentsEntryId()},
				segmentsEntryIds));
	}

	@Test
	@TestInfo("LPD-86335")
	public void testGetSegmentsEntryIdsWithSpecialCharactersInCustomFieldName()
		throws Exception {

		ExpandoTable expandoTable = _expandoTableLocalService.addDefaultTable(
			TestPropsValues.getCompanyId(), User.class.getName());

		ExpandoColumn expandoColumn = ExpandoTestUtil.addColumn(
			expandoTable, "custom-field", ExpandoColumnConstants.STRING);

		_expandoValueLocalService.addValue(
			TestPropsValues.getCompanyId(), User.class.getName(),
			expandoTable.getName(), expandoColumn.getName(),
			TestPropsValues.getUserId(), "test");

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			StringBundler.concat(
				"(customField/_", expandoColumn.getColumnId(), "_",
				Normalizer.normalizeIdentifier(expandoColumn.getName()),
				" eq 'test')"),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		Assert.assertArrayEquals(
			new long[] {segmentsEntry.getSegmentsEntryId()},
			_segmentsEntryProvider.getSegmentsEntryIds(
				_group.getGroupId(), User.class.getName(),
				TestPropsValues.getUserId(), new Context()));
	}

	@Test
	public void testGetSegmentsEntryIdsWithUserDateModifiedCriterion()
		throws Exception {

		_user1 = UserTestUtil.addUser(_group.getGroupId());

		Criteria criteria = new Criteria();

		_userSegmentsCriteriaContributor.contribute(
			criteria,
			String.format(
				"dateModified eq %s",
				ISO8601Utils.format(_user1.getModifiedDate())),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		long[] segmentsEntryIds = _segmentsEntryProvider.getSegmentsEntryIds(
			_group.getGroupId(), User.class.getName(), _user1.getUserId());

		Assert.assertArrayEquals(
			new long[] {segmentsEntry.getSegmentsEntryId()}, segmentsEntryIds);
		Assert.assertEquals(
			StringUtil.merge(segmentsEntryIds, StringPool.COMMA), 1,
			segmentsEntryIds.length);
	}

	@Test
	public void testGetSegmentsEntryIdsWithUserModelCriterionAndUserCreatedAfterSegmentsEntry()
		throws Exception {

		Criteria criteria = new Criteria();

		String firstName = RandomTestUtil.randomString();

		_userSegmentsCriteriaContributor.contribute(
			criteria, String.format("(firstName eq '%s')", firstName),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		_user1 = UserTestUtil.addUser(
			RandomTestUtil.randomString(), LocaleUtil.US, firstName,
			RandomTestUtil.randomString(), new long[0]);

		long[] segmentsEntryIds = _segmentsEntryProvider.getSegmentsEntryIds(
			_group.getGroupId(), User.class.getName(), _user1.getUserId());

		Assert.assertArrayEquals(
			new long[] {segmentsEntry.getSegmentsEntryId()}, segmentsEntryIds);
		Assert.assertEquals(
			StringUtil.merge(segmentsEntryIds, StringPool.COMMA), 1,
			segmentsEntryIds.length);
	}

	@Test
	public void testGetSegmentsEntryIdsWithUserOrganizationDateModifiedCriterion()
		throws Exception {

		_user1 = UserTestUtil.addOrganizationUser(
			OrganizationTestUtil.addOrganization(),
			RoleConstants.ORGANIZATION_USER);

		Criteria criteria = new Criteria();

		_userOrganizationSegmentsCriteriaContributor.contribute(
			criteria,
			String.format(
				"dateModified eq %s",
				ISO8601Utils.format(_user1.getModifiedDate())),
			Criteria.Conjunction.AND);

		SegmentsEntry segmentsEntry = SegmentsTestUtil.addSegmentsEntry(
			_group.getGroupId(), CriteriaSerializer.serialize(criteria));

		long[] segmentsEntryIds = _segmentsEntryProvider.getSegmentsEntryIds(
			_group.getGroupId(), User.class.getName(), _user1.getUserId());

		Assert.assertArrayEquals(
			new long[] {segmentsEntry.getSegmentsEntryId()}, segmentsEntryIds);
		Assert.assertEquals(
			StringUtil.merge(segmentsEntryIds, StringPool.COMMA), 1,
			segmentsEntryIds.length);
	}

	@Inject
	private CompanyLocalService _companyLocalService;

	@Inject(
		filter = "segments.criteria.contributor.key=context",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _contextSegmentsCriteriaContributor;

	@Inject
	private ExpandoTableLocalService _expandoTableLocalService;

	@Inject
	private ExpandoValueLocalService _expandoValueLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private final List<Organization> _organizations = new ArrayList<>();

	@Inject
	private Portal _portal;

	@Inject(
		filter = "segments.entry.provider.source=" + SegmentsEntryConstants.SOURCE_DEFAULT,
		type = SegmentsEntryProvider.class
	)
	private SegmentsEntryProvider _segmentsEntryProvider;

	@Inject
	private SegmentsEntryRelLocalService _segmentsEntryRelLocalService;

	@DeleteAfterTestRun
	private User _user1;

	@DeleteAfterTestRun
	private User _user2;

	@Inject
	private UserLocalService _userLocalService;

	@Inject(
		filter = "segments.criteria.contributor.key=user-organization",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor
		_userOrganizationSegmentsCriteriaContributor;

	@Inject(
		filter = "segments.criteria.contributor.key=user",
		type = SegmentsCriteriaContributor.class
	)
	private SegmentsCriteriaContributor _userSegmentsCriteriaContributor;

}