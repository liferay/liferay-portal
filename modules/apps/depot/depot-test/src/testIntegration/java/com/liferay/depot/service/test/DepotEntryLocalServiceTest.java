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
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
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

	@Test(expected = DepotEntryNameException.class)
	public void testAddDepotEntryWithNullName() throws Exception {
		_addDepotEntry(null, null);
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

	private DepotEntry _addDepotEntry(String name, String description)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			HashMapBuilder.put(
				LocaleUtil.getDefault(), name
			).build(),
			HashMapBuilder.put(
				LocaleUtil.getDefault(), description
			).build(),
			DepotConstants.TYPE_ASSET_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

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
	private UserGroupRoleLocalService _userGroupRoleLocalService;

}