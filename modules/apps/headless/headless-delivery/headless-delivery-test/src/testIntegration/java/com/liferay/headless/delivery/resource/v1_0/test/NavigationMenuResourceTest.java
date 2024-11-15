/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
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
import com.liferay.headless.delivery.client.dto.v1_0.CustomField;
import com.liferay.headless.delivery.client.dto.v1_0.CustomValue;
import com.liferay.headless.delivery.client.dto.v1_0.NavigationMenu;
import com.liferay.headless.delivery.client.dto.v1_0.NavigationMenuItem;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.NavigationMenuResource;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FriendlyURLNormalizer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LanguageIds;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
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
			null,
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
			"blog-postings/" + blogsEntry.getPrimaryKey(),
			BlogsEntry.class.getName(), blogsEntry.getTitle(), "blogPosting",
			true);

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
			DLFileEntry.class, "documents/" + fileEntry.getFileEntryId(),
			FileEntry.class.getName(), fileEntry.getTitle(), "document", true);

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
			DLFileEntry.class, "documents/" + fileEntry.getFileEntryId(),
			FileEntry.class.getName(), fileEntry.getTitle(), "document", true);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_depotEntry.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testGetNavigationMenu(
			journalArticle.getResourcePrimKey(),
			journalArticle.getDDMStructureId(), JournalArticle.class,
			"structured-contents/" + journalArticle.getResourcePrimKey(),
			JournalArticle.class.getName(), journalArticle.getTitle(),
			"structuredContent", false);

		journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testGetNavigationMenu(
			journalArticle.getResourcePrimKey(),
			journalArticle.getDDMStructureId(), JournalArticle.class,
			"structured-contents/" + journalArticle.getResourcePrimKey(),
			JournalArticle.class.getName(), journalArticle.getTitle(),
			"structuredContent", false);

		_testGetNavigationMenuWithChildNavigationMenusAndNavigationMenuItems();
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
			"blog-postings/" + blogsEntry.getPrimaryKey(),
			BlogsEntry.class.getName(), blogsEntry.getTitle(), "blogPosting",
			false);

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
			DLFileEntry.class, "documents/" + fileEntry.getFileEntryId(),
			FileEntry.class.getName(), fileEntry.getTitle(), "document", false);

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
			DLFileEntry.class, "documents/" + fileEntry.getFileEntryId(),
			FileEntry.class.getName(), fileEntry.getTitle(), "document", false);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_depotEntry.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testGetSiteNavigationMenusPage(
			journalArticle.getResourcePrimKey(),
			journalArticle.getDDMStructureId(), JournalArticle.class,
			"structured-contents/" + journalArticle.getResourcePrimKey(),
			JournalArticle.class.getName(), journalArticle.getTitle(),
			"structuredContent", true);

		journalArticle = JournalTestUtil.addArticle(
			testGroup.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		_testGetSiteNavigationMenusPage(
			journalArticle.getResourcePrimKey(),
			journalArticle.getDDMStructureId(), JournalArticle.class,
			"structured-contents/" + journalArticle.getResourcePrimKey(),
			JournalArticle.class.getName(), journalArticle.getTitle(),
			"structuredContent", true);
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

		if (StringUtil.contains(
				stackTraceElement.getMethodName(), "GraphQL",
				StringPool.BLANK)) {

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
			!Objects.equals(
				navigationMenuItem1.getUrl(), navigationMenuItem2.getUrl()) ||
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
								url = RandomTestUtil.randomString();
							}
						}
					};
					type = "navigationMenu";
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
					type = "navigationMenu";
					useCustomName = false;
				}
			},
			new NavigationMenuItem() {
				{
					link = layout1.getFriendlyURL(LocaleUtil.US);
					link_i18n = HashMapBuilder.put(
						"en-US", layout1.getFriendlyURL(LocaleUtil.US)
					).put(
						"es-ES", layout1.getFriendlyURL(LocaleUtil.SPAIN)
					).build();
					name_i18n = nameI18nMap1;
					type = "page";
					useCustomName = true;
				}
			},
			new NavigationMenuItem() {
				{
					link = layout1.getFriendlyURL(LocaleUtil.US);
					link_i18n = HashMapBuilder.put(
						"en-US", layout1.getFriendlyURL(LocaleUtil.US)
					).build();
					name_i18n = nameI18nMap2;
					type = "page";
					useCustomName = true;
				}
			},
			new NavigationMenuItem() {
				{
					link = layout1.getFriendlyURL(LocaleUtil.US);
					link_i18n = HashMapBuilder.put(
						"en-US", layout1.getFriendlyURL(LocaleUtil.US)
					).build();
					name_i18n = nameI18nMap1;
					type = "page";
					useCustomName = false;
				}
			},
			new NavigationMenuItem() {
				{
					link = layout2.getFriendlyURL(LocaleUtil.US);
					link_i18n = HashMapBuilder.put(
						"en-US", layout2.getFriendlyURL(LocaleUtil.US)
					).build();
					name_i18n = nameI18nMap1;
					type = "page";
					useCustomName = false;
				}
			}
		};
	}

	private void _testGetNavigationMenu(
			long classPK, long classTypeId, Class<?> clazz, String contentURL,
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

		Assert.assertTrue(
			navigationMenuItem.getContentURL(
			).contains(
				"/headless-delivery/v1.0/" + contentURL
			));
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
			getNavigationMenu.getNavigationMenuItems()[0], "navigationMenu",
			false);
		_assertNavigationMenuItem(
			nameI18nMap1.get(LocaleUtil.SPAIN.toLanguageTag()), nameI18nMap1,
			getNavigationMenu.getNavigationMenuItems()[1], "page", true);
		_assertNavigationMenuItem(
			nameI18nMap2.get(LocaleUtil.US.toLanguageTag()), nameI18nMap2,
			getNavigationMenu.getNavigationMenuItems()[2], "page", true);
		_assertNavigationMenuItem(
			layoutNameMap1.get(LocaleUtil.SPAIN),
			HashMapBuilder.put(
				LocaleUtil.US.toLanguageTag(), layoutNameMap1.get(LocaleUtil.US)
			).put(
				LocaleUtil.SPAIN.toLanguageTag(),
				layoutNameMap1.get(LocaleUtil.SPAIN)
			).build(),
			getNavigationMenu.getNavigationMenuItems()[3], "page", false);
		_assertNavigationMenuItem(
			layoutNameMap2.get(LocaleUtil.US),
			HashMapBuilder.put(
				LocaleUtil.US.toLanguageTag(), layoutNameMap2.get(LocaleUtil.US)
			).build(),
			getNavigationMenu.getNavigationMenuItems()[4], "page", false);
	}

	private void _testGetSiteNavigationMenusPage(
			long classPK, long classTypeId, Class<?> clazz, String contentURL,
			String displayPageType, String title, String type,
			Boolean useCustomName)
		throws Exception {

		_testGetSiteNavigationMenusPage(
			classPK, classTypeId, clazz, contentURL, displayPageType, title,
			type, useCustomName, _getServiceContext(false));
		_testGetSiteNavigationMenusPage(
			classPK, classTypeId, clazz, contentURL, displayPageType, title,
			type, useCustomName, _getServiceContext(true));
	}

	private void _testGetSiteNavigationMenusPage(
			long classPK, long classTypeId, Class<?> clazz, String contentURL,
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
				testGroup.getGroupId(), Pagination.of(1, 10));

		Assert.assertEquals(1, page.getTotalCount());
		assertValid(page);

		NavigationMenu getNavigationMenu = page.fetchFirstItem();

		Assert.assertEquals(
			postNavigationMenu.getName(), getNavigationMenu.getName());
		Assert.assertEquals(
			postNavigationMenu.getSiteId(), getNavigationMenu.getSiteId());

		NavigationMenuItem navigationMenuItem =
			getNavigationMenu.getNavigationMenuItems()[0];

		Assert.assertTrue(
			navigationMenuItem.getContentURL(
			).contains(
				"/headless-delivery/v1.0/" + contentURL
			));
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
	private ResourceActions _resourceActions;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

}