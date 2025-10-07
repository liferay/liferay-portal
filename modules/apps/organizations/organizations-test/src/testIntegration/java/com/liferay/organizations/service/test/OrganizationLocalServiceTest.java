/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.organizations.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.object.constants.ObjectValidationRuleConstants;
import com.liferay.object.exception.ObjectValidationRuleEngineException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.object.service.ObjectValidationRuleLocalService;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.exception.OrganizationParentException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.OrganizationConstants;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Jorge Ferrer
 * @author Sergio González
 */
@DataGuard(scope = DataGuard.Scope.METHOD)
@RunWith(Arquillian.class)
public class OrganizationLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_originalPermissionChecker =
			PermissionThreadLocal.getPermissionChecker();
	}

	@After
	public void tearDown() throws Exception {
		PermissionThreadLocal.setPermissionChecker(_originalPermissionChecker);

		for (String pid : _pids) {
			ConfigurationTestUtil.deleteConfiguration(pid);
		}
	}

	@Test
	public void testAddOrganization() throws Exception {
		User user = TestPropsValues.getUser();

		Organization organization = _organizationLocalService.addOrganization(
			user.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization", false);

		_organizations.add(organization);

		List<Organization> organizations = user.getOrganizations(true);

		Assert.assertTrue(
			organizations.toString(), organizations.contains(organization));

		Assert.assertFalse(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), organization.getOrganizationId()));
	}

	@Test
	public void testAddOrganizationWithoutSiteToParentOrganizationWithoutSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization B", false);

		_organizations.add(organizationB);

		_organizations.add(organizationA);

		Assert.assertEquals(
			organizationA.getOrganizationId(),
			organizationB.getParentOrganizationId());

		Group groupB = organizationB.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, groupB.getParentGroupId());
	}

	@Test
	public void testAddOrganizationWithoutSiteToParentOrganizationWithSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", true);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization B", false);

		_organizations.add(organizationB);

		_organizations.add(organizationA);

		Assert.assertEquals(
			organizationA.getOrganizationId(),
			organizationB.getParentOrganizationId());

		Group groupB = organizationB.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, groupB.getParentGroupId());
	}

	@Test
	public void testAddOrganizationWithSiteToParentOrganizationWithoutSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization B", true);

		_organizations.add(organizationB);

		_organizations.add(organizationA);

		Assert.assertEquals(
			organizationA.getOrganizationId(),
			organizationB.getParentOrganizationId());

		Group groupB = organizationB.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, groupB.getParentGroupId());
	}

	@Test
	public void testAddOrganizationWithSiteToParentOrganizationWithSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", true);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization B", true);

		_organizations.add(organizationB);

		_organizations.add(organizationA);

		Assert.assertEquals(
			organizationA.getOrganizationId(),
			organizationB.getParentOrganizationId());

		Group groupB = organizationB.getGroup();

		Assert.assertEquals(
			organizationA.getGroupId(), groupB.getParentGroupId());
	}

	@Test
	public void testAddSiteToOrganizationWithChildOrganizationWithoutSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization B", false);

		_organizations.add(organizationB);

		_organizations.add(organizationA);

		OrganizationTestUtil.addSite(organizationA);

		Group groupB = organizationB.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, groupB.getParentGroupId());
	}

	@Test
	public void testAddSiteToOrganizationWithChildOrganizationWithSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization B", true);

		_organizations.add(organizationB);

		_organizations.add(organizationA);

		organizationA = OrganizationTestUtil.addSite(organizationA);

		Group groupA = organizationA.getGroup();

		Group groupB = organizationB.getGroup();

		Assert.assertEquals(groupA.getGroupId(), groupB.getParentGroupId());
	}

	@Test
	public void testAddSiteToOrganizationWithParentOrganizationWithoutSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization B", false);

		_organizations.add(organizationB);

		_organizations.add(organizationA);

		organizationB = OrganizationTestUtil.addSite(organizationB);

		Group groupB = organizationB.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, groupB.getParentGroupId());
	}

	@Test
	public void testAddSiteToOrganizationWithParentOrganizationWithSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", true);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization B", false);

		_organizations.add(organizationB);

		_organizations.add(organizationA);

		organizationB = OrganizationTestUtil.addSite(organizationB);

		Group groupA = organizationA.getGroup();
		Group groupB = organizationB.getGroup();

		Assert.assertEquals(groupA.getGroupId(), groupB.getParentGroupId());
	}

	@Test
	public void testAddUserOrganizationByEmailAddress() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		User user = UserTestUtil.addUser();

		Assert.assertFalse(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), organization.getOrganizationId()));

		_organizationLocalService.addUserOrganizationByEmailAddress(
			user.getEmailAddress(), organization.getOrganizationId());

		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), organization.getOrganizationId()));

		BaseModelSearchResult<User> baseModelSearchResult =
			_userLocalService.searchUsers(
				user.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
				LinkedHashMapBuilder.<String, Object>put(
					"usersOrgs", Long.valueOf(organization.getOrganizationId())
				).build(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				new Sort("userId", false));

		Assert.assertEquals(1, baseModelSearchResult.getLength());

		List<User> users = baseModelSearchResult.getBaseModels();

		Assert.assertEquals(user, users.get(0));

		_organizationLocalService.deleteUserOrganizationByEmailAddress(
			user.getEmailAddress(), organization.getOrganizationId());

		Assert.assertFalse(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), organization.getOrganizationId()));

		baseModelSearchResult = _userLocalService.searchUsers(
			user.getCompanyId(), null, WorkflowConstants.STATUS_APPROVED,
			LinkedHashMapBuilder.<String, Object>put(
				"usersOrgs", Long.valueOf(organization.getOrganizationId())
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, new Sort("userId", false));

		Assert.assertEquals(0, baseModelSearchResult.getLength());
	}

	@Test
	public void testDeleteOrganization() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		_organizationLocalService.deleteOrganization(
			organization.getOrganizationId());

		Assert.assertNull(
			_organizationLocalService.fetchOrganization(
				organization.getOrganizationId()));

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				0, _portal.getClassNameId(organization.getModelClassName()),
				organization.getPrimaryKey());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			organization.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());
		Assert.assertEquals(
			SystemEventConstants.TYPE_DELETE, systemEvent.getType());
	}

	@Test
	public void testGetNoAssetOrganizations() throws Exception {
		for (Organization organization :
				_organizationLocalService.getNoAssetOrganizations()) {

			_organizationLocalService.deleteOrganization(organization);
		}

		long listTypeId = _listTypeLocalService.getListTypeId(
			TestPropsValues.getCompanyId(),
			ListTypeConstants.ORGANIZATION_STATUS_DEFAULT,
			ListTypeConstants.ORGANIZATION_STATUS);

		Organization organizationA = _organizationLocalService.addOrganization(
			null, TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(),
			OrganizationConstants.TYPE_ORGANIZATION, 0, 0, listTypeId,
			StringPool.BLANK, false, new ServiceContext());

		Organization organizationB = _organizationLocalService.addOrganization(
			null, TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID, "Test2",
			OrganizationConstants.TYPE_ORGANIZATION, 0, 0, listTypeId,
			StringPool.BLANK, false, new ServiceContext());

		_organizations.add(organizationB);

		_organizations.add(organizationA);

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			Organization.class.getName(), organizationB.getOrganizationId());

		Assert.assertNotNull(assetEntry);

		_assetEntryLocalService.deleteAssetEntry(assetEntry);

		List<Organization> organizations =
			_organizationLocalService.getNoAssetOrganizations();

		Assert.assertEquals(organizations.toString(), 1, organizations.size());
		Assert.assertEquals(organizationB, organizations.get(0));
	}

	@Test
	public void testGetOrAddEmptyOrganization() throws Exception {

		// Lazy referencing disabled

		try {
			_organizationLocalService.getOrAddEmptyOrganization(
				RandomTestUtil.randomString(), TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString());

			Assert.fail();
		}
		catch (NoSuchOrganizationException noSuchOrganizationException) {
			Assert.assertNotNull(noSuchOrganizationException);
		}

		// Lazy referencing enabled

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			Organization organization =
				_organizationLocalService.getOrAddEmptyOrganization(
					RandomTestUtil.randomString(),
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					RandomTestUtil.randomString());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, organization.getStatus());
		}
	}

	@Test
	public void testGetOrganizationsAndUsers() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		Organization suborganization = OrganizationTestUtil.addOrganization(
			organization.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_organizations.add(suborganization);

		_organizations.add(organization);

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), TestPropsValues.getUserId());

		Assert.assertEquals(2, getOrganizationsAndUsersCount(organization));

		List<Object> results = getOrganizationsAndUsers(organization);

		Assert.assertEquals(results.toString(), 2, results.size());
		Assert.assertTrue(
			results.toString(), results.contains(suborganization));
		Assert.assertTrue(
			results.toString(), results.contains(TestPropsValues.getUser()));

		_userLocalService.deleteOrganizationUser(
			organization.getOrganizationId(), TestPropsValues.getUser());
	}

	@Test
	public void testGetOrganizationsAndUsersWithNoSuborganizations()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		_organizations.add(organization);

		_userLocalService.addOrganizationUser(
			organization.getOrganizationId(), TestPropsValues.getUserId());

		Assert.assertEquals(1, getOrganizationsAndUsersCount(organization));

		List<Object> results = getOrganizationsAndUsers(organization);

		Assert.assertEquals(results.toString(), 1, results.size());
		Assert.assertTrue(
			results.toString(), results.contains(TestPropsValues.getUser()));

		_userLocalService.deleteOrganizationUser(
			organization.getOrganizationId(), TestPropsValues.getUser());
	}

	@Test
	public void testGetOrganizationsAndUsersWithNoUsers() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		Organization suborganization = OrganizationTestUtil.addOrganization(
			organization.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_organizations.add(suborganization);

		_organizations.add(organization);

		Assert.assertEquals(1, getOrganizationsAndUsersCount(organization));

		List<Object> results = getOrganizationsAndUsers(organization);

		Assert.assertEquals(results.toString(), 1, results.size());
		Assert.assertTrue(
			results.toString(), results.contains(suborganization));
	}

	@Test
	public void testGetOrganizationsAndUsersWithRootOrganization()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		_organizations.add(organization);

		Assert.assertEquals(0, getOrganizationsAndUsersCount(organization));

		List<Object> results = getOrganizationsAndUsers(organization);

		Assert.assertTrue(results.toString(), results.isEmpty());
	}

	@Test
	public void testHasUserOrganization1() throws Exception {
		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization B", false);

		_organizations.add(organizationA);
		_organizations.add(organizationB);

		_userLocalService.addOrganizationUser(
			organizationA.getOrganizationId(), TestPropsValues.getUserId());

		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				TestPropsValues.getUserId(), organizationA.getOrganizationId(),
				false, false));
		Assert.assertFalse(
			_organizationLocalService.hasUserOrganization(
				TestPropsValues.getUserId(), organizationB.getOrganizationId(),
				false, false));

		_userLocalService.deleteOrganizationUser(
			organizationA.getOrganizationId(), TestPropsValues.getUser());
	}

	@Test
	public void testHasUserOrganization2() throws Exception {
		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationAA = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization AA", false);

		_organizations.add(organizationAA);

		_organizations.add(organizationA);

		_userLocalService.addOrganizationUser(
			organizationAA.getOrganizationId(), TestPropsValues.getUserId());

		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				TestPropsValues.getUserId(), organizationA.getOrganizationId(),
				true, false));
		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				TestPropsValues.getUserId(), organizationA.getOrganizationId(),
				true, true));

		_userLocalService.deleteOrganizationUser(
			organizationAA.getOrganizationId(), TestPropsValues.getUser());
	}

	@Test
	public void testMoveOrganizationWithoutSiteToParentOrganizationWithoutSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationAA = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization AA", true);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization B", false);

		organizationAA = _organizationLocalService.updateOrganization(
			null, organizationAA.getCompanyId(),
			organizationAA.getOrganizationId(),
			organizationB.getOrganizationId(), organizationAA.getName(),
			organizationAA.getType(), organizationAA.getRegionId(),
			organizationAA.getCountryId(), organizationAA.getStatusListTypeId(),
			organizationAA.getComments(), false, null, true, null);

		_organizations.add(organizationAA);

		_organizations.add(organizationB);
		_organizations.add(organizationA);

		Assert.assertEquals(
			organizationB.getOrganizationId(),
			organizationAA.getParentOrganizationId());

		Group groupAA = organizationAA.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, groupAA.getParentGroupId());
	}

	@Test
	public void testMoveOrganizationWithoutSiteToParentOrganizationWithSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationAA = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization AA", true);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization B", true);

		organizationAA = _organizationLocalService.updateOrganization(
			null, organizationAA.getCompanyId(),
			organizationAA.getOrganizationId(),
			organizationB.getOrganizationId(), organizationAA.getName(),
			organizationAA.getType(), organizationAA.getRegionId(),
			organizationAA.getCountryId(), organizationAA.getStatusListTypeId(),
			organizationAA.getComments(), false, null, true, null);

		_organizations.add(organizationAA);

		_organizations.add(organizationB);
		_organizations.add(organizationA);

		Assert.assertEquals(
			organizationB.getOrganizationId(),
			organizationAA.getParentOrganizationId());

		Group groupAA = organizationAA.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, groupAA.getParentGroupId());
	}

	@Test
	public void testMoveOrganizationWithSiteToParentOrganizationWithoutSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", true);

		Organization organizationAA = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization AA", true);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization B", false);

		organizationAA = _organizationLocalService.updateOrganization(
			null, organizationAA.getCompanyId(),
			organizationAA.getOrganizationId(),
			organizationB.getOrganizationId(), organizationAA.getName(),
			organizationAA.getType(), organizationAA.getRegionId(),
			organizationAA.getCountryId(), organizationAA.getStatusListTypeId(),
			organizationAA.getComments(), false, null, true, null);

		_organizations.add(organizationAA);

		_organizations.add(organizationB);
		_organizations.add(organizationA);

		Assert.assertEquals(
			organizationB.getOrganizationId(),
			organizationAA.getParentOrganizationId());

		Group groupAA = organizationAA.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, groupAA.getParentGroupId());
	}

	@Test
	public void testMoveOrganizationWithSiteToParentOrganizationWithSite()
		throws Exception {

		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", true);

		Organization organizationAA = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization AA", true);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization B", true);

		organizationAA = _organizationLocalService.updateOrganization(
			null, organizationAA.getCompanyId(),
			organizationAA.getOrganizationId(),
			organizationB.getOrganizationId(), organizationAA.getName(),
			organizationAA.getType(), organizationAA.getRegionId(),
			organizationAA.getCountryId(), organizationAA.getStatusListTypeId(),
			organizationAA.getComments(), false, null, true, null);

		_organizations.add(organizationAA);

		_organizations.add(organizationB);
		_organizations.add(organizationA);

		Assert.assertEquals(
			organizationB.getOrganizationId(),
			organizationAA.getParentOrganizationId());

		Group groupAA = organizationAA.getGroup();

		Assert.assertEquals(
			organizationB.getGroupId(), groupAA.getParentGroupId());
	}

	@Test(expected = OrganizationParentException.class)
	public void testNoCircularParentOrganization() throws Exception {
		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationAA = OrganizationTestUtil.addOrganization(
			organizationA.getOrganizationId(), "Organization AA", false);

		Organization organizationAAA = OrganizationTestUtil.addOrganization(
			organizationAA.getOrganizationId(), "Organization AAA", false);

		Organization organizationAAAA = OrganizationTestUtil.addOrganization(
			organizationAAA.getOrganizationId(), "Organization AAAA", false);

		_organizations.add(organizationAAAA);

		_organizations.add(organizationAAA);
		_organizations.add(organizationAA);
		_organizations.add(organizationA);

		organizationA.setParentOrganizationId(
			organizationAAAA.getOrganizationId());

		_updateOrganization(organizationA);
	}

	@Test
	public void testOrganizationObjectValidationRule() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.fetchObjectDefinitionByClassName(
				TestPropsValues.getCompanyId(), Organization.class.getName());

		_objectValidationRuleLocalService.addObjectValidationRule(
			StringPool.BLANK, TestPropsValues.getUserId(),
			objectDefinition.getObjectDefinitionId(), true,
			ObjectValidationRuleConstants.ENGINE_TYPE_DDM,
			LocalizedMapUtil.getLocalizedMap("This name is invalid."),
			LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
			ObjectValidationRuleConstants.OUTPUT_TYPE_FULL_VALIDATION,
			"name != 'Invalid Name'", false, Collections.emptyList());

		User user = TestPropsValues.getUser();

		AssertUtils.assertFailure(
			ModelListenerException.class,
			ObjectValidationRuleEngineException.class.getName() +
				": This name is invalid.",
			() -> _organizationLocalService.addOrganization(
				user.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				"Invalid Name", false));
	}

	@Test
	public void testParentOrganizationUpdatesAllTreePaths() throws Exception {
		Organization organizationA = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization A", false);

		Organization organizationB = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			"Organization B", false);

		Organization organizationBB = OrganizationTestUtil.addOrganization(
			organizationB.getOrganizationId(), "Organization BB", false);

		Organization organizationBBB = OrganizationTestUtil.addOrganization(
			organizationBB.getOrganizationId(), "Organization BBB", false);

		_organizations.add(organizationBBB);

		_organizations.add(organizationBB);
		_organizations.add(organizationB);
		_organizations.add(organizationA);

		String shallowTreePath = _getTreePath(
			new Organization[] {
				organizationB, organizationBB, organizationBBB
			});

		Assert.assertEquals(shallowTreePath, organizationBBB.getTreePath());

		organizationB.setParentOrganizationId(
			organizationA.getOrganizationId());

		_updateOrganization(organizationB);

		organizationBBB = _organizationLocalService.fetchOrganization(
			organizationBBB.getOrganizationId());

		String deepTreePath = _getTreePath(
			new Organization[] {
				organizationA, organizationB, organizationBB, organizationBBB
			});

		Assert.assertEquals(deepTreePath, organizationBBB.getTreePath());

		organizationB.setParentOrganizationId(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID);

		_updateOrganization(organizationB);

		organizationBBB = _organizationLocalService.fetchOrganization(
			organizationBBB.getOrganizationId());

		Assert.assertEquals(shallowTreePath, organizationBBB.getTreePath());
	}

	@Test
	public void testSearchByUserName() throws Exception {
		User user = UserTestUtil.addUser();

		_organizationLocalService.addOrganization(
			user.getUserId(), OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		BaseModelSearchResult<Organization> baseModelSearchResult =
			_organizationLocalService.searchOrganizations(
				TestPropsValues.getCompanyId(),
				OrganizationConstants.ANY_PARENT_ORGANIZATION_ID,
				user.getFullName(), new LinkedHashMap<>(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(0, baseModelSearchResult.getLength());
	}

	@Test
	public void testSearchOrganizationsAndUsers() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		Organization suborganization = OrganizationTestUtil.addOrganization(
			organization.getOrganizationId(), "Organization1", false);

		_organizations.add(suborganization);

		_organizations.add(organization);

		_user = UserTestUtil.addUser("user1", TestPropsValues.getGroupId());

		_userLocalService.addOrganizationUsers(
			organization.getOrganizationId(), new long[] {_user.getUserId()});

		Hits hits = searchOrganizationsAndUsers(organization, null);

		Assert.assertEquals(hits.toString(), 2, hits.getLength());

		hits = searchOrganizationsAndUsers(organization, "Organization1");

		Document document = hits.doc(0);

		Assert.assertEquals(
			document.toString(),
			String.valueOf(suborganization.getOrganizationId()),
			document.get(Field.ORGANIZATION_ID));

		hits = searchOrganizationsAndUsers(organization, "user1");

		document = hits.doc(0);

		Assert.assertEquals(
			document.toString(), String.valueOf(_user.getUserId()),
			document.get(Field.USER_ID));

		_userLocalService.deleteOrganizationUser(
			organization.getOrganizationId(), _user);
	}

	@Test
	public void testSearchOrganizationsAndUsersWhenNoOrganizations()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		_organizations.add(organization);

		_user = UserTestUtil.addUser("user1", TestPropsValues.getGroupId());

		_userLocalService.addOrganizationUsers(
			organization.getOrganizationId(), new long[] {_user.getUserId()});

		Hits hits = searchOrganizationsAndUsers(organization, null);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());

		Document document = hits.doc(0);

		Assert.assertEquals(
			document.toString(), String.valueOf(_user.getUserId()),
			document.get(Field.USER_ID));

		hits = searchOrganizationsAndUsers(organization, "Organization1");

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		hits = searchOrganizationsAndUsers(organization, "user1");

		document = hits.doc(0);

		Assert.assertEquals(
			document.toString(), String.valueOf(_user.getUserId()),
			document.get(Field.USER_ID));

		_userLocalService.deleteOrganizationUser(
			organization.getOrganizationId(), _user);
	}

	@Test
	public void testSearchOrganizationsAndUsersWhenNoUsers() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();

		Organization suborganization = OrganizationTestUtil.addOrganization(
			organization.getOrganizationId(), "Organization1", false);

		_organizations.add(suborganization);

		_organizations.add(organization);

		Hits hits = searchOrganizationsAndUsers(organization, null);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());

		Document document = hits.doc(0);

		Assert.assertEquals(
			document.toString(),
			String.valueOf(suborganization.getOrganizationId()),
			document.get(Field.ORGANIZATION_ID));

		hits = searchOrganizationsAndUsers(organization, "Organization1");

		document = hits.doc(0);

		Assert.assertEquals(
			document.toString(),
			String.valueOf(suborganization.getOrganizationId()),
			document.get(Field.ORGANIZATION_ID));

		hits = searchOrganizationsAndUsers(organization, "user1");

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	@Test
	public void testSearchOrganizationsAndUsersWithRootOrganization()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		_organizations.add(organization);

		Hits hits = searchOrganizationsAndUsers(organization, null);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		hits = searchOrganizationsAndUsers(organization, "Organization1");

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		hits = searchOrganizationsAndUsers(organization, "user1");

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	@Test
	public void testSearchOrganizationsByType() throws Exception {
		_organizations.add(OrganizationTestUtil.addOrganization());

		for (int i = 0; i < 5; i++) {
			String organizationType = RandomTestUtil.randomString();

			_pids.add(
				ConfigurationTestUtil.createFactoryConfiguration(
					"com.liferay.organizations.internal.configuration." +
						"OrganizationTypeConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"name", organizationType
					).build()));

			_organizations.add(
				OrganizationTestUtil.addOrganization(organizationType));
			_organizations.add(
				OrganizationTestUtil.addOrganization(organizationType));
		}

		List<Organization> expectedOrganizations = new ArrayList<>(
			_organizationLocalService.getOrganizations(
				TestPropsValues.getCompanyId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID));

		_testSearchOrganizationsByType(expectedOrganizations, "asc");
		_testSearchOrganizationsByType(expectedOrganizations, "desc");
	}

	@Test
	public void testUpdateOrganizationWithLazyReferencingEnabled()
		throws Exception {

		try (SafeCloseable safeCloseable =
				LazyReferencingThreadLocal.setEnabledWithSafeCloseable(true)) {

			Organization organization =
				_organizationLocalService.getOrAddEmptyOrganization(
					RandomTestUtil.randomString(),
					TestPropsValues.getCompanyId(), TestPropsValues.getUserId(),
					RandomTestUtil.randomString());

			Assert.assertEquals(
				WorkflowConstants.STATUS_EMPTY, organization.getStatus());

			organization = _organizationLocalService.updateOrganization(
				organization.getExternalReferenceCode(),
				organization.getCompanyId(), organization.getOrganizationId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				organization.getName(), organization.getType(),
				organization.getRegionId(), organization.getCountryId(),
				organization.getStatusListTypeId(), organization.getComments(),
				false, null, true, null);

			Assert.assertEquals(
				WorkflowConstants.STATUS_APPROVED, organization.getStatus());
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected List<Object> getOrganizationsAndUsers(Organization organization) {
		return _organizationLocalService.getOrganizationsAndUsers(
			organization.getCompanyId(), organization.getOrganizationId(),
			WorkflowConstants.STATUS_ANY, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
			null);
	}

	protected int getOrganizationsAndUsersCount(Organization organization) {
		return _organizationLocalService.getOrganizationsAndUsersCount(
			organization.getCompanyId(), organization.getOrganizationId(),
			WorkflowConstants.STATUS_ANY);
	}

	protected Hits searchOrganizationsAndUsers(
			Organization parentOrganization, String keywords)
		throws Exception {

		return _organizationLocalService.searchOrganizationsAndUsers(
			parentOrganization.getCompanyId(),
			parentOrganization.getOrganizationId(), keywords,
			WorkflowConstants.STATUS_ANY, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	private String _getTreePath(Organization[] organizations) {
		StringBundler sb = new StringBundler();

		sb.append(StringPool.FORWARD_SLASH);

		for (Organization organization : organizations) {
			sb.append(organization.getOrganizationId());
			sb.append(StringPool.FORWARD_SLASH);
		}

		return sb.toString();
	}

	private void _testSearchOrganizationsByType(
			List<Organization> expectedOrganizations, String orderByType)
		throws Exception {

		Sort sort = SortFactoryUtil.getSort(
			Organization.class, "type", orderByType);

		BaseModelSearchResult<Organization> baseModelSearchResult =
			_organizationLocalService.searchOrganizations(
				TestPropsValues.getCompanyId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID, null,
				null, QueryUtil.ALL_POS, QueryUtil.ALL_POS, sort);

		Comparator<Organization> comparator =
			(organization1, organization2) -> {
				String typeOrder1 = organization1.getType();
				String typeOrder2 = organization2.getType();

				int value = typeOrder1.compareToIgnoreCase(typeOrder2);

				if (value == 0) {
					String name1 = organization1.getName();
					String name2 = organization2.getName();

					value = name1.compareToIgnoreCase(name2);
				}

				return value;
			};

		if (sort.isReverse()) {
			comparator = comparator.reversed();
		}

		expectedOrganizations.sort(comparator);

		List<Organization> actualOrganizations =
			baseModelSearchResult.getBaseModels();

		Assert.assertEquals(
			actualOrganizations.toString(), expectedOrganizations.size(),
			actualOrganizations.size());

		for (int i = 0; i < expectedOrganizations.size(); i++) {
			Assert.assertEquals(
				expectedOrganizations.get(i), actualOrganizations.get(i));
		}
	}

	private Organization _updateOrganization(Organization organization)
		throws Exception {

		Group organizationGroup = organization.getGroup();

		return _organizationLocalService.updateOrganization(
			null, organization.getCompanyId(), organization.getOrganizationId(),
			organization.getParentOrganizationId(), organization.getName(),
			organization.getType(), organization.getRegionId(),
			organization.getCountryId(), organization.getStatusListTypeId(),
			organization.getComments(), false, null, organizationGroup.isSite(),
			null);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private final List<Organization> _organizations = new ArrayList<>();
	private PermissionChecker _originalPermissionChecker;
	private final List<String> _pids = new ArrayList<>();

	@Inject
	private Portal _portal;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}