/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.internal.upgrade.v5_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.blogs.model.BlogsEntry;
import com.liferay.blogs.service.BlogsEntryLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.knowledge.base.model.KBArticle;
import com.liferay.knowledge.base.test.util.KBTestUtil;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.object.constants.ObjectDefinitionConstants;
import com.liferay.object.field.builder.TextObjectFieldBuilder;
import com.liferay.object.field.util.ObjectFieldUtil;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectEntry;
import com.liferay.object.model.ObjectField;
import com.liferay.object.rest.test.util.ObjectEntryTestUtil;
import com.liferay.object.service.ObjectDefinitionLocalService;
import com.liferay.portal.kernel.dao.orm.EntityCacheUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.site.navigation.menu.item.layout.constants.SiteNavigationMenuItemTypeConstants;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.test.util.SiteNavigationMenuItemTestUtil;
import com.liferay.site.navigation.test.util.SiteNavigationMenuTestUtil;

import java.io.Serializable;

import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Gislayne Vitorino
 */
@RunWith(Arquillian.class)
public class SiteNavigationMenuItemUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group1 = GroupTestUtil.addGroup();
		_group2 = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group1.getGroupId());
		_siteNavigationMenu = SiteNavigationMenuTestUtil.addSiteNavigationMenu(
			_group1);
	}

	@Test
	public void testUpgradeWithAssetCategoryAndAssetVocabulary()
		throws Exception {

		AssetVocabulary assetVocabulary1 =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group1.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		SiteNavigationMenuItem assetVocabularySiteNavigationMenuItem1 =
			_addSiteNavigationMenuItem(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"classPK", assetVocabulary1.getVocabularyId()
				).put(
					"externalReferenceCode",
					assetVocabulary1.getExternalReferenceCode()
				).put(
					"groupId", assetVocabulary1.getGroupId()
				).put(
					"type", SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY
				).buildString());

		AssetCategory assetCategory1 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group1.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary1.getVocabularyId(),
			_serviceContext);

		SiteNavigationMenuItem assetCategorySiteNavigationMenuItem1 =
			_addSiteNavigationMenuItem(
				AssetCategory.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", AssetCategory.class.getName()
				).put(
					"classPK", assetCategory1.getCategoryId()
				).put(
					"externalReferenceCode",
					assetCategory1.getExternalReferenceCode()
				).buildString());

		AssetVocabulary assetVocabulary2 =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group2.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		SiteNavigationMenuItem assetVocabularySiteNavigationMenuItem2 =
			_addSiteNavigationMenuItem(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"classPK", assetVocabulary2.getVocabularyId()
				).put(
					"externalReferenceCode",
					assetVocabulary2.getExternalReferenceCode()
				).put(
					"groupId", assetVocabulary2.getGroupId()
				).put(
					"type", SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY
				).buildString());

		AssetCategory assetCategory2 = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group2.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary2.getVocabularyId(),
			_serviceContext);

		SiteNavigationMenuItem assetCategorySiteNavigationMenuItem2 =
			_addSiteNavigationMenuItem(
				AssetCategory.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", AssetCategory.class.getName()
				).put(
					"classPK", assetCategory2.getCategoryId()
				).put(
					"externalReferenceCode",
					assetCategory2.getExternalReferenceCode()
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromDifferentGroup(
			assetCategorySiteNavigationMenuItem2.getSiteNavigationMenuItemId());
		_assertNavigationMenuItemFromDifferentGroup(
			assetVocabularySiteNavigationMenuItem2.
				getSiteNavigationMenuItemId());
		_assertNavigationMenuItemFromSameGroup(
			assetCategorySiteNavigationMenuItem1.getSiteNavigationMenuItemId());
		_assertNavigationMenuItemFromSameGroup(
			assetVocabularySiteNavigationMenuItem1.
				getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithBlogsEntry() throws Exception {
		BlogsEntry blogsEntry1 = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), _serviceContext);

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			_addSiteNavigationMenuItem(
				BlogsEntry.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", BlogsEntry.class.getName()
				).put(
					"classPK", blogsEntry1.getEntryId()
				).put(
					"externalReferenceCode", blogsEntry1.getEntryId()
				).buildString());

		_serviceContext.setScopeGroupId(_group2.getGroupId());

		BlogsEntry blogsEntry2 = _blogsEntryLocalService.addEntry(
			TestPropsValues.getUserId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), new Date(), _serviceContext);

		SiteNavigationMenuItem siteNavigationMenuItem2 =
			_addSiteNavigationMenuItem(
				BlogsEntry.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", BlogsEntry.class.getName()
				).put(
					"classPK", blogsEntry2.getEntryId()
				).put(
					"externalReferenceCode", blogsEntry2.getEntryId()
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromDifferentGroup(
			siteNavigationMenuItem2.getSiteNavigationMenuItemId());
		_assertNavigationMenuItemFromSameGroup(
			siteNavigationMenuItem1.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithCPDefinition() throws Exception {
		CPDefinition cpDefinition1 = CPTestUtil.addCPDefinition(
			_group1.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			_addSiteNavigationMenuItem(
				CPDefinition.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", CPDefinition.class.getName()
				).put(
					"classPK", cpDefinition1.getCPDefinitionId()
				).put(
					"externalReferenceCode",
					() -> {
						CProduct cProduct = cpDefinition1.getCProduct();

						return cProduct.getExternalReferenceCode();
					}
				).put(
					"type", CPDefinition.class.getName()
				).buildString());

		CPDefinition cpDefinition2 = CPTestUtil.addCPDefinition(
			_group2.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem2 =
			_addSiteNavigationMenuItem(
				CPDefinition.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", CPDefinition.class.getName()
				).put(
					"classPK", cpDefinition2.getCPDefinitionId()
				).put(
					"externalReferenceCode",
					() -> {
						CProduct cProduct = cpDefinition2.getCProduct();

						return cProduct.getExternalReferenceCode();
					}
				).put(
					"type", CPDefinition.class.getName()
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromDifferentGroup(
			siteNavigationMenuItem2.getSiteNavigationMenuItemId());
		_assertNavigationMenuItemFromSameGroup(
			siteNavigationMenuItem1.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithCompanyScopedObjectEntry() throws Exception {
		ObjectDefinition companyScopedObjectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, true, false, true,
				false, true, false, false, false, false,
				RandomTestUtil.randomString(),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"TestObject", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_COMPANY,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), null, Collections.emptyList(),
				new ServiceContext());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myText"
			).objectDefinitionId(
				companyScopedObjectDefinition.getObjectDefinitionId()
			).build());

		companyScopedObjectDefinition.setTitleObjectFieldId(
			objectField.getObjectFieldId());

		companyScopedObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				companyScopedObjectDefinition);

		companyScopedObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				companyScopedObjectDefinition.getObjectDefinitionId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			companyScopedObjectDefinition, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				companyScopedObjectDefinition.getClassName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", companyScopedObjectDefinition.getClassName()
				).put(
					"classPK", objectEntry.getObjectEntryId()
				).put(
					"externalReferenceCode",
					objectEntry.getExternalReferenceCode()
				).buildString());

		_runUpgrade();

		siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
				siteNavigationMenuItem.getSiteNavigationMenuItemId());

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		Assert.assertNull(
			typeSettingsUnicodeProperties.get("scopeExternalReferenceCode"));
	}

	@Test
	public void testUpgradeWithFileEntry() throws Exception {
		FileEntry fileEntry1 = DLAppTestUtil.addFileEntry(_group1.getGroupId());
		FileEntry fileEntry2 = DLAppTestUtil.addFileEntry(_group2.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			_addSiteNavigationMenuItem(
				FileEntry.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", FileEntry.class.getName()
				).put(
					"classPK", fileEntry1.getFileEntryId()
				).put(
					"externalReferenceCode",
					fileEntry1.getExternalReferenceCode()
				).put(
					"type", FileEntry.class.getName()
				).buildString());

		SiteNavigationMenuItem siteNavigationMenuItem2 =
			_addSiteNavigationMenuItem(
				FileEntry.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", FileEntry.class.getName()
				).put(
					"classPK", fileEntry2.getFileEntryId()
				).put(
					"externalReferenceCode",
					fileEntry2.getExternalReferenceCode()
				).put(
					"type", FileEntry.class.getName()
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromDifferentGroup(
			siteNavigationMenuItem2.getSiteNavigationMenuItemId());
		_assertNavigationMenuItemFromSameGroup(
			siteNavigationMenuItem1.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithJournalArticle() throws Exception {
		JournalArticle journalArticle1 = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group1.getGroupId(), 0);

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			_addSiteNavigationMenuItem(
				JournalArticle.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", JournalArticle.class.getName()
				).put(
					"classPK", journalArticle1.getResourcePrimKey()
				).put(
					"externalReferenceCode",
					journalArticle1.getExternalReferenceCode()
				).put(
					"type", JournalArticle.class.getName()
				).buildString());

		JournalArticle journalArticle2 = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group2.getGroupId(), 0);

		SiteNavigationMenuItem siteNavigationMenuItem2 =
			_addSiteNavigationMenuItem(
				JournalArticle.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", JournalArticle.class.getName()
				).put(
					"classPK", journalArticle2.getResourcePrimKey()
				).put(
					"externalReferenceCode",
					journalArticle2.getExternalReferenceCode()
				).put(
					"type", JournalArticle.class.getName()
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromSameGroup(
			siteNavigationMenuItem1.getSiteNavigationMenuItemId());
		_assertNavigationMenuItemFromDifferentGroup(
			siteNavigationMenuItem2.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithKBArticle() throws Exception {
		KBArticle kbArticle1 = KBTestUtil.addKBArticle(_group1.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			_addSiteNavigationMenuItem(
				KBArticle.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", KBArticle.class.getName()
				).put(
					"classPK", kbArticle1.getResourcePrimKey()
				).put(
					"externalReferenceCode",
					kbArticle1.getExternalReferenceCode()
				).put(
					"type", KBArticle.class.getName()
				).buildString());

		KBArticle kbArticle2 = KBTestUtil.addKBArticle(_group2.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem2 =
			_addSiteNavigationMenuItem(
				KBArticle.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", KBArticle.class.getName()
				).put(
					"classPK", kbArticle2.getResourcePrimKey()
				).put(
					"externalReferenceCode",
					kbArticle2.getExternalReferenceCode()
				).put(
					"type", KBArticle.class.getName()
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromDifferentGroup(
			siteNavigationMenuItem2.getSiteNavigationMenuItemId());
		_assertNavigationMenuItemFromSameGroup(
			siteNavigationMenuItem1.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group1);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				SiteNavigationMenuItemTypeConstants.LAYOUT,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"externalReferenceCode", layout.getExternalReferenceCode()
				).put(
					"groupId", layout.getGroupId()
				).put(
					"privateLayout", false
				).put(
					"type", SiteNavigationMenuItemTypeConstants.LAYOUT
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromSameGroup(
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithNode() throws Exception {
		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				SiteNavigationMenuItemTypeConstants.NODE,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"defaultLanguageId",
					String.valueOf(_serviceContext.getLocale())
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromSameGroup(
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithNonexistentJournalArticle() throws Exception {
		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				JournalArticle.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", JournalArticle.class.getName()
				).put(
					"classPK", RandomTestUtil.randomLong()
				).put(
					"externalReferenceCode", RandomTestUtil.randomString()
				).put(
					"type", JournalArticle.class.getName()
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromSameGroup(
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithSiteScopedObjectEntry() throws Exception {
		ObjectDefinition siteScopedObjectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, true, false, true,
				false, true, false, false, false, false,
				RandomTestUtil.randomString(),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"Test", null, null,
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), null, Collections.emptyList(),
				new ServiceContext());

		ObjectField objectField = ObjectFieldUtil.addCustomObjectField(
			new TextObjectFieldBuilder(
			).userId(
				TestPropsValues.getUserId()
			).indexed(
				true
			).indexedAsKeyword(
				true
			).labelMap(
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString())
			).name(
				"myText"
			).objectDefinitionId(
				siteScopedObjectDefinition.getObjectDefinitionId()
			).build());

		siteScopedObjectDefinition.setTitleObjectFieldId(
			objectField.getObjectFieldId());

		siteScopedObjectDefinition =
			_objectDefinitionLocalService.updateObjectDefinition(
				siteScopedObjectDefinition);

		siteScopedObjectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				siteScopedObjectDefinition.getObjectDefinitionId());

		ObjectEntry objectEntry1 = ObjectEntryTestUtil.addObjectEntry(
			_group1.getGroupId(), siteScopedObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build());
		ObjectEntry objectEntry2 = ObjectEntryTestUtil.addObjectEntry(
			_group2.getGroupId(), siteScopedObjectDefinition,
			HashMapBuilder.<String, Serializable>put(
				"title_i18n",
				HashMapBuilder.put(
					"en_US", RandomTestUtil.randomString()
				).build()
			).build());

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			_addSiteNavigationMenuItem(
				siteScopedObjectDefinition.getClassName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", siteScopedObjectDefinition.getClassName()
				).put(
					"classPK", objectEntry1.getObjectEntryId()
				).put(
					"externalReferenceCode",
					objectEntry1.getExternalReferenceCode()
				).buildString());
		SiteNavigationMenuItem siteNavigationMenuItem2 =
			_addSiteNavigationMenuItem(
				siteScopedObjectDefinition.getClassName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", siteScopedObjectDefinition.getClassName()
				).put(
					"classPK", objectEntry2.getObjectEntryId()
				).put(
					"externalReferenceCode",
					objectEntry2.getExternalReferenceCode()
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromDifferentGroup(
			siteNavigationMenuItem2.getSiteNavigationMenuItemId());
		_assertNavigationMenuItemFromSameGroup(
			siteNavigationMenuItem1.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithURL() throws Exception {
		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				SiteNavigationMenuItemTypeConstants.URL,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"defaultLanguageId",
					String.valueOf(_serviceContext.getLocale())
				).buildString());

		_runUpgrade();

		_assertNavigationMenuItemFromSameGroup(
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	private SiteNavigationMenuItem _addSiteNavigationMenuItem(
			String siteNavigationMenuItemType, String typeSettings)
		throws Exception {

		SiteNavigationMenuItem siteNavigationMenuItem =
			SiteNavigationMenuItemTestUtil.addSiteNavigationMenuItem(
				_siteNavigationMenu, siteNavigationMenuItemType, typeSettings);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				typeSettings
			).build();

		Assert.assertNull(
			typeSettingsUnicodeProperties.get("scopeExternalReferenceCode"));

		return siteNavigationMenuItem;
	}

	private void _assertNavigationMenuItemFromDifferentGroup(
			long siteNavigationMenuItemId)
		throws Exception {

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
				siteNavigationMenuItemId);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		Assert.assertEquals(
			_group2.getExternalReferenceCode(),
			typeSettingsUnicodeProperties.get("scopeExternalReferenceCode"));
		Assert.assertNull(typeSettingsUnicodeProperties.get("groupId"));
	}

	private void _assertNavigationMenuItemFromSameGroup(
			long siteNavigationMenuItemId)
		throws Exception {

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
				siteNavigationMenuItemId);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		Assert.assertNull(typeSettingsUnicodeProperties.get("groupId"));
		Assert.assertNull(
			typeSettingsUnicodeProperties.get("scopeExternalReferenceCode"));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();
	}

	private static final String _CLASS_NAME =
		"com.liferay.site.navigation.internal.upgrade.v5_0_0." +
			"SiteNavigationMenuItemUpgradeProcess";

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@Inject
	private BlogsEntryLocalService _blogsEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	@Inject
	private DDMStructureLocalService _ddmStructureLocalService;

	private Group _group1;
	private Group _group2;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private SiteNavigationMenu _siteNavigationMenu;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.site.navigation.internal.upgrade.registry.SiteNavigationServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}