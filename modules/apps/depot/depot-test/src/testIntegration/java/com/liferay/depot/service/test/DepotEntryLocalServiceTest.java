/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.constants.DepotRolesConstants;
import com.liferay.depot.exception.DepotEntryNameException;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.DuplicateGroupException;
import com.liferay.portal.kernel.exception.LocaleException;
import com.liferay.portal.kernel.exception.NoSuchGroupException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserGroupTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class DepotEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Test
	public void testAddDepotEntry() throws Exception {
		DepotEntry depotEntry = _addDepotEntry("name", "description");

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		Assert.assertEquals(DepotEntry.class.getName(), group.getClassName());
		Assert.assertEquals(depotEntry.getDepotEntryId(), group.getClassPK());
		Assert.assertEquals(
			"description", group.getDescription(LocaleUtil.getDefault()));
		Assert.assertEquals("name", group.getName(LocaleUtil.getDefault()));
		Assert.assertEquals(
			GroupConstants.DEFAULT_PARENT_GROUP_ID, group.getParentGroupId());
		Assert.assertEquals(GroupConstants.TYPE_DEPOT, group.getType());
		Assert.assertFalse(group.isSite());
		Assert.assertTrue(
			_userGroupRoleLocalService.hasUserGroupRole(
				depotEntry.getUserId(), group.getGroupId(),
				DepotRolesConstants.ASSET_LIBRARY_OWNER, true));
	}

	@Test(expected = DuplicateGroupException.class)
	public void testAddDepotEntryDuplicateDepotName() throws Exception {
		String depotName = RandomTestUtil.randomString();

		_addDepotEntry(depotName, RandomTestUtil.randomString());

		_addDepotEntry(depotName, RandomTestUtil.randomString());
	}

	@Test(expected = DuplicateGroupException.class)
	public void testAddDepotEntryDuplicateGroupName() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_addDepotEntry(
			group.getName(LocaleUtil.getDefault()),
			RandomTestUtil.randomString());
	}

	@Test
	@TestInfo("LPD-92654")
	public void testAddDepotEntryWithGroupExternalReferenceCode()
		throws Exception {

		String externalReferenceCode = RandomTestUtil.randomString();

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setAttribute(
			"groupExternalReferenceCode", externalReferenceCode);

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY, serviceContext);

		_depotEntries.add(depotEntry);

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		Assert.assertEquals(
			externalReferenceCode, group.getExternalReferenceCode());
	}

	@Test(expected = DepotEntryNameException.class)
	public void testAddDepotEntryWithNullName() throws Exception {
		_addDepotEntry((String)null, (String)null);
	}

	@Test
	public void testDeleteCompany() throws Exception {
		Company company = CompanyTestUtil.addCompany();

		User user = UserTestUtil.addUser(company);

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "name"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "description"
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY, _getServiceContext(user));

		Assert.assertEquals(company.getCompanyId(), depotEntry.getCompanyId());

		_companyLocalService.deleteCompany(company);

		Assert.assertNull(
			_depotEntryLocalService.fetchDepotEntry(
				depotEntry.getDepotEntryId()));
	}

	@Test(expected = NoSuchGroupException.class)
	public void testDeleteDepotEntry() throws Exception {
		DepotEntry depotEntry = _addDepotEntry("name", "description");

		_depotEntryLocalService.deleteDepotEntry(depotEntry.getDepotEntryId());

		_depotEntries.remove(depotEntry);

		_groupLocalService.getGroup(depotEntry.getGroupId());
	}

	@Test
	public void testGetDepotEntryGroupIds() throws Exception {
		List<Long> depotEntryGroupIds =
			_depotEntryLocalService.getDepotEntryGroupIds(
				TestPropsValues.getCompanyId(),
				DepotConstants.TYPE_ASSET_LIBRARY);

		int assetLibrariesCount = depotEntryGroupIds.size();

		depotEntryGroupIds = _depotEntryLocalService.getDepotEntryGroupIds(
			TestPropsValues.getCompanyId(), DepotConstants.TYPE_SPACE);

		int spacesCount = depotEntryGroupIds.size();

		depotEntryGroupIds = _depotEntryLocalService.getDepotEntryGroupIds(
			TestPropsValues.getCompanyId(), DepotConstants.TYPE_ANY);

		Assert.assertEquals(
			depotEntryGroupIds.toString(), assetLibrariesCount + spacesCount,
			depotEntryGroupIds.size());

		_addDepotEntry(DepotConstants.TYPE_ASSET_LIBRARY);
		_addDepotEntry(DepotConstants.TYPE_SPACE);

		depotEntryGroupIds = _depotEntryLocalService.getDepotEntryGroupIds(
			TestPropsValues.getCompanyId(), DepotConstants.TYPE_ASSET_LIBRARY);

		Assert.assertEquals(
			depotEntryGroupIds.toString(), assetLibrariesCount + 1,
			depotEntryGroupIds.size());

		depotEntryGroupIds = _depotEntryLocalService.getDepotEntryGroupIds(
			TestPropsValues.getCompanyId(), DepotConstants.TYPE_SPACE);

		Assert.assertEquals(
			depotEntryGroupIds.toString(), spacesCount + 1,
			depotEntryGroupIds.size());

		depotEntryGroupIds = _depotEntryLocalService.getDepotEntryGroupIds(
			TestPropsValues.getCompanyId(), DepotConstants.TYPE_ANY);

		Assert.assertEquals(
			depotEntryGroupIds.toString(),
			assetLibrariesCount + spacesCount + 2, depotEntryGroupIds.size());
	}

	@Test
	public void testGetDepotEntryGroupIdsByUser() throws Exception {
		Organization irrelevantOrganization =
			OrganizationTestUtil.addOrganization();

		User irrelevantUser = UserTestUtil.addUser();
		UserGroup irrelevantUserGroup = UserGroupTestUtil.addUserGroup();

		_userGroupLocalService.addUserUserGroup(
			irrelevantUser.getUserId(), irrelevantUserGroup);

		_addDepotEntry(null, null, (UserGroup)null);
		_addDepotEntry(null, null, irrelevantUserGroup);
		_addDepotEntry(null, irrelevantUser, null);
		_addDepotEntry(null, irrelevantUser, irrelevantUserGroup);
		_addDepotEntry(irrelevantOrganization, null, null);
		_addDepotEntry(irrelevantOrganization, null, irrelevantUserGroup);
		_addDepotEntry(irrelevantOrganization, irrelevantUser, null);
		_addDepotEntry(
			irrelevantOrganization, irrelevantUser, irrelevantUserGroup);

		Organization organization = OrganizationTestUtil.addOrganization();
		User user = UserTestUtil.addUser();
		UserGroup userGroup = UserGroupTestUtil.addUserGroup();

		_organizationLocalService.addUserOrganization(
			user.getUserId(), organization);
		_userGroupLocalService.addUserUserGroup(user.getUserId(), userGroup);

		DepotEntry depotEntry1 = _addDepotEntry(null, null, userGroup);
		DepotEntry depotEntry2 = _addDepotEntry(
			null, irrelevantUser, userGroup);
		DepotEntry depotEntry3 = _addDepotEntry(null, user, null);
		DepotEntry depotEntry4 = _addDepotEntry(
			null, user, irrelevantUserGroup);
		DepotEntry depotEntry5 = _addDepotEntry(null, user, userGroup);
		DepotEntry depotEntry6 = _addDepotEntry(
			irrelevantOrganization, null, userGroup);
		DepotEntry depotEntry7 = _addDepotEntry(
			irrelevantOrganization, user, null);
		DepotEntry depotEntry8 = _addDepotEntry(organization, null, null);
		DepotEntry depotEntry9 = _addDepotEntry(
			organization, null, irrelevantUserGroup);
		DepotEntry depotEntry10 = _addDepotEntry(
			organization, irrelevantUser, null);
		DepotEntry depotEntry11 = _addDepotEntry(
			organization, irrelevantUser, irrelevantUserGroup);

		_testGetDepotEntryGroupIdsByUser(
			List.of(
				depotEntry1, depotEntry2, depotEntry3, depotEntry4, depotEntry5,
				depotEntry6, depotEntry7, depotEntry8, depotEntry9,
				depotEntry10, depotEntry11),
			user, false);
		_testGetDepotEntryGroupIdsByUser(
			List.of(
				depotEntry1, depotEntry2, depotEntry5, depotEntry6, depotEntry8,
				depotEntry9, depotEntry10, depotEntry11),
			user, true);
	}

	@Test
	public void testUpdateDepotEntry() throws Exception {
		DepotEntry depotEntry = _addDepotEntry("name", "description");

		_depotEntryLocalService.updateDepotEntry(
			depotEntry.getDepotEntryId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "newName"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "newDescription"
			).build(),
			Collections.emptyMap(),
			UnicodePropertiesBuilder.put(
				PropsKeys.LOCALES,
				LocaleUtil.toLanguageId(LocaleUtil.getDefault())
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		Assert.assertEquals(
			"newDescription", group.getDescription(LocaleUtil.getDefault()));
		Assert.assertEquals("newName", group.getName(LocaleUtil.getDefault()));
	}

	@Test
	public void testUpdateDepotEntryDeleteDefaultLocale() throws Exception {
		DepotEntry depotEntry = _addDepotEntry("name", "description");

		Set<Locale> availableLocales = new HashSet<>();

		availableLocales.add(LocaleUtil.getDefault());
		availableLocales.add(LocaleUtil.fromLanguageId("es_ES"));

		_depotEntryLocalService.updateDepotEntry(
			depotEntry.getDepotEntryId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "newName"
			).put(
				LocaleUtil.fromLanguageId("es_ES"), "nuevoNombre"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "newDescription"
			).put(
				LocaleUtil.fromLanguageId("es_ES"), "nuevaDescripcion"
			).build(),
			Collections.emptyMap(),
			UnicodePropertiesBuilder.put(
				PropsKeys.LOCALES,
				StringUtil.merge(LocaleUtil.toLanguageIds(availableLocales))
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		Assert.assertEquals(
			"nuevaDescripcion",
			group.getDescription(LocaleUtil.fromLanguageId("es_ES")));
		Assert.assertEquals(
			"nuevoNombre", group.getName(LocaleUtil.fromLanguageId("es_ES")));
	}

	@Test
	public void testUpdateDepotEntryInheritLocale() throws Exception {
		DepotEntry depotEntry = _addDepotEntry("name", "description");

		_depotEntryLocalService.updateDepotEntry(
			depotEntry.getDepotEntryId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "newName"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "newDescription"
			).build(),
			Collections.emptyMap(),
			UnicodePropertiesBuilder.put(
				"inheritLocales", "true"
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		UnicodeProperties typeSettingsUnicodeProperties =
			group.getTypeSettingsProperties();

		Assert.assertTrue(
			GetterUtil.getBoolean(
				typeSettingsUnicodeProperties.getProperty("inheritLocales")));
		Assert.assertEquals(
			StringUtil.merge(
				LocaleUtil.toLanguageIds(_language.getAvailableLocales())),
			typeSettingsUnicodeProperties.getProperty("locales"));
	}

	@Test
	public void testUpdateDepotEntryNoDescription() throws Exception {
		DepotEntry depotEntry = _addDepotEntry("name", "description");

		_depotEntryLocalService.updateDepotEntry(
			depotEntry.getDepotEntryId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "newName"
			).build(),
			Collections.emptyMap(), Collections.emptyMap(),
			UnicodePropertiesBuilder.put(
				PropsKeys.LOCALES,
				LocaleUtil.toLanguageId(LocaleUtil.getDefault())
			).build(),
			ServiceContextTestUtil.getServiceContext());

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		Assert.assertEquals("newName", group.getName(LocaleUtil.getDefault()));
		Assert.assertEquals(
			StringPool.BLANK, group.getDescription(LocaleUtil.getDefault()));
	}

	@Test
	public void testUpdateDepotEntryNoName() throws Exception {
		DepotEntry depotEntry = _addDepotEntry("name", "description");

		_depotEntryLocalService.updateDepotEntry(
			depotEntry.getDepotEntryId(), new HashMap<>(),
			Collections.emptyMap(), Collections.emptyMap(),
			new UnicodeProperties(),
			ServiceContextTestUtil.getServiceContext());

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		Assert.assertEquals(
			"Unnamed Asset Library", group.getName(LocaleUtil.getDefault()));
	}

	@Test
	public void testUpdateDepotEntryPreservesGroupKey() throws Exception {
		DepotEntry depotEntry = _addDepotEntry("name", "description");

		Group group = _groupLocalService.getGroup(depotEntry.getGroupId());

		String oldGroupKey = group.getGroupKey();

		_depotEntryLocalService.updateDepotEntry(
			depotEntry.getDepotEntryId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "name"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "description"
			).build(),
			Collections.emptyMap(),
			UnicodePropertiesBuilder.put(
				PropsKeys.LOCALES,
				LocaleUtil.toLanguageId(LocaleUtil.getDefault())
			).build(),
			ServiceContextTestUtil.getServiceContext());

		group = _groupLocalService.getGroup(depotEntry.getGroupId());

		Assert.assertEquals(oldGroupKey, group.getGroupKey());
	}

	@Test(expected = LocaleException.class)
	public void testUpdateDepotEntryRequiresValidTypeSettingProperties()
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry("name", "description");

		_depotEntryLocalService.updateDepotEntry(
			depotEntry.getDepotEntryId(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "newName"
			).put(
				LocaleUtil.fromLanguageId("es_ES"), "nuevoNombre"
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), "newDescription"
			).put(
				LocaleUtil.fromLanguageId("es_ES"), "descripcion"
			).build(),
			Collections.emptyMap(),
			UnicodePropertiesBuilder.put(
				"inheritLocales", "false"
			).build(),
			ServiceContextTestUtil.getServiceContext());
	}

	private DepotEntry _addDepotEntry(int type) throws Exception {
		return _addDepotEntry(
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), type);
	}

	private DepotEntry _addDepotEntry(
			Organization organization, User user, UserGroup userGroup)
		throws Exception {

		DepotEntry depotEntry = _addDepotEntry(
			_DEPOT_TYPES[_random.nextInt(_DEPOT_TYPES.length)]);

		if (organization != null) {
			_organizationLocalService.addGroupOrganization(
				depotEntry.getGroupId(), organization.getOrganizationId());
		}

		if (user != null) {
			_groupLocalService.addUserGroup(
				user.getUserId(), depotEntry.getGroupId());
		}

		if (userGroup != null) {
			_userGroupLocalService.addGroupUserGroup(
				depotEntry.getGroupId(), userGroup);
		}

		return depotEntry;
	}

	private DepotEntry _addDepotEntry(String name, String description)
		throws Exception {

		return _addDepotEntry(
			name, description, DepotConstants.TYPE_ASSET_LIBRARY);
	}

	private DepotEntry _addDepotEntry(String name, String description, int type)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), description
			).build(),
			type, ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		return depotEntry;
	}

	private ServiceContext _getServiceContext(User user) throws Exception {
		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext();

		serviceContext.setCompanyId(user.getCompanyId());
		serviceContext.setUserId(user.getUserId());

		return serviceContext;
	}

	private void _testGetDepotEntryGroupIdsByUser(
		List<DepotEntry> depotEntries, User user,
		boolean dynamicInheritanceOnly) {

		List<Long> assetLibraryGroupIds = new ArrayList<>();
		List<Long> depotEntryGroupIds = new ArrayList<>();
		List<Long> spaceGroupIds = new ArrayList<>();

		for (DepotEntry depotEntry : depotEntries) {
			depotEntryGroupIds.add(depotEntry.getGroupId());

			if (depotEntry.getType() == DepotConstants.TYPE_ASSET_LIBRARY) {
				assetLibraryGroupIds.add(depotEntry.getGroupId());
			}
			else {
				spaceGroupIds.add(depotEntry.getGroupId());
			}
		}

		AssertUtils.assertEquals(
			depotEntryGroupIds,
			_depotEntryLocalService.getDepotEntryGroupIds(
				user.getCompanyId(), user.getUserId(), DepotConstants.TYPE_ANY,
				dynamicInheritanceOnly));
		AssertUtils.assertEquals(
			assetLibraryGroupIds,
			_depotEntryLocalService.getDepotEntryGroupIds(
				user.getCompanyId(), user.getUserId(),
				DepotConstants.TYPE_ASSET_LIBRARY, dynamicInheritanceOnly));
		AssertUtils.assertEquals(
			spaceGroupIds,
			_depotEntryLocalService.getDepotEntryGroupIds(
				user.getCompanyId(), user.getUserId(),
				DepotConstants.TYPE_SPACE, dynamicInheritanceOnly));
	}

	private static final int[] _DEPOT_TYPES = {
		DepotConstants.TYPE_ASSET_LIBRARY, DepotConstants.TYPE_SPACE
	};

	private static final Random _random = new Random();

	@Inject
	private CompanyLocalService _companyLocalService;

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private Language _language;

	@Inject
	private OrganizationLocalService _organizationLocalService;

	@Inject
	private UserGroupLocalService _userGroupLocalService;

	@Inject
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}