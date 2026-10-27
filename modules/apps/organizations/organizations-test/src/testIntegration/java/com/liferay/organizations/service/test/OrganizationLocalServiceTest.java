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
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.ConfigurationTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.DuplicateOrganizationException;
import com.liferay.portal.kernel.exception.GroupFriendlyURLException;
import com.liferay.portal.kernel.exception.ModelListenerException;
import com.liferay.portal.kernel.exception.NoSuchOrganizationException;
import com.liferay.portal.kernel.exception.OrganizationNameException;
import com.liferay.portal.kernel.exception.OrganizationParentException;
import com.liferay.portal.kernel.lazy.referencing.LazyReferencingThreadLocal;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.ListTypeConstants;
import com.liferay.portal.kernel.model.ModelHintsUtil;
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
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ListTypeLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DataGuard;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
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
		_testAddOrganization();
		_testAddOrganizationWithObjectValidationRule();
		_testAddOrganizationWithSiteToParentWithSite();
		_testAddOrganizationWithSiteToParentWithoutSite();
		_testAddOrganizationWithoutSiteToParentWithSite();
		_testAddOrganizationWithoutSiteToParentWithoutSite();
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
	public void testClearGroupOrganizations() throws Exception {
		Group group = GroupTestUtil.addGroup();
		Organization organization = OrganizationTestUtil.addOrganization();

		_organizationLocalService.addGroupOrganization(
			group.getGroupId(), organization.getOrganizationId());

		User user = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			user.getUserId(), organization.getOrganizationId());

		_assertSearchUser(true, "inheritUsersGroups", group.getGroupId(), user);

		_organizationLocalService.clearGroupOrganizations(group.getGroupId());

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user);
	}

	@Test
	public void testClearUserOrganizations() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();
		User user = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			user.getUserId(), organization.getOrganizationId());

		_assertSearchUser(
			true, "usersOrgs", organization.getOrganizationId(), user);

		_organizationLocalService.clearUserOrganizations(user.getUserId());

		_assertSearchUser(
			false, "usersOrgs", organization.getOrganizationId(), user);
	}

	@Test
	public void testDeleteGroupOrganization() throws Exception {
		Group group = GroupTestUtil.addGroup();
		Organization organization = OrganizationTestUtil.addOrganization();

		_organizationLocalService.addGroupOrganization(
			group.getGroupId(), organization.getOrganizationId());

		User user = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			user.getUserId(), organization.getOrganizationId());

		_assertSearchUser(true, "inheritUsersGroups", group.getGroupId(), user);

		_organizationLocalService.deleteGroupOrganization(
			group.getGroupId(), organization.getOrganizationId());

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user);

		_organizationLocalService.addGroupOrganization(
			group.getGroupId(), organization);

		_assertSearchUser(true, "inheritUsersGroups", group.getGroupId(), user);

		_organizationLocalService.deleteGroupOrganization(
			group.getGroupId(), organization);

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user);
	}

	@Test
	public void testDeleteGroupOrganizations() throws Exception {
		Group group = GroupTestUtil.addGroup();
		Organization organization = OrganizationTestUtil.addOrganization();

		_organizationLocalService.addGroupOrganizations(
			group.getGroupId(), new long[] {organization.getOrganizationId()});

		User user = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			user.getUserId(), organization.getOrganizationId());

		_assertSearchUser(true, "inheritUsersGroups", group.getGroupId(), user);

		_organizationLocalService.deleteGroupOrganizations(
			group.getGroupId(), new long[] {organization.getOrganizationId()});

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user);

		_organizationLocalService.addGroupOrganizations(
			group.getGroupId(), Collections.singletonList(organization));

		_assertSearchUser(true, "inheritUsersGroups", group.getGroupId(), user);

		_organizationLocalService.deleteGroupOrganizations(
			group.getGroupId(), Collections.singletonList(organization));

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user);
	}

	@Test
	public void testDeleteOrganization() throws Exception {
		Group group = GroupTestUtil.addGroup();
		Organization organization = OrganizationTestUtil.addOrganization();

		_organizationLocalService.addGroupOrganization(
			group.getGroupId(), organization.getOrganizationId());

		User user = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			user.getUserId(), organization.getOrganizationId());

		_userLocalService.updateStatus(
			user.getUserId(), WorkflowConstants.STATUS_INACTIVE,
			new ServiceContext());

		_assertSearchUser(true, "inheritUsersGroups", group.getGroupId(), user);
		_assertSearchUser(
			true, "usersOrgs", organization.getOrganizationId(), user);

		_organizationLocalService.deleteOrganization(
			organization.getOrganizationId());

		Assert.assertNull(
			_organizationLocalService.fetchOrganization(
				organization.getOrganizationId()));

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user);
		_assertSearchUser(
			false, "usersOrgs", organization.getOrganizationId(), user);

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				0, _portal.getClassNameId(organization.getModelClassName()),
				organization.getPrimaryKey(), SystemEventConstants.TYPE_DELETE);

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			organization.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());
	}

	@Test
	public void testDeleteUserOrganization() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();
		User user = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			user.getUserId(), organization.getOrganizationId());

		_assertSearchUser(
			true, "usersOrgs", organization.getOrganizationId(), user);

		_organizationLocalService.deleteUserOrganization(
			user.getUserId(), organization.getOrganizationId());

		_assertSearchUser(
			false, "usersOrgs", organization.getOrganizationId(), user);

		_organizationLocalService.addUserOrganization(
			user.getUserId(), organization);

		_assertSearchUser(
			true, "usersOrgs", organization.getOrganizationId(), user);

		_organizationLocalService.deleteUserOrganization(
			user.getUserId(), organization);

		_assertSearchUser(
			false, "usersOrgs", organization.getOrganizationId(), user);
	}

	@Test
	public void testDeleteUserOrganizations() throws Exception {
		Organization organization = OrganizationTestUtil.addOrganization();
		User user = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganizations(
			user.getUserId(), new long[] {organization.getOrganizationId()});

		_assertSearchUser(
			true, "usersOrgs", organization.getOrganizationId(), user);

		_organizationLocalService.deleteUserOrganizations(
			user.getUserId(), new long[] {organization.getOrganizationId()});

		_assertSearchUser(
			false, "usersOrgs", organization.getOrganizationId(), user);

		_organizationLocalService.addUserOrganizations(
			user.getUserId(), Collections.singletonList(organization));

		_assertSearchUser(
			true, "usersOrgs", organization.getOrganizationId(), user);

		_organizationLocalService.deleteUserOrganizations(
			user.getUserId(), Collections.singletonList(organization));

		_assertSearchUser(
			false, "usersOrgs", organization.getOrganizationId(), user);
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

		_organizationLocalService.addOrganization(
			null, TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(),
			OrganizationConstants.TYPE_ORGANIZATION, 0, 0, listTypeId,
			StringPool.BLANK, false, new ServiceContext());

		Organization organization2 = _organizationLocalService.addOrganization(
			null, TestPropsValues.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID, "Test2",
			OrganizationConstants.TYPE_ORGANIZATION, 0, 0, listTypeId,
			StringPool.BLANK, false, new ServiceContext());

		AssetEntry assetEntry = _assetEntryLocalService.fetchEntry(
			Organization.class.getName(), organization2.getOrganizationId());

		Assert.assertNotNull(assetEntry);

		_assetEntryLocalService.deleteAssetEntry(assetEntry);

		List<Organization> organizations =
			_organizationLocalService.getNoAssetOrganizations();

		Assert.assertEquals(organizations.toString(), 1, organizations.size());
		Assert.assertEquals(organization2, organizations.get(0));
	}

	@Test
	public void testGetOrAddEmptyOrganization() throws Exception {

		// Lazy referencing disabled

		Assert.assertThrows(
			NoSuchOrganizationException.class,
			() -> _organizationLocalService.getOrAddEmptyOrganization(
				RandomTestUtil.randomString(), TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), RandomTestUtil.randomString()));

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
	public void testHasUserOrganization() throws Exception {
		_testHasUserOrganizationWithInheritSuborganizations();
		_testHasUserOrganizationWithoutInheritSuborganizations();
	}

	@Test
	public void testSearchOrganizations() throws Exception {
		_testSearchOrganizationsWithType();
		_testSearchOrganizationsWithUserName();
	}

	@Test
	public void testSearchOrganizationsAndUsers() throws Exception {
		_testSearchOrganizationsAndUsers();
		_testSearchOrganizationsAndUsersWithRootOrganization();
		_testSearchOrganizationsAndUsersWithoutOrganizations();
		_testSearchOrganizationsAndUsersWithoutUsers();
	}

	@Test
	public void testSetGroupOrganizations() throws Exception {
		Group group = GroupTestUtil.addGroup();
		Organization organization1 = OrganizationTestUtil.addOrganization();

		_organizationLocalService.setGroupOrganizations(
			group.getGroupId(), new long[] {organization1.getOrganizationId()});

		User user1 = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			user1.getUserId(), organization1.getOrganizationId());

		_assertSearchUser(
			true, "inheritUsersGroups", group.getGroupId(), user1);

		Organization organization2 = OrganizationTestUtil.addOrganization();
		User user2 = UserTestUtil.addUser();

		_organizationLocalService.addUserOrganization(
			user2.getUserId(), organization2.getOrganizationId());

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user2);

		_organizationLocalService.setGroupOrganizations(
			group.getGroupId(), new long[] {organization2.getOrganizationId()});

		_assertSearchUser(
			false, "inheritUsersGroups", group.getGroupId(), user1);
		_assertSearchUser(
			true, "inheritUsersGroups", group.getGroupId(), user2);
	}

	@Test
	public void testSetUserOrganizations() throws Exception {
		Organization organization1 = OrganizationTestUtil.addOrganization();
		User user = UserTestUtil.addUser();

		_organizationLocalService.setUserOrganizations(
			user.getUserId(), new long[] {organization1.getOrganizationId()});

		_assertSearchUser(
			true, "usersOrgs", organization1.getOrganizationId(), user);

		Organization organization2 = OrganizationTestUtil.addOrganization();

		_assertSearchUser(
			false, "usersOrgs", organization2.getOrganizationId(), user);

		_organizationLocalService.setUserOrganizations(
			user.getUserId(), new long[] {organization2.getOrganizationId()});

		_assertSearchUser(
			false, "usersOrgs", organization1.getOrganizationId(), user);
		_assertSearchUser(
			true, "usersOrgs", organization2.getOrganizationId(), user);
	}

	@Test
	public void testUpdateOrganization() throws Exception {
		_testUpdateOrganizationWithCircularParent();
		_testUpdateOrganizationWithFriendlyURLEndingWithDash();
		_testUpdateOrganizationWithLazyReferencingEnabled();
		_testUpdateOrganizationWithNewSiteAndChildWithSite();
		_testUpdateOrganizationWithNewSiteAndChildWithoutSite();
		_testUpdateOrganizationWithNewSiteAndParentWithSite();
		_testUpdateOrganizationWithNewSiteAndParentWithoutSite();
		_testUpdateOrganizationWithSiteToParentWithSite();
		_testUpdateOrganizationWithSiteToParentWithoutSite();
		_testUpdateOrganizationWithTreePath();
		_testUpdateOrganizationWithoutSiteToParentWithSite();
		_testUpdateOrganizationWithoutSiteToParentWithoutSite();
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	private void _assertSearchOrganizationsByType(
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

	private void _assertSearchUser(
			boolean expected, String paramKey, long paramValue, User user)
		throws Exception {

		Hits hits = _userLocalService.search(
			TestPropsValues.getCompanyId(), null, WorkflowConstants.STATUS_ANY,
			LinkedHashMapBuilder.<String, Object>put(
				paramKey, paramValue
			).build(),
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, (Sort[])null);

		List<Long> userIds = TransformUtil.transform(
			hits.toList(),
			document -> GetterUtil.getLong(document.get(Field.USER_ID)));

		Assert.assertEquals(
			userIds.toString(), expected, userIds.contains(user.getUserId()));
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

	private Hits _searchOrganizationsAndUsers(
			Organization parentOrganization, String keywords)
		throws Exception {

		return _organizationLocalService.searchOrganizationsAndUsers(
			parentOrganization.getCompanyId(),
			parentOrganization.getOrganizationId(), keywords,
			WorkflowConstants.STATUS_ANY, null, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);
	}

	private void _testAddOrganization() throws Exception {
		User user = TestPropsValues.getUser();

		Organization organization = _organizationLocalService.addOrganization(
			user.getUserId(),
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		List<Organization> organizations = user.getOrganizations(true);

		Assert.assertTrue(organizations.contains(organization));

		Assert.assertFalse(
			_organizationLocalService.hasUserOrganization(
				user.getUserId(), organization.getOrganizationId()));

		AssertUtils.assertFailure(
			DuplicateOrganizationException.class,
			"There is another organization named " + organization.getName(),
			() -> _organizationLocalService.addOrganization(
				user.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				organization.getName(), false));

		Assert.assertThrows(
			OrganizationNameException.MustNotBeNull.class,
			() -> _organizationLocalService.addOrganization(
				user.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID, null,
				false));

		int maxLength = ModelHintsUtil.getMaxLength(
			Organization.class.getName(), "name");

		Assert.assertThrows(
			OrganizationNameException.MustNotExceedMaximumLength.class,
			() -> _organizationLocalService.addOrganization(
				user.getUserId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
				RandomTestUtil.randomString(maxLength + 1), false));
	}

	private void _testAddOrganizationWithObjectValidationRule()
		throws Exception {

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

	private void _testAddOrganizationWithSiteToParentWithSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			true);

		Assert.assertEquals(
			organization1.getOrganizationId(),
			organization2.getParentOrganizationId());

		Group group2 = organization2.getGroup();

		Assert.assertEquals(
			organization1.getGroupId(), group2.getParentGroupId());
	}

	private void _testAddOrganizationWithSiteToParentWithoutSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			true);

		Assert.assertEquals(
			organization1.getOrganizationId(),
			organization2.getParentOrganizationId());

		Group group2 = organization2.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group2.getParentGroupId());
	}

	private void _testAddOrganizationWithoutSiteToParentWithSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		Assert.assertEquals(
			organization1.getOrganizationId(),
			organization2.getParentOrganizationId());

		Group group2 = organization2.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group2.getParentGroupId());
	}

	private void _testAddOrganizationWithoutSiteToParentWithoutSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		Assert.assertEquals(
			organization1.getOrganizationId(),
			organization2.getParentOrganizationId());

		Group group2 = organization2.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group2.getParentGroupId());
	}

	private void _testHasUserOrganizationWithInheritSuborganizations()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		_userLocalService.addOrganizationUser(
			organization2.getOrganizationId(), TestPropsValues.getUserId());

		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				TestPropsValues.getUserId(), organization1.getOrganizationId(),
				true, false));
		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				TestPropsValues.getUserId(), organization1.getOrganizationId(),
				true, true));

		_userLocalService.deleteOrganizationUser(
			organization2.getOrganizationId(), TestPropsValues.getUser());
	}

	private void _testHasUserOrganizationWithoutInheritSuborganizations()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		_userLocalService.addOrganizationUser(
			organization1.getOrganizationId(), TestPropsValues.getUserId());

		Assert.assertTrue(
			_organizationLocalService.hasUserOrganization(
				TestPropsValues.getUserId(), organization1.getOrganizationId(),
				false, false));
		Assert.assertFalse(
			_organizationLocalService.hasUserOrganization(
				TestPropsValues.getUserId(), organization2.getOrganizationId(),
				false, false));

		_userLocalService.deleteOrganizationUser(
			organization1.getOrganizationId(), TestPropsValues.getUser());
	}

	private void _testSearchOrganizationsAndUsers() throws Exception {
		Organization organization1 = OrganizationTestUtil.addOrganization();

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), "Organization1", false);

		User user = UserTestUtil.addUser("user1", TestPropsValues.getGroupId());

		_userLocalService.addOrganizationUsers(
			organization1.getOrganizationId(), new long[] {user.getUserId()});

		Hits hits = _searchOrganizationsAndUsers(organization1, null);

		Assert.assertEquals(hits.toString(), 2, hits.getLength());

		hits = _searchOrganizationsAndUsers(organization1, "Organization1");

		Document document = hits.doc(0);

		Assert.assertEquals(
			document.toString(),
			String.valueOf(organization2.getOrganizationId()),
			document.get(Field.ORGANIZATION_ID));

		hits = _searchOrganizationsAndUsers(organization1, "user1");

		document = hits.doc(0);

		Assert.assertEquals(
			document.toString(), String.valueOf(user.getUserId()),
			document.get(Field.USER_ID));

		_userLocalService.deleteOrganizationUser(
			organization1.getOrganizationId(), user);
	}

	private void _testSearchOrganizationsAndUsersWithRootOrganization()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();

		Hits hits = _searchOrganizationsAndUsers(organization, null);

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		hits = _searchOrganizationsAndUsers(organization, "Organization1");

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		hits = _searchOrganizationsAndUsers(organization, "user1");

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	private void _testSearchOrganizationsAndUsersWithoutOrganizations()
		throws Exception {

		Organization organization = OrganizationTestUtil.addOrganization();
		User user = UserTestUtil.addUser("user2", TestPropsValues.getGroupId());

		_userLocalService.addOrganizationUsers(
			organization.getOrganizationId(), new long[] {user.getUserId()});

		Hits hits = _searchOrganizationsAndUsers(organization, null);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());

		Document document = hits.doc(0);

		Assert.assertEquals(
			document.toString(), String.valueOf(user.getUserId()),
			document.get(Field.USER_ID));

		hits = _searchOrganizationsAndUsers(organization, "Organization1");

		Assert.assertEquals(hits.toString(), 0, hits.getLength());

		hits = _searchOrganizationsAndUsers(organization, "user2");

		document = hits.doc(0);

		Assert.assertEquals(
			document.toString(), String.valueOf(user.getUserId()),
			document.get(Field.USER_ID));

		_userLocalService.deleteOrganizationUser(
			organization.getOrganizationId(), user);
	}

	private void _testSearchOrganizationsAndUsersWithoutUsers()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization();

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), "Organization2", false);

		Hits hits = _searchOrganizationsAndUsers(organization1, null);

		Assert.assertEquals(hits.toString(), 1, hits.getLength());

		Document document = hits.doc(0);

		Assert.assertEquals(
			document.toString(),
			String.valueOf(organization2.getOrganizationId()),
			document.get(Field.ORGANIZATION_ID));

		hits = _searchOrganizationsAndUsers(organization1, "Organization2");

		document = hits.doc(0);

		Assert.assertEquals(
			document.toString(),
			String.valueOf(organization2.getOrganizationId()),
			document.get(Field.ORGANIZATION_ID));

		hits = _searchOrganizationsAndUsers(organization1, "user1");

		Assert.assertEquals(hits.toString(), 0, hits.getLength());
	}

	private void _testSearchOrganizationsWithType() throws Exception {
		OrganizationTestUtil.addOrganization();

		for (int i = 0; i < 5; i++) {
			String organizationType = RandomTestUtil.randomString();

			_pids.add(
				ConfigurationTestUtil.createFactoryConfiguration(
					"com.liferay.organizations.internal.configuration." +
						"OrganizationTypeConfiguration",
					HashMapDictionaryBuilder.<String, Object>put(
						"name", organizationType
					).build()));

			OrganizationTestUtil.addOrganization(organizationType);
			OrganizationTestUtil.addOrganization(organizationType);
		}

		List<Organization> expectedOrganizations = new ArrayList<>(
			_organizationLocalService.getOrganizations(
				TestPropsValues.getCompanyId(),
				OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID));

		_assertSearchOrganizationsByType(expectedOrganizations, "asc");
		_assertSearchOrganizationsByType(expectedOrganizations, "desc");
	}

	private void _testSearchOrganizationsWithUserName() throws Exception {
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

	private void _testUpdateOrganizationWithCircularParent() throws Exception {
		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		Organization organization3 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		Organization organization4 = OrganizationTestUtil.addOrganization(
			organization3.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		organization1.setParentOrganizationId(
			organization4.getOrganizationId());

		Assert.assertThrows(
			OrganizationParentException.class,
			() -> _updateOrganization(organization1.getName(), organization1));
	}

	private void _testUpdateOrganizationWithFriendlyURLEndingWithDash()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Group group = organization1.getGroup();

		String friendlyURL = group.getFriendlyURL();

		group.setFriendlyURL(friendlyURL + StringPool.DASH);

		group = _groupLocalService.updateGroup(group);

		String name = RandomTestUtil.randomString();

		organization1 = _updateOrganization(name, organization1);

		Assert.assertEquals(name, organization1.getName());

		group = _groupLocalService.getGroup(group.getGroupId());

		Assert.assertEquals(friendlyURL, group.getFriendlyURL());

		Organization organization2 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		group = organization2.getGroup();

		group.setFriendlyURL(friendlyURL + StringPool.DASH);

		_groupLocalService.updateGroup(group);

		GroupFriendlyURLException groupFriendlyURLException =
			Assert.assertThrows(
				GroupFriendlyURLException.class,
				() -> _updateOrganization(
					RandomTestUtil.randomString(), organization2));

		Assert.assertEquals(
			GroupFriendlyURLException.DUPLICATE,
			groupFriendlyURLException.getType());

		Organization organization3 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		group = organization3.getGroup();

		group.setFriendlyURL(group.getFriendlyURL() + StringPool.DASH);

		_groupLocalService.updateGroup(group);

		groupFriendlyURLException = Assert.assertThrows(
			GroupFriendlyURLException.class,
			() -> _updateOrganization(
				RandomTestUtil.randomString(), organization3));

		Assert.assertEquals(
			GroupFriendlyURLException.ENDS_WITH_DASH,
			groupFriendlyURLException.getType());
	}

	private void _testUpdateOrganizationWithLazyReferencingEnabled()
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

	private void _testUpdateOrganizationWithNewSiteAndChildWithSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			true);

		organization1 = OrganizationTestUtil.addSite(organization1);

		Group group1 = organization1.getGroup();

		Group group2 = organization2.getGroup();

		Assert.assertEquals(group1.getGroupId(), group2.getParentGroupId());
	}

	private void _testUpdateOrganizationWithNewSiteAndChildWithoutSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		OrganizationTestUtil.addSite(organization1);

		Group group2 = organization2.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group2.getParentGroupId());
	}

	private void _testUpdateOrganizationWithNewSiteAndParentWithSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		organization2 = OrganizationTestUtil.addSite(organization2);

		Group group1 = organization1.getGroup();
		Group group2 = organization2.getGroup();

		Assert.assertEquals(group1.getGroupId(), group2.getParentGroupId());
	}

	private void _testUpdateOrganizationWithNewSiteAndParentWithoutSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		organization2 = OrganizationTestUtil.addSite(organization2);

		Group group2 = organization2.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group2.getParentGroupId());
	}

	private void _testUpdateOrganizationWithSiteToParentWithSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			true);

		Organization organization3 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		organization2 = _organizationLocalService.updateOrganization(
			null, organization2.getCompanyId(),
			organization2.getOrganizationId(),
			organization3.getOrganizationId(), organization2.getName(),
			organization2.getType(), organization2.getRegionId(),
			organization2.getCountryId(), organization2.getStatusListTypeId(),
			organization2.getComments(), false, null, true, null);

		Assert.assertEquals(
			organization3.getOrganizationId(),
			organization2.getParentOrganizationId());

		Group group2 = organization2.getGroup();

		Assert.assertEquals(
			organization3.getGroupId(), group2.getParentGroupId());
	}

	private void _testUpdateOrganizationWithSiteToParentWithoutSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			true);

		Organization organization3 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		organization2 = _organizationLocalService.updateOrganization(
			null, organization2.getCompanyId(),
			organization2.getOrganizationId(),
			organization3.getOrganizationId(), organization2.getName(),
			organization2.getType(), organization2.getRegionId(),
			organization2.getCountryId(), organization2.getStatusListTypeId(),
			organization2.getComments(), false, null, true, null);

		Assert.assertEquals(
			organization3.getOrganizationId(),
			organization2.getParentOrganizationId());

		Group group2 = organization2.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group2.getParentGroupId());
	}

	private void _testUpdateOrganizationWithTreePath() throws Exception {
		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization3 = OrganizationTestUtil.addOrganization(
			organization2.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		Organization organization4 = OrganizationTestUtil.addOrganization(
			organization3.getOrganizationId(), RandomTestUtil.randomString(),
			false);

		String shallowTreePath = _getTreePath(
			new Organization[] {organization2, organization3, organization4});

		Assert.assertEquals(shallowTreePath, organization4.getTreePath());

		organization2.setParentOrganizationId(
			organization1.getOrganizationId());

		_updateOrganization(organization2.getName(), organization2);

		organization4 = _organizationLocalService.fetchOrganization(
			organization4.getOrganizationId());

		String deepTreePath = _getTreePath(
			new Organization[] {
				organization1, organization2, organization3, organization4
			});

		Assert.assertEquals(deepTreePath, organization4.getTreePath());

		organization2.setParentOrganizationId(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID);

		_updateOrganization(organization2.getName(), organization2);

		organization4 = _organizationLocalService.fetchOrganization(
			organization4.getOrganizationId());

		Assert.assertEquals(shallowTreePath, organization4.getTreePath());
	}

	private void _testUpdateOrganizationWithoutSiteToParentWithSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			true);

		Organization organization3 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), true);

		organization2 = _organizationLocalService.updateOrganization(
			null, organization2.getCompanyId(),
			organization2.getOrganizationId(),
			organization3.getOrganizationId(), organization2.getName(),
			organization2.getType(), organization2.getRegionId(),
			organization2.getCountryId(), organization2.getStatusListTypeId(),
			organization2.getComments(), false, null, true, null);

		Assert.assertEquals(
			organization3.getOrganizationId(),
			organization2.getParentOrganizationId());

		Group group2 = organization2.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group2.getParentGroupId());
	}

	private void _testUpdateOrganizationWithoutSiteToParentWithoutSite()
		throws Exception {

		Organization organization1 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		Organization organization2 = OrganizationTestUtil.addOrganization(
			organization1.getOrganizationId(), RandomTestUtil.randomString(),
			true);

		Organization organization3 = OrganizationTestUtil.addOrganization(
			OrganizationConstants.DEFAULT_PARENT_ORGANIZATION_ID,
			RandomTestUtil.randomString(), false);

		organization2 = _organizationLocalService.updateOrganization(
			null, organization2.getCompanyId(),
			organization2.getOrganizationId(),
			organization3.getOrganizationId(), organization2.getName(),
			organization2.getType(), organization2.getRegionId(),
			organization2.getCountryId(), organization2.getStatusListTypeId(),
			organization2.getComments(), false, null, true, null);

		Assert.assertEquals(
			organization3.getOrganizationId(),
			organization2.getParentOrganizationId());

		Group group2 = organization2.getGroup();

		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group2.getParentGroupId());
	}

	private Organization _updateOrganization(
			String name, Organization organization)
		throws Exception {

		Group organizationGroup = organization.getGroup();

		return _organizationLocalService.updateOrganization(
			null, organization.getCompanyId(), organization.getOrganizationId(),
			organization.getParentOrganizationId(), name,
			organization.getType(), organization.getRegionId(),
			organization.getCountryId(), organization.getStatusListTypeId(),
			organization.getComments(), false, null, organizationGroup.isSite(),
			null);
	}

	@Inject
	private AssetEntryLocalService _assetEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ListTypeLocalService _listTypeLocalService;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	@Inject
	private ObjectValidationRuleLocalService _objectValidationRuleLocalService;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	private PermissionChecker _originalPermissionChecker;
	private final List<String> _pids = new ArrayList<>();

	@Inject
	private Portal _portal;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	@Inject
	private UserLocalService _userLocalService;

}