/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.model.DLFileEntryTypeConstants;
import com.liferay.document.library.kernel.model.DLFolderConstants;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.model.ExpandoColumnConstants;
import com.liferay.expando.kernel.model.ExpandoTable;
import com.liferay.expando.kernel.model.ExpandoTableConstants;
import com.liferay.expando.kernel.service.ExpandoColumnLocalService;
import com.liferay.expando.kernel.service.ExpandoTableLocalService;
import com.liferay.headless.delivery.client.custom.field.CustomField;
import com.liferay.headless.delivery.client.custom.field.CustomValue;
import com.liferay.headless.delivery.client.dto.v1_0.NavigationMenu;
import com.liferay.headless.delivery.client.dto.v1_0.NavigationMenuItem;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.NavigationMenuResource;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.permission.PermissionUtil;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Gamarra
 */
@LanguageIds(
	availableLanguageIds = {"en_US", "es_ES"}, defaultLanguageId = "en_US"
)
@RunWith(Arquillian.class)
public class NavigationMenuResourceTest
	extends BaseNavigationMenuResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		BaseNavigationMenuResourceTestCase.setUpClass();

		_originalExpandoTable = _expandoTableLocalService.fetchDefaultTable(
			TestPropsValues.getCompanyId(),
			SiteNavigationMenuItem.class.getName());

		ExpandoTable expandoTable = _originalExpandoTable;

		if (expandoTable == null) {
			expandoTable = _expandoTableLocalService.addDefaultTable(
				TestPropsValues.getCompanyId(),
				SiteNavigationMenuItem.class.getName());
		}

		ExpandoColumn expandoColumn1 = _expandoColumnLocalService.addColumn(
			expandoTable.getTableId(), RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING, StringPool.BLANK);

		_expandoColumnNames.add(expandoColumn1.getName());

		ExpandoColumn expandoColumn2 = _expandoColumnLocalService.addColumn(
			expandoTable.getTableId(), RandomTestUtil.randomString(),
			ExpandoColumnConstants.STRING, StringPool.BLANK);

		_expandoColumnNames.add(expandoColumn2.getName());
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		if (_originalExpandoTable == null) {
			_expandoTableLocalService.deleteTable(
				TestPropsValues.getCompanyId(),
				SiteNavigationMenuItem.class.getName(),
				ExpandoTableConstants.DEFAULT_TABLE_NAME);

			return;
		}

		for (String expandoColumnName : _expandoColumnNames) {
			_expandoColumnLocalService.deleteColumn(
				_originalExpandoTable.getTableId(), expandoColumnName);
		}
	}

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_depotEntry = _depotEntryLocalService.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testGroup.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			_depotEntry.getDepotEntryId(), testGroup.getGroupId());
	}

	@Override
	@Test
	public void testGetNavigationMenu() throws Exception {
		super.testGetNavigationMenu();

		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(), new Date(),
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));

		_testGetNavigationMenu(
			blogsEntry.getPrimaryKey(), 0, BlogsEntry.class,
			BlogsEntry.class.getName(), blogsEntry.getTitle(),
			BlogsEntry.class.getName(), true);

		FileEntry fileEntry = DLAppTestUtil.addFileEntryWithWorkflow(
			TestPropsValues.getUserId(), _depotEntry.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(
				_depotEntry.getGroupId(), TestPropsValues.getUserId()));

		_testGetNavigationMenu(
			fileEntry.getPrimaryKey(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
			DLFileEntry.class, FileEntry.class.getName(), fileEntry.getTitle(),
			FileEntry.class.getName(), true);

		fileEntry = DLAppTestUtil.addFileEntryWithWorkflow(
			TestPropsValues.getUserId(), testGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));

		_testGetNavigationMenu(
			fileEntry.getPrimaryKey(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
			DLFileEntry.class, FileEntry.class.getName(), fileEntry.getTitle(),
			FileEntry.class.getName(), true);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_depotEntry.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testGetNavigationMenu(
			journalArticle.getResourcePrimKey(),
			journalArticle.getDDMStructureId(), JournalArticle.class,
			JournalArticle.class.getName(), journalArticle.getTitle(),
			JournalArticle.class.getName(), false);

		journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testGetNavigationMenu(
			journalArticle.getResourcePrimKey(),
			journalArticle.getDDMStructureId(), JournalArticle.class,
			JournalArticle.class.getName(), journalArticle.getTitle(),
			JournalArticle.class.getName(), false);

		_testGetNavigationMenuWithChildNavigationMenusAndNavigationMenuItems();
		_testGetNavigationMenuWithNestedFields();
		_testGetNavigationMenuWithoutNestedFields();
	}

	@Override
	@Test
	public void testGetSiteNavigationMenusPage() throws Exception {
		super.testGetSiteNavigationMenusPage();

		BlogsEntry blogsEntry = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), StringUtil.randomString(),
			StringUtil.randomString(), new Date(),
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));

		_testGetSiteNavigationMenusPage(
			blogsEntry.getPrimaryKey(), 0, BlogsEntry.class,
			BlogsEntry.class.getName(), blogsEntry.getTitle(),
			BlogsEntry.class.getName(), false);

		FileEntry fileEntry = DLAppTestUtil.addFileEntryWithWorkflow(
			TestPropsValues.getUserId(), _depotEntry.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(
				_depotEntry.getGroupId(), TestPropsValues.getUserId()));

		_testGetSiteNavigationMenusPage(
			fileEntry.getPrimaryKey(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
			DLFileEntry.class, FileEntry.class.getName(), fileEntry.getTitle(),
			FileEntry.class.getName(), false);

		fileEntry = DLAppTestUtil.addFileEntryWithWorkflow(
			TestPropsValues.getUserId(), testGroup.getGroupId(),
			DLFolderConstants.DEFAULT_PARENT_FOLDER_ID,
			RandomTestUtil.randomString() + ".txt",
			RandomTestUtil.randomString(), true,
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId()));

		_testGetSiteNavigationMenusPage(
			fileEntry.getPrimaryKey(),
			DLFileEntryTypeConstants.FILE_ENTRY_TYPE_ID_BASIC_DOCUMENT,
			DLFileEntry.class, FileEntry.class.getName(), fileEntry.getTitle(),
			FileEntry.class.getName(), false);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_depotEntry.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testGetSiteNavigationMenusPage(
			journalArticle.getResourcePrimKey(),
			journalArticle.getDDMStructureId(), JournalArticle.class,
			JournalArticle.class.getName(), journalArticle.getTitle(),
			JournalArticle.class.getName(), true);

		journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testGetSiteNavigationMenusPage(
			journalArticle.getResourcePrimKey(),
			journalArticle.getDDMStructureId(), JournalArticle.class,
			JournalArticle.class.getName(), journalArticle.getTitle(),
			JournalArticle.class.getName(), true);

		_testGetSiteNavigationMenusPageWithSearch();
	}

	@Override
	@Test
	public void testPostSiteNavigationMenu() throws Exception {
		super.testPostSiteNavigationMenu();

		_testPostSiteNavigationMenuWithNavigationType();
		_testPostSiteNavigationMenuWithPermissions();
	}

	@Override
	@Test
	public void testPutNavigationMenu() throws Exception {
		super.testPutNavigationMenu();

		_testPutSiteNavigationMenuWithPermissions();
	}

	@Override
	protected boolean equals(
		NavigationMenu navigationMenu1, NavigationMenu navigationMenu2) {

		if (navigationMenu1 == navigationMenu2) {
			return true;
		}

		if (!Objects.equals(
				navigationMenu1.getSiteId(), navigationMenu2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(additionalAssertFieldName, "name")) {
				if (!Objects.equals(
						navigationMenu1.getName(), navigationMenu2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					additionalAssertFieldName, "navigationMenuItems")) {

				if (!_equals(
						navigationMenu1.getNavigationMenuItems(),
						navigationMenu2.getNavigationMenuItems())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		Thread currentThread = Thread.currentThread();

		StackTraceElement[] stackTraceElements = currentThread.getStackTrace();

		StackTraceElement stackTraceElement = stackTraceElements[3];

		String methodName = stackTraceElement.getMethodName();

		if (methodName.contains("GraphQL")) {
			return new String[] {"name"};
		}

		return new String[] {"name", "navigationMenuItems"};
	}

	@Override
	protected NavigationMenu randomNavigationMenu() throws Exception {
		return _randomNavigationMenu(true);
	}

	@Override
	protected NavigationMenu testGetNavigationMenu_addNavigationMenu()
		throws Exception {

		return navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), _randomNavigationMenu(false));
	}

	private void _assertNavigationMenuItem(
		String name, Map<String, String> nameI18nMap,
		NavigationMenuItem navigationMenuItem, String type,
		boolean useCustomName) {

		Assert.assertEquals(name, navigationMenuItem.getName());
		Assert.assertEquals(nameI18nMap, navigationMenuItem.getName_i18n());
		Assert.assertEquals(type, navigationMenuItem.getType());
		Assert.assertEquals(
			useCustomName, navigationMenuItem.getUseCustomName());
	}

	private NavigationMenuResource _buildNavigationMenuResource(Locale locale) {
		NavigationMenuResource.Builder builder =
			NavigationMenuResource.builder();

		return builder.authentication(
			"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
		).header(
			"X-Accept-All-Languages", "true"
		).locale(
			(locale == null) ? LocaleUtil.getDefault() : locale
		).build();
	}

	private boolean _equals(
		NavigationMenuItem navigationMenuItem1,
		NavigationMenuItem navigationMenuItem2) {

		if (navigationMenuItem1 == navigationMenuItem2) {
			return true;
		}

		if (!Objects.equals(
				navigationMenuItem1.getName(), navigationMenuItem2.getName()) ||
			!_equals(
				navigationMenuItem1.getNavigationMenuItems(),
				navigationMenuItem2.getNavigationMenuItems()) ||
			!Objects.equals(
				navigationMenuItem1.getType(), navigationMenuItem2.getType()) ||
			!_equalsCustomFieldsIgnoringOrder(
				navigationMenuItem1.getCustomFields(),
				navigationMenuItem2.getCustomFields())) {

			return false;
		}

		return true;
	}

	private boolean _equals(
		NavigationMenuItem[] navigationMenuItems1,
		NavigationMenuItem[] navigationMenuItems2) {

		if (navigationMenuItems1 == navigationMenuItems2) {
			return true;
		}

		if (navigationMenuItems1.length != navigationMenuItems2.length) {
			return false;
		}

		for (int i = 0; i < navigationMenuItems1.length; i++) {
			NavigationMenuItem navigationMenuItem1 = navigationMenuItems1[i];
			NavigationMenuItem navigationMenuItem2 = navigationMenuItems2[i];

			if (!_equals(navigationMenuItem1, navigationMenuItem2)) {
				return false;
			}
		}

		return true;
	}

	private boolean _equalsCustomFieldsIgnoringOrder(
		CustomField[] customFields1, CustomField[] customFields2) {

		if ((ArrayUtil.isEmpty(customFields1) &&
			 ArrayUtil.isEmpty(customFields2)) ||
			ArrayUtil.containsAll(customFields1, customFields2)) {

			return true;
		}

		return false;
	}

	private CustomField[] _getExpectedCustomFields(
		ServiceContext serviceContext) {

		Map<String, Serializable> expandoBridgeAttributes =
			serviceContext.getExpandoBridgeAttributes();

		if (MapUtil.isEmpty(expandoBridgeAttributes)) {
			return TransformUtil.transformToArray(
				_expandoColumnNames,
				expandoColumnName -> new CustomField() {
					{
						customValue = new CustomValue() {
							{
								data = StringPool.BLANK;
							}
						};
						dataType = "Text";
						name = expandoColumnName;
					}
				},
				CustomField.class);
		}

		return TransformUtil.transformToArray(
			expandoBridgeAttributes.entrySet(),
			entry -> new CustomField() {
				{
					customValue = new CustomValue() {
						{
							data = entry.getValue();
						}
					};
					dataType = "Text";
					name = entry.getKey();
				}
			},
			CustomField.class);
	}

	private ServiceContext _getServiceContext(boolean expandoBridgeAttributes)
		throws Exception {

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testGroup.getGroupId(), TestPropsValues.getUserId());

		if (expandoBridgeAttributes) {
			serviceContext.setExpandoBridgeAttributes(
				HashMapBuilder.<String, Serializable>put(
					_expandoColumnNames.get(0), RandomTestUtil.randomString()
				).put(
					_expandoColumnNames.get(1), RandomTestUtil.randomString()
				).build());
		}

		return serviceContext;
	}

	private Map<String, String> _getTypeSettings(
		Layout layout, Map<String, String> nameI18nMap, String type,
		String useCustomName) {

		HashMapBuilder.HashMapWrapper<String, String> hashMapBuilder =
			HashMapBuilder.put(
				"defaultLanguageId",
				LocaleUtil.toLanguageId(LocaleUtil.getDefault()));

		for (Map.Entry<String, String> entry : nameI18nMap.entrySet()) {
			hashMapBuilder.put(
				"name_" + LocaleUtil.fromLanguageId(entry.getKey()),
				nameI18nMap.get(entry.getKey()));
		}

		if (type.equals("layout")) {
			return hashMapBuilder.put(
				"groupId", GetterUtil.getString(layout.getGroupId())
			).put(
				"layoutUuid", layout.getUuid()
			).put(
				"privateLayout", GetterUtil.getString(layout.isPrivateLayout())
			).put(
				"title", layout.getTitle()
			).put(
				"useCustomName", useCustomName
			).build();
		}

		return hashMapBuilder.put(
			"useCustomName", useCustomName
		).build();
	}

	private NavigationMenu _randomNavigationMenu(
			boolean includeNavigationMenuItem)
		throws Exception {

		NavigationMenu navigationMenu = super.randomNavigationMenu();

		navigationMenu.setNavigationMenuItems(
			() -> {
				if (!includeNavigationMenuItem) {
					return new NavigationMenuItem[0];
				}

				return _randomNavigationMenuItems();
			});

		return navigationMenu;
	}

	private NavigationMenu _randomNavigationMenu(
			Layout layout1, Layout layout2, Map<String, String> nameI18nMap1,
			Map<String, String> nameI18nMap2)
		throws Exception {

		NavigationMenu navigationMenu = super.randomNavigationMenu();

		navigationMenu.setNavigationMenuItems(
			_randomNavigationMenuItems(
				layout1, layout2, nameI18nMap1, nameI18nMap2));

		return navigationMenu;
	}

	private NavigationMenuItem[] _randomNavigationMenuItems() {
		return new NavigationMenuItem[] {
			new NavigationMenuItem() {
				{
					customFields = new CustomField[] {
						new CustomField() {
							{
								customValue = new CustomValue() {
									{
										data = RandomTestUtil.randomString();
									}
								};
								dataType = "Text";
								name = _expandoColumnNames.get(0);
							}
						},
						new CustomField() {
							{
								customValue = new CustomValue() {
									{
										data = RandomTestUtil.randomString();
									}
								};
								dataType = "Text";
								name = _expandoColumnNames.get(1);
							}
						}
					};
					name = RandomTestUtil.randomString();
					navigationMenuItems = new NavigationMenuItem[] {
						new NavigationMenuItem() {
							{
								customFields = new CustomField[] {
									new CustomField() {
										{
											customValue = new CustomValue() {
												{
													data =
														RandomTestUtil.
															randomString();
												}
											};
											dataType = "Text";
											name = _expandoColumnNames.get(0);
										}
									},
									new CustomField() {
										{
											customValue = new CustomValue() {
												{
													data =
														RandomTestUtil.
															randomString();
												}
											};
											dataType = "Text";
											name = _expandoColumnNames.get(1);
										}
									}
								};
								name = RandomTestUtil.randomString();
								navigationMenuItems = new NavigationMenuItem[0];
								type = "url";
								typeSettings = HashMapBuilder.put(
									"name_en_US", name
								).put(
									"url", "https://www.google.com"
								).put(
									"useNewTab", "false"
								).build();
							}
						}
					};
					type = "node";
					typeSettings = HashMapBuilder.put(
						"defaultLanguageId", "en_US"
					).put(
						"name_en_US", name
					).build();
				}
			}
		};
	}

	private NavigationMenuItem[] _randomNavigationMenuItems(
		Layout layout1, Layout layout2, Map<String, String> nameI18nMap1,
		Map<String, String> nameI18nMap2) {

		return new NavigationMenuItem[] {
			new NavigationMenuItem() {
				{
					name_i18n = nameI18nMap1;
					type = "node";
					typeSettings = _getTypeSettings(
						layout1, nameI18nMap1, "node", "false");
					useCustomName = false;
				}
			},
			new NavigationMenuItem() {
				{
					name_i18n = nameI18nMap1;
					type = "layout";
					typeSettings = _getTypeSettings(
						layout1, nameI18nMap1, "layout", "true");
					useCustomName = true;
				}
			},
			new NavigationMenuItem() {
				{
					name_i18n = nameI18nMap2;
					type = "layout";
					typeSettings = _getTypeSettings(
						layout1, nameI18nMap2, "layout", "true");
					useCustomName = true;
				}
			},
			new NavigationMenuItem() {
				{
					name_i18n = nameI18nMap1;
					type = "layout";
					typeSettings = _getTypeSettings(
						layout1, nameI18nMap1, "layout", "false");
					useCustomName = false;
				}
			},
			new NavigationMenuItem() {
				{
					name_i18n = nameI18nMap1;
					type = "layout";
					typeSettings = _getTypeSettings(
						layout2, nameI18nMap1, "layout", "false");
					useCustomName = false;
				}
			}
		};
	}

	private void _testGetNavigationMenu(
			long classPK, long classTypeId, Class<?> clazz,
			String displayPageType, String title, String type,
			Boolean useCustomName)
		throws Exception {

		NavigationMenu postNavigationMenu =
			testGetNavigationMenu_addNavigationMenu();

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
				null, TestPropsValues.getUserId(), testGroup.getGroupId(),
				postNavigationMenu.getId(), 0, displayPageType,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", clazz.getName()
				).put(
					"classNameId", String.valueOf(_portal.getClassNameId(clazz))
				).put(
					"classPK", String.valueOf(classPK)
				).put(
					"classTypeId", String.valueOf(classTypeId)
				).put(
					"title", String.valueOf(title)
				).put(
					"type",
					_resourceActions.getModelResource(
						LocaleUtil.getDefault(), clazz.getName())
				).put(
					"useCustomName",
					() -> {
						if (useCustomName) {
							return "true";
						}

						return null;
					}
				).buildString(),
				ServiceContextTestUtil.getServiceContext(
					testGroup.getGroupId(), TestPropsValues.getUserId()));

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getNavigationMenu(
				postNavigationMenu.getId());

		assertValid(getNavigationMenu);

		NavigationMenuItem navigationMenuItem =
			getNavigationMenu.getNavigationMenuItems()[0];

		Assert.assertEquals(
			siteNavigationMenuItem.getSiteNavigationMenuItemId(),
			GetterUtil.getLong(navigationMenuItem.getId()));
		Assert.assertEquals(type, navigationMenuItem.getType());

		if (useCustomName) {
			Assert.assertTrue(navigationMenuItem.getUseCustomName());
		}
		else {
			Assert.assertEquals(title, navigationMenuItem.getName());
			Assert.assertFalse(navigationMenuItem.getUseCustomName());
		}
	}

	private void _testGetNavigationMenuWithChildNavigationMenusAndNavigationMenuItems()
		throws Exception {

		Map<Locale, String> layoutNameMap1 = HashMapBuilder.put(
			LocaleUtil.SPAIN, RandomTestUtil.randomString()
		).put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		Layout layout1 = LayoutTestUtil.addTypePortletLayout(
			testGroup.getGroupId(), false, layoutNameMap1,
			HashMapBuilder.put(
				LocaleUtil.US,
				_friendlyURLNormalizer.normalizeWithEncoding(
					StringPool.SLASH + RandomTestUtil.randomString())
			).build());

		Map<Locale, String> layoutNameMap2 = HashMapBuilder.put(
			LocaleUtil.US, RandomTestUtil.randomString()
		).build();

		Layout layout2 = LayoutTestUtil.addTypePortletLayout(
			testGroup.getGroupId(), false, layoutNameMap2,
			HashMapBuilder.put(
				LocaleUtil.US,
				_friendlyURLNormalizer.normalizeWithEncoding(
					StringPool.SLASH + RandomTestUtil.randomString())
			).build());

		Map<String, String> nameI18nMap1 = HashMapBuilder.put(
			LocaleUtil.SPAIN.toLanguageTag(), RandomTestUtil.randomString()
		).put(
			LocaleUtil.US.toLanguageTag(), RandomTestUtil.randomString()
		).build();
		Map<String, String> nameI18nMap2 = HashMapBuilder.put(
			LocaleUtil.US.toLanguageTag(), RandomTestUtil.randomString()
		).build();

		NavigationMenu postNavigationMenu =
			navigationMenuResource.postSiteNavigationMenu(
				testGroup.getGroupId(),
				_randomNavigationMenu(
					layout1, layout2, nameI18nMap1, nameI18nMap2));

		NavigationMenuResource navigationMenuResource =
			_buildNavigationMenuResource(LocaleUtil.SPAIN);

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getNavigationMenu(
				postNavigationMenu.getId());

		_assertNavigationMenuItem(
			nameI18nMap1.get(LocaleUtil.SPAIN.toLanguageTag()), nameI18nMap1,
			getNavigationMenu.getNavigationMenuItems()[0], "node", false);
		_assertNavigationMenuItem(
			nameI18nMap1.get(LocaleUtil.SPAIN.toLanguageTag()), nameI18nMap1,
			getNavigationMenu.getNavigationMenuItems()[1], "layout", true);
		_assertNavigationMenuItem(
			nameI18nMap2.get(LocaleUtil.US.toLanguageTag()), nameI18nMap2,
			getNavigationMenu.getNavigationMenuItems()[2], "layout", true);
		_assertNavigationMenuItem(
			layoutNameMap1.get(LocaleUtil.SPAIN),
			HashMapBuilder.put(
				LocaleUtil.US.toLanguageTag(), layoutNameMap1.get(LocaleUtil.US)
			).put(
				LocaleUtil.SPAIN.toLanguageTag(),
				layoutNameMap1.get(LocaleUtil.SPAIN)
			).build(),
			getNavigationMenu.getNavigationMenuItems()[3], "layout", false);
		_assertNavigationMenuItem(
			layoutNameMap2.get(LocaleUtil.US),
			HashMapBuilder.put(
				LocaleUtil.US.toLanguageTag(), layoutNameMap2.get(LocaleUtil.US)
			).build(),
			getNavigationMenu.getNavigationMenuItems()[4], "layout", false);
	}

	private void _testGetNavigationMenuWithNestedFields() throws Exception {
		NavigationMenu postNavigationMenu =
			testGetNavigationMenu_addNavigationMenu();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), SiteNavigationMenu.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(postNavigationMenu.getId()), role.getRoleId(),
			new String[] {ActionKeys.DELETE});

		NavigationMenuResource navigationMenuResource =
			NavigationMenuResource.builder(
			).authentication(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).parameters(
				"nestedFields", "permissions"
			).build();

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getNavigationMenu(
				postNavigationMenu.getId());

		Assert.assertTrue(
			ArrayUtil.exists(
				getNavigationMenu.getPermissions(),
				permission ->
					Objects.equals(permission.getRoleName(), role.getName()) &&
					(permission.getActionIds().length == 1) &&
					Objects.equals(permission.getActionIds()[0], "DELETE")));
	}

	private void _testGetNavigationMenuWithoutNestedFields() throws Exception {
		NavigationMenu postNavigationMenu =
			testGetNavigationMenu_addNavigationMenu();

		Role role = RoleTestUtil.addRole(RoleConstants.TYPE_REGULAR);

		_resourcePermissionLocalService.setResourcePermissions(
			TestPropsValues.getCompanyId(), SiteNavigationMenu.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			String.valueOf(postNavigationMenu.getId()), role.getRoleId(),
			new String[] {ActionKeys.DELETE});

		NavigationMenuResource navigationMenuResource =
			NavigationMenuResource.builder(
			).authentication(
				"test@liferay.com", PropsValues.DEFAULT_ADMIN_PASSWORD
			).locale(
				LocaleUtil.getDefault()
			).build();

		NavigationMenu getNavigationMenu =
			navigationMenuResource.getNavigationMenu(
				postNavigationMenu.getId());

		Assert.assertNull(getNavigationMenu.getPermissions());
	}

	private void _testGetSiteNavigationMenusPage(
			long classPK, long classTypeId, Class<?> clazz,
			String displayPageType, String title, String type,
			Boolean useCustomName)
		throws Exception {

		_testGetSiteNavigationMenusPage(
			classPK, classTypeId, clazz, displayPageType, title, type,
			useCustomName, _getServiceContext(false));
		_testGetSiteNavigationMenusPage(
			classPK, classTypeId, clazz, displayPageType, title, type,
			useCustomName, _getServiceContext(true));
	}

	private void _testGetSiteNavigationMenusPage(
			long classPK, long classTypeId, Class<?> clazz,
			String displayPageType, String title, String type,
			Boolean useCustomName, ServiceContext serviceContext)
		throws Exception {

		NavigationMenu postNavigationMenu =
			testGetNavigationMenu_addNavigationMenu();

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
				null, TestPropsValues.getUserId(), testGroup.getGroupId(),
				postNavigationMenu.getId(), 0, displayPageType,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", clazz.getName()
				).put(
					"classNameId", String.valueOf(_portal.getClassNameId(clazz))
				).put(
					"classPK", String.valueOf(classPK)
				).put(
					"classTypeId", String.valueOf(classTypeId)
				).put(
					"title", String.valueOf(title)
				).put(
					"type",
					_resourceActions.getModelResource(
						LocaleUtil.getDefault(), clazz.getName())
				).put(
					"useCustomName",
					() -> {
						if (useCustomName) {
							return "true";
						}

						return null;
					}
				).buildString(),
				serviceContext);

		Page<NavigationMenu> page =
			navigationMenuResource.getSiteNavigationMenusPage(
				testGroup.getGroupId(), null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());
		assertValid(page);

		NavigationMenu getNavigationMenu = page.fetchFirstItem();

		Assert.assertEquals(
			postNavigationMenu.getName(), getNavigationMenu.getName());
		Assert.assertEquals(
			postNavigationMenu.getSiteId(), getNavigationMenu.getSiteId());

		NavigationMenuItem navigationMenuItem =
			getNavigationMenu.getNavigationMenuItems()[0];

		Assert.assertEquals(
			siteNavigationMenuItem.getSiteNavigationMenuItemId(),
			GetterUtil.getLong(navigationMenuItem.getId()));
		Assert.assertEquals(type, navigationMenuItem.getType());

		if (useCustomName) {
			Assert.assertTrue(navigationMenuItem.getUseCustomName());
		}
		else {
			Assert.assertEquals(title, navigationMenuItem.getName());
			Assert.assertFalse(navigationMenuItem.getUseCustomName());
		}

		CustomField[] customFields = ArrayUtil.filter(
			navigationMenuItem.getCustomFields(),
			customField ->
				Objects.equals(
					customField.getName(), _expandoColumnNames.get(0)) ||
				Objects.equals(
					customField.getName(), _expandoColumnNames.get(1)));

		Assert.assertTrue(
			_equalsCustomFieldsIgnoringOrder(
				customFields, _getExpectedCustomFields(serviceContext)));

		navigationMenuResource.deleteNavigationMenu(postNavigationMenu.getId());
	}

	private void _testGetSiteNavigationMenusPageWithSearch() throws Exception {
		NavigationMenu randomNavigationMenu = randomNavigationMenu();

		NavigationMenu postNavigationMenu =
			testPostSiteNavigationMenu_addNavigationMenu(randomNavigationMenu);

		Page<NavigationMenu> page =
			navigationMenuResource.getSiteNavigationMenusPage(
				postNavigationMenu.getSiteId(), postNavigationMenu.getName(),
				null, Pagination.of(1, 10), null);

		Assert.assertEquals(1, page.getTotalCount());

		page = navigationMenuResource.getSiteNavigationMenusPage(
			postNavigationMenu.getSiteId(), RandomTestUtil.randomString(), null,
			Pagination.of(1, 10), null);

		Assert.assertEquals(0, page.getTotalCount());
	}

	private void _testPostSiteNavigationMenuWithNavigationType()
		throws Exception {

		NavigationMenu navigationMenu = _randomNavigationMenu(false);

		navigationMenu.setNavigationType(NavigationMenu.NavigationType.PRIMARY);

		navigationMenu = navigationMenuResource.postSiteNavigationMenu(
			testGroup.getGroupId(), navigationMenu);

		Assert.assertEquals(
			NavigationMenu.NavigationType.PRIMARY,
			navigationMenu.getNavigationType());
	}

	private void _testPostSiteNavigationMenuWithPermissions() throws Exception {
		NavigationMenu randomNavigationMenu = randomNavigationMenu();

		Role serviceBuilderRole = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		Permission permission1 = new Permission() {
			{
				actionIds = new String[] {ActionKeys.VIEW};
				roleExternalReferenceCode =
					serviceBuilderRole.getExternalReferenceCode();
				roleName = serviceBuilderRole.getName();
				roleType = RoleConstants.getTypeLabel(
					serviceBuilderRole.getType());
			}
		};

		randomNavigationMenu.setPermissions(new Permission[] {permission1});

		NavigationMenu postNavigationMenu =
			testPostSiteNavigationMenu_addNavigationMenu(randomNavigationMenu);

		List<com.liferay.portal.vulcan.permission.Permission> permissions =
			ListUtil.fromCollection(
				PermissionUtil.getPermissions(
					TestPropsValues.getCompanyId(),
					_resourceActionLocalService.getResourceActions(
						SiteNavigationMenu.class.getName()),
					postNavigationMenu.getId(),
					SiteNavigationMenu.class.getName(), null));

		Assert.assertTrue(
			ListUtil.exists(
				permissions,
				permission -> {
					String[] actionIds = permission.getActionIds();

					return (actionIds.length == 1) &&
						   Objects.equals(ActionKeys.VIEW, actionIds[0]) &&
						   Objects.equals(
							   serviceBuilderRole.getExternalReferenceCode(),
							   permission.getRoleExternalReferenceCode());
				}));
	}

	private void _testPutSiteNavigationMenuWithPermissions() throws Exception {
		NavigationMenu postNavigationMenu =
			testPutNavigationMenu_addNavigationMenu();

		NavigationMenu randomNavigationMenu = randomNavigationMenu();

		Role serviceBuilderRole = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		Permission permission1 = new Permission() {
			{
				actionIds = new String[] {ActionKeys.VIEW};
				roleExternalReferenceCode =
					serviceBuilderRole.getExternalReferenceCode();
				roleName = serviceBuilderRole.getName();
				roleType = RoleConstants.getTypeLabel(
					serviceBuilderRole.getType());
			}
		};

		randomNavigationMenu.setPermissions(new Permission[] {permission1});

		NavigationMenu putNavigationMenu =
			navigationMenuResource.putNavigationMenu(
				postNavigationMenu.getId(), randomNavigationMenu);

		List<com.liferay.portal.vulcan.permission.Permission> permissions =
			ListUtil.fromCollection(
				PermissionUtil.getPermissions(
					TestPropsValues.getCompanyId(),
					_resourceActionLocalService.getResourceActions(
						SiteNavigationMenu.class.getName()),
					putNavigationMenu.getId(),
					SiteNavigationMenu.class.getName(), null));

		Assert.assertTrue(
			ListUtil.exists(
				permissions,
				permission -> {
					String[] actionIds = permission.getActionIds();

					return (actionIds.length == 1) &&
						   Objects.equals(ActionKeys.VIEW, actionIds[0]) &&
						   Objects.equals(
							   serviceBuilderRole.getExternalReferenceCode(),
							   permission.getRoleExternalReferenceCode());
				}));
	}

	@Inject
	private static ExpandoColumnLocalService _expandoColumnLocalService;

	private static final List<String> _expandoColumnNames = new ArrayList<>();

	@Inject
	private static ExpandoTableLocalService _expandoTableLocalService;

	private static ExpandoTable _originalExpandoTable;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	private DepotEntry _depotEntry;

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject
	private FriendlyURLNormalizer _friendlyURLNormalizer;

	@Inject
	private Portal _portal;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourceActions _resourceActions;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

}