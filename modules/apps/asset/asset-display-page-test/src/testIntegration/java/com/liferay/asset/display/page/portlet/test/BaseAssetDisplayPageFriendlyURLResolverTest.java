/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.display.page.portlet.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.display.page.constants.AssetDisplayPageConstants;
import com.liferay.asset.display.page.test.util.AssetDisplayPageEntryTestUtil;
import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryGroupRelLocalService;
import com.liferay.depot.service.DepotEntryLocalService;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.layout.page.template.constants.LayoutPageTemplateEntryTypeConstants;
import com.liferay.layout.page.template.model.LayoutPageTemplateEntry;
import com.liferay.layout.page.template.test.util.DisplayPageTemplateTestUtil;
import com.liferay.layout.page.template.test.util.LayoutPageTemplateTestUtil;
import com.liferay.layout.page.template.util.LayoutPageTemplateEntryUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.feature.flag.constants.FeatureFlagConstants;
import com.liferay.portal.kernel.model.ColorScheme;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutFriendlyURLComposite;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.model.impl.VirtualLayout;
import com.liferay.portal.kernel.portlet.FriendlyURLResolver;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutSetLocalService;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.FeatureFlagTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.impl.ThemeSettingImpl;
import com.liferay.portal.props.test.util.PropsTemporarySwapper;
import com.liferay.portal.test.rule.FeatureFlag;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Javier Moral
 */
@RunWith(Arquillian.class)
public class BaseAssetDisplayPageFriendlyURLResolverTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		FeatureFlagTestUtil.invokeFeatureFlagListeners(
			TestPropsValues.getCompanyId(), true, "LPD-57283");

		_group = _addGroup();

		_themeSettingKey = RandomTestUtil.randomString();
	}

	@FeatureFlag("LPD-57283")
	@Test
	@TestInfo({"LPD-104242", "LPD-107030"})
	public void testGetLayoutFriendlyURLComposite() throws Exception {
		_testGetLayoutFriendlyURLComposite();
		_testGetLayoutFriendlyURLCompositeLookAndFeel();
		_testGetLayoutFriendlyURLCompositeLookAndFeelWhenMasterLayout();
		_testGetLayoutFriendlyURLCompositeLookAndFeelWhenNotInherited();
		_testGetLayoutFriendlyURLCompositeLookAndFeelWhenThemeSettingSet();
		_testGetLayoutFriendlyURLCompositeWhenDisconnected();
		_testGetLayoutFriendlyURLCompositeWhenNoDisplayPage();
	}

	private Group _addConnectedDesignLibraryGroup(Group group)
		throws Exception {

		DepotEntry depotEntry = _depotEntryLocalService.addDepotEntry(
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			DepotConstants.TYPE_DESIGN_LIBRARY,
			ServiceContextTestUtil.getServiceContext());

		_depotEntries.add(depotEntry);

		_depotEntryGroupRelLocalService.addDepotEntryGroupRel(
			depotEntry.getDepotEntryId(), group.getGroupId());

		return depotEntry.getGroup();
	}

	private LayoutPageTemplateEntry _addDisplayPageTemplate(
			long groupId, JournalArticle journalArticle)
		throws Exception {

		return _addDisplayPageTemplate(groupId, journalArticle, 0);
	}

	private LayoutPageTemplateEntry _addDisplayPageTemplate(
			long groupId, JournalArticle journalArticle, long masterLayoutPlid)
		throws Exception {

		return DisplayPageTemplateTestUtil.addDisplayPageTemplate(
			groupId, _getClassNameId(),
			LayoutPageTemplateEntryUtil.getClassTypeKey(
				_getClassNameId(), journalArticle.getDDMStructureId(),
				_group.getGroupId()),
			true, masterLayoutPlid, WorkflowConstants.STATUS_APPROVED);
	}

	private Group _addGroup() throws Exception {
		Group group = GroupTestUtil.addGroup();

		_groups.add(group);

		return group;
	}

	private JournalArticle _addJournalArticle() throws Exception {
		return JournalTestUtil.addArticle(
			_group.getGroupId(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	private void _assertLayoutFriendlyURLComposite(
			boolean designLibrariesEnabled,
			LayoutPageTemplateEntry expectedLayoutPageTemplateEntry,
			JournalArticle journalArticle)
		throws Exception {

		try (PropsTemporarySwapper propsTemporarySwapper =
				new PropsTemporarySwapper(
					FeatureFlagConstants.getKey("LPD-57283"),
					String.valueOf(designLibrariesEnabled))) {

			Layout layout = _getLayout(journalArticle);

			if (expectedLayoutPageTemplateEntry == null) {
				Assert.assertNull(layout);

				return;
			}

			Assert.assertEquals(
				expectedLayoutPageTemplateEntry.getPlid(), layout.getPlid());
		}
	}

	private void _assertLookAndFeel(Layout layout, LayoutSet layoutSet)
		throws Exception {

		ColorScheme layoutColorScheme = layout.getColorScheme();
		ColorScheme layoutSetColorScheme = layoutSet.getColorScheme();

		Assert.assertEquals(
			layoutSetColorScheme.getColorSchemeId(),
			layoutColorScheme.getColorSchemeId());

		Assert.assertEquals(layoutSet.getCss(), layout.getCssText());

		Theme layoutSetTheme = layoutSet.getTheme();
		Theme layoutTheme = layout.getTheme();

		Assert.assertEquals(
			layoutSetTheme.getThemeId(), layoutTheme.getThemeId());

		Assert.assertEquals(
			layoutSet.getThemeSetting(_themeSettingKey, "regular"),
			layout.getThemeSetting(_themeSettingKey, "regular"));
	}

	private long _getClassNameId() {
		return _portal.getClassNameId(JournalArticle.class.getName());
	}

	private Layout _getLayout(JournalArticle journalArticle) throws Exception {
		LayoutFriendlyURLComposite layoutFriendlyURLComposite =
			_friendlyURLResolver.getLayoutFriendlyURLComposite(
				TestPropsValues.getCompanyId(), _group.getGroupId(), false,
				_friendlyURLResolver.getURLSeparator() +
					journalArticle.getUrlTitle(),
				Collections.emptyMap(),
				HashMapBuilder.<String, Object>put(
					WebKeys.LOCALE, LocaleUtil.getDefault()
				).build());

		return layoutFriendlyURLComposite.getLayout();
	}

	private void _testGetLayoutFriendlyURLComposite() throws Exception {
		JournalArticle journalArticle = _addJournalArticle();

		Group designLibraryGroup = _addConnectedDesignLibraryGroup(_group);

		LayoutPageTemplateEntry designLibraryLayoutPageTemplateEntry =
			_addDisplayPageTemplate(
				designLibraryGroup.getGroupId(), journalArticle);

		_assertLayoutFriendlyURLComposite(false, null, journalArticle);
		_assertLayoutFriendlyURLComposite(
			true, designLibraryLayoutPageTemplateEntry, journalArticle);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate(_group.getGroupId(), journalArticle);

		_assertLayoutFriendlyURLComposite(
			true, layoutPageTemplateEntry, journalArticle);

		Assert.assertFalse(_getLayout(journalArticle) instanceof VirtualLayout);
	}

	private void _testGetLayoutFriendlyURLCompositeLookAndFeel()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		Group designLibraryGroup = _addConnectedDesignLibraryGroup(_group);

		_addDisplayPageTemplate(
			designLibraryGroup.getGroupId(), journalArticle);

		_updateLookAndFeel(designLibraryGroup, _THEME_ID_MINIUM);

		LayoutSet layoutSet = _updateLookAndFeel(_group, _THEME_ID_SPEEDWELL);

		Layout layout = _getLayout(journalArticle);

		Assert.assertTrue(layout instanceof VirtualLayout);

		_assertLookAndFeel(layout, layoutSet);
	}

	private void _testGetLayoutFriendlyURLCompositeLookAndFeelWhenMasterLayout()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		Group designLibraryGroup = _addConnectedDesignLibraryGroup(_group);

		_updateLookAndFeel(designLibraryGroup, _THEME_ID_MINIUM);

		LayoutSet layoutSet = _updateLookAndFeel(_group, _THEME_ID_SPEEDWELL);

		LayoutPageTemplateEntry masterLayoutPageTemplateEntry =
			LayoutPageTemplateTestUtil.addLayoutPageTemplateEntry(
				designLibraryGroup.getGroupId(),
				LayoutPageTemplateEntryTypeConstants.MASTER_LAYOUT,
				WorkflowConstants.STATUS_APPROVED);

		Layout masterLayout = _layoutLocalService.getLayout(
			masterLayoutPageTemplateEntry.getPlid());

		UnicodeProperties typeSettingsUnicodeProperties =
			masterLayout.getTypeSettingsProperties();

		String themeSettingValue = RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.put(
			ThemeSettingImpl.namespaceProperty("regular", _themeSettingKey),
			themeSettingValue);

		_layoutLocalService.updateTypeSettings(
			masterLayout, typeSettingsUnicodeProperties.toString());

		_addDisplayPageTemplate(
			designLibraryGroup.getGroupId(), journalArticle,
			masterLayoutPageTemplateEntry.getPlid());

		Layout layout = _getLayout(journalArticle);

		Assert.assertTrue(layout instanceof VirtualLayout);

		Theme layoutSetTheme = layoutSet.getTheme();
		Theme layoutTheme = layout.getTheme();

		Assert.assertEquals(
			layoutSetTheme.getThemeId(), layoutTheme.getThemeId());

		Assert.assertEquals(
			themeSettingValue,
			layout.getThemeSetting(_themeSettingKey, "regular"));
	}

	private void _testGetLayoutFriendlyURLCompositeLookAndFeelWhenNotInherited()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		Group designLibraryGroup = _addConnectedDesignLibraryGroup(_group);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate(
				designLibraryGroup.getGroupId(), journalArticle);

		LayoutSet designLibraryLayoutSet = _updateLookAndFeel(
			designLibraryGroup, _THEME_ID_SPEEDWELL);

		_updateLookAndFeel(_group, _THEME_ID_SPEEDWELL);

		ColorScheme designLibraryColorScheme =
			designLibraryLayoutSet.getColorScheme();

		Layout designLibraryLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		_layoutLocalService.updateLookAndFeel(
			designLibraryLayout.getGroupId(),
			designLibraryLayout.isPrivateLayout(),
			designLibraryLayout.getLayoutId(), _THEME_ID_MINIUM,
			designLibraryColorScheme.getColorSchemeId(), StringPool.BLANK);

		Layout layout = _getLayout(journalArticle);

		Assert.assertTrue(layout instanceof VirtualLayout);

		Theme layoutTheme = layout.getTheme();

		Assert.assertEquals(_THEME_ID_MINIUM, layoutTheme.getThemeId());
	}

	private void _testGetLayoutFriendlyURLCompositeLookAndFeelWhenThemeSettingSet()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		Group designLibraryGroup = _addConnectedDesignLibraryGroup(_group);

		LayoutPageTemplateEntry layoutPageTemplateEntry =
			_addDisplayPageTemplate(
				designLibraryGroup.getGroupId(), journalArticle);

		_updateLookAndFeel(designLibraryGroup, _THEME_ID_MINIUM);

		_updateLookAndFeel(_group, _THEME_ID_SPEEDWELL);

		Layout designLibraryLayout = _layoutLocalService.getLayout(
			layoutPageTemplateEntry.getPlid());

		UnicodeProperties typeSettingsUnicodeProperties =
			designLibraryLayout.getTypeSettingsProperties();

		String themeSettingValue = RandomTestUtil.randomString();

		typeSettingsUnicodeProperties.put(
			ThemeSettingImpl.namespaceProperty("regular", _themeSettingKey),
			themeSettingValue);

		_layoutLocalService.updateTypeSettings(
			designLibraryLayout, typeSettingsUnicodeProperties.toString());

		Layout layout = _getLayout(journalArticle);

		Assert.assertTrue(layout instanceof VirtualLayout);

		Assert.assertEquals(
			themeSettingValue,
			layout.getThemeSetting(_themeSettingKey, "regular"));
	}

	private void _testGetLayoutFriendlyURLCompositeWhenDisconnected()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		Group designLibraryGroup = _addConnectedDesignLibraryGroup(_addGroup());

		_addDisplayPageTemplate(
			designLibraryGroup.getGroupId(), journalArticle);

		_assertLayoutFriendlyURLComposite(true, null, journalArticle);
	}

	private void _testGetLayoutFriendlyURLCompositeWhenNoDisplayPage()
		throws Exception {

		JournalArticle journalArticle = _addJournalArticle();

		Group designLibraryGroup = _addConnectedDesignLibraryGroup(_group);

		_addDisplayPageTemplate(
			designLibraryGroup.getGroupId(), journalArticle);

		AssetDisplayPageEntryTestUtil.addAssetDisplayPageEntry(
			_group.getGroupId(), _getClassNameId(),
			journalArticle.getResourcePrimKey(), 0,
			AssetDisplayPageConstants.TYPE_NONE);

		_assertLayoutFriendlyURLComposite(true, null, journalArticle);
	}

	private LayoutSet _updateLookAndFeel(Group group, String themeId)
		throws Exception {

		LayoutSet layoutSet = _layoutSetLocalService.updateLookAndFeel(
			group.getGroupId(), false, themeId, StringPool.BLANK,
			RandomTestUtil.randomString());

		UnicodeProperties settingsUnicodeProperties =
			layoutSet.getSettingsProperties();

		settingsUnicodeProperties.put(
			ThemeSettingImpl.namespaceProperty("regular", _themeSettingKey),
			RandomTestUtil.randomString());

		return _layoutSetLocalService.updateSettings(
			group.getGroupId(), false, settingsUnicodeProperties.toString());
	}

	private static final String _THEME_ID_MINIUM = "minium_WAR_miniumtheme";

	private static final String _THEME_ID_SPEEDWELL =
		"speedwell_WAR_speedwelltheme";

	@DeleteAfterTestRun
	private final List<DepotEntry> _depotEntries = new ArrayList<>();

	@Inject
	private DepotEntryGroupRelLocalService _depotEntryGroupRelLocalService;

	@Inject
	private DepotEntryLocalService _depotEntryLocalService;

	@Inject(
		filter = "component.name=com.liferay.journal.web.internal.asset.display.page.portlet.JournalArticleAssetDisplayPageFriendlyURLResolver"
	)
	private FriendlyURLResolver _friendlyURLResolver;

	private Group _group;

	@DeleteAfterTestRun
	private final List<Group> _groups = new ArrayList<>();

	@Inject
	private LayoutLocalService _layoutLocalService;

	@Inject
	private LayoutSetLocalService _layoutSetLocalService;

	@Inject
	private Portal _portal;

	private String _themeSettingKey;

}