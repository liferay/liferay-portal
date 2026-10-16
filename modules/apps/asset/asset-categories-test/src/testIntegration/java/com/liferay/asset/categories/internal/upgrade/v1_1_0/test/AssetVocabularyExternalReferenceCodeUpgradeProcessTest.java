/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.categories.internal.upgrade.v1_1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.categories.navigation.constants.AssetCategoriesNavigationPortletKeys;
import com.liferay.asset.kernel.model.AssetVocabulary;
import com.liferay.asset.kernel.model.AssetVocabularyConstants;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.change.tracking.configuration.CTSettingsConfiguration;
import com.liferay.change.tracking.model.CTCollection;
import com.liferay.change.tracking.service.CTCollectionLocalService;
import com.liferay.fragment.constants.FragmentConstants;
import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.service.FragmentEntryLinkLocalService;
import com.liferay.layout.test.util.LayoutTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.test.util.CompanyConfigurationTemporarySwapper;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.dao.orm.EntityCache;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.UnicodePropertiesBuilder;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.portal.upgrade.test.util.UpgradeTestUtil;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalService;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalService;

import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Adolfo Pérez
 */
@RunWith(Arquillian.class)
public class AssetVocabularyExternalReferenceCodeUpgradeProcessTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	@TestInfo("LPD-110362")
	public void testUpgrade() throws Exception {
		_testUpgradeAssetVocabularies();
		_testUpgradeFragmentEntryLinks();
		_testUpgradePortletPreferences();
		_testUpgradeSiteNavigationMenuItems();
		_testUpgradeWithDefaultExternalReferenceCode();
		_testUpgradeWithMultipleDefaultAssetVocabularies();
		_testUpgradeWithPublication();
	}

	private AssetVocabulary _addAssetVocabulary(long groupId, String name)
		throws Exception {

		return _assetVocabularyLocalService.addVocabulary(
			null, TestPropsValues.getUserId(), groupId, name, StringPool.BLANK,
			HashMapBuilder.put(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()
			).build(),
			null, null, AssetVocabularyConstants.VISIBILITY_TYPE_PUBLIC,
			ServiceContextTestUtil.getServiceContext(groupId));
	}

	private FragmentEntryLink _addFragmentEntryLink(
			String externalReferenceCode, Group group,
			String scopeExternalReferenceCode)
		throws Exception {

		return _fragmentEntryLinkLocalService.addFragmentEntryLink(
			null, TestPropsValues.getUserId(), group.getGroupId(), null, null,
			null, 0, 0, StringPool.BLANK, StringPool.BLANK, StringPool.BLANK,
			StringPool.BLANK,
			JSONUtil.put(
				_KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR,
				JSONUtil.put(
					"source",
					JSONUtil.put(
						"categoryTreeNodeType", "Vocabulary"
					).put(
						"externalReferenceCode", externalReferenceCode
					).put(
						"scopeExternalReferenceCode", scopeExternalReferenceCode
					))
			).toString(),
			StringPool.BLANK, 0, StringPool.BLANK,
			FragmentConstants.TYPE_COMPONENT,
			ServiceContextTestUtil.getServiceContext(group.getGroupId()));
	}

	private Group _addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	private SiteNavigationMenuItem _addSiteNavigationMenuItem(
			String externalReferenceCode, String scopeExternalReferenceCode,
			SiteNavigationMenu siteNavigationMenu)
		throws Exception {

		return _siteNavigationMenuItemLocalService.addSiteNavigationMenuItem(
			null, TestPropsValues.getUserId(), siteNavigationMenu.getGroupId(),
			siteNavigationMenu.getSiteNavigationMenuId(), 0, "asset_vocabulary",
			UnicodePropertiesBuilder.create(
				true
			).put(
				"externalReferenceCode", externalReferenceCode
			).put(
				"scopeExternalReferenceCode", scopeExternalReferenceCode
			).put(
				"title", RandomTestUtil.randomString()
			).buildString(),
			ServiceContextTestUtil.getServiceContext(
				siteNavigationMenu.getGroupId()));
	}

	private void _assertAssetVocabulary(
			String externalReferenceCode, String uuid, long vocabularyId)
		throws Exception {

		AssetVocabulary assetVocabulary =
			_assetVocabularyLocalService.getAssetVocabulary(vocabularyId);

		Assert.assertEquals(
			externalReferenceCode, assetVocabulary.getExternalReferenceCode());
		Assert.assertEquals(uuid, assetVocabulary.getUuid());
	}

	private void _assertFragmentEntryLink(
			String externalReferenceCode, FragmentEntryLink fragmentEntryLink)
		throws Exception {

		JSONObject editableValuesJSONObject =
			fragmentEntryLink.getEditableValuesJSONObject();

		JSONObject jsonObject = editableValuesJSONObject.getJSONObject(
			_KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR);

		JSONObject sourceJSONObject = jsonObject.getJSONObject("source");

		Assert.assertEquals(
			externalReferenceCode,
			sourceJSONObject.getString("externalReferenceCode"));
	}

	private void _assertSiteNavigationMenuItem(
			String externalReferenceCode,
			SiteNavigationMenuItem siteNavigationMenuItem)
		throws Exception {

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
			_upgradeStepRegistrator,
			"com.liferay.asset.categories.internal.upgrade.v1_1_0." +
				"AssetVocabularyExternalReferenceCodeUpgradeProcess");

		upgradeProcess.upgrade();

		_entityCache.clearCache();
		_finderCache.clearCache();
		_multiVMPool.clear();
	}

	private void _testUpgradeAssetVocabularies() throws Exception {
		Group group1 = _addGroup();

		AssetVocabulary assetVocabulary1 = _addAssetVocabulary(
			group1.getGroupId(), "topic");

		Group group2 = _addGroup();

		AssetVocabulary assetVocabulary2 = _addAssetVocabulary(
			group2.getGroupId(), "topic");

		assetVocabulary2 = _assetVocabularyLocalService.updateVocabulary(
			RandomTestUtil.randomString(), assetVocabulary2.getVocabularyId(),
			assetVocabulary2.getTitleMap(),
			assetVocabulary2.getDescriptionMap(),
			assetVocabulary2.getSettings(),
			assetVocabulary2.getVisibilityType());

		AssetVocabulary assetVocabulary3 = _addAssetVocabulary(
			group1.getGroupId(), RandomTestUtil.randomString());

		_runUpgrade();

		_assertAssetVocabulary(
			AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
			assetVocabulary1.getUuid(), assetVocabulary1.getVocabularyId());
		_assertAssetVocabulary(
			assetVocabulary2.getExternalReferenceCode(),
			assetVocabulary2.getUuid(), assetVocabulary2.getVocabularyId());
		_assertAssetVocabulary(
			assetVocabulary3.getExternalReferenceCode(),
			assetVocabulary3.getUuid(), assetVocabulary3.getVocabularyId());
	}

	private void _testUpgradeFragmentEntryLinks() throws Exception {
		Group group1 = _addGroup();

		AssetVocabulary assetVocabulary1 = _addAssetVocabulary(
			group1.getGroupId(), "topic");

		Group group2 = _addGroup();

		AssetVocabulary assetVocabulary2 = _addAssetVocabulary(
			group2.getGroupId(), RandomTestUtil.randomString());

		FragmentEntryLink fragmentEntryLink1 = _addFragmentEntryLink(
			assetVocabulary1.getExternalReferenceCode(), group1,
			StringPool.BLANK);
		FragmentEntryLink fragmentEntryLink2 = _addFragmentEntryLink(
			assetVocabulary1.getExternalReferenceCode(), group1,
			group1.getExternalReferenceCode());
		FragmentEntryLink fragmentEntryLink3 = _addFragmentEntryLink(
			assetVocabulary2.getExternalReferenceCode(), group1,
			group2.getExternalReferenceCode());

		_runUpgrade();

		_assertFragmentEntryLink(
			AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentEntryLink1.getFragmentEntryLinkId()));
		_assertFragmentEntryLink(
			AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentEntryLink2.getFragmentEntryLinkId()));
		_assertFragmentEntryLink(
			assetVocabulary2.getExternalReferenceCode(),
			_fragmentEntryLinkLocalService.getFragmentEntryLink(
				fragmentEntryLink3.getFragmentEntryLinkId()));
	}

	private void _testUpgradePortletPreferences() throws Exception {
		Group group1 = _addGroup();

		AssetVocabulary assetVocabulary1 = _addAssetVocabulary(
			group1.getGroupId(), "topic");

		Group group2 = _addGroup();

		AssetVocabulary assetVocabulary2 = _addAssetVocabulary(
			group2.getGroupId(), RandomTestUtil.randomString());

		Layout layout = LayoutTestUtil.addTypePortletLayout(group1);

		LayoutTestUtil.updateLayoutPortletPreferences(
			layout,
			AssetCategoriesNavigationPortletKeys.ASSET_CATEGORIES_NAVIGATION,
			HashMapBuilder.put(
				"assetVocabularyExternalReferenceCode",
				assetVocabulary1.getExternalReferenceCode()
			).put(
				"assetVocabularyExternalReferenceCodes",
				assetVocabulary1.getExternalReferenceCode()
			).put(
				"assetVocabularyExternalReferenceCodes_" +
					group1.getExternalReferenceCode(),
				assetVocabulary1.getExternalReferenceCode()
			).put(
				"assetVocabularyExternalReferenceCodes_" +
					group2.getExternalReferenceCode(),
				assetVocabulary2.getExternalReferenceCode()
			).put(
				"groupVocabularyExternalReferenceCodes",
				StringBundler.concat(
					group1.getExternalReferenceCode(), "&&",
					assetVocabulary1.getExternalReferenceCode(),
					StringPool.COMMA, group2.getExternalReferenceCode(), "&&",
					assetVocabulary2.getExternalReferenceCode())
			).build());

		_runUpgrade();

		PortletPreferences portletPreferences =
			LayoutTestUtil.getPortletPreferences(
				layout,
				AssetCategoriesNavigationPortletKeys.
					ASSET_CATEGORIES_NAVIGATION);

		Assert.assertEquals(
			AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
			portletPreferences.getValue(
				"assetVocabularyExternalReferenceCode", null));
		Assert.assertEquals(
			AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
			portletPreferences.getValue(
				"assetVocabularyExternalReferenceCodes", null));
		Assert.assertEquals(
			AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
			portletPreferences.getValue(
				"assetVocabularyExternalReferenceCodes_" +
					group1.getExternalReferenceCode(),
				null));
		Assert.assertEquals(
			assetVocabulary2.getExternalReferenceCode(),
			portletPreferences.getValue(
				"assetVocabularyExternalReferenceCodes_" +
					group2.getExternalReferenceCode(),
				null));
		Assert.assertEquals(
			StringBundler.concat(
				group1.getExternalReferenceCode(), "&&",
				AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
				StringPool.COMMA, group2.getExternalReferenceCode(), "&&",
				assetVocabulary2.getExternalReferenceCode()),
			portletPreferences.getValue(
				"groupVocabularyExternalReferenceCodes", null));
	}

	private void _testUpgradeSiteNavigationMenuItems() throws Exception {
		Group group1 = _addGroup();

		AssetVocabulary assetVocabulary1 = _addAssetVocabulary(
			group1.getGroupId(), "topic");

		Group group2 = _addGroup();

		AssetVocabulary assetVocabulary2 = _addAssetVocabulary(
			group2.getGroupId(), RandomTestUtil.randomString());

		SiteNavigationMenu siteNavigationMenu =
			_siteNavigationMenuLocalService.addSiteNavigationMenu(
				null, TestPropsValues.getUserId(), group1.getGroupId(),
				RandomTestUtil.randomString(),
				ServiceContextTestUtil.getServiceContext(group1.getGroupId()));

		SiteNavigationMenuItem siteNavigationMenuItem1 =
			_addSiteNavigationMenuItem(
				assetVocabulary1.getExternalReferenceCode(), StringPool.BLANK,
				siteNavigationMenu);
		SiteNavigationMenuItem siteNavigationMenuItem2 =
			_addSiteNavigationMenuItem(
				assetVocabulary2.getExternalReferenceCode(),
				group2.getExternalReferenceCode(), siteNavigationMenu);

		_runUpgrade();

		_assertSiteNavigationMenuItem(
			AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
				siteNavigationMenuItem1.getSiteNavigationMenuItemId()));
		_assertSiteNavigationMenuItem(
			assetVocabulary2.getExternalReferenceCode(),
			_siteNavigationMenuItemLocalService.getSiteNavigationMenuItem(
				siteNavigationMenuItem2.getSiteNavigationMenuItemId()));
	}

	private void _testUpgradeWithDefaultExternalReferenceCode()
		throws Exception {

		Group group = _addGroup();

		AssetVocabulary assetVocabulary1 = _addAssetVocabulary(
			group.getGroupId(), RandomTestUtil.randomString());

		assetVocabulary1 = _assetVocabularyLocalService.updateVocabulary(
			AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
			assetVocabulary1.getVocabularyId(), assetVocabulary1.getTitleMap(),
			assetVocabulary1.getDescriptionMap(),
			assetVocabulary1.getSettings(),
			assetVocabulary1.getVisibilityType());

		AssetVocabulary assetVocabulary2 = _addAssetVocabulary(
			group.getGroupId(), "topic");

		_runUpgrade();

		_assertAssetVocabulary(
			AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
			assetVocabulary1.getUuid(), assetVocabulary1.getVocabularyId());
		_assertAssetVocabulary(
			assetVocabulary2.getExternalReferenceCode(),
			assetVocabulary2.getUuid(), assetVocabulary2.getVocabularyId());
	}

	private void _testUpgradeWithMultipleDefaultAssetVocabularies()
		throws Exception {

		Group group = _addGroup();

		AssetVocabulary assetVocabulary1 = _addAssetVocabulary(
			group.getGroupId(), "topic");
		AssetVocabulary assetVocabulary2 = _addAssetVocabulary(
			group.getGroupId(),
			StringUtil.toLowerCase(
				LanguageUtil.get(LocaleUtil.SPAIN, "topic")));

		_runUpgrade();

		_assertAssetVocabulary(
			AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
			assetVocabulary1.getUuid(), assetVocabulary1.getVocabularyId());
		_assertAssetVocabulary(
			assetVocabulary2.getExternalReferenceCode(),
			assetVocabulary2.getUuid(), assetVocabulary2.getVocabularyId());
	}

	private void _testUpgradeWithPublication() throws Exception {
		Group group = _addGroup();

		AssetVocabulary assetVocabulary = _addAssetVocabulary(
			group.getGroupId(), "topic");

		try (CompanyConfigurationTemporarySwapper
				companyConfigurationTemporarySwapper =
					new CompanyConfigurationTemporarySwapper(
						TestPropsValues.getCompanyId(),
						CTSettingsConfiguration.class.getName(),
						HashMapDictionaryBuilder.<String, Object>put(
							"enabled", true
						).build())) {

			_ctCollection = _ctCollectionLocalService.addCTCollection(
				null, TestPropsValues.getCompanyId(),
				TestPropsValues.getUserId(), 0, RandomTestUtil.randomString(),
				null);

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						_ctCollection.getCtCollectionId())) {

				_assetVocabularyLocalService.updateVocabulary(
					assetVocabulary.getExternalReferenceCode(),
					assetVocabulary.getVocabularyId(),
					HashMapBuilder.put(
						LocaleUtil.getSiteDefault(),
						RandomTestUtil.randomString()
					).build(),
					assetVocabulary.getDescriptionMap(),
					assetVocabulary.getSettings(),
					assetVocabulary.getVisibilityType());
			}

			_runUpgrade();

			_assertAssetVocabulary(
				AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
				assetVocabulary.getUuid(), assetVocabulary.getVocabularyId());

			try (SafeCloseable safeCloseable =
					CTCollectionThreadLocal.setCTCollectionIdWithSafeCloseable(
						_ctCollection.getCtCollectionId())) {

				_assertAssetVocabulary(
					AssetVocabularyConstants.EXTERNAL_REFERENCE_CODE_TOPIC,
					assetVocabulary.getUuid(),
					assetVocabulary.getVocabularyId());
			}
		}
	}

	private static final String _KEY_FREEMARKER_FRAGMENT_ENTRY_PROCESSOR =
		"com.liferay.fragment.entry.processor.freemarker." +
			"FreeMarkerFragmentEntryProcessor";

	@Inject
	private AssetVocabularyLocalService _assetVocabularyLocalService;

	@DeleteAfterTestRun
	private CTCollection _ctCollection;

	@Inject
	private CTCollectionLocalService _ctCollectionLocalService;

	@Inject
	private EntityCache _entityCache;

	@Inject
	private FinderCache _finderCache;

	@Inject
	private FragmentEntryLinkLocalService _fragmentEntryLinkLocalService;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private MultiVMPool _multiVMPool;

	@Inject
	private SiteNavigationMenuItemLocalService
		_siteNavigationMenuItemLocalService;

	@Inject
	private SiteNavigationMenuLocalService _siteNavigationMenuLocalService;

	@Inject(
		filter = "(&(component.name=com.liferay.asset.categories.internal.upgrade.registry.AssetCategoriesServiceUpgradeStepRegistrator))"
	)
	private UpgradeStepRegistrator _upgradeStepRegistrator;

}