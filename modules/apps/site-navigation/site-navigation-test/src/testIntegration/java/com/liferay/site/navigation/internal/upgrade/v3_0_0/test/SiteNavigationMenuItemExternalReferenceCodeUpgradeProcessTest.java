/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.internal.upgrade.v3_0_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.model.AssetCategory;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.document.library.test.util.DLAppTestUtil;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
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

import java.util.Collections;

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
public class SiteNavigationMenuItemExternalReferenceCodeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId());
		_siteNavigationMenu = SiteNavigationMenuTestUtil.addSiteNavigationMenu(
			_group);
	}

	@Test
	public void testUpgradeWithAssetCategoryAndAssetVocabulary()
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.addVocabulary(
				TestPropsValues.getUserId(), _group.getGroupId(),
				RandomTestUtil.randomString(), _serviceContext);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"classPK", assetVocabulary.getVocabularyId()
				).put(
					"groupId", assetVocabulary.getGroupId()
				).put(
					"type", SiteNavigationMenuItemTypeConstants.ASSET_VOCABULARY
				).put(
					"uuid", assetVocabulary.getUuid()
				).buildString());

		AssetCategory assetCategory = _assetCategoryLocalService.addCategory(
			TestPropsValues.getUserId(), _group.getGroupId(),
			RandomTestUtil.randomString(), assetVocabulary.getVocabularyId(),
			_serviceContext);

		SiteNavigationMenuItem assetCategorySiteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				AssetCategory.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", AssetCategory.class.getName()
				).put(
					"classPK", assetCategory.getCategoryId()
				).buildString());

		_runUpgrade();

		_assertEqualsExternalReferenceCode(
			assetCategory.getExternalReferenceCode(),
			assetCategorySiteNavigationMenuItem.getSiteNavigationMenuItemId());
		_assertEqualsExternalReferenceCode(
			assetVocabulary.getExternalReferenceCode(),
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithCPDefinition() throws Exception {
		CPDefinition cpDefinition = CPTestUtil.addCPDefinition(
			_group.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				FileEntry.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", CPDefinition.class.getName()
				).put(
					"classPK", cpDefinition.getCPDefinitionId()
				).put(
					"type", CPDefinition.class.getName()
				).buildString());

		_runUpgrade();

		CProduct cProduct = cpDefinition.getCProduct();

		_assertEqualsExternalReferenceCode(
			cProduct.getExternalReferenceCode(),
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithFileEntry() throws Exception {
		FileEntry fileEntry = DLAppTestUtil.addFileEntry(_group.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				FileEntry.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", FileEntry.class.getName()
				).put(
					"classPK", fileEntry.getFileEntryId()
				).put(
					"type", FileEntry.class.getName()
				).buildString());

		_runUpgrade();

		_assertEqualsExternalReferenceCode(
			fileEntry.getExternalReferenceCode(),
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithJournalArticle() throws Exception {
		JournalArticle journalArticle = JournalTestUtil.addArticle(
			TestPropsValues.getUserId(), _group.getGroupId(), 0);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				JournalArticle.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", JournalArticle.class.getName()
				).put(
					"classPK", journalArticle.getResourcePrimKey()
				).put(
					"type", JournalArticle.class.getName()
				).buildString());

		_runUpgrade();

		_assertEqualsExternalReferenceCode(
			journalArticle.getExternalReferenceCode(),
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithKBArticle() throws Exception {
		KBArticle kbArticle = KBTestUtil.addKBArticle(_group.getGroupId());

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				KBArticle.class.getName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", KBArticle.class.getName()
				).put(
					"classPK", kbArticle.getResourcePrimKey()
				).put(
					"type", KBArticle.class.getName()
				).buildString());

		_runUpgrade();

		_assertEqualsExternalReferenceCode(
			kbArticle.getExternalReferenceCode(),
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithLayout() throws Exception {
		Layout layout = LayoutTestUtil.addTypeContentLayout(_group);

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				SiteNavigationMenuItemTypeConstants.LAYOUT,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"groupId", String.valueOf(_group.getGroupId())
				).put(
					"layoutUuid", layout.getUuid()
				).put(
					"privateLayout", false
				).buildString());

		_runUpgrade();

		_assertEqualsExternalReferenceCode(
			layout.getExternalReferenceCode(),
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithObjectEntry() throws Exception {
		ObjectDefinition objectDefinition =
			_objectDefinitionLocalService.addCustomObjectDefinition(
				null, TestPropsValues.getUserId(), 0, null, false, true, false,
				true, false, false, false, false, false,
				RandomTestUtil.randomString(),
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				"TestObject", null, "control_panel.sites",
				LocalizedMapUtil.getLocalizedMap(RandomTestUtil.randomString()),
				false, ObjectDefinitionConstants.SCOPE_SITE,
				ObjectDefinitionConstants.STORAGE_TYPE_DEFAULT,
				Collections.emptyList(), null, Collections.emptyList());

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
				objectDefinition.getObjectDefinitionId()
			).build());

		objectDefinition.setTitleObjectFieldId(objectField.getObjectFieldId());

		objectDefinition = _objectDefinitionLocalService.updateObjectDefinition(
			objectDefinition);

		objectDefinition =
			_objectDefinitionLocalService.publishCustomObjectDefinition(
				TestPropsValues.getUserId(),
				objectDefinition.getObjectDefinitionId());

		ObjectEntry objectEntry = ObjectEntryTestUtil.addObjectEntry(
			objectDefinition, RandomTestUtil.randomString(),
			RandomTestUtil.randomString());

		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				objectDefinition.getClassName(),
				UnicodePropertiesBuilder.create(
					true
				).put(
					"className", objectDefinition.getClassName()
				).put(
					"classPK", objectEntry.getObjectEntryId()
				).buildString());

		_runUpgrade();

		_assertEqualsExternalReferenceCode(
			objectEntry.getExternalReferenceCode(),
			siteNavigationMenuItem.getSiteNavigationMenuItemId());
	}

	@Test
	public void testUpgradeWithURL() throws Exception {
		SiteNavigationMenuItem siteNavigationMenuItem =
			_addSiteNavigationMenuItem(
				SiteNavigationMenuItemTypeConstants.URL,
				UnicodePropertiesBuilder.create(
					true
				).put(
					"defaultLanguage",
					String.valueOf(_serviceContext.getLocale())
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
			typeSettingsUnicodeProperties.get("externalReferenceCode"));
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
			typeSettingsUnicodeProperties.get("externalReferenceCode"));

		return siteNavigationMenuItem;
	}

	private void _assertEqualsExternalReferenceCode(
			String externalReferenceCode, long siteNavigationMenuItemId)
		throws Exception {

		SiteNavigationMenuItem siteNavigationMenuItem =
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
				siteNavigationMenuItemId);

		UnicodeProperties typeSettingsUnicodeProperties =
			UnicodePropertiesBuilder.fastLoad(
				siteNavigationMenuItem.getTypeSettings()
			).build();

		Assert.assertEquals(
			externalReferenceCode,
			typeSettingsUnicodeProperties.get("externalReferenceCode"));
	}

	private void _runUpgrade() throws Exception {
		UpgradeProcess upgradeProcess = UpgradeTestUtil.getUpgradeStep(
			_upgradeStepRegistrator, _CLASS_NAME);

		upgradeProcess.upgrade();

		EntityCacheUtil.clearCache();
	}

	private static final String _CLASS_NAME =
		"com.liferay.site.navigation.internal.upgrade.v3_0_0." +
			"SiteNavigationMenuItemExternalReferenceCodeUpgradeProcess";

	@Inject(
		filter = "(&(component.name=com.liferay.site.navigation.internal.upgrade.registry.SiteNavigationServiceUpgradeStepRegistrator))"
	)
	private static UpgradeStepRegistrator _upgradeStepRegistrator;

	@Inject
	private AssetCategoryLocalService _assetCategoryLocalService;

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	private Group _group;

	@Inject
	private ObjectDefinitionLocalService _objectDefinitionLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private SiteNavigationMenu _siteNavigationMenu;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

}